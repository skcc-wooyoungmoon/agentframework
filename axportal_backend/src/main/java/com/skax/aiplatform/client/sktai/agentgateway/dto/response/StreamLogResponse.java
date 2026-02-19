package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Gateway 스트리밍 로그 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 스트리밍 추론과 함께 제공되는 로그 정보를 담는 응답 데이터 구조입니다.
 * 추론 과정의 상세한 로그와 응답 청크를 동시에 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>chunk</strong>: 스트리밍 응답의 일부분</li>
 *   <li><strong>logs</strong>: 추론 과정의 로그 정보</li>
 *   <li><strong>is_final</strong>: 최종 청크 여부</li>
 *   <li><strong>metadata</strong>: 스트리밍 로그 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>디버깅 및 모니터링이 필요한 실시간 추론</li>
 *   <li>추론 과정의 상세 분석</li>
 *   <li>성능 최적화를 위한 로그 수집</li>
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
    description = "SKTAI Agent Gateway 스트리밍 로그 응답",
    example = """
        {
          "chunk": "안녕하세요! 저는",
          "logs": [
            {
              "level": "INFO",
              "message": "토큰 생성 시작",
              "timestamp": "2025-08-22T10:30:00Z"
            }
          ],
          "is_final": false,
          "metadata": {
            "chunk_index": 1,
            "log_count": 1,
            "processing_time": 0.1
          }
        }
        """
)
public class StreamLogResponse {
    
    /**
     * 스트리밍 응답 청크
     * 
     * <p>전체 응답의 일부분을 담는 청크 데이터입니다.
     * 로그와 함께 제공되는 실제 응답 내용입니다.</p>
     */
    @JsonProperty("chunk")
    @Schema(
        description = "스트리밍 응답의 일부분", 
        example = "안녕하세요! 저는"
    )
    private Object chunk;
    
    /**
     * 추론 과정 로그
     * 
     * <p>현재 청크 생성과 관련된 상세 로그 정보입니다.
     * 디버깅 및 모니터링을 위한 정보를 포함합니다.</p>
     */
    @JsonProperty("logs")
    @Schema(
        description = "추론 과정의 상세 로그 정보"
    )
    private Object logs;
    
    /**
     * 최종 청크 여부
     * 
     * <p>현재 청크가 응답의 마지막 부분인지를 나타냅니다.
     * true인 경우 스트리밍이 완료된 것입니다.</p>
     */
    @JsonProperty("is_final")
    @Schema(
        description = "최종 청크 여부", 
        example = "false"
    )
    private Boolean isFinal;
    
    /**
     * 스트리밍 로그 메타데이터
     * 
     * <p>스트리밍 로그 과정의 메타데이터입니다.
     * 청크 인덱스, 로그 수, 처리 시간 등의 정보를 포함할 수 있습니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(
        description = "스트리밍 로그 메타데이터",
        example = "{\"chunk_index\": 1, \"log_count\": 1, \"processing_time\": 0.1}"
    )
    private Object metadata;
}
