package com.skax.aiplatform.dto.agent.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP 카탈로그 생성 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 카탈로그 생성 응답")
public class McpCatalogCreateRes {
    
    @Schema(description = "카탈로그 ID", example = "6e377fa1-b17b-4643-8fe5-1b221eb26c41")
    private String id;
    
    @Schema(description = "카탈로그 이름", example = "deepwiki555")
    private String name;
    
    @Schema(description = "표시 이름", example = "deepwiki1")
    private String displayName;
    
    @Schema(description = "카탈로그 설명", example = "deepwiki1")
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
    
    @Schema(description = "MCP 서빙 ID", example = "181b8ee6-d77e-4d9f-8cb7-698eb4c29b7b")
    private String mcpServingId;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "게이트웨이 엔드포인트")
    private String gatewayEndpoint;
    
    @Schema(description = "태그 목록")
    private List<String> tags;
    
    @Schema(description = "전송 타입", example = "streamable-http")
    private String transportType;
    
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
    
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