package com.skax.aiplatform.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Admin 사용자 권한 할당 요청 DTO
 *
 * @author Jongtae Park
 * @version 1.0.0
 * @since 2025-10-08
 */
@Data
public class AdminUserRoleAssignRequest {
    @NotBlank(message = "그룹 ID는 필수입니다")
    private String groupId;
}
