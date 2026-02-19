package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 멤버 추가 요청 DTO
 * 
 * <p>SKTAI Auth API에서 그룹에 새로운 멤버를 추가하기 위한 요청 데이터 구조입니다.
 * 단일 사용자 또는 여러 사용자를 한 번에 그룹에 추가할 수 있습니다.</p>
 * 
 * <h3>추가 방식:</h3>
 * <ul>
 *   <li><strong>단일 추가</strong>: user_id 필드 사용</li>
 *   <li><strong>일괄 추가</strong>: user_ids 필드 사용</li>
 * </ul>
 * 
 * <h3>멤버 역할:</h3>
 * <ul>
 *   <li><strong>member</strong>: 일반 멤버 (기본값)</li>
 *   <li><strong>admin</strong>: 그룹 관리자</li>
 *   <li><strong>moderator</strong>: 중간 관리자</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 단일 사용자 추가
 * GroupMemberAddRequest request = GroupMemberAddRequest.builder()
 *     .userId("user-123")
 *     .role("member")
 *     .build();
 * 
 * // 여러 사용자 일괄 추가
 * GroupMemberAddRequest request = GroupMemberAddRequest.builder()
 *     .userIds(Arrays.asList("user-123", "user-456", "user-789"))
 *     .role("member")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GroupMembersResponse 그룹 멤버 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 멤버 추가 요청 정보",
    example = """
        {
          "user_id": "user-123",
          "user_ids": ["user-123", "user-456", "user-789"],
          "role": "member"
        }
        """
)
public class GroupMemberAddRequest {

    /**
     * 사용자 ID (단일 추가)
     * 
     * <p>그룹에 추가할 단일 사용자의 ID입니다.
     * user_ids와 함께 사용할 수 없으며, 둘 중 하나만 지정해야 합니다.</p>
     */
    @JsonProperty("user_id")
    @Schema(
        description = "추가할 사용자 ID (단일 추가)", 
        example = "user-123"
    )
    private String userId;

    /**
     * 사용자 ID 목록 (일괄 추가)
     * 
     * <p>그룹에 추가할 여러 사용자의 ID 목록입니다.
     * user_id와 함께 사용할 수 없으며, 둘 중 하나만 지정해야 합니다.</p>
     */
    @JsonProperty("user_ids")
    @Schema(
        description = "추가할 사용자 ID 목록 (일괄 추가)", 
        example = "[\"user-123\", \"user-456\", \"user-789\"]"
    )
    private List<String> userIds;

    /**
     * 그룹 내 역할
     * 
     * <p>추가될 사용자의 그룹 내 역할을 지정합니다.
     * 지정하지 않으면 기본적으로 'member' 역할이 할당됩니다.</p>
     */
    @JsonProperty("role")
    @Schema(
        description = "그룹 내 역할", 
        example = "member",
        allowableValues = {"member", "admin", "moderator"},
        defaultValue = "member"
    )
    private String role;

    /**
     * 알림 발송 여부
     * 
     * <p>그룹 추가 시 해당 사용자에게 알림을 발송할지 결정합니다.
     * 기본값은 true입니다.</p>
     */
    @JsonProperty("send_notification")
    @Schema(
        description = "그룹 추가 알림 발송 여부", 
        example = "true",
        defaultValue = "true"
    )
    private Boolean sendNotification;
}
