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
 * SKTAI Knowledge Repository Retrieval 정보 응답 DTO
 * 
 * <p>Repository의 검색 및 조회 관련 정보를 제공하는 응답 데이터 구조입니다.
 * 검색 설정, 성능 지표, 필터링 옵션, 검색 가능한 필드 등의 정보를 포함합니다.</p>
 * 
 * <h3>제공 정보:</h3>
 * <ul>
 *   <li><strong>검색 설정</strong>: 임베딩 모델, 유사도 임계값, 결과 개수 등</li>
 *   <li><strong>필터링 옵션</strong>: 사용 가능한 메타데이터 필터 및 조건</li>
 *   <li><strong>검색 필드</strong>: 검색 가능한 Document 필드 목록</li>
 *   <li><strong>성능 정보</strong>: 평균 응답 시간, 인덱스 상태 등</li>
 *   <li><strong>사용 통계</strong>: 검색 빈도, 인기 쿼리 등</li>
 * </ul>
 * 
 * <h3>활용 사례:</h3>
 * <ul>
 *   <li><strong>검색 클라이언트 구성</strong>: 검색 UI 구성을 위한 메타데이터 제공</li>
 *   <li><strong>성능 최적화</strong>: 검색 성능 분석 및 튜닝 기준 정보</li>
 *   <li><strong>사용자 가이드</strong>: 효과적인 검색 방법 안내</li>
 *   <li><strong>모니터링</strong>: 검색 시스템 상태 및 품질 관리</li>
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
@Schema(description = "SKTAI Knowledge Repository Retrieval 정보 응답")
public class RepoRetrievalInfo {

    /**
     * Repository 기본 정보
     */
    @JsonProperty("repository_info")
    @Schema(description = "Repository 기본 정보")
    private RepositoryBasicInfo repositoryInfo;

    /**
     * 검색 설정
     */
    @JsonProperty("search_config")
    @Schema(description = "검색 설정 정보")
    private SearchConfig searchConfig;

    /**
     * 사용 가능한 필터
     */
    @JsonProperty("available_filters")
    @Schema(description = "사용 가능한 필터 목록")
    private List<FilterInfo> availableFilters;

    /**
     * 검색 가능한 필드
     */
    @JsonProperty("searchable_fields")
    @Schema(description = "검색 가능한 필드 정보")
    private List<SearchableField> searchableFields;

    /**
     * 성능 정보
     */
    @JsonProperty("performance_info")
    @Schema(description = "검색 성능 정보")
    private RetrievalPerformanceInfo performanceInfo;

    /**
     * 사용 통계
     */
    @JsonProperty("usage_statistics")
    @Schema(description = "검색 사용 통계")
    private UsageStatistics usageStatistics;

    /**
     * 인덱스 상태
     */
    @JsonProperty("index_status")
    @Schema(description = "인덱스 상태 정보")
    private IndexStatusInfo indexStatus;

    /**
     * 권장 설정
     */
    @JsonProperty("recommended_settings")
    @Schema(description = "검색 최적화를 위한 권장 설정")
    private RecommendedSettings recommendedSettings;

    // Inner classes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Repository 기본 정보")
    public static class RepositoryBasicInfo {
        @JsonProperty("repo_id")
        @Schema(description = "Repository ID")
        private String repoId;

        @JsonProperty("name")
        @Schema(description = "Repository 이름")
        private String name;

        @JsonProperty("description")
        @Schema(description = "Repository 설명")
        private String description;

        @JsonProperty("total_documents")
        @Schema(description = "총 Document 수")
        private Long totalDocuments;

        @JsonProperty("total_chunks")
        @Schema(description = "총 청크 수")
        private Long totalChunks;

        @JsonProperty("last_updated")
        @Schema(description = "마지막 업데이트 시간")
        private String lastUpdated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "검색 설정 정보")
    public static class SearchConfig {
        @JsonProperty("embedding_model")
        @Schema(description = "임베딩 모델 이름")
        private String embeddingModel;

        @JsonProperty("vector_dimension")
        @Schema(description = "벡터 차원")
        private Integer vectorDimension;

        @JsonProperty("similarity_metric")
        @Schema(description = "유사도 측정 방식")
        private String similarityMetric;

        @JsonProperty("default_top_k")
        @Schema(description = "기본 검색 결과 개수")
        private Integer defaultTopK;

        @JsonProperty("max_top_k")
        @Schema(description = "최대 검색 결과 개수")
        private Integer maxTopK;

        @JsonProperty("similarity_threshold")
        @Schema(description = "기본 유사도 임계값")
        private Double similarityThreshold;

        @JsonProperty("support_hybrid_search")
        @Schema(description = "하이브리드 검색 지원 여부")
        private Boolean supportHybridSearch;

        @JsonProperty("support_reranking")
        @Schema(description = "재순위화 지원 여부")
        private Boolean supportReranking;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "필터 정보")
    public static class FilterInfo {
        @JsonProperty("field_name")
        @Schema(description = "필터 필드 이름")
        private String fieldName;

        @JsonProperty("field_type")
        @Schema(description = "필드 데이터 타입")
        private String fieldType;

        @JsonProperty("display_name")
        @Schema(description = "화면 표시 이름")
        private String displayName;

