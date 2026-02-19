package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로세서 상세 정보",
        example = """
                {
                  "project_id": null,
                  "id": "3398014c-e0ad-4b4d-a8d2-44f4b0d0ff1d",
                  "description": "특정 컬럼 기준으로 중복된 데이터를 제거합니다.",
                  "data_type": "dataframe",
                  "rule_pattern": "",
                  "code": "",
                  "updated_at": "2025-04-22T07:31:15.666394",
                  "updated_by": "admin",
                  "type": "default",
                  "name": "중복데이터 제거",
                  "default_key": "drop_duplicates",
                  "rule_value": "",
                  "created_at": "2025-04-22T07:31:15.666394",
                  "created_by": "admin"
                }
                """)
public class ProcessorDetail {

    @JsonProperty("id")
    @Schema(description = "프로세서 ID (UUID)")
    private UUID id;

    @JsonProperty("name")
    @Schema(description = "프로세서 명")
    private String name;

    @JsonProperty("description")
    @Schema(description = "설명")
    private String description;

    @JsonProperty("type")
    @Schema(description = "프로세서 유형 (예: default, rule)")
    private String type;

    @JsonProperty("data_type")
    @Schema(description = "데이터 타입 (예: all, dataframe)")
    private String dataType;

    @JsonProperty("rule_pattern")
    @Schema(description = "규칙 패턴(정규식 등)")
    private String rulePattern;

    @JsonProperty("rule_value")
    @Schema(description = "규칙 대체 값")
    private String ruleValue;

    @JsonProperty("code")
    @Schema(description = "커스텀 코드(있다면)")
    private String code;

    @JsonProperty("default_key")
    @Schema(description = "기본 키 (예: drop_duplicates)")
    private String defaultKey;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", nullable = true)
    private String projectId;

    @JsonProperty("created_at")
    @Schema(description = "생성 일시 (ISO-8601, 예: 2025-04-22T07:31:15.666394)")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 일시 (ISO-8601, 예: 2025-04-22T07:31:15.666394)")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "최종 수정자")
    private String updatedBy;
}