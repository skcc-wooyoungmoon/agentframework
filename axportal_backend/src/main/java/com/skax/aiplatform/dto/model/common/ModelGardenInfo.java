package com.skax.aiplatform.dto.model.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.skax.aiplatform.enums.ModelGardenStatus;

@Data
public class ModelGardenInfo {
    
    @Schema(description = "id", example = "1")
    private String id;

    @Schema(description = "artifact_id", example = "1")
    private String artifact_id;

    @Schema(description = "revision_id", example = "1")
    private String revision_id;

    @Schema(description = "name", example = "1")
    private String name;
    
    @Schema(description = "description", example = "1")
    private String description;

    @Schema(description = "size", example = "1")
    private String size;

    @Schema(description = "param_size", example = "1")
    private String param_size;

    @Schema(description = "serving_type", example = "self_hosting", allowableValues = {"self_hosting", "serverless"})
    private String serving_type;

    @Schema(description = "version", example = "1")
    private String version;

    @Schema(description = "provider", example = "1")
    private String provider;

    @Schema(description = "providerId", example = "123e4567-e89b-12d3-a456-426614174000")
    private String providerId;

    @Schema(description = "type", example = "language", allowableValues = {"language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"})
    private String type;

    @Schema(description = "license", example = "1")
    private String license;

    @Schema(description = "readme", example = "1")
    private String readme;

    @Schema(description = "tags", example = "1")
    private String tags;

    @Schema(description = "langauges", example = "1")
    private String langauges;

    @Schema(description = "url")
    private String url;

    @Schema(description = "identifier")
    private String identifier;


    @Schema(description = "statusNm", example = "PENDING", allowableValues = {
        "PENDING", "IMPORT_REQUEST_APPROVAL_IN_PROGRESS", "IMPORT_REQUEST_APPROVAL_COMPLETED", 
        "IMPORT_REQUEST_APPROVAL_REJECTED", "VACCINE_SCAN_COMPLETED", "INTERNAL_NETWORK_IMPORT_COMPLETED",
        "VULNERABILITY_CHECK_COMPLETED", "IMPORT_FAILED", "VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS",
        "VULNERABILITY_CHECK_APPROVAL_REJECTED", "IMPORT_COMPLETED"
    })
    private ModelGardenStatus statusNm;

    @Schema(description = "doipAt", example = "2021-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime doipAt;

    @Schema(description = "doipMn", example = "user")
    private String doipMn;

    @Schema(description = "chkAt", example = "2021-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime chkAt;

    @Schema(description = "chkMn", example = "user")
    private String chkMn;

    @Schema(description = "created_at", example = "2021-01-01 00:00:00")
    private String created_at;

    @Schema(description = "updated_at", example = "2021-01-01 00:00:00")
    private String updated_at;

    @Schema(description = "created_by", example = "user")
    private String created_by;

    @Schema(description = "updated_by", example = "user")
    private String updated_by;

    @Schema(description = "deleted", example = "N")
    private String deleted;
}
