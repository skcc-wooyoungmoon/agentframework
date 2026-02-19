package com.skax.aiplatform.dto.deploy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 세이프티 필터 목록 조회 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 목록 조회 요청")
public class SafetyFilterListReq {

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private int page;

    @Schema(description = "페이지 크기", example = "12")
    private int size;

    @Schema(description = "검색 필터")
    private String filter;

    @Schema(description = "검색어 (분류, 금지어)", example = "욕설")
    private String search;

    @Schema(description = "정렬 (예: createdDate,desc)", example = "createdDate,desc")
    private String sort;

    public static SafetyFilterListReq of(int page, int size, String filter, String search, String sort) {
        return SafetyFilterListReq.builder()
                .page(page)
                .size(size)
                .filter(filter)
                .search(search)
                .sort(sort)
                .build();
    }

}

