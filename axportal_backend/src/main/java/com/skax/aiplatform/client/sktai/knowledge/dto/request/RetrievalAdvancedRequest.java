package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SKTAI Knowledge 고급 검색 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 고급 검색 옵션을 사용한 문서 검색을 수행하기 위한 요청 데이터 구조입니다.
 * 기본 검색에 추가로 상세한 검색 옵션을 설정할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>query_text</strong>: 사용자 질의</li>
 *   <li><strong>repo_id</strong>: 검색 대상 Repository ID</li>
 * </ul>
 * 
 * <h3>고급 옵션:</h3>
 * <ul>
 *   <li><strong>retrieval_mode</strong>: dense, sparse, hybrid, semantic</li>
 *   <li><strong>top_k</strong>: 반환할 상위 결과 수</li>
 *   <li><strong>threshold</strong>: 유사도 임계값</li>
 *   <li><strong>filter</strong>: 메타데이터 필터링</li>
 *   <li><strong>order_by</strong>: 결과 정렬</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RetrievalAdvancedRequest request = RetrievalAdvancedRequest.builder()
 *     .queryText("개인형퇴직연금 중도해지 조건")
 *     .repoId("bc91fa12-f7df-4c77-8023-3f44249210d0")
 *     .retrievalOptions(Map.of(
 *         "retrieval_mode", "hybrid",
 *         "top_k", 5,
 *         "threshold", 0.7
 *     ))
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
    description = "SKTAI Knowledge 고급 검색 요청 정보",
    example = """
        {
          "query_text": "개인형퇴직연금 중도해지 조건 알려줘.",
          "repo_id": "bc91fa12-f7df-4c77-8023-3f44249210d0",
          "retrieval_options": {
            "retrieval_mode": "hybrid",
            "top_k": 5,
            "threshold": 0.7,
            "filter": "category eq 'finance'",
            "order_by": "score desc"
          }
        }
        """
)
public class RetrievalAdvancedRequest {
    
    /**
     * 사용자 질의
     * 
     * <p>검색할 질의 내용입니다.
     * 최소 1글자 이상 입력해야 하며, 고급 검색 옵션과 함께 사용됩니다.</p>
     */
    @JsonProperty("query_text")
    @Schema(
        description = "사용자 질의 (최소 1글자 이상)",
        example = "개인형퇴직연금 중도해지 조건 알려줘.",
        required = true,
        minLength = 1
    )
    private String queryText;
    
    /**
     * 지식저장소 ID
     * 
     * <p>검색 대상이 되는 Knowledge Repository의 고유 식별자입니다.
     * UUID 형식으로 제공되어야 합니다.</p>
     */
    @JsonProperty("repo_id")
    @Schema(
        description = "지식저장소 ID (UUID 형식)",
        example = "bc91fa12-f7df-4c77-8023-3f44249210d0",
        required = true,
        format = "uuid"
    )
    private String repoId;
    
    /**
     * 검색 옵션
     * 
     * <p>고급 검색을 위한 상세 옵션들입니다.
     * Vector DB 종류에 따라 지원되는 검색 옵션이 다를 수 있습니다.</p>
     * 
     * <h4>주요 옵션:</h4>
     * <ul>
     *   <li><strong>retrieval_mode</strong>: dense, sparse, hybrid, semantic</li>
     *   <li><strong>top_k</strong>: 상위 K개 결과 수 (기본값: 3)</li>
     *   <li><strong>threshold</strong>: 유사도 임계값 (0~1)</li>
     *   <li><strong>filter</strong>: 메타데이터 필터 조건</li>
     *   <li><strong>order_by</strong>: 정렬 조건</li>
     *   <li><strong>query_keywords</strong>: 핵심 키워드</li>
     *   <li><strong>semantic_configuration_name</strong>: Semantic 설정명 (Azure AI Search)</li>
     *   <li><strong>scoring_profile</strong>: 스코어링 프로파일 (Azure AI Search)</li>
     * </ul>
     * 
     * @apiNote Vector DB 종류에 따라 지원되는 옵션이 다르므로 자세한 내용은 가이드 문서를 참고하세요.
     */
    @JsonProperty("retrieval_options")
    @Schema(
        description = "검색 옵션 (Vector DB 종류에 따라 지원되는 옵션이 다름)",
        example = """
            {
              "retrieval_mode": "hybrid",
              "top_k": 5,
              "threshold": 0.7,
              "filter": "category eq 'finance'",
              "order_by": "score desc",
              "query_keywords": "퇴직연금 중도해지",
              "scoring_profile": "defaultProfile"
            }
            """
    )
    private Map<String, Object> retrievalOptions;
}
