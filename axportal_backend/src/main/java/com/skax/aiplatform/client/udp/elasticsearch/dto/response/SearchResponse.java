package com.skax.aiplatform.client.udp.elasticsearch.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 데이터 검색 응답 DTO
 *
 * 
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Elasticsearch 데이터 검색 응답")
public class SearchResponse {

    /**
     * 검색 수행 시간 (밀리초)
     */
    @Schema(description = "검색 수행 시간 (밀리초)", example = "15")
    private Long took;

    /**
     * 검색 결과 정보
     */
    @Schema(description = "검색 결과 정보")
    private HitsInfo hits;

    /**
     * Aggregation 결과
     */
    @Schema(description = "Aggregation 결과")
    private Map<String, Object> aggregations;

    // 편의 메서드들
    public List<Map<String, Object>> getHits() {
        if (hits == null) return null;
        return hits.getHits();
    }

    public Long getTotalHits() {
        if (hits == null || hits.getTotal() == null) return 0L;
        return hits.getTotal().getValue();
    }

    /**
     * 검색 결과 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "검색 결과 정보")
    public static class HitsInfo {
        
        /**
         * 전체 검색 결과 정보
         */
        @Schema(description = "전체 검색 결과 정보")
        private Total total;

        /**
         * 검색 결과 항목 목록
         */
        @Schema(description = "검색 결과 항목 목록")
        private List<Map<String, Object>> hits;
    }

    /**
     * 전체 검색 결과 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "전체 검색 결과 정보")
    public static class Total {
        
        /**
         * 전체 검색 결과 수
         */
        @Schema(description = "전체 검색 결과 수", example = "1000")
        private Long value;

        /**
         * 전체 검색 결과 수의 관계 (eq: 정확한 개수, gte: 이상)
         */
        @Schema(description = "전체 검색 결과 수의 관계", example = "eq")
        private String relation;
    }
}