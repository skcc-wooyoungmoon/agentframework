package com.skax.aiplatform.client.sktai.serving.dto.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 서빙 파라미터 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 서빙에 사용할 상세 파라미터 설정입니다.
 * 모델 최적화, 리소스 사용률, 추론 설정 등을 포함합니다.</p>
 * 
 * <h3>주요 설정 카테고리:</h3>
 * <ul>
 *   <li><strong>양자화 설정</strong>: inflight_quantization, quantization, dtype</li>
 *   <li><strong>GPU 설정</strong>: gpu_memory_utilization, tensor_parallel_size, pipeline_parallel_size</li>
 *   <li><strong>모델 로딩</strong>: load_format, max_model_len, trust_remote_code</li>
 *   <li><strong>추론 제어</strong>: max_num_seqs, enforce_eager, enable_reasoning</li>
 *   <li><strong>멀티모달</strong>: limit_mm_per_prompt, mm_processor_kwargs</li>
 *   <li><strong>도구 활용</strong>: enable_auto_tool_choice, tool_call_parser</li>
 *   <li><strong>커스텀 서빙</strong>: custom_serving</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ServingParams params = ServingParams.builder()
 *     .quantization("int8")
 *     .gpuMemoryUtilization(0.9)
 *     .tensorParallelSize(2)
 *     .maxModelLen(4096)
 *     .trustRemoteCode(true)
 *     .enforceEager(false)
 *     .enableAutoToolChoice(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see CustomServingParams 커스텀 서빙 파라미터
 * @see ServingCreate 서빙 생성 요청
 * @see ServingUpdate 서빙 업데이트 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 서빙 파라미터 설정",
    example = """
        {
          "inflight_quantization": false,
          "quantization": "int8",
          "dtype": "float16",
          "gpu_memory_utilization": 0.9,
          "load_format": "auto",
          "tensor_parallel_size": 2,
          "pipeline_parallel_size": 1,
          "cpu_offload_gb": 0,
          "enforce_eager": false,
          "max_model_len": 4096,
          "vllm_use_v1": "1",
          "max_num_seqs": 256,
          "limit_mm_per_prompt": "image=4,audio=1,video=1",
          "tokenizer_mode": "auto",
          "config_format": "auto",
          "trust_remote_code": true,
          "hf_overrides": {"architectures": ["LlamaForCausalLM"]},
          "mm_processor_kwargs": {"num_crops": 4},
          "disable_mm_preprocessor_cache": false,
          "enable_auto_tool_choice": true,
          "tool_call_parser": "hermes",
          "tool_parser_plugin": "openai",
          "chat_template": "/templates/chat.jinja",
          "guided_decoding_backend": "xgrammar",
          "enable_reasoning": false,
          "reasoning_parser": "deepseek_r1",
          "device": "cuda",
          "shm_size": "1Gi"
        }
        """
)
public class ServingParams {
    
    /**
     * 인플라이트 양자화 활성화
     * 
     * <p>추론 중 동적으로 양자화를 수행할지 여부입니다.</p>
     */
    @JsonProperty("inflight_quantization")
    @Schema(
        description = "인플라이트 양자화 활성화 여부",
        example = "false"
    )
    private Boolean inflightQuantization;
    
    /**
     * 양자화 타입
     * 
     * <p>모델 양자화 방식을 지정합니다 (int8, int4, fp16 등).</p>
     */
    @JsonProperty("quantization")
    @Schema(
        description = "양자화 타입 (int8, int4, fp16 등)",
        example = "int8"
    )
    private String quantization;
    
    /**
     * 모델 데이터 타입
     * 
     * <p>모델의 데이터 타입을 설정합니다.</p>
     */
    @JsonProperty("dtype")
    @Schema(
        description = "모델 데이터 타입",
        example = "float16"
    )
    private String dtype;
    
