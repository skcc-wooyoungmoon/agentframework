package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 상태 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training의 현재 상태 정보를 조회한 결과를 담는 응답 데이터 구조입니다.
 * Training의 현재 상태와 이전 상태를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>status</strong>: 현재 Training 상태</li>
 *   <li><strong>prev_status</strong>: 이전 Training 상태</li>
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
    description = "SKTAI Fine-tuning Training 상태 조회 응답 정보",
    example = """
        {
          "status": "training",
          "prev_status": "resource-allocated"
        }
        """
)
public class TrainingStatusRead {
    
    /**
     * 현재 Training 상태
     * 
     * <p>Training의 현재 상태를 나타냅니다.
     * Training 생명주기의 다양한 단계를 표현합니다.</p>
     * 
     * @apiNote 필수 필드이며, 정의된 상태값 중 하나여야 합니다.
     */
    @JsonProperty("status")
    @Schema(
        description = "현재 Training 상태", 
        example = "training",
        required = true,
        allowableValues = {
            "initialized", "starting", "stopping", "stopped", 
            "resource-allocating", "resource-allocated", "training", "trained", "error"
        }
    )
    private String status;
    
    /**
     * 이전 Training 상태
     * 
     * <p>Training의 이전 상태를 나타냅니다.
     * 상태 변화 추적 및 디버깅에 유용합니다.</p>
     * 
     * @apiNote 필수 필드이며, 정의된 상태값 중 하나여야 합니다.
     */
    @JsonProperty("prev_status")
    @Schema(
        description = "이전 Training 상태", 
        example = "resource-allocated",
        required = true,
        allowableValues = {
            "initialized", "starting", "stopping", "stopped", 
            "resource-allocating", "resource-allocated", "training", "trained", "error"
        }
    )
    private String prevStatus;
}
