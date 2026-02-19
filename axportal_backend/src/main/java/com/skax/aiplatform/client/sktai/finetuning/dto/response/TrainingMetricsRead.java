package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 메트릭 목록 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 메트릭 목록을 조회한 결과를 담는 응답 데이터 구조입니다.
 * 페이지네이션 정보와 함께 메트릭 목록을 제공하여 대량의 메트릭 데이터를 효율적으로 처리할 수 있습니다.</p>
 * 
 * <h3>주요 구성요소:</h3>
 * <ul>
 *   <li><strong>data</strong>: Training 메트릭 목록</li>
 *   <li><strong>payload</strong>: 페이지네이션 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Training 성능 지표 대시보드</li>
 *   <li>메트릭 데이터 분석 및 시각화</li>
 *   <li>모델 성능 추이 분석</li>
 *   <li>대용량 메트릭 데이터 페이징 처리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingMetricRead Training 메트릭 상세 정보
 * @see Payload 페이지네이션 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 메트릭 목록 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "step": 100,
              "loss": 0.245,
              "custom_metric": {
                "accuracy": 0.92
              },
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "created_at": "2025-08-15T10:30:00Z",
              "updated_at": "2025-08-15T10:30:00Z"
            }
          ],
          "payload": {
            "page": 1,
            "size": 20,
            "total": 1500,
            "total_pages": 75
          }
        }
        """
)
public class TrainingMetricsRead {
    
    /**
     * Training 메트릭 목록
     * 
     * <p>페이지네이션된 Training 메트릭들의 목록입니다.
     * 각 메트릭은 스텝별 성능 지표와 커스텀 메트릭을 포함합니다.</p>
     * 
     * @apiNote 메트릭은 일반적으로 스텝 순서로 정렬되어 제공되며, 성능 추이 분석에 활용됩니다.
     */
    @JsonProperty("data")
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
    private List<TrainingMetricRead> data;
    
    /**
     * 페이지네이션 정보
     * 
     * <p>메트릭 목록의 페이지네이션 관련 정보입니다.
     * 현재 페이지, 페이지 크기, 전체 개수 등을 포함합니다.</p>
     * 
     * @implNote 대량의 메트릭 데이터를 효율적으로 처리하기 위해 페이지네이션이 적용됩니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이지네이션 정보",
        example = """
            {
              "page": 1,
              "size": 20,
              "total": 1500,
              "total_pages": 75
            }
            """
    )
    private Payload payload;
}
