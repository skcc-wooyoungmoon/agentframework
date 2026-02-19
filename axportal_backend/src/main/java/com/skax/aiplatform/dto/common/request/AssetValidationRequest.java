package com.skax.aiplatform.dto.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 에셋 검증 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "에셋 검증 요청")
public class AssetValidationRequest {
    
    @JsonProperty("project_id")
    @NotBlank(message = "프로젝트 ID는 필수입니다")
    @Schema(description = "프로젝트 ID", example = "-999", required = true)
    private String projectId;

    @JsonProperty("type")
    @NotBlank(message = "객체 타입은 필수입니다")
    @Schema(description = "객체 타입", example = "MODEL", required = true)
    private String type;
}

