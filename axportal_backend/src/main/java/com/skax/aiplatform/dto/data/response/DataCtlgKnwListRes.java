package com.skax.aiplatform.dto.data.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Knowledge 목록 조회 응답 DTO
 * 
 * <p>
 * Controller와 Service 간에 사용되는 Knowledge 목록 조회 응답 데이터 구조입니다.
 * SKTAI API 응답을 내부적으로 처리하기 위한 구조입니다.
 * </p>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Knowledge 목록 조회 응답")
public class DataCtlgKnwListRes {

    /**
     * Knowledge ID
     */
    @Schema(description = "Knowledge ID", example = "24aa2047-0648-4fdb-9411-330bf0d54a7b")
    private String id;

    /**
     * Knowledge 이름
     */
    @Schema(description = "Knowledge 이름", example = "aimm 3rd dev.")
    private String name;

    /**
     * Knowledge 설명
     */
    @Schema(description = "Knowledge 설명", example = "윤대훈 3차시도")
    private String description;

    /**
     * 데이터 소스 ID
     */
    @Schema(description = "데이터 소스 ID", example = "ec0b9445-1dac-47f5-8c96-de6a7680220d")
    private UUID datasourceId;

    /**
     * 활성 컬렉션 ID
     */
    @Schema(description = "활성 컬렉션 ID", example = "e8705aab-f28e-41ec-8440-f185f055f3c5")
    private String activeCollectionId;

    /**
     * 활성 컬렉션 정보
     */
    @Schema(description = "활성 컬렉션 정보")
    private DataCtlgCollection activeCollection;

    /**
     * 최신 컬렉션 정보
     */
    @Schema(description = "최신 컬렉션 정보")
    private DataCtlgCollection latestCollection;

    /**
     * 최신 작업 정보
     */
    @Schema(description = "최신 작업 정보")
    private Object latestTask;

    /**
     * 벡터 DB 타입
     */
    @Schema(description = "벡터 DB 타입", example = "AzureAISearchShared")
    private String vectorDbType;

    /**
     * 벡터 DB 이름
     */
    @Schema(description = "벡터 DB 이름", example = "[AzureAISearch][Shared] axplatform-ai-search-stage")
    private String vectorDbName;

    /**
     * 임베딩 모델 이름
     */
    @Schema(description = "임베딩 모델 이름", example = "gip/text-embedding-3-large")
    private String embeddingModelName;

    /**
     * 임베딩 모델 서빙 이름
     */
    @Schema(description = "임베딩 모델 서빙 이름", example = "gip/text-embedding-3-large")
    private String embeddingModelServingName;

    /**
     * 로더 타입
     */
    @Schema(description = "로더 타입", example = "Default")
    private String loader;

    /**
     * 스플리터 타입
     */
    @Schema(description = "스플리터 타입", example = "Semantic")
    private String splitter;

    /**
     * 활성 상태 여부
     */
    @Schema(description = "활성 상태 여부", example = "true")
    private Boolean isActive;

    /**
     * 활성 컬렉션 정보
     */
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // @Schema(description = "활성 컬렉션 정보")
    // public static class ActiveCollection {

    // /**
    // * 생성자 ID
    // */
    // @Schema(description = "생성자 ID", example =
    // "f676500c-1866-462a-ba8e-e7f76412b1dc")
    // private String createdBy;

    // /**
    // * 기본 구분자
    // */
    // @Schema(description = "기본 구분자")
    // private String baseSeparator;

    // /**
    // * 수정일시
    // */
    // @Schema(description = "수정일시", example = "2025-08-14T07:39:01.558772Z")
    // private LocalDateTime updatedAt;

    // /**
    // * 상태
    // */
    // @Schema(description = "상태", example = "COMPLETE")
    // private String status;

    // /**
    // * 기본 커스텀 로더 ID
    // */
    // @Schema(description = "기본 커스텀 로더 ID")
    // private String baseCustomLoaderId;

