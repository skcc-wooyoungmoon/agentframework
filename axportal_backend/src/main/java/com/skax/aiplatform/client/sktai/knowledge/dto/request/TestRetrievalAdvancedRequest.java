package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Knowledge 고급 테스트 검색 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 고급 옵션을 포함한 검색 기능 테스트를 위한 요청 데이터 구조입니다.
 * 개발 및 디버깅 목적으로 사용되며, 검색 범위, 필터, 정렬 옵션 등을 세밀하게 조정할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>query_text</strong>: 사용자 질의</li>
 *   <li><strong>repo_id</strong>: 검색 대상 Repository ID</li>
 * </ul>
 * 
 * <h3>고급 검색 옵션:</h3>
 * <ul>
 *   <li><strong>retrieval_option</strong>: 검색 알고리즘 설정</li>
 *   <li><strong>collection_id</strong>: 특정 Collection 지정</li>
 *   <li><strong>topk_docs</strong>: 반환할 문서 수</li>
 *   <li><strong>topk_chunks</strong>: 반환할 청크 수</li>
 *   <li><strong>file_ids</strong>: 특정 파일 제한</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TestRetrievalAdvancedRequest request = TestRetrievalAdvancedRequest.builder()
 *     .queryText("개인형퇴직연금 중도해지 조건 알려줘.")
 *     .repoId("bc91fa12-f7df-4c77-8023-3f44249210d0")
 *     .collectionId("collection-123")
 *     .topkDocs(10)
 *     .topkChunks(20)
 *     .fileIds(Arrays.asList("file1", "file2"))
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
    description = "SKTAI Knowledge 고급 테스트 검색 요청 정보",
    example = """
        {
          "query_text": "개인형퇴직연금 중도해지 조건 알려줘.",
          "repo_id": "bc91fa12-f7df-4c77-8023-3f44249210d0",
          "collection_id": "bc91fa12-f7df-4c77-8023-3f44249210d0",
          "retrieval_option": {},
          "topk_docs": 10,
          "topk_chunks": 20,
          "file_ids": ["file1", "file2"]
        }
        """
)
public class TestRetrievalAdvancedRequest {
    
    /**
     * 사용자 질의
     * 
     * <p>테스트할 검색 질의 내용입니다.
     * 최소 1글자 이상 입력해야 합니다.</p>
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
     * <p>테스트 대상이 되는 Knowledge Repository의 고유 식별자입니다.
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
     * Repo Collection ID
     * 
     * <p>특정 Repository Collection을 대상으로 테스트할 때 사용하는 선택적 필드입니다.
     * 지정하지 않으면 Repository의 기본 Collection이 사용됩니다.</p>
     * 
     * @apiNote 테스트 및 디버깅 목적으로 특정 Collection을 지정할 때 사용합니다.
     */
    @JsonProperty("collection_id")
    @Schema(
        description = "Repo Collection ID (선택적, 테스트용)",
        example = "bc91fa12-f7df-4c77-8023-3f44249210d0",
        format = "uuid"
    )
    private String collectionId;
    
    /**
     * 검색 옵션 설정
     * 
     * <p>검색 알고리즘, 임계값, 가중치 등을 설정하는 복합 객체입니다.
     * 검색 성능과 정확도를 조정할 수 있습니다.</p>
     * 
     * @implNote Object 타입으로 정의되어 다양한 검색 옵션 구조를 수용할 수 있습니다.
     */
    @JsonProperty("retrieval_option")
    @Schema(
        description = "검색 옵션 설정 (알고리즘, 임계값, 가중치 등)",
        example = """
            {
              "similarity_threshold": 0.7,
              "search_algorithm": "hybrid",
              "weights": {
                "semantic": 0.7,
                "keyword": 0.3
              }
            }
            """
    )
    private Object retrievalOption;
    
    /**
     * 반환할 최대 문서 수
     * 
     * <p>검색 결과에서 반환할 최대 문서 개수입니다.
     * 1에서 100 사이의 값으로 제한됩니다.</p>
     * 
     * @implNote 기본값은 10개입니다.
     */
    @JsonProperty("topk_docs")
    @Schema(
        description = "반환할 최대 문서 수 (1-100)",
        example = "10",
        minimum = "1",
        maximum = "100"
    )
    private Integer topkDocs;
    
    /**
     * 반환할 최대 청크 수
     * 
     * <p>검색 결과에서 반환할 최대 청크(문서 조각) 개수입니다.
     * 1에서 500 사이의 값으로 제한됩니다.</p>
     * 
     * @implNote 기본값은 20개입니다.
     */
    @JsonProperty("topk_chunks")
    @Schema(
        description = "반환할 최대 청크 수 (1-500)",
        example = "20",
        minimum = "1",
        maximum = "500"
    )
    private Integer topkChunks;
    
    /**
     * 검색 대상 파일 ID 목록
     * 
     * <p>특정 파일들만을 대상으로 검색을 제한할 때 사용합니다.
     * 빈 배열이거나 null인 경우 모든 파일을 대상으로 검색합니다.</p>
     * 
     * @apiNote 대용량 Repository에서 특정 파일 그룹에 대한 테스트 시 유용합니다.
     */
    @JsonProperty("file_ids")
    @Schema(
        description = "검색 대상 파일 ID 목록 (빈 배열이면 모든 파일 검색)",
        example = "[\"file1\", \"file2\", \"file3\"]"
    )
    private List<String> fileIds;
}
