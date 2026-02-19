package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SSO 사용자 등록 요청 DTO
 * 
 * <p>SKTAI Auth 시스템에서 SSO(Single Sign-On) 사용자를 등록하기 위한 요청 데이터 구조입니다.
 * SAML 또는 OAuth2 기반 SSO 인증을 통해 외부 시스템에서 인증된 사용자를 등록할 때 사용됩니다.</p>
 * 
 * <h3>SSO 사용자 등록 특징:</h3>
 * <ul>
 *   <li><strong>외부 인증</strong>: 이미 외부 시스템에서 인증된 사용자</li>
 *   <li><strong>사용자명 필수</strong>: SSO 시스템에서 제공되는 고유 사용자명</li>
 *   <li><strong>이메일 필수</strong>: SSO 시스템에서 제공되는 이메일 주소</li>
 *   <li><strong>비밀번호 불필요</strong>: SSO 인증을 사용하므로 별도 비밀번호 불필요</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>SAML 기반 SSO 로그인 후 사용자 계정 생성</li>
 *   <li>OAuth2 기반 외부 시스템 연동 사용자 등록</li>
 *   <li>기업 AD(Active Directory) 연동 사용자 등록</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * SsoUserRegisterRequestDto request = SsoUserRegisterRequestDto.builder()
 *     .username("john.doe")
 *     .email("john.doe@company.com")
 *     .firstName("John")
 *     .lastName("Doe")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see UserRegistrationResponseDto SSO 사용자 등록 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI SSO 사용자 등록 요청 정보",
    example = """
        {
          "username": "john.doe",
          "email": "john.doe@company.com",
          "first_name": "John",
          "last_name": "Doe"
        }
        """
)
public class SsoUserRegisterRequestDto {
    
    /**
     * SSO 사용자명
     * 
     * <p>SSO 시스템에서 제공되는 고유한 사용자 식별자입니다.
     * 일반적으로 외부 시스템의 사용자 ID나 이메일 주소가 사용됩니다.</p>
     * 
     * @apiNote SSO 시스템에서 제공되는 값을 그대로 사용해야 하며, 중복 검증이 필요합니다.
     */
    @JsonProperty("username")
    @Schema(
        description = "SSO 시스템에서 제공되는 고유 사용자명", 
        example = "john.doe",
        required = true,
        minLength = 3,
        maxLength = 50
    )
    private String username;
    
    /**
     * 사용자 이메일 주소
     * 
     * <p>SSO 시스템에서 제공되는 사용자의 이메일 주소입니다.
     * 시스템 내에서 사용자 식별 및 알림 발송에 사용됩니다.</p>
     * 
     * @implNote 유효한 이메일 형식이어야 하며, 시스템 내에서 고유해야 합니다.
     */
    @JsonProperty("email")
    @Schema(
        description = "사용자 이메일 주소", 
        example = "john.doe@company.com",
        required = true,
        format = "email"
    )
    private String email;
    
    /**
     * 사용자 이름(First Name)
     * 
     * <p>SSO 시스템에서 제공되는 사용자의 이름(First Name)입니다.
     * 사용자 프로필 정보로 사용됩니다.</p>
     */
    @JsonProperty("first_name")
    @Schema(
        description = "사용자 이름 (First Name)", 
        example = "John",
        maxLength = 50
    )
    private String firstName;
    
    /**
     * 사용자 성(Last Name)
     * 
     * <p>SSO 시스템에서 제공되는 사용자의 성(Last Name)입니다.
     * 사용자 프로필 정보로 사용됩니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "사용자 성 (Last Name)", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
}
