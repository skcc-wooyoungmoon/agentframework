package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 수정 요청 DTO
 * 
 * <p>기존 프로젝트의 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 프로젝트 기본 정보와 네임스페이스 리소스 할당량을 변경할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>프로젝트명</strong>: 프로젝트의 표시명 변경</li>
 *   <li><strong>리소스 할당량</strong>: CPU, 메모리, GPU 할당량 조정</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>리소스 할당량 감소 시 현재 사용량보다 낮게 설정할 수 없음</li>
 *   <li>프로젝트명 변경은 기존 참조에 영향을 줄 수 있음</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * UpdateClient request = UpdateClient.builder()
 *     .project(UpdateProject.builder()
 *         .name("Updated Project Name")
 *         .build())
 *     .namespace(UpdateNamespace.builder()
 *         .cpuQuota(8.0)    // 기존 4.0에서 8.0으로 증가
 *         .memQuota(16.0)   // 기존 8.0에서 16.0으로 증가
 *         .gpuQuota(2.0)    // 기존 1.0에서 2.0으로 증가
 *         .build())
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UpdateProject 프로젝트 수정 정보
 * @see UpdateNamespace 네임스페이스 수정 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 수정 요청 정보",
    example = """
        {
          "project": {
            "name": "Updated Project Name"
          },
          "namespace": {
            "cpu_quota": 8.0,
            "mem_quota": 16.0,
            "gpu_quota": 2.0
          }
        }
        """
)
public class UpdateClient {
    
    /**
     * 프로젝트 수정 정보
     * 
     * <p>변경할 프로젝트의 기본 정보를 포함합니다.
     * null이 아닌 필드만 업데이트됩니다.</p>
     */
    @JsonProperty("project")
    @Schema(
        description = "프로젝트 수정 정보",
        required = true
    )
    private UpdateProject project;
    
    /**
     * 네임스페이스 수정 정보
     * 
     * <p>변경할 네임스페이스의 리소스 할당량을 포함합니다.
     * 현재 사용량보다 낮게 설정할 수 없습니다.</p>
     */
    @JsonProperty("namespace")
    @Schema(
        description = "네임스페이스 수정 정보",
        required = true
    )
    private UpdateNamespace namespace;
}
