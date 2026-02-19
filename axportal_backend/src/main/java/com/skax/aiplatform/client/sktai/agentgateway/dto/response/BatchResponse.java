package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Gateway 배치 추론 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 배치 추론 호출의 결과를 담는 응답 데이터 구조입니다.
 * 여러 입력에 대한 에이전트의 응답 결과들과 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>outputs</strong>: 각 입력에 대한 에이전트 응답 결과 목록</li>
 *   <li><strong>metadata</strong>: 배치 추론 과정의 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>다중 입력에 대한 배치 처리 결과 획득</li>
 *   <li>대량 데이터 처리 효율성 향상</li>
 *   <li>배치 처리 성능 모니터링</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Gateway 배치 추론 응답",
    example = """
        {
          "outputs": [
            "첫 번째 질문에 대한 답변입니다",
            "두 번째 질문에 대한 답변입니다",
            "세 번째 질문에 대한 답변입니다"
          ],
          "metadata": {
            "batch_size": 3,
            "total_processing_time": 3.45,
            "average_time_per_item": 1.15
          }
        }
        """
)
public class BatchResponse {
    
    /**
     * 배치 응답 결과 목록
     * 
     * <p>각 입력에 대한 에이전트의 응답 결과들의 목록입니다.
     * 입력 순서와 동일한 순서로 응답이 제공됩니다.</p>
     */
    @JsonProperty("outputs")
    @Schema(
        description = "각 입력에 대한 에이전트 응답 결과 목록"
    )
    private List<Object> outputs;
    
    /**
     * 배치 추론 메타데이터
     * 
     * <p>배치 추론 과정에서 생성된 메타데이터입니다.
     * 배치 크기, 총 처리 시간, 평균 처리 시간 등을 포함할 수 있습니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(
        description = "배치 추론 과정의 메타데이터",
        example = "{\"batch_size\": 3, \"total_processing_time\": 3.45, \"average_time_per_item\": 1.15}"
    )
    private Object metadata;
}
