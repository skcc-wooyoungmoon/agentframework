package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 데이터소스 생성 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 생성 요청 정보")
public class DatasourceCreate {

    @JsonProperty("name")
    @Schema(description = "데이터소스 이름", required = true)
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터소스 타입", example = "database")
    private String type;

    @JsonProperty("description")
    @Schema(description = "데이터소스 설명")
    private String description;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @JsonProperty("s3_config")
    @Schema(description = "스키마 설정")
    private DataSourceS3Config s3Config;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    @JsonProperty("temp_files")
    @Schema(description = "임시 파일 목록")
    private List<TempFileDto> tempFiles;

    @JsonProperty("scope")
    @Schema(description = "스코프", example = "private_physical")
    private String scope;

    @JsonProperty("policy")
    @Schema(description = "정책 목록")
    private List<Object> policy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "임시 파일 정보")
    public static class TempFileDto {

        @JsonProperty("file_name")
        @Schema(description = "파일 이름", example = "테스트_unsupervised.xlsx", required = true)
        private String fileName;

        @JsonProperty("temp_file_path")
        @Schema(description = "임시 파일 경로", example = "/cephfs/data/datasource/temp/9afa8bda-b60_20251019171533591813.xlsx", required = true)
        private String tempFilePath;

        @JsonProperty("file_metadata")
        @Schema(description = "파일 메타데이터")
        private Object fileMetadata;

        @JsonProperty("knowledge_config")
        @Schema(description = "지식 설정")
        private Object knowledgeConfig;
    }
}
