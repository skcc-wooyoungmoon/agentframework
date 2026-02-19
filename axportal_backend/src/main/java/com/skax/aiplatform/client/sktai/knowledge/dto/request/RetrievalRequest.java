package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge 기본 검색 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 문서 검색을 수행하기 위한 기본 요청 데이터 구조입니다.
 * 사용자 질의와 검색 대상 Repository ID를 포함합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>query_text</strong>: 사용자 질의 (최소 1글자 이상)</li>
 *   <li><strong>repo_id</strong>: 검색 대상 Knowledge Repository ID</li>
 * </ul>
 * 
 * <h3>검색 동작:</h3>
 * <ul>
 *   <li>벡터 기반 유사도 검색 (Dense 모드)</li>
 *   <li>기본 상위 3개 결과 반환</li>
 *   <li>임계값 필터링 없음</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RetrievalRequest request = RetrievalRequest.builder()
 *     .queryText("개인형퇴직연금 중도해지 조건 알려줘.")
 *     .repoId("bc91fa12-f7df-4c77-8023-3f44249210d0")
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
    description = "SKTAI Knowledge 기본 검색 요청 정보",
    example = """
        {
          "query_text": "개인형퇴직연금 중도해지 조건 알려줘.",
          "repo_id": "bc91fa12-f7df-4c77-8023-3f44249210d0"
        }
        """
)
public class RetrievalRequest {
    
    /**
     * 사용자 질의
     * 
     * <p>검색할 질의 내용입니다.
     * 최소 1글자 이상 입력해야 하며, 자연어로 작성된 질문이나 키워드를 포함할 수 있습니다.</p>
     * 
     * @implNote 질의는 임베딩 모델을 통해 벡터로 변환되어 검색에 사용됩니다.
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
     * 
     * @apiNote Repository는 사전에 인덱싱이 완료된 상태여야 검색이 가능합니다.
     */
    @JsonProperty("repo_id")
    @Schema(
        description = "지식저장소 ID (UUID 형식)",
        example = "bc91fa12-f7df-4c77-8023-3f44249210d0",
        required = true,
        format = "uuid"
    )
    private String repoId;
}
