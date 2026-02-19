package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API ID별 호출 통계 응답 DTO
 * 
 * <p>각 API ID별로 그룹화된 호출 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiGroupStatistics {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 조회 기간
     */
    private String period;
    
    /**
     * 그룹화 기준
     */
    private String groupBy;
    
    /**
     * 전체 API 수
     */
    private Integer totalApiCount;
    
    /**
     * API 그룹별 통계 목록
     */
    private List<ApiGroupData> apiGroups;
    
    /**
     * API 그룹 데이터 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiGroupData {
        /**
         * API ID
         */
        private String apiId;
        
        /**
         * API 이름
         */
        private String apiName;
        
        /**
         * API 그룹
         */
        private String apiGroup;
        
        /**
         * 총 호출 수
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
         * 성공률 (%)
         */
        private Double successRate;
        
        /**
         * 평균 응답 시간 (ms)
         */
        private Double avgResponseTime;
        
        /**
         * 최대 응답 시간 (ms)
         */
        private Long maxResponseTime;
        
        /**
         * 최소 응답 시간 (ms)
         */
        private Long minResponseTime;
        
        /**
         * 일별 호출 통계
         */
        private List<DailyCallData> dailyStats;
    }
    
    /**
     * 일별 호출 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyCallData {
        /**
         * 날짜
         */
        private String date;
        
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
    }
}