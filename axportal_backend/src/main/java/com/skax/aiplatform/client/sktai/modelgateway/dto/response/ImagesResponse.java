package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 이미지 생성 응답 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 AI 기반 이미지 생성 요청에 대한 응답 데이터 구조입니다.
 * 생성된 이미지 정보와 안전 필터링 결과, 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>created</strong>: 이미지 생성 시간</li>
 *   <li><strong>data</strong>: 생성된 이미지 정보 배열</li>
 *   <li><strong>model</strong>: 사용된 이미지 생성 모델 (선택적)</li>
 * </ul>
 * 
 * <h3>이미지 정보:</h3>
 * <ul>
 *   <li>이미지 URL 또는 Base64 데이터</li>
 *   <li>수정된 프롬프트 (안전성 향상)</li>
 *   <li>콘텐츠 필터링 결과</li>
 *   <li>프롬프트 필터링 결과</li>
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
    description = "SKTAI Model Gateway 이미지 생성 응답 정보",
    example = """
        {
          "created": 1731319421,
          "data": [
            {
              "url": "https://dalleprodsec.blob.core.windows.net/private/images/770b9a56-be80-4adc-a789-cada7730c5e8/generated_00.png",
              "revised_prompt": "Adorable baby sea otter floating on water",
              "content_filter_results": {
                "hate": {"filtered": false, "severity": "safe"},
                "self_harm": {"filtered": false, "severity": "safe"},
                "sexual": {"filtered": false, "severity": "safe"},
                "violence": {"filtered": false, "severity": "safe"}
              },
              "prompt_filter_results": {
                "hate": {"filtered": false, "severity": "safe"},
                "profanity": {"detected": false, "filtered": false},
                "self_harm": {"filtered": false, "severity": "safe"},
                "sexual": {"filtered": false, "severity": "safe"},
                "violence": {"filtered": false, "severity": "safe"}
              }
            }
          ]
        }
        """
)
public class ImagesResponse {
    
    /**
     * 이미지 생성 시간
     * 
     * <p>이미지가 생성된 시간을 Unix 타임스탬프로 나타냅니다.
     * 생성 요청의 추적과 성능 분석에 사용됩니다.</p>
     */
    @JsonProperty("created")
    @Schema(description = "이미지 생성 시간 (Unix 타임스탬프)", example = "1731319421")
    private Long created;
    
    /**
     * 생성된 이미지 데이터 목록
     * 
     * <p>요청에 따라 생성된 이미지들의 정보를 담은 배열입니다.
     * 각 요소는 하나의 생성된 이미지에 대한 상세 정보를 포함합니다.</p>
     */
    @JsonProperty("data")
    @Schema(description = "생성된 이미지 정보 목록")
    private List<ImageData> data;
    
    /**
     * 사용된 모델 (선택적)
     * 
     * <p>이미지 생성에 사용된 AI 모델의 식별자입니다.
     * 일부 응답에서는 제공되지 않을 수 있습니다.</p>
     */
    @JsonProperty("model")
    @Schema(description = "사용된 이미지 생성 모델", example = "dall-e-3")
    private String model;
    
    /**
     * 개별 이미지 데이터 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 생성 이미지 정보")
    public static class ImageData {
        
        /**
         * 이미지 URL
         * 
         * <p>생성된 이미지에 접근할 수 있는 URL입니다.
         * 일반적으로 임시 URL로 제공되며 일정 시간 후 만료됩니다.</p>
         * 
         * @apiNote response_format이 "url"일 때 제공됩니다.
         * @implNote URL은 보안상 일정 시간 후 자동으로 만료됩니다.
         */
        @JsonProperty("url")
        @Schema(description = "생성된 이미지 URL", example = "https://dalleprodsec.blob.core.windows.net/private/images/770b9a56-be80-4adc-a789-cada7730c5e8/generated_00.png")
        private String url;
        
        /**
         * Base64 인코딩된 이미지 데이터
         * 
         * <p>이미지가 Base64 형태로 인코딩된 데이터입니다.
         * response_format이 "b64_json"일 때 제공됩니다.</p>
         * 
         * @apiNote 큰 이미지의 경우 상당한 크기의 문자열이 될 수 있습니다.
         */
        @JsonProperty("b64_json")
        @Schema(description = "Base64 인코딩된 이미지 데이터")
        private String b64Json;
        
        /**
         * 수정된 프롬프트
         * 
         * <p>AI가 안전성과 품질을 위해 수정한 프롬프트입니다.
         * 원본 프롬프트에서 부적절한 내용을 제거하거나 더 구체적으로 개선한 버전입니다.</p>
         * 
         * @apiNote 실제 이미지 생성에 사용된 최종 프롬프트입니다.
         * @implNote 원본과 다를 수 있으며, 일반적으로 더 상세하고 안전한 내용으로 변경됩니다.
         */
        @JsonProperty("revised_prompt")
        @Schema(description = "AI가 수정한 최종 프롬프트", example = "Adorable baby sea otter floating on water")
        private String revisedPrompt;
        
