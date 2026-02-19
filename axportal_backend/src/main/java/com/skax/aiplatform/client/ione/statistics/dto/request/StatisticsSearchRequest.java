package com.skax.aiplatform.client.ione.statistics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 통계 조회 요청 DTO
 * 
 * <p>iONE 시스템에서 통계 정보를 조회할 때 사용되는 조건 DTO입니다.
 * 기간, API ID, 그룹 등의 조건을 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsSearchRequest {
    
    /**
     * 조회 시작일 (YYYY-MM-DD)
     */
    private String startDate;
    
    /**
     * 조회 종료일 (YYYY-MM-DD)
     */
    private String endDate;
    
    /**
     * API ID (선택사항)
     */
    private String apiId;
    
    /**
     * API 그룹 ID (선택사항)
     */
    private String apiGroupId;
    
    /**
     * 통계 타입 (hourly, daily, monthly)
     */
    private String statisticsType;
    
    /**
     * 페이지 번호 (1부터 시작)
     */
    private Integer page;
    
    /**
     * 페이지 크기
     */
    private Integer size;
}