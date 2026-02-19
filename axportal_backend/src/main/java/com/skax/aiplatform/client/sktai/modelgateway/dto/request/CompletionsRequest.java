package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 텍스트 완성 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 텍스트 완성(Completion)을 위한 요청 데이터 구조입니다.
 * 주어진 프롬프트를 기반으로 후속 텍스트를 생성하는 기본적인 언어 모델 기능을 제공합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>prompt</strong>: 완성할 텍스트의 시작 부분</li>
 *   <li><strong>model</strong>: 사용할 언어 모델 식별자</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>텍스트 자동 완성</li>
 *   <li>창의적 글쓰기 지원</li>
 *   <li>코드 완성</li>
 *   <li>스트리밍 응답</li>
 *   <li>다양한 생성 파라미터 조절</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CompletionsRequest request = CompletionsRequest.builder()
 *     .model("text-davinci-003")
 *     .prompt("Write a short story about a robot.")
 *     .maxTokens(200)
 *     .temperature(0.7)
 *     .topP(0.85)
 *     .stream(true)
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
    description = "SKTAI Model Gateway 텍스트 완성 요청 정보",
    example = """
        {
          "model": "text-davinci-003",
          "prompt": "Write a short story about a robot.",
          "max_tokens": 200,
          "temperature": 0.7,
          "top_p": 0.85,
          "frequency_penalty": 0.2,
          "presence_penalty": 0.3,
          "stream": true
        }
        """
)
public class CompletionsRequest {
    
    /**
     * 완성할 프롬프트
     * 
     * <p>텍스트 완성의 시작점이 되는 프롬프트입니다.
     * 모델은 이 프롬프트를 기반으로 후속 텍스트를 생성합니다.</p>
     * 
     * @apiNote 명확하고 구체적인 프롬프트일수록 원하는 결과를 얻기 쉽습니다.
     * @implNote 최대 4000자까지 지원하며, 컨텍스트와 지시사항을 포함할 수 있습니다.
     */
    @JsonProperty("prompt")
    @Schema(
        description = "완성할 텍스트의 시작 프롬프트",
        example = "Write a short story about a robot.",
        required = true,
        maxLength = 4000
    )
    private String prompt;
    
    /**
     * 언어 모델 식별자
     * 
     * <p>텍스트 완성에 사용할 언어 모델의 이름입니다.
     * 각 모델마다 성능, 속도, 특화 분야가 다릅니다.</p>
     * 
     * @implNote 지원 모델: text-davinci-003, gpt-3.5-turbo-instruct 등
     * @apiNote davinci 시리즈는 높은 품질의 완성을 제공하지만 처리 시간이 길 수 있습니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 언어 모델 식별자",
        example = "text-davinci-003",
        required = true
    )
    private String model;
    
    /**
     * 최대 토큰 수
     * 
     * <p>생성할 완성 텍스트의 최대 토큰 수를 제한합니다.
     * 적절한 길이의 응답을 얻기 위해 조절하세요.</p>
     * 
     * @implNote 기본값: 16, 최대값은 모델에 따라 다름
     * @apiNote 토큰 수가 많을수록 비용과 응답 시간이 증가합니다.
     */
    @JsonProperty("max_tokens")
    @Schema(
        description = "생성할 완성 텍스트의 최대 토큰 수",
        example = "200",
        minimum = "1"
    )
    private Integer maxTokens;
    
