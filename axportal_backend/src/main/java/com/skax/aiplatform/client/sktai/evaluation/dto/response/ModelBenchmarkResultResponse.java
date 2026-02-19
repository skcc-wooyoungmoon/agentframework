package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 모델 벤치마크 결과 응답 DTO
 * 
 * <p>개별 모델 벤치마크 결과 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 벤치마크 결과 응답")
public class ModelBenchmarkResultResponse {
    
    @JsonProperty("id")
    @Schema(description = "벤치마크 결과 ID", example = "1")
    private Integer id;
    
    @JsonProperty("model_benchmark_id")
    @Schema(description = "모델 벤치마크 ID", example = "100")
    private Integer modelBenchmarkId;
    
    @JsonProperty("model_id")
    @Schema(description = "모델 ID", example = "model-123")
    private String modelId;
    
    @JsonProperty("score")
    @Schema(description = "벤치마크 점수", example = "0.85")
    private Double score;
    
    @JsonProperty("accuracy")
    @Schema(description = "정확도", example = "0.92")
    private Double accuracy;
    
    @JsonProperty("latency")
    @Schema(description = "지연시간(ms)", example = "150")
    private Integer latency;
    
    @JsonProperty("throughput")
    @Schema(description = "처리량", example = "100")
    private Double throughput;
    
    @JsonProperty("status")
    @Schema(description = "실행 상태", example = "completed")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}
