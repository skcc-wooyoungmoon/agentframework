package com.skax.aiplatform.service.prompt.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.skax.aiplatform.common.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailCreateReq;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailUpdateReq;
import com.skax.aiplatform.client.sktai.agent.dto.response.GuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailCreateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailDetailRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailsRes;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGuardRailsService;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.GuardRailCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailDeleteReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDeleteRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDetailRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.prompt.GuardRailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가드레일 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuardRailServiceImpl implements GuardRailService {

    private static final String GUARDRAIL_API_RESOURCE_PREFIX = "/api/v1/agent/guardrails/";

    private static final Integer PUBLIC_PROJECT_SEQ = -999;

    private final AdminAuthService adminAuthService;

    private final SktaiAgentGuardRailsService sktaiAgentGuardRailsService;

    private final GpoUsersMasRepository userRepository;
    private final ProjectMgmtRepository projectRepository;
    private final GpoAssetPrjMapMasRepository assetRepository;
    private final SktaiAuthService sktaiAuthService;

    /**
     * 가드레일 목록 조회
     */
    @Override
    public PageResponse<GuardRailRes> getGuardRailList(String projectId, Integer page, Integer size, String filter,
            String search, String sort) {
        log.info("가드레일 목록 조회 - projectId: {}, filter: {}, search: {}, sort: {}", projectId, filter, search, sort);

        // 1. SktAiAgentGuardRailsService를 통해 /guardrails API 호출
        SktGuardRailsRes sktResponse =
                sktaiAgentGuardRailsService.getGuardRails(projectId, page, size, sort, filter, search);

        // 원본 데이터 추출
        List<SktGuardRailsRes.GuardRailData> guardRailDataList = Optional.ofNullable(sktResponse.getData())
                .orElse(List.of());

        if (guardRailDataList.isEmpty()) {
            log.info("가드레일 목록이 비어있습니다");

            var pagination = sktResponse.getPayload().getPagination();
            Page<GuardRailRes> emptyResult = new PageImpl<>(
                    List.of(),
                    PageRequest.of(pagination.getPage() - 1, pagination.getItemsPerPage()),
                    pagination.getTotal()
            );

            return PageResponse.from(emptyResult);
        }

        // ===== STEP 1: 에셋 URL 생성 =====
        log.debug("STEP 1: 에셋 URL 생성");

        List<String> assetUrls = guardRailDataList.stream()
                .map(data -> GUARDRAIL_API_RESOURCE_PREFIX + data.getUuid())
                .toList();

        // ===== STEP 2: 에셋 배치 조회 =====
        log.debug("STEP 2: 에셋 배치 조회");

        final Map<String, GpoAssetPrjMapMas> assetMap = new HashMap<>();
        if (!assetUrls.isEmpty()) {
            List<GpoAssetPrjMapMas> assets = assetRepository.findByAsstUrlIn(assetUrls);

            assets.forEach(asset -> assetMap.put(asset.getAsstUrl(), asset));
            log.debug("배치 조회된 에셋 수: {} (캐시 크기: {})", assets.size(), assetMap.size());
        }

        // ===== STEP 3: GuardRailRes 변환 =====
        log.debug("STEP 3: 응답 객체 변환");

        List<GuardRailRes> guardRailList = guardRailDataList.stream()
                .map(data -> {
                    String assetUrl = GUARDRAIL_API_RESOURCE_PREFIX + data.getUuid();
                    GpoAssetPrjMapMas assetInfo = assetMap.get(assetUrl);

                    // 공개 에셋 여부 판단: assetInfo가 존재하고 lstPrjSeq == -999이면 공개 에셋
                    boolean isPublicAsset = assetInfo != null && assetInfo.getLstPrjSeq() != null
                            && assetInfo.getLstPrjSeq() == -999;

                    return GuardRailRes.of(data, isPublicAsset);
                })
                .toList();

        log.info("변환된 GuardRailRes 개수: {}", guardRailList.size());

        // // 4. Payload 정보 추출
        // Pagination pagination = sktResponse.getPayload().getPagination();
        //
        // // 5. 페이지네이션 정보를 기반으로 페이지 객체 생성
        // Page<GuardRailRes> result = new PageImpl<>(
        //         guardRailList,
        //         PageRequest.of(pagination.getPage() - 1, pagination.getItemsPerPage()),
        //         pagination.getTotal()
        // );
        //
        // log.info("가드레일 목록 조회 완료 - 최종 반환 데이터 수: {}, 전체 개수: {}",
        //         result.getContent().size(), result.getTotalElements());
        //
        // return PageResponse.from(result);

        return PaginationUtils.toPageResponseFromAdxp(sktResponse.getPayload(), guardRailList);
    }

    /**
     * 가드레일 상세 조회
     */
    @Override
    public GuardRailDetailRes getGuardRailById(String id) {
        log.info("가드레일 상세 조회 - id: {}", id);

        // SktAiAgentGuardRailsService를 통해 /guardrails/{id} API 호출
        SktGuardRailDetailRes sktResponse = sktaiAgentGuardRailsService.getGuardRailById(id);

        // 원본 데이터 추출
        SktGuardRailDetailRes.GuardRailDetailData guardRailDetailRes = sktResponse.getData();

        // 에셋 정보 조회
        String assetUrl = GUARDRAIL_API_RESOURCE_PREFIX + guardRailDetailRes.getUuid();
        GpoAssetPrjMapMas assetInfo = assetRepository.findByAsstUrl(assetUrl).orElse(null);

        // 담당자 정보 조회
        GpoUsersMas createdUserInfo = userRepository.findByUuid(guardRailDetailRes.getCreatedBy()).orElse(null);
        GpoUsersMas updatedUserInfo = userRepository.findByUuid(guardRailDetailRes.getUpdatedBy()).orElse(null);

        // 프로젝트명 및 권한 정보 조회
        String projectName = null;


        boolean isPublicAsset = false;
        GpoUsersMas publicAssetUpdatedUserInfo = null;

        if (assetInfo != null) {
            Project project = projectRepository.findById(Long.valueOf(assetInfo.getLstPrjSeq())).orElse(null);

            if (project != null) {
                projectName = project.getPrjNm();
            }

            isPublicAsset = PUBLIC_PROJECT_SEQ.equals(assetInfo.getLstPrjSeq());

            if (isPublicAsset) {
                publicAssetUpdatedUserInfo = userRepository.findByMemberId(assetInfo.getUpdatedBy()).orElse(null);
            }
        }

        // 응답 생성
        GuardRailDetailRes response = GuardRailDetailRes.of(
                sktResponse,
                projectName,
                isPublicAsset,
                createdUserInfo,
                updatedUserInfo,
                publicAssetUpdatedUserInfo
        );

        log.info("가드레일 상세 조회 완료 - id: {}", id);

        return response;
    }

    /**
     * 가드레일 생성
     */
    @Override
    public GuardRailCreateRes createGuardRail(GuardRailCreateReq request) {
        log.info("가드레일 생성 - request: {}", request);

        // GuardRailCreateReq를 SktGuardRailCreateReq로 변환 (정적 팩토리 메서드 사용)
        SktGuardRailCreateReq sktRequest = SktGuardRailCreateReq.from(request);

        // SktAiAgentGuardRailsService를 통해 POST /guardrails API 호출
        SktGuardRailCreateRes sktResponse;
        try {
            sktResponse = sktaiAgentGuardRailsService.createGuardRail(sktRequest);
        } catch (BusinessException e) {
            if (e.getMessage() != null && e.getMessage().contains("이미 사용중인 LLM")) {
                throw new BusinessException(ErrorCode.GUARDRAIL_MODEL_ALREADY_IN_USE);
            }

            throw e;
        }

        // 응답 데이터를 GuardRailCreateRes로 매핑 (정적 팩토리 메서드 사용)
        GuardRailCreateRes response = GuardRailCreateRes.from(sktResponse);

        String guardrailsId = response.getGuardrailsId();

        log.info("가드레일 생성 완료 - guardrailsId: {}", guardrailsId);

        // 권한 설정 (현재 사용자의 프로젝트 기준)
        adminAuthService.setResourcePolicyByCurrentGroup(GUARDRAIL_API_RESOURCE_PREFIX + guardrailsId);

        return response;
    }

    /**
     * 가드레일 수정
     */
    @Override
    public GuardRailUpdateRes updateGuardRail(String id, GuardRailUpdateReq request) {
        log.info("가드레일 수정 요청 - id: {}, request: {}", id, request);

        // 1. GuardRailUpdateReq를 SktGuardRailUpdateReq로 변환 (정적 팩토리 메서드 사용)
        SktGuardRailUpdateReq sktRequest = SktGuardRailUpdateReq.from(request);

        // 2. SktAiAgentGuardRailsService를 통해 PUT /guardrails/{id} API 호출
        SktGuardRailUpdateRes sktResponse;
        try {
            sktResponse = sktaiAgentGuardRailsService.updateGuardRail(id, sktRequest);
        } catch (BusinessException e) {
            if (e.getMessage() != null && e.getMessage().contains("이미 사용중인 LLM")) {
                throw new BusinessException(ErrorCode.GUARDRAIL_MODEL_ALREADY_IN_USE);
            }

            throw e;
        }

        // 3. 응답 데이터를 GuardRailUpdateRes로 매핑 (정적 팩토리 메서드 사용)
        GuardRailUpdateRes response = GuardRailUpdateRes.from(sktResponse);

        log.info("가드레일 수정 완료 - id: {}", id);

        return response;
    }

    /**
     * 가드레일 복수 삭제
     */
    @Override
    public GuardRailDeleteRes deleteGuardRailBulk(GuardRailDeleteReq request) {
        List<String> guardrailIds = request.getGuardrailIds();

        log.info("가드레일 복수 삭제 - guardrailIds: {}", guardrailIds);

        int totalCount = guardrailIds.size();
        int successCount = 0;
        int failCount = totalCount - successCount;

        for (String guardrailId : guardrailIds) {
            try {
                sktaiAgentGuardRailsService.deleteGuardRail(guardrailId);

                successCount++;

                log.debug("가드레일 삭제 성공 - guardrailId: {}", guardrailId);
            } catch (BusinessException e) {
                // 일부 실패해도 계속 진행
                log.error("가드레일 삭제 실패 - guardrailId: {}", guardrailId, e);
            } catch (Exception e) {
                // 일부 실패해도 계속 진행
                log.error("가드레일 삭제 실패 - guardrailId: {}", guardrailId, e);
            }
        }

        log.info("가드레일 복수 삭제 완료 - 전체: {} 성공: {}, 실패: {}", totalCount, successCount, failCount);

        return GuardRailDeleteRes.of(totalCount, successCount);
    }

    /**
     * 가드레일 Policy 설정
     */
    @Override
    public List<PolicyRequest> setGuardRailPolicy(String guardrailId, String memberId, String projectName) {
        log.info("가드레일 Policy 설정 요청 - guardrailId: {}, memberId: {}, projectName: {}",
                guardrailId, memberId, projectName);

        // guardrailId 검증
        if (!StringUtils.hasText(guardrailId)) {
            log.error("가드레일 Policy 설정 실패 - guardrailId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "가드레일 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("가드레일 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("가드레일 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            String resourceUrl = GUARDRAIL_API_RESOURCE_PREFIX + guardrailId;

            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);
            log.info("가드레일 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}",
                    resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("가드레일 Policy 조회 결과가 null - guardrailId: {}, resourceUrl: {}",
                        guardrailId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                        "가드레일 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
            }

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("가드레일 Policy 설정 완료 - guardrailId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})",
                    guardrailId, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("가드레일 Policy 설정 실패 (BusinessException) - guardrailId: {}, errorCode: {}",
                    guardrailId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("가드레일 Policy 설정 실패 (RuntimeException) - guardrailId: {}, error: {}",
                    guardrailId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("가드레일 Policy 설정 실패 (Exception) - guardrailId: {}", guardrailId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

}
