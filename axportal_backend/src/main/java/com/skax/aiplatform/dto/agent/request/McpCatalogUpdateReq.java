package com.skax.aiplatform.dto.agent.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 카탈로그 수정 요청 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 카탈로그 수정 요청")
public class McpCatalogUpdateReq {
    
    @Schema(description = "카탈로그 이름", example = "echo-server")
    private String name;

    @Schema(description = "표시 이름", example = "★ 에코 서버")
    private String displayName;

    @Schema(description = "카탈로그 설명", example = "echo server")
    private String description;

    @Schema(description = "타입", allowableValues = {"serverless", "self-hosting", "platform"})
    private String type;

    @Schema(description = "서버 URL", example = "https://echo-server.com")
    private String serverUrl;

    @Schema(description = "인증 타입", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType;

    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;    

    @Schema(description = "태그 목록", example = "[{\"name\": \"test\"}, {\"name\": \"echo\"}]")
    private List<Tag> tags;

    @Schema(description = "전송 타입", allowableValues = {"streamable-http", "sse"})
    private String transportType;
    
    @Schema(description = "활성화 여부")
    private Boolean enabled;
    
    // 추가 속성을 위한 Map (Additional properties allowed)
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        // mcpId, id, mcp_id는 경로 변수로 받는 값이므로 additionalProperties에 포함하지 않음
        if (!"mcpId".equals(name) && !"id".equals(name) && !"mcp_id".equals(name)) {
            additionalProperties.put(name, value);
        }
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