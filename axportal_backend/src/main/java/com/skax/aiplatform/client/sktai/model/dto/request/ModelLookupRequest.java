package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Lookup 요청 DTO
 * 
 * <p>여러 모델과 버전을 (model_id, version_id) 쌍으로 조회하는 요청입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Lookup 요청 정보")
public class ModelLookupRequest {
    
    @JsonProperty("items")
    @Schema(description = "조회할 모델 목록", required = true)
    private List<ModelLookupItem> items;
}
