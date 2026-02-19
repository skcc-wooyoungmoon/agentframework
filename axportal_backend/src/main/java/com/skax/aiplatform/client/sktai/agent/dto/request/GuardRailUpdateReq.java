package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * SKTAI GuardRail 수정 요청 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@Schema(description = "가드레일 수정 요청")
public class GuardRailUpdateReq {

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32", required = true)
    private String projectId;

    @JsonProperty("name")
    @Schema(description = "가드레일 이름", example = "Guardrails Example Updated", required = true)
    private String name;

    @JsonProperty("description")
    @Schema(description = "가드레일 설명", example = "Guardrails Description Updated", required = true)
    private String description;

    @JsonProperty("prompt_id")
    @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be", required = true)
    private String promptId;

    @JsonProperty("llms")
    @Schema(description = "LLM 목록", required = true)
    private List<GuardRailLlm> llms;

    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<GuardRailTag> tags;

    /**
     * 가드레일 LLM 정보
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 LLM 정보")
    public static class GuardRailLlm {

        @JsonProperty("serving_id")
        @Schema(description = "서빙 ID", example = "6d4b5a39-a1c3-48e3-ad12-abfb0e483da1", required = true)
        private String servingId;

        @JsonProperty("serving_name")
        @Schema(description = "서빙 이름", example = "GIP/ax4", required = true)
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
        @Schema(description = "태그명", example = "example", required = true)
        private String tag;
    }
}
