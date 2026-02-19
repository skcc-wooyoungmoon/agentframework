package com.skax.aiplatform.dto.deploy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "API Key 발급 요청")
public class CreateApiKeyReq {
    // API Key 타입
    @Schema(description = "API Key 타입  : USE(사용자), ETC(외부 시스템)", example = "USE", allowableValues = { "USE", "ETC" })
    private ApiKeyType type;
    
    // 이름(별명)
    @Schema(description = "이름(별명)", example = "김신한")
    private String name;

    // 서빙 종류
    @Schema(description = "범위", example = "agent", allowableValues = { "agent", "model" })
    private String scope;

    @Schema(description = "UUID", example = "jsfdhjhks-jsfdhjhks-jsfdhjhks-jsfdhjhks")
    private String uuid;

    public enum ApiKeyType { 
        USE, // 사용자
        ETC, // 외부 시스템 
    }
}