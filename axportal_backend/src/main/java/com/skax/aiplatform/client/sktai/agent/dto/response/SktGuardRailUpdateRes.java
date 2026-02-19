package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * SKT AI GuardRail 수정 응답 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@Schema(description = "SKT AI 가드레일 수정 응답")
public class SktGuardRailUpdateRes {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프", example = "2025-10-15T05:29:56.591Z")
    private String timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드", example = "0")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지", example = "string")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID", example = "string")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "수정 결과 데이터")
    private GuardRailUpdateData data;

    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;

    /**
     * 가드레일 수정 결과 데이터
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 수정 결과 데이터")
    public static class GuardRailUpdateData {

        @JsonProperty("result")
        @Schema(description = "수정 결과", example = "true")
        private Boolean result;

    }

}
