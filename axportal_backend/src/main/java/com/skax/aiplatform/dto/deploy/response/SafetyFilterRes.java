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
 * 세이프티 필터 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "세이프티 필터 응답")
public class SafetyFilterRes {

    @Schema(description = "필터 그룹 ID", example = "6d61c202-b539-4401-a9ce-a916c6eabcde")
    private String filterGroupId;

    @Schema(description = "필터 그룹명", example = "욕설")
    private String filterGroupName;

    @Schema(description = "불용어 목록")
    private List<StopWordData> stopWords;

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

        public static AuditorInfo from(GpoUsersMas createdByUser) {
            return AuditorInfo.builder()
                    .jkwNm(createdByUser.getJkwNm())
                    .deptNm(createdByUser.getDeptNm())
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
     * SafetyFilterGroupAggregate와 사용자 정보, 에셋 정보로부터 SafetyFilterRes 생성
     *
     * @param aggregate     SKT AI 응답 데이터
     * @param createdByUser 생성자 정보
     * @param updatedByUser 수정자 정보
     * @param assetInfo     에셋 매핑 정보 (공개 여부 판단용)
     * @return SafetyFilterRes
     */
    public static SafetyFilterRes of(
            SktSafetyFilterGroupAggregate aggregate,
            GpoUsersMas createdByUser,
            GpoUsersMas updatedByUser,
            GpoAssetPrjMapMas assetInfo
    ) {
        AuditorInfo createdByInfo = createdByUser != null ? AuditorInfo.from(createdByUser) : null;
        AuditorInfo updatedByInfo = updatedByUser != null ? AuditorInfo.from(updatedByUser) : null;

        // 공개 에셋 여부 판단: lst_prj_seq = -999 이면 공개, 아니면 비공개
        boolean isPublicAsset =
                assetInfo != null && assetInfo.getLstPrjSeq() != null && assetInfo.getLstPrjSeq() == -999L;

        return SafetyFilterRes.builder()
                .filterGroupId(aggregate.getGroupId())
                .filterGroupName(aggregate.getGroupName())
                .stopWords(convertStopWords(aggregate.getStopWords()))
                .isPublicAsset(isPublicAsset)
                .createdAt(DateUtils.toDateTimeString(aggregate.getCreatedAt()))
                .updatedAt(DateUtils.toDateTimeString(aggregate.getUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
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
