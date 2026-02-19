package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI RAG 평가 태스크 생성 요청 DTO
 * 
 * <p>새로운 RAG 평가 태스크를 생성하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "RAG 평가 태스크 생성 요청")
public class RagEvaluationTaskCreateRequest {
    
    @JsonProperty("rag_evaluation_id")
    @Schema(description = "RAG 평가 ID", example = "100", required = true)
    private Integer ragEvaluationId;
    
    @JsonProperty("priority")
    @Schema(description = "태스크 우선순위", example = "HIGH")
    private String priority;
    
    @JsonProperty("timeout_minutes")
    @Schema(description = "타임아웃 시간(분)", example = "120")
    private Integer timeoutMinutes;
    
    @JsonProperty("resources")
    @Schema(description = "필요한 리소스 스펙", example = "cpu-8core")
    private String resources;
    
    @JsonProperty("knowledge_base_config")
    @Schema(description = "지식 베이스 설정(JSON 형태)", example = "{\"embedding_model\": \"sentence-transformers\"}")
    private String knowledgeBaseConfig;
    
    @JsonProperty("retrieval_config")
    @Schema(description = "검색 설정(JSON 형태)", example = "{\"top_k\": 5, \"similarity_threshold\": 0.7}")
    private String retrievalConfig;
}
