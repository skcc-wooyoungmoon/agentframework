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
 * SKTAI Knowledge External Repository 테스트 응답 DTO
 * 
 * <p>External Repository의 연결 및 기능 테스트 결과를 제공하는 응답 데이터 구조입니다.
 * 연결 상태, 인증 검증, 데이터 조회, 성능 등 다양한 측면의 테스트 결과를 포함합니다.</p>
 * 
 * <h3>테스트 결과 정보:</h3>
 * <ul>
 *   <li><strong>전체 결과</strong>: 테스트 성공/실패 여부 및 전체 점수</li>
 *   <li><strong>개별 테스트</strong>: 각 기능별 테스트 결과 상세</li>
 *   <li><strong>성능 지표</strong>: 응답 시간, 처리량, 안정성 측정</li>
 *   <li><strong>문제점</strong>: 발견된 이슈 및 권장 해결책</li>
 *   <li><strong>샘플 데이터</strong>: 테스트 중 조회된 실제 데이터 샘플</li>
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
@Schema(description = "SKTAI Knowledge External Repository 테스트 응답")
public class RepoExtTestResponse {

    /**
     * 테스트 기본 정보
     */
    @JsonProperty("test_info")
    @Schema(description = "테스트 기본 정보")
    private TestInfo testInfo;

    /**
     * 전체 테스트 결과
     */
    @JsonProperty("overall_result")
    @Schema(description = "전체 테스트 결과")
    private OverallResult overallResult;

    /**
     * 개별 테스트 결과
     */
    @JsonProperty("test_results")
    @Schema(description = "개별 테스트 결과 목록")
    private List<IndividualTestResult> testResults;

    /**
     * 성능 측정 결과
     */
    @JsonProperty("performance_metrics")
    @Schema(description = "성능 측정 결과")
    private PerformanceMetrics performanceMetrics;

    /**
     * 샘플 데이터
     */
    @JsonProperty("sample_data")
    @Schema(description = "테스트 중 조회된 샘플 데이터")
    private SampleData sampleData;

    /**
     * 발견된 이슈
     */
    @JsonProperty("issues_found")
    @Schema(description = "발견된 이슈 목록")
    private List<IssueInfo> issuesFound;

    /**
     * 권장사항
     */
    @JsonProperty("recommendations")
    @Schema(description = "개선 권장사항")
    private List<String> recommendations;

    /**
     * 추가 정보
     */
    @JsonProperty("additional_info")
    @Schema(description = "추가 정보")
    private Map<String, Object> additionalInfo;

    // Inner classes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "테스트 기본 정보")
    public static class TestInfo {
        @JsonProperty("test_id")
        @Schema(description = "테스트 ID")
        private String testId;

        @JsonProperty("repo_ext_id")
        @Schema(description = "External Repository ID")
        private String repoExtId;

        @JsonProperty("test_mode")
        @Schema(description = "테스트 모드")
        private String testMode;

        @JsonProperty("started_at")
        @Schema(description = "테스트 시작 시간")
        private String startedAt;

        @JsonProperty("completed_at")
        @Schema(description = "테스트 완료 시간")
        private String completedAt;

        @JsonProperty("duration_ms")
        @Schema(description = "테스트 소요 시간 (ms)")
        private Long durationMs;

        @JsonProperty("initiated_by")
        @Schema(description = "테스트 실행자")
        private String initiatedBy;

        @JsonProperty("test_environment")
        @Schema(description = "테스트 환경 정보")
        private Map<String, Object> testEnvironment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "전체 테스트 결과")
    public static class OverallResult {
        @JsonProperty("status")
        @Schema(description = "전체 테스트 상태")
        private String status;

        @JsonProperty("success")
        @Schema(description = "테스트 성공 여부")
        private Boolean success;

        @JsonProperty("overall_score")
        @Schema(description = "전체 점수 (0-100)")
        private Double overallScore;

        @JsonProperty("passed_tests")
        @Schema(description = "통과한 테스트 수")
        private Integer passedTests;

        @JsonProperty("failed_tests")
        @Schema(description = "실패한 테스트 수")
        private Integer failedTests;

        @JsonProperty("skipped_tests")
        @Schema(description = "건너뛴 테스트 수")
        private Integer skippedTests;

        @JsonProperty("total_tests")
        @Schema(description = "전체 테스트 수")
        private Integer totalTests;

        @JsonProperty("ready_for_production")
        @Schema(description = "운영 환경 사용 준비 여부")
        private Boolean readyForProduction;

