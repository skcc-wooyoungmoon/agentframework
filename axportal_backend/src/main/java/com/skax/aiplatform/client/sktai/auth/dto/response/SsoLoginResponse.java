package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SSO 로그인 응답 DTO
 * 
 * <p>SKTAI Auth API에서 SSO 로그인 요청에 대한 응답을 나타내는 DTO입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SSO 로그인 응답")
public class SsoLoginResponse {
    
    @JsonProperty("redirect_url")
    @Schema(description = "리다이렉트 URL", example = "https://sso.example.com/login?state=abc123")
    private String redirectUrl;
    
    @JsonProperty("state")
    @Schema(description = "SSO 상태 코드", example = "abc123xyz")
    private String state;
    
    @JsonProperty("auth_url")
    @Schema(description = "인증 URL", example = "https://sso.example.com/oauth/authorize")
    private String authUrl;
    
    @JsonProperty("client_id")
    @Schema(description = "클라이언트 ID", example = "sktai-client")
    private String clientId;
}
