package com.skax.aiplatform.dto.model.request;

import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelLanguage;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelTag;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelTask;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 카탈로그 수정 요청")
public class GetUpdateModelCtlgReq {

    @Schema(description = "모델 표시 이름", example = "GPT-4 Updated", maxLength = 255)
    private String displayName;

    @Schema(description = "모델 관리 이름", example = "gpt-4-updated", maxLength = 255)
    private String name;

    @Schema(description = "모델 타입", example = "language", allowableValues = {
            "language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"
    })
    private String type;

    @Schema(description = "모델 설명", example = "GPT-4는 OpenAI의 대화형 AI 모델입니다")
    private String description;

    @Schema(description = "모델 파라미터 크기", example = "175B", maxLength = 64)
    private String size;

    @Schema(description = "모델이 처리할 수 있는 토큰 크기", example = "8192", maxLength = 64)
    private String tokenSize;

    @Schema(description = "추론 파라미터 (JSON 객체)", example = "{\"max_tokens\": 4096, \"temperature\": 0.7}")
    private Object inferenceParam;

    @Schema(description = "양자화 정보 (JSON 객체)")
    private Object quantization;

    @Schema(description = "데이터 타입", example = "torch.bfloat16", maxLength = 64)
    private String dtype;

    @Schema(description = "서빙 타입", example = "serverless", allowableValues = {"serverless", "self-hosting"})
    private String servingType;

    @Schema(description = "프라이빗 모델 여부", example = "false")
    private Boolean isPrivate;

    @Schema(description = "유효성 여부", example = "true")
    private Boolean isValid;

    @Schema(description = "라이선스 정보", example = "MIT")
    private String license;

    @Schema(description = "README 문서")
    private String readme;

    @Schema(description = "모델 파일 경로 (self-hosting 타입에서 필수)", maxLength = 255)
    private String path;

    @Schema(description = "모델 제공자 ID", format = "uuid")
    private String providerId;

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "기본 파라미터 (JSON 객체)")
    private Object defaultParams;

    @Schema(description = "마지막 버전 번호")
    private Integer lastVersion;

    @Schema(description = "커스텀 코드 필요 여부", example = "false")
    private Boolean isCustom;

    @Schema(description = "커스텀 코드 파일 경로", maxLength = 255)
    private String customCodePath;

    @Schema(description = "지원 언어 목록")
    private List<ModelLanguage> languages;

    @Schema(description = "수행 가능한 작업 목록")
    private List<ModelTask> tasks;

    @Schema(description = "변경된 모델 태그 목록")
    private List<ModelTag> tags;

    @Schema(description = "기존 모델 태그 목록")
    private List<ModelTag> originTags;

    @Schema(description = "접근 정책")
    private List<PolicyPayload> policy;

    @Schema(description = "엔드포인트 고유 식별자 (UUID)")
    private String endpointId;

    @Schema(description = "Endpoint URL")
    private String url;

    @Schema(description = "Endpoint Identifier")
    private String identifier;

    @Schema(description = "Endpoint Key")
    private String key;
} 