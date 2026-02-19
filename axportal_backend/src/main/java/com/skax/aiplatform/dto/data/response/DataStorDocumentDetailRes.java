package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 데이터 저장소 문서 상세 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "데이터 저장소 문서 상세 조회 응답")
public class DataStorDocumentDetailRes {

    @Schema(description = "데이터셋코드", example = "rgl")
    private String datasetCd;

    @Schema(description = "데이터셋이름", example = "규정")
    private String datasetName;

    @Schema(description = "문서UUID", example = "doc03_sbrgl20060402003600000009")
    private String docUuid;

    @Schema(description = "문서제목", example = "신한은행 자금이체약정서")
    private String docTitle;

    @Schema(description = "문서키워드", example = "키워드1")
    private String docKeyword;

    @Schema(description = "문서요약", example = "신한은행 자금이체약정서의 2025년 최신 개정판으로 업무에 참고하세요.")
    private String docSummary;

    @Schema(description = "생성일", example = "20191228")
    private String createDate;

    @Schema(description = "최종수정일", example = "20191228")
    private String lastModDate;

    @Schema(description = "MD형식 문서 다운로드경로", example = "/sb/rgl/03/doc03_sbrgl20060402003600000009/v1/01/anony_doc03_sbrgl20060402003600000009.md")
    private String docPathAnonyMd;

    @Schema(description = "첨부문서UUID 목록")
    private List<String> attachDocUuids;

    @Schema(description = "첨부부모문서UUID", example = "")
    private String attachParentDocUuid;

    @Schema(description = "문서 키워드 배열")
    private List<String> docArrayKeywords;

    @Schema(description = "원천메타데이터")
    private Map<String, Object> originMetadata;
}