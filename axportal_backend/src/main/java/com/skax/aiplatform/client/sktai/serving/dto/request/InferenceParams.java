package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 추론 파라미터 DTO
 * 
 * <p>모델 추론 시 사용되는 상세 파라미터 설정입니다.
 * 텍스트 생성, 샘플링, 디코딩 등의 추론 동작을 제어합니다.</p>
 * 
 * <h3>주요 설정 영역:</h3>
 * <ul>
 *   <li><strong>샘플링 제어</strong>: temperature, top_p, top_k</li>
 *   <li><strong>길이 제어</strong>: max_tokens, min_length</li>
 *   <li><strong>반복 제어</strong>: repetition_penalty, frequency_penalty</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * InferenceParams params = InferenceParams.builder()
 *     .maxTokens(1024)
 *     .temperature(0.7)
 *     .topP(0.9)
 *     .repetitionPenalty(1.1)
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
    description = "모델 추론 파라미터 설정",
    example = """
        {
          "max_tokens": 1024,
          "temperature": 0.7,
          "top_p": 0.9,
          "top_k": 50,
          "repetition_penalty": 1.1,
          "frequency_penalty": 0.0,
          "presence_penalty": 0.0,
          "stop_sequences": ["\\n\\n", "</response>"]
        }
        """
)
public class InferenceParams {
    
    /**
     * 최대 토큰 수
     * 
     * <p>생성할 수 있는 최대 토큰 수입니다.
     * 응답 길이를 제한하고 처리 시간을 예측 가능하게 만듭니다.</p>
     * 
     * @implNote 모델의 최대 컨텍스트 길이를 초과할 수 없습니다.
     */
    @JsonProperty("max_tokens")
    @Schema(
        description = "생성할 최대 토큰 수", 
        example = "1024",
        minimum = "1",
        maximum = "32768"
    )
    private Integer maxTokens;
    
    /**
     * 최소 토큰 수
     * 
     * <p>생성해야 하는 최소 토큰 수입니다.
     * 너무 짧은 응답을 방지할 때 사용합니다.</p>
     */
    @JsonProperty("min_tokens")
    @Schema(
        description = "생성할 최소 토큰 수", 
        example = "10",
        minimum = "0"
    )
    private Integer minTokens;
    
    /**
     * Temperature 값
     * 
     * <p>샘플링의 무작위성을 조절하는 파라미터입니다.
     * 낮을수록 더 결정적이고 일관된 출력을 생성합니다.</p>
     * 
     * @apiNote 0.0은 탐욕적 디코딩, 1.0은 원본 분포 사용
     */
    @JsonProperty("temperature")
    @Schema(
        description = "샘플링 Temperature (무작위성 조절)", 
        example = "0.7",
        minimum = "0.0",
        maximum = "2.0"
    )
    private Double temperature;
    
    /**
     * Top-p 값 (Nucleus Sampling)
     * 
     * <p>누적 확률이 이 값 이하인 토큰들만 고려하는 샘플링 방법입니다.
     * 응답의 다양성과 품질의 균형을 조절합니다.</p>
     */
    @JsonProperty("top_p")
    @Schema(
        description = "Top-p 누적 확률 임계값 (Nucleus Sampling)", 
        example = "0.9",
        minimum = "0.0",
        maximum = "1.0"
    )
    private Double topP;
    
    /**
     * Top-k 값
     * 
     * <p>확률이 높은 상위 k개의 토큰만 고려하는 샘플링 방법입니다.
     * 선택지를 제한하여 더 일관된 출력을 생성합니다.</p>
     */
    @JsonProperty("top_k")
    @Schema(
        description = "Top-k 상위 토큰 수", 
        example = "50",
        minimum = "1",
        maximum = "1000"
    )
    private Integer topK;
    
    /**
     * 반복 패널티
     * 
     * <p>이미 생성된 토큰의 반복을 억제하는 패널티입니다.
     * 1.0보다 클수록 반복을 더 강하게 억제합니다.</p>
     */
    @JsonProperty("repetition_penalty")
    @Schema(
        description = "반복 억제 패널티", 
        example = "1.1",
        minimum = "0.1",
        maximum = "2.0"
    )
    private Double repetitionPenalty;
    
    /**
     * 빈도 패널티
     * 
     * <p>토큰의 빈도에 따른 패널티입니다.
     * 양수일 때 반복되는 토큰에 패널티를 부여합니다.</p>
     */
    @JsonProperty("frequency_penalty")
    @Schema(
        description = "빈도 기반 패널티", 
        example = "0.0",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double frequencyPenalty;
    
    /**
     * 존재 패널티
     * 
     * <p>이미 등장한 토큰에 대한 패널티입니다.
     * 양수일 때 새로운 주제로의 전환을 촉진합니다.</p>
     */
    @JsonProperty("presence_penalty")
    @Schema(
        description = "토큰 존재 패널티", 
        example = "0.0",
        minimum = "-2.0",
        maximum = "2.0"
    )
    private Double presencePenalty;
    
    /**
     * 정지 시퀀스
     * 
     * <p>생성을 중단할 문자열 시퀀스 목록입니다.
     * 이 중 하나라도 생성되면 텍스트 생성이 중단됩니다.</p>
     */
    @JsonProperty("stop_sequences")
    @Schema(
        description = "생성 중단 시퀀스 목록", 
        example = "[\"\\n\\n\", \"</response>\", \"[END]\"]"
    )
    private java.util.List<String> stopSequences;
    
    /**
     * 시드 값
     * 
     * <p>난수 생성을 위한 시드 값입니다.
     * 동일한 시드로 동일한 입력을 제공하면 재현 가능한 결과를 얻을 수 있습니다.</p>
     */
    @JsonProperty("seed")
    @Schema(
        description = "난수 생성 시드 (재현성 보장)", 
        example = "42"
    )
    private Long seed;
    
    /**
     * 빔 서치 크기
     * 
     * <p>빔 서치 디코딩에서 유지할 빔의 수입니다.
     * 1이면 탐욕적 디코딩, 큰 값일수록 더 다양한 후보를 고려합니다.</p>
     */
    @JsonProperty("num_beams")
    @Schema(
        description = "빔 서치 빔 수", 
        example = "1",
        minimum = "1",
        maximum = "20"
    )
    private Integer numBeams;
    
    /**
     * Early Stopping 활성화 여부
     * 
     * <p>빔 서치에서 조기 종료를 활성화할지 여부입니다.
     * 활성화 시 충분히 좋은 결과를 찾으면 일찍 종료합니다.</p>
     */
    @JsonProperty("early_stopping")
    @Schema(
        description = "빔 서치 조기 종료 활성화 여부", 
        example = "false"
    )
    private Boolean earlyStopping;
    
    /**
     * 길이 패널티
     * 
     * <p>생성된 시퀀스의 길이에 대한 패널티입니다.
     * 양수일 때 더 긴 시퀀스를 선호합니다.</p>
     */
    @JsonProperty("length_penalty")
    @Schema(
        description = "시퀀스 길이 패널티", 
        example = "1.0",
        minimum = "0.0",
        maximum = "2.0"
    )
    private Double lengthPenalty;
    
    /**
     * No Repeat N-gram 크기
     * 
     * <p>반복을 금지할 N-gram의 크기입니다.
     * 예를 들어, 3이면 연속된 3개 토큰의 반복을 금지합니다.</p>
     */
    @JsonProperty("no_repeat_ngram_size")
    @Schema(
        description = "반복 금지 N-gram 크기", 
        example = "3",
        minimum = "0",
        maximum = "10"
    )
    private Integer noRepeatNgramSize;
}
