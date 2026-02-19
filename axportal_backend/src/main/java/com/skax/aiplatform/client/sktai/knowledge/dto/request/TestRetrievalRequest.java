package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge 테스트 검색 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 검색 기능 테스트를 위한 요청 데이터 구조입니다.
 * 개발 및 디버깅 목적으로 사용되며, 특정 Collection을 지정할 수 있습니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>query_text</strong>: 사용자 질의</li>
 *   <li><strong>repo_id</strong>: 검색 대상 Repository ID</li>
 * </ul>
 * 
 * <h3>선택 필드:</h3>
 * <ul>
 *   <li><strong>collection_id</strong>: 특정 Collection ID (테스트용)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TestRetrievalRequest request = TestRetrievalRequest.builder()
 *     .queryText("개인형퇴직연금 중도해지 조건 알려줘.")
 *     .repoId("bc91fa12-f7df-4c77-8023-3f44249210d0")
 *     .collectionId("collection-123")
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
    description = "SKTAI Knowledge 테스트 검색 요청 정보",
    example = """
        {
          "query_text": "개인형퇴직연금 중도해지 조건 알려줘.",
          "repo_id": "bc91fa12-f7df-4c77-8023-3f44249210d0",
          "collection_id": "bc91fa12-f7df-4c77-8023-3f44249210d0"
        }
        """
)
public class TestRetrievalRequest {
    
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
}
