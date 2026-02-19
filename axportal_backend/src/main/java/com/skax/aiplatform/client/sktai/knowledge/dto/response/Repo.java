
package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Knowledge 목록 조회 응답 DTO
 * 
 * <p>
 * SKTAI Knowledge API에서 Repository 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 실제 API 응답 구조를 정확히 반영합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-28
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge 목록 조회 응답 정보")
public class Repo {

  /**
   * Repository 이름
   */
  @JsonProperty("name")
  @Schema(description = "Repository 이름", example = "aimm 3rd dev.", required = true)
  private String name;

  /**
   * Repository 설명
   */
  @JsonProperty("description")
  @Schema(description = "Repository 설명", example = "윤대훈 3차시도")
  private String description;

  /**
   * 데이터 소스 ID
   */
  @JsonProperty("datasource_id")
  @Schema(description = "데이터 소스 ID", example = "ec0b9445-1dac-47f5-8c96-de6a7680220d", format = "uuid")
  private String datasourceId;

  /**
   * Repository ID
   */
  @JsonProperty("id")
  @Schema(description = "Repository ID", example = "24aa2047-0648-4fdb-9411-330bf0d54a7b", format = "uuid")
  private String id;

  /**
   * 활성 컬렉션 ID
   */
  @JsonProperty("active_collection_id")
  @Schema(description = "활성 컬렉션 ID", example = "e8705aab-f28e-41ec-8440-f185f055f3c5", format = "uuid")
  private String activeCollectionId;

  /**
   * 활성 컬렉션 정보
   */
  @JsonProperty("active_collection")
  @Schema(description = "활성 컬렉션 정보")
  private Collection activeCollection;

  /**
   * 최신 컬렉션 정보
   */
  @JsonProperty("latest_collection")
  @Schema(description = "최신 컬렉션 정보")
  private Collection latestCollection;

  /**
   * 최신 작업 정보
   */
  @JsonProperty("latest_task")
  @Schema(description = "최신 작업 정보")
  private Object latestTask;

  /**
   * 벡터 DB 타입
   */
  @JsonProperty("vector_db_type")
  @Schema(description = "벡터 DB 타입", example = "AzureAISearchShared")
  private String vectorDbType;

  /**
   * 벡터 DB 이름
   */
  @JsonProperty("vector_db_name")
  @Schema(description = "벡터 DB 이름", example = "[AzureAISearch][Shared] axplatform-ai-search-stage")
  private String vectorDbName;

  /**
   * 임베딩 모델 이름
   */
  @JsonProperty("embedding_model_name")
  @Schema(description = "임베딩 모델 이름", example = "gip/text-embedding-3-large")
  private String embeddingModelName;

  /**
   * 임베딩 모델 서빙 이름
   */
  @JsonProperty("embedding_model_serving_name")
  @Schema(description = "임베딩 모델 서빙 이름", example = "gip/text-embedding-3-large")
  private String embeddingModelServingName;

  /**
   * 로더 타입
   */
  @JsonProperty("loader")
  @Schema(description = "로더 타입", example = "Default")
  private String loader;

  /**
   * 스플리터 타입
   */
  @JsonProperty("splitter")
  @Schema(description = "스플리터 타입", example = "Semantic")
  private String splitter;

  /**
   * 활성 상태 여부
   */
  @JsonProperty("is_active")
  @Schema(description = "활성 상태 여부", example = "true")
  private Boolean isActive;

  /**
   * 활성 컬렉션 정보
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(description = "활성 컬렉션 정보")
  public static class Collection {

    /**
     * 생성자 ID
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc", format = "uuid")
    private String createdBy;

    /**
     * 기본 구분자
     */
    @JsonProperty("base_separator")
    @Schema(description = "기본 구분자")
    private String baseSeparator;

    /**
     * 수정일시
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-08-14T07:39:01.558772Z", format = "date-time")
    private LocalDateTime updatedAt;

    /**
     * 상태
     */
    @JsonProperty("status")
    @Schema(description = "상태", example = "COMPLETE")
    private String status;

    /**
     * 기본 커스텀 로더 ID
     */
    @JsonProperty("base_custom_loader_id")
    @Schema(description = "기본 커스텀 로더 ID")
    private String baseCustomLoaderId;

    /**
     * 기본 로더
     */
    @JsonProperty("base_loader")
    @Schema(description = "기본 로더", example = "Default")
    private String baseLoader;

