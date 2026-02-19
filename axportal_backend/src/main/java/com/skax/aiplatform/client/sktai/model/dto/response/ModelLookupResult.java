package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Lookup Result DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 조회 결과")
public class ModelLookupResult {
    
    @JsonProperty("model_id")
    @Schema(description = "모델 ID", format = "uuid")
    private String modelId;
    
    @JsonProperty("version_id")
    @Schema(description = "버전 ID", format = "uuid")
    private String versionId;
    
    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름")
    private String displayName;
    
    @JsonProperty("model_name")
    @Schema(description = "모델 이름")
    private String modelName;
    
    @JsonProperty("model_description")
    @Schema(description = "모델 설명")
    private String modelDescription;
    
    @JsonProperty("type")
    @Schema(description = "모델 타입")
    private String type;
    
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;
    
    @JsonProperty("is_private")
    @Schema(description = "프라이빗 모델 여부")
    private Boolean isPrivate;
    
    @JsonProperty("is_valid")
    @Schema(description = "유효성 여부")
    private Boolean isValid;
    
    @JsonProperty("provider_name")
    @Schema(description = "모델 제공자 이름")
    private String providerName;
    
    @JsonProperty("model_version")
    @Schema(description = "모델 버전")
    private Integer modelVersion;
}
