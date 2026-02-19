package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.TaskManagerResource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Benchmark 작업 생성 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark 작업을 저장하고 시작하기 위한 요청 데이터 구조입니다.
 * 모델 정보, 벤치마크 설정, 리소스 할당 등을 포함합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>model_id</strong>: 벤치마크할 모델 ID</li>
 *   <li><strong>benchmark_id</strong>: 벤치마크 설정 ID</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>Fine-tuned 모델이 아닌 경우 version_id를 null로 설정</li>
 *   <li>GPU 메모리 사용률은 0.0 초과 1.0 이하의 값 사용</li>
 *   <li>최대 모델 길이는 양수 값 사용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelBenchmarkTaskCreateRequest request = ModelBenchmarkTaskCreateRequest.builder()
 *     .modelId("model-123")
 *     .benchmarkId(1)
 *     .dtype("auto")
 *     .gpuMemoryUtilization(0.5)
 *     .maxModelLen(4096)
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
    description = "SKTAI Model Benchmark 작업 생성 요청 정보",
    example = """
        {
          "model_id": "550e8400-e29b-41d4-a716-446655440000",
          "version_id": null,
          "benchmark_id": 1,
          "dtype": "auto",
          "gpu_memory_utilization": 0.5,
          "max_model_len": 4096,
          "resource": {
            "cpu_quota": 4,
            "mem_quota": 16,
            "gpu_quota": 2,
            "gpu_type": "H100"
          }
        }
        """
)
public class ModelBenchmarkTaskCreateRequest {
    
    /**
     * 모델 식별자
     * 
     * <p>벤치마크를 수행할 모델의 UUID입니다.</p>
     */
    @JsonProperty("model_id")
    @Schema(
        description = "벤치마크할 모델의 UUID", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        required = true,
        format = "uuid"
    )
    private String modelId;
    
    /**
     * 모델 버전 식별자
     * 
     * <p>사용할 모델의 버전 UUID입니다.
     * Fine-tuned 모델이 아닌 경우 null로 설정합니다.</p>
     */
    @JsonProperty("version_id")
    @Schema(
        description = "모델 버전 UUID (Fine-tuned 모델이 아닌 경우 null)", 
        example = "null",
        format = "uuid"
    )
    private String versionId;
    
    /**
     * 벤치마크 식별자
     * 
     * <p>실행할 벤치마크의 ID입니다.</p>
     */
    @JsonProperty("benchmark_id")
    @Schema(
        description = "실행할 벤치마크 ID", 
        example = "1",
        required = true
    )
    private Integer benchmarkId;
    
    /**
     * 데이터 타입
     * 
     * <p>모델에서 사용할 데이터 타입을 지정합니다.
     * 일반적으로 "auto"를 사용하여 자동 설정합니다.</p>
     */
    @JsonProperty("dtype")
    @Schema(
        description = "모델 데이터 타입 (자동 설정을 위해 'auto' 사용 권장)", 
        example = "auto",
        defaultValue = "auto"
    )
    @Builder.Default
    private String dtype = "auto";
    
    /**
     * GPU 메모리 사용률
     * 
     * <p>GPU 메모리의 사용 비율을 지정합니다.
     * 0.0 초과 1.0 이하의 값을 사용합니다.</p>
     */
    @JsonProperty("gpu_memory_utilization")
    @Schema(
        description = "GPU 메모리 사용률 (0.0 초과 1.0 이하)", 
        example = "0.5",
        minimum = "0.0",
        maximum = "1.0",
        exclusiveMinimum = true,
        defaultValue = "0.5"
    )
    @Builder.Default
    private Double gpuMemoryUtilization = 0.5;
    
    /**
     * 최대 모델 길이
     * 
     * <p>모델이 처리할 수 있는 최대 토큰 길이입니다.
     * 양수 값을 사용해야 합니다.</p>
     */
    @JsonProperty("max_model_len")
    @Schema(
        description = "모델 최대 토큰 길이 (양수)", 
        example = "4096",
        minimum = "1",
        exclusiveMinimum = true,
        defaultValue = "4096"
    )
    @Builder.Default
    private Integer maxModelLen = 4096;
    
    /**
     * 리소스 설정
     * 
     * <p>작업 실행에 필요한 컴퓨팅 리소스 설정입니다.
     * CPU, 메모리, GPU 할당량과 GPU 타입을 지정할 수 있습니다.</p>
     */
    @JsonProperty("resource")
    @Schema(
        description = "작업 실행용 리소스 설정", 
        defaultValue = "{\"cpu_quota\":4,\"mem_quota\":16,\"gpu_quota\":2,\"gpu_type\":\"H100\"}"
    )
    @Builder.Default
    private TaskManagerResource resource = TaskManagerResource.builder()
            .cpuQuota(4)
            .memQuota(16)
            .gpuQuota(2)
            .gpuType("H100")
            .build();
}
