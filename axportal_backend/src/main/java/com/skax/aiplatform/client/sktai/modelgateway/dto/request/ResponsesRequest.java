package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 응답 생성 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 고급 응답 생성을 위한 요청 데이터 구조입니다.
 * 복잡한 상황에서 구조화된 응답을 생성하고 추론 과정을 제공합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>model</strong>: 사용할 AI 모델 식별자</li>
 *   <li><strong>input</strong>: 응답 생성을 위한 입력 (메시지 또는 텍스트)</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>고급 추론 기능</li>
 *   <li>구조화된 응답 생성</li>
 *   <li>스트리밍 지원</li>
 *   <li>응답 저장 옵션</li>
 *   <li>상세한 토큰 사용량 추적</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ResponsesRequest request = ResponsesRequest.builder()
 *     .model("gpt-4")
 *     .input(Arrays.asList(
 *         Map.of("role", "user", "content", "What is OpenAI's responses API")
 *     ))
 *     .instructions("You are a helpful assistant.")
 *     .maxOutputTokens(1000)
 *     .temperature(0.9)
 *     .store(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Gateway 응답 생성 요청 정보",
    example = """
        {
          "model": "gpt-4",
          "input": [
            {
              "role": "user",
              "content": "What is OpenAI's responses API"
            }
          ],
          "instructions": "You are a helpful assistant.",
          "max_output_tokens": 1000,
          "temperature": 0.9,
          "top_p": 0.9,
          "stream": false,
          "store": true
        }
        """
)
public class ResponsesRequest {
    
    /**
     * AI 모델 식별자
     * 
     * <p>응답 생성에 사용할 AI 모델의 이름입니다.
     * 고급 추론 기능을 지원하는 모델을 사용해야 합니다.</p>
     * 
     * @implNote 지원 모델: gpt-4, gpt-4-turbo, gpt-4o 등
     * @apiNote 모델에 따라 추론 능력과 응답 품질이 달라집니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 AI 모델 식별자 (고급 추론 지원 모델)",
        example = "gpt-4",
        required = true
    )
    private String model;
    
    /**
     * 입력 데이터
     * 
     * <p>응답 생성을 위한 입력 데이터입니다.
     * 문자열 또는 메시지 배열 형태로 제공할 수 있습니다.</p>
     * 
     * @apiNote 메시지 배열 형태는 대화형 응답에 적합하고, 문자열은 단순한 텍스트 완성에 적합합니다.
     * @implNote 복잡한 상황이나 다단계 추론이 필요한 경우 메시지 배열을 권장합니다.
     */
    @JsonProperty("input")
    @Schema(
        description = "응답 생성 입력 (문자열 또는 메시지 배열)",
        required = true,
        example = """
            [
              {
                "role": "user",
                "content": "What is OpenAI's responses API"
              }
            ]
            """
    )
    private Object input; // String 또는 List<Object>
    
    /**
     * 시스템 지시사항
     * 
     * <p>AI의 역할과 응답 방식을 정의하는 지시사항입니다.
     * 응답의 톤, 스타일, 전문성 수준 등을 조절할 수 있습니다.</p>
     * 
     * @apiNote 명확하고 구체적인 지시사항일수록 원하는 결과를 얻기 쉽습니다.
     * @implNote 시스템 프롬프트는 전체 대화에 지속적으로 적용됩니다.
     */
    @JsonProperty("instructions")
    @Schema(
        description = "AI 시스템 지시사항 (역할, 톤, 스타일 정의)",
        example = "You are a helpful assistant.",
        maxLength = 2000
    )
    private String instructions;
    
    /**
     * 최대 출력 토큰 수
     * 
     * <p>생성할 응답의 최대 토큰 수를 제한합니다.
     * 긴 응답이 필요한 경우 충분히 높은 값을 설정하세요.</p>
     * 
     * @implNote 기본값: 4096, 최대값은 모델에 따라 다름
     * @apiNote 토큰 수가 많을수록 비용과 응답 시간이 증가합니다.
     */
    @JsonProperty("max_output_tokens")
    @Schema(
        description = "생성할 응답의 최대 토큰 수",
        example = "1000",
        minimum = "1"
    )
    private Integer maxOutputTokens;
    
