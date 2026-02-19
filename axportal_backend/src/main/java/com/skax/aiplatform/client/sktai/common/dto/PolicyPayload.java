package com.skax.aiplatform.client.sktai.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * SKTAI Evaluation API 정책 페이로드 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 사용되는 정책 페이로드 구조입니다.
 * 기본 정책 페이로드들의 배열로 구성됩니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * import static com.skax.aiplatform.common.constant.CommCode.HttpMethodScope.*;
 * 
 * List&lt;BasePolicyPayload&gt; policyPayload = Arrays.asList(
 *     BasePolicyPayload.builder()
 *         .scopes(Arrays.asList("GET", "POST"))
 *         .policies(Arrays.asList(policy))
 *         .build()
 * );
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.common.constant.CommCode.HttpMethodScope
 */
@Schema(
    description = "SKTAI Evaluation API 정책 페이로드 (기본 정책 페이로드 배열)",
    example = """
        [
          {
            "cascade": false,
            "decision_strategy": "UNANIMOUS",
            "logic": "POSITIVE",
            "policies": [
              {
                "logic": "POSITIVE",
                "names": ["admin"],
                "type": "user"
              }
            ],
            "scopes": ["GET", "POST", "PUT", "DELETE"]
          }
        ]
        """
)
public class PolicyPayload extends java.util.ArrayList<BasePolicyPayload> {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 기본 생성자
     */
    public PolicyPayload() {
        super();
    }
    
    /**
     * 컬렉션을 받는 생성자
     * 
     * @param policies 기본 정책 페이로드 목록
     */
    public PolicyPayload(List<BasePolicyPayload> policies) {
        super(policies);
    }
}
