package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Auth Authorize 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 인증/인가를 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Auth Authorize 요청")
public class BodyAuthorizeApiV1AuthAuthorizePost {
    
    @JsonProperty("url_path")
    @Schema(description = "URL 경로", required = true)
    private String urlPath;
    
    @JsonProperty("id")
    @Schema(description = "리소스 ID", required = true)
    private Integer id;
    
    @JsonProperty("policy_payload")
    @Schema(description = "정책 페이로드")
    private PolicyPayload policyPayload;
}
