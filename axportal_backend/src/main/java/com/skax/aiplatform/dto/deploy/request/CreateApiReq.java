package com.skax.aiplatform.dto.deploy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
    
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApiReq {
    @Schema(description = "에셋 타입", example = "model, agent")
    private String type;

    @Schema(description = "배포 Id", example = "project1234")
    private String uuid;

    @Schema(description = "배포 이름", example = "api1234")
    private String name;

    @Schema(description = "배포 설명", example = "api1234")
    private String description;

    @Schema(description = "프로젝트ID", example = "1234567890", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String projectId;
}
