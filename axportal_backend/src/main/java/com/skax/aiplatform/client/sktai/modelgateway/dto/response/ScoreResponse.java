package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 텍스트 유사도 스코어링 응답 DTO
 * 
 * <p>텍스트 간 유사도 점수를 계산한 결과를 담는 구조입니다.
 * 검색 엔진의 랭킹, 문서 분류, 중복 탐지 등에 활용됩니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>scores</strong>: 계산된 유사도 점수들</li>
 *   <li><strong>model</strong>: 사용된 스코어링 모델</li>
 *   <li><strong>usage</strong>: 계산 비용 정보</li>
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
    description = "SKTAI 텍스트 유사도 스코어링 응답 정보",
    example = """
        {
          "scores": [
            {
              "index": 0,
              "score": 0.85,
              "text": "비교 대상 텍스트"
            }
          ],
          "model": "similarity-model-v1",
          "usage": {
            "total_tokens": 50
          }
        }
        """
)
public class ScoreResponse {
    
    /**
     * 계산된 유사도 점수들
     */
    @JsonProperty("scores")
    @Schema(description = "계산된 유사도 점수들")
    private List<SimilarityScore> scores;
    
    /**
     * 사용된 스코어링 모델
     */
    @JsonProperty("model")
    @Schema(description = "사용된 스코어링 모델명", example = "similarity-model-v1")
    private String model;
    
    /**
     * 계산 비용 정보
     */
    @JsonProperty("usage")
    @Schema(description = "계산 비용 정보")
    private Usage usage;
    
    /**
     * 유사도 점수 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "유사도 점수 항목")
    public static class SimilarityScore {
        
        /**
         * 텍스트 인덱스
         */
        @JsonProperty("index")
        @Schema(description = "텍스트 인덱스", example = "0")
        private Integer index;
        
        /**
         * 유사도 점수 (0.0 ~ 1.0)
         */
        @JsonProperty("score")
        @Schema(description = "유사도 점수 (0.0 ~ 1.0)", example = "0.85")
        private Double score;
        
        /**
         * 비교 대상 텍스트
         */
        @JsonProperty("text")
        @Schema(description = "비교 대상 텍스트", example = "비교 대상 텍스트")
        private String text;
        
        /**
         * 추가 메타데이터
         */
        @JsonProperty("metadata")
        @Schema(description = "추가 메타데이터")
        private Object metadata;
    }
    
    /**
     * 계산 비용 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "계산 비용 정보")
    public static class Usage {
        
        /**
         * 총 사용 토큰 수
         */
        @JsonProperty("total_tokens")
        @Schema(description = "총 사용 토큰 수", example = "50")
        private Integer totalTokens;
        
        /**
         * 계산 시간 (밀리초)
         */
        @JsonProperty("processing_time_ms")
        @Schema(description = "계산 시간 (밀리초)", example = "150")
        private Long processingTimeMs;
    }
}
