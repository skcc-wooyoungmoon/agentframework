package com.skax.aiplatform.client.sktai.agentgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SKTAI Agent Gateway Invoke 요청 DTO
 * 
 * <p>SKTAI Agent Gateway에서 에이전트를 실행하기 위한 요청 데이터 구조입니다.
 * 에이전트에게 전달할 메시지와 실행 옵션을 포함합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>messages</strong>: 에이전트에게 전달할 메시지 목록</li>
 * </ul>
 * 
 * <h3>선택 필드:</h3>
 * <ul>
 *   <li><strong>thread_id</strong>: 대화 스레드 식별자 (세션 유지용)</li>
 *   <li><strong>stream</strong>: 스트리밍 응답 여부</li>
 *   <li><strong>metadata</strong>: 추가 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * InvokeRequest request = InvokeRequest.builder()
 *     .messages(List.of(
 *         Map.of("role", "user", "content", "안녕하세요")
 *     ))
 *     .stream(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Gateway Invoke 요청 정보",
    example = """
        {
          "messages": [
            {
              "role": "user",
              "content": "안녕하세요, 도움이 필요합니다."
            }
          ],
          "thread_id": "thread-123",
          "stream": true,
          "metadata": {
            "session_id": "session-456"
          }
        }
        """
)
public class InvokeRequest {
    
    /**
     * 에이전트에게 전달할 메시지 목록
     * 
     * <p>사용자와 에이전트 간의 대화 내역을 나타내는 메시지 배열입니다.
     * 각 메시지는 role(역할)과 content(내용)을 포함해야 합니다.</p>
     * 
     * @apiNote role은 "user", "assistant", "system" 중 하나여야 합니다.
     */
    @JsonProperty("messages")
    @Schema(
        description = "에이전트에게 전달할 메시지 목록 (role과 content 포함)", 
        example = """
            [
              {
                "role": "user",
                "content": "안녕하세요, 도움이 필요합니다."
              }
            ]
            """,
        required = true
    )
    private List<Map<String, Object>> messages;
    
    /**
     * 대화 스레드 식별자
     * 
     * <p>이전 대화와의 연속성을 유지하기 위한 스레드 ID입니다.
     * 지정하지 않으면 새로운 대화로 시작됩니다.</p>
     * 
     * @implNote 동일한 thread_id를 사용하면 이전 대화 맥락이 유지됩니다.
     */
    @JsonProperty("thread_id")
    @Schema(
        description = "대화 스레드 식별자 (세션 유지용)", 
        example = "thread-123"
    )
    private String threadId;
    
    /**
     * 스트리밍 응답 여부
     * 
     * <p>응답을 스트리밍 방식으로 받을지 여부를 결정합니다.
     * true이면 실시간으로 응답을 받을 수 있습니다.</p>
     * 
     * @implNote 스트리밍 모드에서는 Server-Sent Events(SSE) 형태로 응답이 전송됩니다.
     */
    @JsonProperty("stream")
    @Schema(
        description = "스트리밍 응답 여부 (true: 실시간 응답, false: 일괄 응답)", 
        example = "true"
    )
    private Boolean stream;
    
    /**
     * 추가 메타데이터
     * 
     * <p>요청과 함께 전달할 추가 정보입니다.
     * 세션 ID, 사용자 정보, 컨텍스트 정보 등을 포함할 수 있습니다.</p>
     * 
     * @apiNote 메타데이터는 에이전트 실행에 영향을 주지 않고 로깅이나 추적 목적으로 사용됩니다.
     */
    @JsonProperty("metadata")
    @Schema(
        description = "추가 메타데이터 (세션 정보, 컨텍스트 등)", 
        example = """
            {
              "session_id": "session-456",
              "user_id": "user-789"
            }
            """
    )
    private Map<String, Object> metadata;
}
