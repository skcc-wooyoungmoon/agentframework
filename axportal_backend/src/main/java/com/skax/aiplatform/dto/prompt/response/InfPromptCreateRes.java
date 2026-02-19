package com.skax.aiplatform.dto.prompt.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론프롬프트 생성 응답")
public class InfPromptCreateRes {

    @Schema(description = "생성된 추론프롬프트 UUID", example = "00000000-0000-0000-0000-000000000000")
    private String promptUuid;
}
