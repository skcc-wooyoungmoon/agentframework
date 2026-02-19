package com.skax.aiplatform.dto.prompt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailDetailRes;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.GpoUsersMas;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * GuardRail 상세 조회 응답 DTO
 *
 * @author 권두현
 * @version 1.0
 * @since 2025-11-19
 */
@Getter
@Builder
@Schema(description = "가드레일 상세 조회 응답")
public class GuardRailDetailRes {

    @Schema(description = "가드레일 UUID", example = "08dbdf23-ebe0-4156-953c-8b3ca62f7881")
    private String uuid;

    @Schema(description = "가드레일 이름", example = "Guardrails Example")
    private String name;

    @Schema(description = "가드레일 설명", example = "This is a Guardrails Example.")
    private String description;

    @Schema(description = "프롬프트 ID", example = "b7fbfa45-f6b2-4793-9c42-f1b9a0d069be")
    private String promptId;

    @Schema(description = "LLM 목록")
    private List<GuardRailLlm> llms;

    @Schema(description = "프로젝트명")
    private String projectName;

    @Schema(description = "공개 에셋 여부")
    @JsonProperty("isPublicAsset")
    private boolean isPublicAsset;

    @Schema(description = "생성일시")
    private String createdAt;

    @Schema(description = "수정일시")
    private String updatedAt;

    @Schema(description = "생성자 정보")
    private AuditorInfo createdBy;

    @Schema(description = "수정자 정보")
    private AuditorInfo updatedBy;

    @Schema(description = "권한 수정자 정보")
    private AuditorInfo publicAssetUpdatedBy;

    /**
     * 감시자 정보 내부 클래스
     */
    @Getter
    @Builder
    public static class AuditorInfo {

        @Schema(description = "이름", example = "김신한")
        private final String jkwNm;

        @Schema(description = "부서명", example = "AI 플랫폼")
        private final String deptNm;

        public static AuditorInfo from(GpoUsersMas user) {
            return AuditorInfo.builder()
                    .jkwNm(user.getJkwNm())
                    .deptNm(user.getDeptNm())
                    .build();
        }

    }

    /**
     * 가드레일 LLM 정보
     */
    @Getter
    @Builder
    @Schema(description = "가드레일 LLM 정보")
    public static class GuardRailLlm {

        @Schema(description = "서빙 이름", example = "GIP/ax4")
        private String servingName;

    }

    /**
     * SktGuardRailDetailRes를 GuardRailDetailRes로 변환하는 정적 팩토리 메서드
     *
     * @param sktResponse                SKT AI 가드레일 상세 응답
     * @param projectName                프로젝트명
     * @param isPublicAsset              퍼블릭 에셋 여부
     * @param createdUserInfo            생성자 정보
     * @param updatedUserInfo            수정자 정보
     * @param publicAssetUpdatedUserInfo 에셋 수정자 정보
     */
    public static GuardRailDetailRes of(
            SktGuardRailDetailRes sktResponse,
            String projectName,
            boolean isPublicAsset,
            GpoUsersMas createdUserInfo,
            GpoUsersMas updatedUserInfo,
            GpoUsersMas publicAssetUpdatedUserInfo
    ) {
        if (sktResponse == null || sktResponse.getData() == null) {
            return null;
        }

        SktGuardRailDetailRes.GuardRailDetailData sktData = sktResponse.getData();

        // 감시자 정보 변환
        AuditorInfo createdByInfo = createdUserInfo != null ? AuditorInfo.from(createdUserInfo) : null;
        AuditorInfo updatedByInfo = updatedUserInfo != null ? AuditorInfo.from(updatedUserInfo) : null;
        AuditorInfo assetUpdatedByInfo = publicAssetUpdatedUserInfo != null
                ? AuditorInfo.from(publicAssetUpdatedUserInfo) : null;

        // LLMs 리스트 변환
        List<GuardRailLlm> llms = null;
        if (sktData.getLlms() != null) {
            llms = sktData.getLlms().stream()
                    .map(llm -> GuardRailLlm.builder()
                            .servingName(llm.getServingName())
                            .build())
                    .toList();
        }

        return GuardRailDetailRes.builder()
                .uuid(sktData.getUuid())
                .name(sktData.getName())
                .description(sktData.getDescription())
                .promptId(sktData.getPromptId())
                .llms(llms)
                .projectName(projectName)
                .isPublicAsset(isPublicAsset)
                .createdAt(DateUtils.utcToKstDateTimeString(sktData.getCreatedAt()))
                .updatedAt(DateUtils.utcToKstDateTimeString(sktData.getUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
                .publicAssetUpdatedBy(assetUpdatedByInfo)
                .build();
    }

}
