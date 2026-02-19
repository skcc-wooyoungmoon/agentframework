package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI OAuth2 로그인 요청 DTO
 * 
 * <p>SKTAI 인증 시스템에 로그인하기 위한 OAuth2 요청 정보를 담는 데이터 구조입니다.
 * 다양한 인증 타입(grant_type)을 지원하며, 가장 일반적으로 사용되는 것은 'password' 타입입니다.</p>
 * 
 * <h3>지원하는 Grant Types:</h3>
 * <ul>
 *   <li><strong>password</strong>: 사용자명/비밀번호 기반 인증</li>
 *   <li><strong>client_credentials</strong>: 클라이언트 자격증명 기반 인증</li>
 *   <li><strong>refresh_token</strong>: 리프레시 토큰 기반 인증</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * var loginRequest = Body_login_access_token_api_v1_auth_login_post.builder()
 *     .grantType("password")
 *     .username("admin")
 *     .password("password123")
 *     .clientId("my-client")
 *     .scope("openid profile")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 * @see <a href="https://tools.ietf.org/html/rfc6749">OAuth 2.0 Authorization Framework</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI OAuth2 로그인 요청 정보",
    example = """
        {
          "grant_type": "password",
          "username": "admin",
          "password": "password123",
          "scope": "openid profile",
          "client_id": "my-client",
          "client_secret": "my-secret"
        }
        """
)
public class Body_login_access_token_api_v1_auth_login_post {

    /**
     * OAuth2 인증 타입
     * 
     * <p>OAuth2 표준에 따른 인증 방식을 지정합니다.</p>
     * 
     * @implNote 일반적으로 "password" 값을 사용합니다.
     */
    @JsonProperty("grant_type")
    @Schema(
        description = "OAuth2 인증 타입 (password, client_credentials, refresh_token 등)", 
        example = "password", 
        required = true,
        allowableValues = {"password", "client_credentials", "refresh_token", "authorization_code"}
    )
    private String grantType;

    /**
     * 사용자명
     * 
     * <p>인증할 사용자의 고유한 식별자입니다.</p>
     * 
     * @implNote grant_type이 "password"일 때 필수입니다.
     */
    @JsonProperty("username")
    @Schema(
        description = "로그인할 사용자명", 
        example = "admin", 
        required = true,
        minLength = 3,
        maxLength = 50
    )
    private String username;

    /**
     * 사용자 비밀번호
     * 
     * <p>사용자 인증을 위한 비밀번호입니다.</p>
     * 
     * @implNote 보안상 로그에 기록되지 않도록 주의해야 합니다.
     */
    @JsonProperty("password")
    @Schema(
        description = "사용자 비밀번호", 
        example = "password123", 
        required = true,
        format = "password",
        minLength = 8
    )
    private String password;

    /**
     * OAuth2 권한 범위
     * 
     * <p>요청하는 권한의 범위를 공백으로 구분된 문자열로 지정합니다.</p>
     */
    @JsonProperty("scope")
    @Schema(
        description = "OAuth2 권한 범위 (공백으로 구분)", 
        example = "openid profile email",
        allowableValues = {"openid", "profile", "email", "read", "write"}
    )
    private String scope;

    /**
     * OAuth2 클라이언트 ID
     * 
     * <p>사전에 등록된 OAuth2 클라이언트의 식별자입니다.</p>
     */
    @JsonProperty("client_id")
    @Schema(
        description = "OAuth2 클라이언트 ID", 
        example = "my-client",
        minLength = 3,
        maxLength = 100
    )
    private String clientId;

    /**
     * OAuth2 클라이언트 시크릿
     * 
     * <p>클라이언트 인증을 위한 비밀 키입니다.</p>
     * 
     * @implNote 클라이언트가 confidential 타입일 때 필요합니다.
     */
    @JsonProperty("client_secret")
    @Schema(
        description = "OAuth2 클라이언트 시크릿", 
        example = "my-secret",
        format = "password"
    )
    private String clientSecret;
}
