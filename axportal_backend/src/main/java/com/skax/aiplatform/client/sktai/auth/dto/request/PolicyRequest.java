package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 정책 요청 DTO
 * 
 * <p>정책의 HTTP 메서드 범위, 정책 항목 목록, 로직, 결정 전략 등을 포함합니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PolicyRequest request = PolicyRequest.builder()
 *     .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
 *     .policies(policyItems)
 *     .logic("POSITIVE")
 *     .decisionStrategy("AFFIRMATIVE")
 *     .cascade(true)
 *     .build();
 * </pre>
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 2.0
 * @see com.skax.aiplatform.common.constant.CommCode.HttpMethodScope
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정책 요청")
public class PolicyRequest {
    
    /**
     * 스코프 목록 (HTTP 메서드)
     * 
     * <p>정책이 적용될 HTTP 메서드 범위를 지정합니다.</p>
     * 
     * @apiNote CommCode.HttpMethodScope enum 사용을 권장합니다.
     * @see com.skax.aiplatform.common.constant.CommCode.HttpMethodScope
     */
    @JsonProperty("scopes")
    @Schema(
        description = "스코프 목록 (HTTP 메서드) - CommCode.HttpMethodScope 사용 권장", 
        example = "[\"GET\", \"POST\", \"PUT\", \"DELETE\"]",
        allowableValues = {"GET", "POST", "PUT", "DELETE"}
    )
    private List<String> scopes;
    
    @JsonProperty("policies")
    @Schema(description = "정책 항목 목록")
    private List<PolicyItem> policies;
    
    @JsonProperty("logic")
    @Schema(description = "논리 연산자", example = "POSITIVE")
    private String logic;
    
    @JsonProperty("decision_strategy")
    @Schema(description = "결정 전략", example = "AFFIRMATIVE")
    private String decisionStrategy;
    
    @JsonProperty("cascade")
    @Schema(description = "캐스케이드 여부", example = "true")
    private Boolean cascade;
}
