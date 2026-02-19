package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateModelGardenReq {
    // 필수 입력 필드들만 포함
    @Schema(description = "artifact_id", example = "a-test-20251229")
    private String artifact_id;

    @Schema(description = "revision_id", example = "r-test-20251229")
    private String revision_id;

    @Schema(description = "name", example = "test-name")
    private String name;

    @Schema(description = "description", example = "test-description")
    private String description;

    @Schema(description = "size", example = "8.0")
    private String size;

    @Schema(description = "param_size", example = "60")
    private Number param_size;

    @Schema(description = "serving_type", example = "self-hosting", allowableValues = {"self-hosting", "serverless"}, required = true)
    private String serving_type;

    @Schema(description = "version", example = "test-version")
    private String version;

    @Schema(description = "provider", example = "OpenAI")
    private String provider;

    @Schema(description = "providerId", example = "123e4567-e89b-12d3-a456-426614174000")
    private String providerId;

    @Schema(description = "type", example = "language", allowableValues = {"language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"})
    private String type;

    @Schema(description = "license", example = "MIT")
    private String license;

    @Schema(description = "readme", example = "test-사용법")
    private String readme;

    @Schema(description = "tags", example = "test-tags")
    private String tags;
    
    @Schema(description = "langauges", example = "test-langauges,test-langauges2,test-langauges3")
    private String langauges;

    @Schema(description = "url", example = "test-url")
    private String url;

    @Schema(description = "identifier", example = "test-identifier")
    private String identifier;


    
    @Schema(description = "statusNm", example = "PENDING", allowableValues = {
        "PENDING", "IMPORT_REQUEST_APPROVAL_IN_PROGRESS", "IMPORT_REQUEST_APPROVAL_COMPLETED", 
        "IMPORT_REQUEST_APPROVAL_REJECTED", "VACCINE_SCAN_COMPLETED", "INTERNAL_NETWORK_IMPORT_COMPLETED",
        "VULNERABILITY_CHECK_COMPLETED", "IMPORT_FAILED", "VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS",
        "VULNERABILITY_CHECK_APPROVAL_REJECTED", "IMPORT_COMPLETED"
    })
    private String statusNm;
    
}
