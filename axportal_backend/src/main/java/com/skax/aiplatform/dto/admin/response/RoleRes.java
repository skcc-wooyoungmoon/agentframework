package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "역할 상세 정보 응답")
public class RoleRes {

    @Schema(description = "역할 아이디", example = "role-uuid-project-admin-004")
    private final String uuid;

    @Schema(description = "역할명", example = "프로젝트 관리자")
    private final String roleNm;

    @Schema(description = "역할 설명", example = "프로젝트를 관리하는 관리자")
    private final String dtlCtnt;

    @Schema(description = "역할 유형", example = "DEFAULT")
    private final String roleType;

    @Schema(description = "역할 생성일시", example = "2025.08.31 15:45:06")
    private final String fstCreatedAt;

    @Schema(description = "역할 최종 수정일시", example = "2025.08.31 15:45:06")
    private final String lstUpdatedAt;

    public static RoleRes of(Role role) {
        return RoleRes.builder()
                .uuid(role.getUuid())
                .roleNm(role.getRoleNm())
                .dtlCtnt(role.getDtlCtnt())
                .roleType(role.getRoleType().toString())
                .fstCreatedAt(DateUtils.toDateTimeString(role.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(role.getLstUpdatedAt()))
                .build();
    }

}
