package com.skax.aiplatform.dto.admin.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "이미지 리소스 설정 요청", description = "IDE 이미지 생성 가능 개수 설정")
public class UpdateImageResourceReq {

    @NotNull(message = "도구명은 필수입니다")
    @Schema(description = "이미지 구분 (도구명)", enumAsRef = true, example = "VSCODE")
    private ImageType imgG;

    @NotNull(message = "생성 제한 개수는 필수입니다")
    @Min(value = 1, message = "생성 제한 개수는 1개 이상이어야 합니다")
    @Max(value = 99, message = "생성 제한 개수는 99개 이하여야 합니다")
    @Schema(description = "이미지 생성 가능 개수", example = "5", minimum = "1")
    private int limitCnt;

}
