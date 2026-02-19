package com.skax.aiplatform.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 로그인 요청 DTO
 * 
 * <p>사용자 로그인을 위한 요청 데이터를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 요청")
public class LoginReq {

    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "사용자명", example = "admin")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "aisnb")
    private String password;

    @Schema(description = "Default Id", example = "default")
    private String clientId;

    /**
     * GrantType에 따라서 Token 의 값의 길이가 다르게 됨.
     * Password 정보가 Token 에 포함이 됨
     */
    @Schema(description = "Grant_Type", example = "password")
    @Builder.Default
    private String grantType = "password";
}
