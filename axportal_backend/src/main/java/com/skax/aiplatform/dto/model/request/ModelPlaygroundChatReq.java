package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 플레이그라운드 채팅 요청 DTO
 * 
 * <p>
 * 플레이그라운드에서 AI 모델과의 채팅을 위한 요청 데이터를 담는 DTO입니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모델 플레이그라운드 채팅 요청")
public class ModelPlaygroundChatReq {

    @Schema(description = "사용할 모델", example = "gpt-4o")
    private String model;

    @Schema(description = "모델 serving id")
    private String servingId;

    @Schema(description = "시스템 프롬프트", example = "당신은 도움이 되는 AI 어시스턴트입니다.")
    private String systemPrompt;

    @Schema(description = "사용자 프롬프트", example = "안녕하세요! 오늘 날씨는 어떤가요?")
    private String userPrompt;

    @Schema(description = "최대 토큰 수", example = "1000")
    private Integer maxTokens;

    @Schema(description = "온도 (0.0 ~ 2.0)", example = "0.7")
    private Double temperature;

    @Schema(description = "Top-P 샘플링 (0.0 ~ 1.0)", example = "0.9")
    private Double topP;

    @Schema(description = "프리퀀시 페널티", example = "0.0")
    private Double frequencyPenalty;

    @Schema(description = "프리젠스 페널티", example = "0.0")
    private Double presencePenalty;

    @Schema(description = "스트리밍 여부", example = "false")
    private Boolean stream;

    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;
}
