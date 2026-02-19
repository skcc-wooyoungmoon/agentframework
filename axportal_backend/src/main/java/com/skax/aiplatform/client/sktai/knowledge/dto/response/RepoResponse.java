package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * SKTAI Knowledge Repository 상세 정보 응답 DTO
 * 
 * <p>Repository의 전체 정보를 포함하는 응답 데이터 구조입니다.
 * 기본 정보, 설정, 통계, 상태 등 Repository의 모든 측면을 포괄합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, 설명, 생성/수정 이력</li>
 *   <li><strong>설정 정보</strong>: DataSource, 임베딩 모델, 벡터 DB, 처리 설정</li>
 *   <li><strong>상태 정보</strong>: 활성화 상태, 인덱싱 상태, 오류 정보</li>
 *   <li><strong>통계 정보</strong>: Document 수, 청크 수, 인덱싱 진행률</li>
 *   <li><strong>메타데이터</strong>: 추가 속성 및 관리 정보</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge Repository 상세 정보 응답")
public class RepoResponse {

    /**
     * Repository ID
     */
    @JsonProperty("repo_id")
    @Schema(description = "Repository 고유 식별자", example = "repo-12345")
    private String repoId;

    /**
     * Repository 이름
     */
    @JsonProperty("name")
    @Schema(description = "Repository 이름", example = "Customer Service KB")
    private String name;

    /**
     * Repository 설명
     */
    @JsonProperty("description")
    @Schema(description = "Repository 설명", example = "고객 서비스 지식 베이스")
    private String description;

    /**
     * Repository 상태
     */
    @JsonProperty("status")
    @Schema(description = "Repository 상태", example = "ACTIVE")
    private String status;

    /**
     * 활성화 여부
     */
    @JsonProperty("enabled")
    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;

    /**
     * DataSource 정보
     */
    @JsonProperty("datasource")
    @Schema(description = "연결된 DataSource 정보")
    private DataSourceInfo datasource;

    /**
     * 임베딩 모델 정보
     */
    @JsonProperty("embedding_model")
    @Schema(description = "임베딩 모델 정보")
    private EmbeddingModelInfo embeddingModel;

    /**
     * 벡터 DB 정보
     */
    @JsonProperty("vector_db")
    @Schema(description = "벡터 데이터베이스 정보")
    private VectorDbInfo vectorDb;

    /**
     * 처리 설정
     */
    @JsonProperty("processing_config")
    @Schema(description = "문서 처리 설정")
    private ProcessingConfig processingConfig;

    /**
     * 인덱싱 정보
     */
    @JsonProperty("indexing_info")
    @Schema(description = "인덱싱 상태 및 정보")
    private IndexingInfo indexingInfo;

    /**
     * 통계 정보
     */
    @JsonProperty("statistics")
    @Schema(description = "Repository 통계")
    private RepoStatistics statistics;

    /**
     * 컬렉션 정보
     */
    @JsonProperty("collection_info")
    @Schema(description = "벡터 컬렉션 정보")
    private CollectionInfo collectionInfo;

    /**
     * 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Map<String, Object> metadata;

    /**
     * 생성 정보
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", example = "2024-01-15T10:30:00Z")
    private String createdAt;

    /**
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "user@example.com")
    private String createdBy;

    /**
     * 수정 정보
     */
    @JsonProperty("updated_at")
    @Schema(description = "최종 수정 시간", example = "2024-01-16T14:20:00Z")
    private String updatedAt;

    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "최종 수정자", example = "admin@example.com")
    private String updatedBy;

    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "소속 프로젝트 ID", example = "project-999")
    private String projectId;

    // Inner classes for complex types

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "DataSource 정보")
    public static class DataSourceInfo {
        @JsonProperty("id")
        @Schema(description = "DataSource ID")
        private String id;

        @JsonProperty("name")
        @Schema(description = "DataSource 이름")
        private String name;

        @JsonProperty("type")
        @Schema(description = "DataSource 유형")
        private String type;

        @JsonProperty("file_count")
        @Schema(description = "파일 개수")
        private Integer fileCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "임베딩 모델 정보")
    public static class EmbeddingModelInfo {
        @JsonProperty("name")
        @Schema(description = "모델 이름")
        private String name;

        @JsonProperty("dimension")
        @Schema(description = "벡터 차원")
        private Integer dimension;

        @JsonProperty("max_tokens")
        @Schema(description = "최대 토큰 수")
        private Integer maxTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "벡터 DB 정보")
    public static class VectorDbInfo {
        @JsonProperty("id")
        @Schema(description = "벡터 DB ID")
        private String id;

        @JsonProperty("name")
        @Schema(description = "벡터 DB 이름")
        private String name;

        @JsonProperty("type")
        @Schema(description = "벡터 DB 유형")
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "문서 처리 설정")
    public static class ProcessingConfig {
        @JsonProperty("default_loader")
        @Schema(description = "기본 로더")
        private String defaultLoader;

        @JsonProperty("default_splitter")
        @Schema(description = "기본 스플리터")
        private String defaultSplitter;

        @JsonProperty("chunk_size")
        @Schema(description = "청크 크기")
        private Integer chunkSize;

        @JsonProperty("chunk_overlap")
        @Schema(description = "청크 오버랩")
        private Integer chunkOverlap;

        @JsonProperty("separator")
        @Schema(description = "분할 구분자")
        private String separator;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 정보")
    public static class IndexingInfo {
        @JsonProperty("status")
        @Schema(description = "인덱싱 상태")
        private String status;

        @JsonProperty("progress")
        @Schema(description = "진행률 (%)")
        private Double progress;

        @JsonProperty("last_indexed_at")
        @Schema(description = "최종 인덱싱 시간")
        private String lastIndexedAt;

        @JsonProperty("total_chunks")
        @Schema(description = "전체 청크 수")
        private Long totalChunks;

        @JsonProperty("indexed_chunks")
        @Schema(description = "인덱싱된 청크 수")
        private Long indexedChunks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Repository 통계")
    public static class RepoStatistics {
        @JsonProperty("total_documents")
        @Schema(description = "전체 Document 수")
        private Long totalDocuments;

        @JsonProperty("active_documents")
        @Schema(description = "활성 Document 수")
        private Long activeDocuments;

        @JsonProperty("total_chunks")
        @Schema(description = "전체 청크 수")
        private Long totalChunks;

        @JsonProperty("indexed_chunks")
        @Schema(description = "인덱싱된 청크 수")
        private Long indexedChunks;

        @JsonProperty("total_size")
        @Schema(description = "전체 데이터 크기 (바이트)")
        private Long totalSize;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "컬렉션 정보")
    public static class CollectionInfo {
        @JsonProperty("collection_name")
        @Schema(description = "컬렉션 이름")
        private String collectionName;

        @JsonProperty("vector_count")
        @Schema(description = "벡터 개수")
        private Long vectorCount;

        @JsonProperty("index_status")
        @Schema(description = "인덱스 상태")
        private String indexStatus;
    }
}
