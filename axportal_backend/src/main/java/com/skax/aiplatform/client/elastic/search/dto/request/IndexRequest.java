package com.skax.aiplatform.client.elastic.search.dto.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch 문서 인덱싱 요청 DTO
 * 
 * <p>Elasticsearch에 문서를 인덱싱하기 위한 요청 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch 문서 인덱싱 요청 정보")
public class IndexRequest {

    /**
     * 문서 ID
     */
    @Schema(description = "문서 ID", example = "doc_001")
    private String id;

    /**
     * 문서 내용
     */
    @NotNull(message = "문서 내용은 필수입니다")
    @Schema(description = "문서 내용", required = true)
    private Map<String, Object> document;

    /**
     * 라우팅 키
     */
    @Schema(description = "라우팅 키", example = "user_123")
    private String routing;

    /**
     * 리프레시 정책
     */
    @Schema(description = "리프레시 정책", example = "wait_for", allowableValues = {"true", "false", "wait_for"})
    private String refresh;
}