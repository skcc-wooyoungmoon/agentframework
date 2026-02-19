package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI MCP Serving 응답 DTO
 *
 * <p>MCP Serving 생성/수정 시 반환되는 응답 데이터 구조입니다.</p>
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
    description = "SKTAI MCP Serving 응답 정보",
    example = """
        {
          "mcp_serving_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "deployment_name": "mcp-serving-deployment",
          "status": "Ready"
        }
        """
)
public class McpServingResponse {

    /**
     * MCP Serving ID
     */
    @JsonProperty("mcp_serving_id")
    @Schema(description = "MCP Serving ID", format = "uuid")
    private String mcpServingId;

    /**
     * 배포 이름
     */
    @JsonProperty("deployment_name")
    @Schema(description = "배포 이름")
    private String deploymentName;

    /**
     * ISVC 이름
     */
    @JsonProperty("isvc_name")
    @Schema(description = "ISVC 이름")
    private String isvcName;

    /**
     * 설명
     */
    @JsonProperty("description")
    @Schema(description = "MCP 서빙 설명")
    private String description;

    /**
     * KServe YAML
     */
    @JsonProperty("kserve_yaml")
    @Schema(description = "KServe YAML 설정")
    private String kserveYaml;

    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    /**
     * 네임스페이스
     */
    @JsonProperty("namespace")
    @Schema(description = "네임스페이스")
    private String namespace;

    /**
     * MCP ID
     */
    @JsonProperty("mcp_id")
    @Schema(description = "MCP ID", format = "uuid")
    private String mcpId;

    /**
     * 상태
     */
    @JsonProperty("status")
    @Schema(description = "서빙 상태")
    private String status;

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
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    /**
     * 삭제 여부
     */
    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", format = "date-time")
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간", format = "date-time")
    private LocalDateTime updatedAt;
}
