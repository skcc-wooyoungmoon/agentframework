package com.skax.aiplatform.dto.deploy.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent 배포 응답")
public class AgentDeployUpdateOrDeleteRes {
    @Schema(description = "처리된 애플리케이션의 고유 식별자")
    private String appUuid;
    
    @Schema(description = "작업 성공 여부")
    private Boolean success;
    
    @Schema(description = "작업 결과 메시지")
    private String message;

    @Schema(description = "수정 시간 (수정 작업의 경우)")
    private String updatedAt;
}
