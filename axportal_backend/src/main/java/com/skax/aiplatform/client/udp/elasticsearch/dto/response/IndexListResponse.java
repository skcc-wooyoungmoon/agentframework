package com.skax.aiplatform.client.udp.elasticsearch.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Elasticsearch Index 목록 응답 DTO
 * 
 * <p>
 * Elasticsearch Index 목록 조회 결과를 담는 응답 데이터입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch Index 목록 응답")
public class IndexListResponse {

    /**
     * Index 목록
     */
    @Schema(description = "Index 목록")
    private List<IndexInfo> indices;

    /**
     * Index 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Index 정보")
    public static class IndexInfo {

        /**
         * Index 이름
         */
        @JsonProperty("index")
        @Schema(description = "Index 이름", example = "gaf_default_rag_test")
        private String index;

        /**
         * Health 상태
         */
        @JsonProperty("health")
        @Schema(description = "Health 상태", example = "green")
        private String health;

        /**
         * 상태
         */
        @JsonProperty("status")
        @Schema(description = "상태", example = "open")
        private String status;

        /**
         * UUID
         */
        @JsonProperty("uuid")
        @Schema(description = "UUID")
        private String uuid;

        /**
         * Primary 샤드 수
         */
        @JsonProperty("pri")
        @Schema(description = "Primary 샤드 수", example = "1")
        private String pri;

        /**
         * Replica 샤드 수
         */
        @JsonProperty("rep")
        @Schema(description = "Replica 샤드 수", example = "0")
        private String rep;

        /**
         * 문서 수
         */
        @JsonProperty("docs.count")
        @Schema(description = "문서 수", example = "0")
        private String docsCount;

        /**
         * 삭제된 문서 수
         */
        @JsonProperty("docs.deleted")
        @Schema(description = "삭제된 문서 수", example = "0")
        private String docsDeleted;

        /**
         * 저장 크기
         */
        @JsonProperty("store.size")
        @Schema(description = "저장 크기", example = "225b")
        private String storeSize;

        /**
         * Primary 저장 크기
         */
        @JsonProperty("pri.store.size")
        @Schema(description = "Primary 저장 크기", example = "225b")
        private String priStoreSize;
    }
}

