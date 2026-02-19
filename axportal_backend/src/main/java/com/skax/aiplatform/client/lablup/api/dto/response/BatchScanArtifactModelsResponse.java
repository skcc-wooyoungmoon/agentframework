package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 배치 아티팩트 모델 스캔 응답 DTO
 * 
 * <p>여러 아티팩트 모델에 대한 배치 스캔 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchScanArtifactModelsResponse {
    
    /**
     * 배치 스캔 작업 ID
     */
    private String batchJobId;
    
    /**
     * 전체 작업 상태
     */
    private String status;
    
    /**
     * 작업 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 작업 완료 시간
     */
    private LocalDateTime endTime;
    
    /**
     * 진행률 (0-100)
     */
    private Integer progress;
    
    /**
     * 스캔 결과 목록
     */
    private List<ModelScanResult> scanResults;
    
    /**
     * 배치 스캔 통계
     */
    private BatchScanStatistics statistics;
    
    /**
     * 실패한 스캔 목록
     */
    private List<FailedScan> failures;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelScanResult {
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
        private String scanStatus;
        
        /**
         * 스캔 시작 시간
         */
        private LocalDateTime scanStartTime;
        
        /**
         * 스캔 완료 시간
         */
        private LocalDateTime scanEndTime;
        
        /**
         * 발견된 취약점 수
         */
        private Integer vulnerabilityCount;
        
        /**
         * 심각도별 취약점 수
         */
        private Map<String, Integer> vulnerabilitiesBySeverity;
        
        /**
         * 스캔 점수
         */
        private Double scanScore;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchScanStatistics {
        /**
         * 총 스캔 대상 수
         */
        private Integer totalModels;
        
        /**
         * 성공한 스캔 수
         */
        private Integer successfulScans;
        
        /**
         * 실패한 스캔 수
         */
        private Integer failedScans;
        
        /**
         * 진행 중인 스캔 수
         */
        private Integer inProgressScans;
        
        /**
         * 총 발견된 취약점 수
         */
        private Integer totalVulnerabilities;
        
        /**
         * 평균 스캔 시간 (초)
         */
        private Double averageScanTimeSeconds;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedScan {
        /**
         * 모델 ID
         */
        private String modelId;
        
        /**
         * 모델 이름
         */
        private String modelName;
        
        /**
         * 실패 이유
         */
        private String failureReason;
        
        /**
         * 오류 코드
         */
        private String errorCode;
        
        /**
         * 실패 시간
         */
        private LocalDateTime failedAt;
    }
}