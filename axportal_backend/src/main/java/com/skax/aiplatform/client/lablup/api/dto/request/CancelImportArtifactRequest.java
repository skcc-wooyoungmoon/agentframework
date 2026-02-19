package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 가져오기 취소 요청 DTO
 * 
 * <p>진행 중인 아티팩트 가져오기 작업을 취소하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelImportArtifactRequest {

    @JsonProperty("artifact_revision_id")
    @Schema(description = "artifact_revision_id", example = "123e4567-e89b-12d3-a456-426614174000")
    private String artifactRevisionId;
    // /**
    //  * 취소할 작업 ID
    //  */
    // private String jobId;
    
    // /**
    //  * 취소 이유
    //  */
    // private String reason;
    
    // /**
    //  * 강제 취소 여부
    //  */
    // private boolean force;
    
    // /**
    //  * 부분 완료된 데이터 보존 여부
    //  */
    // private boolean preservePartialData;
}