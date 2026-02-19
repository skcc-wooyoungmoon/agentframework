package com.skax.aiplatform.dto.deploy.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "API Key 할당량")
public class ApiKeyQuota {
    
    @Schema(description = "할당량 유형", example = "HR", allowableValues = {"MIN","HR", "M", "D", "W", "M", "Y"})
    
    private String type;
    @Schema(description = "할당량 값", example = "100")
    private Integer value;
}
