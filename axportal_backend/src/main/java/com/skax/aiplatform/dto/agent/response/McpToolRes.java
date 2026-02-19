package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP Tool 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Tool 정보")
public class McpToolRes {
    
    @Schema(description = "도구 이름", example = "echo")
    private String name;
    
    @Schema(description = "도구 제목")
    private String title;
    
    @Schema(description = "도구 설명", example = "Echoes back the message provided")
    private String description;
    
    @Schema(description = "입력 스키마")
    private Object inputSchema;
    
    @Schema(description = "출력 스키마")
    private Object outputSchema;
    
    @Schema(description = "어노테이션")
    private Object annotations;
    
    @Schema(description = "메타데이터")
    private Object meta;
    
} 