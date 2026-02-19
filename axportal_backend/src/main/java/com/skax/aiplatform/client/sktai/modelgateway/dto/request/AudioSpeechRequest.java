package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 음성 합성 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 텍스트-음성 변환(TTS)을 위한 요청 데이터 구조입니다.
 * 입력된 텍스트를 자연스러운 음성으로 변환하여 오디오 파일을 생성합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>model</strong>: 사용할 TTS 모델 식별자</li>
 *   <li><strong>input</strong>: 음성으로 변환할 텍스트</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>고품질 음성 합성 (TTS)</li>
 *   <li>다양한 음성 캐릭터 지원</li>
 *   <li>속도 조절 (0.25x ~ 4.0x)</li>
 *   <li>다양한 오디오 포맷 지원 (MP3, WAV, FLAC 등)</li>
 *   <li>실시간 스트리밍 지원</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AudioSpeechRequest request = AudioSpeechRequest.builder()
 *     .model("tts-1")
 *     .input("The quick brown fox jumped over the lazy dog.")
 *     .voice("alloy")
 *     .responseFormat("mp3")
 *     .speed(1.0)
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
    description = "SKTAI Model Gateway 음성 합성 요청 정보",
    example = """
        {
          "model": "tts-1",
          "input": "The quick brown fox jumped over the lazy dog.",
          "voice": "alloy",
          "response_format": "mp3",
          "speed": 1.0
        }
        """
)
public class AudioSpeechRequest {
    
    /**
     * TTS 모델 식별자
     * 
     * <p>사용할 텍스트-음성 변환 모델의 이름입니다.
     * 각 모델마다 음질, 속도, 지원 언어가 다릅니다.</p>
     * 
     * @implNote 지원 모델: tts-1 (빠른 속도), tts-1-hd (고품질)
     * @apiNote tts-1-hd는 더 자연스러운 음성을 제공하지만 처리 시간이 더 길 수 있습니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 TTS 모델 식별자",
        example = "tts-1",
        required = true,
        allowableValues = {"tts-1", "tts-1-hd"}
    )
    private String model;
    
    /**
     * 음성으로 변환할 텍스트
     * 
     * <p>TTS로 변환할 입력 텍스트입니다.
     * 자연스러운 발음을 위해 문장 부호와 띄어쓰기를 정확히 사용하는 것이 좋습니다.</p>
     * 
     * @apiNote 최대 4096자까지 지원하며, 긴 텍스트는 자동으로 분할 처리됩니다.
     * @implNote 여러 언어를 지원하지만 영어에서 최적의 품질을 제공합니다.
     */
    @JsonProperty("input")
    @Schema(
        description = "음성으로 변환할 텍스트 (최대 4096자)",
        example = "The quick brown fox jumped over the lazy dog.",
        required = true,
        maxLength = 4096
    )
    private String input;
    
    /**
     * 음성 캐릭터
     * 
     * <p>생성할 음성의 캐릭터를 선택합니다.
     * 각 음성은 고유한 톤, 성별, 연령대의 특성을 가집니다.</p>
     * 
     * @implNote 지원 음성: alloy (중성적), echo (남성적), fable (영국 억양), onyx (깊은 남성), nova (젊은 여성), shimmer (부드러운 여성)
     * @apiNote 기본값: alloy - 가장 균형 잡힌 범용적인 음성입니다.
     */
    @JsonProperty("voice")
    @Schema(
        description = "음성 캐릭터 선택",
        example = "alloy",
        allowableValues = {"alloy", "echo", "fable", "onyx", "nova", "shimmer"}
    )
    private String voice;
    
    /**
     * 오디오 응답 포맷
     * 
     * <p>생성될 오디오 파일의 포맷을 지정합니다.
     * 용도와 품질 요구사항에 따라 적절한 포맷을 선택할 수 있습니다.</p>
     * 
     * @implNote 지원 포맷: mp3 (압축, 일반용), opus (웹 스트리밍), aac (모바일), flac (무손실), wav (무압축), pcm (원본)
     * @apiNote 기본값: mp3 - 파일 크기와 품질의 균형이 좋습니다.
     */
    @JsonProperty("response_format")
    @Schema(
        description = "오디오 파일 포맷",
        example = "mp3",
        allowableValues = {"mp3", "opus", "aac", "flac", "wav", "pcm"}
    )
    private String responseFormat;
    
    /**
     * 재생 속도
     * 
     * <p>생성된 음성의 재생 속도를 조절합니다.
     * 1.0이 정상 속도이며, 0.25~4.0 범위에서 조절 가능합니다.</p>
     * 
     * @implNote 범위: 0.25 (매우 느림) ~ 4.0 (매우 빠름)
     * @apiNote 극단적인 속도 변경 시 음질이 저하될 수 있습니다.
     */
    @JsonProperty("speed")
    @Schema(
        description = "재생 속도 (0.25 ~ 4.0배)",
        example = "1.0",
        minimum = "0.25",
        maximum = "4.0"
    )
    private Double speed;
    
    /**
     * 음성 지시사항 (선택적)
     * 
     * <p>음성 합성에 대한 추가 지시사항이나 컨텍스트를 제공합니다.
     * 감정, 톤, 발음 스타일 등을 조절할 때 사용됩니다.</p>
     * 
     * @apiNote 예: "Talk like a sympathetic customer service agent"
     * @implNote 모든 모델에서 지원되지 않을 수 있으며, 실험적 기능입니다.
     */
    @JsonProperty("instruction")
    @Schema(
        description = "음성 합성 지시사항 (감정, 톤 조절)",
        example = "Talk like a sympathetic customer service agent",
        maxLength = 500
    )
    private String instruction;
}
