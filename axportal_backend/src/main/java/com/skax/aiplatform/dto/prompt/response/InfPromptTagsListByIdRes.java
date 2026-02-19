package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 특정 버전의 프롬프트 태그 목록 응답 DTO (현행화)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론프롬프트 태그 목록 응답")
public class InfPromptTagsListByIdRes {

    @Schema(description = "버전 UUID (서버: version_id)")
    private String versionUuid;

    @Schema(description = "태그 목록")
    private List<PromptTag> tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "태그 정보 아이템")
    public static class PromptTag {

        @Schema(description = "태그 UUID (서버: tag_uuid)")
        private String tagId;

        @Schema(description = "태그명 (서버: tag)")
        private String tag;
    }
}