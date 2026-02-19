package com.skax.aiplatform.client.ione.apikey.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open API Key 갱신 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfOpenApiKeyRenewRequest {
    
    /** Open API Key */
    private String openApiKey;
    
    /** 갱신할 유효 기간 (일 단위) */
    private Integer validDays;
}