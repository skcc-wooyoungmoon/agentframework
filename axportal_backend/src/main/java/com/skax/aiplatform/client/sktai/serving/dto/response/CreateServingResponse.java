package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * SKTAI Create Serving 응답 DTO
 * 
 * <p>
 * SKTAI Serving 시스템에서 모델 서빙 생성 작업의 응답 데이터를 담는 구조입니다.
 * 서빙 생성 작업 결과를 포함합니다.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Create Serving 응답 정보")
public class CreateServingResponse {

    @JsonProperty("serving_id")
    @Schema(description = "서빙 고유 식별자")
    private String servingId;

    @JsonProperty("name")
    @Schema(description = "서빙 이름")
    private String name;

    @JsonProperty("description")
    @Schema(description = "서빙 설명")
    private String description;

    @JsonProperty("kserve_yaml")
    @Schema(description = "KServe YAML")
    private String kserveYaml;

    @JsonProperty("isvc_name")
    @Schema(description = "InferenceService 이름")
    private String isvcName;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @JsonProperty("namespace")
    @Schema(description = "네임스페이스")
    private String namespace;

    @JsonProperty("status")
    @Schema(description = "서빙 상태")
    private String status;

    @JsonProperty("model_id")
    @Schema(description = "모델 ID")
    private String modelId;

    @JsonProperty("version_id")
    @Schema(description = "버전 ID")
    private String versionId;

    @JsonProperty("serving_params")
    @Schema(description = "서빙 파라미터")
    private ServingParams servingParams;

    @JsonProperty("error_message")
    @Schema(description = "오류 메시지")
    private String errorMessage;

    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량")
    private Integer cpuRequest;

    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량")
    private Integer cpuLimit;

    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량")
    private Integer gpuRequest;

    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량")
    private Integer gpuLimit;

    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량")
    private Integer memRequest;

    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량")
    private Integer memLimit;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터")
    private Boolean safetyFilterInput;

    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터")
    private Boolean safetyFilterOutput;

    @JsonProperty("data_masking_input")
    @Schema(description = "입력 데이터 마스킹")
    private Boolean dataMaskingInput;

    @JsonProperty("data_masking_output")
    @Schema(description = "출력 데이터 마스킹")
    private Boolean dataMaskingOutput;

    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수")
    private Integer minReplicas;

    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수")
    private Integer maxReplicas;

    @JsonProperty("autoscaling_class")
    @Schema(description = "오토스케일링 클래스")
    private String autoscalingClass;

    @JsonProperty("autoscaling_metric")
    @Schema(description = "오토스케일링 메트릭")
    private String autoscalingMetric;

    @JsonProperty("target")
    @Schema(description = "타겟 값")
    private Integer target;

    @JsonProperty("model_name")
    @Schema(description = "모델 이름")
    private String modelName;

    @JsonProperty("display_name")
    @Schema(description = "표시 이름")
    private String displayName;

    @JsonProperty("model_description")
    @Schema(description = "모델 설명")
    private String modelDescription;

    @JsonProperty("type")
    @Schema(description = "모델 타입")
    private String type;

    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;

    @JsonProperty("is_private")
    @Schema(description = "비공개 여부")
    private Boolean isPrivate;

    @JsonProperty("is_valid")
    @Schema(description = "유효 여부")
    private Boolean isValid;

    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터")
    private String inferenceParam;

    @JsonProperty("quantization")
    @Schema(description = "양자화 정보")
    private Map<String, Object> quantization;

    @JsonProperty("provider_name")
    @Schema(description = "제공자 이름")
    private String providerName;

    @JsonProperty("model_version")
    @Schema(description = "모델 버전")
    private String modelVersion;

    @JsonProperty("path")
    @Schema(description = "경로")
    private String path;

    @JsonProperty("version_path")
    @Schema(description = "버전 경로")
    private String versionPath;

    @JsonProperty("fine_tuning_id")
    @Schema(description = "파인튜닝 ID")
    private String fineTuningId;

    @JsonProperty("version_is_valid")
    @Schema(description = "버전 유효 여부")
    private Boolean versionIsValid;

    @JsonProperty("version_is_deleted")
    @Schema(description = "버전 삭제 여부")
    private Boolean versionIsDeleted;

    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입")
    private String gpuType;

    @JsonProperty("is_custom")
    @Schema(description = "커스텀 여부")
    private Boolean isCustom;

