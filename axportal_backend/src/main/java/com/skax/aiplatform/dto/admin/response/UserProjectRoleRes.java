package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자가 속한 프로젝트내 역할 응답")
public class UserProjectRoleRes {

    @Schema(description = "프로젝트 정보")
    private final ProjectInfo project;

    @Schema(description = "역할 정보")
    private final RoleInfo role;

    @Schema(description = "사용자 역할 정보")
    private final UserRoleInfo userRole;

    @Getter
    @Builder
    @Schema(description = "프로젝트 정보")
    public static class ProjectInfo {

        @Schema(description = "프로젝트 아이디")
        private final String uuid;

        @Schema(description = "프로젝트명")
        private final String prjNm;

        @Schema(description = "프로젝트 설명")
        private final String dtlCtnt;

        @Schema(description = "프로젝트 상태")
        private final String statusNm;

        @Schema(description = "프로젝트 생성일시")
        private final String fstCreatedAt;

        @Schema(description = "프로젝트 최종 수정일시")
        private final String lstUpdatedAt;

    }

    @Getter
    @Builder
    @Schema(description = "역할 정보")
    public static class RoleInfo {

        @Schema(description = "역할 아이디")
        private final String uuid;

        @Schema(description = "역할명")
        private final String roleNm;

        @Schema(description = "역할 설명")
        private final String dtlCtnt;

        @Schema(description = "역할 상태")
        private final String statusNm;

        @Schema(description = "역할 생성일시")
        private final String fstCreatedAt;

        @Schema(description = "역할 최종 수정일시")
        private final String lstUpdatedAt;

    }

    @Getter
    @Builder
    @Schema(description = "사용자 역할 정보")
    public static class UserRoleInfo {

        @Schema(description = "사용자가 참여한 프로젝트내 역할 최종 수정일시")
        private final String lstUpdatedAt;

        @Schema(description = "사용자 참여한 프로젝트내 역할 최종 수정자 정보")
        private final AuditorInfo updatedBy;

    }

    @Getter
    @Builder
    @Schema(description = "수정자 정보")
    public static class AuditorInfo {

        @Schema(description = "이름", example = "김신한")
        private final String jkwNm;

        @Schema(description = "부서명", example = "AI플랫폼셀")
        private final String deptNm;

    }

    public static UserProjectRoleRes of(ProjectUserRole projectUserRole) {
        return of(projectUserRole, null);
    }

    public static UserProjectRoleRes of(ProjectUserRole projectUserRole, User updatedByUser) {
        AuditorInfo updatedByInfo = AuditorInfo.builder()
                .jkwNm(updatedByUser != null ? updatedByUser.getJkwNm() : "")
                .deptNm(updatedByUser != null ? updatedByUser.getDeptNm() : "")
                .build();

        return UserProjectRoleRes.builder()
                .project(ProjectInfo.builder()
                        .uuid(projectUserRole.getProject().getUuid())
                        .prjNm(projectUserRole.getProject().getPrjNm())
                        .dtlCtnt(projectUserRole.getProject().getDtlCtnt())
                        .statusNm(projectUserRole.getProject().getStatusNm().toString())
                        .fstCreatedAt(DateUtils.toDateTimeString(projectUserRole.getProject().getFstCreatedAt()))
                        .lstUpdatedAt(DateUtils.toDateTimeString(projectUserRole.getProject().getLstUpdatedAt()))
                        .build())
                .role(RoleInfo.builder()
                        .uuid(projectUserRole.getRole().getUuid())
                        .roleNm(projectUserRole.getRole().getRoleNm())
                        .dtlCtnt(projectUserRole.getRole().getDtlCtnt())
                        .statusNm(projectUserRole.getRole().getStatusNm().toString())
                        .fstCreatedAt(DateUtils.toDateTimeString(projectUserRole.getRole().getFstCreatedAt()))
                        .lstUpdatedAt(DateUtils.toDateTimeString(projectUserRole.getRole().getLstUpdatedAt()))
                        .build())
                .userRole(UserRoleInfo.builder()
                        .lstUpdatedAt(DateUtils.toDateTimeString(projectUserRole.getLstUpdatedAt()))
                        .updatedBy(updatedByInfo)
                        .build())
                .build();
    }

}
