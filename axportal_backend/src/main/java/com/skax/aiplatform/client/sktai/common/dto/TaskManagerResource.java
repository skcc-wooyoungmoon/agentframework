package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Evaluation API Task Manager 리소스 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 작업 실행 시 필요한 리소스 정보를 정의합니다.
 * CPU, 메모리, GPU 할당량과 GPU 타입을 지정할 수 있습니다.</p>
 * 
 * <h3>리소스 설정:</h3>
 * <ul>
 *   <li><strong>CPU</strong>: CPU 코어 수 (cpu_quota)</li>
 *   <li><strong>Memory</strong>: 메모리 크기 GB (mem_quota)</li>
 *   <li><strong>GPU</strong>: GPU 개수 (gpu_quota)</li>
 *   <li><strong>GPU Type</strong>: GPU 타입 (gpu_type)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TaskManagerResource resource = TaskManagerResource.builder()
 *     .cpuQuota(4)
 *     .memQuota(16)
 *     .gpuQuota(2)
 *     .gpuType("H100")
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
    description = "SKTAI Evaluation API Task Manager 리소스 정보",
    example = """
        {
          "cpu_quota": 4,
          "mem_quota": 16,
          "gpu_quota": 2,
          "gpu_type": "H100"
        }
        """
)
public class TaskManagerResource {
    
    /**
     * CPU 할당량
     * 
     * <p>작업 실행 시 할당할 CPU 코어 수입니다.</p>
     * 
     * @apiNote 양의 정수 값이어야 합니다.
     */
    @JsonProperty("cpu_quota")
    @Schema(
        description = "할당할 CPU 코어 수", 
        example = "4",
        required = true,
        minimum = "1"
    )
    private Integer cpuQuota;
    
    /**
     * 메모리 할당량
     * 
     * <p>작업 실행 시 할당할 메모리 크기(GB)입니다.</p>
     * 
     * @apiNote 양의 정수 값이어야 합니다.
     */
    @JsonProperty("mem_quota")
    @Schema(
        description = "할당할 메모리 크기 (GB)", 
        example = "16",
        required = true,
        minimum = "1"
    )
    private Integer memQuota;
    
    /**
     * GPU 할당량
     * 
     * <p>작업 실행 시 할당할 GPU 개수입니다.</p>
     * 
     * @apiNote 0 이상의 정수 값이어야 합니다.
     */
    @JsonProperty("gpu_quota")
    @Schema(
        description = "할당할 GPU 개수", 
        example = "2",
        required = true,
        minimum = "0"
    )
    private Integer gpuQuota;
    
    /**
     * GPU 타입
     * 
     * <p>사용할 GPU의 타입을 지정합니다.</p>
     * 
     * @implNote 일반적으로 H100, A100, V100 등의 GPU 모델명을 사용합니다.
     */
    @JsonProperty("gpu_type")
    @Schema(
        description = "사용할 GPU 타입 (예: H100, A100, V100)", 
        example = "H100",
        required = true
    )
    private String gpuType;
}
