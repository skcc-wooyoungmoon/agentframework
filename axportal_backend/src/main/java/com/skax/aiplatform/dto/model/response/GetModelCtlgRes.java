package com.skax.aiplatform.dto.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모델 카탈로그 응답 DTO
 *
 * @author 김예리
 * @version 1.0.0
 * @since 2025-08-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetModelCtlgRes {

    @Schema(description = "모델 ID", format = "uuid")
    private String id;

    @Schema(description = "모델 표시 이름")
    private String displayName;

    @Schema(description = "모델 관리 이름")
    private String name;

    @Schema(description = "모델 타입")
    private String type;

    @Schema(description = "모델 설명")
    private String description;

    @Schema(description = "모델 파라미터 크기")
    private String size;

    @Schema(description = "토큰 크기")
    private String tokenSize;

    @Schema(description = "추론 파라미터")
    private Object inferenceParam;

    @Schema(description = "양자화 정보")
    private Object quantization;

    @Schema(description = "데이터 타입")
    private String dtype;

    @Schema(description = "서빙 타입")
    private String servingType;

    @Schema(description = "배포 상태")
    private String deployStatus;

    @Schema(description = "프라이빗 모델 여부")
    private Boolean isPrivate;

    @Schema(description = "유효성 여부")
    private Boolean isValid;

    @Schema(description = "라이선스 정보")
    private String license;

    @Schema(description = "README 문서")
    private String readme;

    @Schema(description = "모델 파일 경로")
    private String path;

    @Schema(description = "모델 제공자 ID")
    private String providerId;

    @Schema(description = "모델 제공자 이름")
    private String providerName;

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "기본 파라미터")
    private Object defaultParams;

    @Schema(description = "마지막 버전 번호")
    private Integer lastVersion;

    @Schema(description = "커스텀 코드 필요 여부")
    private Boolean isCustom;

    @Schema(description = "커스텀 코드 파일 경로")
    private String customCodePath;

    @Schema(description = "지원 언어 목록")
    private List<Tag> languages;

    @Schema(description = "수행 가능한 작업 목록")
    private List<Tag> tasks;

    @Schema(description = "모델 태그 목록")
    private List<Tag> tags;

    @Schema(description = "생성 일시", format = "date-time")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", format = "date-time")
    private LocalDateTime updatedAt;

    @Schema(description = "Endpoint URL")
    private String url;

    @Schema(description = "Endpoint Identifier")
    private String identifier;

    @Schema(description = "Endpoint Key")
    private String key;

    @Schema(description = "트레이닝 ID")
    private String trainingId;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "마지막 수정자")
    private String updatedBy;

    @Schema(description = "공개범위")
    private String publicStatus;

    @Schema(description = "endpointId")
    private String endpointId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "태그 정보")
    public static class Tag {

        @Schema(description = "태그 ID", format = "uuid")
        private String id;
        @Schema(description = "태그 이름")
        private String name;
        @Schema(description = "생성 일시", format = "date-time")
        private String created_at;
        @Schema(description = "수정 일시", format = "date-time")
        private String updated_at;
    }

    // @JsonProperty("description")
    // @Schema(description = "Endpoint Description")
    // private String description;

    // @Schema(description = "Endpoint ID")
    // private String endpointId;

    // @JsonProperty("created_at")
    // @Schema(description = "Endpoint Created At")
    // private String created_at;

    // @JsonProperty("updated_at")
    // @Schema(description = "Endpoint Updated At")
    // private String updated_at;

}
