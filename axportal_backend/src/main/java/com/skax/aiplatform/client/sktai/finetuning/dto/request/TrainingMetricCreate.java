package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 메트릭 생성 요청 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 메트릭을 추가하기 위한 요청 데이터 구조입니다.
 * Training 과정에서 발생하는 손실값, 커스텀 메트릭 등을 기록합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>step</strong>: Training 스텝</li>
 *   <li><strong>loss</strong>: 손실값</li>
 *   <li><strong>custom_metric</strong>: 커스텀 메트릭</li>
 *   <li><strong>type</strong>: 메트릭 타입</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingMetricRead Training 메트릭 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 메트릭 생성 요청 정보",
    example = """
        {
          "step": 100,
          "loss": 0.245,
          "custom_metric": {
            "accuracy": 0.92,
            "f1_score": 0.88
          },
          "type": "train"
        }
        """
)
public class TrainingMetricCreate {
    
    /**
     * Training 스텝
     * 
     * <p>메트릭이 기록된 Training의 스텝 번호입니다.
     * Training 진행 과정을 추적하는 데 사용됩니다.</p>
     * 
     * @apiNote 필수 필드이며, 0 이상의 정수값입니다.
     */
    @JsonProperty("step")
    @Schema(
        description = "Training 스텝 번호", 
        example = "100",
        required = true,
        minimum = "0"
    )
    private Integer step;
    
    /**
     * 손실값
     * 
     * <p>해당 스텝에서의 Training 손실값입니다.
     * 모델의 학습 성능을 나타내는 주요 지표입니다.</p>
     * 
     * @apiNote 필수 필드이며, 일반적으로 양수값입니다.
     */
    @JsonProperty("loss")
    @Schema(
        description = "Training 손실값", 
        example = "0.245",
        required = true,
        minimum = "0"
    )
    private Double loss;
    
    /**
     * 커스텀 메트릭
     * 
     * <p>추적하고자 하는 커스텀 메트릭들입니다.
     * 정확도, F1 스코어, BLEU 스코어 등 다양한 평가 지표를 포함할 수 있습니다.</p>
     * 
     * @apiNote 필수 필드이며, JSON 객체 형태로 저장됩니다.
     */
    @JsonProperty("custom_metric")
    @Schema(
        description = "커스텀 메트릭 (JSON 객체, 정확도, F1 스코어 등)", 
        example = "{\"accuracy\": 0.92, \"f1_score\": 0.88}",
        required = true,
        type = "object"
    )
    private Object customMetric;
    
    /**
     * 메트릭 타입
     * 
     * <p>메트릭의 타입을 나타냅니다.
     * train, evaluation, dpo 중 하나를 선택할 수 있습니다.</p>
     * 
     * @apiNote 필수 필드이며, 정의된 열거값 중 하나여야 합니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "메트릭 타입", 
        example = "train",
        required = true,
        allowableValues = {"train", "evaluation", "dpo"}
    )
    private TrainingMetricTypeEnum type;
}
