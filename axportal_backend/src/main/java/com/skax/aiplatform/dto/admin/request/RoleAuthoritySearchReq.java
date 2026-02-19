package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 프로젝트 역할 권한 검색 요청 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoleAuthoritySearchReq extends PageReq {

    @Schema(description = "검색 유형", example = "authorityNm", allowableValues = {"authorityNm", "dtlCtnt"})
    private String filterType;

    @Schema(description = "검색어", example = "데이터 조회")
    private String keyword;

    @Schema(description = "하위 메뉴명", example = "데이터 조회")
    private String twoDepthMenu;

}
