package com.skax.aiplatform.dto.prompt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 특정 버전의 가드레일 프롬프트 메시지 목록 응답 DTO (백엔드 응답에 맞춘 화면용 구조)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가드레일프롬프트 메시지 목록 응답")
public class GuardRailPromptMsgsByIdRes {

    @Schema(description = "버전 UUID")
    private String versionUuid;

    @Schema(description = "메시지 목록")
    private List<MessageItem> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "메시지 정보 아이템")
    public static class MessageItem {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "메시지 ID (서버 미제공 시 versionUuid:sequence 형태로 합성)")
        private String messageId;

        @Schema(description = "메시지 타입 (0:text, 1:system, 2:user, 3:assistant)")
        private Integer mtype;

        @Schema(description = "메시지 내용")
        private String message;

        @Schema(description = "메시지 순서")
        private Integer order;
    }
}

