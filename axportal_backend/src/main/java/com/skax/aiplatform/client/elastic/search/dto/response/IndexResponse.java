package com.skax.aiplatform.client.elastic.search.dto.response;

import com.skax.aiplatform.client.elastic.common.dto.ElasticResponseMeta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch 인덱싱 응답 DTO
 * 
 * <p>Elasticsearch 문서 인덱싱 결과를 담는 응답 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch 인덱싱 응답 정보")
public class IndexResponse {

    /**
     * 메타데이터 정보
     */
    @Schema(description = "응답 메타데이터")
    private ElasticResponseMeta meta;

    /**
     * 인덱스명
     */
    @Schema(description = "인덱스명", example = "documents")
    private String index;

    /**
     * 문서 ID
     */
    @Schema(description = "문서 ID", example = "doc_001")
    private String id;

    /**
     * 문서 버전
     */
    @Schema(description = "문서 버전", example = "1")
    private Long version;

    /**
     * 수행된 작업 (created/updated)
     */
    @Schema(description = "수행된 작업", example = "created", allowableValues = {"created", "updated"})
    private String result;

    /**
     * 샤드 정보
     */
    @Schema(description = "샤드 정보")
    private Shards shards;

    /**
     * 시퀀스 번호
     */
    @Schema(description = "시퀀스 번호", example = "100")
    private Long seqNo;

    /**
     * Primary term
     */
    @Schema(description = "Primary term", example = "1")
    private Long primaryTerm;

    /**
     * 샤드 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "샤드 정보")
    public static class Shards {
        
        /**
         * 전체 샤드 수
         */
        @Schema(description = "전체 샤드 수", example = "1")
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