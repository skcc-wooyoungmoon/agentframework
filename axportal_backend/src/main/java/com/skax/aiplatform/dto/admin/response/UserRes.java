package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@Schema(description = "사용자 응답")
public class UserRes {

    @Schema(description = "사번", example = "70102312")
    private final String memberId;

    @Schema(description = "사용자 UUID", example = "uuid-kwon-doohyeon-001")
    private final String uuid;

    @Schema(description = "사용자 이름", example = "김신한")
    private final String jkwNm;

    @Schema(description = "부서", example = "Data2플랫폼Unit")
    private final String deptNm;

    @Schema(description = "직급", example = "부장")
    private final String jkgpNm;

    @Schema(description = "계정 상태", example = "ACTIVE")
    private final String dmcStatus;

    @Schema(description = "인사 상태", example = "1")
    private final String retrJkwYn;

    @Schema(description = "마지막 접속 일시", example = "2025.05.24 18:23:43")
    private final String lstLoginAt;

    public static UserRes of(User user) {
        return UserRes.builder()
                .memberId(user.getMemberId())
                .uuid(user.getUuid())
                .jkwNm(user.getJkwNm())
                .deptNm(user.getDeptNm())
                .jkgpNm(user.getJkgpNm())
                .dmcStatus(user.getDmcStatus().toString())
                .retrJkwYn(user.getRetrJkwYn().toString())
                .lstLoginAt(DateUtils.toDateTimeString(user.getLstLoginAt()))
                .build();
    }

}
