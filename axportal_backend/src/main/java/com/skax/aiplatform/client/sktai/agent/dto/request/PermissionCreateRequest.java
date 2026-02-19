package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Permission 생성 요청 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Permission 생성 요청 정보")
public class PermissionCreateRequest {
    
    @JsonProperty("name")
    @Schema(description = "권한 이름", example = "AGENT_READ", required = true)
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "권한 설명", example = "Agent 정보 읽기 권한")
    private String description;
    
    @JsonProperty("resource")
    @Schema(description = "적용 리소스", example = "agent")
    private String resource;
    
    @JsonProperty("actions")
    @Schema(description = "허용 액션 목록", example = "[\"read\", \"list\"]")
    private List<String> actions;
    
    @JsonProperty("scope")
    @Schema(description = "권한 범위", example = "organization")
    private String scope;
}
