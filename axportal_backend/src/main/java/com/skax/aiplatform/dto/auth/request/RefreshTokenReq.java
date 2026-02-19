package com.skax.aiplatform.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 * 
 * <p>Access Token 갱신을 위한 Refresh Token 정보를 담고 있습니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-02
 * @version 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 갱신 요청")
public class RefreshTokenReq {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh Token은 필수입니다")
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    @JsonProperty("refreshToken")
    private String refreshToken;
}
