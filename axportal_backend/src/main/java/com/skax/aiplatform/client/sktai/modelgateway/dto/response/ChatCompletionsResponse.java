package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway Chat Completions 응답 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 채팅 완성 요청에 대한 응답 데이터 구조입니다.
 * AI 모델이 생성한 대화 응답과 관련 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>choices</strong>: 생성된 응답 선택지들</li>
 *   <li><strong>usage</strong>: 토큰 사용량 정보</li>
 *   <li><strong>model</strong>: 사용된 모델 정보</li>
 *   <li><strong>created</strong>: 응답 생성 시간</li>
 * </ul>
 * 
 * <h3>응답 특성:</h3>
 * <ul>
 *   <li>여러 응답 선택지 지원</li>
 *   <li>완료 이유 추적</li>
 *   <li>토큰 사용량 상세 정보</li>
 *   <li>메시지 형태의 구조화된 응답</li>
 * </ul>
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
    description = "SKTAI Model Gateway Chat Completions 응답 정보",
    example = """
        {
          "id": "chat-f1b7ae76b4a143e6967e3fd40008e00a",
          "object": "chat.completion",
          "created": 1727999548,
          "model": "gpt-4",
          "choices": [
            {
              "index": 0,
              "message": {
                "role": "assistant",
                "content": "안녕하세요! 저는 AI 어시스턴트입니다.",
                "tool_calls": []
              },
              "finish_reason": "stop"
            }
          ],
          "usage": {
            "prompt_tokens": 34,
            "completion_tokens": 60,
            "total_tokens": 94
          }
        }
        """
)
public class ChatCompletionsResponse {
    
    /**
     * 응답 고유 식별자
     * 
     * <p>이 채팅 완성 응답을 고유하게 식별하는 ID입니다.
     * 로깅, 디버깅, 추적 목적으로 사용됩니다.</p>
     */
    @JsonProperty("id")
    @Schema(description = "응답 고유 식별자", example = "chat-f1b7ae76b4a143e6967e3fd40008e00a")
    private String id;
    
    /**
     * 응답 객체 타입
     * 
     * <p>이 응답이 채팅 완성임을 나타내는 타입 식별자입니다.
     * 일반적으로 "chat.completion" 값을 가집니다.</p>
     */
    @JsonProperty("object")
    @Schema(description = "응답 객체 타입", example = "chat.completion")
    private String object;
    
    /**
     * 응답 생성 시간
     * 
     * <p>이 응답이 생성된 시간을 Unix 타임스탬프로 나타냅니다.
     * 응답 시간 추적과 성능 분석에 활용됩니다.</p>
     */
    @JsonProperty("created")
    @Schema(description = "응답 생성 시간 (Unix 타임스탬프)", example = "1727999548")
    private Long created;
    
    /**
     * 사용된 모델
     * 
     * <p>이 응답을 생성한 AI 모델의 식별자입니다.
     * 요청된 모델과 실제 사용된 모델이 다를 수 있습니다.</p>
     */
    @JsonProperty("model")
    @Schema(description = "사용된 AI 모델 식별자", example = "gpt-4")
    private String model;
    
    /**
     * 응답 선택지 목록
     * 
     * <p>AI가 생성한 응답의 선택지들입니다.
     * 여러 응답을 요청한 경우 복수의 선택지가 포함됩니다.</p>
     */
    @JsonProperty("choices")
    @Schema(description = "생성된 응답 선택지 목록")
    private List<Choice> choices;
    
    /**
     * 토큰 사용량 정보
     * 
     * <p>이 요청에서 사용된 토큰의 상세 정보입니다.
     * 비용 계산과 사용량 추적에 중요한 정보입니다.</p>
     */
    @JsonProperty("usage")
    @Schema(description = "토큰 사용량 정보")
    private Usage usage;
    
    /**
     * 시스템 지문 (선택적)
     * 
     * <p>응답 생성에 사용된 시스템의 지문 정보입니다.
     * 보안과 추적 목적으로 사용될 수 있습니다.</p>
     */
    @JsonProperty("system_fingerprint")
    @Schema(description = "시스템 지문", example = "fp_44709d6fcb")
    private String systemFingerprint;
    
    /**
     * 응답 선택지 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "응답 선택지 정보")
    public static class Choice {
        
        /**
         * 선택지 인덱스
         * 
         * <p>이 선택지의 순서를 나타내는 인덱스입니다.
         * 0부터 시작하며, 여러 응답 중 순서를 구분할 때 사용됩니다.</p>
         */
        @JsonProperty("index")
        @Schema(description = "선택지 인덱스", example = "0")
        private Integer index;
        
