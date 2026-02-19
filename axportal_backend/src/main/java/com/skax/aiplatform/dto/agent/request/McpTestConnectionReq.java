package com.skax.aiplatform.dto.agent.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP 연결 테스트 요청 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 연결 테스트 요청")
public class McpTestConnectionReq {
    
    @Schema(description = "서버 URL", example = "http://localhost:8080")
    private String serverUrl;
    
    @Schema(description = "인증 타입", example = "none", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType;
    
    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;
    
    @Schema(description = "전송 타입", example = "http", allowableValues = {"streamable-http", "sse"})
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