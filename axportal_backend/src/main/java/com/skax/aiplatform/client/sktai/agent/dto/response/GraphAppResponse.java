package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Tools 상세 정보 응답")
public class GraphAppResponse {
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "GraphAppDetail 상세 정보")
    private GraphAppDetail data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    /**
     * Graph App 상세 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "GraphAppDetail 상세 정보")
    public static class GraphAppDetail {
        
        @JsonProperty("id")
        @Schema(description = "App ID", example = "40293e28-8ed4-4738-885a-c7982c5edd75")
        private String id;
        
        @JsonProperty("name")
        @Schema(description = "앱 이름", example = "SK 에이전트")
        private String name;

        @JsonProperty("description")
        @Schema(description = "description", example = "SK 에이전트 입니다.")
        private String description;
    }
}