    /**
     * 응답 온도 (창의성 조절)
     * 
     * <p>0.0에서 2.0 사이의 값으로, 응답의 창의성을 조절합니다.
     * 낮은 값은 일관성, 높은 값은 창의성을 강조합니다.</p>
     * 
     * @implNote 범위: 0.0 ~ 2.0, 기본값: 1.0
     * @apiNote 추론 작업에서는 0.1~0.3, 창의적 작업에서는 0.7~1.2를 권장합니다.
     */
    @JsonProperty("temperature")
    @Schema(
        description = "응답 온도 (0.0-2.0, 창의성 조절)",
        example = "0.9",
        minimum = "0.0",
        maximum = "2.0"
    )
    private Double temperature;
    
    /**
     * Top-P 샘플링
     * 
     * <p>누적 확률 기반 토큰 선택 방식입니다.
     * 0.0에서 1.0 사이의 값으로, 응답의 다양성을 조절합니다.</p>
     * 
     * @implNote 일반적으로 temperature와 함께 사용하지 않습니다.
     * @apiNote 정확한 답변이 필요한 경우 0.1~0.3, 창의적 응답이 필요한 경우 0.8~0.95를 권장합니다.
     */
    @JsonProperty("top_p")
    @Schema(
        description = "Top-P 샘플링 (0.0-1.0, 누적 확률 기반)",
        example = "0.9",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double topP;
    
    /**
     * 스트리밍 응답 여부
     * 
     * <p>실시간으로 응답을 스트리밍할지 여부를 결정합니다.
     * true로 설정하면 응답이 생성되는 대로 실시간으로 전송됩니다.</p>
     * 
     * @implNote 긴 응답에서 사용자 경험 향상에 도움이 됩니다.
     * @apiNote 스트리밍 모드에서는 추론 과정도 실시간으로 확인할 수 있습니다.
     */
    @JsonProperty("stream")
    @Schema(
        description = "스트리밍 응답 여부 (실시간 응답 전송)",
        example = "false"
    )
    private Object stream; // boolean 또는 string 형태로 올 수 있음
    
    /**
     * 응답 저장 여부
     * 
     * <p>생성된 응답을 시스템에 저장할지 여부를 결정합니다.
     * 저장된 응답은 나중에 조회하거나 분석에 활용할 수 있습니다.</p>
     * 
     * @implNote 중요한 응답이나 재사용이 필요한 경우 true로 설정합니다.
     * @apiNote 저장된 응답은 일정 기간 후 자동으로 삭제될 수 있습니다.
     */
    @JsonProperty("store")
    @Schema(
        description = "응답 저장 여부 (나중에 조회/분석 가능)",
        example = "true"
    )
    private Boolean store;
    
    /**
     * 메타데이터
     * 
     * <p>요청과 함께 저장할 추가 메타데이터입니다.
     * 분류, 태그, 사용자 정의 정보 등을 포함할 수 있습니다.</p>
     * 
     * @apiNote JSON 객체 형태로 자유롭게 구성할 수 있습니다.
     * @implNote 분석이나 추후 검색에 활용할 정보를 포함하세요.
     */
    @JsonProperty("metadata")
    @Schema(
        description = "요청 메타데이터 (사용자 정의 정보)",
        example = """
            {
              "category": "research",
              "priority": "high",
              "user_id": "user-123"
            }
            """
    )
    private Object metadata;
    
    /**
     * 사용자 식별자 (선택적)
     * 
     * <p>요청을 보낸 최종 사용자를 식별하는 고유 식별자입니다.
     * 모니터링, 남용 방지, 사용량 추적 등에 활용됩니다.</p>
     * 
     * @apiNote 개인정보 보호를 위해 해시값이나 익명화된 ID 사용을 권장합니다.
     */
    @JsonProperty("user")
    @Schema(
        description = "최종 사용자 식별자 (모니터링 및 남용 방지용)",
        example = "user-123",
        maxLength = 100
    )
    private String user;
}
