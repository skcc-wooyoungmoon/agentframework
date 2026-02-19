package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI HuggingFace 오버라이드 설정 DTO
 * 
 * <p>HuggingFace 모델 로딩 시 추가 설정을 오버라이드하기 위한 파라미터입니다.
 * 모델 아키텍처, 토크나이저 설정 등을 커스터마이징할 때 사용합니다.</p>
 * 
 * <h3>주요 설정 항목:</h3>
 * <ul>
 *   <li><strong>architectures</strong>: 모델 아키텍처 클래스명</li>
 *   <li><strong>torch_dtype</strong>: PyTorch 데이터 타입</li>
 *   <li><strong>model_type</strong>: 모델 타입 지정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * HuggingFaceOverrides overrides = HuggingFaceOverrides.builder()
 *     .architectures(List.of("DeepseekVLV2ForCausalLM"))
 *     .torchDtype("bfloat16")
 *     .modelType("deepseek_vl")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "HuggingFace 모델 설정 오버라이드",
    example = """
        {
          "architectures": ["DeepseekVLV2ForCausalLM"],
          "torch_dtype": "bfloat16",
          "model_type": "deepseek_vl",
          "vocab_size": 102400,
          "hidden_size": 4096
        }
        """
)
public class HuggingFaceOverrides {
    
    /**
     * 모델 아키텍처 클래스명 목록
     * 
     * <p>HuggingFace 모델의 아키텍처 클래스를 명시적으로 지정합니다.
     * 자동 감지가 실패하거나 특정 아키텍처를 강제로 사용할 때 설정합니다.</p>
     * 
     * @implNote 일반적으로 하나의 아키텍처만 지정하지만, 배열 형태로 제공됩니다.
     */
    @JsonProperty("architectures")
    @Schema(
        description = "모델 아키텍처 클래스명 목록", 
        example = "[\"DeepseekVLV2ForCausalLM\"]"
    )
    private java.util.List<String> architectures;
    
    /**
     * PyTorch 데이터 타입
     * 
     * <p>모델 가중치의 PyTorch 데이터 타입을 지정합니다.
     * 메모리 사용량과 계산 속도에 영향을 줍니다.</p>
     * 
     * @apiNote 일반적인 값: "float16", "bfloat16", "float32"
     */
    @JsonProperty("torch_dtype")
    @Schema(
        description = "PyTorch 데이터 타입", 
        example = "bfloat16",
        allowableValues = {"float16", "bfloat16", "float32", "int8"}
    )
    private String torchDtype;
    
    /**
     * 모델 타입
     * 
     * <p>HuggingFace 모델의 타입을 명시적으로 지정합니다.
     * 모델 로딩 및 처리 방식을 결정하는 데 사용됩니다.</p>
     */
    @JsonProperty("model_type")
    @Schema(
        description = "HuggingFace 모델 타입", 
        example = "deepseek_vl"
    )
    private String modelType;
    
    /**
     * 어휘 크기
     * 
     * <p>모델의 어휘 사전 크기를 오버라이드합니다.
     * 토크나이저와 모델 간 불일치를 해결할 때 사용합니다.</p>
     */
    @JsonProperty("vocab_size")
    @Schema(
        description = "어휘 사전 크기", 
        example = "102400",
        minimum = "1000"
    )
    private Integer vocabSize;
    
    /**
     * 히든 레이어 크기
     * 
     * <p>모델의 히든 레이어 차원을 오버라이드합니다.
     * 모델 아키텍처 변경 시 사용됩니다.</p>
     */
    @JsonProperty("hidden_size")
    @Schema(
        description = "히든 레이어 차원 크기", 
        example = "4096",
        minimum = "128"
    )
    private Integer hiddenSize;
    
    /**
     * 어텐션 헤드 수
     * 
     * <p>멀티헤드 어텐션의 헤드 수를 오버라이드합니다.</p>
     */
    @JsonProperty("num_attention_heads")
    @Schema(
        description = "어텐션 헤드 수", 
        example = "32",
        minimum = "1"
    )
    private Integer numAttentionHeads;
    
    /**
     * 히든 레이어 수
     * 
     * <p>트랜스포머 모델의 히든 레이어 수를 오버라이드합니다.</p>
     */
    @JsonProperty("num_hidden_layers")
    @Schema(
        description = "히든 레이어 수", 
        example = "32",
        minimum = "1"
    )
    private Integer numHiddenLayers;
    
    /**
     * 최대 위치 임베딩
     * 
     * <p>모델이 처리할 수 있는 최대 시퀀스 길이를 오버라이드합니다.</p>
     */
    @JsonProperty("max_position_embeddings")
    @Schema(
        description = "최대 위치 임베딩 (시퀀스 길이)", 
        example = "4096",
        minimum = "512"
    )
    private Integer maxPositionEmbeddings;
    
    /**
     * 중간층 크기
     * 
     * <p>피드포워드 네트워크의 중간층 크기를 오버라이드합니다.</p>
     */
    @JsonProperty("intermediate_size")
    @Schema(
        description = "피드포워드 중간층 크기", 
        example = "11008",
        minimum = "128"
    )
    private Integer intermediateSize;
    
    /**
     * RMS 정규화 엡실론
     * 
     * <p>RMS Layer Normalization에 사용되는 엡실론 값입니다.</p>
     */
    @JsonProperty("rms_norm_eps")
    @Schema(
        description = "RMS 정규화 엡실론 값", 
        example = "1e-6",
        minimum = "1e-10",
        maximum = "1e-3"
    )
    private Double rmsNormEps;
    
    /**
     * 로프 시타 값
     * 
     * <p>Rotary Position Embedding의 시타 파라미터입니다.</p>
     */
    @JsonProperty("rope_theta")
    @Schema(
        description = "Rotary Position Embedding 시타 값", 
        example = "10000.0",
        minimum = "1.0"
    )
    private Double ropeTheta;
}
