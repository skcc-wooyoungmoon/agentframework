package com.skax.aiplatform.dto.prompt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailsRes;
import com.skax.aiplatform.common.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 가드레일 응답 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-10-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 응답")
public class GuardRailRes {

    @Schema(description = "가드레일 ID", example = "08dbdf23-ebe0-4156-953c-8b3ca62f7881")
    private String uuid;

    @Schema(description = "가드레일 이름", example = "Guardrails Example")
    private String name;

    @Schema(description = "가드레일 설명", example = "This is a Guardrails Example.")
    private String description;

    @Schema(description = "공개 에셋 여부")
    @JsonProperty("isPublicAsset")
    private boolean isPublicAsset;

    @Schema(description = "생성일시")
    private String createdAt;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "수정일시")
    private String updatedAt;

    @Schema(description = "수정자", example = "editor")
    private String updatedBy;

    /**
     * SktGuardRailsRes.GuardRailData를 GuardRailRes로 변환하는 정적 팩토리 메서드
     *
     * @param data SKT AI 가드레일 데이터
     * @return GuardRailRes
     */
    public static GuardRailRes of(SktGuardRailsRes.GuardRailData data, boolean isPublicAsset) {
        return GuardRailRes.builder()
                .uuid(data.getUuid())
                .name(data.getName())
                .description(data.getDescription())
                .isPublicAsset(isPublicAsset)
                .createdAt(DateUtils.utcToKstDateTimeString(data.getCreatedAt()))
                .createdBy(data.getCreatedBy())
                .updatedAt(DateUtils.utcToKstDateTimeString(data.getUpdatedAt()))
                .updatedBy(data.getUpdatedBy())
                .build();
    }

}
