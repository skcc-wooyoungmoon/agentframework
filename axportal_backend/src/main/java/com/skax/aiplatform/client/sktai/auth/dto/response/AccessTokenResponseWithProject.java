package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 프로젝트 포함 액세스 토큰 응답 DTO
 * 
 * <p>로그인 성공 시 반환되는 액세스 토큰과 사용자 프로젝트 정보입니다.
 * 기본 액세스 토큰 정보에 프로젝트 목록과 그룹 정보가 추가됩니다.</p>
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
    description = "SKTAI 프로젝트 포함 액세스 토큰 응답",
    example = """
        {
          "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
          "token_type": "Bearer",
          "expires_in": 3600,
          "refresh_token": "def50200123456789abcdef...",
          "refresh_expires_in": 7200,
          "projects": [
            {
              "id": "proj_123456",
              "name": "My Project"
            }
          ],
          "groups": ["admin", "user"]
        }
        """
)
public class AccessTokenResponseWithProject {
    
    /**
     * 액세스 토큰
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
     */
    @JsonProperty("expires_in")
    @Schema(
        description = "액세스 토큰 만료 시간 (초)", 
        example = "3600",
        required = true
    )
    private Long expiresIn;
    
    /**
     * 리프레시 토큰
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
     */
    @JsonProperty("refresh_expires_in")
    @Schema(
        description = "리프레시 토큰 만료 시간 (초)", 
        example = "7200",
        required = true
    )
    private Long refreshExpiresIn;
    
    /**
     * 사용자 프로젝트 목록
     * 
     * <p>사용자가 접근 가능한 프로젝트 목록입니다.</p>
     */
    @JsonProperty("projects")
    @Schema(
        description = "사용자 프로젝트 목록",
        required = true
    )
    private List<ProjectPayload> projects;
    
    /**
     * 사용자 그룹 목록
     * 
     * <p>사용자가 속한 그룹 목록입니다.</p>
     */
    @JsonProperty("groups")
    @Schema(
        description = "사용자 그룹 목록", 
        example = "[\"admin\", \"user\"]",
        required = true
    )
    private List<String> groups;
}
