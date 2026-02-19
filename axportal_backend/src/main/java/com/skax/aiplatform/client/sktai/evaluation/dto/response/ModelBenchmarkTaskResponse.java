package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 모델 벤치마크 태스크 응답 DTO
 * 
 * <p>모델 벤치마크 태스크 실행 결과를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 벤치마크 태스크 응답")
public class ModelBenchmarkTaskResponse {
    
    @JsonProperty("task_id")
    @Schema(description = "태스크 ID", example = "task-123")
    private String taskId;
    
    @JsonProperty("model_benchmark_id")
    @Schema(description = "모델 벤치마크 ID", example = "100")
    private Integer modelBenchmarkId;
    
    @JsonProperty("status")
    @Schema(description = "태스크 상태", example = "running")
    private String status;
    
    @JsonProperty("progress")
    @Schema(description = "진행률 (%)", example = "65")
    private Integer progress;
    
    @JsonProperty("started_at")
    @Schema(description = "시작 시간")
    private LocalDateTime startedAt;
    
    @JsonProperty("estimated_completion")
    @Schema(description = "예상 완료 시간")
    private LocalDateTime estimatedCompletion;
    
    @JsonProperty("message")
    @Schema(description = "상태 메시지", example = "Benchmark task started successfully")
    private String message;
}
