package com.skax.aiplatform.service.deploy.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupKeywordsUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupStopWordsCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupAggregate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupStopWordUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsStopWordRes;
import com.skax.aiplatform.client.sktai.safetyfilter.service.SktaiSafetyFilterGroupStopWordsService;
import com.skax.aiplatform.client.sktai.safetyfilter.service.SktaiSafetyFilterGroupsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterCreateReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterDeleteReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterListReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterUpdateReq;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterCreateRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDeleteRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDetailRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterUpdateRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.deploy.SafetyFilterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 세이프티 필터 서비스 구현체
 *
 * <p>SKT AI Safety Filter API를 호출하여 세이프티 필터를 관리하는 서비스입니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SafetyFilterServiceImpl implements SafetyFilterService {

    private static final String SAFETY_FILTER_API_RESOURCE_PREFIX = "/safety-filters/groups/";

    private final AdminAuthService adminAuthService;

    private final SktaiSafetyFilterGroupsService sktaiSafetyFilterGroupsService;
    private final SktaiSafetyFilterGroupStopWordsService sktaiSafetyFilterGroupStopwordsService;

    private final GpoUsersMasRepository userRepository;
    private final GpoAssetPrjMapMasRepository assetRepository;
    private final ProjectMgmtRepository projectRepository;
    private final SktaiAuthService sktaiAuthService;

    /**
     * 세이프티 필터 목록 조회
     */
    @Override
    public PageResponse<SafetyFilterRes> getSafetyFilterList(SafetyFilterListReq request) {
        log.info("세이프티 필터 목록 조회 시작 - page: {}, size: {}, search: {}, sort: {}, filter: {}",
                request.getPage(), request.getSize(), request.getSearch(), request.getSort(), request.getFilter());

        SktSafetyFilterGroupsStopWordRes filterGroupsStopWordsRes =
                sktaiSafetyFilterGroupStopwordsService.getSafetyFilterGroupsStopWords(
                        request.getPage(),
                        request.getSize(),
                        request.getSort(),
                        request.getFilter(),
                        request.getSearch(),
                        null,
                        false
                );

        log.info("세이프티 필터 그룹 조회 성공 - {}", filterGroupsStopWordsRes);

        // 원본 데이터 추출
        List<SktSafetyFilterGroupAggregate> aggregates = Optional.ofNullable(filterGroupsStopWordsRes.getData())
                .orElse(List.of());

        if (aggregates.isEmpty()) {
            log.info("세이프티 필터 목록이 비어있습니다");

            // ADXP Pagination을 PageResponse로 변환 (빈 리스트)
            return PaginationUtils.toPageResponseFromAdxp(filterGroupsStopWordsRes.getPayload(), List.of());
        }

        // ===== STEP 1: 사용자 UUID 수집 =====
        log.debug("STEP 1: 사용자 UUID 수집");

        Set<String> userUuids = aggregates.stream()
                .flatMap(agg -> Stream.of(agg.getCreatedBy(), agg.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.debug("수집된 사용자 UUID 개수: {}", userUuids.size());

        // <사용자 UUID, USER> HashMap
        final Map<String, GpoUsersMas> userMap = new HashMap<>();
        if (!userUuids.isEmpty()) {
            List<GpoUsersMas> users = userRepository.findByUuidIn(userUuids);

            users.forEach(user -> userMap.put(user.getUuid(), user));
            log.debug("배치 조회된 사용자 수: {} (캐시 크기: {})", users.size(), userMap.size());
        }

        // ===== STEP 2: 에셋 URL 생성 및 배치 조회 =====
        log.debug("STEP 2: 에셋 URL 생성");
        List<String> assetUrls = aggregates.stream()
                .map(agg -> SAFETY_FILTER_API_RESOURCE_PREFIX + agg.getGroupId())
                .toList();

        final Map<String, GpoAssetPrjMapMas> assetMap = new HashMap<>();
        if (!assetUrls.isEmpty()) {
            List<GpoAssetPrjMapMas> assets = assetRepository.findByAsstUrlIn(assetUrls);

            assets.forEach(asset -> assetMap.put(asset.getAsstUrl(), asset));
            log.debug("배치 조회된 에셋 수: {} (캐시 크기: {})", assets.size(), assetMap.size());
        }

        // ===== STEP 3: SafetyFilterRes 변환 =====
        log.debug("STEP 3: 응답 객체 변환");
        List<SafetyFilterRes> safetyFilterRes = aggregates.stream()
                .map(aggregate -> {
                    String assetUrl = SAFETY_FILTER_API_RESOURCE_PREFIX + aggregate.getGroupId();

                    GpoUsersMas createdByUser = userMap.get(aggregate.getCreatedBy());
                    GpoUsersMas updatedByUser = userMap.get(aggregate.getUpdatedBy());
                    GpoAssetPrjMapMas assetInfo = assetMap.get(assetUrl);

                    return SafetyFilterRes.of(aggregate, createdByUser, updatedByUser, assetInfo);
                })
                .toList();

        log.info("변환된 SafetyFilterRes 개수: {}", safetyFilterRes.size());

        // ADXP Pagination을 PageResponse로 변환
        PageResponse<SafetyFilterRes> result = PaginationUtils.toPageResponseFromAdxp(
                filterGroupsStopWordsRes.getPayload(), 
                safetyFilterRes
        );

        log.info("세이프티 필터 목록 조회 완료 - 최종 반환 데이터 수: {}, 전체 개수: {}",
                result.getContent().size(), result.getTotalElements());

        return result;
    }

    /**
     * 세이프티 필터 상세 조회
     */
    @Override
    public SafetyFilterDetailRes getSafetyFilterDetail(String filterGroupId) {
        log.info("세이프티 필터 상세 조회 시작 - filterGroupId: {}", filterGroupId);

        String filterQuery = "group_id:" + filterGroupId;

        SktSafetyFilterGroupsStopWordRes filterGroupsStopWordsRes =
                sktaiSafetyFilterGroupStopwordsService.getSafetyFilterGroupsStopWords(
                        1,
                        -1,
                        null,
                        filterQuery,
                        null,
                        null,
                        false
                );

        // 상세 조회기 떄문에 첫번째 것만 가져오기
        List<SktSafetyFilterGroupAggregate> aggregates = filterGroupsStopWordsRes.getData();
        SktSafetyFilterGroupAggregate aggregate = aggregates.get(0);

        // ===== STEP 1: 사용자 정보 조회 =====
        log.info("STEP 1: 사용자 정보 조회");

        Set<String> userUuids = Stream.of(aggregate.getCreatedBy(), aggregate.getUpdatedBy())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<String, GpoUsersMas> userMap = new HashMap<>();
        if (!userUuids.isEmpty()) {
            List<GpoUsersMas> users = userRepository.findByUuidIn(userUuids);

            users.forEach(user -> userMap.put(user.getUuid(), user));
            log.info("조회된 사용자 수: {}", users.size());
        }

        // ===== STEP 2: 에셋 매핑 정보 및 프로젝트명 조회 =====
        log.info("STEP 2: 에셋 매핑 정보 및 프로젝트명 조회");

        String assetUrl = SAFETY_FILTER_API_RESOURCE_PREFIX + filterGroupId;
        GpoAssetPrjMapMas assetInfo = assetRepository.findByAsstUrl(assetUrl)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "세이프티 필터에 대한 에셋 정보를 찾을 수 없습니다: " + filterGroupId
                ));

        String projectName = extractProjectName(assetInfo.getLstPrjSeq());
        log.info("프로젝트명 해석 완료 - projectName: {}", projectName);

        // ===== STEP 3: 공개 에셋 수정자 정보 조회 =====
        log.info("STEP 3: 공개 에셋 수정자 정보 조회");

        GpoUsersMas publicAssetUpdatedByUser = null;

        if (assetInfo.getLstPrjSeq() != null && assetInfo.getLstPrjSeq() == -999) {
            String publicAssetUpdatedByMemberId = assetInfo.getUpdatedBy();
            log.info("공개 에셋 수정자 memberId: {}", publicAssetUpdatedByMemberId);

            if (publicAssetUpdatedByMemberId != null) {
                publicAssetUpdatedByUser = userRepository.findByMemberId(publicAssetUpdatedByMemberId).orElse(null);
                log.info("공개 에셋 수정자 조회 결과: {}", publicAssetUpdatedByUser != null ?
                        publicAssetUpdatedByUser.getJkwNm() : "NULL");
            }
        }

        // ===== STEP 4: SafetyFilterDetailRes 변환 =====
        log.info("STEP 4: 응답 객체 변환");

        GpoUsersMas createdByUser = userMap.get(aggregate.getCreatedBy());
        GpoUsersMas updatedByUser = userMap.get(aggregate.getUpdatedBy());

        SafetyFilterDetailRes result = SafetyFilterDetailRes.of(
                aggregate,
                projectName,
                createdByUser,
                updatedByUser,
                assetInfo,
                publicAssetUpdatedByUser
        );

        log.info("세이프티 필터 상세 조회 완료 -  name: {}", result.getFilterGroupName());

        return result;
    }

    /**
     * 세이프티 필터 생성
     */
    @Override
    public SafetyFilterCreateRes createSafetyFilter(SafetyFilterCreateReq request) {
        String filterGroupName = request.getFilterGroupName();
        log.info("세이프티 필터 생성 - filterGroupName: {}, stopWords: {}", filterGroupName, request.getStopWords());

        String filterGroupId = null;

        try {
            // === STEP 1: Safety Filter 그룹 생성 ===
            log.info("세이프티 필터 그룹 생성 시작");
            var filterGroupCreateReq = SktSafetyFilterGroupCreateReq.from(filterGroupName);

            SktSafetyFilterGroupUpdateRes filterGroupCreateRes =
                    sktaiSafetyFilterGroupsService.createSafetyFilterGroup(filterGroupCreateReq);

            filterGroupId = filterGroupCreateRes.getId();

            // 세이프티 필터 정책 부여
            adminAuthService.setResourcePolicyByCurrentGroup(SAFETY_FILTER_API_RESOURCE_PREFIX + filterGroupId);

            log.info("세이프티 필터 그룹 생성 완료 - filterGroupId: {}, name: {}", filterGroupId, filterGroupName);

            // === STEP 2: Safety Filter 그룹에 금지어 추가 ===
            List<String> stopWords = request.getStopWords().stream()
                    .map(String::trim)
                    .toList();
            log.info("생성 금지어 목록: {}", stopWords);

            log.info("세이프티 필터 금지어 생성 시작");
            var stopWordsCreateReq = SktSafetyFilterGroupStopWordsCreateReq.from(stopWords);

            SktSafetyFilterGroupStopWordUpdateRes stopWordsCreateRes =
                    sktaiSafetyFilterGroupStopwordsService.appendSafetyFilterGroupKeywords(
                            filterGroupId,
                            stopWordsCreateReq
                    );

            log.info("세이프티 필터 금지어 추가 완료 - filterGroupId: {}, 추가된 수: {}, 최종 개수: {}",
                    filterGroupId, stopWordsCreateRes.getCreatedCount(), stopWordsCreateRes.getTotalCount());

            return SafetyFilterCreateRes.of(filterGroupId, filterGroupName, stopWords);
        } catch (BusinessException e) {
            // ===== 보상 트랜잭션: STEP 2 실패 시 STEP 1 롤백 =====
            if (filterGroupId != null) {
                rollbackGroupCreation(filterGroupId);
            }

            if (e.getErrorCode() == ErrorCode.EXTERNAL_API_CONFLICT) {
                log.warn("세이프티 필터 생성 실패 - 동일한 분류명이 이미 존재합니다. 분류명 : {}", filterGroupName);
                throw new BusinessException(ErrorCode.SAFETY_FILTER_NAME_ALREADY_EXISTS);
            }

            throw e;
        } catch (Exception e) {
            // ===== 보상 트랜잭션: STEP 2 실패 시 STEP 1 롤백 =====
            if (filterGroupId != null) {
                rollbackGroupCreation(filterGroupId);
            }

            log.error("세이프티 필터 생성 실패 - filterGroupName: {}", request.getFilterGroupName(), e);

            throw e; // 원래 예외 재던지기
        }
    }


    /**
     * 세이프티 필터 수정
     */
    @Override
    public SafetyFilterUpdateRes updateSafetyFilter(String groupId, SafetyFilterUpdateReq request) {
        log.info("세이프티 필터 수정 - groupId: {}, category: {}, stopWords: {}",
                groupId, request.getFilterGroupName(), request.getStopWords());

        // === STEP 1: Safety Filter 그룹 수정 ===
        SktSafetyFilterGroupUpdateReq filterGroupUpdateReq =
                SktSafetyFilterGroupUpdateReq.from(request.getFilterGroupName());

        sktaiSafetyFilterGroupsService.updateSafetyFilterGroup(groupId, filterGroupUpdateReq);

        log.debug("세이프티 필터 그룹명 수정 완료 - groupId: {}, newName: {}",
                groupId, request.getFilterGroupName());

        // === STEP 2: Safety Filter 그룹 금지어 교체 ===
        SktSafetyFilterGroupKeywordsUpdateReq stopWordsUpdateReq =
                SktSafetyFilterGroupKeywordsUpdateReq.from(request.getStopWords());

        SktSafetyFilterGroupStopWordUpdateRes stopWordsUpdateRes =
                sktaiSafetyFilterGroupStopwordsService.updateSafetyFilterGroupKeywords(groupId, stopWordsUpdateReq);

        // createdBy 사용자 정보 조회
        GpoUsersMas createdByUser = null;
        if (stopWordsUpdateRes.getCreatedBy() != null) {
            createdByUser = userRepository.findByUuid(stopWordsUpdateRes.getCreatedBy()).orElse(null);
        }

        // updatedBy 사용자 정보 조회
        GpoUsersMas updatedByUser = null;
        if (stopWordsUpdateRes.getUpdatedBy() != null) {
            updatedByUser = userRepository.findByUuid(stopWordsUpdateRes.getUpdatedBy()).orElse(null);
        }

        log.info("세이프티 필터 수정 완료 - groupId: {}, 그룹명: {}, 생성: {}, 삭제: {}, 최종: {}",
                stopWordsUpdateRes.getGroupId(), request.getFilterGroupName(),
                stopWordsUpdateRes.getCreatedCount(), stopWordsUpdateRes.getDeletedCount(),
                stopWordsUpdateRes.getTotalCount());

        return SafetyFilterUpdateRes.of(stopWordsUpdateRes, createdByUser, updatedByUser);
    }

    /**
     * 세이프티 필터 그룹 삭제
     */
    @Override
    public SafetyFilterDeleteRes deleteSafetyFilterBulk(SafetyFilterDeleteReq request) {
        log.info("세이프티 필터 복수 삭제 - groupIds: {}", request.getFilterGroupIds());

        int totalCount = request.getFilterGroupIds().size();
        int successCount = 0;
        RuntimeException lastException = null;

        for (String groupId : request.getFilterGroupIds()) {
            try {
                sktaiSafetyFilterGroupsService.deleteSafetyFilterGroup(groupId);
                successCount++;

                log.debug("세이프티 필터 그룹 삭제 성공 - groupId: {}", groupId);
            } catch (BusinessException e) {
                lastException = e;
                log.error("세이프티 필터 그룹 삭제 실패 - groupId: {}", groupId, e);
            } catch (Exception e) {
                lastException = (e instanceof RuntimeException)
                        ? (RuntimeException) e
                        : new RuntimeException(e);
                log.error("세이프티 필터 그룹 삭제 실패 - groupId: {}", groupId, e);
            }
        }

        // 모든 삭제가 실패한 경우 마지막 예외를 던짐
        if (successCount == 0 && lastException != null) {
            throw lastException;
        }

        int failCount = totalCount - successCount;

        log.info("세이프티 필터 그룹 복수 삭제 완료 - 전체: {} 성공: {}, 실패: {}",
                totalCount, successCount, failCount);

        return SafetyFilterDeleteRes.of(totalCount, successCount);
    }

    // Private Method

    /**
     * 프로젝트명 추출
     *
     * @param lstPrjSeq 최종 프로젝트 seq
     * @return 프로젝트명
     */
    private String extractProjectName(Integer lstPrjSeq) {
        if (lstPrjSeq == null) {
            return null;
        }

        // 유효한 프로젝트 SEQ인 경우, 프로젝트 정보 조회
        Project project = projectRepository.findById(Long.valueOf(lstPrjSeq)).orElse(null);

        if (project != null) {
            return project.getPrjNm();
        }

        return null;
    }

    /**
     * 세이프티 필터 그룹 생성 롤백 (보상 트랜잭션)
     */
    private void rollbackGroupCreation(String filterGroupId) {
        try {
            log.warn("보상 트랜잭션 시작 - 키워드 추가 실패로 그룹 롤백 시도 - filterGroupId: {}", filterGroupId);

            sktaiSafetyFilterGroupsService.deleteSafetyFilterGroup(filterGroupId);

            log.info("보상 트랜잭션 완료 - 그룹 삭제 성공 - filterGroupId: {}", filterGroupId);

        } catch (BusinessException e) {
            log.error("보상 트랜잭션 실패 - 그룹 삭제 실패 (수동 정리 필요) - filterGroupId: {}",
                    filterGroupId, e);
        } catch (Exception rollbackEx) {
            // 롤백 실패 시 처리 로직 추가 필요
            // 현재 상황: 세이프티 필터 그룹은 생성되었지만 삭제 실패 → SKT AI 서버에 빈 세이프티 필터 그룹 남음

            log.error("보상 트랜잭션 실패 - 그룹 삭제 실패 (수동 정리 필요) - filterGroupId: {}",
                    filterGroupId, rollbackEx);
        }
    }

    /**
     * 세이프티 필터 Policy 설정
     */
    @Override
    public List<PolicyRequest> setSafetyFilterPolicy(String filterGroupId, String memberId, String projectName) {
        log.info("세이프티 필터 Policy 설정 요청 - filterGroupId: {}, memberId: {}, projectName: {}",
                filterGroupId, memberId, projectName);

        // filterGroupId 검증
        if (!StringUtils.hasText(filterGroupId)) {
            log.error("세이프티 필터 Policy 설정 실패 - filterGroupId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "세이프티 필터 그룹 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("세이프티 필터 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("세이프티 필터 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            String resourceUrl = SAFETY_FILTER_API_RESOURCE_PREFIX + filterGroupId;

            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName(resourceUrl, memberId, projectName);
            log.info("세이프티 필터 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}",
                    resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("세이프티 필터 Policy 조회 결과가 null - filterGroupId: {}, resourceUrl: {}",
                        filterGroupId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                        "세이프티 필터 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
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

            log.info("세이프티 필터 Policy 설정 완료 - filterGroupId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})",
                    filterGroupId, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("세이프티 필터 Policy 설정 실패 (BusinessException) - filterGroupId: {}, errorCode: {}",
                    filterGroupId, e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "세이프티 필터 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("세이프티 필터 Policy 설정 실패 (RuntimeException) - filterGroupId: {}, error: {}",
                    filterGroupId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "세이프티 필터 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("세이프티 필터 Policy 설정 실패 (Exception) - filterGroupId: {}", filterGroupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "세이프티 필터 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

}
