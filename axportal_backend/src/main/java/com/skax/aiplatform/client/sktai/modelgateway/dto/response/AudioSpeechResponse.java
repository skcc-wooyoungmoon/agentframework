package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 음성 합성 응답 DTO
 * 
 * <p>Text-to-Speech API의 응답 데이터를 담는 구조입니다.
 * 생성된 오디오 데이터를 바이너리 형태로 제공합니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * AudioSpeechResponse response = modelGatewayClient.createSpeech(request);
 * byte[] audioData = response.getAudioData();
 * // 오디오 파일로 저장하거나 스트리밍
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
    description = "SKTAI 음성 합성 응답 정보",
    example = """
        {
          "audio_data": "base64_encoded_audio_data",
          "format": "mp3",
          "duration": 5.2,
          "sample_rate": 22050,
          "content_type": "audio/mpeg"
        }
        """
)
public class AudioSpeechResponse {
    
    /**
     * 생성된 오디오 데이터 (Base64 인코딩)
     */
    @JsonProperty("audio_data")
    @Schema(description = "생성된 오디오 데이터 (Base64 인코딩)", example = "UklGRjIAAABXQVZFZm10...")
    private String audioData;
    
    /**
     * 오디오 포맷
     */
    @JsonProperty("format")
    @Schema(description = "오디오 포맷 (mp3, wav, opus 등)", example = "mp3")
    private String format;
    
    /**
     * 오디오 재생 시간 (초)
     */
    @JsonProperty("duration")
    @Schema(description = "오디오 재생 시간 (초)", example = "5.2")
    private Double duration;
    
    /**
     * 샘플링 레이트
     */
    @JsonProperty("sample_rate")
    @Schema(description = "오디오 샘플링 레이트 (Hz)", example = "22050")
    private Integer sampleRate;
    
    /**
     * Content-Type
     */
    @JsonProperty("content_type")
    @Schema(description = "MIME 타입", example = "audio/mpeg")
    private String contentType;
    
    /**
     * 오디오 메타데이터 (선택적)
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 오디오 메타데이터")
    private Object metadata;
}
