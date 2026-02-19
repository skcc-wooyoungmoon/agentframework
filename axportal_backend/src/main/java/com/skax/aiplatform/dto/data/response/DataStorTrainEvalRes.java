package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학습/평가데이터 검색 응답 DTO
 * 
 * <p>Elasticsearch gaf_datasets 인덱스에서 조회한 학습/평가데이터 정보를 담는 응답 DTO입니다.
 * Elasticsearch 응답 구조를 기반으로 생성되었습니다.</p>
 * 
 * @author System
 * @since 2025-10-17
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학습/평가데이터 검색 응답")
public class DataStorTrainEvalRes {

    @Schema(description = "생성자", example = "user")
    private String createdBy;

    @Schema(description = "데이터셋 카테고리1", example = "학습")
    private String datasetCat01;

    @Schema(description = "데이터셋 카테고리2", example = "SUPERVISED")
    private String datasetCat02;

    @Schema(description = "데이터셋 카테고리3", example = "category03_value_1")
    private String datasetCat03;

    @Schema(description = "데이터셋 카테고리4", example = "category04_value_1")
    private String datasetCat04;

    @Schema(description = "데이터셋 카테고리5", example = "category05_value_1")
    private String datasetCat05;

    @Schema(description = "설명 내용", example = "학습/평가 데이터 설명 내용")
    private String descCtnt;

    @Schema(description = "최초 생성일시", example = "20231017 12:00:00")
    private String fstCreatedAt;

    @Schema(description = "최종 수정일시", example = "20231017 12:00:00")
    private String lstUpdatedAt;

    @Schema(description = "오존 경로", example = "path/to/ozone_file")
    private String ozonePath;

    @Schema(description = "태그", example = "tag1, tag2")
    private String tags;

    @Schema(description = "제목", example = "학습/평가 데이터 제목")
    private String title;

    @Schema(description = "수정자", example = "user")
    private String updatedBy;
}