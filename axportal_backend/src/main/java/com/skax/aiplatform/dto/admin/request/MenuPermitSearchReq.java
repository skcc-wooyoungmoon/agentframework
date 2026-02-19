package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuPermitSearchReq extends PageReq {

    @Schema(description = "상위 메뉴명 필터 (드롭다운용)", example = "사용자 관리")
    private String oneDphMenu;

    @Schema(description = "하위 메뉴명 필터 (드롭다운용)", example = "사용자 목록")
    private String twoDphMenu;

    @Schema(description = "검색어 적용 필드 (검색창용)", example = "oneDphMenu", allowableValues = {"all", "oneDphMenu",
            "twoDphMenu"})
    private String filterType;

    @Schema(description = "검색어 (검색창용)", example = "사용자")
    private String keyword;

}

