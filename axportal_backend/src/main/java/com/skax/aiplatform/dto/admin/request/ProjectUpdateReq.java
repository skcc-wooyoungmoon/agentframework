package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 프로젝트 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProjectUpdateReq {

    @Schema(description = "프로젝트명", example = "AI 개발 프로젝트")
    @NotBlank(message = "프로젝트명은 필수입니다.")
    @Size(max = 50, message = "프로젝트명은 50자 이하여야 합니다.")
    private String prjNm;

    @Schema(description = "프로젝트 설명", example = "머신러닝 모델 개발 및 데이터 분석 프로젝트")
    @Size(max = 100, message = "프로젝트 설명은 100자 이하여야 합니다.")
    private String dtlCtnt;

    @Schema(description = "민감정보 포함 여부", example = "true")
    @NotNull(message = "민감정보 포함 여부는 필수입니다.")
    private String sstvInfInclYn;

    @Schema(description = "민감정보 포함 사유", example = "개인정보 및 기밀데이터 포함")
    @Size(max = 100, message = "민감정보 포함 사유는 100자 이하여야 합니다.")
    private String sstvInfInclDesc;

}
