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
@Schema(description = "추론프롬프트 전체 태그 목록 응답")
public class InfPromptTagsList {
    @Schema(description = "전체 태그 목록")
    private List<String> tags;

    @Schema(description = "총 태그 개수")
    private Integer total;

    public static InfPromptTagsList of(List<String> tags) {
        List<String> safeTags = tags == null ? List.of() : List.copyOf(tags);
        return InfPromptTagsList.builder()
                .tags(safeTags)
                .total(safeTags.size())
                .build();
    }
}
