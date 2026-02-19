package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Lookup Item DTO
 * 
 * <p>모델 조회를 위한 (model_id, version_id) 쌍 정보입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 조회 항목")
public class ModelLookupItem {
    
    @JsonProperty("model_id")
    @Schema(description = "모델 ID")
    private String modelId;
    
    @JsonProperty("version_id")
    @Schema(description = "버전 ID")
    private String versionId;
}
