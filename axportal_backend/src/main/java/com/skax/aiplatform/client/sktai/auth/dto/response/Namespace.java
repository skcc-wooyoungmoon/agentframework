package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI 네임스페이스 상세 정보 DTO
 * 
 * <p>프로젝트에 할당된 네임스페이스의 상세 정보를 나타내는 데이터 구조입니다.
 * 리소스 할당량, 현재 사용량, 생성/수정 이력, 프로젝트 매핑 정보 등을 포함합니다.</p>
 * 
 * <h3>리소스 관리:</h3>
 * <ul>
 *   <li><strong>CPU 관리</strong>: 할당량과 사용량 추적</li>
 *   <li><strong>메모리 관리</strong>: 메모리 할당 및 사용 현황</li>
 *   <li><strong>GPU 관리</strong>: GPU 리소스 할당 및 모니터링</li>
 *   <li><strong>스토리지 관리</strong>: 파일 시스템 할당량 관리</li>
 * </ul>
 * 
 * <h3>프로젝트 매핑:</h3>
 * <ul>
 *   <li><strong>다중 프로젝트 지원</strong>: 하나의 네임스페이스가 여러 프로젝트에 연결 가능</li>
 *   <li><strong>유연한 리소스 공유</strong>: 프로젝트 간 리소스 효율적 활용</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "id": "ns-456",
 *   "name": "production-namespace",
 *   "cpu_quota": 8.0,
 *   "mem_quota": 16.0,
 *   "gpu_quota": 2.0,
 *   "fs_quota": 100.0,
 *   "cpu_used": 4.2,
 *   "mem_used": 7.8,
 *   "gpu_used": 1.0,
 *   "fs_used": 45.6,
 *   "created_at": "2025-08-15T10:30:00Z",
 *   "updated_at": "2025-08-15T14:20:00Z",
 *   "created_by": "admin",
 *   "updated_by": "admin",
 *   "projects": [
 *     {
 *       "id": "proj-123",
 *       "name": "MyProject"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectPayload 네임스페이스에 연결된 프로젝트 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 네임스페이스 상세 정보",
    example = """
        {
          "id": "ns-456",
          "name": "production-namespace",
          "cpu_quota": 8.0,
          "mem_quota": 16.0,
          "gpu_quota": 2.0,
          "fs_quota": 100.0,
          "cpu_used": 4.2,
          "mem_used": 7.8,
          "gpu_used": 1.0,
          "fs_used": 45.6,
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T14:20:00Z",
          "created_by": "admin",
          "updated_by": "admin",
          "projects": [
            {
              "id": "proj-123",
              "name": "MyProject"
            }
          ]
        }
        """
)
public class Namespace {
    
    /**
     * 네임스페이스 고유 식별자
     * 
     * <p>네임스페이스를 유일하게 식별하는 ID입니다.
     * 시스템에서 자동 생성되며 변경되지 않습니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "네임스페이스 고유 식별자",
        example = "ns-456",
        required = true
    )
    private String id;
    
    /**
     * 네임스페이스 이름
     * 
     * <p>사용자가 정의한 네임스페이스의 표시명입니다.
     * 리소스 그룹의 목적이나 환경을 나타내는 명확한 이름을 사용합니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "네임스페이스 표시명",
        example = "production-namespace",
        required = true
    )
    private String name;
    
    /**
     * CPU 할당량
     * 
     * <p>네임스페이스에 할당된 총 CPU 코어 수입니다.
     * 소수점으로 표현되며, 가상 CPU 코어 단위입니다.</p>
     */
    @JsonProperty("cpu_quota")
    @Schema(
        description = "CPU 할당량 (코어 수)",
        example = "8.0",
        minimum = "0.1"
    )
    private Double cpuQuota;
    
    /**
     * 메모리 할당량
     * 
     * <p>네임스페이스에 할당된 총 메모리 용량입니다.
     * GB 단위로 표현됩니다.</p>
     */
    @JsonProperty("mem_quota")
    @Schema(
        description = "메모리 할당량 (GB)",
        example = "16.0",
        minimum = "0.1"
    )
    private Double memQuota;
    
    /**
     * GPU 할당량
     * 
     * <p>네임스페이스에 할당된 GPU 개수입니다.
     * 물리적 GPU 카드 단위로 할당됩니다.</p>
     */
    @JsonProperty("gpu_quota")
    @Schema(
        description = "GPU 할당량 (개수)",
        example = "2.0",
        minimum = "0"
    )
    private Double gpuQuota;
    
    /**
     * 파일시스템 할당량
     * 
     * <p>네임스페이스에 할당된 스토리지 용량입니다.
     * GB 단위로 표현되며, 데이터 저장소 크기를 제한합니다.</p>
     */
    @JsonProperty("fs_quota")
    @Schema(
        description = "파일시스템 할당량 (GB)",
        example = "100.0",
        minimum = "1.0"
    )
    private Double fsQuota;
    
    /**
     * CPU 사용량
     * 
     * <p>현재 네임스페이스에서 실제 사용 중인 CPU 코어 수입니다.
     * 실시간으로 업데이트되는 값입니다.</p>
     */
    @JsonProperty("cpu_used")
    @Schema(
        description = "현재 CPU 사용량 (코어 수)",
        example = "4.2",
        minimum = "0"
    )
    private Double cpuUsed;
    
    /**
     * 메모리 사용량
     * 
     * <p>현재 네임스페이스에서 실제 사용 중인 메모리 용량입니다.
     * GB 단위로 표현되며 실시간 모니터링됩니다.</p>
     */
    @JsonProperty("mem_used")
    @Schema(
        description = "현재 메모리 사용량 (GB)",
        example = "7.8",
        minimum = "0"
    )
    private Double memUsed;
    
    /**
     * GPU 사용량
     * 
     * <p>현재 네임스페이스에서 실제 사용 중인 GPU 개수입니다.
     * 할당된 작업에 의해 점유된 GPU 수를 나타냅니다.</p>
     */
    @JsonProperty("gpu_used")
    @Schema(
        description = "현재 GPU 사용량 (개수)",
        example = "1.0",
        minimum = "0"
    )
    private Double gpuUsed;
    
    /**
     * 파일시스템 사용량
     * 
     * <p>현재 네임스페이스에서 실제 사용 중인 스토리지 용량입니다.
     * GB 단위로 표현되며 파일 저장량을 모니터링합니다.</p>
     */
    @JsonProperty("fs_used")
    @Schema(
        description = "현재 파일시스템 사용량 (GB)",
        example = "45.6",
        minimum = "0"
    )
    private Double fsUsed;
    
    /**
     * 생성 일시
     * 
     * <p>네임스페이스가 생성된 날짜와 시간입니다.
     * ISO 8601 형식으로 표현됩니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "네임스페이스 생성 일시",
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     * 
     * <p>네임스페이스가 마지막으로 수정된 날짜와 시간입니다.
     * 리소스 할당량 변경이나 설정 수정 시 업데이트됩니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "네임스페이스 마지막 수정 일시",
        example = "2025-08-15T14:20:00Z",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
    
    /**
     * 생성자
     * 
     * <p>네임스페이스를 생성한 사용자의 식별자입니다.
     * 일반적으로 관리자나 프로젝트 소유자가 생성합니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
        description = "네임스페이스 생성자 식별자",
        example = "admin"
    )
    private String createdBy;
    
    /**
     * 수정자
     * 
     * <p>네임스페이스를 마지막으로 수정한 사용자의 식별자입니다.
     * 리소스 할당량이나 설정을 변경한 사용자를 추적합니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "네임스페이스 마지막 수정자 식별자",
        example = "admin"
    )
    private String updatedBy;
    
    /**
     * 연결된 프로젝트 목록
     * 
     * <p>현재 네임스페이스에 연결된 모든 프로젝트의 목록입니다.
     * 하나의 네임스페이스는 여러 프로젝트와 연결될 수 있습니다.</p>
     * 
     * @apiNote 연결된 프로젝트가 없는 경우 빈 배열이 반환됩니다.
     */
    @JsonProperty("projects")
    @Schema(
        description = "네임스페이스에 연결된 프로젝트 목록",
        example = """
            [
              {
                "id": "proj-123",
                "name": "MyProject"
              }
            ]
            """
    )
    private List<ProjectPayload> projects;
}
