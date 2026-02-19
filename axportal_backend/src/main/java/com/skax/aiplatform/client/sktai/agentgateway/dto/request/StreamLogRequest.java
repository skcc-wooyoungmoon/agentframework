package com.skax.aiplatform.client.sktai.agentgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Gateway 스트리밍 로그 요청 DTO
 * 
 * <p>SKTAI Agent Gateway에서 실시간 스트리밍 방식으로 추론을 수행하면서
 * 동시에 상세한 로그 정보를 함께 받기 위한 요청 데이터 구조입니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>input</strong>: 에이전트에게 전달할 입력 데이터</li>
 *   <li><strong>config</strong>: 스트리밍 로그 설정 정보 (선택사항)</li>
 *   <li><strong>kwargs</strong>: 추가 매개변수 (선택사항)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * StreamLogRequest request = StreamLogRequest.builder()
 *     .input("디버깅이 필요한 복잡한 요청")
 *     .config(Map.of("include_logs", true, "log_level", "debug"))
 *     .build();
 * </pre>
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
    description = "SKTAI Agent Gateway 스트리밍 로그 요청 정보",
    example = """
        {
          "input": "복잡한 추론 과정이 필요한 요청",
          "config": {
            "include_logs": true,
            "log_level": "debug",
            "stream": true
          },
          "kwargs": {}
        }
        """
)
public class StreamLogRequest {
    
    /**
     * 입력 데이터
     * 
     * <p>에이전트에게 전달할 입력 데이터입니다.
     * 로그 모니터링이 필요한 복잡한 요청을 포함합니다.</p>
     */
    @JsonProperty("input")
    @Schema(
        description = "에이전트에게 전달할 입력 데이터", 
        example = "복잡한 추론 과정이 필요한 요청"
    )
    private Object input;
    
    /**
     * 스트리밍 로그 설정
     * 
     * <p>스트리밍 로그의 동작을 제어하는 설정 정보입니다.
     * include_logs, log_level 등의 파라미터를 포함할 수 있습니다.</p>
     */
    @JsonProperty("config")
    @Schema(
        description = "스트리밍 로그 설정 정보",
        example = "{\"include_logs\": true, \"log_level\": \"debug\", \"stream\": true}"
    )
    private Object config;
    
    /**
     * 추가 매개변수
     * 
     * <p>스트리밍 로그 처리에 필요한 추가 매개변수를 전달할 수 있는 필드입니다.</p>
     */
    @JsonProperty("kwargs")
    @Schema(
        description = "추가 매개변수",
        example = "{}"
    )
    private Object kwargs;
}
