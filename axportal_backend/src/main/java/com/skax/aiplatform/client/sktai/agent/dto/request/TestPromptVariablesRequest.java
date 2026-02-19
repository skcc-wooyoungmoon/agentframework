package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Agent Inference Prompt 변수 테스트 요청 DTO
 * 
 * <p>프롬프트에 정의된 변수들의 검증 규칙을 테스트하기 위한 요청 데이터 구조입니다.
 * 실제 값을 전달하여 변수 검증 로직이 올바르게 동작하는지 확인할 수 있습니다.</p>
 * 
 * <h3>변수 테스트 기능:</h3>
 * <ul>
 *   <li><strong>검증 규칙 테스트</strong>: 정의된 validation 규칙 검증</li>
 *   <li><strong>실제 데이터 시뮬레이션</strong>: 실제 사용될 데이터로 테스트</li>
 *   <li><strong>오류 사전 탐지</strong>: 프로덕션 배포 전 문제점 발견</li>
 *   <li><strong>성능 측정</strong>: 변수 처리 성능 확인</li>
 * </ul>
 * 
 * <h3>테스트 시나리오:</h3>
 * <ul>
 *   <li>새로운 프롬프트 배포 전 변수 검증 테스트</li>
 *   <li>프롬프트 수정 후 기존 변수 호환성 확인</li>
 *   <li>엣지 케이스 및 경계값 테스트</li>
 *   <li>다양한 입력 패턴에 대한 robust성 확인</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 프롬프트에 {{customer_query}}, {{priority_level}} 변수가 정의된 경우
 * Map&lt;String, Object&gt; testValues = Map.of(
 *     "{{customer_query}}", "제품이 정상적으로 작동하지 않습니다. 교환이 가능한가요?",
 *     "{{priority_level}}", "high"
 * );
 * 
 * TestPromptVariablesRequest request = TestPromptVariablesRequest.builder()
 *     .variables(testValues)
 *     .build();
 * 
 * // 프롬프트 "prompt-uuid"의 변수 검증 테스트 실행
 * CommonResponse response = promptsClient.testInferencePromptVariables("prompt-uuid", request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Inference Prompt 변수 테스트 요청 정보",
    example = """
        {
          "variables": {
            "{{customer_query}}": "제품이 정상적으로 작동하지 않습니다. 교환이 가능한가요?",
            "{{priority_level}}": "high",
            "{{customer_name}}": "홍길동",
            "{{product_code}}": "PROD-12345"
          }
        }
        """
)
public class TestPromptVariablesRequest {
    
    /**
     * 테스트할 변수값 맵
     * 
     * <p>프롬프트에 정의된 변수들에 대한 실제 테스트 값들의 맵입니다.
     * 키는 변수명({{variable_name}} 형식), 값은 실제 테스트할 데이터입니다.</p>
     * 
     * <h4>변수값 구성 원칙:</h4>
     * <ul>
     *   <li><strong>완전성</strong>: 프롬프트의 모든 필수 변수에 대한 값 제공</li>
     *   <li><strong>실제성</strong>: 실제 사용될 것과 유사한 데이터 사용</li>
     *   <li><strong>다양성</strong>: 다양한 케이스를 포함한 테스트 데이터</li>
     *   <li><strong>경계값</strong>: min/max 길이, 특수문자 등 경계값 테스트</li>
     * </ul>
     * 
     * <h4>테스트 케이스 예시:</h4>
     * <ul>
     *   <li><strong>정상 케이스</strong>: 일반적인 사용 패턴의 데이터</li>
     *   <li><strong>경계값 케이스</strong>: 최소/최대 길이의 데이터</li>
     *   <li><strong>특수문자 케이스</strong>: 이모지, 특수문자, 다국어 데이터</li>
     *   <li><strong>빈값 케이스</strong>: null, 빈 문자열, 공백 데이터</li>
     * </ul>
     * 
     * <h4>예시 변수값:</h4>
     * <pre>
     * {
     *   "{{customer_query}}": "제품이 정상적으로 작동하지 않습니다.",
     *   "{{priority_level}}": "high",
     *   "{{customer_name}}": "홍길동",
     *   "{{email}}": "hong@example.com",
     *   "{{order_number}}": "ORD-2025-001234",
     *   "{{product_category}}": "전자제품"
     * }
     * </pre>
     * 
     * @implNote 변수명은 반드시 {{variable_name}} 형식으로 프롬프트에 정의된 것과 정확히 일치해야 합니다.
     * @apiNote 정의되지 않은 변수에 대한 값은 무시되며, 필수 변수 누락 시 검증 오류가 발생합니다.
     */
    @JsonProperty("variables")
    @Schema(
        description = "테스트할 변수값 맵 (키: 변수명, 값: 테스트 데이터)",
        required = true,
        example = """
            {
              "{{customer_query}}": "제품이 정상적으로 작동하지 않습니다. 교환이 가능한가요?",
              "{{priority_level}}": "high",
              "{{customer_name}}": "홍길동",
              "{{product_code}}": "PROD-12345"
            }
            """
    )
    private Map<String, Object> variables;
}
