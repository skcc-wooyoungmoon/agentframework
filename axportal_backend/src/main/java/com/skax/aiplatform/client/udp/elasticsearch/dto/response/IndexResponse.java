package com.skax.aiplatform.client.udp.elasticsearch.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch 데이터 삽입 응답 DTO
 * 
 * <p>Elasticsearch에 데이터를 삽입한 결과를 담는 응답 데이터 구조입니다.</p>
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch 데이터 삽입 응답 정보")
public class IndexResponse {

    /**
     * 인덱스명
     */
    @Schema(description = "인덱스명", example = "gaf_datasets")
    @JsonProperty("_index")
    private String index;

    /**
     * 문서 ID
     */
    @Schema(description = "문서 ID", example = "doc_001")
    @JsonProperty("_id")
    private String id;

    /**
     * 문서 버전
     */
    @Schema(description = "문서 버전", example = "1")
    @JsonProperty("_version")
    private Long version;

    /**
     * 결과 상태
     */
    @Schema(description = "결과 상태", example = "created")
    private String result;

    /**
     * 시퀀스 번호
     */
    @Schema(description = "시퀀스 번호", example = "0")
    @JsonProperty("_seq_no")
    private Long seqNo;

    /**
     * 주요 버전
     */
    @Schema(description = "주요 버전", example = "1")
    @JsonProperty("_primary_term")
    private Long primaryTerm;

    /**
     * 샤드 정보
     */
    @Schema(description = "샤드 정보")
    private Shards shards;

    /**
     * 샤드 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Shards {
        
        /**
         * 총 샤드 수
         */
        @Schema(description = "총 샤드 수", example = "2")
        private Integer total;

        /**
         * 성공한 샤드 수
         */
        @Schema(description = "성공한 샤드 수", example = "1")
        private Integer successful;

        /**
         * 실패한 샤드 수
         */
        @Schema(description = "실패한 샤드 수", example = "0")
        private Integer failed;
    }
}
