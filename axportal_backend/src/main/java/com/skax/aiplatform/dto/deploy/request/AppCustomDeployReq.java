package com.skax.aiplatform.dto.deploy.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent 커스텀 앱 생성 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCustomDeployReq {
    
    /**
     * 앱 이름 (필수)
     */
    private String name;
    
    /**
     * 앱 설명 (필수)
     */
    private String description;
    
    /**
     * 버전 설명
     */
    private String versionDescription;
    
    /**
     * 타겟 타입 (기본값: "external_graph")
     */
    private String targetType;
    
    /**
     * 모델 목록
     */
    private List<String> modelList;
    
    /**
     * 이미지 URL (기본값: "")
     */
    private String imageUrl;
    
    /**
     * 외부 레지스트리 사용 여부 (기본값: true)
     */
    private Boolean useExternalRegistry;
    
    /**
     * CPU 요청 (기본값: 1)
     */
    private Integer cpuRequest;
    
    /**
     * CPU 제한 (기본값: 1)
     */
    private Integer cpuLimit;
    
    /**
     * 메모리 요청 (GB) (기본값: 2)
     */
    private Integer memRequest;
    
    /**
     * 메모리 제한 (GB) (기본값: 2)
     */
    private Integer memLimit;
    
    /**
     * 최소 복제본 수 (기본값: 1)
     */
    private Integer minReplicas;
    
    /**
     * 최대 복제본 수 (기본값: 1)
     */
    private Integer maxReplicas;
    
    /**
     * 코어당 워커 수 (기본값: 3)
     */
    private Integer workersPerCore;
    
    /**
     * 안전 필터 옵션 (문자열 또는 SafetyFilterOptions 객체)
     */
    private Object safetyFilterOptions;
}

