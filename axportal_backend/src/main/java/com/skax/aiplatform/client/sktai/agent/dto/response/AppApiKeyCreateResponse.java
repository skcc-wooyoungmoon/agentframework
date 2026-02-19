package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent App API 키 생성 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App API 키 생성 응답 정보")
public class AppApiKeyCreateResponse {
    
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;
    
    @JsonProperty("data")
    @Schema(description = "API 키 생성 데이터")
    private AppApiKeyCreateData data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    /**
     * API 키 생성 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "API 키 생성 데이터")
    public static class AppApiKeyCreateData {
        
        @JsonProperty("api_key")
        @Schema(description = "생성된 API 키")
        private String apiKey;
        
        @JsonProperty("started_at")
        @Schema(description = "시작일시")
        private String startedAt;
        
        @JsonProperty("tag")
        @Schema(description = "태그 목록")
        private List<String> tag;
        
        @JsonProperty("is_master")
        @Schema(description = "마스터 키 여부")
        private Boolean isMaster;
        
        @JsonProperty("is_active")
        @Schema(description = "활성화 여부")
        private Boolean isActive;
        
        @JsonProperty("internal_key")
        @Schema(description = "내부 키", nullable = true)
        private String internalKey;
        
        @JsonProperty("serving_id")
        @Schema(description = "서빙 ID 목록")
        private List<String> servingId;
        
        @JsonProperty("expires_at")
        @Schema(description = "만료일시", nullable = true)
        private String expiresAt;
        
        @JsonProperty("created_at")
        @Schema(description = "생성일시")
        private String createdAt;
        
        @JsonProperty("allowed_host")
        @Schema(description = "허용된 호스트", nullable = true)
        private String allowedHost;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;
        
        @JsonProperty("gateway_type")
        @Schema(description = "게이트웨이 타입")
        private String gatewayType;
        
        @JsonProperty("api_key_id")
        @Schema(description = "API 키 ID")
        private String apiKeyId;
    }
}
