package com.skax.aiplatform.dto.deploy.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent 배포 요청 DTO
 * 
 * @author gyuheeHwang
 * @since 2025-08-30
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDeployReq {
    
    /**
     * 앱 ID
     */
    private String appId;
    
    /**
     * 배포명
     */
    private String deploymentName;
    
    /**
     * 설명
     */
    private String description;
    
    /**
     * 환경 설정
     */
    private String environment;
    
    /**
     * 자동 스케일링 여부
     */
    private Boolean autoScaling;
    
    /**
     * 최소 인스턴스 수
     */
    private Integer minInstances;
    
    /**
     * 최대 인스턴스 수
     */
    private Integer maxInstances;
    
    /**
     * CPU 제한
     */
    private String cpuLimit;
    
    /**
     * 메모리 제한
     */
    private String memoryLimit;
    
    /**
     * 환경 변수
     */
    private List<EnvironmentVariable> environmentVariables;
    
    /**
     * 태그
     */
    private List<String> tags;
    
    /**
     * 환경 변수 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvironmentVariable {
        private String key;
        private String value;
        private Boolean secret;
    }
} 