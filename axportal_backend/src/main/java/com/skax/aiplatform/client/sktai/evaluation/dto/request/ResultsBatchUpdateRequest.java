package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 결과 일괄 업데이트 요청 DTO
 * 
 * <p>여러 평가 결과를 일괄적으로 업데이트하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "결과 일괄 업데이트 요청")
public class ResultsBatchUpdateRequest {
    
    @JsonProperty("result_ids")
    @Schema(description = "업데이트할 결과 ID 목록", example = "[1, 2, 3]", required = true)
    private List<Integer> resultIds;
    
    @JsonProperty("status")
    @Schema(description = "변경할 상태", example = "completed")
    private String status;
    
    @JsonProperty("update_reason")
    @Schema(description = "업데이트 사유", example = "Manual status update")
    private String updateReason;
    
    @JsonProperty("updated_by")
    @Schema(description = "업데이트 수행자", example = "user-123")
    private String updatedBy;
}
