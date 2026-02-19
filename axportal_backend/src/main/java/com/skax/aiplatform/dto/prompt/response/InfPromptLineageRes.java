package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추론 프롬프트와 연결된 프롬프트 응답 DTO
 *
 * <p>추론 프롬프트와 연결된 프롬프트 정보를 클라이언트에 반환할 때 사용됩니다.</p>
 *
 * @author 권두현
 * @since 2025-11-02
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "추론 프롬프트와 연결된 프롬프트 응답")
public class InfPromptLineageRes {

    @Schema(description = "프롬프트 ID", example = "prompt-uuid-123")
    private String id;

    @Schema(description = "프롬프트 이름", example = "System Prompt")
    private String name;

    @Schema(description = "프롬프트 설명", example = "System prompt for customer support")
    private String description;

    @Schema(description = "배포 여부", example = "true")
    private Boolean deployed;

    @Schema(description = "생성일시", example = "2025-10-15 10:30:00")
    private String createdAt;

    @Schema(description = "최종 수정일시", example = "2025-10-23 14:45:30")
    private String updatedAt;

}
