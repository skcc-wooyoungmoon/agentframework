package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 특정 버전의 가드레일 프롬프트 변수 목록 응답 DTO (현행화)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가드레일프롬프트 변수 목록 응답")
public class GuardRailPromptVarsByIdRes {

    @Schema(description = "버전 UUID")
    private String versionUuid;

    @Schema(description = "변수 목록")
    private List<VariableItem> variables;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "변수 정보 아이템")
    public static class VariableItem {

        @Schema(description = "변수 UUID (서버: variable_uuid)")
        private String variableId;

        @Schema(description = "변수명 (서버: variable)")
        private String variable;

        @Schema(description = "검증 규칙 (서버: validation)")
        private String validation;

        @Schema(description = "검증 플래그 (서버: validation_flag)")
        private Boolean validationFlag;

        @Schema(description = "토큰 제한 플래그 (서버: token_limit_flag)")
        private Boolean tokenLimitFlag;

        @Schema(description = "토큰 제한 값 (서버: token_limit)")
        private Integer tokenLimit;
    }
}

