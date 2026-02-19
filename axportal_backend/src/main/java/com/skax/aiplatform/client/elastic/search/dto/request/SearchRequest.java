package com.skax.aiplatform.client.elastic.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * OpenSearch/Elasticsearch 검색 요청 DTO
 * 
 * <p>OpenSearch와 Elasticsearch에서 문서를 검색하기 위한 공통 요청 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "OpenSearch/Elasticsearch 검색 요청 정보")
public class SearchRequest {

    /**
     * 반환할 필드 목록 (_source)
     */
    @JsonProperty("_source")
    @Schema(description = "반환할 필드 목록(_source)", example = "[\"@timestamp\", \"log\", \"kubernetes.pod_name\"]")
    private String[] source;

    /**
     * DSL 쿼리 객체
     */
    @Schema(description = "OpenSearch/Elasticsearch DSL 쿼리 객체")
    private Object query;

    /**
     * 정렬 조건 배열
     */
    @Schema(description = "정렬 조건 배열", example = "[{\"@timestamp\": {\"order\": \"desc\"}}]")
    private List<Map<String, Object>> sort;

    /**
     * 시작 위치(from)
     */
    @JsonProperty("from")
    @Builder.Default
    @Min(value = 0, message = "시작 위치는 0 이상이어야 합니다")
    @Schema(description = "시작 위치(from)", example = "0", defaultValue = "0")
    private Integer from = 0;

    /**
     * 페이지 크기(size)
     */
    @Builder.Default
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Schema(description = "페이지 크기(size)", example = "10", defaultValue = "10")
    private Integer size = 10;
}