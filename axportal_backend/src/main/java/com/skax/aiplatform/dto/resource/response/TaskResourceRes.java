package com.skax.aiplatform.dto.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 배포 리소스 조회 응답 DTO
 *
 * <p>
 * 모델 배포 시 사용 가능한 리소스 정보를 담는 응답 DTO입니다.
 * 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모델 배포 리소스 조회 응답")
public class TaskResourceRes {

    @Schema(description = "노드별 리소스 정보 목록")
    private List<NodeResourceInfo> nodeResource;

    @Schema(description = "네임스페이스 리소스 정보")
    private NamespaceResourceInfo namespaceResource;

    @Schema(description = "태스크 정책 정보")
    private TaskPolicyInfo taskPolicy;

    @Schema(description = "태스크 할당량 정보")
    private TaskQuotaInfo taskQuota;

    /**
     * 노드 리소스 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "노드 리소스 정보")
    public static class NodeResourceInfo {

        @Schema(description = "노드 이름", example = "set3-h06-svr42")
        private String nodeName;

        @Schema(description = "노드 라벨", example = "[\"nodetype=task\", \"gputype=H100\"]")
        private List<String> nodeLabel;

        @Schema(description = "CPU 할당량", example = "96")
        private Double cpuQuota;

        @Schema(description = "메모리 할당량", example = "1007.4")
        private Double memQuota;

        @Schema(description = "GPU 할당량", example = "8")
        private Double gpuQuota;

        @Schema(description = "CPU 사용량", example = "8.4")
        private Double cpuUsed;

        @Schema(description = "메모리 사용량", example = "32.16")
        private Double memUsed;

        @Schema(description = "GPU 사용량", example = "2")
        private Double gpuUsed;

        @Schema(description = "사용 가능한 CPU", example = "87.6")
        private Double cpuUsable;

        @Schema(description = "사용 가능한 메모리", example = "975.24")
        private Double memUsable;

        @Schema(description = "사용 가능한 GPU", example = "6")
        private Double gpuUsable;
    }

    /**
     * 네임스페이스 리소스 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "네임스페이스 리소스 정보")
    public static class NamespaceResourceInfo {

        @Schema(description = "CPU 할당량", example = "4")
        private Double cpuQuota;

        @Schema(description = "메모리 할당량", example = "32")
        private Double memQuota;

        @Schema(description = "GPU 할당량", example = "1")
        private Double gpuQuota;

        @Schema(description = "CPU 사용량", example = "0")
        private Double cpuUsed;

        @Schema(description = "메모리 사용량", example = "0")
        private Double memUsed;

        @Schema(description = "GPU 사용량", example = "0")
        private Double gpuUsed;

        @Schema(description = "사용 가능한 CPU", example = "4")
        private Double cpuUsable;

        @Schema(description = "사용 가능한 메모리", example = "32")
        private Double memUsable;

        @Schema(description = "사용 가능한 GPU", example = "1")
        private Double gpuUsable;
    }

    /**
     * 태스크 정책 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "태스크 정책 정보")
    public static class TaskPolicyInfo {

        @JsonProperty("small")
        @Schema(description = "Small 사이즈 리소스 사양")
        private ResourceSpecInfo small;

        @JsonProperty("medium")
        @Schema(description = "Medium 사이즈 리소스 사양")
        private ResourceSpecInfo medium;

        @JsonProperty("large")
        @Schema(description = "Large 사이즈 리소스 사양")
        private ResourceSpecInfo large;

        @JsonProperty("max")
        @Schema(description = "Max 사이즈 리소스 사양")
        private ResourceSpecInfo max;

        /**
         * 리소스 사양 정보
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "리소스 사양 정보")
        public static class ResourceSpecInfo {

            @Schema(description = "CPU 할당량", example = "4")
            private Double cpuQuota;

            @Schema(description = "메모리 할당량", example = "8")
            private Double memQuota;

            @Schema(description = "GPU 할당량", example = "1")
            private Double gpuQuota;
        }
    }

    /**
     * 태스크 할당량 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "태스크 할당량 정보")
    public static class TaskQuotaInfo {

        @Schema(description = "전체 할당량", example = "2")
        private Integer quota;

        @Schema(description = "사용량", example = "0")
        private Integer used;
    }
}
