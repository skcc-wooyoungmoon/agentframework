package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학습/평가데이터 검색 요청 DTO
 * 
 * <p>학습/평가데이터 검색 요청을 담는 DTO입니다.
 * 
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "학습/평가데이터 검색 요청")
public class DataStorTrainEvalSearchReq {

    @Schema(description = "페이지", example = "1")
    private Long page;

    @Schema(description = "페이지당 표시수", example = "20")
    private Long countPerPage;

    @Schema(description = "카테고리1 (학습/평가)", example = "학습")
    private String cat01;

    @Schema(description = "카테고리2 - 학습: SUPERVISED,UNSUPERVISED,DPO,CUSTOM / 평가: QUERY_SET,RESPONSE_SET,HUMAN_EVALUATION_RESULT_MANUAL,HUMAN_EVALUATION_RESULT_INTERACTIVE", 
            example = "SUPERVISED")
    private String cat02;

    @Schema(description = "제목 검색어 (선택사항)", example = "데이터 제목")
    private String title;
}