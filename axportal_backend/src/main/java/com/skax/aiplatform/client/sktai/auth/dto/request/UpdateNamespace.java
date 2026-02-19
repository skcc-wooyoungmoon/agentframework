package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 네임스페이스 수정 요청 DTO
 * 
 * <p>기존 네임스페이스의 리소스 할당량을 수정하기 위한 데이터 구조입니다.
 * CPU, 메모리, GPU 할당량을 개별적으로 조정할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 리소스:</h3>
 * <ul>
 *   <li><strong>CPU 할당량</strong>: CPU 코어 수 증감</li>
 *   <li><strong>메모리 할당량</strong>: 메모리 용량 증감</li>
 *   <li><strong>GPU 할당량</strong>: GPU 개수 증감</li>
 * </ul>
 * 
 * <h3>제약사항:</h3>
 * <ul>
 *   <li>할당량을 줄일 때는 현재 사용량보다 낮게 설정할 수 없음</li>
 *   <li>null 값인 필드는 업데이트되지 않음 (부분 업데이트 지원)</li>
 *   <li>모든 리소스 값은 0보다 커야 함</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * UpdateNamespace namespace = UpdateNamespace.builder()
 *     .cpuQuota(8.0)     // CPU 할당량을 8코어로 증가
 *     .memQuota(16.0)    // 메모리를 16GB로 증가
 *     .gpuQuota(null)    // GPU 할당량은 기존 값 유지
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UpdateClient 프로젝트 수정 요청의 상위 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 네임스페이스 리소스 할당량 수정 정보",
    example = """
        {
          "cpu_quota": 8.0,
          "mem_quota": 16.0,
          "gpu_quota": 2.0
        }
        """
)
public class UpdateNamespace {
    
    /**
     * CPU 할당량
     * 
     * <p>변경할 CPU 코어 수입니다.
     * null로 설정하면 기존 할당량이 유지됩니다.</p>
     * 
     * @implNote 현재 사용량보다 낮게 설정할 수 없습니다.
     */
    @JsonProperty("cpu_quota")
    @Schema(
        description = "CPU 할당량 (코어 수, null이면 기존 값 유지)",
        example = "8.0",
        nullable = true,
        minimum = "0.1"
    )
    private Double cpuQuota;
    
    /**
     * 메모리 할당량
     * 
     * <p>변경할 메모리 용량입니다 (GB 단위).
     * null로 설정하면 기존 할당량이 유지됩니다.</p>
     */
    @JsonProperty("mem_quota")
    @Schema(
        description = "메모리 할당량 (GB 단위, null이면 기존 값 유지)",
        example = "16.0",
        nullable = true,
        minimum = "0.5"
    )
    private Double memQuota;
    
    /**
     * GPU 할당량
     * 
     * <p>변경할 GPU 개수입니다.
     * null로 설정하면 기존 할당량이 유지됩니다.</p>
     */
    @JsonProperty("gpu_quota")
    @Schema(
        description = "GPU 할당량 (개수, null이면 기존 값 유지)",
        example = "2.0",
        nullable = true,
        minimum = "0"
    )
    private Double gpuQuota;
}
