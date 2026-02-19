package com.skax.aiplatform.dto.data.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge 생성 요청 DTO
 * 
 * <p>
 * External Knowledge 생성을 위한 요청 데이터를 담는 DTO입니다.
 * </p>
 * 
 * @author system
 * @since 2025-10-14
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "External Knowledge 생성 요청")
public class DataCtlgExternalKnowledgeCreateReq {

    /**
     * 지식 UUID (프론트에서 생성)
     */
    @Schema(description = "지식 UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private String knwId;

    /**
     * 지식명
     */
    @Schema(description = "지식명", example = "신한은행 상품 지식", required = true)
    private String knwNm;

    /**
     * 설명
     */
    @Schema(description = "설명", example = "신한은행 금융상품 관련 지식")
    private String description;

    /**
     * 지식 유형
     */
    @Schema(description = "지식 유형", example = "option1")
    private String knowledgeType;

    /**
     * 청킹 알고리즘 id
     */
    @Schema(description = "청킹 알고리즘 ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String chunkId;

    /**
     * 청킹 방법명
     */
    @Schema(description = "청킹 방법명", example = "비정형 청킹 [SPACY]")
    private String chunkNm;

    /**
     * 청크 size
     */
    @Schema(description = "청킹 size", example = "300")
    private Integer chunkSize;

    /**
     * 문장 overlap
     */
    @Schema(description = "문장 overlap", example = "0")
    private Integer sentenceOverlap;

    /**
     * 임베딩 모델 id
     */
    @Schema(description = "임베딩 모델 ID", example = "36ec1946-7191-42c6-9347-05ce1b84dacc")
    private String embModelId;

    /**
     * 임베딩 모델명
     */
    @Schema(description = "임베딩 모델명", example = "비정형데이터 임베딩 모델")
    private String embeddingModel;

    /**
     * 벡터DB id
     */
    @Schema(description = "벡터DB ID", example = "vector-db-uuid")
    private String vectorDbId;

    /**
     * 벡터DB명
     */
    @Schema(description = "벡터DB명", example = "[비정형데이터플랫폼] Elasticsearch")
    private String vectorDB;

    /**
     * RAG chunk index명
     */
    @Schema(description = "RAG chunk index명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String ragChunkIndexNm;

    /**
     * 동기화 여부
     */
    @Schema(description = "동기화 여부", example = "false")
    private Boolean syncEnabled;

    /**
     * 동기화 대상 (개발계, 운영계)
     */
    @Schema(description = "동기화 대상", example = "[\"option1\", \"option2\"]")
    private List<String> syncTargets;

    /**
     * Script
     */
    @Schema(description = "Script")
    private String script;

    /**
     * 생성자 (사용자 이름)
     */
    @Schema(description = "생성자", example = "홍길동")
    private String createdBy;
}
