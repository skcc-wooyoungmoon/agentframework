package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 데이터 저장소 문서 검색 응답 DTO (프론트용)
 * 
 * <p>
 * 프론트엔드로 반환되는 문서 정보 데이터 구조입니다.
 * camelCase 네이밍을 사용합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터 저장소 문서 검색 응답")
public class DataStorDocumentRes {

    /**
     * 데이터셋코드
     */
    @Schema(description = "데이터셋코드", example = "rgl")
    private String datasetCd;

    /**
     * 데이터셋이름
     */
    @Schema(description = "데이터셋이름", example = "규정")
    private String datasetName;

    /**
     * 문서UUID
     */
    @Schema(description = "문서UUID", example = "doc00_sbrgl20160411680300000002")
    private String docUuid;

    /**
     * 문서제목 (origin_metadata.title에서 추출)
     */
    @Schema(description = "문서제목", example = "자산구성형 개인종합자산관리계약 업무규정")
    private String docTitle;

    /**
     * 문서요약
     */
    @Schema(description = "문서 요약")
    private String docSummary;

    /**
     * 생성일
     */
    // @Schema(description = "문서 생성일", example = "20191228")
    // private Long createDate;

    // /**
    // * 최종수정일
    // */
    // @Schema(description = "문서 최종 수정일", example = "20191228")
    // private Long lastModDate;

    /**
     * 생성일
     */
    @Schema(description = "문서 생성일", example = "20191228")
    private String docCreateDay;

    /**
     * 최종수정일
     */
    @Schema(description = "문서 최종 수정일", example = "20191228")
    private String docMdfcnDay;
    /**
     * 문서 경로 (익명화)
     */
    @Schema(description = "익명화된 문서 경로")
    private String docPathAnonyMd;

    /**
     * 첨부 부모문서UUID
     */
    @Schema(description = "첨부 부모문서 UUID")
    private String attachParentDocUuid;

    /**
     * 문서 배열 키워드
     */
    @Schema(description = "문서 키워드 목록")
    private List<String> docArrayKeywords;

    /**
     * 원천메타데이터
     */
    @Schema(description = "수집 원천에서 추출된 메타데이터 (title 필드 포함)")
    private Map<String, Object> originMetadata;
}