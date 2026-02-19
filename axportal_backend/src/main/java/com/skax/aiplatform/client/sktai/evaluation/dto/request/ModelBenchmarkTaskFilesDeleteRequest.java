package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Benchmark 작업 파일 삭제 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark의 작업 파일을 삭제하기 위한 요청 데이터 구조입니다.
 * 삭제할 파일들의 목록을 포함합니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelBenchmarkTaskFilesDeleteRequest request = ModelBenchmarkTaskFilesDeleteRequest.builder()
 *     .files(Arrays.asList("file1.txt", "file2.json", "data.csv"))
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
    description = "SKTAI Model Benchmark 작업 파일 삭제 요청 정보",
    example = """
        {
          "files": ["benchmark_data.csv", "config.json", "results.txt"]
        }
        """
)
public class ModelBenchmarkTaskFilesDeleteRequest {
    
    /**
     * 삭제할 파일 목록
     * 
     * <p>Model Benchmark 작업에서 삭제할 파일들의 이름 목록입니다.
     * 업로드된 파일 중에서 삭제하고자 하는 파일들을 지정합니다.</p>
     * 
     * @apiNote 파일명은 실제 업로드된 파일과 정확히 일치해야 합니다.
     */
    @JsonProperty("files")
    @Schema(
        description = "삭제할 파일 이름 목록", 
        example = "[\"benchmark_data.csv\", \"config.json\", \"results.txt\"]",
        required = true
    )
    private List<String> files;
}
