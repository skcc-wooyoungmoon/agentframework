package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Permission 상세 정보 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Permission 상세 정보 응답")
public class PermissionResponse {
    
    @JsonProperty("permission_id")
    @Schema(description = "권한 고유 식별자")
    private String permissionId;
    
    @JsonProperty("name")
    @Schema(description = "권한 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "권한 설명")
    private String description;
    
    @JsonProperty("resource")
    @Schema(description = "적용 리소스")
    private String resource;
    
    @JsonProperty("actions")
    @Schema(description = "허용 액션 목록")
    private List<String> actions;
    
    @JsonProperty("scope")
    @Schema(description = "권한 범위")
    private String scope;
    
    @JsonProperty("status")
    @Schema(description = "권한 상태")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "마지막 수정 시간")
    private String updatedAt;
}
