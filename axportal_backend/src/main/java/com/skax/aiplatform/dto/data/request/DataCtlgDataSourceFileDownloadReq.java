package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터소스 파일 다운로드 요청 DTO
 * 
 * <p>
 * 프론트엔드에서 전달되는 파일 다운로드 관련 파라미터를 매핑하는 DTO입니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "데이터소스 파일 다운로드 요청")
public class DataCtlgDataSourceFileDownloadReq {

    @Schema(description = "다운로드 여부", example = "true")
    private Boolean download;

    @Schema(description = "S3 업로드 여부", example = "true")
    private Boolean uploadToS3;

    @Schema(description = "ES 메타 정보 저장 여부", example = "true")
    private Boolean saveToEs;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "데이터셋 카테고리 01", example = "학습")
    private String datasetCat01;

    @Schema(description = "데이터셋 카테고리 02", example = "dpo_finetuning")
    private String datasetCat02;

    @Schema(description = "데이터셋 카테고리 03", example = "category03_value_1")
    private String datasetCat03;

    @Schema(description = "데이터셋 카테고리 04", example = "category04_value_1")
    private String datasetCat04;

    @Schema(description = "데이터셋 카테고리 05", example = "category05_value_1")
    private String datasetCat05;

    @Schema(description = "설명 내용", example = "학습 데이터 설명 내용")
    private String descCtnt;

    @Schema(description = "제목", example = "학습 데이터 제목")
    private String title;

    @Schema(description = "태그", example = "tag1, tag2")
    private String tags;

    @Schema(description = "수정자", example = "user")
    private String updatedBy;

    @Schema(description = "최초 생성일시", example = "2025-01-13 10:00:00")
    private String fstCreatedAt;

    @Schema(description = "최종 수정일시", example = "2025-01-13 10:00:00")
    private String lstUpdatedAt;

    @Schema(description = "오존 경로", example = "s3://bucket/path")
    private String ozonePath;
}
