package com.skax.aiplatform.dto.agent.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP 카탈로그 수정 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 카탈로그 수정 응답")
public class McpCatalogUpdateRes {
    
    @Schema(description = "카탈로그 ID", example = "b7c740a4-192c-400c-be2f-7e09d5a23fd9")
    private String id;
    
    @Schema(description = "카탈로그 이름", example = "echo test server")
    private String name;
    
    @Schema(description = "표시 이름", example = "deepwiki1")
    private String displayName;
    
    @Schema(description = "카탈로그 설명", example = "echo test server")
    private String description;
    
    @Schema(description = "타입", example = "serverless")
    private String type;
    
    @Schema(description = "서버 URL", example = "https://server.smithery.ai/deepwiki/mcp?api_key=3a6ea244-9f0b-49e5-96b2-28af76515c45")
    private String serverUrl;
    
    @Schema(description = "인증 타입", example = "none")
    private String authType;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "인증 설정")
    private Object authConfig;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;
    
    @Schema(description = "MCP 서빙 ID", example = "142e5d36-0e17-4d20-94b1-efb154a781a6")
    private String mcpServingId;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "게이트웨이 엔드포인트")
    private String gatewayEndpoint;
    
    @Schema(description = "태그 목록")
    private List<String> tags;
    
    @Schema(description = "전송 타입", example = "streamable-http")
    private String transportType;
    
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @Schema(description = "수정 시간")
    private String updatedAt;
    
    @Schema(description = "도구 목록")
    private List<McpTool> tools;
    
    /**
     * MCP 도구 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
}
