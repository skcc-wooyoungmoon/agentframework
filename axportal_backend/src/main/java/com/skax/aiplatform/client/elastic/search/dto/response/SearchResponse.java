package com.skax.aiplatform.client.elastic.search.dto.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenSearch/Elasticsearch 검색 응답 DTO
 * 
 * <p>OpenSearch와 Elasticsearch 검색 결과를 담는 공통 응답 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch 검색 응답 정보")
public class SearchResponse {

    /**
     * 검색 소요 시간 (밀리초)
     */
    @Schema(description = "검색 소요 시간", example = "8")
    private Integer took;

    /**
     * 타임아웃 여부
     */
    @Schema(description = "타임아웃 여부", example = "false")
    @com.fasterxml.jackson.annotation.JsonProperty("timed_out")
    private Boolean timedOut;

    /**
     * 샤드 정보
     */
    @Schema(description = "샤드 정보")
    @com.fasterxml.jackson.annotation.JsonProperty("_shards")
    private Map<String, Object> shards;

    /**
     * 검색 결과
     */
    @Schema(description = "검색 결과")
    private HitsContainer hits;

    /**
     * 검색 결과 컨테이너
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "검색 결과 컨테이너")
    public static class HitsContainer {
        
        /**
         * 전체 결과 수 정보
         */
        @Schema(description = "전체 결과 수 정보")
        private TotalHits total;

        /**
         * 최대 점수
         */
        @Schema(description = "최대 점수", example = "1.0")
        private Double maxScore;

        /**
         * 검색 결과 목록
         */
        @Schema(description = "검색 결과 목록")
        private List<Hit> hits;
    }

    /**
     * 전체 결과 수 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "전체 결과 수 정보")
    public static class TotalHits {
        
        /**
         * 결과 수
         */
        @Schema(description = "결과 수", example = "100")
        private Long value;

        /**
         * 결과 수 관계 (정확함/근사치)
         */
        @Schema(description = "결과 수 관계", example = "eq", allowableValues = {"eq", "gte"})
        private String relation;
    }

    /**
     * 개별 검색 결과
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 검색 결과")
    public static class Hit {
        
        /**
         * 인덱스명
         */
        @Schema(description = "인덱스명", example = "documents")
        @com.fasterxml.jackson.annotation.JsonProperty("_index")
        private String index;

        /**
         * 문서 ID
         */
        @Schema(description = "문서 ID", example = "doc_001")
        @com.fasterxml.jackson.annotation.JsonProperty("_id")
        private String id;

        /**
         * 점수
         */
        @Schema(description = "점수", example = "0.85")
        @com.fasterxml.jackson.annotation.JsonProperty("_score")
        private Double score;

        /**
         * 문서 소스
         */
        @Schema(description = "문서 소스")
        @com.fasterxml.jackson.annotation.JsonProperty("_source")
        private Map<String, Object> source;

        /**
         * 하이라이트
         */
        @Schema(description = "하이라이트")
        private Map<String, List<String>> highlight;
    }
}