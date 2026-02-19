package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로세서 정보")
public class Processor {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("rule_pattern")
    private String rulePattern;

    @JsonProperty("rule_value")
    private String ruleValue;

    @JsonProperty("code")
    private String code;

    @JsonProperty("default_key")
    private String defaultKey;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
