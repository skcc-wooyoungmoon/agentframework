package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 생성 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 새로운 Few-Shot Learning 세트를 생성하기 위한 요청 데이터 구조입니다.
 * AI 모델의 성능 향상을 위한 입력-출력 예제 쌍들과 메타데이터를 정의합니다.</p>
 * 
 * <h3>Few-Shot Learning 구성 요소:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명, 프로젝트 연결</li>
 *   <li><strong>예제 데이터</strong>: 입력-출력 쌍들의 집합</li>
 *   <li><strong>태그</strong>: 분류 및 검색을 위한 태그</li>
 *   <li><strong>메타데이터</strong>: 도메인, 모델 타입 등</li>
 * </ul>
 * 
 * <h3>Few-Shot 활용 분야:</h3>
 * <ul>
 *   <li><strong>텍스트 분류</strong>: 감정 분석, 주제 분류, 스팸 필터링</li>
 *   <li><strong>생성 작업</strong>: 번역, 요약, 창작 글쓰기</li>
 *   <li><strong>질의응답</strong>: FAQ, 지식베이스 응답</li>
 *   <li><strong>코드 생성</strong>: 프로그래밍 예제, API 사용법</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotCreateRequest request = FewShotCreateRequest.builder()
 *     .name("Customer Sentiment Analysis")
 *     .description("고객 리뷰 감정 분석을 위한 Few-Shot 예제 세트")
 *     .projectId("project-123")
 *     .examples(Arrays.asList(
 *         FewShotExample.builder()
 *             .input("이 제품은 정말 만족스럽습니다. 품질이 우수해요!")
 *             .output("positive")
 *             .explanation("긍정적인 감정 표현과 만족도를 나타내는 단어들")
 *             .build(),
 *         FewShotExample.builder()
 *             .input("배송이 너무 느리고 포장 상태도 좋지 않았습니다.")
 *             .output("negative")
 *             .explanation("불만족과 부정적인 경험을 나타내는 표현")
 *             .build(),
 *         FewShotExample.builder()
 *             .input("보통입니다. 가격 대비 나쁘지 않네요.")
 *             .output("neutral")
 *             .explanation("중립적인 평가와 객관적인 의견")
 *             .build()
 *     ))
 *     .tags(Arrays.asList(
 *         FewShotTag.builder().tag("sentiment-analysis").build(),
 *         FewShotTag.builder().tag("korean-text").build(),
 *         FewShotTag.builder().tag("customer-review").build()
 *     ))
 *     .build();
 * </pre>
 *
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0
 * @see FewShotExample Few-Shot Create Request 구조
 * @see FewShotTag Few-Shot 태그 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 생성 요청 정보",
    example = """
        {
          "name": "Customer Sentiment Analysis",
          "release": false,
          "project_id": "d89a7451-3d40-4bab-b4ee-6aecd55b4f32",
          "items": [
            {
              "input": "이 제품은 정말 만족스럽습니다!",
              "output": "positive",
              "explanation": "긍정적인 감정 표현"
            },
            {
              "input": "배송이 너무 느렸습니다.",
              "output": "negative",
              "explanation": "부정적인 경험 표현"
            }
          ],
          "tags": [
            {
              "tag": "sentiment-analysis"
            },
            {
              "tag": "korean-text"
            }
          ]
        }
        """
)
public class FewShotCreateRequest {
    
    /**
     * Few-Shot 이름
     * 
     * <p>Few-Shot 세트를 식별하는 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, Few-Shot의 목적을 명확히 나타내야 합니다.</p>
     * 
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Few-Shot 고유 이름 (프로젝트 내 중복 불가)", 
        example = "Customer Sentiment Analysis",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;
    
    /**
     * 프로젝트 식별자
     * 
     * <p>Few-Shot이 속할 프로젝트의 고유 식별자입니다.
     * 프로젝트는 Few-Shot의 접근 권한과 관리 범위를 결정합니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)", 
        example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32",
        defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32"
    )
    private String projectId;
    
    /**
     * Few-Shot 예제 목록
     * 
     * <p>AI 모델 학습을 위한 입력-출력 예제 쌍들의 목록입니다.
     * 각 예제는 입력, 기대 출력, 설명을 포함합니다.</p>
     * 
     * @implNote 예제는 순서가 중요하며, 학습 효과를 위해 다양한 패턴을 포함해야 합니다.
     * @apiNote 최소 2개 이상의 예제가 필요합니다.
     */
    @JsonProperty("items")
    @Schema(
        description = "Few-Shot 예제 목록 (최소 2개 이상)",
        required = true
    )
    private List<FewShotItem> items;

    /**
     * 릴리즈 여부
     * 
     * <p>릴리즈 여부를 나타내는 플래그입니다.</p>
    */
    @JsonProperty("release")
    @Schema(
        description = "릴리즈 여부"
    )
    private Boolean release;
    
    /**
     * Few-Shot 태그 목록
     * 
     * <p>Few-Shot 분류 및 검색을 위한 태그들의 목록입니다.
     * 태그를 통해 관련 Few-Shot들을 그룹화하고 효율적으로 검색할 수 있습니다.</p>
     * 
     * @implNote 태그는 대소문자를 구분하지 않으며, 공백은 하이픈(-)으로 대체됩니다.
     * @apiNote 태그가 없는 경우 빈 배열을 전달할 수 있습니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "Few-Shot 분류 태그 목록"
    )
    private List<FewShotTag> tags;
    
    /**
     * Few-Shot 예제 구조
     * 
     * <p>개별 Few-Shot 예제의 구조를 정의합니다.</p>
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
     * <p>Few-Shot 분류를 위한 태그의 구조를 정의합니다.</p>
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