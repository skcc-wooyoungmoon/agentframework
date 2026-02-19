package com.skax.aiplatform.dto.admin.request;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 프로젝트 구성원 역할 변경 요청 DTO
 */
@Data
@Schema(description = "프로젝트 구성원 역할 변경 요청")
public class ProjectUserRoleChangeReq {

    @NotEmpty(message = "역할 변경 대상은 하나 이상이어야 합니다.")
    @Schema(description = "구성원별 역할 변경 정보 목록")
    private List<UserRoleChange> users;

    @Data
    @Schema(description = "구성원 역할 변경 정보")
    public static class UserRoleChange {

        @NotBlank(message = "사용자 UUID는 필수입니다.")
        @Schema(description = "프로젝트 구성원의 사용자 UUID", example = "user-uuid-000001")
        private String userUuid;

        @NotBlank(message = "역할 UUID는 필수입니다.")
        @Schema(description = "변경할 역할 UUID", example = "role-uuid-000001")
        private String roleUuid;

    }

}
