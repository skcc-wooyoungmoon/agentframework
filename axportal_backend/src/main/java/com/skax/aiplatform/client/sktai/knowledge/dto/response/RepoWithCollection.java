package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * SKTAI Knowledge Repository with Collection 정보 응답 DTO
 * 
 * <p>Repository와 연결된 벡터 컬렉션의 상세 정보를 포함하는 응답 데이터 구조입니다.
 * Repository의 기본 정보와 함께 벡터 데이터베이스의 컬렉션 상태, 인덱스 정보, 성능 메트릭 등을 제공합니다.</p>
 * 
 * <h3>주요 용도:</h3>
 * <ul>
 *   <li><strong>Repository 상세 조회</strong>: 벡터 컬렉션 정보까지 포함한 전체 상태 확인</li>
 *   <li><strong>성능 모니터링</strong>: 인덱싱 상태 및 검색 성능 지표 확인</li>
 *   <li><strong>용량 관리</strong>: 벡터 저장 공간 및 리소스 사용량 확인</li>
 *   <li><strong>문제 진단</strong>: 인덱싱 오류나 성능 이슈 분석</li>
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
@Schema(description = "SKTAI Knowledge Repository with Collection 정보 응답")
public class RepoWithCollection {

    /**
     * Repository 기본 정보
     */
    @JsonProperty("repository")
    @Schema(description = "Repository 기본 정보")
    private RepositoryInfo repository;

    /**
     * 벡터 컬렉션 정보
     */
    @JsonProperty("collection")
    @Schema(description = "벡터 컬렉션 상세 정보")
    private CollectionDetails collection;

    /**
     * 인덱싱 상태
     */
    @JsonProperty("indexing_status")
    @Schema(description = "인덱싱 상태 정보")
    private IndexingStatus indexingStatus;

    /**
     * 성능 메트릭
     */
    @JsonProperty("performance_metrics")
    @Schema(description = "성능 메트릭")
    private PerformanceMetrics performanceMetrics;

    /**
     * 저장소 정보
     */
    @JsonProperty("storage_info")
    @Schema(description = "저장소 사용량 정보")
    private StorageInfo storageInfo;

    // Inner classes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Repository 기본 정보")
    public static class RepositoryInfo {
        @JsonProperty("repo_id")
        @Schema(description = "Repository ID")
        private String repoId;

        @JsonProperty("name")
        @Schema(description = "Repository 이름")
        private String name;

        @JsonProperty("description")
        @Schema(description = "Repository 설명")
        private String description;

        @JsonProperty("status")
        @Schema(description = "Repository 상태")
        private String status;

        @JsonProperty("enabled")
        @Schema(description = "활성화 여부")
        private Boolean enabled;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "벡터 컬렉션 상세 정보")
    public static class CollectionDetails {
        @JsonProperty("collection_name")
        @Schema(description = "컬렉션 이름")
        private String collectionName;

        @JsonProperty("vector_db_type")
        @Schema(description = "벡터 DB 유형")
        private String vectorDbType;

        @JsonProperty("dimension")
        @Schema(description = "벡터 차원")
        private Integer dimension;

        @JsonProperty("distance_metric")
        @Schema(description = "거리 측정 방식")
        private String distanceMetric;

        @JsonProperty("index_type")
        @Schema(description = "인덱스 유형")
        private String indexType;

        @JsonProperty("index_parameters")
        @Schema(description = "인덱스 파라미터")
        private Map<String, Object> indexParameters;

        @JsonProperty("is_ready")
        @Schema(description = "검색 준비 상태")
        private Boolean isReady;

        @JsonProperty("created_at")
        @Schema(description = "컬렉션 생성 시간")
        private String createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 상태 정보")
    public static class IndexingStatus {
        @JsonProperty("current_status")
        @Schema(description = "현재 인덱싱 상태")
        private String currentStatus;

        @JsonProperty("progress_percentage")
        @Schema(description = "진행률 (%)")
        private Double progressPercentage;

        @JsonProperty("total_documents")
        @Schema(description = "전체 Document 수")
        private Long totalDocuments;

        @JsonProperty("processed_documents")
        @Schema(description = "처리된 Document 수")
        private Long processedDocuments;

        @JsonProperty("total_chunks")
        @Schema(description = "전체 청크 수")
        private Long totalChunks;

        @JsonProperty("indexed_chunks")
        @Schema(description = "인덱싱된 청크 수")
        private Long indexedChunks;

        @JsonProperty("failed_chunks")
        @Schema(description = "실패한 청크 수")
        private Long failedChunks;

        @JsonProperty("last_indexed_at")
        @Schema(description = "최종 인덱싱 시간")
        private String lastIndexedAt;

        @JsonProperty("estimated_completion_time")
        @Schema(description = "예상 완료 시간")
        private String estimatedCompletionTime;

        @JsonProperty("error_count")
        @Schema(description = "오류 발생 수")
        private Integer errorCount;

        @JsonProperty("last_error")
        @Schema(description = "최근 오류 메시지")
        private String lastError;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "성능 메트릭")
    public static class PerformanceMetrics {
        @JsonProperty("average_search_time")
        @Schema(description = "평균 검색 시간 (ms)")
        private Double averageSearchTime;

        @JsonProperty("average_indexing_time")
        @Schema(description = "평균 인덱싱 시간 (ms)")
        private Double averageIndexingTime;

        @JsonProperty("search_qps")
        @Schema(description = "초당 검색 쿼리 수")
        private Double searchQps;

        @JsonProperty("indexing_throughput")
        @Schema(description = "인덱싱 처리량 (docs/sec)")
        private Double indexingThroughput;

        @JsonProperty("recall_at_10")
        @Schema(description = "Recall@10 점수")
        private Double recallAt10;

        @JsonProperty("precision_at_10")
        @Schema(description = "Precision@10 점수")
        private Double precisionAt10;

        @JsonProperty("last_measured_at")
        @Schema(description = "마지막 측정 시간")
        private String lastMeasuredAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "저장소 사용량 정보")
    public static class StorageInfo {
        @JsonProperty("total_vectors")
        @Schema(description = "총 벡터 수")
        private Long totalVectors;

        @JsonProperty("storage_size_bytes")
        @Schema(description = "저장 공간 사용량 (바이트)")
        private Long storageSizeBytes;

        @JsonProperty("index_size_bytes")
        @Schema(description = "인덱스 크기 (바이트)")
        private Long indexSizeBytes;

        @JsonProperty("memory_usage_bytes")
        @Schema(description = "메모리 사용량 (바이트)")
        private Long memoryUsageBytes;

        @JsonProperty("compression_ratio")
        @Schema(description = "압축 비율")
        private Double compressionRatio;

        @JsonProperty("storage_efficiency")
        @Schema(description = "저장 효율성 (%)")
        private Double storageEfficiency;

        @JsonProperty("last_optimized_at")
        @Schema(description = "마지막 최적화 시간")
        private String lastOptimizedAt;
    }
}
