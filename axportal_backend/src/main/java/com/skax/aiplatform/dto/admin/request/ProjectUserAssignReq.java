package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 프로젝트 구성원 역할 할당 요청 DTO
 */
@Data
@Schema(description = "프로젝트 구성원 역할 할당 요청")
public class ProjectUserAssignReq {

    @NotEmpty(message = "할당 정보는 하나 이상이어야 합니다.")
    @Schema(description = "사용자-역할 할당 목록")
    @Valid
    private List<Assignment> assignments;

    @Data
    @Schema(description = "사용자-역할 할당 정보")
    public static class Assignment {

        @NotBlank(message = "사용자 UUID는 필수입니다.")
        @Schema(description = "사용자 UUID", example = "user-uuid-001")
        private String userUuid;

        @NotBlank(message = "역할 UUID는 필수입니다.")
        @Schema(description = "할당할 역할 UUID", example = "role-uuid-001")
        private String roleUuid;

    }

}
