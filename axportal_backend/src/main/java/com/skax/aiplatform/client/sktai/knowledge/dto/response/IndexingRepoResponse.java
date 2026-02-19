package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * SKTAI Knowledge Repository 인덱싱 작업 응답 DTO
 * 
 * <p>Repository의 인덱싱 작업 결과를 제공하는 응답 데이터 구조입니다.
 * 인덱싱 시작, 진행 상황, 완료 결과 등 인덱싱 생명주기 전반의 정보를 포함합니다.</p>
 * 
 * <h3>인덱싱 작업 정보:</h3>
 * <ul>
 *   <li><strong>작업 상태</strong>: 시작, 진행 중, 완료, 실패, 취소 등</li>
 *   <li><strong>진행 정보</strong>: 처리된 Document 수, 진행률, 예상 완료 시간</li>
 *   <li><strong>성능 지표</strong>: 처리 속도, 오류율, 리소스 사용량</li>
 *   <li><strong>결과 요약</strong>: 성공/실패 통계, 생성된 벡터 수</li>
 *   <li><strong>오류 정보</strong>: 실패한 Document, 오류 원인, 복구 방법</li>
 * </ul>
 * 
 * <h3>활용 사례:</h3>
 * <ul>
 *   <li><strong>진행 모니터링</strong>: 실시간 인덱싱 진행 상황 추적</li>
 *   <li><strong>성능 분석</strong>: 인덱싱 성능 최적화를 위한 데이터 분석</li>
 *   <li><strong>오류 처리</strong>: 인덱싱 실패 원인 분석 및 복구</li>
 *   <li><strong>용량 계획</strong>: 리소스 사용량 분석 및 계획 수립</li>
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
@Schema(description = "SKTAI Knowledge Repository 인덱싱 작업 응답")
public class IndexingRepoResponse {

    /**
     * 인덱싱 작업 정보
     */
    @JsonProperty("job_info")
    @Schema(description = "인덱싱 작업 기본 정보")
    private IndexingJobInfo jobInfo;

    /**
     * Repository 정보
     */
    @JsonProperty("repository_info")
    @Schema(description = "대상 Repository 정보")
    private TargetRepositoryInfo repositoryInfo;

    /**
     * 진행 상황
     */
    @JsonProperty("progress")
    @Schema(description = "인덱싱 진행 상황")
    private IndexingProgress progress;

    /**
     * 성능 메트릭
     */
    @JsonProperty("performance")
    @Schema(description = "인덱싱 성능 메트릭")
    private IndexingPerformance performance;

    /**
     * 처리 결과
     */
    @JsonProperty("results")
    @Schema(description = "인덱싱 처리 결과")
    private IndexingResults results;

    /**
     * 오류 정보
     */
    @JsonProperty("errors")
    @Schema(description = "오류 및 경고 정보")
    private ErrorInfo errors;

    /**
     * 리소스 사용량
     */
    @JsonProperty("resource_usage")
    @Schema(description = "리소스 사용량 정보")
    private ResourceUsage resourceUsage;

    /**
     * 다음 단계
     */
    @JsonProperty("next_actions")
    @Schema(description = "권장되는 다음 단계")
    private List<String> nextActions;

    // Inner classes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 작업 기본 정보")
    public static class IndexingJobInfo {
        @JsonProperty("job_id")
        @Schema(description = "작업 ID")
        private String jobId;

        @JsonProperty("job_type")
        @Schema(description = "작업 유형")
        private String jobType;

        @JsonProperty("status")
        @Schema(description = "작업 상태")
        private String status;

        @JsonProperty("started_at")
        @Schema(description = "시작 시간")
        private String startedAt;

        @JsonProperty("completed_at")
        @Schema(description = "완료 시간")
        private String completedAt;

        @JsonProperty("estimated_completion")
        @Schema(description = "예상 완료 시간")
        private String estimatedCompletion;

        @JsonProperty("duration_seconds")
        @Schema(description = "소요 시간 (초)")
        private Long durationSeconds;

        @JsonProperty("initiated_by")
        @Schema(description = "작업 시작자")
        private String initiatedBy;

        @JsonProperty("priority")
        @Schema(description = "작업 우선순위")
        private String priority;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "대상 Repository 정보")
    public static class TargetRepositoryInfo {
        @JsonProperty("repo_id")
        @Schema(description = "Repository ID")
        private String repoId;

        @JsonProperty("repo_name")
        @Schema(description = "Repository 이름")
        private String repoName;

        @JsonProperty("embedding_model")
        @Schema(description = "사용된 임베딩 모델")
        private String embeddingModel;

        @JsonProperty("vector_db_type")
        @Schema(description = "벡터 DB 유형")
        private String vectorDbType;

        @JsonProperty("collection_name")
        @Schema(description = "벡터 컬렉션 이름")
        private String collectionName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 진행 상황")
    public static class IndexingProgress {
        @JsonProperty("overall_progress_percent")
        @Schema(description = "전체 진행률 (%)")
        private Double overallProgressPercent;

        @JsonProperty("current_phase")
        @Schema(description = "현재 단계")
        private String currentPhase;

        @JsonProperty("total_documents")
        @Schema(description = "총 Document 수")
        private Long totalDocuments;

        @JsonProperty("processed_documents")
        @Schema(description = "처리된 Document 수")
        private Long processedDocuments;

        @JsonProperty("remaining_documents")
        @Schema(description = "남은 Document 수")
        private Long remainingDocuments;

        @JsonProperty("total_chunks_created")
        @Schema(description = "생성된 총 청크 수")
        private Long totalChunksCreated;

