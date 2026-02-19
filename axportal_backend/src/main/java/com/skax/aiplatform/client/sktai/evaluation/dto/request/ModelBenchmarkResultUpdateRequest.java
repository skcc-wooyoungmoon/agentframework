package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Benchmark 결과 업데이트 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 백그라운드에서 Model Benchmark 결과를 업데이트하기 위한 요청 데이터 구조입니다.
 * 결과 파일을 읽어서 데이터베이스에 업데이트하는 작업을 수행합니다.</p>
 * 
 * <h3>업데이트 프로세스:</h3>
 * <ul>
 *   <li>Model Benchmark 로그 ID를 기준으로 결과 파일을 읽습니다</li>
 *   <li>파일 내용을 파싱하여 결과 데이터를 추출합니다</li>
 *   <li>추출된 데이터를 데이터베이스에 업데이트합니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelBenchmarkResultUpdateRequest request = ModelBenchmarkResultUpdateRequest.builder()
 *     .modelBenchmarkLogId(123)
 *     .policy(policyPayload)
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
    description = "SKTAI Model Benchmark 결과 업데이트 요청 정보",
    example = """
        {
          "model_benchmark_log_id": 123,
          "policy": null
        }
        """
)
public class ModelBenchmarkResultUpdateRequest {
    
    /**
     * Model Benchmark 로그 식별자
     * 
     * <p>업데이트할 결과와 연관된 Model Benchmark 로그의 ID입니다.
     * 이 ID를 기준으로 해당 로그의 결과 파일을 찾아 업데이트를 수행합니다.</p>
     */
    @JsonProperty("model_benchmark_log_id")
    @Schema(
        description = "업데이트할 Model Benchmark 로그 ID", 
        example = "123",
        required = true
    )
    private Integer modelBenchmarkLogId;
    
    /**
     * 정책 설정
     * 
     * <p>결과 업데이트 시 적용할 접근 권한 및 보안 정책입니다.
     * 필요한 경우에만 설정합니다.</p>
     */
    @JsonProperty("policy")
    @Schema(
        description = "업데이트 시 적용할 정책 (선택사항)", 
        example = "null"
    )
    private PolicyPayload policy;
}