    @JsonProperty("serving_mode")
    @Schema(description = "서빙 모드")
    private String servingMode;

    @JsonProperty("serving_operator")
    @Schema(description = "서빙 오퍼레이터")
    private String servingOperator;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "서빙 파라미터")
    public static class ServingParams {

        @JsonProperty("inflight_quantization")
        @Schema(description = "인플라이트 양자화")
        private Boolean inflightQuantization;

        @JsonProperty("quantization")
        @Schema(description = "양자화")
        private String quantization;

        @JsonProperty("dtype")
        @Schema(description = "데이터 타입")
        private String dtype;

        @JsonProperty("gpu_memory_utilization")
        @Schema(description = "GPU 메모리 사용률")
        private Double gpuMemoryUtilization;

        @JsonProperty("load_format")
        @Schema(description = "로드 포맷")
        private String loadFormat;

        @JsonProperty("tensor_parallel_size")
        @Schema(description = "텐서 병렬 크기")
        private Integer tensorParallelSize;

        @JsonProperty("cpu_offload_gb")
        @Schema(description = "CPU 오프로드 GB")
        private Integer cpuOffloadGb;

        @JsonProperty("enforce_eager")
        @Schema(description = "강제 이거 모드")
        private Boolean enforceEager;

        @JsonProperty("max_model_len")
        @Schema(description = "최대 모델 길이")
        private Integer maxModelLen;

        @JsonProperty("vllm_use_v1")
        @Schema(description = "VLLM V1 사용")
        private String vllmUseV1;

        @JsonProperty("max_num_seqs")
        @Schema(description = "최대 시퀀스 수")
        private Integer maxNumSeqs;

        @JsonProperty("limit_mm_per_prompt")
        @Schema(description = "프롬프트당 멀티모달 제한")
        private String limitMmPerPrompt;

        @JsonProperty("tokenizer_mode")
        @Schema(description = "토크나이저 모드")
        private String tokenizerMode;

        @JsonProperty("config_format")
        @Schema(description = "설정 포맷")
        private String configFormat;

        @JsonProperty("trust_remote_code")
        @Schema(description = "원격 코드 신뢰")
        private Boolean trustRemoteCode;

        @JsonProperty("hf_overrides")
        @Schema(description = "HuggingFace 오버라이드")
        private Map<String, Object> hfOverrides;

        @JsonProperty("mm_processor_kwargs")
        @Schema(description = "멀티모달 프로세서 키워드 인수")
        private Map<String, Object> mmProcessorKwargs;

        @JsonProperty("disable_mm_preprocessor_cache")
        @Schema(description = "멀티모달 전처리기 캐시 비활성화")
        private Boolean disableMmPreprocessorCache;

        @JsonProperty("enable_auto_tool_choice")
        @Schema(description = "자동 도구 선택 활성화")
        private Boolean enableAutoToolChoice;

        @JsonProperty("tool_call_parser")
        @Schema(description = "도구 호출 파서")
        private String toolCallParser;

        @JsonProperty("tool_parser_plugin")
        @Schema(description = "도구 파서 플러그인")
        private String toolParserPlugin;

        @JsonProperty("chat_template")
        @Schema(description = "채팅 템플릿")
        private String chatTemplate;

        @JsonProperty("guided_decoding_backend")
        @Schema(description = "가이드 디코딩 백엔드")
        private String guidedDecodingBackend;

        @JsonProperty("enable_reasoning")
        @Schema(description = "추론 활성화")
        private Boolean enableReasoning;

        @JsonProperty("reasoning_parser")
        @Schema(description = "추론 파서")
        private String reasoningParser;

        @JsonProperty("device")
        @Schema(description = "디바이스")
        private String device;

        @JsonProperty("shm_size")
        @Schema(description = "공유 메모리 크기")
        private String shmSize;

        @JsonProperty("custom_serving")
        @Schema(description = "커스텀 서빙")
        private CustomServing customServing;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "커스텀 서빙")
    public static class CustomServing {

        @JsonProperty("image_url")
        @Schema(description = "이미지 URL")
        private String imageUrl;

        @JsonProperty("use_bash")
        @Schema(description = "Bash 사용 여부")
        private Boolean useBash;

        @JsonProperty("command")
        @Schema(description = "명령어")
        private String[] command;

        @JsonProperty("args")
        @Schema(description = "인수")
        private String[] args;
    }
}
