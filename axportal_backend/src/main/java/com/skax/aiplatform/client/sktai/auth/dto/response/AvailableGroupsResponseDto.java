package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 사용자 할당 가능한 그룹 목록 응답 DTO
 * 
 * <p>SKTAI Auth 시스템에서 특정 사용자에게 할당 가능한 그룹(Group) 목록을 제공하는 응답 데이터 구조입니다.
 * 현재 사용자에게 할당되지 않은 그룹들 중에서 권한 정책에 따라 할당 가능한 그룹들을 반환합니다.</p>
 * 
 * <h3>할당 가능한 그룹 조건:</h3>
 * <ul>
 *   <li><strong>미할당 그룹</strong>: 현재 사용자에게 할당되지 않은 그룹</li>
 *   <li><strong>권한 정책</strong>: 요청자의 권한으로 할당 가능한 그룹</li>
 *   <li><strong>활성 그룹</strong>: 현재 활성화되어 있는 그룹</li>
 *   <li><strong>프로젝트 범위</strong>: 해당 프로젝트에서 사용 가능한 그룹</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용자 관리 화면에서 그룹 할당 드롭다운 구성</li>
 *   <li>그룹 권한 관리 시 선택 가능한 그룹 목록 제공</li>
 *   <li>사용자별 권한 확장 시 추가 가능한 그룹 확인</li>
 *   <li>팀 또는 부서 기반 권한 관리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AvailableGroupsResponseDto response = sktaiUserClient.getAvailableGroups(userId);
 * List&lt;GroupInfo&gt; availableGroups = response.getGroups();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UserGroupMappingsResponseDto 현재 할당된 그룹 목록
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 할당 가능한 그룹 목록 응답 정보",
    example = """
        {
          "user_id": "user-123",
          "available_groups": [
            {
              "group_id": "group-456",
              "group_name": "Development Team",
              "description": "개발팀 그룹"
            },
            {
              "group_id": "group-789",
              "group_name": "Data Science Team",
              "description": "데이터 사이언스팀 그룹"
            }
          ]
        }
        """
)
public class AvailableGroupsResponseDto {
    
    /**
     * 사용자 식별자
     * 
     * <p>할당 가능한 그룹을 조회하는 대상 사용자의 고유 식별자입니다.</p>
     */
    @JsonProperty("user_id")
    @Schema(
        description = "대상 사용자 ID", 
        example = "user-123"
    )
    private String userId;
    
    /**
     * 할당 가능한 그룹 목록
     * 
     * <p>현재 사용자에게 할당되지 않았으며, 할당 가능한 그룹들의 목록입니다.
     * 각 그룹은 그룹 ID, 이름, 설명 등의 정보를 포함합니다.</p>
     * 
     * @implNote 권한 정책에 따라 필터링된 그룹만 포함됩니다.
     * @apiNote 빈 목록일 경우 할당 가능한 추가 그룹이 없음을 의미합니다.
     */
    @JsonProperty("available_groups")
    @Schema(
        description = "할당 가능한 그룹 목록",
        example = """
            [
              {
                "group_id": "group-456",
                "group_name": "Development Team",
                "description": "개발팀 그룹"
              }
            ]
            """
    )
    private List<GroupInfo> availableGroups;
    
    /**
     * 그룹 정보 내부 클래스
     * 
     * <p>할당 가능한 각 그룹의 상세 정보를 담는 데이터 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "그룹 상세 정보")
    public static class GroupInfo {
        
        /**
         * 그룹 식별자
         */
        @JsonProperty("group_id")
        @Schema(description = "그룹 고유 식별자", example = "group-456")
        private String groupId;
        
        /**
         * 그룹 이름
         */
        @JsonProperty("group_name")
        @Schema(description = "그룹 이름", example = "Development Team")
        private String groupName;
        
        /**
         * 그룹 설명
         */
        @JsonProperty("description")
        @Schema(description = "그룹 설명", example = "개발팀 그룹")
        private String description;
    }
}
