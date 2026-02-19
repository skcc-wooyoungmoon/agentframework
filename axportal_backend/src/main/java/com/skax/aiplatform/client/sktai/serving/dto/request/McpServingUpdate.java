package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI MCP Serving 업데이트 요청 DTO
 *
 * <p>기존 MCP Serving의 설정을 업데이트하기 위한 요청 데이터 구조입니다.</p>
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
    description = "SKTAI MCP Serving 업데이트 요청 정보",
    example = """
        {
          "description": "업데이트된 MCP 서빙 설명",
          "cpu_request": 2,
          "mem_limit": 4096
        }
        """
)
public class McpServingUpdate {

    /**
     * 서빙 설명
     */
    @JsonProperty("description")
    @Schema(description = "MCP 서빙 설명")
    private String description;

    /**
     * CPU 요청량
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량")
    private Integer cpuRequest;

    /**
     * CPU 제한량
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량")
    private Integer cpuLimit;

    /**
     * GPU 요청량
     */
    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량")
    private Integer gpuRequest;

    /**
     * GPU 제한량
     */
    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량")
    private Integer gpuLimit;

    /**
     * 메모리 요청량
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량")
    private Integer memRequest;

    /**
     * 메모리 제한량
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량")
    private Integer memLimit;

    /**
     * GPU 타입
     */
    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입")
    private String gpuType;
}
