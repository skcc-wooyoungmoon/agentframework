package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 특정 가드레일프롬프트(UUID)의 버전 목록 응답 DTO (도메인용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가드레일프롬프트 버전 목록 응답")
public class GuardRailPromptVerListByIdRes {

    @Schema(description = "프롬프트 UUID", example = "4e806085-b2f7-4f0f-9b3a-5e777c199b01")
    private String promptUuid;

    @Schema(description = "총 버전 개수", example = "5")
    @Builder.Default
    private Integer totalVersions = 0;

    @Schema(description = "버전 목록")
    @Builder.Default
    private List<VersionItem> versions = List.of();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "버전 아이템")
    public static class VersionItem {

        @Schema(description = "버전 UUID", example = "76788be4-8bbf-490f-a324-fb0721b78af3")
        private String versionUuid;

        @Schema(description = "버전 번호", example = "5")
        private Integer version;

        @Schema(description = "생성 일시", example = "2025-08-18T01:41:38.318862Z")
        private String createdAt;

        @Schema(description = "릴리즈(배포) 여부", example = "true")
        private Boolean release;

        @Schema(description = "삭제 여부", example = "false")
        private Boolean deleteFlag;

        @Schema(description = "생성자 사용자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String createdBy;
    }
}

