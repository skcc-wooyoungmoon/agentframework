package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 텍스트 완성 응답 DTO
 * 
 * <p>텍스트 완성 API의 응답 데이터를 담는 구조입니다.
 * GPT 스타일의 텍스트 생성 결과를 포함하며, 여러 완성 옵션을 제공할 수 있습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>choices</strong>: 생성된 텍스트 완성 옵션들</li>
 *   <li><strong>usage</strong>: 토큰 사용량 정보</li>
 *   <li><strong>model</strong>: 사용된 모델 정보</li>
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
    description = "SKTAI 텍스트 완성 응답 정보",
    example = """
        {
          "id": "cmpl-abc123",
          "object": "text_completion",
          "choices": [
            {
              "text": "완성된 텍스트 내용",
              "index": 0,
              "finish_reason": "stop"
            }
          ],
          "usage": {
            "prompt_tokens": 10,
            "completion_tokens": 20,
            "total_tokens": 30
          },
          "model": "gpt-3.5-turbo",
          "created": 1690000000
        }
        """
)
public class CompletionsResponse {
    
    /**
     * 완성 응답 고유 식별자
     */
    @JsonProperty("id")
    @Schema(description = "완성 응답 고유 식별자", example = "cmpl-abc123")
    private String id;
    
    /**
     * 응답 객체 타입
     */
    @JsonProperty("object")
    @Schema(description = "응답 객체 타입", example = "text_completion")
    private String object;
    
    /**
     * 생성된 완성 텍스트 옵션들
     */
    @JsonProperty("choices")
    @Schema(description = "생성된 완성 텍스트 옵션들")
    private List<Choice> choices;
    
    /**
     * 토큰 사용량 정보
     */
    @JsonProperty("usage")
    @Schema(description = "토큰 사용량 정보")
    private Usage usage;
    
    /**
     * 사용된 모델명
     */
    @JsonProperty("model")
    @Schema(description = "사용된 모델명", example = "gpt-3.5-turbo")
    private String model;
    
    /**
     * 응답 생성 시간 (Unix 타임스탬프)
     */
    @JsonProperty("created")
    @Schema(description = "응답 생성 시간 (Unix 타임스탬프)", example = "1690000000")
    private Long created;
    
    /**
     * 완성 텍스트 선택 옵션
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "완성 텍스트 선택 옵션")
    public static class Choice {
        
        /**
         * 생성된 완성 텍스트
         */
        @JsonProperty("text")
        @Schema(description = "생성된 완성 텍스트", example = "완성된 텍스트 내용")
        private String text;
        
        /**
         * 선택 옵션 인덱스
         */
        @JsonProperty("index")
        @Schema(description = "선택 옵션 인덱스", example = "0")
        private Integer index;
        
        /**
         * 완성 종료 이유
         */
        @JsonProperty("finish_reason")
        @Schema(description = "완성 종료 이유 (stop, length, content_filter 등)", example = "stop")
        private String finishReason;
        
        /**
         * 로그 확률 (선택적)
         */
        @JsonProperty("logprobs")
        @Schema(description = "토큰별 로그 확률 정보")
        private Object logprobs;
    }
    
    /**
     * 토큰 사용량 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "토큰 사용량 정보")
    public static class Usage {
        
        /**
         * 프롬프트 토큰 수
         */
        @JsonProperty("prompt_tokens")
        @Schema(description = "프롬프트 토큰 수", example = "10")
        private Integer promptTokens;
        
        /**
         * 완성 토큰 수
         */
        @JsonProperty("completion_tokens")
        @Schema(description = "완성 토큰 수", example = "20")
        private Integer completionTokens;
        
        /**
         * 총 토큰 수
         */
        @JsonProperty("total_tokens")
        @Schema(description = "총 토큰 수", example = "30")
        private Integer totalTokens;
    }
}
