package com.skax.aiplatform.dto.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파인튜닝 모델 생성 DTO")
public class ModelFineTuningCreateRes {

    @Schema(description = "파인튜닝 모델 ID", example = "ft-1234567890abcdef")
    private String name;
    
    @Schema(description = "파인튜닝 모델 상태", example = "succeeded")
    private String status;

    @Schema(description = "파인튜닝 모델 이전 상태", example = "pending")
    private String prevStatus;

    @Schema(description = "파인튜닝 모델 진행률", example = "100%") 
    private Object progress;

    @Schema(description = "파인튜닝 모델 리소스", example = "model-resource")
    private Object resource;

    @Schema(description = "파인튜닝 모델 데이터셋 ID", example = "ds-1234567890abcdef")
    private List<String> datasetIds;

    @Schema(description = "파인튜닝 모델 베이스 모델 ID", example = "base-model-123456")
    private String baseModelId;

    @Schema(description = "파인튜닝 모델 파라미터", example = "{\"learning_rate\": 0.01, \"epochs\": 10}")
    private String params;

    @Schema(description = "파인튜닝 모델 환경 설정", example = "{\"env_key\": \"env_value\"}") 
    private Object envs;

    @Schema(description = "파인튜닝 모델 설명", example = "This is a fine-tuning model for text classification.")
    private String description;

    @Schema(description = "파인튜닝 모델 프로젝트 아이디", example = "11026e85-edfa-4789-afb9-83f6eff7ce14")
    private String projectId;

    @Schema(description = "파인튜닝 모델 작업 아이디", example = "013d25aa-7aa1-451e-9c94-efdbb57a4228")
    private String taskId;

    @Schema(description = "파인튜닝 모델 아이디", example = "013d25aa-7aa1-451e-9c94-efdbb57a4228")
    private String id;

    @Schema(description = "파인튜닝 모델 트레이너 아이디", example = "77a85f64-5717-4562-b3fc-2c963f66afa6")
    private String trainerId;
    
    @Schema(description = "파인튜닝 모델 생성 일시", example = "2023-10-01T12:00:00Z")
    private String createdAt;   

    @Schema(description = "파인튜닝 모델 수정 일시", example = "2023-10-02T12:00:00Z")
    private String updatedAt;


}