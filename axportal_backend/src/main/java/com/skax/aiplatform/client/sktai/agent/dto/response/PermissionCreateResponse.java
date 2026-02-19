package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Permission 생성 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Permission 생성 성공 응답")
public class PermissionCreateResponse {
    
    @JsonProperty("permission_id")
    @Schema(description = "생성된 권한의 고유 식별자")
    private String permissionId;
    
    @JsonProperty("name")
    @Schema(description = "생성된 권한 이름")
    private String name;
    
    @JsonProperty("status")
    @Schema(description = "생성된 권한 상태")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @JsonProperty("message")
    @Schema(description = "생성 결과 메시지")
    private String message;
}
