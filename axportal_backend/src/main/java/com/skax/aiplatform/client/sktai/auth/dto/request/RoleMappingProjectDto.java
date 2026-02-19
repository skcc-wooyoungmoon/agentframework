package com.skax.aiplatform.client.sktai.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 역할 매핑 갱신 요청에서 사용되는 프로젝트 참조 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "역할 매핑 대상 프로젝트 참조")
public class RoleMappingProjectDto {

    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String id;

    @Schema(description = "프로젝트 이름", example = "default")
    private String name;
}
