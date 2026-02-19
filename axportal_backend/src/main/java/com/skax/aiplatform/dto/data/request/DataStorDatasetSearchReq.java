package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 데이터 저장소 데이터셋 검색 요청 DTO
 *
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 검색 요청")
public class DataStorDatasetSearchReq {

    @Schema(description = "검색어", example = "규정")
    private String searchWord;

    @Schema(description = "원천시스템코드 (value1,value2,value3 형식, 공백 없이)", example = "sb,crm,mis")
    private List<String> originSystemCd;

    @Schema(description = "페이지당 표시수", example = "20")
    private Long countPerPage;

    @Schema(description = "페이지", example = "1")
    private Long page;
}