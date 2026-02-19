package com.skax.aiplatform.dto.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetTags;
import com.skax.aiplatform.dto.data.response.DataCtlgProcParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 파일명 리스트로부터 훈련 데이터셋 생성 요청 DTO
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일명 리스트로부터 훈련 데이터셋 생성 요청 정보")
public class DataCtlgTrainingDatasetCreateFromFilesReq {

    @JsonProperty("source_bucket_name")
    @Schema(description = "원본 버킷 이름", example = "gaf-data-test", required = true)
    private String sourceBucketName;

    @JsonProperty("file_names")
    @Schema(description = "복사할 파일명들 (쉼표로 구분)", example = "업로드테스트.xlsx,test.txt", required = true)
    private String fileNames;

    @JsonProperty("name")
    @Schema(description = "훈련 데이터셋 이름", example = "이미지 분류 훈련 데이터셋")
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터소스 타입", example = "s3")
    private String type;

    @JsonProperty("dataset_type")
    @Schema(description = "데이터셋 타입", example = "unsupervised_finetuning", allowableValues = {"unsupervised_finetuning", "supervised_finetuning", "model_benchmark", "rag_evaluation", "custom", "dpo_finetuning"})
    private String datasetType;

    @JsonProperty("description")
    @Schema(description = "훈련 데이터셋 설명", example = "이미지 분류를 위한 훈련 데이터셋")
    private String description;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID (선택사항, 미입력시 자동 설정)", example = "default")
    private String projectId;

    @JsonProperty("created_by")
    @Schema(description = "생성자 (선택사항, 미입력시 자동 설정)", example = "system")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자 (선택사항, 미입력시 자동 설정)", example = "system")
    private String updatedBy;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @JsonProperty("scope")
    @Schema(description = "스코프", example = "public")
    private String scope;

    @JsonProperty("tags")
    @Schema(description = "데이터셋 태그 목록", example = "[{\"name\": \"test1\"}, {\"name\": \"test2\"}]")
    private List<DatasetTags> tags;

    @JsonProperty("temp_files")
    @Schema(description = "임시 파일 목록")
    private List<TempFileDto> tempFiles;

    @JsonProperty("policy")
    @Schema(description = "정책 목록")
    private List<Object> policy;

    @JsonProperty("processor")
    @Schema(description = "프로세서 파라미터", example = "{\"ids\": [], \"duplicate_subset_columns\": [\"string\"], \"regular_expression\": [\"string\"]}")
    private DataCtlgProcParam processor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "임시 파일 정보")
    public static class TempFileDto {

        @JsonProperty("file_name")
        @Schema(description = "파일 이름", example = "")
        private String fileName;

        @JsonProperty("temp_file_path")
        @Schema(description = "임시 파일 경로", example = "")
        private String tempFilePath;

        @JsonProperty("file_metadata")
        @Schema(description = "파일 메타데이터", example = "{}")
        private Map<String, Object> fileMetadata;

        @JsonProperty("knowledge_config")
        @Schema(description = "지식 설정", example = "{}")
        private Map<String, Object> knowledgeConfig;
    }
}
