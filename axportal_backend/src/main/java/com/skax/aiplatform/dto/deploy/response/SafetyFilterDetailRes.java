package com.skax.aiplatform.dto.deploy.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupAggregate;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 세이프티 필터 상세 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 상세 조회 응답")
public class SafetyFilterDetailRes {

    @Schema(description = "필터 그룹 아이디")
    private String filterGroupId;

    @Schema(description = "필터 그룹명", example = "욕설")
    private String filterGroupName;

    @Schema(description = "불용어 목록")
    private List<StopWordData> stopWords;

    @Schema(description = "프로젝트명")
    private String projectName;

    @Schema(description = "공개 에셋 여부")
    @JsonProperty("isPublicAsset")
    private boolean isPublicAsset;

    @Schema(description = "생성일시", example = "2025-10-17 14:45:07")
    private String createdAt;

    @Schema(description = "수정일시", example = "2025-10-17 14:45:07")
    private String updatedAt;

    @Schema(description = "최초 생성자 정보")
    private AuditorInfo createdBy;

    @Schema(description = "최종 수정자 정보")
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

        @Schema(description = "부서명", example = "AI플랫폼셀")
        private final String deptNm;

        public static AuditorInfo from(GpoUsersMas user) {
            return AuditorInfo.builder()
                    .jkwNm(user.getJkwNm())
                    .deptNm(user.getDeptNm())
                    .build();
        }

    }

    /**
     * 불용어 정보 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "불용어 정보")
    public static class StopWordData {

        @Schema(description = "불용어 ID", example = "b5fea912-2fcb-4ee0-8bae-3aa0c42935d0")
        private String id;

        @Schema(description = "불용어", example = "단어1")
        private String stopWord;

    }

    /**
     * SafetyFilterGroupAggregate와 사용자 정보로부터 SafetyFilterDetailRes 생성
     *
     * @param aggregate       SKT AI 응답 데이터
     * @param createdByUser   생성자 정보
     * @param updatedByUser   수정자 정보
     * @param assetInfo       에셋 매핑 정보
     * @param publicAssetUser 권한 수정자 정보
     * @return SafetyFilterDetailRes
     */
    public static SafetyFilterDetailRes of(
            SktSafetyFilterGroupAggregate aggregate,
            String projectName,
            GpoUsersMas createdByUser,
            GpoUsersMas updatedByUser,
            GpoAssetPrjMapMas assetInfo,
            GpoUsersMas publicAssetUser
    ) {
        AuditorInfo createdByInfo = createdByUser != null ? AuditorInfo.from(createdByUser) : null;
        AuditorInfo updatedByInfo = updatedByUser != null ? AuditorInfo.from(updatedByUser) : null;
        AuditorInfo publicAssetUpdatedByInfo = publicAssetUser != null ? AuditorInfo.from(publicAssetUser) : null;

        // 공개 에셋 여부 판단: lst_prj_seq = -999 이면 공개, 아니면 비공개
        boolean isPublicAsset =
                assetInfo != null && assetInfo.getLstPrjSeq() != null && assetInfo.getLstPrjSeq() == -999;

        return SafetyFilterDetailRes.builder()
                .filterGroupId(aggregate.getGroupId())
                .filterGroupName(aggregate.getGroupName())
                .stopWords(convertStopWords(aggregate.getStopWords()))
                .projectName(projectName)
                .isPublicAsset(isPublicAsset)
                .createdAt(DateUtils.toDateTimeString(aggregate.getCreatedAt()))
                .updatedAt(DateUtils.toDateTimeString(aggregate.getUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
                .publicAssetUpdatedBy(publicAssetUpdatedByInfo)
                .build();
    }

    private static List<StopWordData> convertStopWords(List<SktSafetyFilterGroupAggregate.StopWordData> stopWords) {
        if (stopWords == null) {
            return List.of();
        }

        return stopWords.stream()
                .map(stopWord -> StopWordData.builder()
                        .id(stopWord.getId())
                        .stopWord(stopWord.getStopWord())
                        .build())
                .toList();
    }

}
