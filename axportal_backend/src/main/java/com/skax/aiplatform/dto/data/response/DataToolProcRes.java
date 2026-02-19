package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 데이터 툴 프로세서 목록 응답 DTO (Public Contract)
 * 실제 응답 스키마에 맞게 필드 구성
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터 툴 프로세서 아이템")
public class DataToolProcRes {

    @JsonProperty("id")
    @Schema(description = "프로세서 ID")
    private UUID id;

    @JsonProperty("name")
    @Schema(description = "프로세서 명")
    private String name;

    @JsonProperty("description")
    @Schema(description = "설명")
    private String description;

    @JsonProperty("type")
    @Schema(description = "프로세서 유형 (default | rule 등)")
    private String type;

    @JsonProperty("data_type")
    @Schema(description = "데이터 타입 (all | dataframe 등)")
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

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "최종 수정자")
    private String updatedBy;

    @JsonProperty("created_at")
    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;
}