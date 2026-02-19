package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 역할 상세 조회 응답 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-08-31
 */
@Getter
@Builder
@Schema(description = "역할 정보 응답")
public class RoleDetailRes {

    private final RoleInfo role;

    @Getter
    @Builder
    public static class RoleInfo {

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

        @Schema(description = "최초 생성자 정보")
        private final AuditorInfo createdBy;

        @Schema(description = "최종 수정자 정보")
        private final AuditorInfo updatedBy;

    }

    @Getter
    @Builder
    public static class AuditorInfo {

        @Schema(description = "이름", example = "김신한")
        private final String jkwNm;

        @Schema(description = "부서명", example = "AI플랫폼셀")
        private final String deptNm;

    }

    public static RoleDetailRes of(Role role, GpoUsersMas createdBy, GpoUsersMas updatedBy) {
        AuditorInfo createdByInfo = AuditorInfo.builder()
                .jkwNm(createdBy.getJkwNm())
                .deptNm(createdBy.getDeptNm())
                .build();

        AuditorInfo updatedByInfo = AuditorInfo.builder()
                .jkwNm(updatedBy != null ? updatedBy.getJkwNm() : "")
                .deptNm(updatedBy != null ? updatedBy.getDeptNm() : "")
                .build();

        RoleInfo roleInfo = RoleInfo.builder()
                .uuid(role.getUuid())
                .roleNm(role.getRoleNm())
                .dtlCtnt(role.getDtlCtnt())
                .roleType(role.getRoleType().toString())
                .fstCreatedAt(DateUtils.toDateTimeString(role.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(role.getLstUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
                .build();

        return RoleDetailRes.builder()
                .role(roleInfo)
                .build();
    }

}
