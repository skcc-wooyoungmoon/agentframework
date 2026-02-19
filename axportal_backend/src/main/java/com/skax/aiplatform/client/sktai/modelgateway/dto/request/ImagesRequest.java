package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Gateway 이미지 생성 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 AI 기반 이미지 생성을 위한 요청 데이터 구조입니다.
 * 텍스트 프롬프트를 기반으로 고품질 이미지를 생성하는 DALL-E 등의 모델을 활용합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>prompt</strong>: 이미지 생성을 위한 텍스트 설명</li>
 *   <li><strong>model</strong>: 사용할 이미지 생성 모델 식별자</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>텍스트-이미지 변환 (Text-to-Image)</li>
 *   <li>다양한 해상도 지원 (256x256, 512x512, 1024x1024 등)</li>
 *   <li>배치 생성 (최대 10개)</li>
 *   <li>스타일 및 품질 조절</li>
 *   <li>안전 필터링 자동 적용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ImagesRequest request = ImagesRequest.builder()
 *     .model("dall-e-3")
 *     .prompt("A cute baby sea otter floating on water")
 *     .size("1024x1024")
 *     .quality("standard")
 *     .n(1)
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
    description = "SKTAI Model Gateway 이미지 생성 요청 정보",
    example = """
        {
          "model": "dall-e-3",
          "prompt": "A cute baby sea otter floating on water",
          "size": "1024x1024",
          "quality": "standard",
          "n": 1,
          "style": "vivid"
        }
        """
)
public class ImagesRequest {
    
    /**
     * 이미지 생성 프롬프트
     * 
     * <p>생성하고자 하는 이미지를 설명하는 텍스트입니다.
     * 구체적이고 상세한 설명일수록 원하는 결과에 가까운 이미지가 생성됩니다.</p>
     * 
     * @apiNote 프롬프트는 영어로 작성할 때 최적의 결과를 얻을 수 있습니다.
     * @implNote 최대 1000자까지 지원하며, 부적절한 내용은 자동으로 필터링됩니다.
     */
    @JsonProperty("prompt")
    @Schema(
        description = "이미지 생성을 위한 텍스트 설명 (상세할수록 좋음)",
        example = "A cute baby sea otter floating on water",
        required = true,
        maxLength = 1000
    )
    private String prompt;
    
    /**
     * 이미지 생성 모델 식별자
     * 
     * <p>사용할 이미지 생성 AI 모델의 이름입니다.
     * 각 모델마다 화질, 스타일, 생성 속도가 다릅니다.</p>
     * 
     * @implNote 지원 모델: dall-e-2, dall-e-3, stable-diffusion 등
     * @apiNote DALL-E 3가 최신 모델로 가장 높은 품질을 제공합니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 이미지 생성 모델 식별자",
        example = "dall-e-3",
        required = true
    )
    private String model;
    
    /**
     * 생성할 이미지 개수
     * 
     * <p>한 번의 요청으로 생성할 이미지의 개수입니다.
     * 여러 변형을 원할 때 사용합니다.</p>
     * 
     * @implNote 기본값: 1, 최대값: 10 (모델에 따라 다름)
     * @apiNote 개수가 많을수록 생성 시간과 비용이 증가합니다.
     */
    @JsonProperty("n")
    @Schema(
        description = "생성할 이미지 개수 (1-10)",
        example = "1",
        minimum = "1",
        maximum = "10"
    )
    private Integer n;
    
    /**
     * 이미지 크기
     * 
     * <p>생성할 이미지의 해상도를 지정합니다.
     * 큰 해상도일수록 더 선명하고 상세한 이미지가 생성됩니다.</p>
     * 
     * @implNote 지원 크기: 256x256, 512x512, 1024x1024, 1024x1792, 1792x1024
     * @apiNote 큰 해상도는 생성 시간이 더 오래 걸리고 비용이 높습니다.
     */
    @JsonProperty("size")
    @Schema(
        description = "이미지 크기 (해상도)",
        example = "1024x1024",
        allowableValues = {"256x256", "512x512", "1024x1024", "1024x1792", "1792x1024"}
    )
    private String size;
    
    /**
     * 이미지 품질
     * 
     * <p>생성할 이미지의 품질 수준을 설정합니다.
     * 높은 품질은 더 상세하고 선명한 이미지를 제공합니다.</p>
     * 
     * @implNote 옵션: standard (표준), hd (고해상도)
     * @apiNote HD 품질은 생성 시간과 비용이 더 높지만 뛰어난 결과를 제공합니다.
     */
    @JsonProperty("quality")
    @Schema(
        description = "이미지 품질 (standard 또는 hd)",
        example = "standard",
        allowableValues = {"standard", "hd"}
    )
    private String quality;
    
    /**
     * 이미지 스타일
     * 
     * <p>생성할 이미지의 전반적인 스타일과 느낌을 조절합니다.
     * 생생하고 극적인 효과 또는 자연스럽고 사실적인 효과를 선택할 수 있습니다.</p>
     * 
     * @implNote 옵션: vivid (생생한), natural (자연스러운)
     * @apiNote vivid는 더 극적이고 예술적인 결과를, natural은 사실적인 결과를 제공합니다.
     */
    @JsonProperty("style")
    @Schema(
        description = "이미지 스타일 (vivid 또는 natural)",
        example = "vivid",
        allowableValues = {"vivid", "natural"}
    )
    private String style;
    
    /**
     * 응답 포맷
     * 
     * <p>생성된 이미지의 반환 형식을 지정합니다.
     * URL 형태로 받거나 Base64 인코딩된 데이터로 받을 수 있습니다.</p>
     * 
     * @implNote 기본값: url, 옵션: url, b64_json
     * @apiNote URL 형태는 일반적으로 더 효율적이며, 브라우저에서 직접 표시 가능합니다.
     */
    @JsonProperty("response_format")
    @Schema(
        description = "응답 포맷 (url 또는 b64_json)",
        example = "url",
        allowableValues = {"url", "b64_json"}
    )
    private String responseFormat;
    
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
