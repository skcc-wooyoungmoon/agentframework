package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Namespace Resource Response DTO
 * 
 * <p>
 * 네임스페이스별 리소스 정보를 담는 응답 DTO입니다.
 * 네임스페이스의 리소스 할당량, 사용량, 사용 가능량을 포함합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "네임스페이스 리소스 정보")
public class NamespaceResource {

    /**
     * CPU 할당량
     * 
     * <p>
     * 네임스페이스에 할당된 총 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_quota")
    @Schema(description = "CPU 할당량", example = "4")
    private Integer cpuQuota;

    /**
     * 메모리 할당량 (GB)
     * 
     * <p>
     * 네임스페이스에 할당된 총 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_quota")
    @Schema(description = "메모리 할당량 (GB)", example = "32")
    private Integer memQuota;

    /**
     * GPU 할당량
     * 
     * <p>
     * 네임스페이스에 할당된 총 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_quota")
    @Schema(description = "GPU 할당량", example = "1")
    private Integer gpuQuota;

    /**
     * CPU 사용량
     * 
     * <p>
     * 현재 사용 중인 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_used")
    @Schema(description = "CPU 사용량", example = "0")
    private Integer cpuUsed;

    /**
     * 메모리 사용량 (GB)
     * 
     * <p>
     * 현재 사용 중인 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_used")
    @Schema(description = "메모리 사용량 (GB)", example = "0")
    private Integer memUsed;

    /**
     * GPU 사용량
     * 
     * <p>
     * 현재 사용 중인 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_used")
    @Schema(description = "GPU 사용량", example = "0")
    private Integer gpuUsed;

    /**
     * CPU 사용 가능량
     * 
     * <p>
     * 추가로 사용 가능한 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_usable")
    @Schema(description = "CPU 사용 가능량", example = "4")
    private Integer cpuUsable;

    /**
     * 메모리 사용 가능량 (GB)
     * 
     * <p>
     * 추가로 사용 가능한 메모리 크기입니다.
     * </p>
     */
    @JsonProperty("mem_usable")
    @Schema(description = "메모리 사용 가능량 (GB)", example = "32")
    private Integer memUsable;

    /**
     * GPU 사용 가능량
     * 
     * <p>
     * 추가로 사용 가능한 GPU 개수입니다.
     * </p>
     */
    @JsonProperty("gpu_usable")
    @Schema(description = "GPU 사용 가능량", example = "1")
    private Integer gpuUsable;
}