    /**
     * 기본 커스텀 스플리터 ID
     */
    @JsonProperty("base_custom_splitter_id")
    @Schema(description = "기본 커스텀 스플리터 ID")
    private String baseCustomSplitterId;

    /**
     * 기본 프로세서
     */
    @JsonProperty("base_processor")
    @Schema(description = "기본 프로세서", example = "none")
    private String baseProcessor;

    /**
     * 기본 도구 ID
     */
    @JsonProperty("base_tool_id")
    @Schema(description = "기본 도구 ID")
    private String baseToolId;

    /**
     * 기본 스플리터
     */
    @JsonProperty("base_splitter")
    @Schema(description = "기본 스플리터", example = "Semantic")
    private String baseSplitter;

    /**
     * 기본 프로세서 ID 목록
     */
    @JsonProperty("base_processor_ids")
    @Schema(description = "기본 프로세서 ID 목록")
    private List<String> baseProcessorIds;

    /**
     * 임베딩 모델 ID
     */
    @JsonProperty("embedding_model_id")
    @Schema(description = "임베딩 모델 ID", example = "64fed852-2367-460d-9349-1d03a9144455", format = "uuid")
    private String embeddingModelId;

    /**
     * 기본 청크 크기
     */
    @JsonProperty("base_chunk_size")
    @Schema(description = "기본 청크 크기", example = "1000")
    private Integer baseChunkSize;

    /**
     * 인덱싱 설정
     */
    @JsonProperty("indexing_config")
    @Schema(description = "인덱싱 설정")
    private IndexingConfig indexingConfig;

    /**
     * Repository ID
     */
    @JsonProperty("repo_id")
    @Schema(description = "Repository ID", example = "24aa2047-0648-4fdb-9411-330bf0d54a7b", format = "uuid")
    private String repoId;

    /**
     * 기본 청크 오버랩
     */
    @JsonProperty("base_chunk_overlap")
    @Schema(description = "기본 청크 오버랩", example = "0")
    private Integer baseChunkOverlap;

    /**
     * 컬렉션 ID
     */
    @JsonProperty("id")
    @Schema(description = "컬렉션 ID", example = "e8705aab-f28e-41ec-8440-f185f055f3c5", format = "uuid")
    private String id;

    /**
     * 벡터 DB ID
     */
    @JsonProperty("vector_db_id")
    @Schema(description = "벡터 DB ID", example = "376f55f0-cfe3-493c-a911-6fe7fbf16691", format = "uuid")
    private String vectorDbId;

    /**
     * 생성일시
     */
    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-08-14T07:38:03.118260Z", format = "date-time")
    private LocalDateTime createdAt;
  }

  /**
   * 인덱싱 설정
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(description = "인덱싱 설정")
  public static class IndexingConfig {

    /**
     * 메타데이터 필드 목록
     */
    @JsonProperty("metadata_fields")
    @Schema(description = "메타데이터 필드 목록")
    private List<MetadataField> metadataFields;
  }

  /**
   * 메타데이터 필드
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(description = "메타데이터 필드")
  public static class MetadataField {

    /**
     * 필드 이름
     */
    @JsonProperty("name")
    @Schema(description = "필드 이름", example = "m_id")
    private String name;

    /**
     * 필드 타입
     */
    @JsonProperty("type")
    @Schema(description = "필드 타입", example = "SearchFieldDataType.String")
    private String type;

    /**
     * 검색 가능 여부
     */
    @JsonProperty("searchable")
    @Schema(description = "검색 가능 여부", example = "false")
    private Boolean searchable;

    /**
     * 필터 가능 여부
     */
    @JsonProperty("filterable")
    @Schema(description = "필터 가능 여부", example = "true")
    private Boolean filterable;

    /**
     * 정렬 가능 여부
     */
    @JsonProperty("sortable")
    @Schema(description = "정렬 가능 여부", example = "false")
    private Boolean sortable;

    /**
     * 패싯 가능 여부
     */
    @JsonProperty("facetable")
    @Schema(description = "패싯 가능 여부", example = "true")
    private Boolean facetable;

    /**
     * 검색 결과에 포함 여부
     */
    @JsonProperty("retrievable")
    @Schema(description = "검색 결과에 포함 여부", example = "true")
    private Boolean retrievable;
  }
}