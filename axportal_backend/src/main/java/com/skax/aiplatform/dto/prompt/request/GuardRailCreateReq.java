package com.skax.aiplatform.dto.prompt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 가드레일 생성 요청 DTO
 *
 * @author sonmunwoo
 * @version 1.0.0
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 생성 요청")
public class GuardRailCreateReq {

    @NotBlank(message = "프로젝트 ID는 필수입니다")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @NotBlank(message = "가드레일 이름은 필수입니다")
    @Schema(description = "가드레일 이름", example = "개인정보 보호 가드레일")
    private String name;

    @Schema(description = "가드레일 설명", example = "개인정보 및 민감정보를 검증하는 가드레일")
    private String description;

    @NotBlank(message = "프롬프트 ID는 필수입니다")
    @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be")
    private String promptId;

    @NotEmpty(message = "LLM 목록은 필수입니다")
    @Schema(description = "LLM 목록")
    private List<GuardRailLlm> llms;

    @Schema(description = "태그 목록")
    private List<GuardRailTag> tags;

    /**
     * 가드레일 LLM 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "가드레일 LLM 정보")
    public static class GuardRailLlm {

        @NotBlank(message = "서빙 ID는 필수입니다")
        @Schema(description = "서빙 ID", example = "6d4b5a39-a1c3-48e3-ad12-abfb0e483da1")
        private String servingId;

        @NotBlank(message = "서빙 이름은 필수입니다")
        @Schema(description = "서빙 이름", example = "GIP/ax4")
        private String servingName;

    }

    /**
     * 가드레일 태그 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "가드레일 태그 정보")
    public static class GuardRailTag {

        @NotBlank(message = "태그명은 필수입니다")
        @Schema(description = "태그명", example = "security")
        private String tag;

    }

}
