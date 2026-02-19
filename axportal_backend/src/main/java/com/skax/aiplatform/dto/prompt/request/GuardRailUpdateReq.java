package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 가드레일 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 수정 요청")
public class GuardRailUpdateReq {

    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @Schema(description = "가드레일 이름", example = "Guardrails Example Updated")
    private String name;

    @Schema(description = "가드레일 설명", example = "Guardrails Description Updated")
    private String description;

    @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be")
    private String promptId;

    @Schema(description = "LLM 목록")
    private List<GuardRailLlm> llms;

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

        @Schema(description = "태그명", example = "example")
        private String tag;

    }

}