        @JsonProperty("description")
        @Schema(description = "필터 설명")
        private String description;

        @JsonProperty("possible_values")
        @Schema(description = "가능한 값 목록")
        private List<String> possibleValues;

        @JsonProperty("value_range")
        @Schema(description = "값 범위 (숫자/날짜 타입)")
        private ValueRange valueRange;

        @JsonProperty("is_faceted")
        @Schema(description = "패싯 검색 지원 여부")
        private Boolean isFaceted;

        @JsonProperty("usage_frequency")
        @Schema(description = "사용 빈도")
        private Double usageFrequency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "검색 가능한 필드")
    public static class SearchableField {
        @JsonProperty("field_name")
        @Schema(description = "필드 이름")
        private String fieldName;

        @JsonProperty("field_type")
        @Schema(description = "필드 타입")
        private String fieldType;

        @JsonProperty("search_type")
        @Schema(description = "검색 타입")
        private String searchType;

        @JsonProperty("weight")
        @Schema(description = "검색 가중치")
        private Double weight;

        @JsonProperty("is_indexable")
        @Schema(description = "인덱싱 가능 여부")
        private Boolean isIndexable;

        @JsonProperty("sample_content")
        @Schema(description = "샘플 내용")
        private String sampleContent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "검색 성능 정보")
    public static class RetrievalPerformanceInfo {
        @JsonProperty("average_latency_ms")
        @Schema(description = "평균 검색 지연시간 (ms)")
        private Double averageLatencyMs;

        @JsonProperty("p95_latency_ms")
        @Schema(description = "95퍼센타일 지연시간 (ms)")
        private Double p95LatencyMs;

        @JsonProperty("average_recall")
        @Schema(description = "평균 Recall 점수")
        private Double averageRecall;

        @JsonProperty("average_precision")
        @Schema(description = "평균 Precision 점수")
        private Double averagePrecision;

        @JsonProperty("throughput_qps")
        @Schema(description = "처리량 (쿼리/초)")
        private Double throughputQps;

        @JsonProperty("cache_hit_ratio")
        @Schema(description = "캐시 적중률")
        private Double cacheHitRatio;

        @JsonProperty("index_efficiency")
        @Schema(description = "인덱스 효율성 점수")
        private Double indexEfficiency;

        @JsonProperty("last_benchmark_date")
        @Schema(description = "마지막 벤치마크 일시")
        private String lastBenchmarkDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사용 통계")
    public static class UsageStatistics {
        @JsonProperty("total_searches")
        @Schema(description = "총 검색 횟수")
        private Long totalSearches;

        @JsonProperty("daily_average_searches")
        @Schema(description = "일평균 검색 횟수")
        private Double dailyAverageSearches;

        @JsonProperty("popular_queries")
        @Schema(description = "인기 검색어 목록")
        private List<String> popularQueries;

        @JsonProperty("common_filter_combinations")
        @Schema(description = "자주 사용되는 필터 조합")
        private List<Map<String, Object>> commonFilterCombinations;

        @JsonProperty("peak_usage_hours")
        @Schema(description = "사용량 피크 시간대")
        private List<Integer> peakUsageHours;

        @JsonProperty("average_results_returned")
        @Schema(description = "평균 반환 결과 수")
        private Double averageResultsReturned;

        @JsonProperty("zero_result_rate")
        @Schema(description = "무결과 검색 비율")
        private Double zeroResultRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인덱스 상태 정보")
    public static class IndexStatusInfo {
        @JsonProperty("index_health")
        @Schema(description = "인덱스 상태")
        private String indexHealth;

        @JsonProperty("last_rebuild_date")
        @Schema(description = "마지막 재구축 일시")
        private String lastRebuildDate;

        @JsonProperty("index_coverage")
        @Schema(description = "인덱스 커버리지 (%)")
        private Double indexCoverage;

        @JsonProperty("fragmentation_level")
        @Schema(description = "조각화 수준")
        private Double fragmentationLevel;

        @JsonProperty("optimization_needed")
        @Schema(description = "최적화 필요 여부")
        private Boolean optimizationNeeded;

        @JsonProperty("next_maintenance_date")
        @Schema(description = "다음 유지보수 예정일")
        private String nextMaintenanceDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "권장 설정")
    public static class RecommendedSettings {
        @JsonProperty("optimal_top_k")
        @Schema(description = "권장 검색 결과 개수")
        private Integer optimalTopK;

        @JsonProperty("recommended_similarity_threshold")
        @Schema(description = "권장 유사도 임계값")
        private Double recommendedSimilarityThreshold;

        @JsonProperty("suggested_filters")
        @Schema(description = "권장 필터 목록")
        private List<String> suggestedFilters;

        @JsonProperty("performance_tips")
        @Schema(description = "성능 향상 팁")
        private List<String> performanceTips;

        @JsonProperty("query_optimization_hints")
        @Schema(description = "쿼리 최적화 힌트")
        private List<String> queryOptimizationHints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "값 범위")
    public static class ValueRange {
        @JsonProperty("min_value")
        @Schema(description = "최소값")
        private Object minValue;

        @JsonProperty("max_value")
        @Schema(description = "최대값")
        private Object maxValue;

        @JsonProperty("data_type")
        @Schema(description = "데이터 타입")
        private String dataType;
    }
}
