package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent App API 키 재생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App API 키 재생성 요청 정보")
public class AppApiKeyRegenerateRequest {
    
    @JsonProperty("serving_ids")
    @Schema(description = "서빙 ID 목록")
    private List<String> servingIds;
    
    @JsonProperty("regenerate")
    @Schema(description = "재생성 여부", required = true)
    private Boolean regenerate;
}
