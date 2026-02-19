package com.skax.aiplatform.dto.model.response;

import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainerRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricRead;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetByIdRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파인튜닝 모델 응답 DTO")
public class ModelFineTuningTrainingRes {

    @Schema(description = "파인튜닝 모델 명", example = "ft-1234567890abcdef")
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

    @Schema(description = "파인튜닝 모델 베이스 상세", example = "{\"id\": \"base-model-123456\", \"displayName\": \"Base Model\", \"name\": \"base-model\", \"type\": \"base\", \"description\": \"This is a base model\"}")
    private ModelDetailRes baseModelDetail;

    @Schema(description = "파인튜닝 모델 데이터셋 상세 목록", example = "[{\"id\": \"dataset-123456\", \"name\": \"Dataset Name\", \"description\": \"This is a dataset\"}]")
    private List<DataCtlgDataSetByIdRes> datasetDetails;

    @Schema(description = "파인튜닝 모델 트레이너 상세", example = "{\"id\": \"trainer-123456\", \"registryUrl\": \"https://registry.example.com/trainer\", \"description\": \"Custom trainer\"}")
    private TrainerRead trainerDetail;

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

    @Schema(description = "파인튜닝 모델 아이디", example = "2e1ec635-9e09-446a-a4b1-97050fa07f7a")
    private String id;

    @Schema(description = "파인튜닝 모델 트레이너 아이디", example = "77a85f64-5717-4562-b3fc-2c963f66afa6")
    private String trainerId;

    @Schema(description = "파인튜닝 모델 생성 일자", example = "2025-07-17T11:24:03.718857")
    private LocalDateTime createdAt;

    @Schema(description = "파인튜닝 모델 수정 일자", example = "2025-07-17T11:24:03.718868")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Training 메트릭 목록 (스텝 순서로 정렬)",
            example = """
                    [
                      {
                        "step": 100,
                        "loss": 0.245,
                        "custom_metric": {
                          "accuracy": 0.92
                        },
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                      }
                    ]
                    """
    )
    private List<TrainingMetricRead> metricDetails;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "수정자")
    private String updatedBy;

}