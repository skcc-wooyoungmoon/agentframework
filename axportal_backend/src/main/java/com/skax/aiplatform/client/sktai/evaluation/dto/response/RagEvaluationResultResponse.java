package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI RAG 평가 결과 응답 DTO
 * 
 * <p>개별 RAG 평가 결과 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "RAG 평가 결과 응답")
public class RagEvaluationResultResponse {
    
    @JsonProperty("id")
    @Schema(description = "RAG 평가 결과 ID", example = "1")
    private Integer id;
    
    @JsonProperty("rag_evaluation_id")
    @Schema(description = "RAG 평가 ID", example = "100")
    private Integer ragEvaluationId;
    
    @JsonProperty("retrieval_score")
    @Schema(description = "검색 점수", example = "0.85")
    private Double retrievalScore;
    
    @JsonProperty("relevance_score")
    @Schema(description = "관련성 점수", example = "0.92")
    private Double relevanceScore;
    
    @JsonProperty("faithfulness_score")
    @Schema(description = "충실도 점수", example = "0.88")
    private Double faithfulnessScore;
    
    @JsonProperty("answer_correctness")
    @Schema(description = "답변 정확성", example = "0.90")
    private Double answerCorrectness;
    
    @JsonProperty("contextual_precision")
    @Schema(description = "맥락적 정밀도", example = "0.89")
    private Double contextualPrecision;
    
    @JsonProperty("contextual_recall")
    @Schema(description = "맥락적 재현율", example = "0.87")
    private Double contextualRecall;
    
    @JsonProperty("status")
    @Schema(description = "결과 상태", example = "completed")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}
