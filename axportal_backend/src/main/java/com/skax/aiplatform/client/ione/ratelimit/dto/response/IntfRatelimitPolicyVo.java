package com.skax.aiplatform.client.ione.ratelimit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ratelimit 정책 VO
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfRatelimitPolicyVo {
    
    /**
     * Ratelimit 정책의 식별자(ID)
     */
    private String policyId;
    
    /**
     * 해당 정책에 설정된 총 요청 한도
     */
    private String policyCount;
    
    /**
     * 같은 정책에 속한 개별 파트너(회원)에게 할당된 요청 한도
     */
    private Integer policyPartnerCount;
    
    /**
     * 정책 전체 허용 건수와 파트너별 허용 건수를 자동으로 리셋하는 주기 유형
     */
    private String replenishIntervalType;
    
    /**
     * 횟수 차감을 반영할 쉼표(',')로 구분된 여러 HTTP 상태 코드 문자열
     */
    private String httpStatus;
    
    /**
     * 쉼표(',')로 구분된 정책에 속한 API 서비스 그룹의 문자열
     */
    private String scope;
}
