package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Lookup 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Lookup 응답 정보")
public class ModelLookupResponse {
    
    @JsonProperty("results")
    @Schema(description = "조회 결과 목록")
    private List<ModelLookupResult> results;
}
