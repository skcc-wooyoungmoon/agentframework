package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모델 플레이그라운드 채팅 응답 DTO
 * 
 * <p>
 * 플레이그라운드에서 AI 모델과의 채팅 결과를 담는 응답 DTO입니다.
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
@Schema(description = "모델 플레이그라운드 채팅 응답")
public class ModelPlaygroundChatRes {

    @Schema(description = "응답 ID", example = "chatcmpl-1234567890")
    private String id;

    @Schema(description = "사용된 모델 ID", example = "gpt-4o")
    private String model;

    @Schema(description = "생성된 메시지 목록")
    private List<ChatMessage> choices;

    @Schema(description = "사용된 토큰 정보")
    private TokenUsage usage;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "스트리밍 여부")
    private Boolean stream;

    @Schema(description = "에러 메시지", example = "요청 시간이 초과되었습니다.")
    private String error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "채팅 메시지")
    public static class ChatMessage {

        @Schema(description = "메시지 인덱스", example = "0")
        private Integer index;

        @Schema(description = "메시지 내용")
        private MessageContent message;

        @Schema(description = "완료 이유", example = "stop")
        private String finishReason;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "메시지 내용")
        public static class MessageContent {

            @Schema(description = "역할", example = "assistant")
            private String role;

            @Schema(description = "메시지 텍스트", example = "안녕하세요! 오늘은 맑은 날씨입니다.")
            private String content;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "토큰 사용량")
    public static class TokenUsage {

        @Schema(description = "프롬프트 토큰 수", example = "10")
        private Integer promptTokens;

        @Schema(description = "완성 토큰 수", example = "20")
        private Integer completionTokens;

        @Schema(description = "총 토큰 수", example = "30")
        private Integer totalTokens;
    }
}

