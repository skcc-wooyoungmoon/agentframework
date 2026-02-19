package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author gyuHeeHwang
 * @since 2025-09-19
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Script Test 응답 정보"
)
public class ScriptTestResponse {
    @JsonProperty("document_list")
    @Schema(description = "document list")
    private List<PreviewDocument> documentList;

    @JsonProperty("document_count")
    @Schema(description = "document count")
    private Integer documentCount;

    @JsonProperty("result_message")
    @Schema(description = "result message")
    private String resultMessage;

    
    /**
     * Preview Document 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Preview Document 정보")
    public static class PreviewDocument {
        @JsonProperty("page_content")
        @Schema(description = "page content")
        private String pageContent;

        @JsonProperty("metadata")
        @Schema(description = "metadata")
        private Metadata metadata;

        @JsonProperty("endpoint")
        @Schema(description = "endpoint")
        private String endpoint;

        @JsonProperty("key")
        @Schema(description = "key")
        private String key;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Preview Document 정보")
    public static class Metadata {
        @JsonProperty("CATE_NM")
        @Schema(description = "cateNm")
        private String cateNm;

        @JsonProperty("CATE_SEQ")
        @Schema(description = "cateSeq")
        private Integer cateSeq;

        @JsonProperty("CATE_NM_1")
        @Schema(description = "cateNm1")
        private String cateNm1;

        @JsonProperty("file_name")
        @Schema(description = "file Name")
        private String fileName;

        @JsonProperty("doc_type")
        @Schema(description = "docType")
        private String docType;

        @JsonProperty("timestamp")
        @Schema(description = "timestamp")
        private Integer timestamp;
    }
}
