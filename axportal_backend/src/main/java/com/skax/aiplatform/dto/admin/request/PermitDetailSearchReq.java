package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermitDetailSearchReq extends PageReq {

    @Schema(description = "선택된 권한 ID 목록", example = "AUTH001,AUTH002")
    private List<String> authorityIds;

    @Schema(description = "하위 메뉴명 필터 (드롭다운용)", example = "데이터 저장소")
    private String twoDphMenu;

    @Schema(description = "검색어 적용 필드 (검색창용)", example = "authorityNm", allowableValues = {"all", "authorityNm",
            "dtlCtnt"})
    private String filterType;

    @Schema(description = "검색어 (검색창용)", example = "조회")
    private String keyword;

}
