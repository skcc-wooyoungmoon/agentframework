package com.skax.aiplatform.dto.deploy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetApiEndpointStatus {
    @Schema(description = "작업 상태", example = "SUCCESS, FAIL")
    private String status;

    @Schema(description = "작업 메시지", example = "작업 메시지")
    private String message;

    @Schema(description = "작업 순번", example = "1234567890")
    private String infWorkSeq;

}
