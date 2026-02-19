package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Gateway 추론 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 에이전트 추론 호출의 결과를 담는 응답 데이터 구조입니다.
 * 에이전트가 처리한 결과와 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>output</strong>: 에이전트의 응답 결과</li>
 *   <li><strong>metadata</strong>: 추론 과정의 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>단일 추론 요청의 결과 처리</li>
 *   <li>에이전트 응답 분석 및 활용</li>
 *   <li>추론 품질 평가 및 모니터링</li>
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
    description = "SKTAI Agent Gateway 추론 응답",
    example = """
        {
          "output": "안녕하세요! 무엇을 도와드릴까요?",
          "metadata": {
            "processing_time": 1.23,
            "tokens_used": 50,
            "model": "gpt-4"
          }
        }
        """
)
public class InvokeResponse {
    
    /**
     * 에이전트 응답 결과
     * 
     * <p>에이전트가 입력에 대해 생성한 응답 결과입니다.
     * 텍스트, 객체, 배열 등 다양한 형태의 데이터가 될 수 있습니다.</p>
     */
    @JsonProperty("output")
    @Schema(
        description = "에이전트의 응답 결과", 
        example = "안녕하세요! 무엇을 도와드릴까요?"
    )
    private Object output;
    
    /**
     * 추론 메타데이터
     * 
     * <p>추론 과정에서 생성된 메타데이터입니다.
     * 처리 시간, 사용된 토큰 수, 모델 정보 등을 포함할 수 있습니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(
        description = "추론 과정의 메타데이터",
        example = "{\"processing_time\": 1.23, \"tokens_used\": 50, \"model\": \"gpt-4\"}"
    )
    private Object metadata;
}
