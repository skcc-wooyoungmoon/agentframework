package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Evaluation API 기본 정책 페이로드 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 사용되는 기본 정책 페이로드 구조입니다.
 * 권한 범위, 정책 목록, 로직, 결정 전략 등을 포함합니다.</p>
 * 
 * <h3>결정 전략:</h3>
 * <ul>
 *   <li><strong>AFFIRMATIVE</strong>: 하나라도 승인되면 허용</li>
 *   <li><strong>CONSENSUS</strong>: 과반수 승인 시 허용</li>
 *   <li><strong>UNANIMOUS</strong>: 모든 정책 승인 시 허용 (기본값)</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * BasePolicyPayload payload = BasePolicyPayload.builder()
 *     .scopes(Arrays.asList("GET", "POST"))
 *     .policies(Arrays.asList(policy))
 *     .decisionStrategy("UNANIMOUS")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.common.constant.CommCode.HttpMethodScope
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Evaluation API 기본 정책 페이로드",
    example = """
        {
          "scopes": ["GET", "POST", "PUT", "DELETE"],
          "policies": [
            {
              "type": "user",
              "logic": "POSITIVE",
              "names": ["admin"]
            }
          ],
          "logic": "POSITIVE",
          "decision_strategy": "UNANIMOUS",
          "cascade": false
        }
        """
)
public class BasePolicyPayload {
    
    /**
     * 권한 범위 목록
     * 
     * <p>정책이 적용될 HTTP 메서드 범위를 지정합니다.</p>
     * 
     * @apiNote CommCode.HttpMethodScope enum 사용을 권장합니다.
     * @see com.skax.aiplatform.common.constant.CommCode.HttpMethodScope
     */
    @JsonProperty("scopes")
    @Schema(
        description = "정책 적용 HTTP 메서드 범위 - CommCode.HttpMethodScope 사용 권장", 
        example = "[\"GET\", \"POST\", \"PUT\", \"DELETE\"]",
        required = true,
        allowableValues = {"GET", "POST", "PUT", "DELETE"}
    )
    private List<String> scopes;
    
    /**
     * 정책 목록
     * 
     * <p>적용할 기본 정책들의 목록입니다.</p>
     * 
     * @apiNote 최소 1개 이상의 정책이 필요합니다.
     */
    @JsonProperty("policies")
    @Schema(
        description = "적용할 기본 정책 목록", 
        required = true
    )
    private List<BasePolicy> policies;
    
    /**
     * 전체 로직
     * 
     * <p>정책 적용 시 사용할 전체 로직을 지정합니다.
     * POSITIVE는 허용, NEGATIVE는 거부를 의미합니다.</p>
     * 
     * @implNote 기본값은 POSITIVE입니다.
     */
    @JsonProperty("logic")
    @Schema(
        description = "전체 정책 로직 (POSITIVE: 허용, NEGATIVE: 거부)", 
        example = "POSITIVE",
        allowableValues = {"POSITIVE", "NEGATIVE"},
        defaultValue = "POSITIVE"
    )
    @Builder.Default
    private String logic = "POSITIVE";
    
    /**
     * 결정 전략
     * 
     * <p>여러 정책이 있을 때 최종 결정을 내리는 전략을 지정합니다.</p>
     * 
     * @implNote 기본값은 UNANIMOUS입니다.
     */
    @JsonProperty("decision_strategy")
    @Schema(
        description = "결정 전략 (AFFIRMATIVE: 하나라도 승인, CONSENSUS: 과반수, UNANIMOUS: 모든 정책 승인)", 
        example = "UNANIMOUS",
        allowableValues = {"AFFIRMATIVE", "CONSENSUS", "UNANIMOUS"},
        defaultValue = "UNANIMOUS"
    )
    @Builder.Default
    private String decisionStrategy = "UNANIMOUS";
    
    /**
     * 캐스케이드 여부
     * 
     * <p>정책이 하위 리소스까지 적용될지 여부를 지정합니다.</p>
     * 
     * @implNote 기본값은 false입니다.
     */
    @JsonProperty("cascade")
    @Schema(
        description = "하위 리소스까지 정책 적용 여부", 
        example = "false",
        defaultValue = "false"
    )
    @Builder.Default
    private Boolean cascade = false;
}
