package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Usage 요청 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model Usage 요청 정보")
public class ModelUsageRequest {
    
    @JsonProperty("usage_uuid_path")
    @Schema(description = "사용 중인 모델의 경로", required = true, maxLength = 500)
    private String usageUuidPath;
}
