package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Benchmark 응답 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark 정보를 반환하는 응답 데이터 구조입니다.
 * 벤치마크의 기본 정보와 설정을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>id</strong>: 벤치마크 고유 식별자</li>
 *   <li><strong>name</strong>: 벤치마크 이름</li>
 *   <li><strong>tasks</strong>: 수행할 작업들</li>
 *   <li><strong>n_fewshot</strong>: Few-shot 샘플 수</li>
 *   <li><strong>dataset_id</strong>: 사용 데이터셋 ID</li>
 *   <li><strong>is_custom</strong>: 커스텀 벤치마크 여부</li>
 * </ul>
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
    description = "SKTAI Model Benchmark 응답 정보",
    example = """
        {
          "id": 1,
          "name": "GPT-4 Performance Benchmark",
          "tasks": "text_generation,question_answering",
          "n_fewshot": 5,
          "dataset_id": "550e8400-e29b-41d4-a716-446655440000",
          "is_custom": true
        }
        """
)
public class ModelBenchmarkResponse {
    
    /**
     * 벤치마크 식별자
     */
    @JsonProperty("id")
    @Schema(description = "벤치마크 고유 식별자", example = "1")
    private Integer id;
    
    /**
     * 벤치마크 이름
     */
    @JsonProperty("name")
    @Schema(description = "벤치마크 이름", example = "GPT-4 Performance Benchmark", required = true, minLength = 1, maxLength = 255)
    private String name;
    
    /**
     * 작업 목록
     */
    @JsonProperty("tasks")
    @Schema(description = "수행할 작업 목록", example = "text_generation,question_answering", required = true)
    private String tasks;
    
    /**
     * Few-shot 샘플 수
     */
    @JsonProperty("n_fewshot")
    @Schema(description = "Few-shot 학습 예시 샘플 수", example = "5", required = true, minimum = "0")
    private Integer nFewshot;
    
    /**
     * 데이터셋 식별자
     */
    @JsonProperty("dataset_id")
    @Schema(description = "사용할 데이터셋 UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String datasetId;
    
    /**
     * 커스텀 벤치마크 여부
     */
    @JsonProperty("is_custom")
    @Schema(description = "커스텀 벤치마크 여부", example = "true", defaultValue = "true")
    private Boolean isCustom;
}
