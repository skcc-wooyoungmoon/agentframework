package com.skax.aiplatform.dto.prompt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 특정 가드레일프롬프트(UUID)의 최신 버전(단건) 응답 DTO (도메인용)
 *
 * - Upstream(PromptVersionResponse)의 data 필드 구조를 평탄화한 뷰
 * - messages/variables/tags 는 이 엔드포인트에서 내려오지 않으므로 기본 빈 리스트 유지
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가드레일프롬프트 최신 버전 단건 응답")
public class GuardRailPromptLatestByIdRes {

    @Schema(description = "프롬프트 UUID", example = "4e806085-b2f7-4f0f-9b3a-5e777c199b01")
    private String promptUuid;

    @Schema(description = "버전 UUID", example = "76788be4-8bbf-490f-a324-fb0721b78af3")
    private String versionUuid;

    @Schema(description = "버전 번호", example = "5")
    private Integer version;

    @Schema(description = "버전 생성 시간 (UTC ISO-8601)", example = "2025-08-18T01:41:38.318862Z")
    private String createdAt;

    @Schema(description = "릴리즈(배포) 여부", example = "true")
    private Boolean release;

    @Schema(description = "삭제 여부", example = "false")
    private Boolean deleteFlag;

    @Schema(description = "생성자 사용자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
    private String createdBy;
}

