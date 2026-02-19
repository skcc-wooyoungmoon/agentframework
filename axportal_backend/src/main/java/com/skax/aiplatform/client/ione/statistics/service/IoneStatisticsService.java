package com.skax.aiplatform.client.ione.statistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.statistics.IoneStatisticsClient;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiGroupStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiKeyGroupStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiKeyRatelimitStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.StatisticTypeRatelimitStatistics;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * iONE 통계정보 조회 서비스
 * 
 * <p>iONE 통계정보 조회 API와의 통신을 담당하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * <h3>제공하는 통계 API:</h3>
 * <ul>
 *   <li>API 호출 통계</li>
 *   <li>API ID별 호출 통계</li>
 *   <li>API KEY 호출 통계</li>
 *   <li>API KEY별 호출 통계</li>
 *   <li>API KEY RateLimit 호출 통계</li>
 *   <li>통계 유형별 RateLimit 호출 통계</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneStatisticsService {
    
    private final IoneStatisticsClient ioneStatisticsClient;
    
    /**
     * [API-STS-001] API 호출 통계 조회
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiId API ID (선택사항)
     * @return API 호출 통계
     */
    public List<ApiStatistics> getApiCallStatistics(String fromDtm, String toDtm, String apiId, String groupType) {
        try {
            log.info("🟣 iONE API 호출 통계 조회 요청 - startDate: {}, endDate: {}, apiId: {}", 
            fromDtm, toDtm, apiId);
            
            InfResponseBody<List<ApiStatistics>> result = ioneStatisticsClient.getApiCallStatistics(fromDtm, toDtm, groupType, apiId);
            
            log.info("🟣 iONE API 호출 통계 조회 성공 - 총 호출수: {}", result.getData().size());
            
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API 호출 통계 조회 실패 - startDate: {}, endDate: {}, apiId: {}, BusinessException: {}", 
            fromDtm, toDtm, apiId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API 호출 통계 조회 실패 - startDate: {}, endDate: {}, apiId: {}, 예상치 못한 오류", 
            fromDtm, toDtm, apiId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-STS-002] API ID별 호출 통계 조회
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param groupBy 그룹화 기준 (hour, day, month)
     * @return API ID별 호출 통계
     */
    public ApiGroupStatistics getApiGroupStatistics(String startDate, String endDate, String groupBy) {
        try {
            log.info("🟣 iONE API ID별 호출 통계 조회 요청 - startDate: {}, endDate: {}, groupBy: {}", 
                    startDate, endDate, groupBy);
            
            ApiGroupStatistics result = ioneStatisticsClient.getApiGroupStatistics(startDate, endDate, groupBy);
            
            log.info("🟣 iONE API ID별 호출 통계 조회 성공 - 총 API 수: {}", result.getTotalApiCount());
            
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API ID별 호출 통계 조회 실패 - startDate: {}, endDate: {}, groupBy: {}, BusinessException: {}", 
                    startDate, endDate, groupBy, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API ID별 호출 통계 조회 실패 - startDate: {}, endDate: {}, groupBy: {}, 예상치 못한 오류", 
                    startDate, endDate, groupBy, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API ID별 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-STS-003] API KEY 호출 통계 조회
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiKey API KEY (선택사항)
     * @return API KEY 호출 통계
     */
    public List<ApiStatistics> getApiKeyStatistics(String fromDtm, String toDtm, String groupType, String apiKey) {
        try {
            log.info("🟣 iONE API KEY 호출 통계 조회 요청 - fromDtm: {}, toDtm: {},groupType: {}, apiKey: {}", 
                    fromDtm, toDtm, groupType, apiKey != null ? "***" : null);
            
            InfResponseBody<List<ApiStatistics>> result = ioneStatisticsClient.getApiKeyStatistics(fromDtm, toDtm, groupType, apiKey);
            
            log.info("🟣 iONE API KEY 호출 통계 조회 성공 - 총 API KEY 수: {}", result.getData().size());
            
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API KEY 호출 통계 조회 실패 - fromDtm: {}, toDtm: {}, BusinessException: {}", 
                    fromDtm, toDtm, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API KEY 호출 통계 조회 실패 - fromDtm: {}, toDtm: {}, 예상치 못한 오류", 
                    fromDtm, toDtm, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API KEY 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-STS-004] API KEY별 호출 통계 조회
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param groupBy 그룹화 기준 (hour, day, month)
     * @return API KEY별 호출 통계
     */
      public InfResponseBody<ApiKeyGroupStatistics> getApiKeyGroupStatistics(String fromDtm, String toDtm, String groupType, String apiKey) {
        try {
            log.info("🟣 iONE API KEY별 호출 통계 조회 요청 - fromDtm: {}, toDtm: {}, groupType: {}, apiKey: {}", 
                    fromDtm, toDtm, groupType, apiKey != null ? "***" : null);
            
            InfResponseBody<ApiKeyGroupStatistics> result = ioneStatisticsClient.getApiKeyGroupStatistics(fromDtm, toDtm, groupType, apiKey);
            
            log.info("🟣 iONE API KEY별 호출 통계 조회 성공 - 총 그룹 수: {}", result.getData().getTotalGroupCount());
            
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API KEY별 호출 통계 조회 실패 - fromDtm: {}, toDtm: {}, groupType: {}, BusinessException: {}", 
                    fromDtm, toDtm, groupType, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API KEY별 호출 통계 조회 실패 - fromDtm: {}, toDtm: {}, groupType: {}, 예상치 못한 오류", 
                    fromDtm, toDtm, groupType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API KEY별 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-STS-005] API KEY RateLimit 호출 통계 조회
     * 
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param apiKey API KEY (선택사항)
     * @return API KEY RateLimit 호출 통계
     */
    public List<ApiKeyRatelimitStatistics> getApiKeyRatelimitStatistics(String startDate, String endDate, String apiKey) {
        try {
            log.info("🟣 iONE API KEY RateLimit 호출 통계 조회 요청 - startDate: {}, endDate: {}, apiKey: {}", 
                    startDate, endDate, apiKey != null ? "***" : null);
            
            InfResponseBody<List<ApiKeyRatelimitStatistics>> result = ioneStatisticsClient.getApiKeyRatelimitStatistics(startDate, endDate, apiKey);
            
            log.info("🟣 iONE API KEY RateLimit 호출 통계 조회 성공 - {}", result.getData());
            
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE API KEY RateLimit 호출 통계 조회 실패 - startDate: {}, endDate: {}, BusinessException: {}", 
                    startDate, endDate, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API KEY RateLimit 호출 통계 조회 실패 - startDate: {}, endDate: {}, 예상치 못한 오류", 
                    startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE API KEY RateLimit 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-STS-006] 통계 유형별 RateLimit 호출 통계 조회
     * 
     * @param statisticType 통계 유형 (api, apikey, partner, policy)
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param filter 필터 조건 (선택사항)
     * @return 통계 유형별 RateLimit 호출 통계
     */
    public StatisticTypeRatelimitStatistics getStatisticTypeRatelimitStatistics(String statisticType, String startDate, String endDate, String filter) {
        try {
            log.info("🟣 iONE 통계 유형별 RateLimit 호출 통계 조회 요청 - statisticType: {}, startDate: {}, endDate: {}, filter: {}", 
                    statisticType, startDate, endDate, filter);
            
            StatisticTypeRatelimitStatistics result = ioneStatisticsClient.getStatisticTypeRatelimitStatistics(
                    statisticType, startDate, endDate, filter);
            
            log.info("🟣 iONE 통계 유형별 RateLimit 호출 통계 조회 성공 - 통계 유형: {}, 총 아이템 수: {}, RateLimit 적용 아이템 수: {}, 총 차단 건수: {}", 
                    result.getStatisticType(), result.getTotalItemCount(), result.getRatelimitedItemCount(), result.getTotalBlockedCalls());
            
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE 통계 유형별 RateLimit 호출 통계 조회 실패 - statisticType: {}, startDate: {}, endDate: {}, BusinessException: {}", 
                    statisticType, startDate, endDate, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE 통계 유형별 RateLimit 호출 통계 조회 실패 - statisticType: {}, startDate: {}, endDate: {}, 예상치 못한 오류", 
                    statisticType, startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE 통계 유형별 RateLimit 호출 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
}