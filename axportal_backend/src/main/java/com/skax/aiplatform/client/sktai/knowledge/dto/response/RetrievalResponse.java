package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Knowledge 검색 응답 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 검색 요청에 대한 응답 데이터 구조입니다.
 * 검색된 문서, 청크, 점수, 메타데이터 등을 포함하여 상세한 검색 결과를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>retrieved_docs</strong>: 검색된 문서 목록</li>
 *   <li><strong>retrieved_chunks</strong>: 검색된 청크 목록</li>
 *   <li><strong>scores</strong>: 검색 점수 정보</li>
 *   <li><strong>metadata</strong>: 검색 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>일반 검색 결과 표시</li>
 *   <li>고급 검색 결과 분석</li>
 *   <li>테스트 검색 결과 검증</li>
 *   <li>검색 성능 모니터링</li>
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
    description = "SKTAI Knowledge 검색 응답 정보",
    example = """
        {
          "retrieved_docs": [
            {
              "doc_id": "doc-123",
              "title": "개인형퇴직연금 가이드",
              "content": "개인형퇴직연금 중도해지는...",
              "score": 0.95
            }
          ],
          "retrieved_chunks": [
            {
              "chunk_id": "chunk-456",
              "content": "중도해지 조건은 다음과 같습니다...",
              "score": 0.92,
              "doc_id": "doc-123"
            }
          ],
          "scores": {
            "max_score": 0.95,
            "min_score": 0.75,
            "avg_score": 0.85
          },
          "metadata": {
            "total_docs": 150,
            "search_time_ms": 245,
            "algorithm": "hybrid"
          }
        }
        """
)
public class RetrievalResponse {
    
    /**
     * 검색된 문서 목록
     * 
     * <p>검색 쿼리와 관련성이 높은 문서들의 목록입니다.
     * 각 문서는 ID, 제목, 내용 일부, 관련성 점수 등을 포함합니다.</p>
     * 
     * @implNote Object 타입으로 정의되어 다양한 문서 메타데이터 구조를 수용할 수 있습니다.
     */
    @JsonProperty("retrieved_docs")
    @Schema(
        description = "검색된 문서 목록 (관련성 점수 포함)",
        example = """
            [
              {
                "doc_id": "doc-123",
                "title": "개인형퇴직연금 가이드",
                "content": "개인형퇴직연금 중도해지는...",
                "score": 0.95,
                "file_name": "pension_guide.pdf",
                "page_number": 15
              }
            ]
            """
    )
    private List<Object> retrievedDocs;
    
    /**
     * 검색된 청크 목록
     * 
     * <p>검색 쿼리와 관련성이 높은 문서 청크(조각)들의 목록입니다.
     * 각 청크는 ID, 내용, 관련성 점수, 소속 문서 정보 등을 포함합니다.</p>
     * 
     * @implNote Object 타입으로 정의되어 다양한 청크 메타데이터 구조를 수용할 수 있습니다.
     */
    @JsonProperty("retrieved_chunks")
    @Schema(
        description = "검색된 청크 목록 (관련성 점수 및 소속 문서 정보 포함)",
        example = """
            [
              {
                "chunk_id": "chunk-456",
                "content": "중도해지 조건은 다음과 같습니다...",
                "score": 0.92,
                "doc_id": "doc-123",
                "chunk_index": 5,
                "start_position": 1024,
                "end_position": 1536
              }
            ]
            """
    )
    private List<Object> retrievedChunks;
    
    /**
     * 검색 점수 정보
     * 
     * <p>검색 결과의 점수 통계 정보입니다.
     * 최고점, 최저점, 평균점 등을 포함하여 검색 품질을 평가할 수 있습니다.</p>
     * 
     * @implNote Object 타입으로 정의되어 다양한 점수 통계 구조를 수용할 수 있습니다.
     */
    @JsonProperty("scores")
    @Schema(
        description = "검색 점수 통계 정보",
        example = """
            {
              "max_score": 0.95,
              "min_score": 0.75,
              "avg_score": 0.85,
              "score_distribution": {
                "high": 3,
                "medium": 5,
                "low": 2
              }
            }
            """
    )
    private Object scores;
    
    /**
     * 검색 메타데이터
     * 
     * <p>검색 실행에 대한 메타데이터 정보입니다.
     * 전체 문서 수, 검색 시간, 사용된 알고리즘 등의 정보를 포함합니다.</p>
     * 
     * @implNote Object 타입으로 정의되어 다양한 메타데이터 구조를 수용할 수 있습니다.
     */
    @JsonProperty("metadata")
    @Schema(
        description = "검색 실행 메타데이터 (문서 수, 검색 시간, 알고리즘 등)",
        example = """
            {
              "total_docs": 150,
              "total_chunks": 1250,
              "search_time_ms": 245,
              "algorithm": "hybrid",
              "query_length": 15,
              "repository_id": "repo-789"
            }
            """
    )
    private Object metadata;
}
