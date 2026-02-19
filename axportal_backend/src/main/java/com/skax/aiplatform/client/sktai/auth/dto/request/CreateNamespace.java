package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 네임스페이스 생성 요청 DTO
 * 
 * <p>프로젝트와 연결될 네임스페이스의 리소스 할당량을 설정하는 데이터 구조입니다.
 * 네임스페이스는 프로젝트의 리소스 사용량을 제한하고 격리를 제공합니다.</p>
 * 
 * <h3>리소스 유형:</h3>
 * <ul>
 *   <li><strong>CPU</strong>: CPU 코어 수 (예: 2.0 = 2코어)</li>
 *   <li><strong>Memory</strong>: 메모리 용량 (GB 단위)</li>
 *   <li><strong>GPU</strong>: GPU 개수 (정수값, 0.5 단위 지원)</li>
 *   <li><strong>Private Volume</strong>: 전용 저장소 볼륨 (선택사항)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CreateNamespace namespace = CreateNamespace.builder()
 *     .cpuQuota(4.0)      // 4 CPU 코어
 *     .memQuota(8.0)      // 8GB 메모리
 *     .gpuQuota(1.0)      // 1 GPU
 *     .privateVolumeName("proj-storage-001")  // 선택사항
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see CreateClient 프로젝트 생성 요청의 상위 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 네임스페이스 리소스 할당 설정",
    example = """
        {
          "cpu_quota": 4.0,
          "mem_quota": 8.0,
          "gpu_quota": 1.0,
          "private_volume_name": "proj-storage-001"
        }
        """
)
public class CreateNamespace {
    
    /**
     * CPU 할당량
     * 
     * <p>네임스페이스에서 사용할 수 있는 최대 CPU 코어 수입니다.
     * 소수점 단위로 지정 가능하며, 0.1 단위까지 설정할 수 있습니다.</p>
     * 
     * @implNote 실제 사용량이 이 값을 초과하면 리소스 제한이 적용됩니다.
     */
    @JsonProperty("cpu_quota")
    @Schema(
        description = "CPU 할당량 (코어 수, 소수점 지원)",
        example = "4.0",
        required = true,
        minimum = "0.1"
    )
    private Double cpuQuota;
    
    /**
     * 메모리 할당량
     * 
     * <p>네임스페이스에서 사용할 수 있는 최대 메모리 용량입니다.
     * GB 단위로 지정하며, 소수점 단위로 세밀한 조정이 가능합니다.</p>
     */
    @JsonProperty("mem_quota")
    @Schema(
        description = "메모리 할당량 (GB 단위)",
        example = "8.0",
        required = true,
        minimum = "0.5"
    )
    private Double memQuota;
    
    /**
     * GPU 할당량
     * 
     * <p>네임스페이스에서 사용할 수 있는 최대 GPU 개수입니다.
     * 0.5 단위로 지정 가능하며, GPU 가상화 환경에서 부분 할당을 지원합니다.</p>
     */
    @JsonProperty("gpu_quota")
    @Schema(
        description = "GPU 할당량 (개수, 0.5 단위 지원)",
        example = "1.0",
        required = true,
        minimum = "0"
    )
    private Double gpuQuota;
    
    /**
     * 전용 볼륨명
     * 
     * <p>프로젝트 전용 저장소 볼륨의 이름입니다.
     * 지정하지 않으면 기본 공유 스토리지를 사용합니다.</p>
     * 
     * @apiNote 전용 볼륨 사용 시 별도 과금이 발생할 수 있습니다.
     */
    @JsonProperty("private_volume_name")
    @Schema(
        description = "전용 저장소 볼륨명 (선택사항, 미지정 시 공유 스토리지 사용)",
        example = "proj-storage-001",
        nullable = true
    )
    private String privateVolumeName;
}
