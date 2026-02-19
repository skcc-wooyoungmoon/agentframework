package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Prompt 생성 결과 데이터 (data 섹션)
 *
 * <p>PromptCreateResponse의 data 필드를 별도의 클래스로 분리한 타입입니다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Prompt 생성 결과 데이터")
public class PromptCreateBody {

    @JsonProperty("prompt_uuid")
    @Schema(description = "생성된 프롬프트 UUID", example = "4ff2dab7-bffe-414d-88a5-1826b9fea8df", format = "uuid")
    private String promptUuid;
}
