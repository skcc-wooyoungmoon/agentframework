package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 위임 가져오기 요청 DTO
 * 
 * <p>레저버 레지스트리를 통해 아티팩트를 위임 가져오기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-31
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportArtifactsDelegationRequest {

    @JsonProperty("artifact_revision_ids")
    @Schema(description = "Import 요청할 아티팩트 리비전들의 리스트", example = "[\"uuid1\", \"uuid2\"]", required = true)
    private String[] artifactRevisionIds;
    
    @JsonProperty("artifact_type")
    @Schema(description = "아티팩트 유형", example = "MODEL", allowableValues = {"MODEL", "PACKAGE", "IMAGE"})
    private String artifactType;
    
}

