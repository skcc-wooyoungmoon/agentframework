package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway Chat Completions 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 채팅 완성 요청을 위한 데이터 구조입니다.
 * 대화형 AI 모델과의 상호작용을 통해 지능적인 응답을 생성합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>messages</strong>: 대화 메시지 배열 (시스템, 사용자, 어시스턴트 역할)</li>
 *   <li><strong>model</strong>: 사용할 AI 모델 식별자</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>다양한 AI 모델 지원 (GPT-4, Claude 등)</li>
 *   <li>스트리밍 응답 지원</li>
 *   <li>토큰 수 제한 및 온도 조절</li>
 *   <li>빈도/존재 페널티 적용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ChatCompletionsRequest request = ChatCompletionsRequest.builder()
 *     .model("gpt-4")
 *     .messages(Arrays.asList(
 *         new ChatMessage("system", "You are a helpful assistant."),
 *         new ChatMessage("user", "Hello, how are you?")
 *     ))
 *     .temperature(0.7)
 *     .maxTokens(100)
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
    description = "SKTAI Model Gateway Chat Completions 요청 정보",
    example = """
        {
          "model": "gpt-4",
          "messages": [
            {
              "role": "system",
              "content": "You are a helpful assistant."
            },
            {
              "role": "user", 
              "content": "Who is New Jeans?"
            }
          ],
          "temperature": 0.7,
          "max_tokens": 100,
          "top_p": 0.9,
          "frequency_penalty": 0.2,
          "presence_penalty": 0.5,
          "stream": false
        }
        """
)
public class ChatCompletionsRequest {
    
    /**
     * 대화 메시지 배열
     * 
     * <p>시스템 프롬프트, 사용자 입력, 어시스턴트 응답을 포함한 대화 히스토리입니다.
     * 각 메시지는 역할(role)과 내용(content)을 포함합니다.</p>
     * 
     * @apiNote 최소 1개 이상의 메시지가 필요하며, 일반적으로 시스템 메시지로 시작합니다.
     */
    @JsonProperty("messages")
    @Schema(
        description = "대화 메시지 배열 (시스템, 사용자, 어시스턴트 역할 포함)",
        required = true,
        example = """
            [
              {
                "role": "system",
                "content": "You are a helpful assistant."
              },
              {
                "role": "user",
                "content": "Hello!"
              }
            ]
            """
    )
    private List<Object> messages;
    
    /**
     * AI 모델 식별자
     * 
     * <p>사용할 AI 모델의 이름 또는 식별자입니다.
     * 각 모델마다 고유한 특성과 성능을 가집니다.</p>
     * 
     * @implNote 지원되는 모델: gpt-4, gpt-3.5-turbo, claude-3 등
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 AI 모델 식별자",
        example = "gpt-4",
        required = true
    )
    private String model;
    
    /**
     * 응답 온도 (창의성 조절)
     * 
     * <p>0.0에서 2.0 사이의 값으로, 응답의 창의성을 조절합니다.
     * 낮은 값은 일관성 있는 응답, 높은 값은 창의적인 응답을 생성합니다.</p>
     * 
     * @implNote 기본값: 1.0, 범위: 0.0 ~ 2.0
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
     * 최대 토큰 수
     * 
     * <p>생성할 응답의 최대 토큰 수를 제한합니다.
     * 토큰은 단어나 문자의 단위입니다.</p>
     * 
     * @apiNote 모델별로 최대 허용 토큰 수가 다릅니다.
     */
    @JsonProperty("max_tokens")
    @Schema(
        description = "생성할 응답의 최대 토큰 수",
        example = "100",
        minimum = "1"
    )
    private Integer maxTokens;
    
    /**
     * Top-P 샘플링
     * 
     * <p>누적 확률 기반 토큰 선택 방식입니다.
     * 0.0에서 1.0 사이의 값으로, 더 다양한 응답을 위해 사용합니다.</p>
     * 
     * @implNote 일반적으로 temperature와 함께 사용하지 않습니다.
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
     * 빈도 페널티
     * 
     * <p>-2.0에서 2.0 사이의 값으로, 이미 사용된 토큰의 재사용을 억제합니다.
     * 양수 값은 반복을 줄이고, 음수 값은 반복을 늘립니다.</p>
     * 
     * @implNote 창의적인 응답을 위해 양수 값을 권장합니다.
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
     * @implNote 주제의 다양성을 원할 때 양수 값을 사용합니다.
     */
    @JsonProperty("presence_penalty")
    @Schema(
        description = "존재 페널티 (-2.0 ~ 2.0, 새로운 주제 도입 장려)",
        example = "0.5",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double presencePenalty;
    
    /**
     * 스트리밍 응답 여부
     * 
     * <p>실시간으로 응답을 스트리밍할지 여부를 결정합니다.
     * true로 설정하면 토큰이 생성되는 대로 실시간으로 전송됩니다.</p>
     * 
     * @implNote 긴 응답에서 사용자 경험 향상을 위해 사용합니다.
     */
    @JsonProperty("stream")
    @Schema(
        description = "스트리밍 응답 여부 (실시간 응답 전송)",
        example = "false"
    )
    private Object stream; // boolean 또는 string 형태로 올 수 있음
}
