package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 문서 검색 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문서 검색 응답")
public class DocumentSearchRes {

    /**
     * 총 개수
     */
    @JsonProperty("totalCount")
    @Schema(description = "총 개수", example = "18")
    private Long totalCount;

    /**
     * 현재 페이지
     */
    @JsonProperty("page")
    @Schema(description = "현재 페이지", example = "1")
    private Long page;

    /**
     * 문서 목록
     */
    @JsonProperty("items")
    @Schema(description = "문서 목록")
    private List<DocumentItem> items;

    /**
     * 문서 항목
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "문서 항목")
    public static class DocumentItem {

        @JsonProperty("datasetCd")
        @Schema(description = "데이터셋 코드")
        private String datasetCd;

        @JsonProperty("datasetName")
        @Schema(description = "데이터셋명")
        private String datasetName;

        @JsonProperty("docUuid")
        @Schema(description = "문서 UUID")
        private String docUuid;

        @JsonProperty("docTitle")
        @Schema(description = "문서 제목")
        private String docTitle;

        @JsonProperty("docKeyword")
        @Schema(description = "문서 키워드")
        private String docKeyword;

        @JsonProperty("docSummary")
        @Schema(description = "문서 요약")
        private String docSummary;

        @JsonProperty("createDate")
        @Schema(description = "생성일")
        private String createDate;

        @JsonProperty("lastModDate")
        @Schema(description = "최종 수정일")
        private String lastModDate;

        @JsonProperty("docPathAnonymMd")
        @Schema(description = "문서 경로(익명화)")
        private String docPathAnonymMd;

        @JsonProperty("attachDocUuids")
        @Schema(description = "첨부 문서 UUID 목록")
        private List<String> attachDocUuids;

        @JsonProperty("attachParentDocUuid")
        @Schema(description = "첨부 부모 문서 UUID")
        private String attachParentDocUuid;

        @JsonProperty("originMetadata")
        @Schema(description = "원본 메타데이터")
        private Map<String, Object> originMetadata;
    }
}
