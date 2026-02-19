package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Inference Prompt 수정/삭제 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Inference Prompt 수정 또는 삭제 작업 결과를 담는 응답 데이터 구조입니다.
 * 작업 완료 상태와 관련 메타데이터를 포함합니다.</p>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>프롬프트 수정</strong>: PUT 요청 후 수정 완료 확인</li>
 *   <li><strong>프롬프트 삭제</strong>: DELETE 요청 후 삭제 완료 확인</li>
 *   <li><strong>버전 업데이트</strong>: 수정 시 새 버전 생성 확인</li>
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
    description = "SKTAI Agent Inference Prompt 수정/삭제 응답",
    example = """
        {
          "message": "프롬프트가 성공적으로 수정되었습니다.",
          "prompt_uuid": "550e8400-e29b-41d4-a716-446655440000",
          "updated_at": "2025-08-15T11:45:00Z",
          "new_version": "1.3.0",
          "operation": "update"
        }
        """
)
public class PromptUpdateOrDeleteResponse {
    
    /**
     * 응답 메시지
     * 
     * <p>작업 결과에 대한 메시지입니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "작업 결과 메시지", 
        example = "프롬프트가 성공적으로 수정되었습니다."
    )
    private String message;
    
    /**
     * 프롬프트 UUID
     * 
     * <p>작업 대상 프롬프트의 고유 식별자입니다.</p>
     */
    @JsonProperty("prompt_uuid")
    @Schema(
        description = "작업 대상 프롬프트 UUID", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String promptUuid;
    
    /**
     * 작업 시간
     * 
     * <p>작업이 완료된 시간입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "작업 완료 시간", 
        example = "2025-08-15T11:45:00Z",
        format = "date-time"
    )
    private String updatedAt;
    
    /**
     * 새로운 버전 (수정 시만)
     * 
     * <p>수정 작업 시 생성된 새로운 버전 번호입니다. 삭제 시는 null입니다.</p>
     */
    @JsonProperty("new_version")
    @Schema(
        description = "수정 시 생성된 새 버전 (삭제 시 null)", 
        example = "1.3.0"
    )
    private String newVersion;
    
    /**
     * 수행된 작업 타입
     * 
     * <p>수행된 작업의 타입을 나타냅니다.</p>
     */
    @JsonProperty("operation")
    @Schema(
        description = "수행된 작업 타입", 
        example = "update",
        allowableValues = {"update", "delete"}
    )
    private String operation;
}
