package com.skax.aiplatform.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 사용자 사용량 관리 통계 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUsageMgmtStatsRes {
    
    /**
     * 조회 조건 (month, week, day)
     */
    private String searchType;
    
    /**
     * 선택된 날짜 (month: yyyy-MM, week/day: yyyy-MM-dd)
     */
    private String selectedDate;
    
    /**
     * 선택된 프로젝트
     */
    private String projectType;
    
    /**
     * 월별 로그인 성공 건수 목록 (1년간 데이터)
     */
    private List<Map<String, Object>> loginSuccessCounts;
    
    /**
     * 1년간 전체 로그인 성공 건수
     */
    private Long totalLoginSuccessCount;
    
    /**
     * API 호출 성공 건수 (해당월)
     */
    private Long apiSuccessCount;
    
    /**
     * API 호출 실패 건수 (해당월)
     */
    private Long apiFailureCount;
    
    /**
     * 총 API 호출 건수 (해당월)
     */
    private Long totalApiCalls;
    
    /**
     * API 호출 성공률 (해당월)
     */
    private Double apiSuccessRate;
    
    /**
     * API 호출 실패률 (해당월)
     */
    private Double apiFailureRate;
    
    /**
     * API 호출 실패 요약 데이터 (최신순 6개)
     */
    private List<Map<String, Object>> apiFailureSummary;
    
    /**
     * 가장 많이 사용한 메뉴 (상위 5개)
     */
    private List<Map<String, Object>> topUsedMenus;
    
    /**
     * 통계 조회 일시
     */
    private LocalDateTime statisticsDate;
    
    /**
     * 통계 기간 시작일 (1년 전)
     */
    private LocalDateTime periodStartDate;
    
    /**
     * 통계 기간 종료일 (선택된 월)
     */
    private LocalDateTime periodEndDate;
}
