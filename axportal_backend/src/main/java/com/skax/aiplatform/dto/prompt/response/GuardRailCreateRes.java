package com.skax.aiplatform.dto.prompt.response;

import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailCreateRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가드레일 생성 응답 DTO
 *
 * @author sonmunwoo
 * @version 1.0.0
 * @since 2025-10-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 생성 응답")
public class GuardRailCreateRes {

    @Schema(description = "생성된 가드레일 ID", example = "08dbdf23-ebe0-4156-953c-8b3ca62f7881")
    private String guardrailsId;

    /**
     * SktGuardRailCreateRes를 GuardRailCreateRes로 변환하는 정적 팩토리 메서드
     *
     * @param response SKT AI 가드레일 생성 응답
     * @return GuardRailCreateRes
     */
    public static GuardRailCreateRes from(SktGuardRailCreateRes response) {
        return GuardRailCreateRes.builder()
            .guardrailsId(response.getData().getGuardrailsId())
            .build();
    }
}
