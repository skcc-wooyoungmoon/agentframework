package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Evaluation API 작업 실패 요청 DTO
 * 
 * <p>Task Manager에서 작업 실패 시 콜백으로 사용되는 요청 데이터 구조입니다.
 * 작업 상태, 워크플로우 정보, 오류 메시지 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>status</strong>: 작업 실패 상태</li>
 *   <li><strong>workflow_id</strong>: 워크플로우 식별자</li>
 *   <li><strong>message</strong>: 실패 원인 메시지</li>
 *   <li><strong>type</strong>: 작업 타입</li>
 *   <li><strong>ref_id</strong>: 참조 식별자</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TaskFailedRequest request = TaskFailedRequest.builder()
 *     .status("FAILED")
 *     .workflowId("workflow-123")
 *     .message("Task execution failed")
 *     .type("EVALUATION")
 *     .refId("ref-456")
 *     .build();
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
    description = "SKTAI Evaluation API 작업 실패 요청 정보",
    example = """
        {
          "status": "FAILED",
          "workflow_id": "workflow-123",
          "message": "Task execution failed",
          "type": "EVALUATION",
          "ref_id": "ref-456"
        }
        """
)
public class TaskFailedRequest {
    
    /**
     * 작업 상태
     * 
     * <p>작업의 실패 상태를 나타냅니다.</p>
     * 
     * @implNote 일반적으로 "FAILED" 값을 사용합니다.
     */
    @JsonProperty("status")
    @Schema(
        description = "작업 실패 상태", 
        example = "FAILED",
        required = true
    )
    private String status;
    
    /**
     * 워크플로우 식별자
     * 
     * <p>실패한 작업이 속한 워크플로우의 고유 식별자입니다.</p>
     */
    @JsonProperty("workflow_id")
    @Schema(
        description = "워크플로우 고유 식별자", 
        example = "workflow-123",
        required = true
    )
    private String workflowId;
    
    /**
     * 실패 메시지
     * 
     * <p>작업 실패의 원인이나 상세 정보를 설명하는 메시지입니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "작업 실패 원인 메시지", 
        example = "Task execution failed due to resource limitation",
        required = true
    )
    private String message;
    
    /**
     * 작업 타입
     * 
     * <p>실패한 작업의 타입을 나타냅니다.</p>
     * 
     * @implNote EVALUATION, BENCHMARK 등의 값을 사용할 수 있습니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "작업 타입 (예: EVALUATION, BENCHMARK)", 
        example = "EVALUATION",
        required = true
    )
    private String type;
    
    /**
     * 참조 식별자
     * 
     * <p>작업을 식별하기 위한 참조 ID입니다.</p>
     */
    @JsonProperty("ref_id")
    @Schema(
        description = "작업 참조 식별자", 
        example = "ref-456",
        required = true
    )
    private String refId;
}
