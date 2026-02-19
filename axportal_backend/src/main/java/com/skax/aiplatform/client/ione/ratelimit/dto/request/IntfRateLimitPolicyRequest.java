package com.skax.aiplatform.client.ione.ratelimit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ratelimit 정책 관리 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfRateLimitPolicyRequest {
    
    /** 정책 ID */
    private String policyId;
    
    /** 정책 이름 */
    private String policyName;
    
    /** 정책 설명 */
    private String description;
    
    /** 요청 제한 수 */
    private Integer limitCount;
    
    /** 시간 단위 (SECOND, MINUTE, HOUR, DAY) */
    private String timeUnit;
    
    /** 시간 값 */
    private Integer timeValue;
    
    /** 사용 여부 */
    private String useYn;
    
    /** 작업 구분 (CREATE, UPDATE, DELETE) */
    private String operation;
}