package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파인튜닝 모델 응답 DTO")
public class ModelFineTuningStatusRes {
    
    @Schema(description = "파인튜닝 모델 상태", example = "succeeded")
    private String status;

    @Schema(description = "파인튜닝 모델 이전 상태", example = "pending")
    private String prevStatus;

}