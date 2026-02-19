package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API KEY별 호출 통계 응답 DTO
 * 
 * <p>각 API KEY별로 그룹화된 호출 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyGroupStatistics {
    
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
     * 전체 API KEY 그룹 수
     */
    private Integer totalGroupCount;
    
    /**
     * API KEY 그룹별 통계 목록
     */
    private List<ApiKeyGroupData> keyGroups;
    
    /**
     * API KEY 그룹 데이터 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiKeyGroupData {
        /**
         * API KEY
         */
        private String apiKey;
        
        /**
         * API KEY 그룹
         */
        private String keyGroup;
        
        /**
         * 그룹 이름
         */
        private String groupName;
        
        /**
         * 사용자 ID
         */
        private String userId;
        
        /**
         * 사용자 이름
         */
        private String userName;
        
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
         * 사용된 API 수
         */
        private Integer usedApiCount;
        
        /**
         * 활성 API KEY 수
         */
        private Integer activeKeyCount;
        
        /**
         * 평균 호출 수
         */
        private Double avgCallsPerKey;
        
        /**
         * 최대 호출 수 (단일 KEY)
         */
        private Long maxCallsPerKey;
        
        /**
         * 시간별 호출 통계
         */
        private List<HourlyGroupCallData> hourlyStats;
    }
    
    /**
     * 시간별 그룹 호출 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HourlyGroupCallData {
        /**
         * 시간 (HH:mm)
         */
        private String hour;
        
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
         * 활성 KEY 수
         */
        private Integer activeKeys;
    }
}