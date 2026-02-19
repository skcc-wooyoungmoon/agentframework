package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 데이터셋 하드 삭제 응답 DTO
 * 
 * <p>SKTAI Data API에서 데이터셋 하드 삭제 요청 결과를 반환할 때 사용하는 DTO입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 하드 삭제 응답")
public class DatasetHardDeleteResponse {
    
    @JsonProperty("job_id")
    @Schema(description = "삭제 작업 ID", example = "job-123456")
    private String jobId;
    
    @JsonProperty("status")
    @Schema(description = "작업 상태", example = "started")
    private String status;
    
    @JsonProperty("message")
    @Schema(description = "작업 메시지", example = "백그라운드에서 데이터셋 하드 삭제 작업이 시작되었습니다.")
    private String message;
    
    @JsonProperty("started_at")
    @Schema(description = "작업 시작 시간")
    private LocalDateTime startedAt;
    
    @JsonProperty("estimated_completion")
    @Schema(description = "예상 완료 시간")
    private LocalDateTime estimatedCompletion;
    
    @JsonProperty("datasets_to_delete")
    @Schema(description = "삭제 예정 데이터셋 수", example = "25")
    private Integer datasetsToDelete;
}
