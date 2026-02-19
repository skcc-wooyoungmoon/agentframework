package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가드레일 프롬프트 기본 정보 (목록 조회용)")
public class GuardRailPromptRes {

    @Schema(description = "프롬프트 UUID", example = "prompt-uuid-123")
    private String uuid;

    @Schema(description = "프롬프트 이름", example = "Customer Support Assistant")
    private String name;

    @Schema(description = "릴리즈 버전", example = "1")
    private String releaseVersion;

    @Schema(description = "현재 버전", example = "1")
    private String latestVersion;

    @Schema(description = "ptype", example = "1")
    private String ptype;

    @Schema(description = "태그 목록")
    @Builder.Default
    private List<TagInfo> tags = List.of();

    @Schema(description = "연결된 에이전트 수")
    private Integer connectedAgentCount;

    @Schema(description = "생성 시간", example = "2025-08-15 10:30:0Z")
    private String createdAt;

    @Schema(description = "최종 수정 시간", example = "2025-08-16 12:15:00Z")
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "태그 정보")
    public static class TagInfo {

        @Schema(description = "태그명")
        private String tag;

        @Schema(description = "버전 ID (프롬프트 목록 조회 시에만 포함)")
        private String versionId;

    }

}

