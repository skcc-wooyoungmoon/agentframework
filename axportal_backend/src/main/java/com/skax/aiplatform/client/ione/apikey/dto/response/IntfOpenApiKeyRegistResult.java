package com.skax.aiplatform.client.ione.apikey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open API Key 등록 결과 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfOpenApiKeyRegistResult {
    
    
    /** 발급된 Open API Key */
    private String openApiKey;

    /** 생성일시 */
    private String createdAt;
}