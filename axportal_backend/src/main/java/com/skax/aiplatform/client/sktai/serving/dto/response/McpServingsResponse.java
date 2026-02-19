package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI MCP Servings 목록 응답 DTO
 *
 * <p>MCP Serving 목록 조회 시 반환되는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI MCP Servings 목록 응답",
    example = """
        {
          "data": [
            {
              "mcp_serving_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
              "deployment_name": "mcp-serving-deployment",
              "status": "Ready"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 10
            }
          }
        }
        """
)
public class McpServingsResponse {

    /**
     * MCP Serving 목록 데이터
     */
    @JsonProperty("data")
    @Schema(description = "MCP Serving 목록")
    private List<McpServingInfo> data;

    /**
     * 페이지네이션 정보를 포함한 페이로드
     */
    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private McpServingPayload payload;

    /**
     * MCP Serving 페이로드
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "MCP Serving 페이로드")
    public static class McpServingPayload {
        
        /**
         * 페이지네이션 정보
         */
        @JsonProperty("pagination")
        @Schema(description = "페이지네이션 정보")
        private Pagination pagination;
    }
}
