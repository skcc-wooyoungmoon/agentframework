package com.skax.aiplatform.dto.home.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "사용자 IDE 목록 검색 요청")
public class SearchIdeStatusReq extends PageReq {

    @Schema(description = "검색어 (도구명, 이미지명, DW계정 통합 검색)")
    private String keyword;

}
