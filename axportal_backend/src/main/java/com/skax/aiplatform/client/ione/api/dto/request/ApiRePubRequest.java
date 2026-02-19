package com.skax.aiplatform.client.ione.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRePubRequest {
        
    @JsonProperty("infWorkUser")
    @Schema(description = "작업 사용자")
    private String infWorkUser;

    @JsonProperty("infWorkSchedule")
    @Schema(description = "작업 데이터")
    private String infWorkSchedule;
}
