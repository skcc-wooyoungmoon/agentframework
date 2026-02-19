package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 이벤트 기본 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 진행 중 발생하는 이벤트 정보를 담는 기본 데이터 구조입니다.
 * 실시간 모니터링과 로그 추적을 위한 정보를 제공합니다.</p>
 * 
 * <h3>주요 정보:</h3>
 * <ul>
 *   <li><strong>time</strong>: 이벤트가 발생한 정확한 시간</li>
 *   <li><strong>log</strong>: 이벤트와 관련된 로그 메시지</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Training 진행 상황 실시간 모니터링</li>
 *   <li>오류 발생 시점 추적</li>
 *   <li>Training 성능 분석</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingEventsRead Training 이벤트 목록 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 이벤트 기본 정보",
    example = """
        {
          "time": "2025-08-15T10:30:00Z",
          "log": "Epoch 1/10 - Loss: 0.245, Accuracy: 0.92"
        }
        """
)
public class TrainingEventBase {
    
    /**
     * 이벤트 발생 시간
     * 
     * <p>Training 이벤트가 발생한 정확한 시간을 ISO 8601 형식으로 제공합니다.
     * 시간 순서대로 정렬하여 Training 진행 순서를 추적할 수 있습니다.</p>
     * 
     * @implNote UTC 시간으로 제공되며, 클라이언트에서 로컬 시간으로 변환 필요
     */
    @JsonProperty("time")
    @Schema(
        description = "이벤트 발생 시간 (ISO 8601 형식)", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private String time;
    
    /**
     * 이벤트 로그 메시지
     * 
     * <p>이벤트와 관련된 상세한 로그 정보입니다.
     * Training 진행 상황, 오류 메시지, 성능 지표 등을 포함할 수 있습니다.</p>
     * 
     * @apiNote 로그 메시지는 디버깅과 모니터링에 중요한 정보를 제공합니다.
     */
    @JsonProperty("log")
    @Schema(
        description = "이벤트 로그 메시지", 
        example = "Epoch 1/10 - Loss: 0.245, Accuracy: 0.92"
    )
    private String log;
}
