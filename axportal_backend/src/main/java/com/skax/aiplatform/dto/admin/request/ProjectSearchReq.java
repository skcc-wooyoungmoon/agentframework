package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 프로젝트 검색 요청 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-30
 */
@Getter
@NoArgsConstructor
@ToString
@Setter
public class ProjectSearchReq extends PageReq {

    @Schema(description = "검색 유형", example = "prjNm", allowableValues = {"prjNm", "dtlCtnt"})
    private String filterType;

    @Schema(description = "검색어", example = "대출 상품 추천")
    private String keyword;

}
