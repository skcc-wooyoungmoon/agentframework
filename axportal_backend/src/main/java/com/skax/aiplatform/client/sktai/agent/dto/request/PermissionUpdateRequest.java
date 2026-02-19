package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Permission 수정 요청 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Permission 수정 요청 정보")
public class PermissionUpdateRequest {
    
    @JsonProperty("name")
    @Schema(description = "수정할 권한 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "수정할 권한 설명")
    private String description;
    
    @JsonProperty("resource")
    @Schema(description = "수정할 적용 리소스")
    private String resource;
    
    @JsonProperty("actions")
    @Schema(description = "수정할 허용 액션 목록")
    private List<String> actions;
    
    @JsonProperty("scope")
    @Schema(description = "수정할 권한 범위")
    private String scope;
    
    @JsonProperty("status")
    @Schema(description = "권한 상태")
    private String status;
}
