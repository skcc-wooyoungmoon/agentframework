package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 통계 유형별 RateLimit 호출 통계 응답 DTO
 * 
 * <p>지정된 통계 유형에 따른 RateLimit 호출 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticTypeRatelimitStatistics {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 통계 유형 (api, apikey, partner, policy)
     */
    private String statisticType;
    
    /**
     * 조회 기간
     */
    private String period;
    
    /**
     * 전체 아이템 수
     */
    private Integer totalItemCount;
    
    /**
     * RateLimit 적용 아이템 수
     */
    private Integer ratelimitedItemCount;
    
    /**
     * 전체 차단 건수
     */
    private Long totalBlockedCalls;
    
    /**
     * 통계 유형별 RateLimit 데이터 목록
     */
    private List<StatisticTypeRatelimitData> statisticData;
    
    /**
     * 통계 유형별 RateLimit 데이터 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticTypeRatelimitData {
        /**
         * 아이템 ID (API ID, API KEY, Partner ID, Policy ID 등)
         */
        private String itemId;
        
        /**
         * 아이템 이름
         */
        private String itemName;
        
        /**
         * 아이템 유형
         */
        private String itemType;
        
        /**
         * 소유자 ID
         */
        private String ownerId;
        
        /**
         * 소유자 이름
         */
        private String ownerName;
        
        /**
         * RateLimit 정책 ID
         */
        private String ratelimitPolicyId;
        
        /**
         * RateLimit 정책 이름
         */
        private String ratelimitPolicyName;
        
        /**
         * 허용 한도 (시간당)
         */
        private Integer hourlyLimit;
        
        /**
         * 허용 한도 (일일)
         */
        private Integer dailyLimit;
        
        /**
         * 총 요청 수
         */
        private Long totalRequests;
        
        /**
         * 허용된 요청 수
         */
        private Long allowedRequests;
        
        /**
         * 차단된 요청 수
         */
        private Long blockedRequests;
        
        /**
         * 차단율 (%)
         */
        private Double blockRate;
        
        /**
         * 시간당 사용률 (%)
         */
        private Double hourlyUsageRate;
        
        /**
         * 일일 사용률 (%)
         */
        private Double dailyUsageRate;
        
        /**
         * 마지막 요청 시간
         */
        private String lastRequestTime;
        
        /**
         * 마지막 차단 시간
         */
        private String lastBlockTime;
        
        /**
         * RateLimit 상태
         */
        private String ratelimitStatus;
        
        /**
         * 일별 통계
         */
        private List<DailyStatisticRatelimitData> dailyStats;
    }
    
    /**
     * 일별 통계 유형 RateLimit 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStatisticRatelimitData {
        /**
         * 날짜
         */
        private String date;
        
        /**
         * 요청 수
         */
        private Long requestCount;
        
        /**
         * 허용 수
         */
        private Long allowedCount;
        
        /**
         * 차단 수
         */
        private Long blockedCount;
        
        /**
         * 사용률 (%)
         */
        private Double usageRate;
        
        /**
         * 최대 동시 요청 수
         */
        private Integer maxConcurrentRequests;
    }
}