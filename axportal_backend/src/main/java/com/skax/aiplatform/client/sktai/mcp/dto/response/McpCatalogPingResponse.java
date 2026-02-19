package com.skax.aiplatform.client.sktai.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI MCP Catalog Ping 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MCP Catalog Ping 응답")
public class McpCatalogPingResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "Ping 정보")
    private McpCatalogPing data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Ping 정보")
    public static class McpCatalogPing {
        @JsonProperty("is_connected")
        @Schema(description = "연결 상태")
        private Boolean isConnected;

        @JsonProperty("error_message")
        @Schema(description = "에러 메시지")
        private String errorMessage;
    }
} 