        /**
         * 콘텐츠 필터링 결과
         * 
         * <p>생성된 이미지에 대한 안전성 검사 결과입니다.
         * 다양한 카테고리별로 필터링 여부와 위험도를 제공합니다.</p>
         */
        @JsonProperty("content_filter_results")
        @Schema(description = "생성된 이미지 콘텐츠 안전성 검사 결과")
        private ContentFilterResults contentFilterResults;
        
        /**
         * 프롬프트 필터링 결과
         * 
         * <p>입력 프롬프트에 대한 안전성 검사 결과입니다.
         * 부적절한 내용이 포함되었는지 여부를 확인합니다.</p>
         */
        @JsonProperty("prompt_filter_results")
        @Schema(description = "입력 프롬프트 안전성 검사 결과")
        private PromptFilterResults promptFilterResults;
    }
    
    /**
     * 콘텐츠 필터링 결과 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "콘텐츠 안전성 필터링 결과")
    public static class ContentFilterResults {
        
        /**
         * 혐오 표현 필터링
         */
        @JsonProperty("hate")
        @Schema(description = "혐오 표현 검사 결과")
        private FilterResult hate;
        
        /**
         * 자해 관련 필터링
         */
        @JsonProperty("self_harm")
        @Schema(description = "자해 관련 검사 결과")
        private FilterResult selfHarm;
        
        /**
         * 성적 콘텐츠 필터링
         */
        @JsonProperty("sexual")
        @Schema(description = "성적 콘텐츠 검사 결과")
        private FilterResult sexual;
        
        /**
         * 폭력적 콘텐츠 필터링
         */
        @JsonProperty("violence")
        @Schema(description = "폭력적 콘텐츠 검사 결과")
        private FilterResult violence;
    }
    
    /**
     * 프롬프트 필터링 결과 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 안전성 필터링 결과")
    public static class PromptFilterResults {
        
        /**
         * 혐오 표현 필터링
         */
        @JsonProperty("hate")
        @Schema(description = "혐오 표현 검사 결과")
        private FilterResult hate;
        
        /**
         * 자해 관련 필터링
         */
        @JsonProperty("self_harm")
        @Schema(description = "자해 관련 검사 결과")
        private FilterResult selfHarm;
        
        /**
         * 성적 콘텐츠 필터링
         */
        @JsonProperty("sexual")
        @Schema(description = "성적 콘텐츠 검사 결과")
        private FilterResult sexual;
        
        /**
         * 폭력적 콘텐츠 필터링
         */
        @JsonProperty("violence")
        @Schema(description = "폭력적 콘텐츠 검사 결과")
        private FilterResult violence;
        
        /**
         * 욕설 검사 결과
         */
        @JsonProperty("profanity")
        @Schema(description = "욕설 검사 결과")
        private ProfanityResult profanity;
    }
    
    /**
     * 개별 필터링 결과 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 카테고리 필터링 결과")
    public static class FilterResult {
        
        /**
         * 필터링 여부
         * 
         * <p>해당 카테고리에서 부적절한 내용이 감지되어 필터링되었는지 여부입니다.
         * true일 경우 콘텐츠가 차단되었음을 의미합니다.</p>
         */
        @JsonProperty("filtered")
        @Schema(description = "필터링 적용 여부", example = "false")
        private Boolean filtered;
        
        /**
         * 위험도 수준
         * 
         * <p>감지된 콘텐츠의 위험도 수준입니다.
         * safe, low, medium, high 등의 값을 가질 수 있습니다.</p>
         */
        @JsonProperty("severity")
        @Schema(description = "위험도 수준", example = "safe", allowableValues = {"safe", "low", "medium", "high"})
        private String severity;
    }
    
    /**
     * 욕설 검사 결과 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "욕설 검사 결과")
    public static class ProfanityResult {
        
        /**
         * 욕설 감지 여부
         * 
         * <p>입력 프롬프트에서 욕설이 감지되었는지 여부입니다.</p>
         */
        @JsonProperty("detected")
        @Schema(description = "욕설 감지 여부", example = "false")
        private Boolean detected;
        
        /**
         * 필터링 여부
         * 
         * <p>감지된 욕설로 인해 필터링이 적용되었는지 여부입니다.</p>
         */
        @JsonProperty("filtered")
        @Schema(description = "욕설 필터링 적용 여부", example = "false")
        private Boolean filtered;
    }
}
