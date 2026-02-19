package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task Policy Response DTO
 * 
 * <p>
 * 태스크 정책 정보를 담는 응답 DTO입니다.
 * 각 크기별(small, medium, large, max) 리소스 할당량을 정의합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "태스크 정책 정보")
public class TaskPolicy {

    /**
     * Small 크기 정책
     * 
     * <p>
     * 소규모 태스크에 할당되는 리소스 사양입니다.
     * </p>
     */
    @Schema(description = "Small 크기 정책")
    private ResourceSpec small;

    /**
     * Medium 크기 정책
     * 
     * <p>
     * 중규모 태스크에 할당되는 리소스 사양입니다.
     * </p>
     */
    @Schema(description = "Medium 크기 정책")
    private ResourceSpec medium;

    /**
     * Large 크기 정책
     * 
     * <p>
     * 대규모 태스크에 할당되는 리소스 사양입니다.
     * </p>
     */
    @Schema(description = "Large 크기 정책")
    private ResourceSpec large;

    /**
     * Max 크기 정책
     * 
     * <p>
     * 최대 규모 태스크에 할당되는 리소스 사양입니다.
     * </p>
     */
    @Schema(description = "Max 크기 정책")
    private ResourceSpec max;

    /**
     * Resource Specification
     * 
     * <p>
     * 리소스 사양을 정의하는 내부 클래스입니다.
     * </p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "리소스 사양")
    public static class ResourceSpec {

        /**
         * CPU 할당량
         * 
         * <p>
         * 태스크에 할당될 CPU 코어 수입니다.
         * </p>
         */
        @JsonProperty("cpu_quota")
        @Schema(description = "CPU 할당량", example = "4")
        private Integer cpuQuota;

        /**
         * 메모리 할당량 (GB)
         * 
         * <p>
         * 태스크에 할당될 메모리 크기입니다.
         * </p>
         */
        @JsonProperty("mem_quota")
        @Schema(description = "메모리 할당량 (GB)", example = "8")
        private Integer memQuota;

        /**
         * GPU 할당량
         * 
         * <p>
         * 태스크에 할당될 GPU 개수입니다.
         * </p>
         */
        @JsonProperty("gpu_quota")
        @Schema(description = "GPU 할당량", example = "1")
        private Integer gpuQuota;
    }
}
