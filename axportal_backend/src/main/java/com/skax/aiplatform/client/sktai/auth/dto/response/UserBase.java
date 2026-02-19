package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 사용자 기본 정보 응답 DTO
 * 
 * <p>SKTAI Auth API의 사용자 관련 엔드포인트에서 사용되는 기본 사용자 정보 구조입니다.
 * 개별 사용자 조회, 수정 후 응답 등에서 사용됩니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 사용자명, 이메일</li>
 *   <li><strong>개인 정보</strong>: 이름, 성</li>
 * </ul>
 * 
 * <h3>MeResponse와의 차이점:</h3>
 * <ul>
 *   <li><strong>권한 정보 없음</strong>: roles, groups 정보 불포함</li>
 *   <li><strong>프로젝트 정보 없음</strong>: 현재 프로젝트 정보 불포함</li>
 *   <li><strong>로그인 정보 없음</strong>: 마지막 로그인 시간 불포함</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>개별 사용자 조회</strong>: GET /api/v1/users/{user_id}</li>
 *   <li><strong>사용자 목록</strong>: GET /api/v1/users의 data 배열 요소</li>
 *   <li><strong>사용자 수정 후</strong>: PUT /api/v1/users/{user_id} 응답</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see MeResponse 현재 사용자 상세 정보 (권한 포함)
 * @see UserRepresentation 사용자 등록 후 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 기본 정보",
    example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "username": "john.doe",
          "email": "john.doe@company.com",
          "first_name": "John",
          "last_name": "Doe"
        }
        """
)
public class UserBase {
    
    /**
     * 사용자 고유 식별자
     * 
     * <p>SKTAI 시스템에서 사용자를 고유하게 식별하는 UUID입니다.
     * 모든 사용자 관련 작업에서 기본 키로 사용됩니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "사용자 고유 식별자 (UUID)", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 사용자명
     * 
     * <p>시스템에서 사용자를 식별하는 고유한 사용자명입니다.
     * 로그인 시 사용되며, 생성 후 변경할 수 없습니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "시스템 사용자명 (로그인 ID)", 
        example = "john.doe",
        minLength = 3,
        maxLength = 50
    )
    private String username;
    
    /**
     * 이메일 주소
     * 
     * <p>사용자의 이메일 주소입니다.
     * 시스템 알림, 비밀번호 재설정 등에 사용됩니다.</p>
     */
    @JsonProperty("email")
    @Schema(
        description = "사용자 이메일 주소", 
        example = "john.doe@company.com",
        format = "email"
    )
    private String email;
    
    /**
     * 사용자 이름
     * 
     * <p>사용자의 이름(first name)입니다.
     * UI에서 사용자를 표시할 때 사용됩니다.</p>
     */
    @JsonProperty("first_name")
    @Schema(
        description = "사용자 이름", 
        example = "John",
        maxLength = 50
    )
    private String firstName;
    
    /**
     * 사용자 성
     * 
     * <p>사용자의 성(last name)입니다.
     * UI에서 사용자를 표시할 때 사용됩니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "사용자 성", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
}
