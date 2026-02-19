package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 변수 목록 응답 DTO
 *
 * <p>실제 응답(envelope + data[]) 구조를 반영합니다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        description = "SKTAI Agent Inference Prompt 변수 목록 응답",
        example = """
            {
                "timestamp": 1756226572268,
                "code": 1,
                "detail": "성공",
                "traceId": null,
                "data": [
                    {
                        "validation_flag": false,
                        "token_limit_flag": false,
                        "uuid": "b7776afe-c266-44b1-8c42-6a15a0d4e988",
                        "version_id": "7865e1e3-42b2-448b-93b8-ff7515ff6f3e",
                        "variable": "query",
                        "variable_uuid": "cc2a0e23-47a9-4e28-9653-b19ec1c056b8",
                        "validation": "",
                        "token_limit": 0
                    }
                ],
                "payload": null
            }
            """
)
public class PromptVariablesResponse {

    @Schema(description = "응답 시각(UTC epoch millis)")
    private Long timestamp;

    @Schema(description = "응답 코드(예: 1=성공)")
    private Integer code;

    @Schema(description = "상세 메시지")
    private String detail;

    @Schema(description = "트레이스 ID")
    private String traceId;

    @Schema(description = "변수 데이터 목록")
    private List<PromptVariableItem> data;

    @Schema(description = "추가 페이로드")
    private JsonNode payload;

    /**
     * 단일 프롬프트 변수 아이템 (data 배열 원소)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "프롬프트 변수 아이템")
    public static class PromptVariableItem {

        @JsonProperty("validation_flag")
        @Schema(description = "검증 플래그")
        private Boolean validationFlag;

        @JsonProperty("token_limit_flag")
        @Schema(description = "토큰 제한 플래그")
        private Boolean tokenLimitFlag;

        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @JsonProperty("version_id")
        @Schema(description = "버전 UUID")
        private String versionId;

        @Schema(description = "변수명")
        private String variable;

        @JsonProperty("variable_uuid")
        @Schema(description = "변수 UUID")
        private String variableUuid;

        @Schema(description = "검증 규칙(정규식 등)")
        private String validation;

        @JsonProperty("token_limit")
        @Schema(description = "토큰 제한 값")
        private Integer tokenLimit;
    }
}