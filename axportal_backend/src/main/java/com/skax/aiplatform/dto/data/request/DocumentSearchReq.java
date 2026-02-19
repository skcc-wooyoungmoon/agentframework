package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 문서 검색 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문서 검색 요청")
public class DocumentSearchReq {
    
    /**
     * 데이터셋 코드 (필수)
     */
    @Schema(description = "데이터셋 코드 (필수)", example = "rgl", required = true)
    private String datasetCd;
    
    /**
     * 문서 수정 시작일 (YYYYMMDD)
     */
    @Schema(description = "문서 수정 시작일 (YYYYMMDD)", example = "20250101")
    private String docModStart;
    
    /**
     * 문서 수정 종료일 (YYYYMMDD)
     */
    @Schema(description = "문서 수정 종료일 (YYYYMMDD)", example = "20251231")
    private String docModEnd;
    
    /**
     * 원본 메타데이터 포함 여부 (Y/N)
     */
    @Schema(description = "원본 메타데이터 포함 여부 (Y/N)", example = "N")
    private String originMetadataYn;
    
    /**
     * 검색어
     */
    @Schema(description = "검색어", example = "규정")
    private String searchWord;
    
    /**
     * 문서 UUID
     */
    @Schema(description = "문서 UUID", example = "DOC-RGL-00001")
    private String docUuid;
    
    /**
     * 페이지당 개수
     */
    @Schema(description = "페이지당 개수", example = "5")
    @Builder.Default
    private Long countPerPage = 5L;
    
    /**
     * 페이지 번호 (1부터 시작)
     */
    @Schema(description = "페이지 번호", example = "1")
    @Builder.Default
    private Long page = 1L;
}

