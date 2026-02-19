package com.skax.aiplatform.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Admin 사용자 생성 요청 DTO
 * 
 * @author Jongtae Park
 * @since 2025-10-08
 * @version 1.0.0
 */
@Data
public class AdminUserCreateRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;
    
    @NotBlank(message = "사용자명은 필수입니다")
    private String name;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
    
    private String department;
    
    private String position;
    
    private String phoneNumber;
}
