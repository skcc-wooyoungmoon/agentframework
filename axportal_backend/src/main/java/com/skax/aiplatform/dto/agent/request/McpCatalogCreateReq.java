package com.skax.aiplatform.dto.agent.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP 카탈로그 생성 요청 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 카탈로그 생성 요청")
public class McpCatalogCreateReq {
    
    @Schema(description = "인증 타입", example = "none", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType;
    
    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;
    
    @Schema(description = "카탈로그 설명", example = "MCP 카탈로그 설명")
    private String description;
    
    @Schema(description = "표시 이름", example = "My MCP Catalog")
    private String displayName;
    
    @Schema(description = "카탈로그 이름", example = "my-mcp-catalog")
    private String name;
    
    @Schema(description = "서버 URL", example = "http://localhost:8080")
    private String serverUrl;
    
    @Schema(description = "태그 목록", example = "[{\"name\": \"test\"}, {\"name\": \"echo\"}]")
    private List<Tag> tags;
    
    @Schema(description = "전송 타입", example = "http")
    private String transportType;
    
    @Schema(description = "카탈로그 타입", example = "mcp")
    private String type;
    
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

    /**
     * 태그 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "태그 정보")
    public static class Tag {
        @Schema(description = "태그 이름", example = "test")
        private String name;
    }
}
