package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 액세스 토큰 응답 DTO
 * 
 * <p>로그인 성공 시 반환되는 액세스 토큰 정보입니다.
 * 액세스 토큰, 리프레시 토큰, 만료 시간 등을 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 액세스 토큰 응답",
    example = """
        {
          "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
          "token_type": "Bearer",
          "expires_in": 3600,
          "refresh_token": "def50200123456789abcdef...",
          "refresh_expires_in": 7200
        }
        """
)
public class AccessTokenResponse {
    
    /**
     * 액세스 토큰
     * 
     * <p>API 호출 시 사용할 JWT 액세스 토큰입니다.</p>
     */
    @JsonProperty("access_token")
    @Schema(
        description = "JWT 액세스 토큰", 
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        required = true
    )
    private String accessToken;
    
    /**
     * 토큰 타입
     * 
     * <p>토큰의 유형입니다. 일반적으로 "Bearer"입니다.</p>
     */
    @JsonProperty("token_type")
    @Schema(
        description = "토큰 타입", 
        example = "Bearer",
        required = true
    )
    private String tokenType;
    
    /**
     * 액세스 토큰 만료 시간 (초)
     * 
     * <p>액세스 토큰이 만료되기까지의 시간을 초 단위로 나타냅니다.</p>
     */
    @JsonProperty("expires_in")
    @Schema(
        description = "액세스 토큰 만료 시간 (초)", 
        example = "3600",
        required = true
    )
    private Integer expiresIn;
    
    /**
     * 리프레시 토큰
     * 
     * <p>액세스 토큰 갱신을 위한 리프레시 토큰입니다.</p>
     */
    @JsonProperty("refresh_token")
    @Schema(
        description = "리프레시 토큰", 
        example = "def50200123456789abcdef...",
        required = true
    )
    private String refreshToken;
    
    /**
     * 리프레시 토큰 만료 시간 (초)
     * 
     * <p>리프레시 토큰이 만료되기까지의 시간을 초 단위로 나타냅니다.</p>
     */
    @JsonProperty("refresh_expires_in")
    @Schema(
        description = "리프레시 토큰 만료 시간 (초)", 
        example = "7200",
        required = true
    )
    private Integer refreshExpiresIn;
}
