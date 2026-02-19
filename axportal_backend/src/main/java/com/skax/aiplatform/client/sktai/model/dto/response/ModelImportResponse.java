package com.skax.aiplatform.client.sktai.model.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Import 응답 DTO
 * 
 * <p>SKTAI Model 시스템에서 Model Import 결과를 담는 응답 데이터 구조입니다.</p>
 *
 * @since 2025-11-11
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Model Import 응답")
public class ModelImportResponse {
    
    @JsonProperty("id")
    @Schema(description = "모델 ID", format = "uuid")
    private String id;
    
    @JsonProperty("status")
    @Schema(description = "Import 상태", example = "existing")
    private String status;
    
    @JsonProperty("message")
    @Schema(description = "Import 메시지", example = "Model with same ID and identical properties found - no changes needed")
    private String message;
    
    @JsonProperty("model")
    @Schema(description = "Import된 Model 데이터")
    private ImportedModel model;
    
    @JsonProperty("endpoints")
    @Schema(description = "엔드포인트 목록 (루트 레벨)")
    private List<Endpoint> endpoints;
    
    /**
     * Import된 Model 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Import된 Model 데이터")
    public static class ImportedModel {
        
        @JsonProperty("id")
        @Schema(description = "모델 ID", format = "uuid")
        private String id;
        
        @JsonProperty("name")
        @Schema(description = "모델 관리 이름")
        private String name;
        
        @JsonProperty("display_name")
        @Schema(description = "모델 표시 이름")
        private String displayName;
        
        @JsonProperty("type")
        @Schema(description = "모델 타입", example = "language")
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
        @Schema(description = "서빙 타입", example = "serverless")
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
        @Schema(description = "모델 제공자 ID", format = "uuid")
        private String providerId;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID", format = "uuid")
        private String projectId;
        
        @JsonProperty("default_params")
        @Schema(description = "기본 파라미터")
        private Object defaultParams;
        
        @JsonProperty("last_version")
        @Schema(description = "마지막 버전 번호")
        private Integer lastVersion;
        
        @JsonProperty("is_custom")
        @Schema(description = "커스텀 모델 여부")
        private Boolean isCustom;
        
        @JsonProperty("custom_code_path")
        @Schema(description = "커스텀 코드 파일 경로")
        private String customCodePath;
        
        @JsonProperty("training_id")
        @Schema(description = "트레이닝 ID", format = "uuid")
        private String trainingId;
        
        @JsonProperty("is_deleted")
        @Schema(description = "삭제 여부")
        private Boolean isDeleted;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 일시", format = "date-time")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 일시", format = "date-time")
        private String updatedAt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자 ID")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "수정자 ID")
        private String updatedBy;
        
    }
    
    /**
     * 엔드포인트 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "엔드포인트 정보")
    public static class Endpoint {
        
        @JsonProperty("url")
        @Schema(description = "엔드포인트 URL")
        private String url;
        
        @JsonProperty("identifier")
        @Schema(description = "엔드포인트 식별자")
        private String identifier;
        
        @JsonProperty("key")
        @Schema(description = "엔드포인트 키")
        private String key;
        
        @JsonProperty("description")
        @Schema(description = "엔드포인트 설명")
        private String description;
        
        @JsonProperty("id")
        @Schema(description = "엔드포인트 ID", format = "uuid")
        private String id;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 일시", format = "date-time")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 일시", format = "date-time")
        private String updatedAt;
    }
}

