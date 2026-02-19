package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Model Benchmark 결과 생성 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark 결과를 생성하기 위한 요청 데이터 구조입니다.
 * 모델 정보, 벤치마크 정보, 작업 결과, 메트릭 등을 포함합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>benchmark_id</strong>: 벤치마크 식별자</li>
 *   <li><strong>task</strong>: 수행된 작업</li>
 *   <li><strong>metric</strong>: 평가 메트릭</li>
 *   <li><strong>metric_result</strong>: 메트릭 결과값</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelBenchmarkResultCreateRequest request = ModelBenchmarkResultCreateRequest.builder()
 *     .benchmarkId(1)
 *     .task("text_generation")
 *     .metric("bleu_score")
 *     .metricResult(0.85)
 *     .modelId("model-123")
 *     .versionId("version-456")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Benchmark 결과 생성 요청 정보",
    example = """
        {
          "id": null,
          "model_id": "550e8400-e29b-41d4-a716-446655440000",
          "version_id": "550e8400-e29b-41d4-a716-446655440001",
          "benchmark_id": 1,
          "task": "text_generation",
          "metric": "bleu_score",
          "metric_result": 0.85,
          "updated_at": "2025-08-15T10:30:00",
          "policy": null
        }
        """
)
public class ModelBenchmarkResultCreateRequest {
    
    /**
     * 결과 식별자
     * 
     * <p>기존 결과를 업데이트하는 경우 사용하는 ID입니다.
     * 새로 생성하는 경우 null로 설정합니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "결과 ID (업데이트 시에만 사용, 신규 생성 시 null)", 
        example = "null"
    )
    private Integer id;
    
    /**
     * 모델 식별자
     * 
     * <p>벤치마크를 수행한 모델의 UUID입니다.
     * Fine-tuned 모델이 아닌 경우 null로 설정할 수 있습니다.</p>
     */
    @JsonProperty("model_id")
    @Schema(
        description = "벤치마크를 수행한 모델의 UUID", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String modelId;
    
    /**
     * 모델 버전 식별자
     * 
     * <p>사용된 모델의 버전 UUID입니다.
     * Fine-tuned 모델이 아닌 경우 null로 설정할 수 있습니다.</p>
     */
    @JsonProperty("version_id")
    @Schema(
        description = "사용된 모델 버전의 UUID", 
        example = "550e8400-e29b-41d4-a716-446655440001",
        format = "uuid"
    )
    private String versionId;
    
    /**
     * 벤치마크 식별자
     * 
     * <p>결과가 속한 벤치마크의 ID입니다.</p>
     */
    @JsonProperty("benchmark_id")
    @Schema(
        description = "벤치마크 ID", 
        example = "1",
        required = true
    )
    private Integer benchmarkId;
    
    /**
     * 작업명
     * 
     * <p>수행된 구체적인 작업의 이름입니다.</p>
     */
    @JsonProperty("task")
    @Schema(
        description = "수행된 작업명", 
        example = "text_generation",
        required = true
    )
    private String task;
    
    /**
     * 평가 메트릭
     * 
     * <p>결과 측정에 사용된 메트릭의 이름입니다.</p>
     */
    @JsonProperty("metric")
    @Schema(
        description = "평가 메트릭명", 
        example = "bleu_score",
        required = true
    )
    private String metric;
    
    /**
     * 메트릭 결과값
     * 
     * <p>평가 메트릭을 통해 측정된 수치 결과입니다.</p>
     */
    @JsonProperty("metric_result")
    @Schema(
        description = "메트릭 측정 결과값", 
        example = "0.85",
        required = true
    )
    private Double metricResult;
    
    /**
     * 업데이트 시간
     * 
     * <p>결과가 마지막으로 업데이트된 시간입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "결과 업데이트 시간", 
        example = "2025-08-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
    
    /**
     * 정책 설정
     * 
     * <p>결과 접근 권한 및 보안 정책입니다.
     * 필요한 경우에만 설정합니다.</p>
     */
    @JsonProperty("policy")
    @Schema(
        description = "결과 접근 권한 정책 (선택사항)", 
        example = "null"
    )
    private PolicyPayload policy;
}
