package com.skax.aiplatform.client.sktai.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI MCP Catalog Import 응답 DTO
 * 
 * <p>SKTAI MCP 시스템에서 Catalog Import 결과를 담는 응답 데이터 구조입니다.
 * Catalog가 존재하면 검증하고, 존재하지 않으면 생성합니다.</p>
 * 
 * <h3>응답 코드:</h3>
 * <ul>
 *   <li><strong>code: 1</strong>: 검증 성공 또는 생성 성공 (detail: "Validated")</li>
 *   <li><strong>code: -108</strong>: Catalog body와 catalog가 일치하지 않음</li>
 * </ul>
 *
 * @since 2025-11-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI MCP Catalog Import 응답")
public class McpCatalogImportResponse {
    
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드 (1: 성공, -108: 일치하지 않음)", example = "1")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지", example = "Validated")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "Catalog Import 결과 정보")
    private McpCatalogInfo data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
}

