package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 정책 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정책 응답")
public class PolicyResponse {
    
    @JsonProperty("id")
    @Schema(description = "정책 ID", example = "policy-123")
    private String id;
    
    @JsonProperty("name")
    @Schema(description = "정책 이름", example = "user_access_policy")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "정책 설명", example = "사용자 접근 정책")
    private String description;
    
    @JsonProperty("rules")
    @Schema(description = "정책 규칙")
    private Object rules;
    
    @JsonProperty("type")
    @Schema(description = "정책 타입", example = "default")
    private String type;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-08-22T10:30:00Z")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-08-22T10:30:00Z")
    private String updatedAt;
}
