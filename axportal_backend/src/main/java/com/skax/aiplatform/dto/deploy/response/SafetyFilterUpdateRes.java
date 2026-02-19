package com.skax.aiplatform.dto.deploy.response;

import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupStopWordUpdateRes;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.GpoUsersMas;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Safety Filter 수정 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safety Filter 수정 응답")
public class SafetyFilterUpdateRes {

    @Schema(description = "생성된 세이프티 필터 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String filterGroupId;

    @Schema(description = "분류", example = "욕설")
    private String filterGroupName;

    @Schema(description = "생성된 키워드 수", example = "3")
    private Integer createdCount;

    @Schema(description = "삭제된 키워드 수", example = "2")
    private Integer deletedCount;

    @Schema(description = "최종 키워드 총 개수", example = "5")
    private Integer totalCount;

    @Schema(description = "금지어 목록", example = "[\"비속어1\", \"비속어2\", \"비속어3\"]")
    private List<String> stopWords;

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

    public static SafetyFilterUpdateRes of(
            SktSafetyFilterGroupStopWordUpdateRes res,
            GpoUsersMas createdByUser,
            GpoUsersMas updatedByUser
    ) {
        AuditorInfo createdByInfo = createdByUser != null ? AuditorInfo.from(createdByUser) : null;
        AuditorInfo updatedByInfo = updatedByUser != null ? AuditorInfo.from(updatedByUser) : null;

        return SafetyFilterUpdateRes.builder()
                .filterGroupId(res.getGroupId())
                .filterGroupName(res.getGroupName())
                .createdCount(res.getCreatedCount())
                .deletedCount(res.getDeletedCount())
                .totalCount(res.getTotalCount())
                .stopWords(res.getStopwords())
                .createdAt(DateUtils.toDateTimeString(res.getCreatedAt()))
                .updatedAt(DateUtils.toDateTimeString(res.getUpdatedAt()))
                .createdBy(createdByInfo)
                .updatedBy(updatedByInfo)
                .build();
    }

}
