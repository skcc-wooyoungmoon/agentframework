package com.skax.aiplatform.client.ione.apikey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 요청 통계 정보 VO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfRateLimitStatisticsVo {
    
    /** 파트너 ID */
    private String partnerId;
    
    /** 그룹 ID */
    private String grpId;
    
    /** 통계 날짜 */
    private String statisticDate;
    
    /** 전체 요청 수 */
    private Long totalCount;
    
    /** 성공 요청 수 */
    private Long successCount;
    
    /** 실패 요청 수 */
    private Long failCount;
    
    /** 평균 응답 시간 (ms) */
    private Double avgResponseTime;
}