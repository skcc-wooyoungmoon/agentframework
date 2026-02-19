package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Shared Agent Backend 업데이트 요청 DTO
 *
 * <p>기존 Shared Agent Backend의 설정을 업데이트하기 위한 요청 데이터 구조입니다.</p>
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
    description = "SKTAI Shared Agent Backend 업데이트 요청 정보",
    example = """
        {
          "shared_agent_backend_image": "aip-stg-harbor.sktai.io/sktai/agent/shared_app:v1.1.2",
          "cpu_request": 1,
          "cpu_limit": 1,
          "mem_request": 2,
          "mem_limit": 2,
          "min_replicas": 1,
          "max_replicas": 2,
          "autoscaling_class": "kpa.autoscaling.knative.dev",
          "autoscaling_metric": "concurrency",
          "target": 20
        }
        """
)
public class SharedAgentBackendUpdate {

    /**
     * Shared Agent Backend 이미지
     */
    @JsonProperty("shared_agent_backend_image")
    @Schema(
        description = "Shared Agent Backend 이미지",
        example = "aip-stg-harbor.sktai.io/sktai/agent/shared_app:v1.1.2",
        required = true
    )
    private String sharedAgentBackendImage;

    /**
     * CPU 요청량
     */
    @JsonProperty("cpu_request")
    @Schema(
        description = "CPU 요청량",
        example = "1",
        required = true
    )
    private Integer cpuRequest;

    /**
     * CPU 제한량
     */
    @JsonProperty("cpu_limit")
    @Schema(
        description = "CPU 제한량",
        example = "1",
        required = true
    )
    private Integer cpuLimit;

    /**
     * 메모리 요청량
     */
    @JsonProperty("mem_request")
    @Schema(
        description = "메모리 요청량",
        example = "2",
        required = true
    )
    private Integer memRequest;

    /**
     * 메모리 제한량
     */
    @JsonProperty("mem_limit")
    @Schema(
        description = "메모리 제한량",
        example = "2",
        required = true
    )
    private Integer memLimit;

    /**
     * 최소 복제본 수
     */
    @JsonProperty("min_replicas")
    @Schema(
        description = "최소 복제본 수",
        example = "1",
        required = true
    )
    private Integer minReplicas;

    /**
     * 최대 복제본 수
     */
    @JsonProperty("max_replicas")
    @Schema(
        description = "최대 복제본 수",
        example = "2",
        required = true
    )
    private Integer maxReplicas;

    /**
     * 자동 스케일링 클래스
     */
    @JsonProperty("autoscaling_class")
    @Schema(
        description = "자동 스케일링 클래스",
        example = "kpa.autoscaling.knative.dev",
        required = true
    )
    private String autoscalingClass;

    /**
     * 자동 스케일링 메트릭
     */
    @JsonProperty("autoscaling_metric")
    @Schema(
        description = "자동 스케일링 메트릭",
        example = "concurrency",
        required = true
    )
    private String autoscalingMetric;

    /**
     * 타겟 값
     */
    @JsonProperty("target")
    @Schema(
        description = "자동 스케일링 타겟 값",
        example = "20",
        required = true
    )
    private Integer target;
}