    /**
     * GPU 메모리 사용률
     * 
     * <p>GPU 메모리 사용률을 조정합니다 (0.0 ~ 1.0).</p>
     */
    @JsonProperty("gpu_memory_utilization")
    @Schema(
        description = "GPU 메모리 사용률 (0.0 ~ 1.0)",
        example = "0.9",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double gpuMemoryUtilization;
    
    /**
     * 로드 포맷
     * 
     * <p>최적화된 모델 포맷을 선택합니다 (auto, onnx, bitsandbytes 등).</p>
     */
    @JsonProperty("load_format")
    @Schema(
        description = "최적화된 모델 포맷 (auto, onnx, bitsandbytes 등)",
        example = "auto"
    )
    private String loadFormat;
    
    /**
     * 텐서 병렬화 크기
     * 
     * <p>다중 GPU 사용 시 병렬화 설정입니다.</p>
     */
    @JsonProperty("tensor_parallel_size")
    @Schema(
        description = "다중 GPU 사용 시 텐서 병렬화 크기",
        example = "2",
        minimum = "1"
    )
    private Integer tensorParallelSize;
    
    /**
     * 파이프라인 병렬화 크기
     * 
     * <p>대규모 모델을 여러 GPU에 분산할 때 파이프라인 병렬화 설정입니다.</p>
     */
    @JsonProperty("pipeline_parallel_size")
    @Schema(
        description = "파이프라인 병렬화 크기",
        example = "1",
        minimum = "1"
    )
    private Integer pipelineParallelSize;
    
    /**
     * CPU 오프로드 메모리
     * 
     * <p>GPU 메모리 부족 시 CPU로 오프로드할 최대 메모리(GB)입니다.</p>
     */
    @JsonProperty("cpu_offload_gb")
    @Schema(
        description = "GPU 메모리 부족 시 CPU 오프로드 최대 메모리 (GB)",
        example = "0",
        minimum = "0"
    )
    private Integer cpuOffloadGb;
    
    /**
     * Eager 실행 강제
     * 
     * <p>디버깅이나 개발을 위해 Eager 실행 모드를 강제할지 여부입니다.</p>
     */
    @JsonProperty("enforce_eager")
    @Schema(
        description = "디버깅/개발용 Eager 실행 모드 강제 여부",
        example = "false"
    )
    private Boolean enforceEager;
    
    /**
     * 최대 모델 길이
     * 
     * <p>모델의 최대 시퀀스 길이 제한을 설정합니다.</p>
     */
    @JsonProperty("max_model_len")
    @Schema(
        description = "모델 최대 시퀀스 길이 제한",
        example = "4096",
        minimum = "1"
    )
    private Integer maxModelLen;
    
    /**
     * vLLM v1 엔진 사용
     * 
     * <p>vLLM v1 엔진 또는 v0 엔진 사용 여부를 설정합니다.</p>
     */
    @JsonProperty("vllm_use_v1")
    @Schema(
        description = "vLLM v1 엔진 사용 여부 (1: v1, 0: v0)",
        example = "1",
        allowableValues = {"0", "1"}
    )
    private String vllmUseV1;
    
    /**
     * 최대 시퀀스 수
     * 
     * <p>반복당 최대 시퀀스 수를 설정합니다.</p>
     */
    @JsonProperty("max_num_seqs")
    @Schema(
        description = "반복당 최대 시퀀스 수",
        example = "256",
        minimum = "1"
    )
    private Integer maxNumSeqs;
    
    /**
     * 프롬프트당 멀티모달 입력 제한
     * 
     * <p>각 프롬프트에 허용할 멀티모달 입력 인스턴스 수를 제한합니다.</p>
     */
    @JsonProperty("limit_mm_per_prompt")
    @Schema(
        description = "프롬프트당 멀티모달 입력 제한 (예: image=4,audio=1,video=1)",
        example = "image=4,audio=1,video=1"
    )
    private String limitMmPerPrompt;
    
    /**
     * 토크나이저 모드
     * 
     * <p>모델의 토크나이저 모드를 설정합니다.</p>
     */
    @JsonProperty("tokenizer_mode")
    @Schema(
        description = "모델 토크나이저 모드",
        example = "auto"
    )
    private String tokenizerMode;
    
    /**
     * 설정 포맷
     * 
     * <p>모델의 설정 포맷을 설정합니다.</p>
     */
    @JsonProperty("config_format")
    @Schema(
        description = "모델 설정 포맷",
        example = "auto"
    )
    private String configFormat;
    
    /**
     * 원격 코드 신뢰
     * 
     * <p>HuggingFace에서 원격 코드를 신뢰할지 여부입니다.</p>
     */
    @JsonProperty("trust_remote_code")
    @Schema(
        description = "HuggingFace 원격 코드 신뢰 여부",
        example = "true"
    )
    private Boolean trustRemoteCode;
    
    /**
     * HuggingFace 오버라이드
     * 
     * <p>HuggingFace 설정에 대한 추가 인수입니다.</p>
     */
    @JsonProperty("hf_overrides")
    @Schema(
        description = "HuggingFace 설정 오버라이드 (JSON 객체)",
        example = "{\"architectures\": [\"LlamaForCausalLM\"]}"
    )
    private Map<String, Object> hfOverrides;
    
    /**
     * 멀티모달 프로세서 파라미터
     * 
     * <p>멀티모달 입력 매핑/처리에 대한 오버라이드입니다.</p>
     */
    @JsonProperty("mm_processor_kwargs")
    @Schema(
        description = "멀티모달 프로세서 파라미터 (JSON 객체)",
        example = "{\"num_crops\": 4}"
    )
    private Map<String, Object> mmProcessorKwargs;
    
    /**
     * 멀티모달 전처리기 캐시 비활성화
     * 
     * <p>멀티모달 전처리기/매퍼의 캐싱을 비활성화할지 여부입니다.</p>
     */
    @JsonProperty("disable_mm_preprocessor_cache")
    @Schema(
        description = "멀티모달 전처리기 캐시 비활성화 여부 (권장하지 않음)",
        example = "false"
    )
    private Boolean disableMmPreprocessorCache;
    
    /**
     * 자동 도구 선택 활성화
     * 
     * <p>지원되는 모델에 대해 자동 도구 선택을 활성화합니다.</p>
     */
    @JsonProperty("enable_auto_tool_choice")
    @Schema(
        description = "지원 모델에 대한 자동 도구 선택 활성화",
        example = "true"
    )
    private Boolean enableAutoToolChoice;
    
    /**
     * 도구 호출 파서
     * 
     * <p>사용하는 모델에 따라 도구 호출 파서를 선택합니다.</p>
     */
    @JsonProperty("tool_call_parser")
    @Schema(
        description = "도구 호출 파서 (hermes, llama3_json, mistral, pythonic 등)",
        example = "hermes"
    )
    private String toolCallParser;
    
    /**
     * 도구 파서 플러그인
     * 
     * <p>모델 생성 도구를 OpenAI API 형식으로 파싱하는 도구 파서 플러그인입니다.</p>
     */
    @JsonProperty("tool_parser_plugin")
    @Schema(
        description = "OpenAI API 형식으로 파싱하는 도구 파서 플러그인",
        example = "openai"
    )
    private String toolParserPlugin;
    
    /**
     * 채팅 템플릿
     * 
     * <p>채팅 템플릿 파일 경로 또는 단일 라인 형태의 템플릿입니다.</p>
     */
    @JsonProperty("chat_template")
    @Schema(
        description = "채팅 템플릿 파일 경로 또는 단일 라인 템플릿",
        example = "/templates/chat.jinja"
    )
    private String chatTemplate;
    
    /**
     * 가이드 디코딩 백엔드
     * 
     * <p>JSON 스키마/정규식 등 가이드 디코딩에 사용할 엔진입니다.</p>
     */
    @JsonProperty("guided_decoding_backend")
    @Schema(
        description = "가이드 디코딩 백엔드 (xgrammar, guidance, auto, pythonic 등)",
        example = "xgrammar"
    )
    private String guidedDecodingBackend;
    
    /**
     * 추론 활성화
     * 
     * <p>모델에 대한 reasoning_content 활성화 여부입니다.</p>
     */
    @JsonProperty("enable_reasoning")
    @Schema(
        description = "모델 추론 content 활성화 여부",
        example = "false"
    )
    private Boolean enableReasoning;
    
    /**
     * 추론 파서
     * 
     * <p>사용하는 모델에 따라 추론 파서를 선택합니다.</p>
     */
    @JsonProperty("reasoning_parser")
    @Schema(
        description = "추론 파서 (deepseek_r1, granite 등)",
        example = "deepseek_r1"
    )
    private String reasoningParser;
    
    /**
     * 디바이스
     * 
     * <p>모델 실행에 사용할 디바이스를 설정합니다 (cuda, cpu 등).</p>
     */
    @JsonProperty("device")
    @Schema(
        description = "모델 실행 디바이스 (cuda, cpu 등)",
        example = "cuda"
    )
    private String device;
    
    /**
     * 공유 메모리 크기
     * 
     * <p>공유 메모리 크기를 설정합니다.</p>
     */
    @JsonProperty("shm_size")
    @Schema(
        description = "공유 메모리 크기 (예: 1Gi, 512Mi)",
        example = "1Gi"
    )
    private String shmSize;
    
    /**
     * 커스텀 서빙 설정
     * 
     * <p>사용자 정의 서빙 파라미터입니다.</p>
     */
    @JsonProperty("custom_serving")
    @Schema(
        description = "커스텀 서빙 파라미터"
    )
    private CustomServingParams customServing;
    
    /**
     * 모델 정의 경로
     * 
     * <p>모델 정의 파일의 경로입니다.</p>
     */
    @JsonProperty("model_definition_path")
    @Schema(
        description = "모델 정의 경로",
        example = "string"
    )
    private String modelDefinitionPath;
}