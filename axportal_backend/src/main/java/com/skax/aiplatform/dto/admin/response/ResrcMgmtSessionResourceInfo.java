package com.skax.aiplatform.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResrcMgmtSessionResourceInfo {
    
    private String sessionId;
    private String modelName;
    private String servingId;
    private String status;
    private String projectId;
    private String projectName;
    
    // CPU 자원 (Core 단위)
    private Double cpuUsage;
    private Double cpuUtilization;
    private Double cpuRequest;
    private Double cpuLimit;
    
    // Memory 자원 (GiB 단위)
    private Double memoryUsage;
    private Double memoryUtilization;
    private Double memoryRequest;
    private Double memoryLimit;
    
    // GPU 자원 (MiB 단위)
    private Double gpuUsage;
    private Double gpuUtilization;
    private Double gpuRequest;
    private Double gpuLimit;
}
