package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 카탈로그 상세 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 카탈로그 상세 응답")
public class McpCatalogDetailRes {
    
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;
    
    @Schema(description = "응답 코드")
    private Integer code;
    
    @Schema(description = "응답 상세 메시지")
    private String detail;
    
    @Schema(description = "추적 ID")
    private String traceId;
    
    @Schema(description = "카탈로그 상세 정보")
    private McpCatalogInfoRes data;
    
    @Schema(description = "페이로드 정보")
    private Object payload;
} 