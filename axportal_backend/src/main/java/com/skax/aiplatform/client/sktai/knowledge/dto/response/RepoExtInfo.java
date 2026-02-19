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
 * SKTAI Knowledge External Repository 정보 응답 DTO
 * 
 * <p>External Repository의 상세 정보를 제공하는 응답 데이터 구조입니다.
 * 기본 정보, 연결 설정, 동기화 상태, 성능 지표, 사용 통계 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, 설명, 유형, 상태</li>
 *   <li><strong>연결 설정</strong>: 엔드포인트, 인증 방식, 연결 옵션</li>
 *   <li><strong>동기화 정보</strong>: 동기화 상태, 마지막 동기화 시간, 다음 동기화</li>
 *   <li><strong>데이터 통계</strong>: 동기화된 데이터 수, 크기, 성공률</li>
 *   <li><strong>성능 지표</strong>: 응답 시간, 처리량, 안정성</li>
 *   <li><strong>상태 정보</strong>: 연결 상태, 오류 정보, 알림</li>
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
@Schema(description = "SKTAI Knowledge External Repository 정보 응답")
public class RepoExtInfo {

    /**
     * 기본 정보
     */
    @JsonProperty("basic_info")
    @Schema(description = "External Repository 기본 정보")
    private BasicInfo basicInfo;

    /**
     * 연결 설정
     */
    @JsonProperty("connection_config")
    @Schema(description = "연결 설정 정보")
    private ConnectionConfig connectionConfig;

    /**
     * 동기화 정보
     */
    @JsonProperty("sync_info")
    @Schema(description = "동기화 상태 정보")
    private SyncInfo syncInfo;

    /**
     * 데이터 통계
     */
    @JsonProperty("data_statistics")
    @Schema(description = "데이터 통계")
    private DataStatistics dataStatistics;

    /**
     * 성능 지표
     */
    @JsonProperty("performance_metrics")
    @Schema(description = "성능 지표")
    private PerformanceMetrics performanceMetrics;

    /**
     * 상태 정보
     */
    @JsonProperty("status_info")
    @Schema(description = "현재 상태 정보")
    private StatusInfo statusInfo;

    /**
     * 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Map<String, Object> metadata;

    /**
     * 관리 정보
     */
    @JsonProperty("management_info")
    @Schema(description = "관리 정보")
    private ManagementInfo managementInfo;

    // Inner classes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "기본 정보")
    public static class BasicInfo {
        @JsonProperty("repo_ext_id")
        @Schema(description = "External Repository ID")
        private String repoExtId;

        @JsonProperty("name")
        @Schema(description = "이름")
        private String name;

        @JsonProperty("description")
        @Schema(description = "설명")
        private String description;

        @JsonProperty("type")
        @Schema(description = "External Repository 유형")
        private String type;

        @JsonProperty("enabled")
        @Schema(description = "활성화 상태")
        private Boolean enabled;

        @JsonProperty("priority")
        @Schema(description = "검색 우선순위")
        private Integer priority;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;

        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;

        @JsonProperty("updated_by")
        @Schema(description = "수정자")
        private String updatedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "연결 설정 정보")
    public static class ConnectionConfig {
        @JsonProperty("endpoint")
        @Schema(description = "엔드포인트 URL")
        private String endpoint;

        @JsonProperty("auth_type")
        @Schema(description = "인증 방식")
        private String authType;

        @JsonProperty("connection_timeout")
        @Schema(description = "연결 타임아웃 (ms)")
        private Integer connectionTimeout;

        @JsonProperty("read_timeout")
        @Schema(description = "읽기 타임아웃 (ms)")
        private Integer readTimeout;

        @JsonProperty("max_retries")
        @Schema(description = "최대 재시도 횟수")
        private Integer maxRetries;

        @JsonProperty("connection_pool_size")
        @Schema(description = "연결 풀 크기")
        private Integer connectionPoolSize;

        @JsonProperty("ssl_enabled")
        @Schema(description = "SSL 사용 여부")
        private Boolean sslEnabled;

        @JsonProperty("ssl_verify")
        @Schema(description = "SSL 인증서 검증 여부")
        private Boolean sslVerify;

        @JsonProperty("last_tested_at")
        @Schema(description = "마지막 연결 테스트 시간")
        private String lastTestedAt;

        @JsonProperty("test_result")
        @Schema(description = "마지막 테스트 결과")
        private String testResult;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "동기화 정보")
    public static class SyncInfo {
        @JsonProperty("sync_enabled")
        @Schema(description = "동기화 활성화 여부")
        private Boolean syncEnabled;

        @JsonProperty("sync_mode")
        @Schema(description = "동기화 모드")
        private String syncMode;

        @JsonProperty("sync_interval")
        @Schema(description = "동기화 주기")
        private String syncInterval;

        @JsonProperty("last_sync_at")
        @Schema(description = "마지막 동기화 시간")
        private String lastSyncAt;

        @JsonProperty("next_sync_at")
        @Schema(description = "다음 동기화 예정 시간")
        private String nextSyncAt;

        @JsonProperty("sync_status")
        @Schema(description = "현재 동기화 상태")
        private String syncStatus;

        @JsonProperty("last_sync_duration_ms")
        @Schema(description = "마지막 동기화 소요 시간 (ms)")
        private Long lastSyncDurationMs;

