package com.skax.aiplatform.client.sktai.serving.dto.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 추론 파라미터 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 추론 시 사용되는 파라미터들입니다.
 * 텍스트 생성, 채팅, 임베딩 등 다양한 추론 작업의 동작을 제어합니다.</p>
 * 
 * <h3>주요 파라미터:</h3>
 * <ul>
 *   <li><strong>temperature</strong>: 생성 텍스트의 창의성 조절</li>
 *   <li><strong>max_tokens</strong>: 최대 생성 토큰 수</li>
 *   <li><strong>top_p</strong>: 누적 확률 기반 토큰 선택</li>
 *   <li><strong>top_k</strong>: 상위 K개 토큰에서 선택</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * InferenceParam params = InferenceParam.builder()
 *     .temperature(0.7)
 *     .maxTokens(512)
 *     .topP(0.9)
 *     .topK(50)
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
    description = "SKTAI 모델 추론 파라미터",
    example = """
        {
          "temperature": 0.7,
          "max_tokens": 512,
          "top_p": 0.9,
          "top_k": 50,
          "repetition_penalty": 1.0,
          "stop": ["\\n\\n"],
          "presence_penalty": 0.0,
          "frequency_penalty": 0.0
        }
        """
)
public class InferenceParam {
    
    /**
     * 생성 온도
     * 
     * <p>생성된 텍스트의 창의성을 조절하는 파라미터입니다.
     * 낮은 값(0.1)은 일관성 있는 텍스트를, 높은 값(1.0)은 창의적인 텍스트를 생성합니다.</p>
     */
    @JsonProperty("temperature")
    @Schema(
        description = "생성 텍스트의 창의성 조절 (0.0-2.0)",
        example = "0.7",
        minimum = "0.0",
        maximum = "2.0"
    )
    private Double temperature;
    
    /**
     * 최대 토큰 수
     * 
     * <p>모델이 생성할 수 있는 최대 토큰 수입니다.
     * 응답의 길이를 제한하고 비용을 제어하는 데 사용됩니다.</p>
     */
    @JsonProperty("max_tokens")
    @Schema(
        description = "최대 생성 토큰 수",
        example = "512",
        minimum = "1",
        maximum = "8192"
    )
    private Integer maxTokens;
    
    /**
     * Top-p (nucleus) 샘플링
     * 
     * <p>누적 확률이 p 이하인 토큰들 중에서만 선택합니다.
     * 값이 작을수록 더 일관성 있는 응답을 생성합니다.</p>
     */
    @JsonProperty("top_p")
    @Schema(
        description = "누적 확률 기반 토큰 선택 (0.0-1.0)",
        example = "0.9",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double topP;
    
    /**
     * Top-k 샘플링
     * 
     * <p>가장 높은 확률을 가진 상위 k개 토큰 중에서만 선택합니다.
     * 값이 작을수록 더 예측 가능한 응답을 생성합니다.</p>
     */
    @JsonProperty("top_k")
    @Schema(
        description = "상위 K개 토큰에서 선택",
        example = "50",
        minimum = "1",
        maximum = "100"
    )
    private Integer topK;
    
    /**
     * 반복 패널티
     * 
     * <p>이미 생성된 토큰의 반복을 방지하는 패널티입니다.
     * 1.0보다 큰 값은 반복을 줄이고, 작은 값은 반복을 허용합니다.</p>
     */
    @JsonProperty("repetition_penalty")
    @Schema(
        description = "토큰 반복 방지 패널티 (0.0-2.0)",
        example = "1.0",
        minimum = "0.0",
        maximum = "2.0"
    )
    private Double repetitionPenalty;
    
    /**
     * 중지 시퀀스
     * 
     * <p>생성을 중지할 문자열 시퀀스들입니다.
     * 이 시퀀스를 만나면 텍스트 생성이 종료됩니다.</p>
     */
    @JsonProperty("stop")
    @Schema(
        description = "생성 중지 시퀀스 목록",
        example = "[\"\\n\\n\", \"Human:\", \"AI:\"]"
    )
    private List<String> stop;
    
    /**
     * 존재 패널티
     * 
     * <p>새로운 토큰의 등장을 장려하는 패널티입니다.
     * 양수 값은 새로운 내용을 생성하도록 장려합니다.</p>
     */
    @JsonProperty("presence_penalty")
    @Schema(
        description = "새로운 토큰 등장 장려 패널티 (-2.0-2.0)",
        example = "0.0",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double presencePenalty;
    
    /**
     * 빈도 패널티
     * 
     * <p>토큰의 빈도에 기반한 패널티입니다.
     * 자주 사용된 토큰의 재사용을 줄입니다.</p>
     */
    @JsonProperty("frequency_penalty")
    @Schema(
        description = "토큰 빈도 기반 패널티 (-2.0-2.0)",
        example = "0.0",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double frequencyPenalty;
    
    /**
     * 시드 값
     * 
     * <p>재현 가능한 결과를 위한 랜덤 시드 값입니다.
     * 같은 시드와 파라미터로는 같은 결과를 얻을 수 있습니다.</p>
     */
    @JsonProperty("seed")
    @Schema(
        description = "재현 가능한 결과를 위한 랜덤 시드",
        example = "42"
    )
    private Integer seed;
}