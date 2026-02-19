package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API 호출 통계 응답 DTO
 * 
 * <p>API 호출에 대한 전반적인 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCallStatistics {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 전체 호출 수
     */
    private Long totalCalls;
    
    /**
     * 성공 호출 수
     */
    private Long successCalls;
    
    /**
     * 실패 호출 수
     */
    private Long failureCalls;
    
    /**
     * API별 호출 통계 목록
     */
    private List<ApiCallData> apiCallData;
    
    /**
     * API 호출 데이터 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiCallData {
        /**
         * API ID
         */
        private String apiId;
        
        /**
         * API 이름
         */
        private String apiName;
        
        /**
         * 호출 수
         */
        private Long callCount;
        
        /**
         * 성공 수
         */
        private Long successCount;
        
        /**
         * 실패 수
         */
        private Long failureCount;
        
        /**
         * 평균 응답 시간 (ms)
         */
        private Double avgResponseTime;
        
        /**
         * 호출 날짜
         */
        private String callDate;
        
        /**
         * 호출 시간
         */
        private String callTime;
    }
}