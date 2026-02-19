package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent 태그로 프롬프트 검색 테스트 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 태그 기반 프롬프트 검색 기능을 테스트하기 위한 요청 데이터 구조입니다.
 * 특정 태그들을 기준으로 프롬프트를 필터링하고 검색하는 기능의 동작을 검증할 때 사용됩니다.</p>
 * 
 * <h3>태그 검색 테스트 특징:</h3>
 * <ul>
 *   <li><strong>다중 태그 검색</strong>: 여러 태그를 조합하여 프롬프트 검색</li>
 *   <li><strong>AND/OR 연산</strong>: 태그 간의 논리 연산 지원</li>
 *   <li><strong>성능 테스트</strong>: 대량 데이터에서의 검색 성능 측정</li>
 *   <li><strong>정확도 검증</strong>: 검색 결과의 정확성 확인</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>태그 시스템의 검색 성능 테스트</li>
 *   <li>복합 태그 조건의 동작 검증</li>
 *   <li>검색 알고리즘의 정확도 평가</li>
 *   <li>대용량 데이터에서의 성능 벤치마킹</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TagSearchTestRequest request = TagSearchTestRequest.builder()
 *     .tags(Arrays.asList("AI", "NLP", "conversation"))
 *     .searchType("AND")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see PromptFilterByTagsResponse 태그 검색 테스트 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 태그 기반 프롬프트 검색 테스트 요청 정보",
    example = """
        {
          "tags": ["AI", "NLP", "conversation"],
          "search_type": "AND"
        }
        """
)
public class TagSearchTestRequest {
    
    /**
     * 검색할 태그 목록
     * 
     * <p>프롬프트 검색에 사용할 태그들의 목록입니다.
     * 각 태그는 프롬프트의 분류나 특성을 나타내는 키워드입니다.</p>
     * 
     * @implNote 태그는 대소문자를 구분하지 않으며, 공백이 포함될 수 있습니다.
     * @apiNote 최소 1개 이상의 태그가 필요하며, 최대 10개까지 지원됩니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "검색할 태그 목록 (최대 10개)",
        example = """
            [
              "AI",
              "NLP", 
              "conversation",
              "chatbot"
            ]
            """,
        required = true
    )
    private List<String> tags;
    
    /**
     * 검색 타입
     * 
     * <p>태그 간의 논리 연산 방식을 지정합니다.
     * AND 연산은 모든 태그를 포함하는 프롬프트를, OR 연산은 하나 이상의 태그를 포함하는 프롬프트를 검색합니다.</p>
     * 
     * @implNote 기본값은 "AND"이며, "OR" 연산도 지원됩니다.
     * @apiNote 대소문자를 구분하지 않습니다.
     */
    @JsonProperty("search_type")
    @Schema(
        description = "태그 검색 타입 (AND: 모든 태그 포함, OR: 하나 이상 태그 포함)",
        example = "AND",
        allowableValues = {"AND", "OR"},
        defaultValue = "AND"
    )
    private String searchType;
}
