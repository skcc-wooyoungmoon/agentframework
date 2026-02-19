package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SKTAI Agent Graph 실행 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Graph 실행 결과를 나타내는 응답 데이터 구조입니다.
 * Query(동기), Stream(비동기), Test(테스트) 실행 모드의 결과를 포함하며, 실행 과정과 최종 결과를 상세히 제공합니다.</p>
 * 
 * <h3>실행 결과 포함 항목:</h3>
 * <ul>
 *   <li><strong>실행 정보</strong>: 실행 ID, Graph ID, 상태</li>
 *   <li><strong>입출력 데이터</strong>: 실행 입력, 최종 출력</li>
 *   <li><strong>성능 지표</strong>: 실행 시간, 노드별 처리 시간</li>
 *   <li><strong>실행 추적</strong>: 노드별 실행 순서와 결과</li>
 * </ul>
 * 
 * <h3>실행 상태:</h3>
 * <ul>
 *   <li><strong>running</strong>: 실행 중 (Stream 모드에서 주로 사용)</li>
 *   <li><strong>completed</strong>: 정상 완료</li>
 *   <li><strong>failed</strong>: 실행 실패</li>
 *   <li><strong>cancelled</strong>: 사용자에 의한 취소</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphExecuteResponse result = graphClient.executeGraph(request);
 * String executionId = result.getExecutionId();
 * Map&lt;String, Object&gt; outputs = result.getOutputs();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GraphExecuteRequest Graph 실행 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 실행 결과 응답",
    example = """
        {
          "execution_id": "exec-123e4567-e89b-12d3-a456-426614174000",
          "graph_id": "graph-456",
          "status": "completed",
          "inputs": {
            "user_query": "환불 정책을 알려주세요"
          },
          "outputs": {
            "response": "환불 정책은 다음과 같습니다...",
            "confidence": 0.95
          },
          "execution_time_ms": 2500,
          "started_at": "2025-08-22T10:30:00Z",
          "completed_at": "2025-08-22T10:30:02Z"
        }
        """
)
public class GraphExecuteResponse {
    
    /**
     * 실행 고유 식별자
     * 
     * <p>Graph 실행의 고유한 UUID입니다.
     * 실행 결과 추적 및 로그 분석에 사용됩니다.</p>
     */
    @JsonProperty("execution_id")
    @Schema(
        description = "실행 고유 식별자 (UUID)", 
        example = "exec-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String executionId;
    
    /**
     * Graph 식별자
     * 
     * <p>실행된 Graph의 UUID입니다.</p>
     */
    @JsonProperty("graph_id")
    @Schema(
        description = "실행된 Graph의 UUID", 
        example = "graph-456"
    )
    private String graphId;
    
    /**
     * 실행 상태
     * 
     * <p>Graph 실행의 현재 상태입니다.</p>
     */
    @JsonProperty("status")
    @Schema(
        description = "실행 상태", 
        example = "completed",
        allowableValues = {"running", "completed", "failed", "cancelled"}
    )
    private String status;
    
    /**
     * 입력 데이터
     * 
     * <p>Graph 실행 시 사용된 입력 변수들입니다.</p>
     */
    @JsonProperty("inputs")
    @Schema(
        description = "Graph 실행 입력 데이터", 
        example = """
            {
              "user_query": "환불 정책을 알려주세요",
              "language": "ko"
            }
            """
    )
    private Map<String, Object> inputs;
    
    /**
     * 출력 데이터
     * 
     * <p>Graph 실행의 최종 결과 데이터입니다.</p>
     */
    @JsonProperty("outputs")
    @Schema(
        description = "Graph 실행 출력 데이터", 
        example = """
            {
              "response": "환불 정책은 다음과 같습니다...",
              "confidence": 0.95,
              "source": "knowledge_base"
            }
            """
    )
    private Map<String, Object> outputs;
    
    /**
     * 실행 시간
     * 
     * <p>Graph 실행에 소요된 총 시간(밀리초)입니다.</p>
     */
    @JsonProperty("execution_time_ms")
    @Schema(
        description = "실행 시간 (밀리초)", 
        example = "2500",
        minimum = "0"
    )
    private Long executionTimeMs;
    
    /**
     * 실행 시작 시간
     * 
     * <p>Graph 실행이 시작된 날짜와 시간입니다.</p>
     */
    @JsonProperty("started_at")
    @Schema(
        description = "실행 시작 시간 (ISO 8601)", 
        example = "2025-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime startedAt;
    
    /**
     * 실행 완료 시간
     * 
     * <p>Graph 실행이 완료된 날짜와 시간입니다.</p>
     */
    @JsonProperty("completed_at")
    @Schema(
        description = "실행 완료 시간 (ISO 8601)", 
        example = "2025-08-22T10:30:02Z",
        format = "date-time"
    )
    private LocalDateTime completedAt;
    
    /**
     * 노드별 실행 추적
     * 
     * <p>Graph 내 각 노드의 실행 순서와 결과를 추적한 정보입니다.</p>
     */
    @JsonProperty("execution_trace")
    @Schema(
        description = "노드별 실행 추적 정보", 
        example = """
            [
              {
                "node_id": "input_node",
                "execution_order": 1,
                "status": "completed",
                "execution_time_ms": 50
              },
              {
                "node_id": "llm_node",
                "execution_order": 2,
                "status": "completed",
                "execution_time_ms": 2200
              }
            ]
            """
    )
    private List<Object> executionTrace;
    
    /**
     * 오류 정보
     * 
     * <p>실행 실패 시 오류 상세 정보입니다.</p>
     */
    @JsonProperty("error")
    @Schema(
        description = "실행 오류 정보 (실패 시)", 
        example = """
            {
              "error_code": "NODE_EXECUTION_FAILED",
              "error_message": "LLM 노드에서 처리 오류가 발생했습니다",
              "failed_node_id": "llm_node"
            }
            """
    )
    private Object error;
    
    /**
     * 세션 ID
     * 
     * <p>실행과 연관된 세션 식별자입니다.</p>
     */
    @JsonProperty("session_id")
    @Schema(
        description = "세션 ID", 
        example = "session-456"
    )
    private String sessionId;
    
    /**
     * 실행 모드
     * 
     * <p>실행된 모드 정보입니다.</p>
     */
    @JsonProperty("execution_mode")
    @Schema(
        description = "실행 모드", 
        example = "sync",
        allowableValues = {"sync", "async", "test", "stream"}
    )
    private String executionMode;
}
