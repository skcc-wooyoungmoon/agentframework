package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App API 키 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App API 키 생성 요청 정보")
public class AppApiKeyCreateRequest {

    @JsonProperty("policy")
    @Schema(description = "정책 목록")
    private List<PolicyRequest> policy;
}
