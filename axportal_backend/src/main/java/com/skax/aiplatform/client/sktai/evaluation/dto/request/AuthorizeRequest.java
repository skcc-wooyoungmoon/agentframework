package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 인증 요청 DTO
 * 
 * <p>인증 및 권한 확인을 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "인증 요청")
public class AuthorizeRequest {
    
    @JsonProperty("user_id")
    @Schema(description = "사용자 ID", example = "user-123", required = true)
    private String userId;
    
    @JsonProperty("resource")
    @Schema(description = "접근하려는 리소스", example = "model-benchmark", required = true)
    private String resource;
    
    @JsonProperty("action")
    @Schema(description = "수행하려는 액션", example = "read", required = true)
    private String action;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "project-456")
    private String projectId;
}
