package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway Embeddings 응답 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 텍스트 임베딩 변환 요청에 대한 응답 데이터 구조입니다.
 * 입력 텍스트들이 고차원 벡터로 변환된 결과와 관련 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: 임베딩 벡터 데이터 배열</li>
 *   <li><strong>model</strong>: 사용된 임베딩 모델</li>
 *   <li><strong>usage</strong>: 토큰 사용량 정보</li>
 *   <li><strong>object</strong>: 응답 타입 식별자</li>
 * </ul>
 * 
 * <h3>응답 특성:</h3>
 * <ul>
 *   <li>다중 텍스트 배치 처리 지원</li>
 *   <li>고차원 벡터 (일반적으로 1536차원)</li>
 *   <li>정규화된 벡터 값</li>
 *   <li>인덱스 기반 순서 보장</li>
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
    description = "SKTAI Model Gateway Embeddings 응답 정보",
    example = """
        {
          "object": "list",
          "data": [
            {
              "object": "embedding",
              "embedding": [0.0037623814, -0.0104302, -0.02287454],
              "index": 0
            },
            {
              "object": "embedding", 
              "embedding": [0.0023259174, 0.009064, -0.011702964],
              "index": 1
            }
          ],
          "model": "text-embedding-3-large",
          "usage": {
            "prompt_tokens": 9,
            "total_tokens": 9
          }
        }
        """
)
public class EmbeddingsResponse {
    
    /**
     * 응답 객체 타입
     * 
     * <p>이 응답이 리스트 형태임을 나타내는 식별자입니다.
     * 임베딩 응답의 경우 일반적으로 "list" 값을 가집니다.</p>
     */
    @JsonProperty("object")
    @Schema(description = "응답 객체 타입", example = "list")
    private String object;
    
    /**
     * 임베딩 데이터 목록
     * 
     * <p>입력 텍스트들에 대응하는 임베딩 벡터들의 배열입니다.
     * 각 요소는 하나의 텍스트에 대한 임베딩 정보를 포함합니다.</p>
     */
    @JsonProperty("data")
    @Schema(description = "임베딩 벡터 데이터 목록")
    private List<EmbeddingData> data;
    
    /**
     * 사용된 모델
     * 
     * <p>임베딩 생성에 사용된 모델의 식별자입니다.
     * 요청된 모델과 실제 사용된 모델이 다를 수 있습니다.</p>
     */
    @JsonProperty("model")
    @Schema(description = "사용된 임베딩 모델", example = "text-embedding-3-large")
    private String model;
    
    /**
     * 토큰 사용량 정보
     * 
     * <p>이 임베딩 요청에서 사용된 토큰의 정보입니다.
     * 비용 계산과 사용량 추적에 사용됩니다.</p>
     */
    @JsonProperty("usage")
    @Schema(description = "토큰 사용량 정보")
    private Usage usage;
    
    /**
     * 개별 임베딩 데이터 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 임베딩 벡터 정보")
    public static class EmbeddingData {
        
        /**
         * 데이터 객체 타입
         * 
         * <p>이 데이터가 임베딩 벡터임을 나타내는 타입 식별자입니다.
         * 일반적으로 "embedding" 값을 가집니다.</p>
         */
        @JsonProperty("object")
        @Schema(description = "데이터 객체 타입", example = "embedding")
        private String object;
        
        /**
         * 임베딩 벡터
         * 
         * <p>텍스트가 변환된 고차원 벡터입니다.
         * 일반적으로 부동소수점 수의 배열 형태로 제공됩니다.</p>
         * 
         * @apiNote 벡터 차원은 모델에 따라 다르며, 일반적으로 1536차원입니다.
         * @implNote 벡터는 정규화되어 있어 코사인 유사도 계산에 최적화되어 있습니다.
         */
        @JsonProperty("embedding")
        @Schema(
            description = "임베딩 벡터 (고차원 부동소수점 배열)",
            example = "[0.0037623814, -0.0104302, -0.02287454]"
        )
        private List<Double> embedding;
        
        /**
         * 인덱스
         * 
         * <p>이 임베딩이 입력 텍스트 배열에서 몇 번째 요소에 해당하는지를 나타냅니다.
         * 0부터 시작하며, 입력 순서와 출력 순서를 매칭할 때 사용됩니다.</p>
         */
        @JsonProperty("index")
        @Schema(description = "입력 텍스트 배열에서의 인덱스", example = "0")
        private Integer index;
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
         * <p>입력 텍스트들에서 사용된 토큰의 총 개수입니다.
         * 임베딩에서는 입력만 있으므로 이 값이 주요 비용 산정 기준입니다.</p>
         */
        @JsonProperty("prompt_tokens")
        @Schema(description = "입력 텍스트 토큰 수", example = "9")
        private Integer promptTokens;
        
        /**
         * 전체 토큰 수
         * 
         * <p>전체 사용된 토큰 수입니다.
         * 임베딩에서는 일반적으로 prompt_tokens와 동일한 값을 가집니다.</p>
         */
        @JsonProperty("total_tokens")
        @Schema(description = "총 사용 토큰 수", example = "9")
        private Integer totalTokens;
    }
}