    /**
     * 응답 온도 (창의성 조절)
     * 
     * <p>0.0에서 2.0 사이의 값으로, 텍스트 생성의 창의성을 조절합니다.
     * 낮은 값은 예측 가능한 텍스트, 높은 값은 창의적인 텍스트를 생성합니다.</p>
     * 
     * @implNote 범위: 0.0 ~ 2.0, 기본값: 1.0
     * @apiNote 사실적 내용에는 0.1~0.3, 창의적 글쓰기에는 0.7~1.2를 권장합니다.
     */
    @JsonProperty("temperature")
    @Schema(
        description = "응답 온도 (0.0-2.0, 창의성 조절)",
        example = "0.7",
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
     * @apiNote 다양한 표현을 원할 때는 0.8~0.95, 일관성을 원할 때는 0.1~0.3을 권장합니다.
     */
    @JsonProperty("top_p")
    @Schema(
        description = "Top-P 샘플링 (0.0-1.0, 누적 확률 기반)",
        example = "0.85",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double topP;
    
    /**
     * 생성할 완성 개수
     * 
     * <p>하나의 프롬프트에 대해 생성할 완성 변형의 개수입니다.
     * 여러 옵션을 비교하거나 다양한 결과를 원할 때 사용합니다.</p>
     * 
     * @implNote 기본값: 1, 최대값: 제한적
     * @apiNote 개수가 많을수록 비용이 배수로 증가합니다.
     */
    @JsonProperty("n")
    @Schema(
        description = "생성할 완성 개수 (여러 변형 생성)",
        example = "1",
        minimum = "1",
        maximum = "10"
    )
    private Integer n;
    
    /**
     * 스트리밍 응답 여부
     * 
     * <p>실시간으로 완성 텍스트를 스트리밍할지 여부를 결정합니다.
     * true로 설정하면 텍스트가 생성되는 대로 실시간으로 전송됩니다.</p>
     * 
     * @implNote 긴 텍스트 생성 시 사용자 경험 향상에 도움이 됩니다.
     * @apiNote 스트리밍 모드에서는 부분적 결과를 실시간으로 확인할 수 있습니다.
     */
    @JsonProperty("stream")
    @Schema(
        description = "스트리밍 응답 여부 (실시간 텍스트 전송)",
        example = "true"
    )
    private Object stream; // boolean 또는 string 형태로 올 수 있음
    
    /**
     * 로그 확률 포함 개수
     * 
     * <p>각 토큰의 로그 확률 정보를 응답에 포함할 개수입니다.
     * 모델의 확신도나 대안 토큰을 분석할 때 유용합니다.</p>
     * 
     * @implNote 범위: 0~5, 0은 로그 확률을 포함하지 않음
     * @apiNote 디버깅이나 모델 분석 목적으로 주로 사용됩니다.
     */
    @JsonProperty("logprobs")
    @Schema(
        description = "포함할 로그 확률 개수 (0-5, 모델 분석용)",
        example = "0",
        minimum = "0",
        maximum = "5"
    )
    private Integer logprobs;
    
    /**
     * 응답에 프롬프트 포함 여부
     * 
     * <p>생성된 완성 텍스트에 원본 프롬프트를 포함할지 여부입니다.
     * true로 설정하면 프롬프트와 완성이 연결된 전체 텍스트를 반환합니다.</p>
     * 
     * @implNote 기본값: false
     * @apiNote 전체 맥락을 확인하고 싶을 때 유용합니다.
     */
    @JsonProperty("echo")
    @Schema(
        description = "응답에 원본 프롬프트 포함 여부",
        example = "false"
    )
    private Boolean echo;
    
    /**
     * 정지 시퀀스
     * 
     * <p>텍스트 생성을 중단할 문자열 패턴들입니다.
     * 이 패턴 중 하나가 생성되면 완성이 즉시 중단됩니다.</p>
     * 
     * @apiNote 문장 끝을 명확히 하거나 특정 형식을 유지할 때 유용합니다.
     * @implNote 최대 4개까지 지정 가능하며, 각각 최대 10자까지 지원합니다.
     */
    @JsonProperty("stop")
    @Schema(
        description = "텍스트 생성 중단 시퀀스 (최대 4개)",
        example = "[\"\\n\", \".\"]"
    )
    private Object stop; // String 또는 List<String>
    
    /**
     * 빈도 페널티
     * 
     * <p>-2.0에서 2.0 사이의 값으로, 이미 사용된 토큰의 재사용을 억제합니다.
     * 양수 값은 반복을 줄이고, 음수 값은 반복을 늘립니다.</p>
     * 
     * @implNote 범위: -2.0 ~ 2.0, 기본값: 0.0
     * @apiNote 반복적인 내용을 피하고 싶을 때 0.1~0.5 값을 권장합니다.
     */
    @JsonProperty("frequency_penalty")
    @Schema(
        description = "빈도 페널티 (-2.0 ~ 2.0, 토큰 반복 억제)",
        example = "0.2",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double frequencyPenalty;
    
    /**
     * 존재 페널티
     * 
     * <p>-2.0에서 2.0 사이의 값으로, 새로운 주제 도입을 장려합니다.
     * 양수 값은 다양한 주제를 유도하고, 음수 값은 집중된 주제를 유도합니다.</p>
     * 
     * @implNote 범위: -2.0 ~ 2.0, 기본값: 0.0
     * @apiNote 주제의 다양성을 원할 때 0.1~0.5 값을 권장합니다.
     */
    @JsonProperty("presence_penalty")
    @Schema(
        description = "존재 페널티 (-2.0 ~ 2.0, 새로운 주제 도입 장려)",
        example = "0.3",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double presencePenalty;
    
    /**
     * 최선 완성 개수
     * 
     * <p>서버 측에서 생성할 완성 후보의 개수입니다.
     * 이 중에서 가장 좋은 n개를 선택하여 반환합니다.</p>
     * 
     * @implNote n보다 크거나 같아야 하며, 품질 향상을 위해 사용됩니다.
     * @apiNote 값이 클수록 더 나은 결과를 얻을 수 있지만 비용이 증가합니다.
     */
    @JsonProperty("best_of")
    @Schema(
        description = "생성할 후보 완성 개수 (품질 향상용)",
        example = "1",
        minimum = "1"
    )
    private Integer bestOf;
    
    /**
     * 로그 확률 바이어스
     * 
     * <p>특정 토큰의 선택 확률을 조절하는 바이어스 맵입니다.
     * 토큰 ID를 키로, 바이어스 값을 값으로 하는 객체입니다.</p>
     * 
     * @apiNote 특정 단어의 사용을 장려하거나 억제할 때 사용합니다.
     * @implNote 바이어스 범위: -100 ~ 100
     */
    @JsonProperty("logit_bias")
    @Schema(
        description = "토큰별 로그 확률 바이어스 (토큰 선택 확률 조절)",
        example = """
            {
              "50256": -100,
              "1234": 10
            }
            """
    )
    private Object logitBias;
    
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
