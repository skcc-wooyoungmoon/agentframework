package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 특정 워크플로우(UUID)의 버전 목록 응답 DTO (도메인용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "워크플로우 버전 목록 응답")
public class WorkFlowVerListByIdRes {
    @Schema(description = "워크플로우 UUID", example = "4e806085-b2f7-4f0f-9b3a-5e777c199b01")
    private String workFlowId;

    @Schema(description = "총 버전 개수", example = "5")
    @Builder.Default
    private Integer totalVersions = 0;

    @Schema(description = "버전 목록")
    @Builder.Default
    private List<WorkFlowVerListByIdRes.VersionItem> versions = List.of();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "버전 아이템")
    public static class VersionItem {

        @Schema(description = "버전 번호", example = "5")
        private Integer versionNo;

        @Schema(description = "생성 일시", example = "2025-08-18T01:41:38.318862Z")
        private String createdAt;

        @Schema(description = "수정 일시", example = "2025-08-18T01:41:38.318862Z")
        private String updatedAt;
    }
}
