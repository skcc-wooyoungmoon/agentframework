package com.skax.aiplatform.dto.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "파인튜닝 모델 상태 수정 DTO")
public class ModelFineTuningStatusUpdateReq {

    @NotBlank(message = "상태값은 필수입니다")
    @Schema(description = "파인튜닝 모델 상태", example = "stopped", allowableValues = {"initialized", "starting", "stopping", "stopped", "resource-allocating", "resource-allocated", "training", "trained", "error"}, required = true)
    private String status;

    @Schema(description = "리소스그룹명", example = "default")
    private String scalingGroup;
}

