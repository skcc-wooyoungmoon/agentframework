package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKT AI GuardRails 목록 응답 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@Schema(description = "SKT AI 가드레일 목록 응답")
public class SktGuardRailsRes {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프", example = "1760319098878")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드", example = "1")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지", example = "성공")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "가드레일 목록 데이터")
    private List<GuardRailData> data;

    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;

    /**
     * 가드레일 데이터
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 데이터")
    public static class GuardRailData {

        @JsonProperty("uuid")
        @Schema(description = "가드레일 UUID", example = "08dbdf23-ebe0-4156-953c-8b3ca62f7881")
        private String uuid;

        @JsonProperty("name")
        @Schema(description = "가드레일 이름", example = "Guardrails Example")
        private String name;

        @JsonProperty("description")
        @Schema(description = "가드레일 설명", example = "This is a Guardrails Example.")
        private String description;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<GuardRailTag> tags;

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

    }

    /**
     * 가드레일 태그
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 태그")
    public static class GuardRailTag {

        @JsonProperty("tag")
        @Schema(description = "태그명", example = "example")
        private String tag;

    }

}

