package com.skax.aiplatform.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 요청")
public class RegisterReq {
    @NotBlank(message = "행번은 필수입니다")
    @Schema(description = "행번", example = "SGO1000200")
    private String userNo;

    @Schema(description = "이름", example = "홍길동")
    private String displayName;

    @NotBlank(message = "패스워드 필수입니다")
    @Schema(description = "패스워드", example = "!QAZ2wsx3edc")
    private String password;

    @NotBlank(message = "이메일정보는 필수입니다")
    @Schema(description = "이메일정보", example = "test10@shinhan.co.kr")
    private String email;

    @Schema(description = "휴대폰 번호", example = "010-1234-5678")
    private String hpNo;

    @Schema(description = "부서", example = "AI플랫폼팀")
    private String dept;

    @Schema(description = "직급", example = "과장")
    private String position;
}
