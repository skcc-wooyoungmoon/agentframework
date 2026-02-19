package com.skax.aiplatform.client.sktai.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP Tool 공통 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Tool 정보")
public class McpTool {
    
    @JsonProperty("name")
    @Schema(description = "도구 이름", example = "echo")
    private String name;
    
    @JsonProperty("title")
    @Schema(description = "도구 제목")
    private String title;
    
    @JsonProperty("description")
    @Schema(description = "도구 설명", example = "Echoes back the message provided")
    private String description;
    
    @JsonProperty("inputSchema")
    @Schema(description = "입력 스키마")
    private Object inputSchema;
    
    @JsonProperty("outputSchema")
    @Schema(description = "출력 스키마")
    private Object outputSchema;
    
    @JsonProperty("annotations")
    @Schema(description = "어노테이션")
    private Annotations annotations;
    
    @JsonProperty("_meta")
    @Schema(description = "메타데이터")
    private Object meta;
    
    
    /**
     * 어노테이션
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "어노테이션")
    public static class Annotations {
        
        @JsonProperty("title")
        @Schema(description = "제목")
        private String title;
        
        @JsonProperty("readOnlyHint")
        @Schema(description = "읽기 전용 힌트")
        private String readOnlyHint;
        
        @JsonProperty("destructiveHint")
        @Schema(description = "파괴적 힌트")
        private String destructiveHint;
        
        @JsonProperty("idempotentHint")
        @Schema(description = "멱등성 힌트")
        private String idempotentHint;
        
        @JsonProperty("openWorldHint")
        @Schema(description = "오픈 월드 힌트")
        private String openWorldHint;
        
        @JsonProperty("message")
        @Schema(description = "메시지")
        private Message message;
        
        /**
         * 메시지
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "메시지")
        public static class Message {
            
            @JsonProperty("type")
            @Schema(description = "타입", example = "string")
            private String type;
            
            @JsonProperty("description")
            @Schema(description = "설명", example = "The message to echo back")
            private String description;
        }
    }
} 