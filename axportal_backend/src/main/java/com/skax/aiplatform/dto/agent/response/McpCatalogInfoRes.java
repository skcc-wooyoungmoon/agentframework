package com.skax.aiplatform.dto.agent.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP 카탈로그 정보 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "MCP 카탈로그 정보")
public class McpCatalogInfoRes {
    
    @Schema(description = "카탈로그 ID", example = "4ff03486-c04c-4037-96e5-4b3ac4c271c3")
    private String id;
    
    @Schema(description = "카탈로그 이름", example = "adxp_mcp_server")
    private String name;
    
    @Schema(description = "표시 이름", example = "⭐️ADXP MCP Server")
    private String displayName;
    
    @Schema(description = "설명", example = "ADXP 플랫폼 mcp 서버(플랫폼에서 배포된 agent 호출)")
    private String description;
    
    @Schema(description = "타입", example = "platform")
    private String type;
    
    @Schema(description = "서버 URL", example = "https://aip-stg.sktai.io/mcp/")
    private String serverUrl;
    
    @Schema(description = "인증 타입", example = "none")
    private String authType;
    
    @Schema(description = "인증 설정")
    private Object authConfig;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;
    
    @Schema(description = "MCP 서빙 ID")
    private String mcpServingId;
    
    @Schema(description = "게이트웨이 엔드포인트")
    private String gatewayEndpoint;
    
    @Schema(description = "태그 목록")
    private List<McpTag> tags;
    
    @Schema(description = "전송 타입", example = "streamable-http")
    private String transportType;
    
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @Schema(description = "수정 시간")
    private String updatedAt;

    @Schema(description = "생성자 ID")
    private String createdBy;

    @Schema(description = "수정자 ID")
    private String updatedBy;
    
    @Schema(description = "도구 목록")
    private List<McpTool> tools;
    
    @Schema(description = "공개범위")
    private String publicStatus;
    
    /**
     * MCP 도구 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "MCP 도구 정보")
    public static class McpTool {
        
        @Schema(description = "도구 이름", example = "read_wiki_structure")
        private String name;
        
        @Schema(description = "도구 제목")
        private String title;
        
        @Schema(description = "도구 설명", example = "Get a list of documentation topics for a GitHub repository")
        private String description;
        
        @Schema(description = "입력 스키마")
        private Object inputSchema;
        
        @Schema(description = "출력 스키마")
        private Object outputSchema;
        
        @Schema(description = "어노테이션")
        private Object annotations;
        
        @Schema(description = "메타 정보")
        private Object meta;
    }

    /**
     * MCP 태그 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "MCP 태그 정보")
    public static class McpTag {
        @Schema(description = "태그 이름", example = "test")
        private String name;
    }

} 