package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모델 배포 삭제 요청")
public class DeleteModelDeployReq {

    @NotBlank(message = "서빙 ID는 필수입니다")
    @Schema(description = "서빙 ID", example = "serving-123")
    private String servingId;

    @NotBlank(message = "서빙 타입은 필수입니다")
    @Schema(description = "서빙 타입", example = "MODEL_SERVING")
    private String servingType;
}
