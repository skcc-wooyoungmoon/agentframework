package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 태스크 생성 응답 DTO
 * 
 * <p>평가 태스크 생성 결과를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "태스크 생성 응답")
public class TaskCreateResponse {
    
    @JsonProperty("task_id")
    @Schema(description = "생성된 태스크 ID", example = "task-123")
    private String taskId;
    
    @JsonProperty("evaluation_id")
    @Schema(description = "평가 ID", example = "100")
    private Integer evaluationId;
    
    @JsonProperty("status")
    @Schema(description = "태스크 상태", example = "created")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @JsonProperty("message")
    @Schema(description = "생성 결과 메시지", example = "Task created successfully")
    private String message;
}
