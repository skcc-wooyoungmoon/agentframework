package com.skax.aiplatform.client.sktai.model.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 생성 요청 DTO
 * 
 * <p>SKTAI Model 시스템에서 새로운 모델을 생성하기 위한 요청 데이터 구조입니다.
 * 모델의 기본 정보, 서빙 타입, 추론 파라미터 등을 포함합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 모델 관리 이름</li>
 *   <li><strong>provider_id</strong>: 모델 제공자 ID</li>
 * </ul>
 * 
 * <h3>주요 옵션:</h3>
 * <ul>
 *   <li><strong>type</strong>: 모델 타입 (language, embedding, image 등)</li>
 *   <li><strong>serving_type</strong>: 서빙 타입 (serverless, self-hosting)</li>
 *   <li><strong>is_private</strong>: 프라이빗 모델 여부</li>
 *   <li><strong>is_custom</strong>: 커스텀 코드 필요 여부</li>
 * </ul>
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
@Schema(description = "SKTAI Model 생성 요청 정보")
public class ModelCreate {
    
    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름", example = "GPT-4", maxLength = 255)
    private String displayName;
    
    @JsonProperty("name")
    @Schema(description = "모델 관리 이름", example = "gpt-4", required = true, maxLength = 255)
    private String name;
    
    @JsonProperty("type")
    @Schema(description = "모델 타입", example = "language", allowableValues = {
        "language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"
    })
    @Builder.Default
    private String type = "language";
    
    @JsonProperty("description")
    @Schema(description = "모델 설명", example = "GPT-4는 OpenAI의 대화형 AI 모델입니다")
    private String description;
    
    @JsonProperty("size")
    @Schema(description = "모델 파라미터 크기", example = "175B", maxLength = 64)
    private String size;
    
    @JsonProperty("token_size")
    @Schema(description = "모델이 처리할 수 있는 토큰 크기", example = "8192", maxLength = 64)
    private String tokenSize;
    
    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터 (JSON 객체)", example = "{\"max_tokens\": 4096, \"temperature\": 0.7}")
    private Object inferenceParam;
    
    @JsonProperty("quantization")
    @Schema(description = "양자화 정보 (JSON 객체)")
    private Object quantization;
    
    @JsonProperty("dtype")
    @Schema(description = "데이터 타입", example = "torch.bfloat16", maxLength = 64)
    private String dtype;
    
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", example = "serverless", allowableValues = {"serverless", "self-hosting"})
    @Builder.Default
    private String servingType = "serverless";
    
    @JsonProperty("is_private")
    @Schema(description = "프라이빗 모델 여부", example = "false")
    @Builder.Default
    private Boolean isPrivate = false;
    
    @JsonProperty("is_valid")
    @Schema(description = "유효성 여부", example = "true")
    @Builder.Default
    private Boolean isValid = true;
    
    @JsonProperty("license")
    @Schema(description = "라이선스 정보", example = "MIT")
    private String license;
    
    @JsonProperty("readme")
    @Schema(description = "README 문서")
    private String readme;
    
    @JsonProperty("path")
    @Schema(description = "모델 파일 경로 (self-hosting 타입에서 필수)", maxLength = 255)
    private String path;
    
    @JsonProperty("provider_id")
    @Schema(description = "모델 제공자 ID", required = true, format = "uuid")
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
    @Schema(description = "커스텀 코드 필요 여부", example = "false")
    @Builder.Default
    private Boolean isCustom = false;
    
    @JsonProperty("custom_code_path")
    @Schema(description = "커스텀 코드 파일 경로", maxLength = 255)
    private String customCodePath;
    
    @JsonProperty("id")
    @Schema(description = "모델 ID (선택사항)", format = "uuid")
    private String id;
    
    @JsonProperty("languages")
    @Schema(description = "지원 언어 목록")
    private List<ModelLanguageRequest> languages;
    
    @JsonProperty("tasks")
    @Schema(description = "수행 가능한 작업 목록")
    private List<ModelTask> tasks;
    
    @JsonProperty("tags")
    @Schema(description = "모델 태그 목록")
    private List<ModelTagRequest> tags;
    
    @JsonProperty("policy")
    @Schema(description = "접근 정책")
    private List<PolicyPayload> policy;
}
