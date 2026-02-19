package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터셋카드 원천 시스템 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "데이터셋카드 원천 시스템 정보")
public class DatasetCardSourceSystemRes {

    @JsonProperty("datasetcard_refer_cd")
    @Schema(description = "원천 시스템 코드", example = "sb")
    private String datasetcardReferCd;

    @JsonProperty("datasetcard_refer_nm")
    @Schema(description = "원천 시스템명", example = "S-Basic")
    private String datasetcardReferNm;
}


