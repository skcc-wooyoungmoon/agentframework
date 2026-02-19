package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 벌크 아티팩트 스캔 요청 DTO
 * 
 * <p>여러 아티팩트를 한 번에 스캔하기 위한 요청 데이터 구조입니다.
 * 벌크 처리를 통해 효율적인 아티팩트 스캔을 수행합니다.</p>
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
    description = "벌크 아티팩트 스캔 요청 정보",
    example = """
        {
          "artifact_ids": ["artifact-1", "artifact-2", "artifact-3"],
          "scan_type": "security",
          "options": {
            "deep_scan": true,
            "include_dependencies": true
          }
        }
        """
)
public class BulkArtifactScanRequest {
    
    /**
     * 스캔할 아티팩트 ID 목록
     */
    @JsonProperty("artifact_ids")
    @Schema(description = "스캔할 아티팩트 ID 목록", example = "[\"artifact-1\", \"artifact-2\"]", required = true)
    private List<String> artifactIds;
    
    /**
     * 스캔 타입
     */
    @JsonProperty("scan_type")
    @Schema(description = "스캔 타입", example = "security", allowableValues = {"security", "quality", "license"})
    private String scanType;
    
    /**
     * 스캔 옵션
     */
    @JsonProperty("options")
    @Schema(description = "스캔 옵션")
    private Object options;
}