package com.skax.aiplatform.client.sktai.serving.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 모델 양자화 설정 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 양자화 옵션을 설정하기 위한 구조입니다.
 * 양자화를 통해 모델 크기를 줄이고 추론 속도를 향상시킬 수 있습니다.</p>
 * 
 * <h3>지원하는 양자화 타입:</h3>
 * <ul>
 *   <li><strong>awq</strong>: Activation-aware Weight Quantization</li>
 *   <li><strong>gptq</strong>: GPTQ (GPT Quantization)</li>
 *   <li><strong>bitsandbytes</strong>: 8bit 및 4bit 양자화</li>
 *   <li><strong>fp8</strong>: FP8 형식 양자화</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * Quantization quantization = Quantization.builder()
 *     .method("awq")
 *     .bits(4)
 *     .groupSize(128)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 모델 양자화 설정",
    example = """
        {
          "method": "awq",
          "bits": 4,
          "group_size": 128,
          "desc_act": false
        }
        """
)
public class Quantization {
    
    /**
     * 양자화 방식
     * 
     * <p>사용할 양자화 알고리즘을 지정합니다.
     * 각 방식은 서로 다른 성능 특성과 호환성을 가집니다.</p>
     */
    @JsonProperty("method")
    @Schema(
        description = "양자화 알고리즘 (awq, gptq, bitsandbytes, fp8)",
        example = "awq",
        allowableValues = {"awq", "gptq", "bitsandbytes", "fp8"}
    )
    private String method;
    
    /**
     * 양자화 비트 수
     * 
     * <p>가중치를 표현하는 데 사용할 비트 수입니다.
     * 낮은 비트 수일수록 모델 크기가 작아지지만 정확도가 감소할 수 있습니다.</p>
     */
    @JsonProperty("bits")
    @Schema(
        description = "양자화 비트 수 (일반적으로 4, 8)",
        example = "4",
        minimum = "1",
        maximum = "32"
    )
    private Integer bits;
    
    /**
     * 그룹 크기
     * 
     * <p>양자화 그룹의 크기입니다.
     * 작은 그룹 크기는 더 정확한 양자화를 제공하지만 메모리 사용량이 증가합니다.</p>
     */
    @JsonProperty("group_size")
    @Schema(
        description = "양자화 그룹 크기",
        example = "128"
    )
    private Integer groupSize;
    
    /**
     * Activation Quantization 사용 여부
     * 
     * <p>활성화 함수도 양자화할지 여부입니다.
     * true로 설정하면 더 많은 메모리를 절약할 수 있지만 계산 복잡도가 증가합니다.</p>
     */
    @JsonProperty("desc_act")
    @Schema(
        description = "Activation Quantization 사용 여부",
        example = "false"
    )
    private Boolean descAct;
    
    /**
     * 대칭 양자화 사용 여부
     * 
     * <p>대칭적 양자화를 사용할지 여부입니다.
     * 대칭 양자화는 0을 중심으로 균등하게 분포된 양자화 레벨을 사용합니다.</p>
     */
    @JsonProperty("symmetric")
    @Schema(
        description = "대칭 양자화 사용 여부",
        example = "true"
    )
    private Boolean symmetric;
    
    /**
     * 체크포인트 이름
     * 
     * <p>사전 양자화된 모델의 체크포인트 이름입니다.
     * 이미 양자화된 모델을 로드할 때 사용합니다.</p>
     */
    @JsonProperty("checkpoint_name")
    @Schema(
        description = "사전 양자화된 모델의 체크포인트 이름",
        example = "model_quantized_awq"
    )
    private String checkpointName;
}