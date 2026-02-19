package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI RAG Evaluation 생성 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 새로운 RAG Evaluation을 생성하기 위한 요청 데이터 구조입니다.
 * RAG(Retrieval-Augmented Generation) 모델의 성능을 평가하기 위한 설정을 포함합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: RAG 평가 이름</li>
 *   <li><strong>tasks</strong>: 수행할 평가 작업들</li>
 *   <li><strong>dataset_id</strong>: 사용할 데이터셋 ID</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RagEvaluationCreateRequest request = RagEvaluationCreateRequest.builder()
 *     .name("RAG Performance Evaluation")
 *     .tasks("retrieval_accuracy,generation_quality")
 *     .datasetId("dataset-123")
 *     .isCustom(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI RAG Evaluation 생성 요청 정보",
    example = """
        {
          "id": null,
          "name": "RAG Performance Evaluation",
          "tasks": "retrieval_accuracy,generation_quality",
          "dataset_id": "550e8400-e29b-41d4-a716-446655440000",
          "is_custom": true,
          "policy": null
        }
        """
)
public class RagEvaluationCreateRequest {
    
    /**
     * RAG 평가 식별자
     * 
     * <p>기존 RAG 평가를 업데이트하는 경우 사용하는 ID입니다.
     * 새로 생성하는 경우 null로 설정합니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "RAG 평가 ID (업데이트 시에만 사용, 신규 생성 시 null)", 
        example = "null"
    )
    private Integer id;
    
    /**
     * RAG 평가 이름
     * 
     * <p>RAG Evaluation의 고유한 이름입니다.
     * 평가의 목적이나 대상 RAG 시스템을 명확히 나타내는 이름을 사용합니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "RAG 평가 이름 (최대 255자)", 
        example = "RAG Performance Evaluation",
        required = true,
        maxLength = 255
    )
    private String name;
    
    /**
     * 평가 작업 목록
     * 
     * <p>RAG 평가에서 수행할 작업들의 목록입니다.
     * 쉼표로 구분된 문자열 형태로 제공됩니다.</p>
     */
    @JsonProperty("tasks")
    @Schema(
        description = "수행할 평가 작업 목록 (쉼표로 구분)", 
        example = "retrieval_accuracy,generation_quality,relevance_score",
        required = true
    )
    private String tasks;
    
    /**
     * 데이터셋 식별자
     * 
     * <p>RAG 평가에 사용할 데이터셋의 UUID입니다.
     * 사전에 생성된 데이터셋이어야 합니다.</p>
     */
    @JsonProperty("dataset_id")
    @Schema(
        description = "사용할 데이터셋의 UUID", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        required = true,
        format = "uuid"
    )
    private String datasetId;
    
    /**
     * 커스텀 평가 여부
     * 
     * <p>사용자 정의 RAG 평가인지 여부를 나타냅니다.</p>
     * 
     * @implNote 기본값은 true입니다.
     */
    @JsonProperty("is_custom")
    @Schema(
        description = "커스텀 RAG 평가 여부", 
        example = "true",
        defaultValue = "true"
    )
    @Builder.Default
    private Boolean isCustom = true;
    
    /**
     * 정책 설정
     * 
     * <p>RAG 평가 접근 권한 및 보안 정책입니다.
     * 필요한 경우에만 설정합니다.</p>
     */
    @JsonProperty("policy")
    @Schema(
        description = "RAG 평가 접근 권한 정책 (선택사항)", 
        example = "null"
    )
    private PolicyPayload policy;
}
