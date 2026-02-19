package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 시스템 상태 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent 시스템 상태 응답")
public class DefaultStatusResponse {
    
    @JsonProperty("status")
    @Schema(description = "시스템 상태", example = "healthy")
    private String status;
    
    @JsonProperty("uptime")
    @Schema(description = "시스템 가동 시간 (초)")
    private Long uptime;
    
    @JsonProperty("version")
    @Schema(description = "시스템 버전")
    private String version;
    
    @JsonProperty("timestamp")
    @Schema(description = "상태 조회 시간")
    private String timestamp;
    
    @JsonProperty("services")
    @Schema(description = "서비스별 상태 정보")
    private Object services;
}
