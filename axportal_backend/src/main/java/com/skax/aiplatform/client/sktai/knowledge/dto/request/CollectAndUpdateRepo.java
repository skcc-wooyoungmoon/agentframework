package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * SKTAI Knowledge S3 수집 및 Repository 업데이트 요청 DTO
 * 
 * <p>S3에서 파일을 수집하고 Knowledge Repository의 변경사항을 반영하여 인덱싱을 수행하는 요청 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge S3 수집 및 Repository 업데이트 요청 정보")
public class CollectAndUpdateRepo {

    /**
     * S3 버킷 이름
     */
    @JsonProperty("s3_bucket_name")
    @Schema(description = "S3 버킷 이름", example = "my-knowledge-bucket", required = true)
    private String s3BucketName;

    /**
     * S3 객체 접두사
     */
    @JsonProperty("s3_prefix")
    @Schema(description = "S3 객체 키 접두사", example = "documents/kb/")
    private String s3Prefix;

    /**
     * 강제 재인덱싱 여부
     */
    @JsonProperty("force_reindex")
    @Schema(description = "강제 재인덱싱 여부", example = "false")
    private Boolean forceReindex;

    /**
     * 배치 크기
     */
    @JsonProperty("batch_size")
    @Schema(description = "배치 크기", example = "100")
    private Integer batchSize;

    /**
     * 최대 파일 크기 (바이트)
     */
    @JsonProperty("max_file_size")
    @Schema(description = "최대 파일 크기 (바이트)", example = "10485760")
    private Long maxFileSize;

    /**
     * 포함 패턴 목록
     */
    @JsonProperty("include_patterns")
    @Schema(description = "포함할 파일의 glob 패턴 목록")
    private List<String> includePatterns;

    /**
     * 제외 패턴 목록
     */
    @JsonProperty("exclude_patterns")
    @Schema(description = "제외할 파일의 glob 패턴 목록")
    private List<String> excludePatterns;

    /**
     * 동시 처리 스레드 수
     */
    @JsonProperty("parallel_threads")
    @Schema(description = "동시 처리 스레드 수", example = "4")
    private Integer parallelThreads;

    /**
     * 처리 타임아웃 (초)
     */
    @JsonProperty("timeout_seconds")
    @Schema(description = "처리 타임아웃 (초)", example = "3600")
    private Integer timeoutSeconds;

    /**
     * 진행 상황 알림 여부
     */
    @JsonProperty("enable_progress_notification")
    @Schema(description = "진행 상황 실시간 알림 여부", example = "true")
    private Boolean enableProgressNotification;

    /**
     * 작업 수행자 정보
     */
    @JsonProperty("initiated_by")
    @Schema(description = "작업 수행자 정보", example = "admin@example.com")
    private String initiatedBy;

    /**
     * 작업 설명
     */
    @JsonProperty("description")
    @Schema(description = "작업 설명", example = "월간 정기 동기화")
    private String description;
}
