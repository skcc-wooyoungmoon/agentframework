package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 사용자 검색 요청 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-08-23
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSearchReq extends PageReq {

    @Schema(description = "검색 조건 필드명", example = "jkwNm", allowableValues = {"jkwNm", "deptNm", "memberId"})
    private String filterType;

    @Schema(description = "검색어", example = "김신한")
    private String keyword;

    @Schema(description = "계정 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "DORMANT"})
    private String dmcStatus;

    @Schema(description = "퇴직 여부 (1: 재직, 0: 퇴직)", example = "1")
    private String retrJkwYn;

}
