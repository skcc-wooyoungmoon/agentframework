package com.skax.aiplatform.client.ione.ratelimit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API KEY 정책 limit 충전 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfApiKeyPolicyReplenishRequest {
    
    /** Open API Key */
    private String openApiKey;
    
    /** 정책 ID */
    private String policyId;
    
    /** 충전할 limit 수량 */
    private Integer replenishCount;
    
    /** 충전 사유 */
    private String reason;
    
    /** 관리자 ID */
    private String adminId;
}