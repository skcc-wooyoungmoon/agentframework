package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.dto.prompt.request.GuardRailUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * SKT AI GuardRail 수정 요청 DTO
 *
 * @author doohyeon
 * @version 1.0
 * @since 2025-10-15
 */
@Getter
@Builder
@ToString
@Schema(description = "SKT AI 가드레일 수정 요청")
public class SktGuardRailUpdateReq {

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @JsonProperty("name")
    @Schema(description = "가드레일 이름", example = "Guardrails Example Updated")
    private String name;

    @JsonProperty("description")
    @Schema(description = "가드레일 설명", example = "Guardrails Description Updated")
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
    @ToString
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
     * GuardRailUpdateReq를 SktGuardRailUpdateReq로 변환하는 정적 팩토리 메서드
     *
     * @param request 가드레일 수정 요청
     * @return SktGuardRailUpdateReq
     */
    public static SktGuardRailUpdateReq from(GuardRailUpdateReq request) {
        // GuardRailUpdateReq의 필드를 SktGuardRailUpdateReq로 변환
        // - llms: 요청에서 받은 값을 SktGuardRailUpdateReq.GuardRailLlm으로 타입 변환
        // - tags: 항상 빈 배열로 설정 (수정 요청에서는 태그 변경 불가)
        List<GuardRailLlm> convertedLlms = null;
        if (request.getLlms() != null) {
            convertedLlms = request.getLlms().stream()
                    .map(llm -> GuardRailLlm.builder()
                            .servingName(llm.getServingName())
                            .build())
                    .toList();
        }

        return SktGuardRailUpdateReq.builder()
                .name(request.getName())
                .description(request.getDescription())
                .projectId(request.getProjectId())
                .promptId(request.getPromptId())
                .llms(convertedLlms)           // 타입 변환된 LLM 목록
                .tags(List.of())               // 항상 빈 배열
                .build();
    }

}