        /**
         * 응답 메시지
         * 
         * <p>AI가 생성한 실제 응답 메시지입니다.
         * 역할(role)과 내용(content)을 포함하는 구조화된 형태입니다.</p>
         */
        @JsonProperty("message")
        @Schema(description = "AI 응답 메시지")
        private Message message;
        
        /**
         * 완료 이유
         * 
         * <p>응답 생성이 완료된 이유를 나타냅니다.
         * 정상 완료, 길이 제한, 중지 시퀀스 등의 정보를 제공합니다.</p>
         */
        @JsonProperty("finish_reason")
        @Schema(
            description = "응답 완료 이유", 
            example = "stop",
            allowableValues = {"stop", "length", "function_call", "tool_calls", "content_filter"}
        )
        private String finishReason;
        
        /**
         * 로그 확률 정보 (선택적)
         * 
         * <p>각 토큰의 로그 확률 정보입니다.
         * 모델의 확신도나 대안 토큰 분석에 사용됩니다.</p>
         */
        @JsonProperty("logprobs")
        @Schema(description = "토큰별 로그 확률 정보")
        private Object logprobs;
    }
    
    /**
     * 메시지 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "AI 응답 메시지")
    public static class Message {
        
        /**
         * 메시지 역할
         * 
         * <p>이 메시지의 역할을 나타냅니다.
         * AI 응답의 경우 일반적으로 "assistant" 값을 가집니다.</p>
         */
        @JsonProperty("role")
        @Schema(description = "메시지 역할", example = "assistant")
        private String role;
        
        /**
         * 메시지 내용
         * 
         * <p>AI가 생성한 실제 텍스트 응답입니다.
         * 사용자의 질문에 대한 답변이나 대화 내용이 포함됩니다.</p>
         */
        @JsonProperty("content")
        @Schema(description = "메시지 내용", example = "안녕하세요! 저는 AI 어시스턴트입니다.")
        private String content;
        
        /**
         * 도구 호출 정보 (선택적)
         * 
         * <p>AI가 외부 도구나 함수를 호출한 경우의 정보입니다.
         * Function calling이나 tool use 기능에서 사용됩니다.</p>
         */
        @JsonProperty("tool_calls")
        @Schema(description = "도구 호출 정보")
        private List<Object> toolCalls;
        
        /**
         * 함수 호출 정보 (선택적, 레거시)
         * 
         * <p>이전 버전 호환성을 위한 함수 호출 정보입니다.
         * 새로운 구현에서는 tool_calls를 사용하는 것을 권장합니다.</p>
         */
        @JsonProperty("function_call")
        @Schema(description = "함수 호출 정보 (레거시)")
        private Object functionCall;
    }
    
    /**
     * 토큰 사용량 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "토큰 사용량 정보")
    public static class Usage {
        
        /**
         * 프롬프트 토큰 수
         * 
         * <p>입력 프롬프트에서 사용된 토큰의 개수입니다.
         * 사용자 메시지와 시스템 프롬프트를 포함합니다.</p>
         */
        @JsonProperty("prompt_tokens")
        @Schema(description = "입력 프롬프트 토큰 수", example = "34")
        private Integer promptTokens;
        
        /**
         * 완성 토큰 수
         * 
         * <p>AI가 생성한 응답에서 사용된 토큰의 개수입니다.
         * 실제 생성된 텍스트의 길이를 나타냅니다.</p>
         */
        @JsonProperty("completion_tokens")
        @Schema(description = "AI 응답 토큰 수", example = "60")
        private Integer completionTokens;
        
        /**
         * 전체 토큰 수
         * 
         * <p>입력과 출력을 합친 전체 토큰 수입니다.
         * 비용 계산의 기준이 되는 수치입니다.</p>
         */
        @JsonProperty("total_tokens")
        @Schema(description = "총 사용 토큰 수", example = "94")
        private Integer totalTokens;
        
        /**
         * 완성 토큰 상세 정보 (선택적)
         * 
         * <p>완성 토큰의 상세 분류 정보입니다.
         * 추론 토큰, 캐시된 토큰 등의 세부 정보를 포함할 수 있습니다.</p>
         */
        @JsonProperty("completion_tokens_details")
        @Schema(description = "완성 토큰 상세 정보")
        private Object completionTokensDetails;
        
        /**
         * 프롬프트 토큰 상세 정보 (선택적)
         * 
         * <p>프롬프트 토큰의 상세 분류 정보입니다.
         * 캐시된 토큰 등의 세부 정보를 포함할 수 있습니다.</p>
         */
        @JsonProperty("prompt_tokens_details")
        @Schema(description = "프롬프트 토큰 상세 정보")
        private Object promptTokensDetails;
    }
}
