package com.skax.aiplatform.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 사용량 관리 API 호출 성공/실패율 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUsageMgmtApiStatsRes {
    
    /**
     * 조회 조건 (month, week, day)
     */
    private String searchType;
    
    /**
     * 기준 년월 (yyyy-MM)
     */
    private String selectedMonth;
    
    /**
     * 선택된 프로젝트
     */
    private String projectType;
    
    /**
     * API 호출 성공 건수
     */
    private Long apiSuccessCount;
    
    /**
     * API 호출 실패 건수
     */
    private Long apiFailureCount;
    
    /**
     * API 호출 총 건수
     */
    private Long totalApiCalls;
    
    /**
     * API 호출 성공률 (%)
     */
    private Double apiSuccessRate;
    
    /**
     * API 호출 실패률 (%)
     */
    private Double apiFailureRate;
    
    /**
     * 통계 조회 일시
     */
    private LocalDateTime statisticsDate;
    
    /**
     * 통계 기간 시작일 (해당월 1일)
     */
    private LocalDateTime periodStartDate;
    
    /**
     * 통계 기간 종료일 (해당월 말일)
     */
    private LocalDateTime periodEndDate;
}
