package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 이벤트 목록 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 특정 Training의 이벤트 목록을 조회한 결과를 담는 응답 데이터 구조입니다.
 * 실시간 모니터링을 위한 이벤트 스트림과 마지막 이벤트 식별자를 제공합니다.</p>
 * 
 * <h3>주요 구성요소:</h3>
 * <ul>
 *   <li><strong>data</strong>: Training 이벤트 목록</li>
 *   <li><strong>last</strong>: 마지막 이벤트 식별자 (polling을 위한 커서)</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Training 진행 상황 실시간 모니터링</li>
 *   <li>로그 스트리밍 및 이벤트 폴링</li>
 *   <li>Training 이력 분석 및 디버깅</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingEventBase Training 이벤트 기본 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 이벤트 목록 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "time": "2025-08-15T10:30:00Z",
              "log": "Training started"
            },
            {
              "time": "2025-08-15T10:31:00Z",
              "log": "Epoch 1/10 - Loss: 0.245"
            }
          ],
          "last": "2025-08-15T11:30:00.000Z"
        }
        """
)
public class TrainingEventsRead {
    
    /**
     * Training 이벤트 목록
     * 
     * <p>시간 순서대로 정렬된 Training 이벤트들의 목록입니다.
     * 각 이벤트는 발생 시간과 로그 메시지를 포함합니다.</p>
     * 
     * @apiNote 최신 이벤트부터 정렬되어 제공될 수 있으므로 시간 필드를 확인해야 합니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "Training 이벤트 목록 (시간 순서로 정렬)",
        example = """
            [
              {
                "time": "2025-08-15T10:30:00Z",
                "log": "Training started"
              }
            ]
            """
    )
    private List<TrainingEventBase> data;
    
    /**
     * 마지막 이벤트 식별자
     * 
     * <p>이벤트 스트림에서 마지막으로 처리된 이벤트의 식별자입니다.
     * 다음 폴링 요청 시 이 값을 사용하여 새로운 이벤트만 조회할 수 있습니다.</p>
     * 
     * @implNote 일반적으로 타임스탬프 형태이며, 클라이언트는 이 값을 저장하여 증분 업데이트를 구현할 수 있습니다.
     */
    @JsonProperty("last")
    @Schema(
        description = "마지막 이벤트 식별자 (다음 폴링을 위한 커서)", 
        example = "2025-08-15T11:30:00.000Z"
    )
    private String last;
}
