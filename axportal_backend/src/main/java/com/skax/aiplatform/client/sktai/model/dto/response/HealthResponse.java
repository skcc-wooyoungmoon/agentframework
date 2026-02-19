package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "SKTAI 헬스 체크 응답")
public class HealthResponse {

    @JsonProperty("status")
    @Schema(description = "서비스 상태", example = "UP")
    private String status;

    @JsonProperty("timestamp")
    @Schema(description = "체크 시간", example = "2024-01-01T00:00:00Z")
    private String timestamp;

    @JsonProperty("checks")
    @Schema(description = "개별 체크 결과")
    private java.util.Map<String, Object> checks;

    @JsonProperty("info")
    @Schema(description = "추가 정보")
    private java.util.Map<String, Object> info;
}
