package com.skax.aiplatform.client.ione.ratelimit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API KEY 정책 설정 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfApiKeyPolicyConfigRequest {
    
    /** Open API Key */
    private String openApiKey;
    
    /** 정책 ID */
    private String policyId;
    
    /** 작업 구분 (CREATE, UPDATE, DELETE) */
    private String operation;
    
    /** 적용 시작일 */
    private String startDate;
    
    /** 적용 종료일 */
    private String endDate;
    
    /** 사용 여부 */
    private String useYn;
}