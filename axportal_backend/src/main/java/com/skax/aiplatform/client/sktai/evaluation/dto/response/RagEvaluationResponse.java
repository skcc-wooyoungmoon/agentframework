package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI RAG 평가 응답 DTO
 * 
 * <p>개별 RAG 평가 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "RAG 평가 응답")
public class RagEvaluationResponse {
    
    @JsonProperty("id")
    @Schema(description = "RAG 평가 ID", example = "1")
    private Integer id;
    
    @JsonProperty("name")
    @Schema(description = "RAG 평가명", example = "RAG Knowledge Test")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "RAG 평가 설명", example = "RAG 시스템의 지식 검색 성능 평가")
    private String description;
    
    @JsonProperty("retrieval_method")
    @Schema(description = "검색 방법", example = "vector_search")
    private String retrievalMethod;
    
    @JsonProperty("knowledge_base")
    @Schema(description = "지식 베이스", example = "wiki_corpus")
    private String knowledgeBase;
    
    @JsonProperty("status")
    @Schema(description = "평가 상태", example = "completed")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}
