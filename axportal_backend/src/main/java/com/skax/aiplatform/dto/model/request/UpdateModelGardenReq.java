package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateModelGardenReq {

    @Schema(description = "name", example = "test-name", required = true)
    private String name;

    @Schema(description = "description", example = "test-description")
    private String description;

    @Schema(description = "param_size", example = "60")
    private Number param_size;


    @Schema(description = "provider", example = "OpenAI")
    private String provider;
    
    @Schema(description = "providerId", example = "123e4567-e89b-12d3-a456-426614174000")
    private String providerId;

    @Schema(description = "type", example = "language", allowableValues = {"language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"})
    private String type;

    @Schema(description = "url", example = "test-url")
    private String url;

    @Schema(description = "identifier", example = "test-identifier")
    private String identifier;


    @Schema(description = "tags", example = "test-tags")
    private String tags;
    
    @Schema(description = "langauges", example = "test-langauges,test-langauges2,test-langauges3")
    private String langauges;

    @Schema(description = "license", example = "MIT")
    private String license;

    
    @Schema(description = "statusNm", example = "PENDING", allowableValues = {
        "PENDING", "IMPORT_REQUEST_APPROVAL_IN_PROGRESS", "IMPORT_REQUEST_APPROVAL_COMPLETED", 
        "IMPORT_REQUEST_APPROVAL_REJECTED", "VACCINE_SCAN_COMPLETED", "INTERNAL_NETWORK_IMPORT_COMPLETED",
        "VULNERABILITY_CHECK_COMPLETED", "IMPORT_FAILED", "VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS",
        "VULNERABILITY_CHECK_APPROVAL_REJECTED", "IMPORT_COMPLETED"
    })
    private String statusNm;
    
    @Schema(description = "파일 반입 상세 내용", example = "test-fistChkDtlCtnt")
    private String fistChkDtlCtnt;

    @Schema(description = "백신 검사 상세 내용", example = "test-secdChkDtlCtnt")
    private String secdChkDtlCtnt;

    @Schema(description = "취약점 점검 상세 내용", example = "test-vanbBrDtlCtnt")
    private String vanbBrDtlCtnt;

    @Schema(description = "취약점 점검 요약 내용", example = "test-vanbBrSmryCtnt")
    private String vanbBrSmryCtnt;

    @Schema(description = "파일 분할 개수", example = "1")
    private Integer fileDivCnt;
    
}
