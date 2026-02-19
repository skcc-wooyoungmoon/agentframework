package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Benchmark 목록 응답 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark 목록을 반환하는 응답 데이터 구조입니다.
 * 벤치마크 목록과 페이징 정보를 포함합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: Model Benchmark 목록</li>
 *   <li><strong>payload</strong>: 페이징 메타데이터</li>
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
    description = "SKTAI Model Benchmark 목록 응답",
    example = """
        {
          "data": [
            {
              "id": 1,
              "name": "GPT-4 Performance Benchmark",
              "tasks": "text_generation,question_answering",
              "n_fewshot": 5,
              "dataset_id": "550e8400-e29b-41d4-a716-446655440000",
              "is_custom": true
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 50,
              "last_page": 3,
              "items_per_page": 20
            }
          }
        }
        """
)
public class ModelBenchmarkListResponse {
    
    /**
     * Model Benchmark 목록
     */
    @JsonProperty("data")
    @Schema(description = "Model Benchmark 목록", required = true)
    private List<ModelBenchmarkResponse> data;
    
    /**
     * 페이징 정보
     */
    @JsonProperty("payload")
    @Schema(description = "페이징 메타데이터", required = true)
    private Payload payload;
}
