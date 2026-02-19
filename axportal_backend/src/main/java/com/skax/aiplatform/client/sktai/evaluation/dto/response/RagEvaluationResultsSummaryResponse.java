package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI RAG 평가 결과 요약 응답 DTO
 * 
 * <p>RAG 평가 결과들의 통계 요약 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "RAG 평가 결과 요약 응답")
public class RagEvaluationResultsSummaryResponse {
    
    @JsonProperty("total_results")
    @Schema(description = "총 RAG 평가 결과 수", example = "50")
    private Integer totalResults;
    
    @JsonProperty("average_retrieval_score")
    @Schema(description = "평균 검색 점수", example = "0.85")
    private Double averageRetrievalScore;
    
    @JsonProperty("average_relevance_score")
    @Schema(description = "평균 관련성 점수", example = "0.88")
    private Double averageRelevanceScore;
    
    @JsonProperty("average_faithfulness_score")
    @Schema(description = "평균 충실도 점수", example = "0.83")
    private Double averageFaithfulnessScore;
    
    @JsonProperty("completed_count")
    @Schema(description = "완료된 RAG 평가 수", example = "45")
    private Integer completedCount;
    
    @JsonProperty("failed_count")
    @Schema(description = "실패한 RAG 평가 수", example = "3")
    private Integer failedCount;
    
    @JsonProperty("pending_count")
    @Schema(description = "대기 중인 RAG 평가 수", example = "2")
    private Integer pendingCount;
}
