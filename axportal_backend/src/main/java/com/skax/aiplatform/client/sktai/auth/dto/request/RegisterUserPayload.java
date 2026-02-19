package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 사용자 등록 요청 DTO
 * 
 * <p>SKTAI Auth API의 "/api/v1/users/register" 엔드포인트 요청을 위한 DTO입니다.
 * 새로운 사용자를 시스템에 등록할 때 사용되는 필수 정보들을 포함합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>username</strong>: 고유한 사용자명 (로그인 ID)</li>
 *   <li><strong>password</strong>: 보안 정책에 맞는 비밀번호</li>
 * </ul>
 * 
 * <h3>선택적 정보:</h3>
 * <ul>
 *   <li><strong>email</strong>: 사용자 이메일 주소</li>
 *   <li><strong>first_name</strong>: 사용자 이름</li>
 *   <li><strong>last_name</strong>: 사용자 성</li>
 * </ul>
 * 
 * <h3>비밀번호 요구사항:</h3>
 * <ul>
 *   <li><strong>최소 길이</strong>: 8자 이상</li>
 *   <li><strong>최대 길이</strong>: 50자 이하</li>
 *   <li><strong>보안 정책</strong>: 영문, 숫자, 특수문자 조합 권장</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see UserRepresentation 사용자 등록 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 등록 요청 정보",
    example = """
        {
          "username": "john.doe",
          "password": "SecurePass123!",
          "email": "john.doe@company.com",
          "first_name": "John",
          "last_name": "Doe"
        }
        """
)
public class RegisterUserPayload {
    
    /**
     * 사용자명
     * 
     * <p>시스템에서 사용자를 고유하게 식별하는 사용자명입니다.
     * 로그인 시 사용되며, 시스템 내에서 중복될 수 없습니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "고유한 사용자명 (로그인 ID)", 
        example = "john.doe",
        required = true,
        minLength = 3,
        maxLength = 50
    )
    private String username;
    
    /**
     * 비밀번호
     * 
     * <p>사용자 계정의 비밀번호입니다.
     * 보안 정책에 따라 최소 8자 이상, 최대 50자 이하로 설정해야 합니다.</p>
     */
    @JsonProperty("password")
    @Schema(
        description = "사용자 비밀번호 (8-50자)", 
        example = "SecurePass123!",
        required = true,
        minLength = 8,
        maxLength = 50,
        format = "password"
    )
    private String password;
    
    /**
     * 이메일 주소
     * 
     * <p>사용자의 이메일 주소입니다.
     * 선택적 필드이지만, 비밀번호 재설정 등에 사용될 수 있습니다.</p>
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
     * UI에서 사용자를 식별할 때 사용됩니다.</p>
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
     * UI에서 사용자를 식별할 때 사용됩니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "사용자 성", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
}
