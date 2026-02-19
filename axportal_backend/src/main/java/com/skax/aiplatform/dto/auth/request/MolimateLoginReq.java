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
 * @since 2025-09-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "몰리메이트 로그인 요청")
public class MolimateLoginReq {

    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "사용자명", example = "admin")
    private String username;

    private String newJoinYn;
}
