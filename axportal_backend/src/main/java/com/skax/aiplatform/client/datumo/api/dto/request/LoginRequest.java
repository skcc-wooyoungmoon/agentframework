package com.skax.aiplatform.client.datumo.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Datumo 로그인 요청 DTO
 * 
 * <p>Datumo 시스템에 로그인하기 위한 요청 데이터 구조입니다.
 * 사용자 인증을 통해 API 호출에 필요한 accessToken을 획득합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>loginId</strong>: 로그인 사용자 ID</li>
 *   <li><strong>password</strong>: 로그인 비밀번호</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * LoginRequest request = LoginRequest.builder()
 *     .loginId("shinhan_admin")
 *     .password("shinhanadmin12!")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 * @see com.skax.aiplatform.client.datumo.api.dto.response.LoginResponse 로그인 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Datumo 로그인 요청 정보",
    example = """
        {
          "loginId": "shinhan_admin",
          "password": "shinhanadmin12!"
        }
        """
)
public class LoginRequest {
    
    /**
     * 로그인 사용자 ID
     * 
     * <p>Datumo 시스템에 등록된 사용자의 고유 식별자입니다.</p>
     * 
     * @apiNote 시스템에 사전 등록된 유효한 사용자 ID여야 합니다.
     */
    @JsonProperty("loginId")
    @Schema(
        description = "로그인 사용자 ID", 
        example = "shinhan_admin",
        required = true,
        minLength = 3,
        maxLength = 50
    )
    @NotBlank(message = "로그인 ID는 필수입니다")
    private String loginId;
    
    /**
     * 로그인 비밀번호
     * 
     * <p>사용자 인증을 위한 비밀번호입니다.</p>
     * 
     * @implNote 보안을 위해 로그 출력 시 마스킹 처리됩니다.
     */
    @JsonProperty("password")
    @Schema(
        description = "로그인 비밀번호", 
        example = "shinhanadmin12!",
        required = true,
        minLength = 8,
        maxLength = 100
    )
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}