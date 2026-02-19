package com.skax.aiplatform.dto.admin.response;

import java.time.LocalDateTime;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@Schema(description = "프로젝트 구성원 응답")
public class ProjectUserRes {

    @Schema(description = "사용자 UUID", example = "uuid-kwon-doohyeon-001")
    private final String uuid;

    @Schema(description = "사번", example = "SGO1234567")
    private final String memberId;

    @Schema(description = "사용자 이름", example = "김신한")
    private final String jkwNm;

    @Schema(description = "사용자 부서명", example = "Data2플랫폼Unit")
    private final String deptNm;

    @Schema(description = "계정 상태", example = "ACTIVE")
    private final DormantStatus dmcStatus;

    @Schema(description = "인사 상태", example = "1")
    private final Integer retrJkwYn;

    @Schema(description = "마지막 접속 일시", example = "2025.05.24 18:23:43")
    private final String lstLoginAt;

    @Schema(description = "역할아이디", example = "역할 UUID")
    private final String roleUuid;

    @Schema(description = "역할명", example = "프로젝트 관리자")
    private final String roleNm;

    @Schema(description = "프로젝트 참여 일시", example = "2025.05.24 18:23:43")
    private final String joinedAt;

    public static ProjectUserRes of(User user, Role role, LocalDateTime joinedAt) {
        return ProjectUserRes.builder()
                .uuid(user.getUuid())
                .memberId(user.getMemberId())
                .jkwNm(user.getJkwNm())
                .deptNm(user.getDeptNm())
                .dmcStatus(user.getDmcStatus())
                .retrJkwYn(user.getRetrJkwYn())
                .lstLoginAt(DateUtils.toDateTimeString(user.getLstLoginAt()))
                .roleUuid(role.getUuid())
                .roleNm(role.getRoleNm())
                .joinedAt(DateUtils.toDateTimeString(joinedAt))
                .build();
    }

}
