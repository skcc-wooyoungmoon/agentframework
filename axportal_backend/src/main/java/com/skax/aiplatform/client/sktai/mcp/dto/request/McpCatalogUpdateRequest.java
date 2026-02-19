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
import java.util.List;
import java.util.Map;

/**
 * SKTAI MCP Catalog 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Catalog 수정 요청")
public class McpCatalogUpdateRequest {

    // 모든 필드는 업데이트 시 선택적(optional)로 처리됩니다.

    @JsonProperty("name")
    @Schema(description = "카탈로그 이름", example = "echo-server")
    private String name; // string | null

    @JsonProperty("display_name")
    @Schema(description = "표시 이름", example = "★ 에코 서버")
    private String displayName; // string | null

    @JsonProperty("description")
    @Schema(description = "카탈로그 설명", example = "echo server")
    private String description; // string | null

    @JsonProperty("type")
    @Schema(description = "타입", allowableValues = {"serverless", "self-hosting", "platform"})
    private String type; // string | null

    @JsonProperty("server_url")
    @Schema(description = "서버 URL", example = "https://echo-server.com")
    private String serverUrl; // string | null

    @JsonProperty("auth_type")
    @Schema(description = "인증 타입", allowableValues = {"none", "basic", "bearer", "custom-header"})
    private String authType; // string | null

    @JsonProperty("auth_config")
    @Schema(description = "인증 설정 (auth_type에 따라 다른 구조)")
    private Object authConfig;

    @JsonProperty("tags")
    @Schema(description = "태그 목록", example = "[{\"name\": \"test\"}, {\"name\": \"echo\"}]")
    private List<Tag> tags; // array<{name: string}>, default []

    @JsonProperty("transport_type")
    @Schema(description = "전송 타입", allowableValues = {"streamable-http", "sse"})
    private String transportType; // string | null

    @JsonProperty("enabled")
    @Schema(description = "활성화 여부")
    private Boolean enabled; // boolean | null

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