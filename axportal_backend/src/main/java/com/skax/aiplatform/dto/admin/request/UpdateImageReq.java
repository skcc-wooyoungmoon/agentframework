package com.skax.aiplatform.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * IDE 이미지 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateImageReq {

    @NotNull(message = "도구명은 필수입니다")
    @Schema(description = "이미지 구분 (도구명)", enumAsRef = true, example = "VSCODE")
    private ImageType imgG;

    @NotBlank(message = "이미지명은 필수입니다")
    @Schema(description = "이미지명", example = "VSCode Python Dev", maxLength = 150)
    private String imgNm;

    @NotBlank(message = "이미지 경로는 필수입니다")
    @Schema(description = "이미지 경로 (URL)", example = "https://registry.example.com/vscode/python:1.0", maxLength = 300)
    private String imgUrl;

    @NotBlank(message = "설명은 필수입니다")
    @Schema(description = "이미지 설명", example = "Python 개발 환경 기본 구성", maxLength = 4000)
    private String dtlCtnt;

}
