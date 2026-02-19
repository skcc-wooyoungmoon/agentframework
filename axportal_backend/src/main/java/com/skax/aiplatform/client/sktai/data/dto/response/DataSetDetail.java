package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetTags;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

/**
 * 데이터셋 상세 정보 응답 DTO
 * 
 * <p>
 * OpenAPI 스펙에 따른 DataSetDetail 정의입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 상세 정보")
public class DataSetDetail {

    @JsonProperty("id")
    @Schema(description = "데이터셋 ID", required = true)
    private UUID id;

    @JsonProperty("name")
    @Schema(description = "데이터셋 이름", required = true)
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터셋 타입", allowableValues = { "unsupervised_finetuning", "supervised_finetuning",
            "model_benchmark", "rag_evaluation" }, defaultValue = "unsupervised_finetuning")
    private String type;

    @JsonProperty("description")
    @Schema(description = "데이터셋 설명", defaultValue = "")
    private String description;

    @JsonProperty("tags")
    @Schema(description = "데이터셋 태그 목록")
    private List<DatasetTags> tags;

    @JsonProperty("status")
    @Schema(description = "데이터셋 상태", defaultValue = "processing")
    private String status;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", defaultValue = "")
    private String projectId;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", defaultValue = "false")
    private Boolean isDeleted;

    @JsonProperty("file_path")
    @Schema(description = "파일 경로", required = true)
    private String filePath;

    @JsonProperty("created_by")
    @Schema(description = "생성자", required = true)
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", required = true)
    private String updatedBy;

    @JsonProperty("created_at")
    @Schema(description = "생성일시", required = true)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정일시", required = true)
    private LocalDateTime updatedAt;

    @JsonProperty("datasource_id")
    @Schema(description = "데이터소스 ID", required = true)
    private String datasourceId;

    @JsonProperty("source_file_name")
    @Schema(description = "소스 파일 이름", required = true)
    private String sourceFileName;
}
