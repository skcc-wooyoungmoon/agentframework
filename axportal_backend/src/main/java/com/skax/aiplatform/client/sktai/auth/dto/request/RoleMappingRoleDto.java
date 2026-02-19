package com.skax.aiplatform.client.sktai.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 역할 매핑 갱신 요청에서 사용되는 역할 참조 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "역할 매핑 대상 역할 참조")
public class RoleMappingRoleDto {

    @Schema(description = "역할 ID", example = "a8209cf9-0a76-4ef5-9e43-017ba3200c40")
    private String id;

    @Schema(description = "역할 이름", example = "admin")
    private String name;

    @Schema(description = "역할 설명", example = "관리자 역할", nullable = true)
    private String description;
}
