package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 생성 요청 DTO
 * 
 * <p>새로운 프로젝트와 관련 네임스페이스를 생성하기 위한 요청 데이터 구조입니다.
 * 프로젝트는 사용자 및 리소스 관리의 기본 단위이며, 네임스페이스는 리소스 할당과 격리를 제공합니다.</p>
 * 
 * <h3>구성 요소:</h3>
 * <ul>
 *   <li><strong>project</strong>: 프로젝트 기본 정보 (이름 등)</li>
 *   <li><strong>namespace</strong>: 네임스페이스 리소스 설정 (CPU, 메모리, GPU 할당량)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CreateClient request = CreateClient.builder()
 *     .project(CreateProject.builder()
 *         .name("MyProject")
 *         .build())
 *     .namespace(CreateNamespace.builder()
 *         .cpuQuota(2.0)
 *         .memQuota(4.0)
 *         .gpuQuota(1.0)
 *         .build())
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see CreateProject 프로젝트 기본 정보
 * @see CreateNamespace 네임스페이스 리소스 설정
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 생성 요청 정보",
    example = """
        {
          "project": {
            "name": "MyProject"
          },
          "namespace": {
            "cpu_quota": 2.0,
            "mem_quota": 4.0,
            "gpu_quota": 1.0
          }
        }
        """
)
public class CreateClient {
    
    /**
     * 프로젝트 기본 정보
     * 
     * <p>생성할 프로젝트의 기본 정보를 포함합니다.
     * 프로젝트명은 시스템 내에서 고유해야 합니다.</p>
     */
    @JsonProperty("project")
    @Schema(
        description = "프로젝트 기본 정보",
        required = true
    )
    private CreateProject project;
    
    /**
     * 네임스페이스 리소스 설정
     * 
     * <p>프로젝트와 연결될 네임스페이스의 리소스 할당량을 설정합니다.
     * CPU, 메모리, GPU의 최대 사용량을 제한할 수 있습니다.</p>
     */
    @JsonProperty("namespace")
    @Schema(
        description = "네임스페이스 리소스 설정",
        required = true
    )
    private CreateNamespace namespace;
}
