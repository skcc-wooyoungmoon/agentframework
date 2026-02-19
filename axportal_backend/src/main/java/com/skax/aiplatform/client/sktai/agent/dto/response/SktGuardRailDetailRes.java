package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKT AI GuardRail 상세 조회 응답 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@Schema(description = "SKT AI 가드레일 상세 조회 응답")
public class SktGuardRailDetailRes {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프", example = "2025-10-15T05:26:28.251Z")
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
    @Schema(description = "가드레일 상세 데이터")
    private GuardRailDetailData data;

    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;

    /**
     * 가드레일 상세 데이터
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 상세 데이터")
    public static class GuardRailDetailData {

        @JsonProperty("uuid")
        @Schema(description = "가드레일 UUID", example = "08dbdf23-ebe0-4156-953c-8b3ca62f7881")
        private String uuid;

        @JsonProperty("name")
        @Schema(description = "가드레일 이름", example = "Guardrails Example")
        private String name;

        @JsonProperty("description")
        @Schema(description = "가드레일 설명", example = "This is a Guardrails Example.")
        private String description;

        @JsonProperty("prompt_id")
        @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be")
        private String promptId;

        @JsonProperty("created_at")
        @Schema(description = "생성일시", example = "2024-10-01T12:00:00Z")
        private LocalDateTime createdAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자", example = "admin")
        private String createdBy;

        @JsonProperty("updated_at")
        @Schema(description = "수정일시", example = "2024-10-02T12:00:00Z")
        private LocalDateTime updatedAt;

        @JsonProperty("updated_by")
        @Schema(description = "수정자", example = "editor")
        private String updatedBy;

        @JsonProperty("llms")
        @Schema(description = "LLM 목록")
        private List<GuardRailLlm> llms;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<GuardRailTag> tags;

    }

    /**
     * 가드레일 LLM 정보
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 LLM 정보")
    public static class GuardRailLlm {

        @JsonProperty("serving_name")
        @Schema(description = "서빙 이름", example = "GIP/ax4")
        private String servingName;

    }

    /**
     * 가드레일 태그 정보
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 태그 정보")
    public static class GuardRailTag {

        @JsonProperty("tag")
        @Schema(description = "태그명", example = "example")
        private String tag;

    }

}
