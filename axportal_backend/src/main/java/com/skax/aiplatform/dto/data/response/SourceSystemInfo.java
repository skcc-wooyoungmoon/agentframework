package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 원천 시스템 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "원천 시스템 정보")
public class SourceSystemInfo {

    @JsonProperty("value")
    @Schema(description = "원천 시스템 코드", example = "SOL_SAM")
    private String value;

    @JsonProperty("label")
    @Schema(description = "원천 시스템명", example = "SOL_SAM")
    private String label;

    @JsonProperty("description")
    @Schema(description = "원천 시스템 설명", example = "SOL_SAM 시스템")
    private String description;
}

