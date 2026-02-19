package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 양자화 설정 DTO
 * 
 * <p>모델 양자화 관련 설정을 정의하는 DTO입니다.
 * 메모리 사용량 감소와 추론 속도 향상을 위한 양자화 옵션을 제공합니다.</p>
 * 
 * <h3>주요 양자화 방법:</h3>
 * <ul>
 *   <li><strong>INT8</strong>: 8비트 정수 양자화</li>
 *   <li><strong>FP16</strong>: 16비트 부동소수점</li>
 *   <li><strong>GPTQ</strong>: GPT 기반 양자화</li>
 *   <li><strong>AWQ</strong>: Activation-aware Weight Quantization</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * QuantizationConfig config = QuantizationConfig.builder()
 *     .method("INT8")
 *     .enabled(true)
 *     .calibrationDataset("c4")
 *     .groupSize(128)
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
    description = "모델 양자화 설정",
    example = """
        {
          "method": "INT8",
          "enabled": true,
          "calibration_dataset": "c4",
          "group_size": 128,
          "activation_scheme": "dynamic",
          "weight_bits": 8,
          "activation_bits": 8
        }
        """
)
public class QuantizationConfig {
    
    /**
     * 양자화 방법
     * 
     * <p>사용할 양자화 알고리즘을 지정합니다.</p>
     * 
     * @apiNote 지원되는 방법: "INT8", "FP16", "GPTQ", "AWQ", "GGML", "GGUF"
     */
    @JsonProperty("method")
    @Schema(
        description = "양자화 방법/알고리즘", 
        example = "INT8",
        allowableValues = {"INT8", "FP16", "GPTQ", "AWQ", "GGML", "GGUF", "SmoothQuant"}
    )
    private String method;
    
    /**
     * 양자화 활성화 여부
     * 
     * <p>양자화를 활성화할지 여부입니다.
     * 비활성화 시 원본 정밀도로 모델을 실행합니다.</p>
     */
    @JsonProperty("enabled")
    @Schema(
        description = "양자화 활성화 여부", 
        example = "true"
    )
    private Boolean enabled;
    
    /**
     * 캘리브레이션 데이터셋
     * 
     * <p>양자화 캘리브레이션에 사용할 데이터셋입니다.
     * 정적 양자화에서 최적의 스케일링 팩터를 찾는 데 사용됩니다.</p>
     */
    @JsonProperty("calibration_dataset")
    @Schema(
        description = "양자화 캘리브레이션 데이터셋", 
        example = "c4",
        allowableValues = {"c4", "wikitext", "ptb", "custom"}
    )
    private String calibrationDataset;
    
    /**
     * 그룹 크기
     * 
     * <p>가중치 양자화에서 사용하는 그룹의 크기입니다.
     * 작을수록 더 정확하지만 메모리 사용량이 증가합니다.</p>
     */
    @JsonProperty("group_size")
    @Schema(
        description = "가중치 양자화 그룹 크기", 
        example = "128",
        allowableValues = {"32", "64", "128", "256"}
    )
    private Integer groupSize;
    
    /**
     * 활성화 양자화 스킴
     * 
     * <p>활성화 값의 양자화 방식입니다.</p>
     */
    @JsonProperty("activation_scheme")
    @Schema(
        description = "활성화 양자화 스킴", 
        example = "dynamic",
        allowableValues = {"static", "dynamic", "none"}
    )
    private String activationScheme;
    
    /**
     * 가중치 비트 수
     * 
     * <p>가중치를 표현하는 데 사용할 비트 수입니다.
     * 낮을수록 더 압축되지만 정확도가 감소할 수 있습니다.</p>
     */
    @JsonProperty("weight_bits")
    @Schema(
        description = "가중치 양자화 비트 수", 
        example = "8",
        allowableValues = {"1", "2", "4", "8", "16"}
    )
    private Integer weightBits;
    
    /**
     * 활성화 비트 수
     * 
     * <p>활성화 값을 표현하는 데 사용할 비트 수입니다.</p>
     */
    @JsonProperty("activation_bits")
    @Schema(
        description = "활성화 양자화 비트 수", 
        example = "8",
        allowableValues = {"8", "16", "32"}
    )
    private Integer activationBits;
    
    /**
     * 대칭 양자화 여부
     * 
     * <p>대칭 양자화를 사용할지 여부입니다.
     * 대칭 양자화는 0을 중심으로 대칭적인 범위를 사용합니다.</p>
     */
    @JsonProperty("symmetric")
    @Schema(
        description = "대칭 양자화 사용 여부", 
        example = "true"
    )
    private Boolean symmetric;
    
    /**
     * 채널별 양자화 여부
     * 
     * <p>채널(또는 축)별로 독립적인 양자화 파라미터를 사용할지 여부입니다.
     * 더 정확하지만 계산 비용이 증가합니다.</p>
     */
    @JsonProperty("per_channel")
    @Schema(
        description = "채널별 양자화 사용 여부", 
        example = "true"
    )
    private Boolean perChannel;
    
    /**
     * 캘리브레이션 샘플 수
     * 
     * <p>양자화 캘리브레이션에 사용할 샘플 수입니다.
     * 더 많은 샘플은 더 정확한 양자화를 제공하지만 시간이 오래 걸립니다.</p>
     */
    @JsonProperty("calibration_samples")
    @Schema(
        description = "캘리브레이션 샘플 수", 
        example = "512",
        minimum = "32",
        maximum = "10000"
    )
    private Integer calibrationSamples;
    
    /**
     * 캘리브레이션 시퀀스 길이
     * 
     * <p>캘리브레이션에 사용할 입력 시퀀스의 길이입니다.</p>
     */
    @JsonProperty("calibration_seq_length")
    @Schema(
        description = "캘리브레이션 시퀀스 길이", 
        example = "2048",
        minimum = "128",
        maximum = "8192"
    )
    private Integer calibrationSeqLength;
    
    /**
     * 혼합 정밀도 사용 여부
     * 
     * <p>일부 레이어는 높은 정밀도를 유지하는 혼합 정밀도를 사용할지 여부입니다.
     * 중요한 레이어의 정확도를 보존하면서 전체적인 압축 효과를 얻을 수 있습니다.</p>
     */
    @JsonProperty("mixed_precision")
    @Schema(
        description = "혼합 정밀도 사용 여부", 
        example = "false"
    )
    private Boolean mixedPrecision;
    
    /**
     * KV 캐시 양자화 여부
     * 
     * <p>Key-Value 캐시를 양자화할지 여부입니다.
     * 메모리 사용량을 크게 줄일 수 있지만 품질에 영향을 줄 수 있습니다.</p>
     */
    @JsonProperty("quantize_kv_cache")
    @Schema(
        description = "KV 캐시 양자화 여부", 
        example = "false"
    )
    private Boolean quantizeKvCache;
}
