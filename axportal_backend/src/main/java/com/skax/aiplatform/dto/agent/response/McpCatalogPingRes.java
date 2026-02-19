package com.skax.aiplatform.dto.agent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI MCP Catalog 목록 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Catalog Ping 응답")
public class McpCatalogPingRes {
    @Schema(description = "연결 상태")
    private Boolean isConnected;

    @Schema(description = "에러 메시지")
    private String errorMessage;
} 