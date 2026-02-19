package com.skax.aiplatform.client.ione.common.dto;

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
public class InfRequestBody<T> {

    @JsonProperty("data")
    @Schema(description = "작업 데이터")
    private InfReqData<T> data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InfReqData <T> {
        @JsonProperty("infWorkUser")
        @Schema(description = "작업 사용자")
        private String infWorkUser;
    
        @JsonProperty("infWorkData")
        @Schema(description = "작업 데이터")    
        private T infWorkData;
    }

    
}
