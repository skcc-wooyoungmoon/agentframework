package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge 테스트 요청 DTO
 * 
 * <p>
 * External Knowledge 테스트를 위한 요청 데이터를 담는 DTO입니다.
 * </p>
 * 
 * @author system
 * @since 2025-10-17
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "External Knowledge 테스트 요청")
public class DataCtlgExternalKnowledgeTestReq {

    /**
     * 임베딩 모델명
     */
    @Schema(description = "임베딩 모델명", example = "비정형데이터 임베딩 모델", required = true)
    private String embeddingModel;

    /**
     * 벡터DB명
     */
    @Schema(description = "벡터DB명", example = "[비정형데이터플랫폼] Elasticsearch", required = true)
    private String vectorDB;

    /**
     * 벡터DB ID
     */
    @Schema(description = "벡터DB ID", required = true)
    private String vectorDbId;

    /**
     * 인덱스명
     */
    @Schema(description = "인덱스명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000", required = true)
    private String indexName;

    /**
     * Script
     */
    @Schema(description = "Retrieval Script", required = true)
    private String script;

    /**
     * 테스트 질의
     */
    @Schema(description = "Retrieval 테스트 질의", example = "연금 수령 조건 알려줘", required = true)
    private String query;

    /**
     * Retrieval 옵션(JSON 문자열)
     */
    @Schema(description = "Retrieval 옵션(JSON 문자열)", example = "{\"top_k\":3}")
    private String retrievalOptions;
}

