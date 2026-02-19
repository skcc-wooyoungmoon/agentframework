package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 통계 조회 결과 DTO
 * 
 * <p>iONE 시스템에서 통계 정보 조회 결과를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResult {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 전체 건수
     */
    private Long totalCount;
    
    /**
     * 통계 데이터 목록
     */
    private List<StatisticsData> data;
    
    /**
     * 통계 데이터 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsData {
        
        /**
         * 날짜/시간
         */
        private String dateTime;
        
        /**
         * API ID
         */
        private String apiId;
        
        /**
         * API 명
         */
        private String apiName;
        
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
         * 총 데이터 크기 (bytes)
         */
        private Long totalDataSize;
    }
}