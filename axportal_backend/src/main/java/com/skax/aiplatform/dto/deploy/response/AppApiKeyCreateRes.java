package com.skax.aiplatform.dto.deploy.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent App API 키 생성 응답 DTO
 * 
 * <p>SKTAI Agent App API 키 생성 시 반환되는 응답 정보를 담는 DTO입니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent App API 키 생성 응답 정보")
public class AppApiKeyCreateRes {
    
    @JsonProperty("api_key")
    @Schema(description = "생성된 API 키", example = "sk-5e81c744ade5b74c13083d9bb38d7a73")
    private String apiKey;
    
    @JsonProperty("started_at")
    @Schema(description = "시작일시", example = "2025-11-10")
    private String startedAt;
    
    @JsonProperty("tag")
    @Schema(description = "태그 목록", example = "[\"agent_app\", \"app-d0416e75-14c0-411a-a975-e4d3f457d8c1\"]")
    private List<String> tag;
    
    @JsonProperty("is_master")
    @Schema(description = "마스터 키 여부", example = "false")
    private Boolean isMaster;
    
    @JsonProperty("is_active")
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
    
    @JsonProperty("internal_key")
    @Schema(description = "내부 키", nullable = true)
    private String internalKey;
    
    @JsonProperty("serving_id")
    @Schema(description = "서빙 ID 목록", example = "[\"d0416e75-14c0-411a-a975-e4d3f457d8c1\"]")
    private List<String> servingId;
    
    @JsonProperty("expires_at")
    @Schema(description = "만료일시", nullable = true)
    private String expiresAt;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-11-10")
    private String createdAt;
    
    @JsonProperty("allowed_host")
    @Schema(description = "허용된 호스트", nullable = true)
    private String allowedHost;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
    
    @JsonProperty("gateway_type")
    @Schema(description = "게이트웨이 타입", example = "agent")
    private String gatewayType;
    
    @JsonProperty("api_key_id")
    @Schema(description = "API 키 ID", example = "c8de6cde-517d-446d-b83e-1053910654f2")
    private String apiKeyId;
}

