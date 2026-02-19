package com.skax.aiplatform.dto.deploy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 세이프티 필터 생성 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 생성 응답")
public class SafetyFilterCreateRes {

    @Schema(description = "생성된 세이프티 필터 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String filterGroupId;

    @Schema(description = "분류", example = "욕설")
    private String filterGroupName;

    @Schema(description = "금지어 목록", example = "[\"비속어1\", \"비속어2\", \"비속어3\"]")
    private List<String> stopWords;

    public static SafetyFilterCreateRes of(String filterGroupId, String filterGroupName, List<String> stopWords) {
        return SafetyFilterCreateRes.builder()
                .filterGroupId(filterGroupId)
                .filterGroupName(filterGroupName)
                .stopWords(stopWords)
                .build();
    }

}

