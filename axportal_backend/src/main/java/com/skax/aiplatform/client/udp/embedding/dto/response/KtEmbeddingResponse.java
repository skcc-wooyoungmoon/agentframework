package com.skax.aiplatform.client.udp.embedding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * KT 임베딩 추론 응답 DTO
 * 
 * <p>KT 임베딩 모델의 추론 결과를 담는 응답 데이터 구조입니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>임베딩 벡터</strong>: 각 텍스트에 대한 벡터 표현</li>
 *   <li><strong>처리 정보</strong>: 토큰 수, 처리 시간 등</li>
 *   <li><strong>모델 정보</strong>: 사용된 모델 및 버전</li>
 *   <li><strong>메타데이터</strong>: 추가적인 처리 정보</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "KT 임베딩 추론 응답 정보",
    example = """
        {
          "embeddings": [
            {
              "index": 0,
              "embedding": [0.1, -0.2, 0.3, 0.4],
              "text": "안녕하세요, 신한은행입니다.",
              "tokenCount": 8
            }
          ],
          "model": "kt-embedding-v1",
          "dimension": 768,
          "processingTimeMs": 150,
          "totalTokens": 16,
          "normalized": true
        }
        """
)
public class KtEmbeddingResponse {

    /**
     * 임베딩 결과 목록
     * 
     * <p>요청된 각 텍스트에 대한 임베딩 벡터와 메타데이터입니다.</p>
     */
    @Schema(
        description = "임베딩 결과 목록",
        example = """
            [
              {
                "index": 0,
                "embedding": [0.1, -0.2, 0.3, 0.4],
                "text": "안녕하세요, 신한은행입니다.",
                "tokenCount": 8
              }
            ]
            """
    )
    private List<EmbeddingResult> embeddings;

    /**
     * 사용된 모델 이름
     */
    @Schema(
        description = "사용된 KT 임베딩 모델 이름",
        example = "kt-embedding-v1"
    )
    private String model;

    /**
     * 임베딩 벡터 차원
     * 
     * <p>생성된 임베딩 벡터의 차원 수입니다.</p>
     */
    @Schema(
        description = "임베딩 벡터 차원 수",
        example = "768"
    )
    private Integer dimension;

    /**
     * 처리 시간 (밀리초)
     * 
     * <p>임베딩 생성에 소요된 시간입니다.</p>
     */
    @Schema(
        description = "임베딩 생성 소요 시간 (밀리초)",
        example = "150"
    )
    private Long processingTimeMs;

    /**
     * 총 토큰 수
     * 
     * <p>모든 텍스트에서 처리된 총 토큰 수입니다.</p>
     */
    @Schema(
        description = "처리된 총 토큰 수",
        example = "16"
    )
    private Integer totalTokens;

    /**
     * 정규화 적용 여부
     * 
     * <p>반환된 벡터가 정규화되었는지 여부입니다.</p>
     */
    @Schema(
        description = "벡터 정규화 적용 여부",
        example = "true"
    )
    private Boolean normalized;

    /**
     * 요청 ID
     * 
     * <p>요청 시 제공된 추적 ID입니다.</p>
     */
    @Schema(
        description = "요청 추적 ID",
        example = "req_12345"
    )
    private String requestId;

    /**
     * 모델 버전
     * 
     * <p>사용된 모델의 버전 정보입니다.</p>
     */
    @Schema(
        description = "모델 버전",
        example = "1.2.0"
    )
    private String modelVersion;

    /**
     * 추가 메타데이터
     * 
     * <p>추가적인 처리 정보나 모델 관련 메타데이터입니다.</p>
     */
    @Schema(
        description = "추가 메타데이터",
        example = """
            {
              "encoding": "utf-8",
              "truncated": false,
              "performance": {
                "tokensPerSecond": 107
              }
            }
            """
    )
    private Map<String, Object> metadata;

    /**
     * 개별 임베딩 결과 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 임베딩 결과")
    public static class EmbeddingResult {

        /**
         * 텍스트 인덱스
         * 
         * <p>요청 시 텍스트 배열에서의 순서입니다.</p>
         */
        @Schema(
            description = "텍스트 순서 인덱스",
            example = "0"
        )
        private Integer index;

        /**
         * 임베딩 벡터
         * 
         * <p>텍스트를 벡터로 변환한 결과입니다.</p>
         */
        @Schema(
            description = "임베딩 벡터 (실수 배열)",
            example = "[0.1, -0.2, 0.3, 0.4, ...]"
        )
        private List<Double> embedding;

        /**
         * 원본 텍스트
         * 
         * <p>임베딩이 생성된 원본 텍스트입니다.</p>
         */
        @Schema(
            description = "원본 텍스트",
            example = "안녕하세요, 신한은행입니다."
        )
        private String text;

        /**
         * 토큰 수
         * 
         * <p>해당 텍스트에서 처리된 토큰 수입니다.</p>
         */
        @Schema(
            description = "텍스트의 토큰 수",
            example = "8"
        )
        private Integer tokenCount;

        /**
         * 텍스트 잘림 여부
         * 
         * <p>최대 토큰 수 제한으로 인해 텍스트가 잘렸는지 여부입니다.</p>
         */
        @Schema(
            description = "텍스트 잘림 여부",
            example = "false"
        )
        private Boolean truncated;

        /**
         * 벡터 노름
         * 
         * <p>정규화 전 벡터의 L2 노름 값입니다.</p>
         */
        @Schema(
            description = "벡터 L2 노름 (정규화 전)",
            example = "12.34"
        )
        private Double norm;
    }
}