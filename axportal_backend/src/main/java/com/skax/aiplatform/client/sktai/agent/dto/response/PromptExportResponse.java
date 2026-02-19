package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt Export 응답 DTO
 * 
 * <p>Import를 위한 통합 데이터를 포함하는 응답 구조입니다.
 * messages, variables, tags를 모두 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-11-12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Agent Inference Prompt Export 응답")
public class PromptExportResponse {

    @Schema(description = "응답 시각(UTC epoch millis)")
    private Long timestamp;

    @Schema(description = "응답 코드(예: 1=성공)")
    private Integer code;

    @Schema(description = "상세 메시지")
    private String detail;

    @Schema(description = "트레이스 ID")
    private String traceId;

    @Schema(description = "Export 데이터")
    private PromptExportData data;

    @Schema(description = "추가 페이로드")
    private JsonNode payload;

    /**
     * Export 데이터 구조
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Export 데이터")
    public static class PromptExportData {

        @Schema(description = "프롬프트 이름")
        private String name;

        @Schema(description = "프롬프트 타입")
        private Integer ptype;

        @Schema(description = "프롬프트 설명")
        private String desc;

        @Schema(description = "메시지 목록")
        private List<PromptExportMessage> messages;

        @Schema(description = "변수 목록")
        private List<PromptExportVariable> variables;

        @Schema(description = "태그 목록")
        private List<PromptExportTag> tags;
    }

    /**
     * Export 메시지 구조
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Export 메시지")
    public static class PromptExportMessage {

        @Schema(description = "메시지 순서")
        private Integer sequence;

        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @Schema(description = "메시지 내용")
        private String message;

        @JsonProperty("version_id")
        @Schema(description = "버전 UUID")
        private String versionId;

        @Schema(description = "메시지 타입")
        private Integer mtype;
    }

    /**
     * Export 변수 구조
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Export 변수")
    public static class PromptExportVariable {

        @Schema(description = "변수명")
        private String variable;

        @Schema(description = "검증 규칙")
        private String validation;

        @JsonProperty("variable_uuid")
        @Schema(description = "변수 UUID")
        private String variableUuid;

        @JsonProperty("token_limit")
        @Schema(description = "토큰 제한")
        private Integer tokenLimit;

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
    }

    /**
     * Export 태그 구조
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Export 태그")
    public static class PromptExportTag {

        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @JsonProperty("tag_uuid")
        @Schema(description = "태그 UUID")
        private String tagUuid;

        @Schema(description = "태그명")
        private String tag;

        @JsonProperty("version_id")
        @Schema(description = "버전 UUID")
        private String versionId;
    }
}

