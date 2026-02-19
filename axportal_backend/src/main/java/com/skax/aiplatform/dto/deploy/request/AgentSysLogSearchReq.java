package com.skax.aiplatform.dto.deploy.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Agent 시스템 로그 검색 요청 DTO (OpenSearch/Elasticsearch DSL 래퍼)
 * 
 * <p>OpenSearch와 Elasticsearch 모두 지원하는 공통 DSL 구조입니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSysLogSearchReq {

    /**
     * 반환할 필드 목록 (_source)
     */
    @JsonProperty("_source")
    @Schema(description = "반환할 필드 목록(_source)")
    private String[] source;

    /**
     * DSL 쿼리 객체
     */
    @Schema(description = "OpenSearch/Elasticsearch DSL 쿼리 객체")
    private Object query;

    /**
     * 정렬 조건 배열
     */
    @Schema(description = "정렬 조건 배열")
    private List<Map<String, Object>> sort;

    /**
     * 시작 위치(from)
     */
    @JsonProperty("from")
    @Schema(description = "시작 위치(from)")
    private Integer from;

    /**
     * 페이지 크기(size)
     */
    @Schema(description = "페이지 크기(size)")
    private Integer size;
}


