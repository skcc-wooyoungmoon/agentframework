package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * 데이터셋 생성 요청 DTO
 * 
 * <p>
 * SKTAI Data API의 Dataset 생성을 위한 요청 DTO입니다.
 * OpenAPI 스펙에 따라 정의되었습니다.
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
@Schema(description = "데이터셋 생성 요청 정보")
public class DatasetCreate {

    @JsonProperty("name")
    @Schema(description = "데이터셋 이름", required = true, example = "My Dataset")
    private String name;

    @JsonProperty("type")
    @Schema(description = "데이터셋 타입", allowableValues = { "unsupervised_finetuning", "supervised_finetuning",
            "model_benchmark", "rag_evaluation", "custom",
            "dpo_finetuning" }, example = "unsupervised_finetuning", defaultValue = "unsupervised_finetuning")
    private String type;

    @JsonProperty("description")
    @Schema(description = "데이터셋 설명", example = "AI 학습을 위한 데이터셋")
    private String description;

    @JsonProperty("tags")
    @Schema(description = "데이터셋 태그 목록")
    private List<DatasetTags> tags;

    @JsonProperty("status")
    @Schema(description = "데이터셋 상태", example = "processing")
    private String status;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", required = true)
    private String projectId;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", defaultValue = "false")
    private Boolean isDeleted;

    @JsonProperty("datasource_id")
    @Schema(description = "데이터소스 ID", required = true)
    private UUID datasourceId;

    @JsonProperty("processor")
    @Schema(description = "프로세서 파라미터")
    private ProcessorParam processor;

    @JsonProperty("created_by")
    @Schema(description = "생성자", maxLength = 255)
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자", maxLength = 255)
    private String updatedBy;

    @JsonProperty("policy")
    @Schema(description = "정책 설정")
    private Object policy;
}
