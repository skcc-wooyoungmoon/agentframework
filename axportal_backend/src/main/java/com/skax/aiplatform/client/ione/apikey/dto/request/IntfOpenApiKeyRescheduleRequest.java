package com.skax.aiplatform.client.ione.apikey.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open API Key 등록 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "IONE Open API Key 등록 요청",
    example = """
        {
          "scope": "[*]",
          "validForDays": 365,
          "openApiKeyAlias": "MY_API_KEY",
          "partnerId": "PARTNER001",
          "grpId": "GROUP001",
          "replenishIntervalType": "DAILY",
          "allowedCount": 1000
        }
        """
)
public class IntfOpenApiKeyRescheduleRequest {
    
    /*
     * API Key
     */
    @JsonProperty("openApiKey")
    @Schema(
        description = "API Key",
        example = "202501081856019122HEHDKS017641M4VU62Q4T7ZIUEHFM543",
        required = true
    )
    private String openApiKey;

    @JsonProperty("startFrom")
    @Schema(
        description = "시작일",
        example = "2025-01-01 00:00:00",
        required = true
    )
    private String startFrom;


    @JsonProperty("expireAt")
    @Schema(
        description = "만료일",
        example = "2025-01-01 00:00:00",
        required = true
    )
    private String expireAt;
   
}