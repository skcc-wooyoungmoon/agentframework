package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 멀티모달 프로세서 파라미터 DTO
 * 
 * <p>멀티모달 입력(이미지, 오디오, 비디오 등)의 매핑 및 처리를 위한 오버라이드 설정입니다.
 * 비전-언어 모델이나 멀티모달 모델에서 입력 처리 방식을 커스터마이징할 때 사용합니다.</p>
 * 
 * <h3>주요 설정 항목:</h3>
 * <ul>
 *   <li><strong>num_crops</strong>: 이미지 크롭 수</li>
 *   <li><strong>image_size</strong>: 이미지 크기 설정</li>
 *   <li><strong>patch_size</strong>: 패치 크기 설정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * MultiModalProcessorKwargs kwargs = MultiModalProcessorKwargs.builder()
 *     .numCrops(4)
 *     .imageSize(224)
 *     .patchSize(16)
 *     .enableImageTokens(true)
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
    description = "멀티모달 입력 프로세서 설정",
    example = """
        {
          "num_crops": 4,
          "image_size": 224,
          "patch_size": 16,
          "enable_image_tokens": true,
          "max_video_frames": 8,
          "audio_sample_rate": 16000
        }
        """
)
public class MultiModalProcessorKwargs {
    
    /**
     * 이미지 크롭 수
     * 
     * <p>이미지 처리 시 생성할 크롭(crop)의 수입니다.
     * 더 많은 크롭은 이미지의 세부 정보를 더 잘 포착하지만 계산 비용이 증가합니다.</p>
     * 
     * @implNote 일반적으로 4-16 사이의 값을 사용합니다.
     */
    @JsonProperty("num_crops")
    @Schema(
        description = "이미지 크롭 수 (세부 정보 포착 정도)", 
        example = "4",
        minimum = "1",
        maximum = "64"
    )
    private Integer numCrops;
    
    /**
     * 이미지 크기
     * 
     * <p>입력 이미지를 리사이즈할 목표 크기입니다.
     * 정사각형 이미지로 가정하며, 너비와 높이가 동일합니다.</p>
     * 
     * @apiNote 일반적인 값: 224, 336, 448, 512
     */
    @JsonProperty("image_size")
    @Schema(
        description = "입력 이미지 크기 (픽셀, 정사각형)", 
        example = "224",
        minimum = "32",
        maximum = "2048"
    )
    private Integer imageSize;
    
    /**
     * 패치 크기
     * 
     * <p>Vision Transformer에서 사용하는 패치의 크기입니다.
     * 이미지를 작은 패치로 나누어 처리할 때 사용됩니다.</p>
     */
    @JsonProperty("patch_size")
    @Schema(
        description = "Vision Transformer 패치 크기", 
        example = "16",
        allowableValues = {"8", "16", "32"}
    )
    private Integer patchSize;
    
    /**
     * 이미지 토큰 활성화 여부
     * 
     * <p>이미지를 별도의 토큰으로 처리할지 여부입니다.
     * 활성화 시 이미지 정보가 텍스트와 별도로 토큰화됩니다.</p>
     */
    @JsonProperty("enable_image_tokens")
    @Schema(
        description = "이미지 토큰 처리 활성화 여부", 
        example = "true"
    )
    private Boolean enableImageTokens;
    
    /**
     * 최대 비디오 프레임 수
     * 
     * <p>비디오 입력 시 처리할 최대 프레임 수입니다.
     * 긴 비디오의 경우 균등하게 샘플링됩니다.</p>
     */
    @JsonProperty("max_video_frames")
    @Schema(
        description = "최대 비디오 프레임 수", 
        example = "8",
        minimum = "1",
        maximum = "64"
    )
    private Integer maxVideoFrames;
    
    /**
     * 오디오 샘플링 레이트
     * 
     * <p>오디오 입력의 샘플링 레이트(Hz)입니다.
     * 오디오 품질과 처리 속도에 영향을 줍니다.</p>
     */
    @JsonProperty("audio_sample_rate")
    @Schema(
        description = "오디오 샘플링 레이트 (Hz)", 
        example = "16000",
        allowableValues = {"8000", "16000", "22050", "44100", "48000"}
    )
    private Integer audioSampleRate;
    
    /**
     * 최대 오디오 길이
     * 
     * <p>처리할 수 있는 최대 오디오 길이(초)입니다.
     * 긴 오디오는 자동으로 분할되거나 압축됩니다.</p>
     */
    @JsonProperty("max_audio_length")
    @Schema(
        description = "최대 오디오 길이 (초)", 
        example = "30",
        minimum = "1",
        maximum = "300"
    )
    private Integer maxAudioLength;
    
    /**
     * 이미지 정규화 평균값
     * 
     * <p>이미지 전처리 시 사용할 정규화 평균값입니다.
     * RGB 채널별로 설정할 수 있습니다.</p>
     */
    @JsonProperty("image_mean")
    @Schema(
        description = "이미지 정규화 평균값 [R, G, B]", 
        example = "[0.485, 0.456, 0.406]"
    )
    private java.util.List<Double> imageMean;
    
    /**
     * 이미지 정규화 표준편차
     * 
     * <p>이미지 전처리 시 사용할 정규화 표준편차입니다.
     * RGB 채널별로 설정할 수 있습니다.</p>
     */
    @JsonProperty("image_std")
    @Schema(
        description = "이미지 정규화 표준편차 [R, G, B]", 
        example = "[0.229, 0.224, 0.225]"
    )
    private java.util.List<Double> imageStd;
    
    /**
     * 토큰 병합 전략
     * 
     * <p>멀티모달 토큰을 텍스트 토큰과 병합하는 전략입니다.</p>
     * 
     * @apiNote 가능한 값: "concat", "interleave", "attention"
     */
    @JsonProperty("token_merge_strategy")
    @Schema(
        description = "멀티모달 토큰 병합 전략", 
        example = "concat",
        allowableValues = {"concat", "interleave", "attention"}
    )
    private String tokenMergeStrategy;
    
    /**
     * 시간 축 다운샘플링 팩터
     * 
     * <p>비디오나 오디오의 시간 축을 다운샘플링하는 팩터입니다.
     * 처리 속도 향상과 메모리 절약에 사용됩니다.</p>
     */
    @JsonProperty("temporal_downsample_factor")
    @Schema(
        description = "시간 축 다운샘플링 팩터", 
        example = "2",
        minimum = "1",
        maximum = "16"
    )
    private Integer temporalDownsampleFactor;
}
