package com.skax.aiplatform.dto.deploy.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "API Key 조회 요청")
public class GetApiKeyListReq  {
    
    // @Schema(description = "사용자ID", example = "user1234", required = false)
    // private String userId;

    @Schema(description = "프로젝트 Id", example = "project1234", required = false)
    private String projectId;

    @Schema(description = "서빙ID", example = "servingId1234", required = false)
    private String uuid;
    
}
