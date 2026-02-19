package com.skax.aiplatform.dto.deploy.request;

import com.skax.aiplatform.client.sktai.agent.dto.request.SafetyFilterOptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 앱 생성 요청 DTO
 * 
 * @author gyuheeHwang
 * @since 2025-08-30
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCreateReq {
    
    /**
     * 앱 이름
     */
    private String name;
    
    /**
     * 설명
     */
    private String description;
    
    /**
     * 배포 대상 ID
     */
    private String targetId;
    
    /**
     * 배포 대상 타입
     */
    private String targetType;
    
    /**
     * 서빙 타입
     */
    private String servingType;
    
    /**
     * CPU 제한
     */
    private Integer cpuLimit;
    
    /**
     * CPU 요청
     */
    private Integer cpuRequest;
    
    /**
     * GPU 제한
     */
    private Integer gpuLimit;
    
    /**
     * GPU 요청
     */
    private Integer gpuRequest;
    
    /**
     * 메모리 제한 (GB)
     */
    private Integer memLimit;
    
    /**
     * 메모리 요청 (GB)
     */
    private Integer memRequest;
    
    /**
     * 최대 복제본 수
     */
    private Integer maxReplicas;
    
    /**
     * 최소 복제본 수
     */
    private Integer minReplicas;
    
    /**
     * 코어당 워커 수
     */
    private Integer workersPerCore;
    
    /**
     * 버전 설명
     */
    private String versionDescription;

    /**
     * 안전 필터 옵션
     */
    private SafetyFilterOptions safetyFilterOptions;
}
