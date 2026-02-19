package com.skax.aiplatform.client.datumo.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datumo 로그인 응답 DTO
 * 
 * <p>Datumo 시스템 로그인 후 받은 응답 데이터를 담는 구조입니다.
 * API 호출에 필요한 인증 토큰 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>accessToken</strong>: API 호출에 사용할 Bearer 토큰</li>
 *   <li><strong>tokenType</strong>: 토큰 타입 (일반적으로 "Bearer")</li>
 *   <li><strong>expiresIn</strong>: 토큰 만료 시간 (초 단위)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * LoginResponse response = datumoClient.login(request);
 * String token = response.getAccessToken();
 * // Authorization: Bearer {token} 헤더로 사용
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 * @see com.skax.aiplatform.client.datumo.api.dto.request.LoginRequest 로그인 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Datumo 로그인 응답 정보",
    example = """
        {
          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
          "tokenType": "Bearer",
          "expiresIn": 3600
        }
        """
)
public class LoginResponse {
    
    /**
     * 액세스 토큰
     * 
     * <p>Datumo API 호출 시 Authorization 헤더에 사용할 Bearer 토큰입니다.
     * JWT 형태의 문자열로 제공됩니다.</p>
     * 
     * @implNote Bearer 토큰으로 사용할 때는 "Bearer " 접두사를 추가해야 합니다.
     */
    @JsonProperty("accessToken")
    @Schema(
        description = "API 호출용 액세스 토큰 (JWT 형태)", 
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        format = "jwt"
    )
    private String accessToken;
    
    /**
     * 토큰 타입
     * 
     * <p>토큰의 타입을 나타냅니다. 일반적으로 "Bearer"입니다.</p>
     */
    @JsonProperty("tokenType")
    @Schema(
        description = "토큰 타입", 
        example = "Bearer"
    )
    private String tokenType;
    
    /**
     * 토큰 만료 시간
     * 
     * <p>액세스 토큰의 유효 시간을 초 단위로 나타냅니다.
     * 이 시간이 지나면 토큰을 갱신해야 합니다.</p>
     */
    @JsonProperty("expiresIn")
    @Schema(
        description = "토큰 만료 시간 (초 단위)", 
        example = "3600",
        minimum = "1"
    )
    private Integer expiresIn;
    
    /**
     * 리프레시 토큰
     * 
     * <p>액세스 토큰 갱신에 사용할 리프레시 토큰입니다.</p>
     */
    @JsonProperty("refreshToken")
    @Schema(
        description = "토큰 갱신용 리프레시 토큰", 
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;
}