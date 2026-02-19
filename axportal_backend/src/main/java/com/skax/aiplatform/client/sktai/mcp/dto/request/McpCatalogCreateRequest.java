package com.skax.aiplatform.client.sktai.mcp.dto.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI MCP Catalog 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Catalog 생성 요청")
public class McpCatalogCreateRequest {

    @JsonProperty("auth_type")
    @Schema(description = "인증 타입", example = "none", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType;

    @JsonProperty("auth_config")
    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;

    @JsonProperty("description")
    @Schema(description = "카탈로그 설명", example = "echo server")
    private String description;

    @JsonProperty("display_name")
    @Schema(description = "표시 이름", example = "★ 에코 서버")
    private String displayName;

    @JsonProperty("name")
    @Schema(description = "카탈로그 이름", example = "echo server")
    private String name;

    @JsonProperty("server_url")
    @Schema(description = "서버 URL", example = "https://echo-server.com")
    private String serverUrl;

    @JsonProperty("tags")
    @Schema(description = "태그 목록", example = "[{\"name\": \"test\"}, {\"name\": \"echo\"}]")
    private List<Tag> tags;

    @JsonProperty("transport_type")
    @Schema(description = "전송 타입", example = "streamable-http")
    private String transportType;

    @JsonProperty("type")
    @Schema(description = "타입", example = "serverless")
    private String type;

    @JsonProperty("policy")
    @Schema(description = "정책 목록", example = "[{\"scopes\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"], \"policies\": [{\"type\": \"regex\", \"logic\": \"POSITIVE\", \"target_claim\": \"current_group\", \"pattern\": \"^/D2$\"}], \"logic\": \"POSITIVE\", \"decision_strategy\": \"AFFIRMATIVE\", \"cascade\": false}]")
    @Builder.Default
    private List<PolicyRequest> policy = null;

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
        @JsonProperty("name")
        @Schema(description = "태그 이름", example = "test")
        private String name;
    }
} 