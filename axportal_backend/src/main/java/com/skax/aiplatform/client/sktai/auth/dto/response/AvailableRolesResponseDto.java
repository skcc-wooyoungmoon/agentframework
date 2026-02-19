package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 사용자 할당 가능한 역할 목록 응답 DTO
 * 
 * <p>SKTAI Auth 시스템에서 특정 사용자에게 할당 가능한 역할(Role) 목록을 제공하는 응답 데이터 구조입니다.
 * 현재 사용자에게 할당되지 않은 역할들 중에서 권한 정책에 따라 할당 가능한 역할들을 반환합니다.</p>
 * 
 * <h3>할당 가능한 역할 조건:</h3>
 * <ul>
 *   <li><strong>미할당 역할</strong>: 현재 사용자에게 할당되지 않은 역할</li>
 *   <li><strong>권한 정책</strong>: 요청자의 권한으로 할당 가능한 역할</li>
 *   <li><strong>활성 역할</strong>: 현재 활성화되어 있는 역할</li>
 *   <li><strong>프로젝트 범위</strong>: 해당 프로젝트에서 사용 가능한 역할</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용자 관리 화면에서 역할 할당 드롭다운 구성</li>
 *   <li>역할 권한 관리 시 선택 가능한 역할 목록 제공</li>
 *   <li>사용자별 권한 확장 시 추가 가능한 역할 확인</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AvailableRolesResponseDto response = sktaiUserClient.getAvailableRoles(userId);
 * List&lt;RoleInfo&gt; availableRoles = response.getRoles();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UserRoleMappingsResponseDto 현재 할당된 역할 목록
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 할당 가능한 역할 목록 응답 정보",
    example = """
        {
          "user_id": "user-123",
          "available_roles": [
            {
              "role_id": "role-456",
              "role_name": "PROJECT_MANAGER",
              "description": "프로젝트 관리자 역할"
            },
            {
              "role_id": "role-789",
              "role_name": "DATA_ANALYST",
              "description": "데이터 분석가 역할"
            }
          ]
        }
        """
)
public class AvailableRolesResponseDto {
    
    /**
     * 사용자 식별자
     * 
     * <p>할당 가능한 역할을 조회하는 대상 사용자의 고유 식별자입니다.</p>
     */
    @JsonProperty("user_id")
    @Schema(
        description = "대상 사용자 ID", 
        example = "user-123"
    )
    private String userId;
    
    /**
     * 할당 가능한 역할 목록
     * 
     * <p>현재 사용자에게 할당되지 않았으며, 할당 가능한 역할들의 목록입니다.
     * 각 역할은 역할 ID, 이름, 설명 등의 정보를 포함합니다.</p>
     * 
     * @implNote 권한 정책에 따라 필터링된 역할만 포함됩니다.
     * @apiNote 빈 목록일 경우 할당 가능한 추가 역할이 없음을 의미합니다.
     */
    @JsonProperty("available_roles")
    @Schema(
        description = "할당 가능한 역할 목록",
        example = """
            [
              {
                "role_id": "role-456",
                "role_name": "PROJECT_MANAGER",
                "description": "프로젝트 관리자 역할"
              }
            ]
            """
    )
    private List<RoleInfo> availableRoles;
    
    /**
     * 역할 정보 내부 클래스
     * 
     * <p>할당 가능한 각 역할의 상세 정보를 담는 데이터 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "역할 상세 정보")
    public static class RoleInfo {
        
        /**
         * 역할 식별자
         */
        @JsonProperty("role_id")
        @Schema(description = "역할 고유 식별자", example = "role-456")
        private String roleId;
        
        /**
         * 역할 이름
         */
        @JsonProperty("role_name")
        @Schema(description = "역할 이름", example = "PROJECT_MANAGER")
        private String roleName;
        
        /**
         * 역할 설명
         */
        @JsonProperty("description")
        @Schema(description = "역할 설명", example = "프로젝트 관리자 역할")
        private String description;
    }
}
