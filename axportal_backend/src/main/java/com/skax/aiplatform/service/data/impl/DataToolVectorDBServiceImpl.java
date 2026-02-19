package com.skax.aiplatform.service.data.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBCreateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBDetailResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDbsResponse;
import com.skax.aiplatform.client.sktai.knowledge.service.SktaiVectorDbsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.data.request.DataToolVectorDBCreateReq;
import com.skax.aiplatform.dto.data.request.DataToolVectorDBUpdateReq;
import com.skax.aiplatform.dto.data.response.DataArgRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBCreateRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBDetailRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBRes;
import com.skax.aiplatform.mapper.data.DataToolVectorDBMapper;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.data.DataToolVectorDBService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataToolVectorDBServiceImpl implements DataToolVectorDBService {

    private final SktaiVectorDbsService sktaiVectorDbsService;
    private final DataToolVectorDBMapper dataToolVectorDBMapper;
    private final AdminAuthService adminAuthService;
    private final SktaiAuthService sktaiAuthService;

    @Override
    public PageResponse<DataToolVectorDBRes> getVectorDBList(Integer page, Integer size,
            String sort, String filter, String search) {
        try {
            // 1) 외부(SKTAI) API 호출
            VectorDbsResponse response = sktaiVectorDbsService.getVectorDbs(
                    page,
                    size,
                    sort,
                    filter,
                    search);
            // 2) 목록 매핑 (VectorDBsSummary -> DataToolVectorDBRes)
            List<DataToolVectorDBRes> content = Optional.ofNullable(response)
                    .map(VectorDbsResponse::getData)
                    .orElseGet(List::of)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(dataToolVectorDBMapper::from)
                    .collect(Collectors.toList());

            // 3) 페이지네이션 정보 확인 (null-safe 체인으로 NPE 경고 제거)
            // Pagination pagination = Optional.ofNullable(response)
            // .map(VectorDbsResponse::getPayload)
            // .map(com.skax.aiplatform.client.sktai.common.dto.Payload::getPagination)
            // .orElseThrow(() -> {
            // return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "페이지네이션 정보가
            // 없습니다");
            // });

            // // 4) PageableInfo 생성
            // PageableInfo pageableInfo = PageableInfo.builder()
            // .page(Optional.ofNullable(pagination.getPage()).orElse(1))
            // .size(Optional.ofNullable(pagination.getItemsPerPage()).orElse(size))
            // .sort(sort != null ? sort : "")
            // .build();

            // // 5) PageResponse 생성
            // return PageResponse.<DataToolVectorDBRes>builder()
            // .content(content)
            // .pageable(pageableInfo)
            // .totalElements(Optional.ofNullable(pagination.getTotal()).map(Integer::longValue).orElse(0L))
            // .totalPages(Optional.ofNullable(pagination.getLastPage()).orElse(1))
            // .first(Optional.ofNullable(pagination.getPage()).orElse(1) == 1)
            // .last(Objects.equals(pagination.getPage(), pagination.getLastPage()))
            // .hasNext(pagination.getNextPageUrl() != null)
            // .hasPrevious(pagination.getPrevPageUrl() != null)
            // .build();

            PageResponse<DataToolVectorDBRes> pageResponse = PaginationUtils
                    .toPageResponseFromAdxp(response.getPayload(), content);
            return pageResponse;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public DataToolVectorDBDetailRes getVectorDBById(String vectorDbId) {
        try {
            VectorDBDetailResponse response = sktaiVectorDbsService.getVectorDb(vectorDbId);
            DataToolVectorDBDetailRes result = dataToolVectorDBMapper.from(response);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public void deleteVectorDB(String vectorDbId) {
        // log.info("-----------------------------------------------------------------------------------------");
        // log.info("[ Execute Service DataToolVectorDBServiceImpl.deleteVectorDB ]");
        // log.info("VectorDB_id : {}", vectorDbId);
        // log.info("-----------------------------------------------------------------------------------------");

        try {
            sktaiVectorDbsService.deleteVectorDb(vectorDbId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // log.error("데이터 도구 벡터 DB 삭제 실패", e);
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DataToolVectorDBCreateRes createVectorDB(DataToolVectorDBCreateReq request) {
        // log.info("-----------------------------------------------------------------------------------------");
        // log.info("[ Execute Service DataToolVectorDBServiceImpl.createVectorDB ]");
        // log.info("request : {}", request);
        // log.info("-----------------------------------------------------------------------------------------");

        try {
            // 요청을 VectorDBCreateRequest로 변환
            VectorDBCreate createRequest = dataToolVectorDBMapper.toDataToolVectorDBCreateReq(request);

            VectorDBCreateResponse createResponse = sktaiVectorDbsService.addVectorDb(createRequest);

            // 응답을 VectorDBRes로 변환
            DataToolVectorDBCreateRes VectorDBCreateRes = dataToolVectorDBMapper
                    .toDataToolVectorDBCreateRes(createResponse);

            // Dataset ADXP 권한부여
            adminAuthService.setResourcePolicyByCurrentGroup(
                    "/api/v1/knowledge/vectordbs/" + VectorDBCreateRes.getVectorDbId());

            // put 권한 부여 로직 2025-11-26 주석처리 포탈관리자만 CRUD 가능하도록 권한 수정됨
            // adminAuthService.setResourcePolicyByCurrentGroupWithPut(
            // "/api/v1/knowledge/vectordbs/" + VectorDBCreateRes.getVectorDbId());

            return VectorDBCreateRes;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // log.error("데이터 도구 벡터 DB 생성 실패", e);
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateVectorDB(String VectorDBUuid, DataToolVectorDBUpdateReq request) {
        // log.info("-----------------------------------------------------------------------------------------");
        // log.info("[ Execute Service DataToolVectorDBServiceImpl.updateVectorDB ]");
        // log.info("request : {}", request);
        // log.info("-----------------------------------------------------------------------------------------");

        try {
            VectorDBUpdate updateRequest = dataToolVectorDBMapper.toDataToolVectorDBUpdateReq(request);
            sktaiVectorDbsService.updateVectorDb(VectorDBUuid, updateRequest);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 수정에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<DataArgRes> getConnectionArgs() {
        try {
            List<ArgResponse> response = sktaiVectorDbsService.getConnectionArgs();
            List<DataArgRes> result = dataToolVectorDBMapper.toDataArgResList(response);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 벡터 DB 연결 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<PolicyRequest> setVectorDBPolicy(String vectordbId, String memberId, String projectName) {
        log.info("벡터디비 Policy 설정 요청 - vectordbId: {}, memberId: {}, projectName: {}", vectordbId, memberId,
                projectName);

        // fewShotUuid 검증
        if (!StringUtils.hasText(vectordbId)) {
            log.error("벡터디비 Policy 설정 실패 - vectordbId null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "Few-Shot UUID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("벡터디비 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("벡터디비 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/knowledge/vectordbs/" + vectordbId,
                    memberId, projectName);

            log.info("벡터디비 Policy 설정 완료 - vectordbId: {}, memberId: {}, projectName: {}", vectordbId, memberId,
                    projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy("/api/v1/knowledge/vectordbs/" + vectordbId);

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

            log.info("벡터디비 Policy 설정 완료 - vectordbId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", vectordbId,
                    filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("벡터디비 Policy 설정 실패 (BusinessException) - vectordbId: {}, errorCode: {}", vectordbId,
                    e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "벡터디비 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("벡터디비 Policy 설정 실패 (RuntimeException) - vectordbId: {}, error: {}", vectordbId, e.getMessage(),
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "벡터디비 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("벡터디비 Policy 설정 실패 (Exception) - vectordbId: {}", vectordbId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "벡터디비 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }
}