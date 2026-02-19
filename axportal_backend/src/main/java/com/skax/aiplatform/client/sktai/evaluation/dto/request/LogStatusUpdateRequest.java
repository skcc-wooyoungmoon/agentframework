package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 로그 상태 업데이트 요청 DTO
 * 
 * <p>로그들의 상태를 업데이트하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그 상태 업데이트 요청")
public class LogStatusUpdateRequest {
    
    @JsonProperty("log_ids")
    @Schema(description = "업데이트할 로그 ID 목록", example = "[1, 2, 3]")
    private List<Integer> logIds;
    
    @JsonProperty("task_id")
    @Schema(description = "태스크 ID", example = "task-123")
    private String taskId;
    
    @JsonProperty("status")
    @Schema(description = "변경할 상태", example = "processed", required = true)
    private String status;
    
    @JsonProperty("update_reason")
    @Schema(description = "업데이트 사유", example = "Log processing completed")
    private String updateReason;
    
    @JsonProperty("updated_by")
    @Schema(description = "업데이트 수행자", example = "system")
    private String updatedBy;
}
