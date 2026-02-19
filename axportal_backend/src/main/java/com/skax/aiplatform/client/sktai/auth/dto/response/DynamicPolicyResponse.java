package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 동적 정책 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "동적 정책 응답")
public class DynamicPolicyResponse {
    
    @JsonProperty("success")
    @Schema(description = "성공 여부", example = "true")
    private Boolean success;
    
    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "동적 정책이 성공적으로 수정되었습니다.")
    private String message;
    
    @JsonProperty("policies")
    @Schema(description = "수정된 동적 정책 목록")
    private Object policies;
}
