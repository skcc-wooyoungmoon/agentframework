package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 평가 태스크 생성 요청 DTO
 * 
 * <p>새로운 평가 태스크를 생성하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 태스크 생성 요청")
public class EvaluationTaskCreateRequest {
    
    @JsonProperty("evaluation_id")
    @Schema(description = "평가 ID", example = "100", required = true)
    private Integer evaluationId;
    
    @JsonProperty("priority")
    @Schema(description = "태스크 우선순위", example = "HIGH")
    private String priority;
    
    @JsonProperty("timeout_minutes")
    @Schema(description = "타임아웃 시간(분)", example = "60")
    private Integer timeoutMinutes;
    
    @JsonProperty("resources")
    @Schema(description = "필요한 리소스 스펙", example = "gpu-v100")
    private String resources;
    
    @JsonProperty("parameters")
    @Schema(description = "평가 파라미터(JSON 형태)", example = "{\"batch_size\": 32}")
    private String parameters;
}
