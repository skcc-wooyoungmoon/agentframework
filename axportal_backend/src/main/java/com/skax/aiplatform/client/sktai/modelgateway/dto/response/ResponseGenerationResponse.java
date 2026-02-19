package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 응답 생성 결과 DTO
 * 
 * <p>고급 응답 생성 API의 결과를 담는 구조입니다.
 * 복잡한 요청에 대한 맞춤형 응답을 생성하고 관련 메타데이터를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>response</strong>: 생성된 응답 내용</li>
 *   <li><strong>confidence</strong>: 응답 신뢰도</li>
 *   <li><strong>processing_info</strong>: 처리 정보</li>
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
    description = "SKTAI 응답 생성 결과 정보",
    example = """
        {
          "response": "생성된 응답 내용",
          "confidence": 0.88,
          "processing_info": {
            "model": "response-gen-v1",
            "processing_time_ms": 500,
            "tokens_used": 75
          },
          "metadata": {
            "request_id": "req-123",
            "timestamp": "2025-08-15T10:30:00Z"
          }
        }
        """
)
public class ResponseGenerationResponse {
    
    /**
     * 생성된 응답 내용
     */
    @JsonProperty("response")
    @Schema(description = "생성된 응답 내용", example = "생성된 응답 내용")
    private String response;
    
    /**
     * 응답 신뢰도 (0.0 ~ 1.0)
     */
    @JsonProperty("confidence")
    @Schema(description = "응답 신뢰도 (0.0 ~ 1.0)", example = "0.88")
    private Double confidence;
    
    /**
     * 처리 정보
     */
    @JsonProperty("processing_info")
    @Schema(description = "응답 생성 처리 정보")
    private ProcessingInfo processingInfo;
    
    /**
     * 추가 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Object metadata;
    
    /**
     * 응답 생성 처리 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "응답 생성 처리 정보")
    public static class ProcessingInfo {
        
        /**
         * 사용된 모델명
         */
        @JsonProperty("model")
        @Schema(description = "사용된 모델명", example = "response-gen-v1")
        private String model;
        
        /**
         * 처리 시간 (밀리초)
         */
        @JsonProperty("processing_time_ms")
        @Schema(description = "처리 시간 (밀리초)", example = "500")
        private Long processingTimeMs;
        
        /**
         * 사용된 토큰 수
         */
        @JsonProperty("tokens_used")
        @Schema(description = "사용된 토큰 수", example = "75")
        private Integer tokensUsed;
        
        /**
         * 요청 복잡도 점수
         */
        @JsonProperty("complexity_score")
        @Schema(description = "요청 복잡도 점수 (1-10)", example = "6")
        private Integer complexityScore;
    }
}
