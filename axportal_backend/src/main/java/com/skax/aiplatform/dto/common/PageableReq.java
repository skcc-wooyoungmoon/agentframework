package com.skax.aiplatform.dto.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableReq {
    

    @Schema(description = "페이지", example = "1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "페이지 크기", example = "12", required = false)
    @Builder.Default
    private Integer size = 12;

    @Schema(description = "정렬", example = "createdAt", required = false)
    private String sort;

    @Schema(description = "필터", example = "name", required = false)
    private String filter;

    @Schema(description = "검색어", required = false)
    private String search;

}