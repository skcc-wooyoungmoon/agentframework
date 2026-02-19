package com.skax.aiplatform.client.ione.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.system.IoneSystemClient;
import com.skax.aiplatform.client.ione.system.dto.request.ApiListSearchData;
import com.skax.aiplatform.client.ione.system.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.system.dto.response.ApiListResultWithPagination;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

/**
 * iONE System API 서비스
 * 
 * <p>iONE 시스템 API와의 통신을 담당하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneSystemService {
    
    private final IoneSystemClient ioneSystemClient;
    
    /**
     * API 목록 조회
     * 
     * @param searchData API 목록 검색 조건
     * @return API 목록 결과
     */
    public ApiListResultWithPagination getApiList(ApiListSearchData searchData) {
        try {
            log.info("🟣 iONE System API 목록 조회 요청 - taskId: {}, page: {}, size: {}", 
                    searchData.getTaskId(), searchData.getCurrentPage(), searchData.getPageSize());
            
            ApiListResultWithPagination result = ioneSystemClient.getApiList(searchData);
            
            log.info("🟣 iONE System API 목록 조회 성공 - totalCount: {}", 
                    result.getTotalCount());
            
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE System API 목록 조회 실패 - taskId: {}, BusinessException: {}", 
                    searchData.getTaskId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE System API 목록 조회 실패 - taskId: {}, 예상치 못한 오류", 
                    searchData.getTaskId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE System API 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * API 정보 조회
     * 
     * @param apiId 조회할 API ID
     * @return API 상세 정보
     */
    public ApiInfoResult getApiInfo(String apiId) {
        try {
            log.info("🟣 iONE System API 정보 조회 요청 - apiId: {}", apiId);
            
            InfResponseBody<ApiInfoResult> result = ioneSystemClient.getApiInfo(apiId);
            
            log.info("🟣 iONE System API 정보 조회 성공 - apiId: {}", result.getData());
            
            return result.getData();
        } catch (BusinessException e) {
            log.error("🟣 iONE System API 정보 조회 실패 - apiId: {}, BusinessException: {}", apiId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE System API 정보 조회 실패 - apiId: {}, 예상치 못한 오류", apiId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "iONE System API 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
}