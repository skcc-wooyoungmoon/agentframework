package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 아티팩트 정리 응답 DTO
 * 
 * <p>아티팩트 정리 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CleanupArtifactsResponse {
    
    /**
     * 정리 작업 ID
     */
    private String jobId;
    
    /**
     * 작업 상태
     */
    private String status;
    
    /**
     * 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 완료 시간
     */
    private LocalDateTime endTime;
    
    /**
     * 정리된 아티팩트 목록
     */
    private List<CleanedArtifact> cleanedArtifacts;
    
    /**
     * 정리 통계
     */
    private CleanupStatistics statistics;
    
    /**
     * 드라이런 결과 (드라이런 모드인 경우)
     */
    private List<CleanupCandidate> dryRunResults;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanedArtifact {
        /**
         * 아티팩트 ID
         */
        private String artifactId;
        
        /**
         * 아티팩트 이름
         */
        private String name;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 크기 (bytes)
         */
        private Long size;
        
        /**
         * 정리 시간
         */
        private LocalDateTime cleanedAt;
        
        /**
         * 정리 이유
         */
        private String reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupStatistics {
        /**
         * 총 검토 대상 수
         */
        private Integer totalCandidates;
        
        /**
         * 정리된 수
         */
        private Integer cleaned;
        
        /**
         * 보존된 수
         */
        private Integer preserved;
        
        /**
         * 절약된 공간 (bytes)
         */
        private Long spaceSaved;
        
        /**
         * 실행 시간 (초)
         */
        private Long executionTimeSeconds;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupCandidate {
        /**
         * 아티팩트 ID
         */
        private String artifactId;
        
        /**
         * 아티팩트 이름
         */
        private String name;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 크기 (bytes)
         */
        private Long size;
        
        /**
         * 정리 예정 이유
         */
        private String reason;
        
        /**
         * 마지막 사용 시간
         */
        private LocalDateTime lastUsed;
    }
}