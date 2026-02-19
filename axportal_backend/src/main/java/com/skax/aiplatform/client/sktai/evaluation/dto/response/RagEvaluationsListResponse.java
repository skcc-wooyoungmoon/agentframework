package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI RAG 평가 목록 응답 DTO
 * 
 * <p>RAG 평가들의 페이징된 목록을 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "RAG 평가 목록 응답")
public class RagEvaluationsListResponse {
    
    @JsonProperty("total")
    @Schema(description = "전체 RAG 평가 수", example = "50")
    private Integer total;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지", example = "1")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기", example = "20")
    private Integer size;
    
    @JsonProperty("rag_evaluations")
    @Schema(description = "RAG 평가 목록")
    private List<RagEvaluationResponse> ragEvaluations;
}
