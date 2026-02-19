package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 메트릭 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 메트릭 정보를 조회한 결과를 담는 응답 데이터 구조입니다.
 * Training 성능 지표와 커스텀 메트릭을 포함하여 모델 학습 품질을 평가할 수 있습니다.</p>
 * 
 * <h3>주요 메트릭 정보:</h3>
 * <ul>
 *   <li><strong>step</strong>: Training 진행 단계</li>
 *   <li><strong>loss</strong>: 손실 함수 값</li>
 *   <li><strong>custom_metric</strong>: 사용자 정의 메트릭</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Training 성능 모니터링</li>
 *   <li>모델 품질 평가</li>
 *   <li>학습 진행률 추적</li>
 *   <li>성능 지표 시각화</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingMetricsRead Training 메트릭 목록 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 메트릭 조회 응답 정보",
    example = """
        {
          "step": 100,
          "loss": 0.245,
          "custom_metric": {
            "accuracy": 0.92,
            "precision": 0.89,
            "recall": 0.94
          },
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T10:30:00Z"
        }
        """
)
public class TrainingMetricRead {
    
    /**
     * Training 스텝 번호
     * 
     * <p>현재 Training이 진행된 스텝(반복) 번호입니다.
     * 전체 Training 진행률과 성능 변화 추이를 파악하는 기준이 됩니다.</p>
     * 
     * @implNote 스텝은 일반적으로 배치 단위로 증가하며, 전체 에포크와 연관되어 계산됩니다.
     */
    @JsonProperty("step")
    @Schema(
        description = "Training 스텝 번호 (배치 단위 진행률)", 
        example = "100",
        minimum = "0"
    )
    private Integer step;
    
    /**
     * Training 손실값
     * 
     * <p>현재 스텝에서의 손실 함수 값입니다.
     * 모델의 학습 성능을 나타내는 핵심 지표로, 일반적으로 낮을수록 좋습니다.</p>
     * 
     * @apiNote 손실값의 변화 추이를 통해 학습이 잘 진행되고 있는지 판단할 수 있습니다.
     */
    @JsonProperty("loss")
    @Schema(
        description = "Training 손실값 (낮을수록 좋음)", 
        example = "0.245",
        minimum = "0.0"
    )
    private Double loss;
    
    /**
     * 커스텀 메트릭
     * 
     * <p>사용자가 정의한 추가적인 성능 지표들입니다.
     * 정확도, 정밀도, 재현율 등 모델 평가에 필요한 다양한 메트릭을 포함할 수 있습니다.</p>
     * 
     * @implNote JSON 객체 형태로 제공되며, 메트릭 이름과 값의 키-값 쌍으로 구성됩니다.
     */
    @JsonProperty("custom_metric")
    @Schema(
        description = "커스텀 메트릭 (사용자 정의 성능 지표)", 
        example = """
            {
              "accuracy": 0.92,
              "precision": 0.89,
              "recall": 0.94,
              "f1_score": 0.915
            }
            """
    )
    private Object customMetric;
    
    /**
     * 메트릭 고유 식별자
     * 
     * <p>메트릭 레코드의 고유 식별자입니다.
     * UUID 형식으로 제공되며, 특정 메트릭을 참조하거나 업데이트할 때 사용됩니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "메트릭 고유 식별자", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 생성 일시
     * 
     * <p>메트릭이 처음 생성된 시간입니다.
     * ISO 8601 형식으로 제공되며, Training 진행 시점을 파악할 수 있습니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "메트릭 생성 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private String createdAt;
    
    /**
     * 수정 일시
     * 
     * <p>메트릭이 마지막으로 수정된 시간입니다.
     * 메트릭 값이 업데이트되거나 보정된 경우의 시점을 나타냅니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "메트릭 수정 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private String updatedAt;
}
