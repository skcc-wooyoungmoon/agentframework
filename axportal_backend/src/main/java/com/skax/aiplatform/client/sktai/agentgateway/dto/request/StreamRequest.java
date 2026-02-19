package com.skax.aiplatform.client.sktai.agentgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Gateway 스트리밍 추론 요청 DTO
 * 
 * <p>SKTAI Agent Gateway에서 실시간 스트리밍 방식으로 추론을 수행하기 위한 요청 데이터 구조입니다.
 * 응답을 청크 단위로 실시간으로 받을 수 있어 사용자 경험을 향상시킬 수 있습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>input</strong>: 에이전트에게 전달할 입력 데이터</li>
 *   <li><strong>config</strong>: 스트리밍 추론 설정 정보 (선택사항)</li>
 *   <li><strong>kwargs</strong>: 추가 매개변수 (선택사항)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * StreamRequest request = StreamRequest.builder()
 *     .input("긴 텍스트 생성 요청")
 *     .config(Map.of("stream", true, "temperature", 0.7))
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
    description = "SKTAI Agent Gateway 스트리밍 추론 요청 정보",
    example = """
        {
          "input": "긴 텍스트를 생성해주세요",
          "config": {
            "stream": true,
            "temperature": 0.7,
            "max_tokens": 2000
          },
          "kwargs": {}
        }
        """
)
public class StreamRequest {
    
    /**
     * 입력 데이터
     * 
     * <p>에이전트에게 전달할 입력 데이터입니다.
     * 스트리밍 응답을 위한 프롬프트나 질문을 포함합니다.</p>
     */
    @JsonProperty("input")
    @Schema(
        description = "에이전트에게 전달할 입력 데이터", 
        example = "긴 텍스트를 생성해주세요"
    )
    private Object input;
    
    /**
     * 스트리밍 추론 설정
     * 
     * <p>스트리밍 추론의 동작을 제어하는 설정 정보입니다.
     * stream=true, temperature, max_tokens 등의 파라미터를 포함할 수 있습니다.</p>
     */
    @JsonProperty("config")
    @Schema(
        description = "스트리밍 추론 설정 정보",
        example = "{\"stream\": true, \"temperature\": 0.7, \"max_tokens\": 2000}"
    )
    private Object config;
    
    /**
     * 추가 매개변수
     * 
     * <p>스트리밍 처리에 필요한 추가 매개변수를 전달할 수 있는 필드입니다.</p>
     */
    @JsonProperty("kwargs")
    @Schema(
        description = "추가 매개변수",
        example = "{}"
    )
    private Object kwargs;
}
