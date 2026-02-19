package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.response.ProjectPayload;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트-역할 매핑 요청 DTO
 * 
 * <p>사용자에게 특정 프로젝트의 특정 역할을 할당하거나 해제하기 위한 데이터 구조입니다.
 * 프로젝트와 역할 정보를 조합하여 정확한 권한 매핑을 정의합니다.</p>
 * 
 * <h3>사용 목적:</h3>
 * <ul>
 *   <li><strong>역할 할당</strong>: 사용자에게 프로젝트별 역할 부여</li>
 *   <li><strong>역할 해제</strong>: 사용자로부터 프로젝트별 역할 제거</li>
 *   <li><strong>권한 관리</strong>: 세밀한 접근 제어 구현</li>
 * </ul>
 * 
 * <h3>매핑 구조:</h3>
 * <p>프로젝트 + 역할의 조합으로 구성되며, 동일한 사용자가 여러 프로젝트에서
 * 서로 다른 역할을 가질 수 있습니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ProjectRoles mapping = ProjectRoles.builder()
 *     .project(ProjectPayload.builder()
 *         .id("proj-123")
 *         .name("MyProject")
 *         .build())
 *     .role(RoleBase.builder()
 *         .id("role-456")
 *         .name("admin")
 *         .description("관리자 역할")
 *         .build())
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectPayload 프로젝트 정보
 * @see RoleBase 역할 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트-역할 매핑 정보",
    example = """
        {
          "project": {
            "id": "proj-123",
            "name": "MyProject"
          },
          "role": {
            "id": "role-456",
            "name": "admin",
            "description": "관리자 역할"
          }
        }
        """
)
public class ProjectRoles {
    
    /**
     * 프로젝트 정보
     * 
     * <p>역할이 적용될 프로젝트의 기본 정보입니다.
     * 프로젝트 ID와 이름이 포함됩니다.</p>
     */
    @JsonProperty("project")
    @Schema(
        description = "대상 프로젝트 정보",
        required = true
    )
    private ProjectPayload project;
    
    /**
     * 역할 정보
     * 
     * <p>할당 또는 해제할 역할의 상세 정보입니다.
     * 역할 ID, 이름, 설명이 포함됩니다.</p>
     */
    @JsonProperty("role")
    @Schema(
        description = "할당/해제할 역할 정보",
        required = true
    )
    private RoleBase role;
}
