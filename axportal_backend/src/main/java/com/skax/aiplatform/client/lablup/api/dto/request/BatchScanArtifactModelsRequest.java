package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 배치 아티팩트 모델 스캔 요청 DTO
 * 
 * <p>배치 형태로 여러 아티팩트 모델을 동시에 스캔하기 위한 요청 데이터 구조입니다.
 * 대량의 아티팩트 모델에 대한 효율적인 스캔 처리를 지원합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "배치 아티팩트 모델 스캔 요청 정보",
    example = """
        {
          "artifact_batch": [
            {
              "artifact_id": "artifact-1",
              "model_type": "tensorflow"
            },
            {
              "artifact_id": "artifact-2",
              "model_type": "pytorch"
            }
          ],
          "batch_options": {
            "parallel_processing": true,
            "max_concurrent": 5
          }
        }
        """
)
public class BatchScanArtifactModelsRequest {
    
    /**
     * 배치 스캔할 아티팩트 목록
     */
    @JsonProperty("artifact_batch")
    @Schema(description = "배치 스캔할 아티팩트 목록", required = true)
    private List<ArtifactBatchItem> artifactBatch;
    
    /**
     * 배치 처리 옵션
     */
    @JsonProperty("batch_options")
    @Schema(description = "배치 처리 옵션")
    private Object batchOptions;
    
    /**
     * 아티팩트 배치 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "아티팩트 배치 아이템")
    public static class ArtifactBatchItem {
        
        /**
         * 아티팩트 ID
         */
        @JsonProperty("artifact_id")
        @Schema(description = "아티팩트 ID", example = "artifact-123", required = true)
        private String artifactId;
        
        /**
         * 모델 타입
         */
        @JsonProperty("model_type")
        @Schema(description = "모델 타입", example = "tensorflow")
        private String modelType;
    }
}