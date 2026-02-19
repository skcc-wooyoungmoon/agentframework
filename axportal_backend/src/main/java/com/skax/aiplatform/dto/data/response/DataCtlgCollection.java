package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 활성 컬렉션 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "활성 컬렉션 정보")
public class DataCtlgCollection {
    /**
     * Repository ID
     */
    @Schema(description = "Repository ID", example = "24aa2047-0648-4fdb-9411-330bf0d54a7b")
    private String repoId;

    /**
     * 임베딩 모델 ID
     */
    @Schema(description = "임베딩 모델 ID", example = "64fed852-2367-460d-9349-1d03a9144455")
    private String embeddingModelId;
    /**
     * 벡터 DB ID
     */
    @Schema(description = "벡터 DB ID", example = "376f55f0-cfe3-493c-a911-6fe7fbf16691")
    private String vectorDbId;

    /**
     * 생성자 ID
     */
    @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String createdBy;

    /**
     * 기본 구분자
     */
    @Schema(description = "기본 구분자")
    private String baseSeparator;

    /**
     * 수정일시
     */
    @Schema(description = "수정일시", example = "2025-08-14T07:39:01.558772Z")
    private LocalDateTime updatedAt;

    /**
     * 상태
     */
    @Schema(description = "상태", example = "COMPLETE")
    private String status;

    /**
     * 기본 커스텀 로더 ID
     */
    @Schema(description = "기본 커스텀 로더 ID")
    private String baseCustomLoaderId;

    /**
     * 기본 로더
     */
    @Schema(description = "기본 로더", example = "Default")
    private String baseLoader;

    /**
     * 기본 커스텀 스플리터 ID
     */
    @Schema(description = "기본 커스텀 스플리터 ID")
    private String baseCustomSplitterId;

    /**
     * 기본 프로세서
     */
    @Schema(description = "기본 프로세서", example = "none")
    private String baseProcessor;

    /**
     * 기본 도구 ID
     */
    @Schema(description = "기본 도구 ID")
    private String baseToolId;

    /**
     * 기본 스플리터
     */
    @Schema(description = "기본 스플리터", example = "Semantic")
    private String baseSplitter;

    /**
     * 기본 프로세서 ID 목록
     */
    @Schema(description = "기본 프로세서 ID 목록")
    private List<String> baseProcessorIds;

    /**
     * 기본 청크 크기
     */
    @Schema(description = "기본 청크 크기", example = "1000")
    private Integer baseChunkSize;

    /**
     * 인덱싱 설정
     */
    @Schema(description = "인덱싱 설정")
    private DataCtlgIndexingConfig indexingConfig;

    /**
     * 기본 청크 오버랩
     */
    @Schema(description = "기본 청크 오버랩", example = "0")
    private Integer baseChunkOverlap;

    /**
     * 컬렉션 ID
     */
    @Schema(description = "컬렉션 ID", example = "e8705aab-f28e-41ec-8440-f185f055f3c5")
    private String id;

    /**
     * 생성일시
     */
    @Schema(description = "생성일시", example = "2025-08-14T07:38:03.118260Z")
    private LocalDateTime createdAt;

}
