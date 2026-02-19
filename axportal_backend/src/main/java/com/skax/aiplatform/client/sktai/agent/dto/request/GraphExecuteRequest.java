package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Agent Graph 실행 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Graph를 실행하기 위한 요청 데이터 구조입니다.
 * Query(동기) 실행과 Stream(비동기) 실행 모두에 사용되며, Graph의 입력 변수와 실행 옵션을 포함합니다.</p>
 * 
 * <h3>Graph 실행 특징:</h3>
 * <ul>
 *   <li><strong>동적 입력</strong>: Graph에 정의된 변수에 런타임 값 전달</li>
 *   <li><strong>실행 모드</strong>: Query(동기), Stream(비동기), Test(테스트) 지원</li>
 *   <li><strong>컨텍스트 관리</strong>: 실행별 독립적인 컨텍스트 유지</li>
 *   <li><strong>결과 추적</strong>: 실행 과정과 결과를 상세히 기록</li>
 * </ul>
 * 
 * <h3>실행 모드별 특징:</h3>
 * <ul>
 *   <li><strong>Query 모드</strong>: 완전한 실행 결과를 한 번에 반환</li>
 *   <li><strong>Stream 모드</strong>: 실행 과정을 실시간 스트리밍</li>
 *   <li><strong>Test 모드</strong>: DB 저장 없이 테스트 실행</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphExecuteRequest request = GraphExecuteRequest.builder()
 *     .graphId("graph-123")
 *     .inputs(Map.of(
 *         "user_query", "환불 정책을 알려주세요",
 *         "language", "ko"
 *     ))
 *     .options(Map.of(
 *         "temperature", 0.7,
 *         "max_tokens", 500
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GraphExecuteResponse Graph 실행 결과 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 실행 요청",
    example = """
        {
          "graph_id": "graph-123",
          "inputs": {
            "user_query": "환불 정책을 알려주세요",
            "language": "ko"
          },
          "options": {
            "temperature": 0.7,
            "max_tokens": 500
          }
        }
        """
)
public class GraphExecuteRequest {
    
    /**
     * Graph 식별자
     * 
     * <p>실행할 Graph의 고유 식별자입니다.
     * UUID 형태의 문자열로, 특정 Graph를 지정하는 데 사용됩니다.</p>
     * 
     * @implNote Graph ID는 사전에 생성된 유효한 Graph여야 합니다.
     */
    @JsonProperty("graph_id")
    @Schema(
        description = "실행할 Graph의 UUID", 
        example = "graph-123",
        required = true
    )
    private String graphId;
    
    /**
     * 입력 변수
     * 
     * <p>Graph 실행 시 사용할 입력 변수들의 키-값 쌍입니다.
     * Graph에 정의된 입력 노드의 변수명과 일치해야 합니다.</p>
     * 
     * @implNote 변수명은 Graph 설계 시 정의된 이름과 정확히 일치해야 합니다.
     * @apiNote 필수 입력 변수가 누락되면 실행이 실패할 수 있습니다.
     */
    @JsonProperty("inputs")
    @Schema(
        description = "Graph 입력 변수 (키-값 쌍)", 
        example = """
            {
              "user_query": "환불 정책을 알려주세요",
              "language": "ko",
              "user_id": "user123"
            }
            """,
        required = true
    )
    private Map<String, Object> inputs;
    
    /**
     * 입력 데이터 (SKT AI Platform API 요구사항)
     * 
     * <p>SKT AI Platform API에서 필수로 요구하는 input_data 필드입니다.
     * messages 배열과 추가 정보를 포함할 수 있습니다.</p>
     */
    @JsonProperty("input_data")
    @Schema(
        description = "입력 데이터 (SKT AI Platform API 필수 필드)", 
        example = """
            {
              "messages": [
                {
                  "content": "안녕하세요",
                  "type": "human"
                }
              ],
              "additional_kwargs": {}
            }
            """,
        required = true
    )
    private Map<String, Object> inputData;
    
    /**
     * 실행 옵션
     * 
     * <p>Graph 실행 시 적용할 추가 옵션들입니다.
     * 모델 파라미터, 실행 제한, 로깅 설정 등을 포함할 수 있습니다.</p>
     * 
     * @implNote 옵션은 선택사항이며, 제공하지 않으면 기본값이 사용됩니다.
     */
    @JsonProperty("options")
    @Schema(
        description = "Graph 실행 옵션 (선택사항)", 
        example = """
            {
              "temperature": 0.7,
              "max_tokens": 500,
              "timeout": 30000,
              "save_history": true
            }
            """
    )
    private Map<String, Object> options;
    
    /**
     * 세션 ID
     * 
     * <p>연속된 대화나 상태를 유지해야 하는 경우 사용할 세션 식별자입니다.
     * 동일한 세션 ID로 여러 번 실행하면 컨텍스트가 유지됩니다.</p>
     */
    @JsonProperty("session_id")
    @Schema(
        description = "세션 ID (상태 유지를 위한 식별자)", 
        example = "session-456"
    )
    private String sessionId;
    
    /**
     * 실행 모드
     * 
     * <p>Graph 실행 방식을 지정합니다.
     * sync(동기), async(비동기), test(테스트) 등의 값을 가질 수 있습니다.</p>
     */
    @JsonProperty("execution_mode")
    @Schema(
        description = "실행 모드", 
        example = "sync",
        allowableValues = {"sync", "async", "test", "stream"}
    )
    private String executionMode;
}
