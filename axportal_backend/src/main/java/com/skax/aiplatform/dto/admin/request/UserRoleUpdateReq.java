package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 역할 수정 요청 DTO
 *
 * @author 장지원
 * @version 2.0.0
 * @since 2025-10-03
 */
@Getter
@NoArgsConstructor
public class UserRoleUpdateReq {

    @Schema(description = "새로운 역할 UUID", example = "role_uuid_123456")
    @NotBlank(message = "역할 UUID는 필수입니다.")
    private String uuid;

}
