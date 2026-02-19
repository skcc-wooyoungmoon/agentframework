package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 아티팩트 가져오기 응답 DTO
 * 
 * <p>아티팩트 가져오기 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportArtifactsResponse {
    

    @JsonProperty("tasks")
    @Schema(description = "작업 목록")
    private LablupTask[] tasks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LablupTask {
        @JsonProperty("task_id")
        @Schema(description = "작업 ID")
        private String taskId;

        @JsonProperty("artifact_revision")
        @Schema(description = "아티팩트 리비전")
        private ArtifactRevision artifactRevision;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ArtifactRevision {
            @JsonProperty("id")
            @Schema(description = "리비전 ID")
            private String id;

            @JsonProperty("artifact_id")
            @Schema(description = "아티팩트 ID")
            private String artifactId;

            @JsonProperty("version")
            @Schema(description = "버전")   
            private String version;

            @JsonProperty("size")
            @Schema(description = "크기")
            private Long size;

            @JsonProperty("status")
            @Schema(description = "상태")
            private String status;

            @JsonProperty("created_at")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
            @Schema(description = "생성 일시")
            private LocalDateTime createdAt;

            @JsonProperty("updated_at")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
            @Schema(description = "업데이트 일시")
            private LocalDateTime updatedAt;
        }
    }


    // /**
    //  * 가져오기 작업 ID
    //  */
    // private String jobId;
    
    // /**
    //  * 작업 상태
    //  */
    // private String status;
    
    // /**
    //  * 시작 시간
    //  */
    // private LocalDateTime startTime;
    
    // /**
    //  * 완료 시간
    //  */
    // private LocalDateTime endTime;
    
    // /**
    //  * 진행률 (0-100)
    //  */
    // private Integer progress;
    
    // /**
    //  * 가져온 아티팩트 목록
    //  */
    // private List<ImportedArtifact> importedArtifacts;
    
    // /**
    //  * 실패한 항목 목록
    //  */
    // private List<FailedImport> failures;
    
    // /**
    //  * 작업 통계
    //  */
    // private ImportStatistics statistics;
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class ImportedArtifact {
    //     /**
    //      * 소스 식별자
    //      */
    //     private String sourceId;
        
    //     /**
    //      * 대상 아티팩트 ID
    //      */
    //     private String targetArtifactId;
        
    //     /**
    //      * 아티팩트 이름
    //      */
    //     private String name;
        
    //     /**
    //      * 버전
    //      */
    //     private String version;
        
    //     /**
    //      * 크기 (bytes)
    //      */
    //     private Long size;
        
    //     /**
    //      * 가져오기 완료 시간
    //      */
    //     private LocalDateTime importedAt;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class FailedImport {
    //     /**
    //      * 소스 식별자
    //      */
    //     private String sourceId;
        
    //     /**
    //      * 실패 이유
    //      */
    //     private String reason;
        
    //     /**
    //      * 오류 코드
    //      */
    //     private String errorCode;
        
    //     /**
    //      * 상세 오류 메시지
    //      */
    //     private String errorDetails;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class ImportStatistics {
    //     /**
    //      * 총 시도 수
    //      */
    //     private Integer totalAttempted;
        
    //     /**
    //      * 성공 수
    //      */
    //     private Integer successful;
        
    //     /**
    //      * 실패 수
    //      */
    //     private Integer failed;
        
    //     /**
    //      * 건너뛴 수
    //      */
    //     private Integer skipped;
        
    //     /**
    //      * 총 크기 (bytes)
    //      */
    //     private Long totalSize;
    // }
}