package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 그룹 생성 요청 DTO
 * 
 * <p>SKTAI Auth API에서 새로운 그룹을 생성하기 위한 요청 데이터 구조입니다.
 * 그룹명, 설명, 유형, 권한 등을 포함하여 완전한 그룹 정보를 제공합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>name</strong>: 그룹의 고유한 이름</li>
 *   <li><strong>type</strong>: 그룹 유형 (department, project, role, custom)</li>
 * </ul>
 * 
 * <h3>그룹 유형:</h3>
 * <ul>
 *   <li><strong>department</strong>: 부서/조직 그룹</li>
 *   <li><strong>project</strong>: 프로젝트 기반 그룹</li>
 *   <li><strong>role</strong>: 역할 기반 그룹</li>
 *   <li><strong>custom</strong>: 사용자 정의 그룹</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GroupCreateRequest request = GroupCreateRequest.builder()
 *     .name("Development Team")
 *     .description("개발팀 그룹")
 *     .type("department")
 *     .parentGroupId("parent-group-123")
 *     .tags(Arrays.asList("development", "backend", "frontend"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GroupResponse 그룹 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 생성 요청 정보",
    example = """
        {
          "name": "Development Team",
          "description": "개발팀 그룹",
          "type": "department",
          "parent_group_id": "parent-group-123",
          "tags": ["development", "backend", "frontend"]
        }
        """
)
public class GroupCreateRequest {

    /**
     * 그룹 이름
     * 
     * <p>그룹의 고유한 이름입니다.
     * 시스템 내에서 중복될 수 없으며, 그룹을 식별하는 주요 정보입니다.</p>
     * 
     * @implNote 그룹 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("group_name")
    @Schema(
        description = "그룹의 고유한 이름", 
        example = "public",
        required = true
    )
    private String group_name;
}
