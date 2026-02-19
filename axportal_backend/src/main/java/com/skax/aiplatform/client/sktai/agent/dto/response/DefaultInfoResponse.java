package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 시스템 정보 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent 시스템 정보 응답")
public class DefaultInfoResponse {
    
    @JsonProperty("name")
    @Schema(description = "시스템 이름", example = "SKTAI Agent Platform")
    private String name;
    
    @JsonProperty("version")
    @Schema(description = "시스템 버전", example = "1.0.0")
    private String version;
    
    @JsonProperty("description")
    @Schema(description = "시스템 설명")
    private String description;
    
    @JsonProperty("build_info")
    @Schema(description = "빌드 정보")
    private Object buildInfo;
    
    @JsonProperty("features")
    @Schema(description = "지원 기능 목록")
    private Object features;
    
    @JsonProperty("configuration")
    @Schema(description = "시스템 설정 정보")
    private Object configuration;
}
