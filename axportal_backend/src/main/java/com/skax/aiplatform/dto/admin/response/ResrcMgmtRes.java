package com.skax.aiplatform.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 포탈 자원 현황 응답 DTO
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포탈 자원 현황 응답")
public class ResrcMgmtRes {
    
    @Schema(description = "에이전트 배포 자원 현황")
    private AgentDeployResources agentDeploy;
    
    @Schema(description = "모델 배포 자원 현황")
    private ModelDeployResources modelDeploy;
    
    @Schema(description = "IDE 자원 현황")
    private IdeResources ideResources;
    
    @Schema(description = "전체 통계")
    private TotalStats totalStats;
    
    /**
     * 에이전트 배포 자원 현황
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "에이전트 배포 자원 현황")
    public static class AgentDeployResources {
        @Schema(description = "CPU 할당량", example = "4 cores")
        private String cpuAllocated;
        
        @Schema(description = "CPU 사용량", example = "2.5 cores")
        private String cpuUsed;
        
        @Schema(description = "Memory 할당량", example = "8GB")
        private String memoryAllocated;
        
        @Schema(description = "Memory 사용량", example = "5.2GB")
        private String memoryUsed;
        
        @Schema(description = "총 배포 수", example = "10")
        private Long totalDeployments;
        
        @Schema(description = "활성 배포 수", example = "7")
        private Long activeDeployments;
        
        @Schema(description = "프로젝트별 통계")
        private List<Map<String, Object>> projectStats;
    }
    
    /**
     * 모델 배포 자원 현황
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "모델 배포 자원 현황")
    public static class ModelDeployResources {
        @Schema(description = "CPU 할당량", example = "8 cores")
        private String cpuAllocated;
        
        @Schema(description = "CPU 사용량", example = "6.1 cores")
        private String cpuUsed;
        
        @Schema(description = "Memory 할당량", example = "16GB")
        private String memoryAllocated;
        
        @Schema(description = "Memory 사용량", example = "12.3GB")
        private String memoryUsed;
        
        @Schema(description = "GPU 할당량", example = "2 GPUs")
        private String gpuAllocated;
        
        @Schema(description = "GPU 사용량", example = "1.8 GPUs")
        private String gpuUsed;
        
        @Schema(description = "총 배포 수", example = "5")
        private Long totalDeployments;
        
        @Schema(description = "활성 배포 수", example = "4")
        private Long activeDeployments;
        
        @Schema(description = "프로젝트별 통계")
        private List<Map<String, Object>> projectStats;
    }
    
    /**
     * IDE 자원 현황
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "IDE 자원 현황")
    public static class IdeResources {
        @Schema(description = "총 IDE 수", example = "15")
        private Long totalIdeCount;
        
        @Schema(description = "활성 IDE 수", example = "12")
        private Long activeIdeCount;
        
        @Schema(description = "중지된 IDE 수", example = "2")
        private Long stoppedIdeCount;
        
        @Schema(description = "오류 IDE 수", example = "1")
        private Long errorIdeCount;
        
        @Schema(description = "IDE 목록")
        private List<Map<String, Object>> ideList;
        
        @Schema(description = "프로젝트별 통계")
        private List<Map<String, Object>> projectStats;
    }
    
    /**
     * 전체 통계
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "전체 통계")
    public static class TotalStats {
        @Schema(description = "총 배포 수 (에이전트 + 모델)", example = "15")
        private Long totalDeployments;
        
        @Schema(description = "총 IDE 인스턴스 수", example = "15")
        private Long totalIdeInstances;
        
        @Schema(description = "총 CPU 할당량", example = "4 cores + 8 cores")
        private String totalCpuAllocated;
        
        @Schema(description = "총 CPU 사용량", example = "2.5 cores + 6.1 cores")
        private String totalCpuUsed;
        
        @Schema(description = "총 Memory 할당량", example = "8GB + 16GB")
        private String totalMemoryAllocated;
        
        @Schema(description = "총 Memory 사용량", example = "5.2GB + 12.3GB")
        private String totalMemoryUsed;
        
        @Schema(description = "총 GPU 할당량", example = "2 GPUs")
        private String totalGpuAllocated;
        
        @Schema(description = "총 GPU 사용량", example = "1.8 GPUs")
        private String totalGpuUsed;
    }
}
