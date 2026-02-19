package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 아티팩트 검색 응답 DTO
 * 
 * <p>아티팩트 검색 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchArtifactsResponse {
    
    /**
     * 총 검색 결과 수
     */
    private Integer totalCount;
    
    /**
     * 현재 페이지
     */
    private Integer page;
    
    /**
     * 페이지 크기
     */
    private Integer size;
    
    /**
     * 총 페이지 수
     */
    private Integer totalPages;
    
    /**
     * 검색된 아티팩트 목록
     */
    private List<ArtifactSummary> artifacts;
    
    /**
     * 검색 메타데이터
     */
    private SearchMetadata metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArtifactSummary {
        /**
         * 아티팩트 ID
         */
        private String id;
        
        /**
         * 아티팩트 이름
         */
        private String name;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 태그 목록
         */
        private List<String> tags;
        
        /**
         * 생성 시간
         */
        private LocalDateTime createdAt;
        
        /**
         * 수정 시간
         */
        private LocalDateTime updatedAt;
        
        /**
         * 크기 (bytes)
         */
        private Long size;
        
        /**
         * 상태
         */
        private String status;
        
        /**
         * 메타데이터
         */
        private Map<String, Object> metadata;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchMetadata {
        /**
         * 검색 실행 시간 (ms)
         */
        private Long executionTimeMs;
        
        /**
         * 사용된 인덱스 목록
         */
        private List<String> usedIndexes;
        
        /**
         * 검색 타입
         */
        private String searchType;
    }
}