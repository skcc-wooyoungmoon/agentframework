package com.skax.aiplatform.client.ione.apikey.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.ione.apikey.IoneApiKeyClient;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyDeleteRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRegistRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRenewRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRescheduleRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyUpdateRequest;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyListResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyRegistResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyRenewResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyUpdateResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyVo;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfPubApiResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfRateLimitStatisticsVo;
import com.skax.aiplatform.client.ione.common.dto.InfRequestBody;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * iONE Open API Key 서비스
 * 
 * <p>iONE Open API Key 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * API Key 관리 관련 11개 API에 대한 서비스 메서드를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneApiKeyService {

    private final IoneApiKeyClient ioneApiKeyClient;

    /**
     * [API-KEY-001] Open API Key 목록 조회
     * 
     * <p>등록된 API Key 목록을 페이징하여 조회합니다.</p>
     * 
     * @param pageNum 페이지 번호
     * @param pageSize 페이지 크기
     * @param partnerId 파트너 ID (선택)
     * @param grpId 그룹 ID (선택)
     * @param scope 스코프 (선택)
     * @return API Key 목록 조회 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyListResult selectApiKeyList(Integer pageNum, Integer pageSize, String partnerId, String grpId, String scope, String orderBy) {
        try {
            log.info("🟣 iONE Open API Key 목록 조회 요청 - pageNum: {}, pageSize: {}, partnerId: {}, grpId: {}, scope: {}", 
                    pageNum, pageSize, partnerId, grpId, scope);
            
            IntfOpenApiKeyListResult result = ioneApiKeyClient.selectApiKeyList(pageNum, pageSize, partnerId, grpId, scope, orderBy);
            
            log.info("🟣 iONE Open API Key 목록 조회 성공 - 조회 건수: {}", result.getTotalCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 목록 조회 실패 - pageNum: {}, pageSize: {}, BusinessException: {}", pageNum, pageSize, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 목록 조회 실패 - pageNum: {}, pageSize: {}, 예상치 못한 오류", pageNum, pageSize, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-002] Open API Key 단건 조회
     * 
     * <p>특정 API Key의 상세 정보를 조회합니다.</p>
     * 
     * @param openApiKey 조회할 API Key
     * @return API Key 상세 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyVo selectApiKey(String openApiKey) {
        try {
            log.info("🟣 iONE Open API Key 단건 조회 요청 - openApiKey: {}", openApiKey);
            
            InfResponseBody<IntfOpenApiKeyVo> result = ioneApiKeyClient.selectApiKey(openApiKey);
            
            log.info("🟣 iONE Open API Key 단건 조회 성공 - openApiKey: {}", openApiKey);
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 단건 조회 실패 - openApiKey: {}, BusinessException: {}", openApiKey, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 단건 조회 실패 - openApiKey: {}, 예상치 못한 오류", openApiKey, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-003] Open API Key 신규 발급
     * 
     * <p>새로운 API Key를 발급합니다.
     * 파트너 정보와 그룹 정보를 기반으로 API Key를 생성합니다.</p>
     * 
     * @param request API Key 발급 요청
     * @return API Key 발급 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public InfResponseBody<IntfOpenApiKeyRegistResult> issueApiKey(IntfOpenApiKeyRegistRequest request) {
        try {
            log.info("🟣 iONE Open API Key 신규 발급 요청 - partnerId: {}, grpId: {}", 
                    request.getPartnerId(), request.getGrpId());
            
            InfRequestBody<IntfOpenApiKeyRegistRequest> requestBody = InfRequestBody.<IntfOpenApiKeyRegistRequest>builder()
                    .data(InfRequestBody.InfReqData.<IntfOpenApiKeyRegistRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            log.info("🟣 iONE Open API Key 신규 발급 요청 - requestBody: {}", requestBody);

            InfResponseBody<IntfOpenApiKeyRegistResult> response = ioneApiKeyClient.issueApiKey(requestBody);
            
            log.info("🟣 iONE Open API Key 신규 발급 성공 - partnerId: {}, result: {}", 
                    request.getPartnerId(), response.getResult().getMsg().getDesc());

            
            return response;
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 신규 발급 실패 - partnerId: {}, BusinessException: {}", request.getPartnerId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 신규 발급 실패 - partnerId: {}, 예상치 못한 오류", request.getPartnerId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 발급에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-004] Open API Key 수정
     * 
     * <p>기존 API Key의 정보를 수정합니다.
     * 설명, 사용 여부, 유효 기간 등을 변경할 수 있습니다.</p>
     * 
     * @param request API Key 수정 요청
     * @return API Key 수정 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyUpdateResult updateApiKey(IntfOpenApiKeyUpdateRequest request) {
        try {
            log.info("🟣 iONE Open API Key 수정 요청 - openApiKey: {}, useYn: {}", 
                    request.getOpenApiKey());
            
            InfRequestBody<IntfOpenApiKeyUpdateRequest> requestBody = InfRequestBody.<IntfOpenApiKeyUpdateRequest>builder()
                    .data(InfRequestBody.InfReqData.<IntfOpenApiKeyUpdateRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            log.info("🟣 iONE Open API Key 수정 요청 - requestBody: {}", requestBody);
            IntfOpenApiKeyUpdateResult result = ioneApiKeyClient.updateApiKey(requestBody);
            // IntfOpenApiKeyUpdateResult result = IntfOpenApiKeyUpdateResult.builder()
            //         .resultCode("0000")
            //         .resultMessage("API Key 수정 성공")
            //         .build();
            log.info("🟣 iONE Open API Key 수정 성공 - openApiKey: {}, result: {}", 
                    request.getOpenApiKey(), result.getResultCode());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 수정 실패 - openApiKey: {}, BusinessException: {}", request.getOpenApiKey(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 수정 실패 - openApiKey: {}, 예상치 못한 오류", request.getOpenApiKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-005] Open API Key 삭제
     * 
     * <p>기존 API Key를 삭제합니다.
     * 실제로는 사용 여부를 'N'으로 변경하여 비활성화합니다.</p>
     * 
     * @param request API Key 삭제 요청
     * @return API Key 삭제 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public void deleteApiKey(IntfOpenApiKeyDeleteRequest request) {
        try {
            log.info("🟣 iONE Open API Key 삭제 요청 - openApiKey: {}", request.getOpenApiKey());
            
            InfRequestBody<IntfOpenApiKeyDeleteRequest> requestBody = InfRequestBody.<IntfOpenApiKeyDeleteRequest>builder()
                    .data(InfRequestBody.InfReqData.<IntfOpenApiKeyDeleteRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            ioneApiKeyClient.deleteApiKey(requestBody);
            
            log.info("🟣 iONE Open API Key 삭제 성공 - openApiKey: {}", request.getOpenApiKey());
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 삭제 실패 - openApiKey: {}, BusinessException: {}", request.getOpenApiKey(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 삭제 실패 - openApiKey: {}, 예상치 못한 오류", request.getOpenApiKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-006] Open API Key 갱신
     * 
     * <p>기존 API Key를 갱신하여 새로운 Key 값을 발급받습니다.
     * 기존 설정은 유지하면서 Key 값만 새로 생성됩니다.</p>
     * 
     * @param request API Key 갱신 요청
     * @return API Key 갱신 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyRenewResult renewApiKey(IntfOpenApiKeyRenewRequest request) {
        try {
            log.info("🟣 iONE Open API Key 갱신 요청 - openApiKey: {}", request.getOpenApiKey());
            
            IntfOpenApiKeyRenewResult result = ioneApiKeyClient.renewApiKey(request);
            
            log.info("🟣 iONE Open API Key 갱신 성공 - oldKey: {}, resultCode: {}", 
                    request.getOpenApiKey(), result.getResultCode());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 갱신 실패 - openApiKey: {}, BusinessException: {}", request.getOpenApiKey(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 갱신 실패 - openApiKey: {}, 예상치 못한 오류", request.getOpenApiKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 갱신에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-007] Open API Key 재발급
     * 
     * <p>기존 API Key를 완전히 새로운 Key로 재발급합니다.
     * 기존 Key는 무효화되고 새로운 Key가 생성됩니다.</p>
     * 
     * @param request API Key 재발급 요청
     * @return API Key 재발급 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyRegistResult regenerateApiKey(IntfOpenApiKeyRegistRequest request) {
        try {
            log.info("🟣 iONE Open API Key 재발급 요청 - partnerId: {}, grpId: {}", 
                    request.getPartnerId(), request.getGrpId());
            
            InfRequestBody<IntfOpenApiKeyRegistRequest> requestBody = InfRequestBody.<IntfOpenApiKeyRegistRequest>builder()
                    .data(InfRequestBody.InfReqData.<IntfOpenApiKeyRegistRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();

            InfResponseBody<IntfOpenApiKeyRegistResult> result = ioneApiKeyClient.regenerateApiKey(requestBody);

            log.info("🟣 iONE Open API Key 재발급 성공 - partnerId: {}, result: {}", 
                    request.getPartnerId(), result.getData());
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 재발급 실패 - partnerId: {}, BusinessException: {}", request.getPartnerId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 재발급 실패 - partnerId: {}, 예상치 못한 오류", request.getPartnerId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 재발급에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-008] Open API Key 유효기간 재설정
     * 
     * <p>기존 API Key의 유효기간을 재설정합니다.
     * Key 값은 변경되지 않고 만료일만 연장됩니다.</p>
     * 
     * @param request API Key 유효기간 재설정 요청
     * @return API Key 유효기간 재설정 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public void rescheduleApiKey(IntfOpenApiKeyRescheduleRequest request) {
        try {
            log.info("🟣 iONE Open API Key 유효기간 재설정 요청 - openApiKey: {}, validDays: {}", 
                    request.getOpenApiKey());
            
            InfRequestBody<IntfOpenApiKeyRescheduleRequest> requestBody = InfRequestBody.<IntfOpenApiKeyRescheduleRequest>builder()
                    .data(InfRequestBody.InfReqData.<IntfOpenApiKeyRescheduleRequest>builder()
                            .infWorkUser("ShinHanManager01@shinhan.com")
                            .infWorkData(request)
                            .build())
                    .build();
                    
            InfResponseBody<Void> result = ioneApiKeyClient.rescheduleApiKey(requestBody);
            
            log.info("🟣 iONE Open API Key 유효기간 재설정 성공 - openApiKey: {}, result: {}", 
                    request.getOpenApiKey(), result.getData());
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key 유효기간 재설정 실패 - openApiKey: {}, BusinessException: {}", request.getOpenApiKey(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key 유효기간 재설정 실패 - openApiKey: {}, 예상치 못한 오류", request.getOpenApiKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key 유효기간 재설정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-009] Open API Key scope 추가
     * 
     * <p>기존 API Key에 새로운 scope(권한 범위)를 추가합니다.
     * 기존 scope는 유지하면서 추가 권한을 부여합니다.</p>
     * 
     * @param request API Key scope 추가 요청
     * @return API Key scope 추가 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfOpenApiKeyUpdateResult addScopeToApiKey(IntfOpenApiKeyUpdateRequest request) {
        try {
            log.info("🟣 iONE Open API Key scope 추가 요청 - openApiKey: {}", request.getOpenApiKey());
            
            IntfOpenApiKeyUpdateResult result = ioneApiKeyClient.addScopeToApiKey(request);
            
            log.info("🟣 iONE Open API Key scope 추가 성공 - openApiKey: {}, result: {}", 
                    request.getOpenApiKey(), result.getResultCode());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Open API Key scope 추가 실패 - openApiKey: {}, BusinessException: {}", request.getOpenApiKey(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Open API Key scope 추가 실패 - openApiKey: {}, 예상치 못한 오류", request.getOpenApiKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API Key scope 추가에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-010] (ione portal solution) 포탈용 API 목록
     * 
     * <p>iONE 포탈에서 사용할 수 있는 공개 API 목록을 조회합니다.
     * API Key 발급 시 선택 가능한 API 목록을 제공합니다.</p>
     * 
     * @return 포탈용 API 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfPubApiResult getPortalApis() {
        try {
            log.info("🟣 iONE 포탈용 API 목록 조회 요청");
            
            IntfPubApiResult result = ioneApiKeyClient.getPortalApis();
            
            log.info("🟣 iONE 포탈용 API 목록 조회 성공 - API 개수: {}", 
                    result.getApiList() != null ? result.getApiList().size() : 0);
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 포탈용 API 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 포탈용 API 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "포탈용 API 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * [API-KEY-011] (ione portal solution) 파트너 및 그룹 API 요청 통계 조회
     * 
     * <p>특정 기간 동안의 API 사용 통계를 조회합니다.
     * 연도, 월, 일 단위로 통계 데이터를 제공하며, 특정 API Key나 그룹별 통계를 확인할 수 있습니다.</p>
     * 
     * @param year 조회 연도
     * @param statisticType 통계 유형 (YEAR, MONTH, DAY 등)
     * @param month 조회 월 (선택)
     * @param day 조회 일 (선택)
     * @param statisticKey 통계 키 (API Key 또는 그룹 ID)
     * @return API 요청 통계 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public List<IntfRateLimitStatisticsVo> getStatistics(Integer year, String statisticType, Integer month, 
                                                         Integer day, String statisticKey) {
        try {
            log.info("🟣 iONE API 요청 통계 조회 요청 - year: {}, statisticType: {}, month: {}, day: {}, statisticKey: {}", 
                    year, statisticType, month, day, statisticKey);
            
            List<IntfRateLimitStatisticsVo> result = ioneApiKeyClient.getStatistics(year, statisticType, month, day, statisticKey);
            
            log.info("🟣 iONE API 요청 통계 조회 성공 - year: {}, statisticType: {}, 결과 건수: {}", 
                    year, statisticType, result != null ? result.size() : 0);
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API 요청 통계 조회 실패 - year: {}, statisticType: {}, BusinessException: {}", year, statisticType, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 요청 통계 조회 실패 - year: {}, statisticType: {}, 예상치 못한 오류", year, statisticType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 요청 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
}