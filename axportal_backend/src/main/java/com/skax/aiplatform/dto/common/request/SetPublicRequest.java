package com.skax.aiplatform.dto.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 자산 공개 설정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자산 공개 설정 요청")
public class SetPublicRequest {
    
    @NotBlank(message = "자산 타입은 필수입니다")
    @Schema(description = "자산 타입 (agent, app, few-shot, tool, mcp)", example = "few-shot", required = true)
    private String type;
    
    @NotBlank(message = "자산 ID는 필수입니다")
    @Schema(description = "자산 ID (UUID)", example = "bd7bf6ca-b12d-4643-a1f3-4ce7387cc60e", required = true)
    private String id;
}

