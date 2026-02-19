package com.skax.aiplatform.client.elastic.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch 공통 응답 메타데이터 DTO
 * 
 * <p>Elasticsearch API 응답에서 공통으로 사용되는 메타데이터 정보를 담는 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch 응답 메타데이터")
public class ElasticResponseMeta {

    /**
     * 요청 처리 시간 (밀리초)
     */
    @Schema(description = "요청 처리 시간 (밀리초)", example = "15")
    private Integer took;

    /**
     * 타임아웃 여부
     */
    @Schema(description = "타임아웃 여부", example = "false")
    private Boolean timedOut;

    /**
     * 샤드 정보
     */
    @Schema(description = "샤드 정보")
    private ShardInfo shards;

    /**
     * 샤드 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "샤드 정보")
    public static class ShardInfo {
        
        /**
         * 전체 샤드 수
         */
        @Schema(description = "전체 샤드 수", example = "5")
        private Integer total;

        /**
         * 성공한 샤드 수
         */
        @Schema(description = "성공한 샤드 수", example = "5")
        private Integer successful;

        /**
         * 건너뛴 샤드 수
         */
        @Schema(description = "건너뛴 샤드 수", example = "0")
        private Integer skipped;

        /**
         * 실패한 샤드 수
         */
        @Schema(description = "실패한 샤드 수", example = "0")
        private Integer failed;
    }
}