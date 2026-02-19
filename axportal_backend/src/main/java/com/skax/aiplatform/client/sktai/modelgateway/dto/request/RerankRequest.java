package com.skax.aiplatform.client.sktai.modelgateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 리랭킹 요청 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 문서 리랭킹을 위한 요청 데이터 구조입니다.
 * 주어진 쿼리에 대해 여러 문서들의 관련도를 재평가하여 순서를 재정렬합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>model</strong>: 사용할 리랭킹 모델 식별자</li>
 *   <li><strong>query</strong>: 검색 쿼리 텍스트</li>
 *   <li><strong>documents</strong>: 순위를 매길 문서 목록</li>
 * </ul>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>검색 결과 품질 향상</li>
 *   <li>의미적 관련도 기반 순위 조정</li>
 *   <li>다국어 문서 지원</li>
 *   <li>배치 문서 처리</li>
 *   <li>관련도 스코어 제공</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RerankRequest request = RerankRequest.builder()
 *     .model("BAAI/bge-reranker-v2-m3")
 *     .query("What is the capital of the United States?")
 *     .documents(Arrays.asList(
 *         "Carson City is the capital city of the American state of Nevada.",
 *         "Washington, D.C. is the capital of the United States. It is a federal district."
 *     ))
 *     .topN(10)
 *     .returnDocuments(true)
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
    description = "SKTAI Model Gateway 리랭킹 요청 정보",
    example = """
        {
          "model": "BAAI/bge-reranker-v2-m3",
          "query": "What is the capital of the United States?",
          "documents": [
            "Carson City is the capital city of the American state of Nevada.",
            "Washington, D.C. is the capital of the United States. It is a federal district."
          ],
          "top_n": 10,
          "return_documents": true
        }
        """
)
public class RerankRequest {
    
    /**
     * 리랭킹 모델 식별자
     * 
     * <p>문서 순위를 재평가할 모델의 이름입니다.
     * 전문적인 리랭킹 모델로 검색 품질을 크게 향상시킵니다.</p>
     * 
     * @implNote 지원 모델: BAAI/bge-reranker-v2-m3, BAAI/bge-reranker-large 등
     * @apiNote BGE 리랭커는 높은 정확도와 다국어 지원을 제공합니다.
     */
    @JsonProperty("model")
    @Schema(
        description = "사용할 리랭킹 모델 식별자",
        example = "BAAI/bge-reranker-v2-m3",
        required = true
    )
    private String model;
    
    /**
     * 검색 쿼리
     * 
     * <p>문서들의 관련도를 평가할 기준이 되는 검색 쿼리입니다.
     * 명확하고 구체적인 쿼리일수록 정확한 리랭킹 결과를 얻을 수 있습니다.</p>
     * 
     * @apiNote 질문 형태, 키워드, 또는 문장 형태 모두 지원합니다.
     * @implNote 쿼리의 품질이 리랭킹 성능에 직접적인 영향을 미칩니다.
     */
    @JsonProperty("query")
    @Schema(
        description = "검색 쿼리 텍스트 (문서 관련도 평가 기준)",
        example = "What is the capital of the United States?",
        required = true,
        maxLength = 1000
    )
    private String query;
    
    /**
     * 순위를 매길 문서 목록
     * 
     * <p>리랭킹할 문서들의 텍스트 목록입니다.
     * 각 문서는 쿼리와의 관련도에 따라 새로운 순위를 부여받습니다.</p>
     * 
     * @apiNote 최대 1000개의 문서까지 처리 가능합니다.
     * @implNote 문서가 많을수록 처리 시간이 증가하지만 더 정확한 순위를 얻을 수 있습니다.
     */
    @JsonProperty("documents")
    @Schema(
        description = "순위를 매길 문서 텍스트 목록",
        example = """
            [
              "Carson City is the capital city of the American state of Nevada.",
              "Washington, D.C. is the capital of the United States. It is a federal district."
            ]
            """,
        required = true
    )
    private List<String> documents;
    
    /**
     * 반환할 상위 문서 수
     * 
     * <p>리랭킹 결과에서 반환할 상위 문서의 개수를 지정합니다.
     * 전체 문서보다 적은 수를 지정하면 상위 결과만 반환됩니다.</p>
     * 
     * @implNote 기본값: 전체 문서 수, 최소값: 1
     * @apiNote 성능과 결과 품질의 균형을 고려하여 설정하세요.
     */
    @JsonProperty("top_n")
    @Schema(
        description = "반환할 상위 문서 개수",
        example = "10",
        minimum = "1"
    )
    private Integer topN;
    
    /**
     * 문서 반환 여부
     * 
     * <p>응답에 원본 문서 텍스트를 포함할지 여부를 결정합니다.
     * false로 설정하면 순위와 스코어만 반환됩니다.</p>
     * 
     * @implNote 기본값: true
     * @apiNote 문서를 포함하지 않으면 응답 크기가 줄어들어 네트워크 효율성이 향상됩니다.
     */
    @JsonProperty("return_documents")
    @Schema(
        description = "응답에 원본 문서 포함 여부",
        example = "true"
    )
    private Boolean returnDocuments;
    
    /**
     * 최대 문서 길이
     * 
     * <p>각 문서의 최대 토큰 길이를 제한합니다.
     * 긴 문서는 자동으로 잘려서 처리됩니다.</p>
     * 
     * @implNote 기본값: 512, 범위: 1~4096
     * @apiNote 너무 긴 문서는 처리 시간을 증가시키고 성능에 영향을 줄 수 있습니다.
     */
    @JsonProperty("max_chunks_per_doc")
    @Schema(
        description = "문서당 최대 청크 수 (긴 문서 분할 처리)",
        example = "5",
        minimum = "1"
    )
    private Integer maxChunksPerDoc;
    
    /**
     * 청크 크기
     * 
     * <p>긴 문서를 분할할 때 각 청크의 크기를 지정합니다.
     * 문서가 이 크기보다 길면 여러 청크로 나누어 처리됩니다.</p>
     * 
     * @implNote 기본값: 512, 권장 범위: 256~1024
     * @apiNote 적절한 청크 크기는 모델의 성능과 처리 속도를 모두 고려해야 합니다.
     */
    @JsonProperty("chunk_size")
    @Schema(
        description = "문서 분할 시 청크 크기 (토큰 단위)",
        example = "512",
        minimum = "1"
    )
    private Integer chunkSize;
    
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
