package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 작업 콜백 요청 DTO
 * 
 * <p>Task Manager로부터의 콜백을 위해 사용되는 요청 데이터 구조입니다.
 * Training 작업 실패 시 상태 업데이트를 위해 사용됩니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>status</strong>: 작업 상태</li>
 *   <li><strong>workflow_id</strong>: 워크플로우 ID</li>
 *   <li><strong>message</strong>: 메시지</li>
 *   <li><strong>type</strong>: 작업 타입</li>
 *   <li><strong>ref_id</strong>: 참조 ID</li>
 * </ul>
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
    description = "SKTAI Fine-tuning Training 작업 콜백 요청 정보",
    example = """
        {
          "status": "failed",
          "workflow_id": "workflow-123",
          "message": "Training failed due to insufficient resources",
          "type": "training",
          "ref_id": "ref-456"
        }
        """
)
public class TrainingTaskCallback {
    
    /**
     * 작업 상태
     * 
     * <p>Training 작업의 상태를 나타냅니다.
     * Task Manager에서 전달하는 상태 정보입니다.</p>
     * 
     * @apiNote 필수 필드입니다.
     */
    @JsonProperty("status")
    @Schema(
        description = "작업 상태", 
        example = "failed",
        required = true
    )
    private String status;
    
    /**
     * 워크플로우 ID
     * 
     * <p>작업과 연관된 워크플로우의 식별자입니다.
     * Task Manager에서 관리하는 워크플로우를 참조합니다.</p>
     * 
     * @apiNote 필수 필드입니다.
     */
    @JsonProperty("workflow_id")
    @Schema(
        description = "워크플로우 ID", 
        example = "workflow-123",
        required = true
    )
    private String workflowId;
    
    /**
     * 메시지
     * 
     * <p>작업 상태에 대한 상세 메시지입니다.
     * 오류 발생 시 오류 내용, 성공 시 성공 메시지 등을 포함합니다.</p>
     * 
     * @apiNote 필수 필드입니다.
     */
    @JsonProperty("message")
    @Schema(
        description = "작업 상태 메시지", 
        example = "Training failed due to insufficient resources",
        required = true
    )
    private String message;
    
    /**
     * 작업 타입
     * 
     * <p>콜백이 발생한 작업의 타입을 나타냅니다.
     * Training, validation 등의 작업 유형을 구분합니다.</p>
     * 
     * @apiNote 필수 필드입니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "작업 타입", 
        example = "training",
        required = true
    )
    private String type;
    
    /**
     * 참조 ID
     * 
     * <p>콜백 대상이 되는 Training의 참조 ID입니다.
     * Training과 콜백을 연결하는 식별자 역할을 합니다.</p>
     * 
     * @apiNote 필수 필드입니다.
     */
    @JsonProperty("ref_id")
    @Schema(
        description = "참조 ID (Training 연결용)", 
        example = "ref-456",
        required = true
    )
    private String refId;
}
