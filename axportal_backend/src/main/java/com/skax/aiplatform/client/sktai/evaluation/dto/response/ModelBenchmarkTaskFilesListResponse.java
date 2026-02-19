package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Benchmark 작업 파일 목록 응답 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark에 업로드된 작업 파일 목록을 반환하는 응답 데이터 구조입니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: 업로드된 파일명 목록</li>
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
    description = "SKTAI Model Benchmark 작업 파일 목록 응답",
    example = """
        {
          "data": ["benchmark_data.csv", "config.json", "results.txt"]
        }
        """
)
public class ModelBenchmarkTaskFilesListResponse {
    
    /**
     * 업로드된 파일명 목록
     */
    @JsonProperty("data")
    @Schema(description = "업로드된 파일명 목록", required = true)
    private List<String> data;
}
