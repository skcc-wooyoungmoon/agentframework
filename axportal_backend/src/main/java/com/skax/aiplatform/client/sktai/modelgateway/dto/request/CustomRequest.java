package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 커스텀 엔드포인트 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 사용자 정의 API 엔드포인트 호출을 위한 요청 데이터 구조입니다.
 * 표준 API로 제공되지 않는 특수 모델이나 기능에 접근할 때 사용합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>model</strong>: 사용할 모델 식별자</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>사용자 정의 모델 접근</li>
 *   <li>실험적 기능 활용</li>
 *   <li>특수 용도 API 호출</li>
 *   <li>유연한 파라미터 구조</li>
 *   <li>모델별 맞춤 설정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CustomRequest request = CustomRequest.builder()
 *     .model("stable-diffusion-xl-base-1.0")
 *     .prompt("Please draw a rainbow.")
 *     .steps(50)
 *     .guidance_scale(7.5)
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
    description = "SKTAI Model Gateway 커스텀 엔드포인트 요청 정보",
    example = """
        {
          "model": "stable-diffusion-xl-base-1.0",
          "prompt": "Please draw a rainbow.",
          "steps": 50,
          "guidance_scale": 7.5,
          "width": 1024,
          "height": 1024,
          "seed": 42
        }
        """
)
public class CustomRequest {
    
    /**
     * 모델 식별자
     * 
     * <p>호출할 사용자 정의 모델의 이름입니다.
     * 각 모델마다 고유한 파라미터와 기능을 가집니다.</p>
     * 
     * @implNote 모델별로 지원하는 파라미터가 다르므로 문서를 참조하세요.
     * @apiNote 일부 모델은 사전 승인이나 특별한 권한이 필요할 수 있습니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 커스텀 모델 식별자",
        example = "stable-diffusion-xl-base-1.0",
        required = true
    )
    private String model;
    
    /**
     * 프롬프트 또는 입력 텍스트
     * 
     * <p>모델에 전달할 주요 입력 내용입니다.
     * 이미지 생성 모델의 경우 설명 텍스트, 텍스트 모델의 경우 프롬프트가 됩니다.</p>
     * 
     * @apiNote 모델의 특성에 따라 프롬프트 형식이 달라질 수 있습니다.
     * @implNote 일부 모델은 특수한 프롬프트 문법이나 토큰을 사용할 수 있습니다.
     */
    @JsonProperty("prompt")
    @Schema(
        description = "모델 입력 프롬프트 또는 텍스트",
        example = "Please draw a rainbow.",
        maxLength = 2000
    )
    private String prompt;
    
    /**
     * 입력 데이터 (선택적)
     * 
     * <p>프롬프트 외에 추가로 필요한 입력 데이터입니다.
     * 이미지, 오디오, 또는 구조화된 데이터 등이 될 수 있습니다.</p>
     * 
     * @apiNote 데이터 형식은 모델의 요구사항에 따라 달라집니다.
     * @implNote Base64 인코딩된 바이너리 데이터나 URL 형태로 제공할 수 있습니다.
     */
    @JsonProperty("input")
    @Schema(
        description = "추가 입력 데이터 (이미지, 오디오 등)",
        example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..."
    )
    private Object input;
    
    /**
     * 생성 스텝 수 (이미지 생성 모델용)
     * 
     * <p>이미지 생성 시 수행할 스텝의 개수입니다.
     * 더 많은 스텝은 일반적으로 더 정교한 결과를 만들지만 시간이 오래 걸립니다.</p>
     * 
     * @implNote 일반적 범위: 20~100, 기본값은 모델에 따라 다름
     * @apiNote 품질과 생성 시간의 균형을 고려하여 설정하세요.
     */
    @JsonProperty("steps")
    @Schema(
        description = "생성 스텝 수 (이미지 생성 모델용)",
        example = "50",
        minimum = "1",
        maximum = "150"
    )
    private Integer steps;
    
    /**
     * 가이던스 스케일 (이미지 생성 모델용)
     * 
     * <p>프롬프트 준수도를 조절하는 파라미터입니다.
     * 높은 값은 프롬프트를 더 엄격히 따르고, 낮은 값은 더 창의적인 결과를 만듭니다.</p>
     * 
     * @implNote 일반적 범위: 1.0~20.0, 권장값: 7.5
     * @apiNote 너무 높은 값은 아티팩트를 발생시킬 수 있습니다.
     */
    @JsonProperty("guidance_scale")
    @Schema(
        description = "가이던스 스케일 (프롬프트 준수도 조절)",
        example = "7.5",
        minimum = "1.0",
        maximum = "20.0"
    )
    private Double guidanceScale;
    
