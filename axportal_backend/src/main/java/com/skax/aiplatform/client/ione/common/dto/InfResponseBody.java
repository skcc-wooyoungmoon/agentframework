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
@Schema(description = "IONE API 공통 응답 본문")
public class InfResponseBody<T> {
    
    @JsonProperty("result")
    @Schema(description = "처리 결과 정보")
    private IntfResultBody result;

    @JsonProperty("data")
    @Schema(description = "처리 결과 데이터")
    private T data;

}
