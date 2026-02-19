package com.skax.aiplatform.dto.sample.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 샘플 사용자 수정 요청 DTO
 * 
 * <p>기존 샘플 사용자 정보를 수정할 때 사용되는 요청 데이터입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "샘플 사용자 수정 요청")
public class SampleUserUpdateReq {
    
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    @Schema(description = "이메일 주소", example = "john.doe@example.com")
    private String email;
    
    @Size(min = 2, max = 100, message = "전체 이름은 2자 이상 100자 이하로 입력해주세요.")
    @Schema(description = "전체 이름", example = "홍길동")
    private String fullName;
    
    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "올바른 전화번호 형식을 입력해주세요.")
    @Size(max = 20, message = "전화번호는 20자 이하로 입력해주세요.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @Size(max = 50, message = "부서명은 50자 이하로 입력해주세요.")
    @Schema(description = "부서", example = "개발팀")
    private String department;
    
    @Size(max = 50, message = "직급은 50자 이하로 입력해주세요.")
    @Schema(description = "직급", example = "시니어 개발자")
    private String position;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
}
