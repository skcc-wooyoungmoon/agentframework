package com.skax.aiplatform.client.ione.api.service;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.ione.api.IoneApiClient;
import com.skax.aiplatform.client.ione.api.dto.request.ApiDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.request.ApiRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.request.ApiUpdateRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupUpdateRequest;
import com.skax.aiplatform.client.ione.api.dto.response.ApiDeleteResponse;
import com.skax.aiplatform.client.ione.api.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.ApiListResultWithPagination;
import com.skax.aiplatform.client.ione.api.dto.response.ApiRegistResponse;
import com.skax.aiplatform.client.ione.api.dto.response.ApiServerGroupInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.ApiServerGroupListResult;
import com.skax.aiplatform.client.ione.api.dto.response.CommonResult;
import com.skax.aiplatform.client.ione.api.dto.response.PublishWorkInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.PublishWorkListResult;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupDeleteResponse;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupListResult;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupRegistResult;
import com.skax.aiplatform.client.ione.common.dto.InfRequestBody;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * iONE API Common 서비스
 * 
 * <p>iONE Gateway 등록된 API 정보 관리 API와의 통신을 담당하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneApiService {

    private final IoneApiClient ioneApiClient;

    // ========== API 목록/정보 조회 ==========

    /**
     * [API-COM-001] API 목록 조회
     * 
     * @return API 목록 결과
     */
    public ApiListResultWithPagination getApiList() {
        try {
            log.info("🟣 iONE API 목록 조회 요청");
            
            ApiListResultWithPagination result = ioneApiClient.getApiList();
            
            log.info("🟣 iONE API 목록 조회 성공 - totalCount: {}", result.getTotalCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-002] API 정보 조회
     * 
     * @param apiId 조회할 API ID
     * @return API 상세 정보
     */
    public ApiInfoResult getApiInfo(String apiId) {
        try {
            log.info("🟣 iONE API 정보 조회 요청 - apiId: {}", apiId);
            
            InfResponseBody<ApiInfoResult> result = ioneApiClient.getApiInfo(apiId);
            
            log.info("🟣 iONE API 정보 조회 성공 - apiId: {}", apiId);
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API 정보 조회 실패 - apiId: {}, BusinessException: {}", apiId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 정보 조회 실패 - apiId: {}, 예상치 못한 오류", apiId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== API 등록/수정/삭제 ==========

    /**
     * [API-COM-003] API 등록
     * 
     * @param request API 등록 요청 정보
     * @return 등록 결과
     */
    public ApiRegistResponse registApi(ApiRegistRequest request) {
        try {
            log.info("🟣 iONE API 등록 요청 - apiId: {}, apiName: {}", request.getApiId(), request.getApiName());
            
            InfRequestBody<ApiRegistRequest> requestBody = InfRequestBody.<ApiRegistRequest>builder()
                    .data(InfRequestBody.InfReqData.<ApiRegistRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            InfResponseBody<ApiRegistResponse> result = ioneApiClient.registApi(requestBody);
            
            log.info("🟣 iONE API 등록 성공 - apiId: {}", request.getApiId());
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API 등록 실패 - apiId: {}, BusinessException: {}", request.getApiId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 등록 실패 - apiId: {}, 예상치 못한 오류", request.getApiId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-004] API 수정
     * 
     * @param request API 수정 요청 정보
     * @return 수정 결과
     */
    public CommonResult updateApi(ApiUpdateRequest request) {
        try {
            log.info("🟣 iONE API 수정 요청 - apiId: {}, apiName: {}", request.getApiId(), request.getApiName());
            
            CommonResult result = ioneApiClient.updateApi(request);
            
            log.info("🟣 iONE API 수정 성공 - apiId: {}", request.getApiId());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API 수정 실패 - apiId: {}, BusinessException: {}", request.getApiId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 수정 실패 - apiId: {}, 예상치 못한 오류", request.getApiId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-005] API 삭제
     * 
     * @param request API 삭제 요청 정보
     * @return 삭제 결과
     */
    public ApiDeleteResponse deleteApi(ApiDeleteRequest request) {
        try {
            log.info("🟣 iONE API 삭제 요청 - apiId: {}", request.getApiId());
            
            InfRequestBody<ApiDeleteRequest> requestBody = InfRequestBody.<ApiDeleteRequest>builder()
                    .data(InfRequestBody.InfReqData.<ApiDeleteRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            InfResponseBody<ApiDeleteResponse> result = ioneApiClient.deleteApi(requestBody);
            
            log.info("🟣 iONE API 삭제 성공 - apiId: {}", request.getApiId());
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API 삭제 실패 - apiId: {}, BusinessException: {}", request.getApiId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 삭제 실패 - apiId: {}, 예상치 못한 오류", request.getApiId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== API 서버 그룹 관리 ==========

    /**
     * [API-COM-006] API 서버 그룹 목록 조회
     * 
     * @return API 서버 그룹 목록
     */
    public ApiServerGroupListResult getApiServerGroupList() {
        try {
            log.info("🟣 iONE API 서버 그룹 목록 조회 요청");
            
            ApiServerGroupListResult result = ioneApiClient.getApiServerGroupList();
            
            log.info("🟣 iONE API 서버 그룹 목록 조회 성공 - totalCount: {}", result.getTotalCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API 서버 그룹 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 서버 그룹 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 서버 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-007] API 서버 그룹 정보 조회
     * 
     * @param apiSvrGrpId API 서버 그룹 ID
     * @return API 서버 그룹 상세 정보
     */
    public ApiServerGroupInfoResult getApiServerGroupInfo(String apiSvrGrpId) {
        try {
            log.info("🟣 iONE API 서버 그룹 정보 조회 요청 - apiSvrGrpId: {}", apiSvrGrpId);
            
            ApiServerGroupInfoResult result = ioneApiClient.getApiServerGroupInfo(apiSvrGrpId);
            
            log.info("🟣 iONE API 서버 그룹 정보 조회 성공 - apiSvrGrpId: {}", apiSvrGrpId);
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API 서버 그룹 정보 조회 실패 - apiSvrGrpId: {}, BusinessException: {}", apiSvrGrpId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 서버 그룹 정보 조회 실패 - apiSvrGrpId: {}, 예상치 못한 오류", apiSvrGrpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 서버 그룹 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== 업무 코드 관리 ==========

    /**
     * [API-COM-008] 업무 코드 등록
     * 
     * @param request 업무 코드 등록 요청 정보
     * @return 등록 결과
     */
    public WorkGroupRegistResult registWorkGroup(WorkGroupRegistRequest request) {
        try {
            log.info("🟣 iONE 업무 코드 등록 요청 - businessCode: {}, businessName: {}", request.getBusinessCode(), request.getBusinessName());
            
            InfRequestBody<WorkGroupRegistRequest> requestBody = InfRequestBody.<WorkGroupRegistRequest>builder()
                    .data(InfRequestBody.InfReqData.<WorkGroupRegistRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            InfResponseBody<WorkGroupRegistResult> result = ioneApiClient.registWorkGroup(requestBody);
            
            log.info("🟣 iONE 업무 코드 등록 성공 - taskId: {}", request.getBusinessCode());
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE 업무 코드 등록 실패 - businessCode: {}, BusinessException: {}", request.getBusinessCode(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 업무 코드 등록 실패 - businessCode: {}, 예상치 못한 오류", request.getBusinessCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 업무 코드 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-009] 업무 코드 조회
     * 
     * @return 업무 코드 목록
     */
    public WorkGroupListResult getWorkGroupList() {
        try {
            log.info("🟣 iONE 업무 코드 목록 조회 요청");
            
            WorkGroupListResult result = ioneApiClient.getWorkGroupList();
            
            log.info("🟣 iONE 업무 코드 목록 조회 성공 - totalCount: {}", result.getTotalCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 업무 코드 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 업무 코드 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 업무 코드 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-010] 업무 코드 삭제
     * 
     * @param request 업무 코드 삭제 요청 정보
     * @return 삭제 결과
     */
    public WorkGroupDeleteResponse deleteWorkGroup(WorkGroupDeleteRequest request) {
        try {
            log.info("🟣 iONE 업무 코드 삭제 요청 - request: {}", request);
            
            InfRequestBody<WorkGroupDeleteRequest> requestBody = InfRequestBody.<WorkGroupDeleteRequest>builder()
                    .data(InfRequestBody.InfReqData.<WorkGroupDeleteRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            InfResponseBody<WorkGroupDeleteResponse> result = ioneApiClient.deleteWorkGroup(requestBody);
            
            log.info("🟣 iONE 업무 코드 삭제 성공 - businessCode: {}", request.getBusinessCode());
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE 업무 코드 삭제 실패 - businessCode: {}, BusinessException: {}", request.getBusinessCode(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 업무 코드 삭제 실패 - request: {}, 예상치 못한 오류", request, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 업무 코드 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-011] 업무 코드 수정
     * 
     * @param request 업무 코드 수정 요청 정보
     * @return 수정 결과
     */
    public CommonResult updateWorkGroup(WorkGroupUpdateRequest request) {
        try {
            log.info("🟣 iONE 업무 코드 수정 요청 - taskId: {}, taskName: {}", request.getTaskId(), request.getTaskName());
            
            CommonResult result = ioneApiClient.updateWorkGroup(request);
            
            log.info("🟣 iONE 업무 코드 수정 성공 - taskId: {}", request.getTaskId());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 업무 코드 수정 실패 - taskId: {}, BusinessException: {}", request.getTaskId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 업무 코드 수정 실패 - taskId: {}, 예상치 못한 오류", request.getTaskId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 업무 코드 수정에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== 작업 요청 관리 ==========

    /**
     * [API-COM-012] 작업 재 요청
     * 
     * @param infWorkSeq 작업 순번
     * @return 재 요청 결과
     */
    public ApiRegistResponse republishWork(String infWorkSeq) {
        try {
            log.info("🟣 iONE 작업 재 요청 - infWorkSeq: {}", infWorkSeq);
            
            // ApiRePubRequest request = ApiRePubRequest.builder()
            //         .infWorkUser("ShinHanManager01@shinhan.com")
            //         .build();

            InfRequestBody<Void> requestBody = InfRequestBody.<Void>builder()
                    .data(InfRequestBody.InfReqData.<Void>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(null)
                            .build())
                    .build();

            InfResponseBody<ApiRegistResponse> result = ioneApiClient.republishWork(infWorkSeq, requestBody);
            
            log.info("🟣 iONE 작업 재 요청 성공 - infWorkSeq: {}", infWorkSeq);
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE 작업 재 요청 실패 - infWorkSeq: {}, BusinessException: {}", infWorkSeq, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 작업 재 요청 실패 - infWorkSeq: {}, 예상치 못한 오류", infWorkSeq, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 작업 재 요청에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-013] 작업 요청 취소
     * 
     * @param infWorkSeq 작업 순번
     * @return 취소 결과
     */
    public CommonResult cancelWork(String infWorkSeq) {
        try {
            log.info("🟣 iONE 작업 요청 취소 - infWorkSeq: {}", infWorkSeq);
            
            CommonResult result = ioneApiClient.cancelWork(infWorkSeq);
            
            log.info("🟣 iONE 작업 요청 취소 성공 - infWorkSeq: {}", infWorkSeq);
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 작업 요청 취소 실패 - infWorkSeq: {}, BusinessException: {}", infWorkSeq, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 작업 요청 취소 실패 - infWorkSeq: {}, 예상치 못한 오류", infWorkSeq, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 작업 요청 취소에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-014] 작업 요청 결과 목록 조회
     * 
     * @return 작업 요청 결과 목록
     */
    public PublishWorkListResult getPublishWorkList() {
        try {
            log.info("🟣 iONE 작업 요청 결과 목록 조회 요청");
            
            PublishWorkListResult result = ioneApiClient.getPublishWorkList();
            
            log.info("🟣 iONE 작업 요청 결과 목록 조회 성공 - totalCount: {}", result.getTotalCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 작업 요청 결과 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 작업 요청 결과 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 작업 요청 결과 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-COM-015] 작업 요청 결과 조회
     * 
     * @param infWorkSeq 작업 순번
     * @return 작업 요청 결과 상세 정보
     */
    public PublishWorkInfoResult getPublishWorkInfo(String infWorkSeq) {
        try {
            log.info("🟣 iONE 작업 요청 결과 조회 - infWorkSeq: {}", infWorkSeq);
            
            InfResponseBody<PublishWorkInfoResult> result = ioneApiClient.getPublishWorkInfo(infWorkSeq);
            
            log.info("🟣 iONE 작업 요청 결과 조회 성공 - infWorkSeq: {}", infWorkSeq);
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE 작업 요청 결과 조회 실패 - infWorkSeq: {}, BusinessException: {}", infWorkSeq, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 작업 요청 결과 조회 실패 - infWorkSeq: {}, 예상치 못한 오류", infWorkSeq, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 작업 요청 결과 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
