package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 프로젝트 역할 구성원 정보 검색 요청 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectRoleUserSearchReq extends PageReq {

    @Schema(description = "검색 조건", example = "jkwNm", allowableValues = {"jkwNm", "deptNm"})
    private String filterType;

    @Schema(description = "검색어", example = "김신한")
    private String keyword;

    @Schema(description = "계정 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "DORMANT", "ALL"})
    private String dmcStatus;

    @Schema(description = "퇴직 여부 (1: 재직, 0: 퇴직)", example = "1")
    private String retrJkwYn;

}
