package com.skax.aiplatform.client.lablup.api.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 가져오기 취소 응답 DTO
 * 
 * <p>아티팩트 가져오기 취소 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelImportArtifactResponse {

    @JsonProperty("artifact_revision")
    @Schema(description = "artifact_revision", example = "{\"id\": \"uuid - 리비전 ID\", \"artifact_id\": \"uuid - 아티팩트 ID\", \"version\": \"string - 버전\", \"size\": \"integer - 크기\", \"status\": \"string - 상태\", \"remote_status\": \"string (optional) - 리모트 레지스트리에서의 상태 (SCANNED, AVAILABLE, etc.)\", \"readonly\": \"boolean (optional) - 읽기 전용 여부, 기본값은 true\", \"created_at\": \"datetime - 생성 일시\", \"updated_at\": \"datetime - 업데이트 일시\"}")
    private ArtifactRevision artifactRevision;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArtifactRevision {
        @JsonProperty("id")
        @Schema(description = "id", example = "uuid - 리비전 ID")
        private String id;
        
        @JsonProperty("artifact_id")
        @Schema(description = "artifact_id", example = "uuid - 아티팩트 ID")
        private String artifactId;

        @JsonProperty("version")
        @Schema(description = "version", example = "string - 버전")
        private String version;

        @JsonProperty("size")
        @Schema(description = "size", example = "integer - 크기")
        private Long size;

        @JsonProperty("status")
        @Schema(description = "status", example = "string - 상태")
        private String status;

        @JsonProperty("remote_status")
        @Schema(description = "remote_status", example = "string (optional) - 리모트 레지스트리에서의 상태 (SCANNED, AVAILABLE, etc.)")
        private String remoteStatus;

        @JsonProperty("readonly")
        @Schema(description = "readonly", example = "boolean (optional) - 읽기 전용 여부, 기본값은 true")
        private Boolean readonly;

        @JsonProperty("created_at")
        @Schema(description = "created_at", example = "datetime - 생성 일시")
        private LocalDateTime createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "updated_at", example = "datetime - 업데이트 일시")
        private LocalDateTime updatedAt;
    }
    
    // /**
    //  * 작업 ID
    //  */
    // private String jobId;
    
    // /**
    //  * 취소 상태
    //  */
    // private String status;
    
    // /**
    //  * 취소 메시지
    //  */
    // private String message;
    
    // /**
    //  * 부분 완료된 데이터 보존 여부
    //  */
    // private boolean partialDataPreserved;
    
    // /**
    //  * 취소 완료 여부
    //  */
    // private boolean cancelled;
}