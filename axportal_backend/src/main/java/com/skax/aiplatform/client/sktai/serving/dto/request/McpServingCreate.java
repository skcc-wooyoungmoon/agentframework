package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI MCP Serving 생성 요청 DTO
 *
 * <p>SKTAI MCP (Model Context Protocol) Serving을 생성하기 위한 요청 데이터 구조입니다.
 * MCP 서빙은 특별한 형태의 서빙으로 Model Context Protocol을 지원합니다.</p>
 *
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>deployment_name</strong>: 배포 이름</li>
 *   <li><strong>mcp_id</strong>: MCP ID</li>
 * </ul>
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
    description = "SKTAI MCP Serving 생성 요청 정보",
    example = """
        {
          "deployment_name": "mcp-serving-deployment",
          "description": "MCP 서빙 설명",
          "mcp_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        }
        """
)
public class McpServingCreate {

    /**
     * 배포 이름
     *
     * <p>MCP 서빙 배포의 고유한 이름입니다.</p>
     */
    @JsonProperty("deployment_name")
    @Schema(
        description = "배포 이름",
        example = "mcp-serving-deployment",
        required = true
    )
    private String deploymentName;

    /**
     * 서빙 설명
     *
     * <p>MCP 서빙의 목적과 용도를 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "MCP 서빙 설명",
        example = "MCP 서빙 설명"
    )
    private String description;

    /**
     * MCP ID
     *
     * <p>배포할 MCP의 고유 식별자입니다.</p>
     */
    @JsonProperty("mcp_id")
    @Schema(
        description = "MCP ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        required = true,
        format = "uuid"
    )
    private String mcpId;

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

    /**
     * 정책 목록
     */
    @JsonProperty("policy")
    @Schema(description = "정책 목록")
    private List<Object> policy;
}
