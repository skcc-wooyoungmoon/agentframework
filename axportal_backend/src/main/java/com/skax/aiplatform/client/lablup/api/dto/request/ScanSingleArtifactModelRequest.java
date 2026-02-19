package com.skax.aiplatform.client.lablup.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 단일 아티팩트 모델 스캔 요청 DTO
 * 
 * <p>개별 아티팩트 모델에 대한 스캔을 수행하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanSingleArtifactModelRequest {
    
    /**
     * 스캔할 모델의 메타데이터
     */
    private ModelMetadata metadata;
    
    /**
     * 스캔 설정
     */
    private ScanConfiguration configuration;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelMetadata {
        /**
         * 모델 이름
         */
        private String name;
        
        /**
         * 모델 버전
         */
        private String version;
        
        /**
         * 모델 프레임워크
         */
        private String framework;
        
        /**
         * 모델 크기 (바이트)
         */
        private long sizeBytes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScanConfiguration {
        /**
         * 스캔 깊이 레벨 (1-5)
         */
        private int depthLevel;
        
        /**
         * 성능 분석 포함 여부
         */
        private boolean includePerformanceAnalysis;
        
        /**
         * 보안 검사 포함 여부
         */
        private boolean includeSecurityCheck;
    }
}