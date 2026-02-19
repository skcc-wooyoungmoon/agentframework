package com.skax.aiplatform.client.lablup.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 아티팩트 업데이트 요청 DTO
 * 
 * <p>기존 아티팩트의 메타데이터나 속성을 업데이트하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateArtifactRequest {
    
    /**
     * 업데이트할 메타데이터
     */
    private ArtifactMetadata metadata;
    
    /**
     * 업데이트할 태그
     */
    private TagUpdate tags;
    
    /**
     * 업데이트할 레이블
     */
    private Map<String, String> labels;
    
    /**
     * 업데이트할 설명
     */
    private String description;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArtifactMetadata {
        /**
         * 제목
         */
        private String title;
        
        /**
         * 설명
         */
        private String description;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 작성자
         */
        private String author;
        
        /**
         * 라이선스
         */
        private String license;
        
        /**
         * 사용자 정의 속성
         */
        private Map<String, Object> customProperties;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagUpdate {
        /**
         * 추가할 태그
         */
        private String[] addTags;
        
        /**
         * 제거할 태그
         */
        private String[] removeTags;
        
        /**
         * 모든 태그 교체 여부
         */
        private boolean replaceAll;
    }
}