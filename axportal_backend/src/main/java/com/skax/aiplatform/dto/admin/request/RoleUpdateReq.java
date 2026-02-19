package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 역할 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 역할 수정 요청")
public class RoleUpdateReq {

    @Schema(description = "역할명", example = "데이터 분석가")
    @NotBlank(message = "역할명은 필수입니다.")
    @Size(max = 50, message = "역할명은 50자 이하여야 합니다.")
    private String roleNm;

    @Schema(description = "역할 설명", example = "데이터 수집 및 분석을 담당하는 역할")
    @Size(max = 100, message = "역할 설명은 100자 이하여야 합니다.")
    private String dtlCtnt;

}
