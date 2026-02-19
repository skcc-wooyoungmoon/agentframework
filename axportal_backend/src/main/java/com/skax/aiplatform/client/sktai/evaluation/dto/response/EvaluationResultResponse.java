package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 평가 결과 응답 DTO
 * 
 * <p>개별 평가 결과 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 결과 응답")
public class EvaluationResultResponse {
    
    @JsonProperty("id")
    @Schema(description = "평가 결과 ID", example = "1")
    private Integer id;
    
    @JsonProperty("evaluation_id")
    @Schema(description = "평가 ID", example = "100")
    private Integer evaluationId;
    
    @JsonProperty("score")
    @Schema(description = "평가 점수", example = "0.85")
    private Double score;
    
    @JsonProperty("accuracy")
    @Schema(description = "정확도", example = "0.92")
    private Double accuracy;
    
    @JsonProperty("precision")
    @Schema(description = "정밀도", example = "0.88")
    private Double precision;
    
    @JsonProperty("recall")
    @Schema(description = "재현율", example = "0.90")
    private Double recall;
    
    @JsonProperty("f1_score")
    @Schema(description = "F1 점수", example = "0.89")
    private Double f1Score;
    
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