    // /**
    // * 기본 로더
    // */
    // @Schema(description = "기본 로더", example = "Default")
    // private String baseLoader;

    // /**
    // * 기본 커스텀 스플리터 ID
    // */
    // @Schema(description = "기본 커스텀 스플리터 ID")
    // private String baseCustomSplitterId;

    // /**
    // * 기본 프로세서
    // */
    // @Schema(description = "기본 프로세서", example = "none")
    // private String baseProcessor;

    // /**
    // * 기본 도구 ID
    // */
    // @Schema(description = "기본 도구 ID")
    // private String baseToolId;

    // /**
    // * 기본 스플리터
    // */
    // @Schema(description = "기본 스플리터", example = "Semantic")
    // private String baseSplitter;

    // /**
    // * 기본 프로세서 ID 목록
    // */
    // @Schema(description = "기본 프로세서 ID 목록")
    // private List<String> baseProcessorIds;

    // /**
    // * 임베딩 모델 ID
    // */
    // @Schema(description = "임베딩 모델 ID", example =
    // "64fed852-2367-460d-9349-1d03a9144455")
    // private String embeddingModelId;

    // /**
    // * 기본 청크 크기
    // */
    // @Schema(description = "기본 청크 크기", example = "1000")
    // private Integer baseChunkSize;

    // /**
    // * 인덱싱 설정
    // */
    // @Schema(description = "인덱싱 설정")
    // private IndexingConfig indexingConfig;

    // /**
    // * Repository ID
    // */
    // @Schema(description = "Repository ID", example =
    // "24aa2047-0648-4fdb-9411-330bf0d54a7b")
    // private String repoId;

    // /**
    // * 기본 청크 오버랩
    // */
    // @Schema(description = "기본 청크 오버랩", example = "0")
    // private Integer baseChunkOverlap;

    // /**
    // * 컬렉션 ID
    // */
    // @Schema(description = "컬렉션 ID", example =
    // "e8705aab-f28e-41ec-8440-f185f055f3c5")
    // private String id;

    // /**
    // * 벡터 DB ID
    // */
    // @Schema(description = "벡터 DB ID", example =
    // "376f55f0-cfe3-493c-a911-6fe7fbf16691")
    // private String vectorDbId;

    // /**
    // * 생성일시
    // */
    // @Schema(description = "생성일시", example = "2025-08-14T07:38:03.118260Z")
    // private LocalDateTime createdAt;
    // }

    // /**
    // * 인덱싱 설정
    // */
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // @Schema(description = "인덱싱 설정")
    // public static class IndexingConfig {

    // /**
    // * 메타데이터 필드 목록
    // */
    // @Schema(description = "메타데이터 필드 목록")
    // private List<MetadataField> metadataFields;
    // }

    // /**
    // * 메타데이터 필드
    // */
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // @Schema(description = "메타데이터 필드")
    // public static class MetadataField {

    // /**
    // * 필드 이름
    // */
    // @Schema(description = "필드 이름", example = "m_id")
    // private String name;

    // /**
    // * 필드 타입
    // */
    // @Schema(description = "필드 타입", example = "SearchFieldDataType.String")
    // private String type;

    // /**
    // * 검색 가능 여부
    // */
    // @Schema(description = "검색 가능 여부", example = "false")
    // private Boolean searchable;

    // /**
    // * 필터 가능 여부
    // */
    // @Schema(description = "필터 가능 여부", example = "true")
    // private Boolean filterable;

    // /**
    // * 정렬 가능 여부
    // */
    // @Schema(description = "정렬 가능 여부", example = "false")
    // private Boolean sortable;

    // /**
    // * 패싯 가능 여부
    // */
    // @Schema(description = "패싯 가능 여부", example = "true")
    // private Boolean facetable;

    // /**
    // * 검색 결과에 포함 여부
    // */
    // @Schema(description = "검색 결과에 포함 여부", example = "true")
    // private Boolean retrievable;
    // }
}