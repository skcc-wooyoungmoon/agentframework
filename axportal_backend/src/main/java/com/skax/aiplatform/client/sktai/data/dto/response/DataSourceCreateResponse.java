package com.skax.aiplatform.client.sktai.data.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSourceS3Config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 생성 응답")
public class DataSourceCreateResponse {

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private UUID projectId;

    @JsonProperty("name")
    @Schema(description = "데이터소스 이름", example = "11_datasource_73ffc86a")
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터소스 타입", example = "file")
    private String type;

    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    @JsonProperty("description")
    @Schema(description = "데이터소스 설명", example = "")
    private String description;

    @JsonProperty("s3_config")
    @Schema(description = "S3 설정")
    private DataCtlgDataSourceS3Config s3Config;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @JsonProperty("scope")
    @Schema(description = "스코프", example = "private_logical")
    private String scope;

    @JsonProperty("id")
    @Schema(description = "데이터소스 ID", example = "454c67ff-99f6-4295-8cfb-810be4345467")
    private UUID id;

    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-10-19T17:15:41.548443")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-10-19T17:15:42.366263")
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    @Schema(description = "상태", example = "enabled")
    private String status;

    @JsonProperty("bucket_name")
    @Schema(description = "버킷 이름", example = "private/default/data/datasource/repo/datasource-454c67ff-99f6-4295-8cfb-810be4345467")
    private String bucketName;
}
