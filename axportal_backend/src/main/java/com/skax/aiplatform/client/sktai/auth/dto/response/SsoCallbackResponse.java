package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SSO 콜백 응답 DTO
 * 
 * <p>SKTAI Auth API에서 SSO 콜백 처리 결과를 나타내는 DTO입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SSO 콜백 응답")
public class SsoCallbackResponse {
    
    @JsonProperty("access_token")
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @JsonProperty("token_type")
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;
    
    @JsonProperty("expires_in")
    @Schema(description = "토큰 만료 시간 (초)", example = "3600")
    private Integer expiresIn;
    
    @JsonProperty("user_id")
    @Schema(description = "사용자 ID", example = "user-123")
    private String userId;
    
    @JsonProperty("email")
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;
    
    @JsonProperty("name")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
}
