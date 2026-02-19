package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터 저장소 문서(document) 검색 요청 DTO
 *
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터 저장소 문서 검색 요청")
public class DataStorDocumentSearchReq {

    /**
     * 데이터셋코드 (필수)
     */
    @NotNull(message = "데이터셋코드는 필수입니다")
    @Schema(description = "데이터셋코드 (데이터셋조회 API로 획득, 2자)", example = "rgl", required = true)
    private String datasetCd;

    /**
     * 파일명 (선택)
     */
    @Schema(description = "검색어 (2자 이상)", example = "규정")
    private String searchWord;

    /**
     * uuid (선택)
     */
    @Schema(description = "uuid", example = "doc03_sbrgl20060402003600000009")
    private String uuid;

    /**
     * 페이지당표시수 (선택)
     */
    @Schema(description = "페이지당표시수 (기본값: 20, 최대값: 100)", example = "20")
    @Builder.Default
    private Long countPerPage = 20L;

    /**
     * 페이지 (선택)
     */
    @Schema(description = "페이지 (기본값: 1)", example = "1")
    @Builder.Default
    private Long page = 1L;
}