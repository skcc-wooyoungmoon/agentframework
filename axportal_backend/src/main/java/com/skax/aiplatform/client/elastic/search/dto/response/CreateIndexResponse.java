package com.skax.aiplatform.client.elastic.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elasticsearch Index 생성 응답 DTO
 * 
 * <p>Elasticsearch 인덱스 생성 결과를 담는 응답 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch Index 생성 응답 정보")
public class CreateIndexResponse {

    /**
     * 인덱스명
     */
    @Schema(description = "인덱스명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String index;

    /**
     * 인덱스 생성 성공 여부
     */
    @Schema(description = "인덱스 생성 성공 여부", example = "true")
    private Boolean acknowledged;

    /**
     * 인덱스 생성 여부 (새로 생성된 경우 true)
     */
    @Schema(description = "인덱스 생성 여부", example = "true")
    private Boolean created;

    /**
     * 샤드 수
     */
    @Schema(description = "샤드 수", example = "1")
    private Integer shardsAcknowledged;

    /**
     * 에러 메시지 (실패 시)
     */
    @Schema(description = "에러 메시지")
    private String error;

    /**
     * 에러 타입 (실패 시)
     */
    @Schema(description = "에러 타입")
    private String errorType;
}

