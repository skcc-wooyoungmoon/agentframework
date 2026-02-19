package com.skax.aiplatform.dto.deploy.request;

import com.skax.aiplatform.dto.common.PageableReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API Key 조회 요청")
public class GetApiKeyReq extends PageableReq {
    
    // @Schema(description = "사용자ID", example = "user1234", required = false)
    // private String userId;

    // @Schema(description = "프로젝트 ID", example = "project1234", required = false)
    // private String projectId;
    
}
