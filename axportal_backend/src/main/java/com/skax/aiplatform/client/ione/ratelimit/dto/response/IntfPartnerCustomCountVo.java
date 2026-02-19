package com.skax.aiplatform.client.ione.ratelimit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파트너 별 예외 건수 VO
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfPartnerCustomCountVo {
    
    /**
     * 회원(파트너) 식별자(ID)
     */
    private String partnerId;
    
    /**
     * Ratelimit 정책의 식별자(ID)
     */
    private String policyId;
    
    /**
     * 설정된 회원의 정책에 대한 예외 건수
     */
    private Integer allowedCount;
}
