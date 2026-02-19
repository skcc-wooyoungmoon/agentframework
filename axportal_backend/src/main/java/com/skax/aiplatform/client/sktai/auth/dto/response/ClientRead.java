package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 상세 조회 응답 DTO
 * 
 * <p>특정 프로젝트의 상세 정보를 조회할 때 반환되는 데이터 구조입니다.
 * 프로젝트 기본 정보와 연결된 네임스페이스 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>project</strong>: 프로젝트 기본 정보 (ID, 이름)</li>
 *   <li><strong>namespace</strong>: 네임스페이스 상세 정보 (리소스 할당량, 사용량 등)</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>프로젝트 상세 페이지 표시</li>
 *   <li>프로젝트 설정 화면 구성</li>
 *   <li>리소스 사용량 모니터링</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "project": {
 *     "id": "proj-123",
 *     "name": "MyProject"
 *   },
 *   "namespace": {
 *     "id": "ns-456",
 *     "cpu_quota": 4.0,
 *     "mem_quota": 8.0,
 *     "gpu_quota": 1.0,
 *     "cpu_used": 2.5,
 *     "mem_used": 3.2,
 *     "gpu_used": 0.5
 *   }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectPayload 프로젝트 기본 정보
 * @see Namespace 네임스페이스 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 상세 조회 응답",
    example = """
        {
          "project": {
            "id": "proj-123",
            "name": "MyProject"
          },
          "namespace": {
            "id": "ns-456",
            "cpu_quota": 4.0,
            "mem_quota": 8.0,
            "gpu_quota": 1.0,
            "cpu_used": 2.5,
            "mem_used": 3.2,
            "gpu_used": 0.5
          }
        }
        """
)
public class ClientRead {
    
    /**
     * 프로젝트 기본 정보
     * 
     * <p>프로젝트의 식별자와 표시명을 포함합니다.
     * 이 정보는 프로젝트를 식별하고 표시하는 데 사용됩니다.</p>
     */
    @JsonProperty("project")
    @Schema(
        description = "프로젝트 기본 정보",
        required = true
    )
    private ProjectPayload project;
    
    /**
     * 네임스페이스 상세 정보
     * 
     * <p>프로젝트와 연결된 네임스페이스의 상세 정보입니다.
     * 리소스 할당량, 현재 사용량, 생성/수정 이력 등을 포함합니다.</p>
     * 
     * @apiNote 네임스페이스가 없는 경우 null일 수 있습니다.
     */
    @JsonProperty("namespace")
    @Schema(
        description = "네임스페이스 상세 정보 (없는 경우 null)",
        nullable = true
    )
    private Namespace namespace;
}
