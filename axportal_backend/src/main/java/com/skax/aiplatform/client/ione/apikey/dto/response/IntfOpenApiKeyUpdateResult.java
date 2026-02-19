package com.skax.aiplatform.client.ione.apikey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open API Key 수정 결과 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfOpenApiKeyUpdateResult {
    
    /** 결과 코드 */
    private String resultCode;
    
    /** 결과 메시지 */
    private String resultMessage;
    
    /** Open API Key */
    private String openApiKey;
}