    /**
     * 이미지 너비 (픽셀)
     * 
     * <p>생성할 이미지의 너비를 픽셀 단위로 지정합니다.
     * 모델의 지원 해상도 내에서 설정해야 합니다.</p>
     * 
     * @implNote 일반적으로 8의 배수로 설정하는 것이 좋습니다.
     * @apiNote 큰 해상도는 더 많은 메모리와 시간을 필요로 합니다.
     */
    @JsonProperty("width")
    @Schema(
        description = "이미지 너비 (픽셀 단위)",
        example = "1024",
        minimum = "64",
        maximum = "2048"
    )
    private Integer width;
    
    /**
     * 이미지 높이 (픽셀)
     * 
     * <p>생성할 이미지의 높이를 픽셀 단위로 지정합니다.
     * 모델의 지원 해상도 내에서 설정해야 합니다.</p>
     * 
     * @implNote 일반적으로 8의 배수로 설정하는 것이 좋습니다.
     * @apiNote 큰 해상도는 더 많은 메모리와 시간을 필요로 합니다.
     */
    @JsonProperty("height")
    @Schema(
        description = "이미지 높이 (픽셀 단위)",
        example = "1024",
        minimum = "64",
        maximum = "2048"
    )
    private Integer height;
    
    /**
     * 생성 시드
     * 
     * <p>랜덤 생성의 시드값입니다.
     * 같은 시드를 사용하면 동일한 결과를 재현할 수 있습니다.</p>
     * 
     * @implNote -1 또는 null로 설정하면 랜덤 시드를 사용합니다.
     * @apiNote 실험이나 디버깅 시 일관된 결과를 얻기 위해 사용합니다.
     */
    @JsonProperty("seed")
    @Schema(
        description = "생성 시드 (재현 가능한 결과용)",
        example = "42"
    )
    private Long seed;
    
    /**
     * 생성할 개수
     * 
     * <p>한 번의 요청으로 생성할 결과물의 개수입니다.
     * 여러 변형을 원할 때 사용합니다.</p>
     * 
     * @implNote 기본값: 1, 최대값은 모델 및 서버 설정에 따라 다름
     * @apiNote 개수가 많을수록 비용과 처리 시간이 증가합니다.
     */
    @JsonProperty("num_outputs")
    @Schema(
        description = "생성할 결과물 개수",
        example = "1",
        minimum = "1",
        maximum = "10"
    )
    private Integer numOutputs;
    
    /**
     * 스케줄러 (이미지 생성 모델용)
     * 
     * <p>생성 과정에서 사용할 스케줄러의 종류입니다.
     * 각 스케줄러마다 생성 품질과 속도가 다릅니다.</p>
     * 
     * @implNote 지원 스케줄러: DDIM, DPM++, Euler 등
     * @apiNote 일반적으로 모델의 기본 스케줄러가 최적화되어 있습니다.
     */
    @JsonProperty("scheduler")
    @Schema(
        description = "생성 스케줄러 종류",
        example = "DPMSolverMultistep",
        allowableValues = {"DDIM", "DPMSolverMultistep", "HeunDiscrete", "EulerAncestralDiscrete", "EulerDiscrete", "PNDM"}
    )
    private String scheduler;
    
    /**
     * 기타 파라미터
     * 
     * <p>모델별로 고유한 추가 파라미터들을 포함할 수 있습니다.
     * 동적으로 다양한 설정을 전달할 때 사용합니다.</p>
     * 
     * @apiNote JSON 객체 형태로 자유롭게 구성할 수 있습니다.
     * @implNote 모델 문서를 참조하여 올바른 파라미터를 사용하세요.
     */
    @JsonProperty("extra_params")
    @Schema(
        description = "모델별 추가 파라미터",
        example = """
            {
              "sampler": "k_euler_ancestral",
              "cfg_scale": 7.0,
              "clip_skip": 2
            }
            """
    )
    private Object extraParams;
    
    /**
     * 사용자 식별자 (선택적)
     * 
     * <p>요청을 보낸 최종 사용자를 식별하는 고유 식별자입니다.
     * 모니터링, 남용 방지, 사용량 추적 등에 활용됩니다.</p>
     * 
     * @apiNote 개인정보 보호를 위해 해시값이나 익명화된 ID 사용을 권장합니다.
     */
    @JsonProperty("user")
    @Schema(
        description = "최종 사용자 식별자 (모니터링 및 남용 방지용)",
        example = "user-123",
        maxLength = 100
    )
    private String user;
}
