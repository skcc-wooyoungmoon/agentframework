package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 평가 결과 요약 응답 DTO
 * 
 * <p>평가 결과들의 통계 요약 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 결과 요약 응답")
public class EvaluationResultsSummaryResponse {
    
    @JsonProperty("total_results")
    @Schema(description = "총 결과 수", example = "100")
    private Integer totalResults;
    
    @JsonProperty("average_score")
    @Schema(description = "평균 점수", example = "0.85")
    private Double averageScore;
    
    @JsonProperty("best_score")
    @Schema(description = "최고 점수", example = "0.95")
    private Double bestScore;
    
    @JsonProperty("worst_score")
    @Schema(description = "최저 점수", example = "0.72")
    private Double worstScore;
    
    @JsonProperty("completed_count")
    @Schema(description = "완료된 평가 수", example = "95")
    private Integer completedCount;
    
    @JsonProperty("failed_count")
    @Schema(description = "실패한 평가 수", example = "5")
    private Integer failedCount;
    
    @JsonProperty("pending_count")
    @Schema(description = "대기 중인 평가 수", example = "0")
    private Integer pendingCount;
}
