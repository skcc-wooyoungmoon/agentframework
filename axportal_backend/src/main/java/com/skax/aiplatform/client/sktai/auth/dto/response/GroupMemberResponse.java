package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 그룹 멤버 정보 응답 DTO
 * 
 * <p>SKTAI Auth API에서 그룹 멤버 정보를 반환할 때 사용하는 데이터 구조입니다.
 * 사용자의 기본 정보와 그룹 내 역할 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>사용자 정보</strong>: ID, 이름, 이메일</li>
 *   <li><strong>그룹 역할</strong>: 그룹 내 역할, 가입 일시</li>
 *   <li><strong>상태 정보</strong>: 활성 상태, 승인 상태</li>
 * </ul>
 * 
 * <h3>그룹 역할:</h3>
 * <ul>
 *   <li><strong>member</strong>: 일반 멤버</li>
 *   <li><strong>admin</strong>: 그룹 관리자</li>
 *   <li><strong>moderator</strong>: 중간 관리자</li>
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
    description = "SKTAI 그룹 멤버 정보 응답",
    example = """
        {
          "user_id": "user-123",
          "username": "john.doe",
          "email": "john.doe@example.com",
          "full_name": "John Doe",
          "role": "member",
          "is_active": true,
          "is_approved": true,
          "joined_at": "2025-08-22T10:30:00Z"
        }
        """
)
public class GroupMemberResponse {

    /**
     * 사용자 ID
     */
    @JsonProperty("user_id")
    @Schema(description = "사용자 고유 식별자", example = "user-123")
    private String userId;

    /**
     * 사용자명
     */
    @JsonProperty("username")
    @Schema(description = "사용자명", example = "john.doe")
    private String username;

    /**
     * 이메일
     */
    @JsonProperty("email")
    @Schema(description = "사용자 이메일", example = "john.doe@example.com")
    private String email;

    /**
     * 전체 이름
     */
    @JsonProperty("full_name")
    @Schema(description = "사용자 전체 이름", example = "John Doe")
    private String fullName;

    /**
     * 그룹 내 역할
     */
    @JsonProperty("role")
    @Schema(description = "그룹 내 역할", example = "member")
    private String role;

    /**
     * 활성 상태
     */
    @JsonProperty("is_active")
    @Schema(description = "사용자 활성화 상태", example = "true")
    private Boolean isActive;

    /**
     * 승인 상태
     */
    @JsonProperty("is_approved")
    @Schema(description = "그룹 멤버 승인 상태", example = "true")
    private Boolean isApproved;

    /**
     * 그룹 가입 일시
     */
    @JsonProperty("joined_at")
    @Schema(description = "그룹 가입 일시", example = "2025-08-22T10:30:00Z")
    private LocalDateTime joinedAt;
}
