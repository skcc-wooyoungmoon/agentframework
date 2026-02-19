package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API KEY RateLimit 호출 통계 응답 DTO
 * 
 * <p>API KEY의 RateLimit 적용 및 호출 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyRatelimitStatistics {
    
    /**
     * 년도
     */
    private String year;
    
    /**
     * 월
     */
    private String month;
    
    /**
     * 일
     */
    private String day;
    
    /**
     * 시간
     */
    private String hour;
    
    /**
     * 분
     */
    private String minute;
    
    /**
     * 총 호출 건수
     */
    private Integer totalCount;
}