package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 커스텀 배포 추가 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 커스텀 배포 추가 요청 정보")
public class AppCustomDeploymentAddRequest {
    
    @JsonProperty("app_id")
    @Schema(description = "앱 ID", required = true)
    private String appId;
    
    @JsonProperty("deployment_name")
    @Schema(description = "배포 이름", required = true)
    private String deploymentName;
    
    @JsonProperty("deployment_config")
    @Schema(description = "배포 설정")
    private Object deploymentConfig;
}
