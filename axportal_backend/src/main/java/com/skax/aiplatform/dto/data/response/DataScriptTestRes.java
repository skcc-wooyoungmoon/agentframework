package com.skax.aiplatform.dto.data.response;

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
    description = "Script Test 응답 정보"
)
public class DataScriptTestRes {
    @Schema(description = "document list")
    private List<PreviewDocument> documentList;

    @Schema(description = "document count")
    private Integer documentCount;

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

        @Schema(description = "page content")
        private String pageContent;

        @Schema(description = "metadata")
        private Metadata metadata;

        @Schema(description = "endpoint")
        private String endpoint;

        @Schema(description = "key")
        private String key;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Metadata 정보")
    public static class Metadata {
        @Schema(description = "cateNm")
        private String cateNm;

        @Schema(description = "cateSeq")
        private Integer cateSeq;

        @Schema(description = "cateNm1")
        private String cateNm1;

        @Schema(description = "file Name")
        private String fileName;

        @Schema(description = "docType")
        private String docType;

        @Schema(description = "timestamp")
        private Integer timestamp;
    }
}
