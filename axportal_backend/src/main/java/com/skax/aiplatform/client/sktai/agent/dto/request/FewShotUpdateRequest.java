package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 수정 요청 DTO
 * 
 * <p>기존 Few-Shot Learning 세트를 수정하기 위한 요청 데이터 구조입니다.
 * Few-Shot의 기본 정보, 예제 데이터, 태그를 업데이트할 수 있으며, 
 * 수정 시 새로운 버전이 생성됩니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명</li>
 *   <li><strong>예제 데이터</strong>: 입력-출력 예제 쌍들</li>
 *   <li><strong>태그</strong>: 분류 및 검색을 위한 태그</li>
 * </ul>
 * 
 * <h3>버전 관리:</h3>
 * <ul>
 *   <li>수정 시 자동으로 새로운 버전 생성</li>
 *   <li>기존 버전은 보존되어 이력 관리</li>
 *   <li>최신 버전이 기본 사용 버전으로 설정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotUpdateRequest request = FewShotUpdateRequest.builder()
 *     .name("Enhanced Customer Sentiment Analysis")
 *     .description("개선된 고객 리뷰 감정 분석 Few-Shot 예제 세트")
 *     .examples(Arrays.asList(
 *         FewShotExample.builder()
 *             .input("이 제품은 기대 이상입니다! 정말 추천하고 싶어요.")
 *             .output("positive")
 *             .explanation("강한 긍정적 감정과 추천 의도를 포함")
 *             .build(),
 *         FewShotExample.builder()
 *             .input("품질이 떨어지고 가격도 비쌉니다.")
 *             .output("negative")
 *             .explanation("품질과 가격에 대한 불만족 표현")
 *             .build(),
 *         FewShotExample.builder()
 *             .input("그냥 평범한 제품입니다.")
 *             .output("neutral")
 *             .explanation("객관적이고 중립적인 평가")
 *             .build()
 *     ))
 *     .tags(Arrays.asList(
 *         FewShotTag.builder().tag("sentiment-analysis").build(),
 *         FewShotTag.builder().tag("enhanced").build(),
 *         FewShotTag.builder().tag("korean-text").build()
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see FewShotCreateRequest Few-Shot 생성 요청 (동일한 구조)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 수정 요청 정보",
    example = """
        {
          "new_name": "Enhanced Customer Sentiment Analysis",
          "items": [
            {
              "item_answer": "이 제품은 정말 만족스럽습니다. 품질이 우수해요!",
              "item_query": "positive"
            },
            {
              "item_answer": "품질이 떨어지고 가격도 비쌉니다.",
              "item_query": "negative"
            }
          ],
          "tags": [
            {
              "tag": "sentiment-analysis"
            },
            {
              "tag": "enhanced"
            }
          ]
        }
        """
)
public class FewShotUpdateRequest {
    
    /**
     * Few-Shot 이름
     * 
     * <p>수정할 Few-Shot의 새로운 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, Few-Shot의 목적을 명확히 나타내야 합니다.</p>
     * 
     * @implNote 이름 변경 시 참조하는 시스템들에 영향을 줄 수 있으므로 주의가 필요합니다.
     */
    @JsonProperty("new_name")
    @Schema(
        description = "수정할 Few-Shot 이름 (프로젝트 내 중복 불가)", 
        example = "Enhanced Customer Sentiment Analysis",
        // required = true,
        minLength = 3,
        maxLength = 100
    )
    private String newName;
    
    /**
     * Few-Shot 예제 목록
     * 
     * <p>수정할 Few-Shot을 구성하는 새로운 예제들의 목록입니다.
     * 기존 예제들을 완전히 대체하며, 학습 효과를 고려하여 구성해야 합니다.</p>
     * 
     * @implNote 기존 예제들은 삭제되고 새로운 예제들로 완전 교체됩니다.
     * @apiNote 최소 2개 이상의 예제가 필요합니다.
     */
    @JsonProperty("items")
    @Schema(
        description = "수정할 Few-Shot 예제 목록 (기존 예제 완전 대체)",
        required = true
    )
    private List<FewShotItem> items;
    
    /**
     * Few-Shot 태그 목록
     * 
     * <p>수정할 Few-Shot에 적용될 새로운 태그들의 목록입니다.
     * 기존 태그들을 완전히 대체합니다.</p>
     * 
     * @implNote 태그 변경은 검색 및 분류에 영향을 줄 수 있습니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "수정할 Few-Shot 태그 목록 (기존 태그 완전 대체)"
    )
    private List<FewShotTag> tags;
    
    /**
     * 릴리즈 여부
     * 
     * <p>수정할 Few-Shot의 릴리즈 여부를 나타내는 플래그입니다.</p>
     */
    @JsonProperty("release")
    @Schema(
        description = "릴리즈 여부"
    )
    private Boolean release;
    
    /**
     * Few-Shot 예제 구조
     * 
     * <p>개별 Few-Shot 예제의 구조를 정의합니다.
     * FewShotCreateRequest의 FewShotExample과 동일한 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 예제 구조")
    public static class FewShotItem {
        
          /**
         * 입력 데이터
         * 
         * <p>AI 모델에 제공될 입력 데이터입니다.
         * 실제 사용 시나리오와 유사한 형태의 데이터여야 합니다.</p>
         */
        @JsonProperty("item_answer")
        @Schema(
            description = "입력 데이터 (실제 사용 시나리오와 유사한 형태)", 
            example = "이 제품은 정말 만족스럽습니다. 품질이 우수해요!",
            required = true,
            maxLength = 5000
        )
        private String itemAnswer;
        
        /**
         * 기대 출력
         * 
         * <p>입력에 대한 AI 모델의 기대되는 출력입니다.
         * 정확하고 일관된 형태로 제공되어야 합니다.</p>
         */
        @JsonProperty("item_query")
        @Schema(
            description = "기대 출력 (정확하고 일관된 형태)", 
            example = "positive",
            required = true,
            maxLength = 2000
        )
        private String itemQuery;
    }
    
    /**
     * Few-Shot 태그 구조
     * 
     * <p>Few-Shot 분류를 위한 태그의 구조를 정의합니다.
     * FewShotCreateRequest의 FewShotTag와 동일한 구조입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 태그 구조")
    public static class FewShotTag {
        
        /**
         * 태그명
         * 
         * <p>Few-Shot을 분류하기 위한 태그 이름입니다.
         * 영문자, 숫자, 하이픈(-)만 사용 가능하며 대소문자를 구분하지 않습니다.</p>
         */
        @JsonProperty("tag")
        @Schema(
            description = "태그명 (영문자, 숫자, 하이픈만 허용)", 
            example = "sentiment-analysis",
            required = true,
            pattern = "^[a-zA-Z0-9-]+$",
            maxLength = 50
        )
        private String tag;
    }
}