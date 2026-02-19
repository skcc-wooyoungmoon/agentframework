package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 인증 응답 DTO
 * 
 * <p>인증 요청에 대한 응답 정보를 담는 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "인증 응답")
public class AuthorizeResponse {
    
    @JsonProperty("authorized")
    @Schema(description = "인증 성공 여부", example = "true")
    private Boolean authorized;
    
    @JsonProperty("user_id")
    @Schema(description = "사용자 ID", example = "user-123")
    private String userId;
    
    @JsonProperty("permissions")
    @Schema(description = "권한 목록", example = "[\"read\", \"write\"]")
    private String[] permissions;
    
    @JsonProperty("expires_at")
    @Schema(description = "권한 만료 시간")
    private LocalDateTime expiresAt;
    
    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "Authentication successful")
    private String message;
}
