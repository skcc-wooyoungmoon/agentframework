package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 연결 테스트 응답 DTO
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MCP 연결 테스트 응답")
public class McpTestConnectionRes {
    @Schema(description = "연결 성공 여부")
    private Boolean isConnected;
    
    @Schema(description = "에러 메시지")
    private String errorMessage;
}