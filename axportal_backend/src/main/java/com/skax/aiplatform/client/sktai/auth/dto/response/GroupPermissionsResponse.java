package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 권한 응답 DTO
 * 
 * <p>SKTAI Auth API에서 그룹의 권한 정보를 반환할 때 사용하는 데이터 구조입니다.
 * 그룹이 가진 권한 목록과 상속 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>직접 권한</strong>: 그룹에 직접 할당된 권한들</li>
 *   <li><strong>상속 권한</strong>: 상위 그룹에서 상속받은 권한들</li>
 *   <li><strong>유효 권한</strong>: 실제로 적용되는 모든 권한들</li>
 * </ul>
 * 
 * <h3>권한 형식:</h3>
 * <ul>
 *   <li><strong>type:resource:action</strong> 형태의 문자열</li>
 *   <li><strong>예시</strong>: "project:read", "api:model:write", "system:admin"</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 권한 정보 응답",
    example = """
        {
          "group_id": "group-123",
          "direct_permissions": [
            "project:read",
            "project:write",
            "api:model:read"
          ],
          "inherited_permissions": [
            "system:login",
            "api:basic:read"
          ],
          "effective_permissions": [
            "system:login",
            "api:basic:read",
            "project:read",
            "project:write",
            "api:model:read"
          ],
          "inherit_from_parent": true
        }
        """
)
public class GroupPermissionsResponse {

    /**
     * 그룹 ID
     */
    @JsonProperty("group_id")
    @Schema(description = "그룹 고유 식별자", example = "group-123")
    private String groupId;

    /**
     * 직접 할당된 권한
     */
    @JsonProperty("direct_permissions")
    @Schema(description = "그룹에 직접 할당된 권한 목록", example = "[\"project:read\", \"project:write\", \"api:model:read\"]")
    private List<String> directPermissions;

    /**
     * 상속받은 권한
     */
    @JsonProperty("inherited_permissions")
    @Schema(description = "상위 그룹에서 상속받은 권한 목록", example = "[\"system:login\", \"api:basic:read\"]")
    private List<String> inheritedPermissions;

    /**
     * 유효한 권한 (직접 + 상속)
     */
    @JsonProperty("effective_permissions")
    @Schema(description = "실제로 적용되는 모든 권한 목록", example = "[\"system:login\", \"api:basic:read\", \"project:read\", \"project:write\", \"api:model:read\"]")
    private List<String> effectivePermissions;

    /**
     * 상위 그룹 권한 상속 여부
     */
    @JsonProperty("inherit_from_parent")
    @Schema(description = "상위 그룹 권한 상속 여부", example = "true")
    private Boolean inheritFromParent;
}