        @JsonProperty("sync_success_rate")
        @Schema(description = "동기화 성공률 (%)")
        private Double syncSuccessRate;

        @JsonProperty("batch_size")
        @Schema(description = "배치 크기")
        private Integer batchSize;

        @JsonProperty("incremental_sync_supported")
        @Schema(description = "증분 동기화 지원 여부")
        private Boolean incrementalSyncSupported;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "데이터 통계")
    public static class DataStatistics {
        @JsonProperty("total_records")
        @Schema(description = "총 레코드 수")
        private Long totalRecords;

        @JsonProperty("synced_records")
        @Schema(description = "동기화된 레코드 수")
        private Long syncedRecords;

        @JsonProperty("failed_records")
        @Schema(description = "동기화 실패 레코드 수")
        private Long failedRecords;

        @JsonProperty("last_added_records")
        @Schema(description = "마지막 동기화에서 추가된 레코드 수")
        private Long lastAddedRecords;

        @JsonProperty("last_updated_records")
        @Schema(description = "마지막 동기화에서 수정된 레코드 수")
        private Long lastUpdatedRecords;

        @JsonProperty("last_deleted_records")
        @Schema(description = "마지막 동기화에서 삭제된 레코드 수")
        private Long lastDeletedRecords;

        @JsonProperty("total_data_size_bytes")
        @Schema(description = "총 데이터 크기 (바이트)")
        private Long totalDataSizeBytes;

        @JsonProperty("average_record_size_bytes")
        @Schema(description = "평균 레코드 크기 (바이트)")
        private Double averageRecordSizeBytes;

        @JsonProperty("data_freshness_hours")
        @Schema(description = "데이터 신선도 (시간)")
        private Double dataFreshnessHours;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "성능 지표")
    public static class PerformanceMetrics {
        @JsonProperty("average_response_time_ms")
        @Schema(description = "평균 응답 시간 (ms)")
        private Double averageResponseTimeMs;

        @JsonProperty("p95_response_time_ms")
        @Schema(description = "95퍼센타일 응답 시간 (ms)")
        private Double p95ResponseTimeMs;

        @JsonProperty("throughput_records_per_second")
        @Schema(description = "처리량 (레코드/초)")
        private Double throughputRecordsPerSecond;

        @JsonProperty("error_rate_percent")
        @Schema(description = "오류율 (%)")
        private Double errorRatePercent;

        @JsonProperty("availability_percent")
        @Schema(description = "가용성 (%)")
        private Double availabilityPercent;

        @JsonProperty("connection_success_rate")
        @Schema(description = "연결 성공률 (%)")
        private Double connectionSuccessRate;

        @JsonProperty("data_quality_score")
        @Schema(description = "데이터 품질 점수 (0-100)")
        private Double dataQualityScore;

        @JsonProperty("last_measured_at")
        @Schema(description = "마지막 측정 시간")
        private String lastMeasuredAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "상태 정보")
    public static class StatusInfo {
        @JsonProperty("overall_status")
        @Schema(description = "전체 상태")
        private String overallStatus;

        @JsonProperty("connection_status")
        @Schema(description = "연결 상태")
        private String connectionStatus;

        @JsonProperty("sync_status")
        @Schema(description = "동기화 상태")
        private String syncStatus;

        @JsonProperty("health_score")
        @Schema(description = "상태 점수 (0-100)")
        private Double healthScore;

        @JsonProperty("last_error")
        @Schema(description = "마지막 오류")
        private String lastError;

        @JsonProperty("last_error_at")
        @Schema(description = "마지막 오류 발생 시간")
        private String lastErrorAt;

        @JsonProperty("warning_count")
        @Schema(description = "경고 수")
        private Integer warningCount;

        @JsonProperty("active_alerts")
        @Schema(description = "활성 알림 목록")
        private List<String> activeAlerts;

        @JsonProperty("maintenance_mode")
        @Schema(description = "유지보수 모드 여부")
        private Boolean maintenanceMode;

        @JsonProperty("next_health_check")
        @Schema(description = "다음 상태 확인 시간")
        private String nextHealthCheck;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "관리 정보")
    public static class ManagementInfo {
        @JsonProperty("backup_enabled")
        @Schema(description = "백업 활성화 여부")
        private Boolean backupEnabled;

        @JsonProperty("last_backup_at")
        @Schema(description = "마지막 백업 시간")
        private String lastBackupAt;

        @JsonProperty("monitoring_enabled")
        @Schema(description = "모니터링 활성화 여부")
        private Boolean monitoringEnabled;

        @JsonProperty("log_retention_days")
        @Schema(description = "로그 보관 기간 (일)")
        private Integer logRetentionDays;

        @JsonProperty("auto_recovery_enabled")
        @Schema(description = "자동 복구 활성화 여부")
        private Boolean autoRecoveryEnabled;

        @JsonProperty("maintenance_window")
        @Schema(description = "유지보수 시간대")
        private String maintenanceWindow;

        @JsonProperty("contact_info")
        @Schema(description = "담당자 연락처")
        private String contactInfo;

        @JsonProperty("documentation_url")
        @Schema(description = "문서화 URL")
        private String documentationUrl;
    }
}
