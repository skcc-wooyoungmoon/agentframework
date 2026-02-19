package com.skax.aiplatform.client.sktai.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP 카탈로그 정보 공통 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP 카탈로그 정보")
public class McpCatalogInfo {
    
    @JsonProperty("id")
    @Schema(description = "카탈로그 ID", example = "4ff03486-c04c-4037-96e5-4b3ac4c271c3")
    private String id;
    
    @JsonProperty("name")
    @Schema(description = "카탈로그 이름", example = "adxp_mcp_server")
    private String name;
    
    @JsonProperty("display_name")
    @Schema(description = "표시 이름", example = "⭐️ADXP MCP Server")
    private String displayName;
    
    @JsonProperty("description")
    @Schema(description = "설명", example = "ADXP 플랫폼 mcp 서버(플랫폼에서 배포된 agent 호출)")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "타입", example = "platform")
    private String type;
    
    @JsonProperty("server_url")
    @Schema(description = "서버 URL", example = "https://aip-stg.sktai.io/mcp/")
    private String serverUrl;
    
    @JsonProperty("auth_type")
    @Schema(description = "인증 타입", example = "none")
    private String authType;
    
    @JsonProperty("auth_config")
    @Schema(description = "인증 설정")
    private Object authConfig;
    
    @JsonProperty("enabled")
    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;
    
    @JsonProperty("mcp_serving_id")
    @Schema(description = "MCP 서빙 ID")
    private String mcpServingId;
    
    @JsonProperty("gateway_endpoint")
    @Schema(description = "게이트웨이 엔드포인트")
    private String gatewayEndpoint;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<Tag> tags;
    
    @JsonProperty("transport_type")
    @Schema(description = "전송 타입", example = "streamable-http")
    private String transportType;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-09-15T05:21:29.606174Z")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-09-15T05:21:29.606187Z")
    private String updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "4ff03486-c04c-4037-96e5-4b3ac4c271c3")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "4ff03486-c04c-4037-96e5-4b3ac4c271c3")
    private String updatedBy;
    
    @JsonProperty("tools")
    @Schema(description = "도구 목록")
    private List<McpTool> tools;

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
        @Schema(description = "태그 이름")
        private String name;
    }
} 