package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Node Resource Response DTO
 * 
 * <p>
 * 노드별 리소스 정보를 담는 응답 DTO입니다.
 * 각 노드의 리소스 할당량, 사용량, 사용 가능량을 포함합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "노드 리소스 정보")
public class NodeResource {

    /**
     * 노드 이름
     * 
     * <p>
     * 노드의 고유 식별자입니다.
     * </p>
     */
    @JsonProperty("node_name")
    @Schema(description = "노드 이름", example = "node-1")
    private String nodeName;

    /**
     * 노드 라벨
     * 
     * <p>
     * 노드에 부여된 라벨 목록입니다.
     * </p>
     */
    @JsonProperty("node_label")
    @Schema(description = "노드 라벨", example = "[\"gpu\", \"t4\"]")
    private java.util.List<String> nodeLabel;

    /**
     * CPU 할당량
     * 
     * <p>
     * 노드에 할당된 총 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_quota")
    @Schema(description = "CPU 할당량", example = "96")
    private Integer cpuQuota;

    /**
     * 메모리 할당량 (GB)
     * 
     * <p>
     * 노드에 할당된 총 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_quota")
    @Schema(description = "메모리 할당량 (GB)", example = "1007.4")
    private Double memQuota;

    /**
     * GPU 할당량
     * 
     * <p>
     * 노드에 할당된 총 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_quota")
    @Schema(description = "GPU 할당량", example = "8")
    private Integer gpuQuota;

    /**
     * CPU 사용량
     * 
     * <p>
     * 현재 사용 중인 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_used")
    @Schema(description = "CPU 사용량", example = "45")
    private Integer cpuUsed;

    /**
     * 메모리 사용량 (GB)
     * 
     * <p>
     * 현재 사용 중인 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_used")
    @Schema(description = "메모리 사용량 (GB)", example = "523.2")
    private Double memUsed;

    /**
     * GPU 사용량
     * 
     * <p>
     * 현재 사용 중인 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_used")
    @Schema(description = "GPU 사용량", example = "6")
    private Integer gpuUsed;

    /**
     * CPU 사용 가능량
     * 
     * <p>
     * 추가로 사용 가능한 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_usable")
    @Schema(description = "CPU 사용 가능량", example = "51")
    private Integer cpuUsable;

    /**
     * 메모리 사용 가능량 (GB)
     * 
     * <p>
     * 추가로 사용 가능한 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_usable")
    @Schema(description = "메모리 사용 가능량 (GB)", example = "484.2")
    private Double memUsable;

    /**
     * GPU 사용 가능량
     * 
     * <p>
     * 추가로 사용 가능한 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_usable")
    @Schema(description = "GPU 사용 가능량", example = "2")
    private Integer gpuUsable;
}
