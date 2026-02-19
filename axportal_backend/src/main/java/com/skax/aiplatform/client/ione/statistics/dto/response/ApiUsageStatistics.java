package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API 사용량 통계 DTO
 * 
 * <p>iONE 시스템에서 API 사용량 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiUsageStatistics {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 조회 기간 시작일
     */
    private String startDate;
    
    /**
     * 조회 기간 종료일
     */
    private String endDate;
    
    /**
     * 전체 호출 횟수
     */
    private Long totalCalls;
    
    /**
     * 전체 성공 횟수
     */
    private Long totalSuccessCalls;
    
    /**
     * 전체 실패 횟수
     */
    private Long totalFailCalls;
    
    /**
     * 성공률 (%)
     */
    private Double successRate;
    
    /**
     * API별 사용량 통계 목록
     */
    private List<ApiUsageDetail> apiUsageDetails;
    
    /**
     * API별 사용량 상세 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiUsageDetail {
        
        /**
         * API ID
         */
        private String apiId;
        
        /**
         * API 명
         */
        private String apiName;
        
        /**
         * API 설명
         */
        private String apiDescription;
        
        /**
         * 호출 횟수
         */
        private Long callCount;
        
        /**
         * 성공 횟수
         */
        private Long successCount;
        
        /**
         * 실패 횟수
         */
        private Long failCount;
        
        /**
         * 평균 응답시간 (ms)
         */
        private Double avgResponseTime;
        
        /**
         * 최대 응답시간 (ms)
         */
        private Long maxResponseTime;
        
        /**
         * 최소 응답시간 (ms)
         */
        private Long minResponseTime;
        
        /**
         * 데이터 전송량 (bytes)
         */
        private Long dataTransferSize;
    }
}