        @JsonProperty("vectors_indexed")
        @Schema(description = "인덱싱된 벡터 수")
        private Long vectorsIndexed;

        @JsonProperty("current_document")
        @Schema(description = "현재 처리 중인 Document")
        private String currentDocument;

        @JsonProperty("phase_details")
        @Schema(description = "단계별 상세 진행 정보")
        private Map<String, Object> phaseDetails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 성능 메트릭")
    public static class IndexingPerformance {
        @JsonProperty("documents_per_second")
        @Schema(description = "Document 처리 속도 (개/초)")
        private Double documentsPerSecond;

        @JsonProperty("chunks_per_second")
        @Schema(description = "청크 처리 속도 (개/초)")
        private Double chunksPerSecond;

        @JsonProperty("vectors_per_second")
        @Schema(description = "벡터 인덱싱 속도 (개/초)")
        private Double vectorsPerSecond;

        @JsonProperty("average_chunk_size")
        @Schema(description = "평균 청크 크기 (문자)")
        private Double averageChunkSize;

        @JsonProperty("embedding_latency_ms")
        @Schema(description = "평균 임베딩 생성 시간 (ms)")
        private Double embeddingLatencyMs;

        @JsonProperty("indexing_latency_ms")
        @Schema(description = "평균 인덱싱 시간 (ms)")
        private Double indexingLatencyMs;

        @JsonProperty("queue_depth")
        @Schema(description = "처리 대기열 깊이")
        private Integer queueDepth;

        @JsonProperty("success_rate")
        @Schema(description = "성공률 (%)")
        private Double successRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱싱 처리 결과")
    public static class IndexingResults {
        @JsonProperty("total_documents_processed")
        @Schema(description = "처리된 총 Document 수")
        private Long totalDocumentsProcessed;

        @JsonProperty("successful_documents")
        @Schema(description = "성공한 Document 수")
        private Long successfulDocuments;

        @JsonProperty("failed_documents")
        @Schema(description = "실패한 Document 수")
        private Long failedDocuments;

        @JsonProperty("skipped_documents")
        @Schema(description = "건너뛴 Document 수")
        private Long skippedDocuments;

        @JsonProperty("total_chunks_created")
        @Schema(description = "생성된 총 청크 수")
        private Long totalChunksCreated;

        @JsonProperty("total_vectors_indexed")
        @Schema(description = "인덱싱된 총 벡터 수")
        private Long totalVectorsIndexed;

        @JsonProperty("collection_size_before")
        @Schema(description = "인덱싱 전 컬렉션 크기")
        private Long collectionSizeBefore;

        @JsonProperty("collection_size_after")
        @Schema(description = "인덱싱 후 컬렉션 크기")
        private Long collectionSizeAfter;

        @JsonProperty("data_size_processed_bytes")
        @Schema(description = "처리된 데이터 크기 (바이트)")
        private Long dataSizeProcessedBytes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "오류 및 경고 정보")
    public static class ErrorInfo {
        @JsonProperty("error_count")
        @Schema(description = "총 오류 수")
        private Integer errorCount;

        @JsonProperty("warning_count")
        @Schema(description = "총 경고 수")
        private Integer warningCount;

        @JsonProperty("failed_documents")
        @Schema(description = "실패한 Document 목록")
        private List<FailedDocumentInfo> failedDocuments;

        @JsonProperty("error_summary")
        @Schema(description = "오류 유형별 요약")
        private Map<String, Integer> errorSummary;

        @JsonProperty("last_error")
        @Schema(description = "마지막 오류 정보")
        private String lastError;

        @JsonProperty("recovery_suggestions")
        @Schema(description = "복구 방법 제안")
        private List<String> recoverySuggestions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "실패한 Document 정보")
    public static class FailedDocumentInfo {
        @JsonProperty("document_id")
        @Schema(description = "Document ID")
        private String documentId;

        @JsonProperty("document_name")
        @Schema(description = "Document 이름")
        private String documentName;

        @JsonProperty("error_type")
        @Schema(description = "오류 유형")
        private String errorType;

        @JsonProperty("error_message")
        @Schema(description = "오류 메시지")
        private String errorMessage;

        @JsonProperty("failed_at")
        @Schema(description = "실패 시간")
        private String failedAt;

        @JsonProperty("retry_count")
        @Schema(description = "재시도 횟수")
        private Integer retryCount;

        @JsonProperty("can_retry")
        @Schema(description = "재시도 가능 여부")
        private Boolean canRetry;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "리소스 사용량 정보")
    public static class ResourceUsage {
        @JsonProperty("peak_cpu_usage_percent")
        @Schema(description = "최대 CPU 사용률 (%)")
        private Double peakCpuUsagePercent;

        @JsonProperty("peak_memory_usage_mb")
        @Schema(description = "최대 메모리 사용량 (MB)")
        private Double peakMemoryUsageMb;

        @JsonProperty("network_data_transferred_mb")
        @Schema(description = "네트워크 전송량 (MB)")
        private Double networkDataTransferredMb;

        @JsonProperty("storage_space_used_mb")
        @Schema(description = "저장 공간 사용량 (MB)")
        private Double storageSpaceUsedMb;

        @JsonProperty("gpu_usage_percent")
        @Schema(description = "GPU 사용률 (%)")
        private Double gpuUsagePercent;

        @JsonProperty("api_calls_made")
        @Schema(description = "API 호출 횟수")
        private Long apiCallsMade;

        @JsonProperty("estimated_cost")
        @Schema(description = "예상 비용")
        private Double estimatedCost;
    }
}
