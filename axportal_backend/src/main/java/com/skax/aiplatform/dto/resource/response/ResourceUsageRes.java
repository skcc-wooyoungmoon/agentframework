package com.skax.aiplatform.dto.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resource Usage Response DTO
 * 
 * <p>리소스 사용량 정보를 담는 내부 응답 DTO입니다.</p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리소스 사용량 정보")
public class ResourceUsageRes {
    
    /**
     * 노드 리소스 정보 목록
     */
    @JsonProperty("node_resource")
    @Schema(description = "노드별 리소스 정보 목록")
    private List<NodeResourceInfo> nodeResource;
    
    /**
     * 클러스터 전체 리소스 정보
     */
    @JsonProperty("cluster_resource")
    @Schema(description = "클러스터 전체 리소스 정보")
    private ClusterResourceInfo clusterResource;
    
    /**
     * 노드 리소스 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "노드 리소스 정보")
    public static class NodeResourceInfo {
        
        /**
         * 노드 이름
         */
        @JsonProperty("node_name")
        @Schema(description = "노드 이름", example = "aix-k8s-g01")
        private String nodeName;
        
        /**
         * 노드 라벨
         */
        @JsonProperty("node_label")
        @Schema(description = "노드 라벨", example = "[\"nodetype=task\", \"gputype=T4\"]")
        private List<String> nodeLabel;
        
        /**
         * CPU 할당량
         */
        @JsonProperty("cpu_quota")
        @Schema(description = "CPU 할당량", example = "16.0")
        private Double cpuQuota;
        
        /**
         * 메모리 할당량
         */
        @JsonProperty("mem_quota")
        @Schema(description = "메모리 할당량 (GB)", example = "107.92")
        private Double memQuota;
        
        /**
         * GPU 할당량
         */
        @JsonProperty("gpu_quota")
        @Schema(description = "GPU 할당량", example = "1.0")
        private Double gpuQuota;
        
        /**
         * CPU 사용량
         */
        @JsonProperty("cpu_used")
        @Schema(description = "CPU 사용량", example = "10.55")
        private Double cpuUsed;
        
        /**
         * 메모리 사용량
         */
        @JsonProperty("mem_used")
        @Schema(description = "메모리 사용량 (GB)", example = "18.58")
        private Double memUsed;
        
        /**
         * GPU 사용량
         */
        @JsonProperty("gpu_used")
        @Schema(description = "GPU 사용량", example = "0.0")
        private Double gpuUsed;
        
        /**
         * CPU 사용 가능량
         */
        @JsonProperty("cpu_usable")
        @Schema(description = "CPU 사용 가능량", example = "5.45")
        private Double cpuUsable;
        
        /**
         * 메모리 사용 가능량
         */
        @JsonProperty("mem_usable")
        @Schema(description = "메모리 사용 가능량 (GB)", example = "89.34")
        private Double memUsable;
        
        /**
         * GPU 사용 가능량
         */
        @JsonProperty("gpu_usable")
        @Schema(description = "GPU 사용 가능량", example = "1.0")
        private Double gpuUsable;
    }
    
    /**
     * 클러스터 리소스 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "클러스터 리소스 정보")
    public static class ClusterResourceInfo {
        
        /**
         * CPU 총량
         */
        @JsonProperty("cpu_total")
        @Schema(description = "CPU 총량", example = "32.0")
        private Double cpuTotal;
        
        /**
         * CPU 사용량
         */
        @JsonProperty("cpu_used")
        @Schema(description = "CPU 사용량", example = "22.8")
        private Double cpuUsed;
        
        /**
         * CPU 사용 가능량
         */
        @JsonProperty("cpu_usable")
        @Schema(description = "CPU 사용 가능량", example = "9.2")
        private Double cpuUsable;
        
        /**
         * 메모리 총량
         */
        @JsonProperty("memory_total")
        @Schema(description = "메모리 총량 (GB)", example = "215.84")
        private Double memoryTotal;
        
        /**
         * 메모리 사용량
         */
        @JsonProperty("memory_used")
        @Schema(description = "메모리 사용량 (GB)", example = "36.48")
        private Double memoryUsed;
        
        /**
         * 메모리 사용 가능량
         */
        @JsonProperty("memory_usable")
        @Schema(description = "메모리 사용 가능량 (GB)", example = "179.36")
        private Double memoryUsable;
        
        /**
         * GPU 총량
         */
        @JsonProperty("gpu_total")
        @Schema(description = "GPU 총량", example = "2.0")
        private Double gpuTotal;
        
        /**
         * GPU 사용량
         */
        @JsonProperty("gpu_used")
        @Schema(description = "GPU 사용량", example = "0.0")
        private Double gpuUsed;
        
        /**
         * GPU 사용 가능량
         */
        @JsonProperty("gpu_usable")
        @Schema(description = "GPU 사용 가능량", example = "2.0")
        private Double gpuUsable;
    }
}
