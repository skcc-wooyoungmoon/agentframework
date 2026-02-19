package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 음성 인식 응답 DTO
 * 
 * <p>Speech-to-Text API의 응답 데이터를 담는 구조입니다.
 * 오디오 파일에서 변환된 텍스트와 관련 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>text</strong>: 변환된 텍스트</li>
 *   <li><strong>language</strong>: 감지된 언어</li>
 *   <li><strong>duration</strong>: 오디오 재생 시간</li>
 *   <li><strong>confidence</strong>: 인식 신뢰도</li>
 * </ul>
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
    description = "SKTAI 음성 인식 응답 정보",
    example = """
        {
          "text": "안녕하세요, 음성 인식 테스트입니다.",
          "language": "ko",
          "duration": 3.5,
          "confidence": 0.95,
          "segments": [
            {
              "start": 0.0,
              "end": 1.2,
              "text": "안녕하세요,"
            }
          ]
        }
        """
)
public class AudioTranscriptionResponse {
    
    /**
     * 변환된 텍스트
     */
    @JsonProperty("text")
    @Schema(description = "음성에서 변환된 텍스트", example = "안녕하세요, 음성 인식 테스트입니다.")
    private String text;
    
    /**
     * 감지된 언어 코드
     */
    @JsonProperty("language")
    @Schema(description = "감지된 언어 코드 (ISO 639-1)", example = "ko")
    private String language;
    
    /**
     * 오디오 재생 시간 (초)
     */
    @JsonProperty("duration")
    @Schema(description = "오디오 재생 시간 (초)", example = "3.5")
    private Double duration;
    
    /**
     * 인식 신뢰도 (0.0 ~ 1.0)
     */
    @JsonProperty("confidence")
    @Schema(description = "인식 신뢰도 (0.0 ~ 1.0)", example = "0.95")
    private Double confidence;
    
    /**
     * 세그먼트별 인식 결과 (선택적)
     */
    @JsonProperty("segments")
    @Schema(description = "세그먼트별 인식 결과")
    private Object segments;
    
    /**
     * 추가 메타데이터 (선택적)
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Object metadata;
}
