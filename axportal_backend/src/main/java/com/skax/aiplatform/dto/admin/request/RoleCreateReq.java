package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 역할 생성 요청 DTO
 *
 * <p>
 * 프로젝트 내 새 역할을 생성할 때 사용하는 요청 정보입니다.
 * </p>
 */
@Getter
@NoArgsConstructor
@Setter
@Schema(description = "프로젝트 역할 생성 요청")
public class RoleCreateReq {

    @Schema(description = "역할명", example = "데이터 분석가")
    @NotBlank(message = "역할명은 필수입니다.")
    @Size(max = 50, message = "역할명은 50자 이하여야 합니다.")
    private String roleNm;

    @Schema(description = "역할 설명", example = "데이터 수집 및 분석을 담당하는 역할")
    @Size(max = 100, message = "역할 설명은 100자 이하여야 합니다.")
    private String dtlCtnt;

    @NotNull(message = "권한 아이디는 필수입니다.")
    private List<String> authorityIds;

}
