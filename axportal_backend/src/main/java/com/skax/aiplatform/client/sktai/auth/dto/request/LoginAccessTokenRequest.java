package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth2 로그인 요청 DTO
 *
 * <p>OAuth2 패스워드 기반 로그인을 위한 요청 데이터를 담습니다.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * LoginAccessTokenRequest request = LoginAccessTokenRequest.builder()
 *     .grantType("password")
 *     .username("sgo1033618")
 *     .password("password")
 *     .clientId("default")
 *     .clientSecret("")
 *     .scope("")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "OAuth2 로그인 요청")
public class LoginAccessTokenRequest {

    /**
     * OAuth2 권한 부여 타입
     *
     * <p>일반적으로 "password"를 사용합니다.</p>
     */
    @JsonProperty("grant_type")
    @Schema(
        description = "OAuth2 권한 부여 타입",
        example = "password",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String grantType;

    /**
     * 사용자명
     *
     * <p>로그인할 사용자의 ID입니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "사용자명",
        example = "sgo1033618",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    /**
     * 비밀번호
     *
     * <p>로그인할 사용자의 비밀번호입니다.</p>
     */
    @JsonProperty("password")
    @Schema(
        description = "비밀번호",
        example = "password",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    /**
     * 클라이언트 ID
     *
     * <p>OAuth2 클라이언트 식별자입니다. 기본값은 "default"입니다.</p>
     */
    @JsonProperty("client_id")
    @Schema(
        description = "클라이언트 ID",
        example = "default",
        defaultValue = "default"
    )
    private String clientId;

    /**
     * 클라이언트 시크릿
     *
     * <p>OAuth2 클라이언트 비밀키입니다. 필요하지 않은 경우 빈 문자열을 사용합니다.</p>
     */
    @JsonProperty("client_secret")
    @Schema(
        description = "클라이언트 시크릿",
        example = "",
        defaultValue = ""
    )
    private String clientSecret;

    /**
     * OAuth2 스코프
     *
     * <p>요청할 권한 범위입니다. 필요하지 않은 경우 빈 문자열을 사용합니다.</p>
     */
    @JsonProperty("scope")
    @Schema(
        description = "OAuth2 스코프",
        example = "",
        defaultValue = ""
    )
    private String scope;
}
