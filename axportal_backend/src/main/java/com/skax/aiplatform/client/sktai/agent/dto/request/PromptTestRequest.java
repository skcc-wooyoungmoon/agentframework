package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent 프롬프트 테스트 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 프롬프트 통합 테스트를 수행하기 위한 요청 데이터 구조입니다.
 * 프롬프트가 실제 추론 환경에서 올바르게 동작하는지 검증할 때 사용됩니다.</p>
 * 
 * <h3>프롬프트 테스트 특징:</h3>
 * <ul>
 *   <li><strong>통합 테스트</strong>: 실제 추론 파이프라인에서 프롬프트 동작 검증</li>
 *   <li><strong>변수 검증</strong>: 프롬프트 변수의 유효성 및 치환 결과 확인</li>
 *   <li><strong>성능 측정</strong>: 프롬프트 처리 시간 및 리소스 사용량 측정</li>
 *   <li><strong>결과 분석</strong>: 예상 결과와 실제 결과 비교 분석</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>새로 생성된 프롬프트의 동작 검증</li>
 *   <li>프롬프트 수정 후 regression 테스트</li>
 *   <li>배포 전 품질 검증</li>
 *   <li>성능 벤치마킹</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PromptTestRequest request = PromptTestRequest.builder()
 *     .testVariables(Map.of("user_name", "홍길동", "topic", "AI"))
 *     .testCases(Arrays.asList("test_case_1", "test_case_2"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see CommonResponse 프롬프트 테스트 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 프롬프트 테스트 요청 정보",
    example = """
        {
          "test_variables": {
            "user_name": "홍길동",
            "topic": "AI"
          },
          "test_cases": ["test_case_1", "test_case_2"]
        }
        """
)
public class PromptTestRequest {
    
    /**
     * 테스트용 변수 맵
     * 
     * <p>프롬프트 테스트 시 사용할 변수들의 키-값 매핑입니다.
     * 프롬프트에 정의된 변수들을 실제 값으로 치환하여 테스트합니다.</p>
     * 
     * @implNote JSON 객체 형태로 전송되며, 프롬프트 변수명과 일치해야 합니다.
     * @apiNote 프롬프트에서 필수로 정의된 변수는 반드시 포함되어야 합니다.
     */
    @JsonProperty("test_variables")
    @Schema(
        description = "테스트용 변수 맵 (프롬프트 변수명과 테스트값 매핑)",
        example = """
            {
              "user_name": "홍길동",
              "topic": "AI",
              "language": "Korean"
            }
            """,
        required = true
    )
    private Object testVariables;
    
    /**
     * 테스트 케이스 목록
     * 
     * <p>실행할 테스트 케이스들의 식별자 목록입니다.
     * 각 테스트 케이스는 특정 시나리오나 조건을 검증합니다.</p>
     * 
     * @implNote 테스트 케이스는 사전에 정의된 것이어야 하며, 시스템에서 인식 가능해야 합니다.
     * @apiNote 빈 목록일 경우 기본 테스트 케이스가 실행됩니다.
     */
    @JsonProperty("test_cases")
    @Schema(
        description = "실행할 테스트 케이스 식별자 목록",
        example = """
            [
              "basic_functionality",
              "edge_cases",
              "performance_test"
            ]
            """
    )
    private List<String> testCases;
}
