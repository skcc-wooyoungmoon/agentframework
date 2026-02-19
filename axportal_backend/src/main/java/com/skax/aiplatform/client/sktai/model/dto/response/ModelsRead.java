package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Models 목록 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Models 목록 응답 정보")
public class ModelsRead {
    
    @JsonProperty("data")
    @Schema(description = "모델 목록")
    private List<ModelRead> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;
}