        @JsonProperty("summary")
        @Schema(description = "결과 요약")
        private String summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 테스트 결과")
    public static class IndividualTestResult {
        @JsonProperty("test_name")
        @Schema(description = "테스트 이름")
        private String testName;

        @JsonProperty("test_category")
        @Schema(description = "테스트 카테고리")
        private String testCategory;

        @JsonProperty("status")
        @Schema(description = "테스트 상태")
        private String status;

        @JsonProperty("success")
        @Schema(description = "성공 여부")
        private Boolean success;

        @JsonProperty("score")
        @Schema(description = "테스트 점수")
        private Double score;

        @JsonProperty("duration_ms")
        @Schema(description = "테스트 소요 시간 (ms)")
        private Long durationMs;

        @JsonProperty("message")
        @Schema(description = "테스트 결과 메시지")
        private String message;

        @JsonProperty("details")
        @Schema(description = "상세 결과")
        private Map<String, Object> details;

        @JsonProperty("error_info")
        @Schema(description = "오류 정보 (실패시)")
        private String errorInfo;

        @JsonProperty("retry_count")
        @Schema(description = "재시도 횟수")
        private Integer retryCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "성능 측정 결과")
    public static class PerformanceMetrics {
        @JsonProperty("connection_time_ms")
        @Schema(description = "연결 시간 (ms)")
        private Double connectionTimeMs;

        @JsonProperty("authentication_time_ms")
        @Schema(description = "인증 시간 (ms)")
        private Double authenticationTimeMs;

        @JsonProperty("average_response_time_ms")
        @Schema(description = "평균 응답 시간 (ms)")
        private Double averageResponseTimeMs;

        @JsonProperty("max_response_time_ms")
        @Schema(description = "최대 응답 시간 (ms)")
        private Double maxResponseTimeMs;

        @JsonProperty("min_response_time_ms")
        @Schema(description = "최소 응답 시간 (ms)")
        private Double minResponseTimeMs;

        @JsonProperty("throughput_qps")
        @Schema(description = "처리량 (쿼리/초)")
        private Double throughputQps;

        @JsonProperty("error_rate_percent")
        @Schema(description = "오류율 (%)")
        private Double errorRatePercent;

        @JsonProperty("timeout_count")
        @Schema(description = "타임아웃 발생 횟수")
        private Integer timeoutCount;

        @JsonProperty("concurrent_connections_tested")
        @Schema(description = "테스트된 동시 연결 수")
        private Integer concurrentConnectionsTested;

        @JsonProperty("stability_score")
        @Schema(description = "안정성 점수 (0-100)")
        private Double stabilityScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "샘플 데이터")
    public static class SampleData {
        @JsonProperty("total_records_found")
        @Schema(description = "발견된 총 레코드 수")
        private Long totalRecordsFound;

        @JsonProperty("sample_size")
        @Schema(description = "샘플 크기")
        private Integer sampleSize;

        @JsonProperty("sample_records")
        @Schema(description = "샘플 레코드")
        private List<Map<String, Object>> sampleRecords;

        @JsonProperty("data_types_detected")
        @Schema(description = "감지된 데이터 타입")
        private Map<String, String> dataTypesDetected;

        @JsonProperty("field_statistics")
        @Schema(description = "필드 통계")
        private Map<String, Object> fieldStatistics;

        @JsonProperty("data_quality_score")
        @Schema(description = "데이터 품질 점수 (0-100)")
        private Double dataQualityScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "이슈 정보")
    public static class IssueInfo {
        @JsonProperty("issue_id")
        @Schema(description = "이슈 ID")
        private String issueId;

        @JsonProperty("severity")
        @Schema(description = "심각도")
        private String severity;

        @JsonProperty("category")
        @Schema(description = "이슈 카테고리")
        private String category;

        @JsonProperty("title")
        @Schema(description = "이슈 제목")
        private String title;

        @JsonProperty("description")
        @Schema(description = "이슈 설명")
        private String description;

        @JsonProperty("affected_component")
        @Schema(description = "영향받은 컴포넌트")
        private String affectedComponent;

        @JsonProperty("suggested_fix")
        @Schema(description = "권장 해결책")
        private String suggestedFix;

        @JsonProperty("impact")
        @Schema(description = "영향도")
        private String impact;

        @JsonProperty("detected_at")
        @Schema(description = "발견 시간")
        private String detectedAt;
    }
}
