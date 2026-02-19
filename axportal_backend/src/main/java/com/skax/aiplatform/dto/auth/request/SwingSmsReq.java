package com.skax.aiplatform.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 몰리메이트 로그인 요청 DTO
 *
 * <p>몰리메이트 로그인을 위한 요청 데이터를 담는 DTO입니다.</p>
 *
 * @author JongtaePark
 * @version 1.0.0
 * @since 2025-12-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "스윙 SMS 인증코드 발급 요청")
public class SwingSmsReq {
    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "사용자명", example = "admin")
    private String username;

    private String authEventId;
    private String randomNumber;
    private String newJoinYn;
}
