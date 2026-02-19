package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 평가 생성 요청 DTO
 * 
 * <p>새로운 평가를 생성하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 생성 요청")
public class EvaluationCreateRequest {
    
    @JsonProperty("name")
    @Schema(description = "평가명", example = "GPT-4 Performance Test", required = true)
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "평가 설명", example = "GPT-4 모델의 성능 평가")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "평가 타입", example = "benchmark", required = true)
    private String type;
    
    @JsonProperty("model_id")
    @Schema(description = "평가할 모델 ID", example = "model-123")
    private String modelId;
    
    @JsonProperty("dataset_id")
    @Schema(description = "평가용 데이터셋 ID", example = "dataset-456")
    private String datasetId;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "project-789", required = true)
    private String projectId;
}
