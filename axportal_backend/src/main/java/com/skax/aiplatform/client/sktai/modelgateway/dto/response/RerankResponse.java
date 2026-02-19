package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 문서 리랭킹 응답 DTO
 * 
 * <p>검색 결과 문서들의 관련성 순서를 재조정한 결과를 담는 구조입니다.
 * 검색 엔진의 정확도 향상과 사용자 만족도 개선에 활용됩니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>results</strong>: 리랭킹된 문서 순서</li>
 *   <li><strong>model</strong>: 사용된 리랭킹 모델</li>
 *   <li><strong>query</strong>: 원본 검색 쿼리</li>
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
    description = "SKTAI 문서 리랭킹 응답 정보",
    example = """
        {
          "results": [
            {
              "index": 2,
              "relevance_score": 0.92,
              "document": {
                "title": "관련 문서 제목",
                "content": "문서 내용..."
              }
            }
          ],
          "model": "rerank-model-v1",
          "query": "사용자 검색 쿼리",
          "total_documents": 10
        }
        """
)
public class RerankResponse {
    
    /**
     * 리랭킹된 문서 결과들
     */
    @JsonProperty("results")
    @Schema(description = "리랭킹된 문서 결과들 (관련성 순)")
    private List<RerankResult> results;
    
    /**
     * 사용된 리랭킹 모델
     */
    @JsonProperty("model")
    @Schema(description = "사용된 리랭킹 모델명", example = "rerank-model-v1")
    private String model;
    
    /**
     * 원본 검색 쿼리
     */
    @JsonProperty("query")
    @Schema(description = "원본 검색 쿼리", example = "사용자 검색 쿼리")
    private String query;
    
    /**
     * 총 문서 수
     */
    @JsonProperty("total_documents")
    @Schema(description = "처리된 총 문서 수", example = "10")
    private Integer totalDocuments;
    
    /**
     * 처리 시간 (밀리초)
     */
    @JsonProperty("processing_time_ms")
    @Schema(description = "리랭킹 처리 시간 (밀리초)", example = "200")
    private Long processingTimeMs;
    
    /**
     * 리랭킹 결과 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "리랭킹 결과 항목")
    public static class RerankResult {
        
        /**
         * 원본 문서 인덱스
         */
        @JsonProperty("index")
        @Schema(description = "원본 문서 인덱스", example = "2")
        private Integer index;
        
        /**
         * 관련성 점수 (0.0 ~ 1.0)
         */
        @JsonProperty("relevance_score")
        @Schema(description = "관련성 점수 (0.0 ~ 1.0)", example = "0.92")
        private Double relevanceScore;
        
        /**
         * 리랭킹된 순위
         */
        @JsonProperty("rank")
        @Schema(description = "새로운 순위 (1부터 시작)", example = "1")
        private Integer rank;
        
        /**
         * 문서 정보
         */
        @JsonProperty("document")
        @Schema(description = "문서 정보")
        private Document document;
    }
    
    /**
     * 문서 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "문서 정보")
    public static class Document {
        
        /**
         * 문서 제목
         */
        @JsonProperty("title")
        @Schema(description = "문서 제목", example = "관련 문서 제목")
        private String title;
        
        /**
         * 문서 내용
         */
        @JsonProperty("content")
        @Schema(description = "문서 내용", example = "문서 내용...")
        private String content;
        
        /**
         * 문서 URL (선택적)
         */
        @JsonProperty("url")
        @Schema(description = "문서 URL", example = "https://example.com/doc")
        private String url;
        
        /**
         * 문서 메타데이터
         */
        @JsonProperty("metadata")
        @Schema(description = "문서 메타데이터")
        private Object metadata;
    }
}
