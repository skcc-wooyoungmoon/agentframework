package com.skax.aiplatform.client.sktai.mcp.dto.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * SKTAI MCP 연결 테스트 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP 연결 테스트 요청")
public class McpTestConnectionRequest {

    @JsonProperty("server_url")
    @Schema(description = "서버 URL", example = "https://echo-server.com")
    private String serverUrl;

    @JsonProperty("auth_type")
    @Schema(description = "인증 타입", example = "none", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType;

    @JsonProperty("auth_config")
    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;

    @JsonProperty("transport_type")
    @Schema(description = "전송 타입", example = "streamable-http", allowableValues = {"streamable-http", "sse"})
    private String transportType;

    // 추가 속성을 위한 Map (Additional properties allowed)
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

} 