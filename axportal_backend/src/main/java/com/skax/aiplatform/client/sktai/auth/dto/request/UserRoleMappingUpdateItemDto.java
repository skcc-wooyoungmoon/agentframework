package com.skax.aiplatform.client.sktai.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PUT /api/v1/users/{userId}/role-mappings 요청 본문 항목 DTO
 *
 * <p>요청은 이 DTO의 배열(List) 형태입니다.</p>
 *
 * <pre>
 * [
 *   {
 *     "project": { "id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", "name": "default" },
 *     "role": { "id": "a8209cf9-0a76-4ef5-9e43-017ba3200c40", "name": "admin", "description": null }
 *   }
 * ]
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 역할 매핑 갱신 요청 항목")
public class UserRoleMappingUpdateItemDto {

    @Schema(description = "대상 프로젝트 정보", required = true)
    private RoleMappingProjectDto project;

    @Schema(description = "할당할 역할 정보", required = true)
    private RoleMappingRoleDto role;
}
