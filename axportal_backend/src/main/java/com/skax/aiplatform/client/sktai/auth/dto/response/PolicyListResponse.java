package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 정책 목록 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정책 목록 응답")
public class PolicyListResponse {
    
    @JsonProperty("policies")
    @Schema(description = "정책 목록")
    private List<PolicyResponse> policies;
    
    @JsonProperty("total")
    @Schema(description = "총 정책 수", example = "10")
    private Integer total;
}
