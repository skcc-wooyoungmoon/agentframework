package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 수정 요청 DTO
 * 
 * <p>기존 모델 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 모든 필드는 선택사항이며, 제공된 필드만 업데이트됩니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "SKTAI Model 수정 요청 정보")
public class ModelUpdate {
    
    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름", example = "GPT-4 Updated", maxLength = 255)
    private String displayName;
    
    @JsonProperty("name")
    @Schema(description = "모델 관리 이름", example = "gpt-4-updated", maxLength = 255)
    private String name;
    
    @JsonProperty("type")
    @Schema(description = "모델 타입", example = "language", allowableValues = {
        "language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"
    })
    private String type;
    
    @JsonProperty("description")
    @Schema(description = "모델 설명")
    private String description;
    
    @JsonProperty("size")
    @Schema(description = "모델 파라미터 크기", maxLength = 64)
    private String size;
    
    @JsonProperty("token_size")
    @Schema(description = "모델이 처리할 수 있는 토큰 크기", maxLength = 64)
    private String tokenSize;
    
    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터 (JSON 객체)")
    private Object inferenceParam;
    
    @JsonProperty("quantization")
    @Schema(description = "양자화 정보 (JSON 객체)")
    private Object quantization;
    
    @JsonProperty("dtype")
    @Schema(description = "데이터 타입", maxLength = 64)
    private String dtype;
    
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", allowableValues = {"serverless", "self-hosting"})
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
    @Schema(description = "모델 파일 경로", maxLength = 255)
    private String path;
    
    @JsonProperty("provider_id")
    @Schema(description = "모델 제공자 ID", format = "uuid")
    private String providerId;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;
    
    @JsonProperty("default_params")
    @Schema(description = "기본 파라미터 (JSON 객체)")
    private Object defaultParams;
    
    @JsonProperty("last_version")
    @Schema(description = "마지막 버전 번호")
    private Integer lastVersion;
    
    @JsonProperty("is_custom")
    @Schema(description = "커스텀 코드 필요 여부")
    private Boolean isCustom;
    
    @JsonProperty("custom_code_path")
    @Schema(description = "커스텀 코드 파일 경로", maxLength = 255)
    private String customCodePath;
}
