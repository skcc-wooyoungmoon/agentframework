package com.skax.aiplatform.client.deepsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Scan 응답 DTO
 *
 * @author system
 * @since 2025-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Scan 응답 정보")
public class ScanResponse {
    
    @JsonProperty("success")
    @Schema(description = "요청 성공 여부", example = "true")
    private Boolean success;
    
    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "Model registered successfully")
    private String message;
    
    @JsonProperty("model_id")
    @Schema(description = "등록된 모델 ID", example = "model-12345")
    private String modelId;
    
    @JsonProperty("uid")
    @Schema(description = "사용자 ID", example = "123456")
    private String uid;
    
    @JsonProperty("filename")
    @Schema(description = "모델 파일명", example = "meta-llama/Llama-3.1-8b-Instruct")
    private String filename;
    
    @JsonProperty("status")
    @Schema(description = "모델 상태", example = "active")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", example = "2025-01-15T10:30:00Z")
    private String createdAt;
}