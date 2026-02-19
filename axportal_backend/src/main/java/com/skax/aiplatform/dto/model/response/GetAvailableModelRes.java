package com.skax.aiplatform.dto.model.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 가능 모델 조회 요청")
public class GetAvailableModelRes {
    /**
     * 아티팩트 목록
     */
    private List<Artifact> artifacts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Artifact {
        /**
         * 아티팩트 ID (UUID)
         */
        private String id;
        
        /**
         * 아티팩트 이름
         */
        private String name;
        
        /**
         * 아티팩트 유형 (MODEL, PACKAGE, IMAGE)
         */
        private String type;
        
        /**
         * 아티팩트 설명 (허깅페이스의 경우 None)
         */
        private String description;
        
        /**
         * 레지스트리 ID (UUID)
         */
        private String registryId;
        
        /**
         * 원본 레지스트리 ID (UUID)
         */
        private String sourceRegistryId;
        
        /**
         * 레지스트리 유형 (huggingface, reservoir)
         */
        private String registryType;
        
        /**
         * 원본 레지스트리 유형
         */
        private String sourceRegistryType;
        
        /**
         * 스캔 일시
         */
        private LocalDateTime scannedAt;
        
        /**
         * 업데이트 일시
         */
        private LocalDateTime updatedAt;
        
        /**
         * 읽기 전용 여부
         */
        private Boolean readonly;
        

        private String revision_id;
        
        /**
         * 버전
         */
        private String version;
        
        /**
         * 크기 (바이트)
         */
        private Long size;
        
        /**
         * 상태 (SCANNED, PULLING, PULLED, etc.)
         */
        private String status;
        
        /**
         * 생성 일시
         */
        private LocalDateTime revisionCreatedAt;
        
        /**
         * 업데이트 일시
         */
        private LocalDateTime revisionUpdatedAt;
    }


    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class Revision {
    //     /**
    //      * 리비전 ID (UUID)
    //      */
    //     private String id;
        
    //     /**
    //      * 아티팩트 ID (UUID)
    //      */
    //     private String artifactId;
        
    //     /**
    //      * 버전
    //      */
    //     private String version;
        
    //     /**
    //      * 크기 (바이트)
    //      */
    //     private Long size;
        
    //     /**
    //      * 상태 (SCANNED, PULLING, PULLED, etc.)
    //      */
    //     private String status;
        
    //     /**
    //      * 생성 일시
    //      */
    //     private LocalDateTime createdAt;
        
    //     /**
    //      * 업데이트 일시
    //      */
    //     private LocalDateTime updatedAt;
    // }
}
