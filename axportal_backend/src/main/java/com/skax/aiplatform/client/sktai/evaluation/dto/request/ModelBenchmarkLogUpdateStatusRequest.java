package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Benchmark 로그 상태 업데이트 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark 로그의 상태를 업데이트하기 위한 요청 데이터 구조입니다.
 * 벤치마크 실행 상태와 관련 메시지를 포함합니다.</p>
 * 
 * <h3>상태 코드:</h3>
 * <ul>
 *   <li><strong>0</strong>: 대기 중</li>
 *   <li><strong>1</strong>: 실행 중</li>
 *   <li><strong>2</strong>: 완료</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelBenchmarkLogUpdateStatusRequest request = ModelBenchmarkLogUpdateStatusRequest.builder()
 *     .status(2)  // 완료 상태
 *     .message("Benchmark completed successfully")
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
    description = "SKTAI Model Benchmark 로그 상태 업데이트 요청 정보",
    example = """
        {
          "status": 2,
          "message": "Benchmark completed successfully"
        }
        """
)
public class ModelBenchmarkLogUpdateStatusRequest {
    
    /**
     * 벤치마크 상태
     * 
     * <p>Model Benchmark의 현재 실행 상태를 나타내는 정수 코드입니다.</p>
     * 
     * @implNote 0: 대기 중, 1: 실행 중, 2: 완료
     */
    @JsonProperty("status")
    @Schema(
        description = "벤치마크 실행 상태 (0: 대기 중, 1: 실행 중, 2: 완료)", 
        example = "2",
        required = true,
        allowableValues = {"0", "1", "2"}
    )
    private Integer status;
    
    /**
     * 상태 메시지
     * 
     * <p>벤치마크 상태와 관련된 상세 메시지입니다.
     * 실행 진행 상황, 오류 정보, 완료 알림 등을 포함할 수 있습니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "벤치마크 상태 관련 메시지", 
        example = "Benchmark completed successfully"
    )
    private String message;
}
