package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 권한 설정 요청 DTO
 * 
 * <p>SKTAI Auth API에서 그룹의 권한을 설정하기 위한 요청 데이터 구조입니다.
 * 그룹이 가질 수 있는 권한들을 정의하고 관리합니다.</p>
 * 
 * <h3>권한 유형:</h3>
 * <ul>
 *   <li><strong>system</strong>: 시스템 레벨 권한</li>
 *   <li><strong>project</strong>: 프로젝트 관련 권한</li>
 *   <li><strong>resource</strong>: 리소스 접근 권한</li>
 *   <li><strong>api</strong>: API 호출 권한</li>
 * </ul>
 * 
 * <h3>권한 액션:</h3>
 * <ul>
 *   <li><strong>read</strong>: 읽기 권한</li>
 *   <li><strong>write</strong>: 쓰기 권한</li>
 *   <li><strong>delete</strong>: 삭제 권한</li>
 *   <li><strong>admin</strong>: 관리자 권한</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GroupPermissionRequest request = GroupPermissionRequest.builder()
 *     .permissions(Arrays.asList(
 *         "project:read", 
 *         "project:write", 
 *         "api:model:read"
 *     ))
 *     .inheritFromParent(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GroupPermissionsResponse 그룹 권한 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 권한 설정 요청 정보",
    example = """
        {
          "permissions": [
            "project:read",
            "project:write",
            "api:model:read",
            "resource:dataset:read"
          ],
          "inherit_from_parent": true
        }
        """
)
public class GroupPermissionRequest {

    /**
     * 권한 목록
     * 
     * <p>그룹에 할당할 권한들의 목록입니다.
     * 각 권한은 'type:resource:action' 형태의 문자열로 표현됩니다.</p>
     * 
     * @implNote 권한 형식: "project:read", "api:model:write", "system:admin" 등
     */
    @JsonProperty("permissions")
    @Schema(
        description = "그룹에 할당할 권한 목록 (type:resource:action 형태)", 
        example = "[\"project:read\", \"project:write\", \"api:model:read\"]",
        required = true
    )
    private List<String> permissions;

    /**
     * 상위 그룹 권한 상속 여부
     * 
     * <p>상위 그룹이 있는 경우 상위 그룹의 권한을 상속받을지 결정합니다.
     * true인 경우 상위 그룹의 권한이 추가로 적용됩니다.</p>
     */
    @JsonProperty("inherit_from_parent")
    @Schema(
        description = "상위 그룹 권한 상속 여부", 
        example = "true",
        defaultValue = "false"
    )
    private Boolean inheritFromParent;

    /**
     * 권한 덮어쓰기 여부
     * 
     * <p>기존 권한을 모두 제거하고 새로운 권한으로 완전히 대체할지 결정합니다.
     * false인 경우 기존 권한에 추가로 권한을 부여합니다.</p>
     */
    @JsonProperty("overwrite_existing")
    @Schema(
        description = "기존 권한 덮어쓰기 여부", 
        example = "false",
        defaultValue = "false"
    )
    private Boolean overwriteExisting;
}
