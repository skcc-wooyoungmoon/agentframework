package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 단일 아티팩트 모델 스캔 응답 DTO
 * 
 * <p>단일 아티팩트 모델에 대한 스캔 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanSingleArtifactModelResponse {
    
    /**
     * 스캔 작업 ID
     */
    private String scanId;
    
    /**
     * 모델 ID
     */
    private String modelId;
    
    /**
     * 모델 이름
     */
    private String modelName;
    
    /**
     * 스캔 상태
     */
    private String status;
    
    /**
     * 스캔 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 스캔 완료 시간
     */
    private LocalDateTime endTime;
    
    /**
     * 스캔 결과 요약
     */
    private ModelScanSummary summary;
    
    /**
     * 발견된 취약점 목록
     */
    private List<ModelVulnerability> vulnerabilities;
    
    /**
     * 모델 분석 정보
     */
    private ModelAnalysis analysis;
    
    /**
     * 스캔 메타데이터
     */
    private Map<String, Object> metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelScanSummary {
        /**
         * 총 스캔된 컴포넌트 수
         */
        private Integer totalComponents;
        
        /**
         * 발견된 취약점 수
         */
        private Integer vulnerabilityCount;
        
        /**
         * 심각도별 취약점 수
         */
        private Map<String, Integer> vulnerabilitiesBySeverity;
        
        /**
         * 스캔 점수 (0-100)
         */
        private Double scanScore;
        
        /**
         * 보안 등급
         */
        private String securityGrade;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelVulnerability {
        /**
         * 취약점 ID
         */
        private String id;
        
        /**
         * CVE ID
         */
        private String cveId;
        
        /**
         * 취약점 이름
         */
        private String name;
        
        /**
         * 심각도
         */
        private String severity;
        
        /**
         * CVSS 점수
         */
        private Double cvssScore;
        
        /**
         * 설명
         */
        private String description;
        
        /**
         * 영향받는 컴포넌트
         */
        private String affectedComponent;
        
        /**
         * 수정 권장사항
         */
        private String fixRecommendation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelAnalysis {
        /**
         * 모델 타입
         */
        private String modelType;
        
        /**
         * 모델 크기 (bytes)
         */
        private Long modelSize;
        
        /**
         * 프레임워크
         */
        private String framework;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 의존성 목록
         */
        private List<String> dependencies;
        
        /**
         * 라이선스 정보
         */
        private String license;
    }
}