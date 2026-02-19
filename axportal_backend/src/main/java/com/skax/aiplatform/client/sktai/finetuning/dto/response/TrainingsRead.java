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
 * SKTAI Fine-tuning Training 목록 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 목록을 조회한 결과를 담는 응답 데이터 구조입니다.
 * 페이지네이션 정보와 함께 Training 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: Training 목록 데이터</li>
 *   <li><strong>payload</strong>: 페이지네이션 정보</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingRead Training 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 목록 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "name": "GPT-4 Fine-tuning",
              "status": "training",
              "id": "training-123",
              "created_at": "2025-08-15T10:30:00Z"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 1
            }
          }
        }
        """
)
public class TrainingsRead {
    
    /**
     * Training 목록 데이터
     * 
     * <p>조회된 Training들의 상세 정보 배열입니다.
     * 각 항목은 TrainingRead와 동일한 구조를 가집니다.</p>
     */
    @JsonProperty("data")
    @Schema(
        description = "Training 목록 데이터",
        example = """
            [
              {
                "name": "GPT-4 Fine-tuning",
                "status": "training",
                "id": "training-123"
              }
            ]
            """
    )
    private List<TrainingRead> data;
    
    /**
     * 페이지네이션 정보
     * 
     * <p>목록 조회 결과의 페이지네이션 관련 메타데이터입니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이지네이션 정보",
        example = """
            {
              "pagination": {
                "page": 1,
                "total": 1
              }
            }
            """
    )
    private Payload payload;
}
