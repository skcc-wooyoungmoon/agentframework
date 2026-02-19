package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.dto.prompt.request.GuardRailCreateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * SKT AI GuardRail 생성 요청 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@Schema(description = "SKT AI 가드레일 생성 요청")
public class SktGuardRailCreateReq {

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @JsonProperty("name")
    @Schema(description = "가드레일 이름", example = "Guardrails Example")
    private String name;

    @JsonProperty("description")
    @Schema(description = "가드레일 설명", example = "Guardrails Description")
    private String description;

    @JsonProperty("prompt_id")
    @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be")
    private String promptId;

    @JsonProperty("llms")
    @Schema(description = "LLM 목록")
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
        @Schema(description = "서빙 ID", example = "6d4b5a39-a1c3-48e3-ad12-abfb0e483da1")
        private String servingId;

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

    /**
     * GuardRailCreateReq를 SktGuardRailCreateReq로 변환하는 정적 팩토리 메서드
     *
     * @param request 가드레일 생성 요청
     * @return SktGuardRailCreateReq
     */
    public static SktGuardRailCreateReq from(GuardRailCreateReq request) {
        // LLMs 리스트 변환
        List<GuardRailLlm> llms = request.getLlms().stream()
                .map(llm -> GuardRailLlm.builder()
                        .servingId(llm.getServingId())
                        .servingName(llm.getServingName())
                        .build())
                .toList();

        // Tags 리스트 변환 (null이거나 비어있으면 빈 리스트로 전송)
        List<GuardRailTag> tags;
        if (request.getTags() == null || request.getTags().isEmpty()) {
            tags = Collections.emptyList();
        } else {
            tags = request.getTags().stream()
                    .map(tag -> GuardRailTag.builder()
                            .tag(tag.getTag())
                            .build())
                    .toList();
        }

        return SktGuardRailCreateReq.builder()
                .projectId(request.getProjectId())
                .name(request.getName())
                .description(request.getDescription())
                .promptId(request.getPromptId())
                .llms(llms)
                .tags(tags)
                .build();
    }

}
