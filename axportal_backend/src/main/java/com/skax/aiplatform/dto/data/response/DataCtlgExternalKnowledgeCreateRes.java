package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * External Knowledge 생성 응답 DTO
 * 
 * <p>
 * External Knowledge 생성 결과를 담는 DTO입니다.
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
@Schema(description = "External Knowledge 생성 응답")
public class DataCtlgExternalKnowledgeCreateRes {

    /**
     * 지식 UUID
     */
    @Schema(description = "지식 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String knwId;

    /**
     * 지식명
     */
    @Schema(description = "지식명", example = "신한은행 상품 지식")
    private String knwNm;

    /**
     * External Knowledge repo id
     */
    @Schema(description = "External Knowledge repo id", example = "exp-repo-uuid")
    private String expKnwId;

    /**
     * 청킹 알고리즘 id
     */
    @Schema(description = "청킹 알고리즘 ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String chunkId;

    /**
     * 임베딩 모델 id
     */
    @Schema(description = "임베딩 모델 ID", example = "36ec1946-7191-42c6-9347-05ce1b84dacc")
    private String embModelId;

    /**
     * RAG chunk index명
     */
    @Schema(description = "RAG chunk index명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String ragChunkIndexNm;

    /**
     * 개발계 동기화 여부
     */
    @Schema(description = "개발계 동기화 여부", example = "Y")
    private String devSyncYn;

    /**
     * 운영계 동기화 여부
     */
    @Schema(description = "운영계 동기화 여부", example = "N")
    private String prodSyncYn;

    /**
     * 생성자
     */
    @Schema(description = "생성자", example = "system")
    private String createdBy;

    /**
     * 최초 생성일시
     */
    @Schema(description = "최초 생성일시", example = "2025-10-14T10:06:50")
    private LocalDateTime fstCreatedAt;

    /**
     * 최종 수정일시
     */
    @Schema(description = "최종 수정일시", example = "2025-10-14T10:06:50")
    private LocalDateTime lstUpdatedAt;
}


