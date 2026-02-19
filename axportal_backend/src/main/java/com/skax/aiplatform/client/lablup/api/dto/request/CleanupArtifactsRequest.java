package com.skax.aiplatform.client.lablup.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 아티팩트 정리 요청 DTO
 * 
 * <p>저장소의 아티팩트를 정리하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CleanupArtifactsRequest {
    
    /**
     * 정리 정책
     */
    private CleanupPolicy policy;
    
    /**
     * 필터 조건
     */
    private CleanupFilter filter;
    
    /**
     * 실행 옵션
     */
    private CleanupOptions options;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupPolicy {
        /**
         * 정리 타입 (by_age, by_count, by_size 등)
         */
        private String type;
        
        /**
         * 보존 기간 (일)
         */
        private Integer retentionDays;
        
        /**
         * 최대 보존 개수
         */
        private Integer maxCount;
        
        /**
         * 최대 크기 (bytes)
         */
        private Long maxSize;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupFilter {
        /**
         * 대상 레지스트리 목록
         */
        private List<String> registries;
        
        /**
         * 대상 네임스페이스 목록
         */
        private List<String> namespaces;
        
        /**
         * 포함할 태그 패턴
         */
        private List<String> includeTags;
        
        /**
         * 제외할 태그 패턴
         */
        private List<String> excludeTags;
        
        /**
         * 시작 날짜
         */
        private LocalDateTime fromDate;
        
        /**
         * 종료 날짜
         */
        private LocalDateTime toDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupOptions {
        /**
         * 드라이런 모드 (실제 삭제하지 않고 대상만 확인)
         */
        private boolean dryRun;
        
        /**
         * 병렬 처리 여부
         */
        private boolean parallel;
        
        /**
         * 진행률 알림 여부
         */
        private boolean notifyProgress;
        
        /**
         * 백업 생성 여부
         */
        private boolean createBackup;
    }
}