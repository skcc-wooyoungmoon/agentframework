package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Model 응답 DTO
 *
 * <p>SKTAI Model 시스템에서 모델 정보를 반환하는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model 응답 정보")
public class ModelRead {

    @JsonProperty("id")
    @Schema(description = "모델 ID", format = "uuid")
    private String id;

    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름")
    private String displayName;

    @JsonProperty("name")
    @Schema(description = "모델 관리 이름")
    private String name;

    @JsonProperty("type")
    @Schema(description = "모델 타입")
    private String type;

    @JsonProperty("description")
    @Schema(description = "모델 설명")
    private String description;

    @JsonProperty("size")
    @Schema(description = "모델 파라미터 크기")
    private String size;

    @JsonProperty("token_size")
    @Schema(description = "토큰 크기")
    private String tokenSize;

    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터")
    private Object inferenceParam;

    @JsonProperty("quantization")
    @Schema(description = "양자화 정보")
    private Object quantization;

    @JsonProperty("dtype")
    @Schema(description = "데이터 타입")
    private String dtype;

    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;

    @JsonProperty("is_private")
    @Schema(description = "프라이빗 모델 여부")
    private Boolean isPrivate;

    @JsonProperty("is_valid")
    @Schema(description = "유효성 여부")
    private Boolean isValid;

    @JsonProperty("license")
    @Schema(description = "라이선스 정보")
    private String license;

    @JsonProperty("readme")
    @Schema(description = "README 문서")
    private String readme;

    @JsonProperty("path")
    @Schema(description = "모델 파일 경로")
    private String path;

    @JsonProperty("provider_id")
    @Schema(description = "모델 제공자 ID")
    private String providerId;

    @JsonProperty("provider_name")
    @Schema(description = "모델 제공자 이름")
    private String providerName;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @JsonProperty("default_params")
    @Schema(description = "기본 파라미터")
    private Object defaultParams;

    @JsonProperty("last_version")
    @Schema(description = "마지막 버전 번호")
    private Integer lastVersion;

    @JsonProperty("is_custom")
    @Schema(description = "커스텀 코드 필요 여부")
    private Boolean isCustom;

    @JsonProperty("custom_code_path")
    @Schema(description = "커스텀 코드 파일 경로")
    private String customCodePath;

    @JsonProperty("languages")
    @Schema(description = "지원 언어 목록")
    private List<Tag> languages;

    @JsonProperty("tasks")
    @Schema(description = "수행 가능한 작업 목록")
    private List<Tag> tasks;

    @JsonProperty("tags")
    @Schema(description = "모델 태그 목록")
    private List<Tag> tags;

    @JsonProperty("created_at")
    @Schema(description = "생성 일시", format = "date-time")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", format = "date-time")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "마지막 수정자")
    private String updatedBy;

    @JsonProperty("training_id")
    @Schema(description = "트레이닝 ID")
    private String trainingId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "태그 정보")
    public static class Tag {
        @JsonProperty("id")
        @Schema(description = "태그 ID", format = "uuid")
        private String id;

        @JsonProperty("name")
        @Schema(description = "태그 이름")
        private String name;

        @Schema(description = "생성 일시", format = "date-time")
        @JsonProperty("created_at")
        private String created_at;

        @JsonProperty("updated_at")
        @Schema(description = "수정 일시", format = "date-time")
        private String updated_at;
    }
}
