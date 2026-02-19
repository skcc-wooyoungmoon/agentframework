package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * SKTAI Knowledge External Repository 테스트 요청 DTO
 * 
 * <p>External Repository의 연결 상태와 동작을 테스트하기 위한 요청 데이터 구조입니다.
 * 실제 운영 환경에 반영하기 전에 외부 시스템과의 연동이 올바르게 작동하는지 확인할 수 있습니다.</p>
 * 
 * <h3>테스트 범위:</h3>
 * <ul>
 *   <li><strong>연결 테스트</strong>: 외부 시스템에 접근 가능한지 확인</li>
 *   <li><strong>인증 테스트</strong>: 제공된 인증 정보의 유효성 확인</li>
 *   <li><strong>데이터 조회 테스트</strong>: 실제 데이터를 읽어올 수 있는지 확인</li>
 *   <li><strong>매핑 테스트</strong>: 데이터 매핑 설정이 올바른지 확인</li>
 *   <li><strong>성능 테스트</strong>: 응답 시간 및 처리량 측정</li>
 * </ul>
 * 
 * <h3>테스트 모드:</h3>
 * <ul>
 *   <li><strong>QUICK</strong>: 기본 연결 및 인증만 테스트 (빠른 확인)</li>
 *   <li><strong>BASIC</strong>: 연결, 인증, 간단한 데이터 조회 테스트</li>
 *   <li><strong>COMPREHENSIVE</strong>: 전체 기능 및 성능 테스트 (권장)</li>
 *   <li><strong>SAMPLE_DATA</strong>: 샘플 데이터를 사용한 매핑 테스트</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoExtTestRequest testRequest = RepoExtTestRequest.builder()
 *     .testMode("COMPREHENSIVE")
 *     .sampleSize(10)
 *     .timeout(30000)
 *     .testConfig(Map.of(
 *         "verify_ssl", true,
 *         "test_queries", Arrays.asList("SELECT COUNT(*) FROM documents")
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult External Repository 테스트 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge External Repository 테스트 요청 정보")
public class RepoExtTestRequest {

    @JsonProperty("embedding_model_name")
    @Schema(description = "임베딩 모델 서빙명", example = "text-embedding-3-large")
    private String embeddingModelName;

    @JsonProperty("vector_db_id")
    @Schema(description = "Vector DB ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String vectorDbId;

    @JsonProperty("index_name")
    @Schema(description = "Vector DB 인덱스명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
    private String indexName;

    @Schema(description = "Retrieval Script 내용 (파일로 변환되어 전송)", hidden = true)
    private String script;

    @JsonProperty("query")
    @Schema(description = "Retrieval 테스트용 질의", example = "연금 수령 조건 알려줘")
    private String query;

    @JsonProperty("retrieval_options")
    @Schema(description = "Retrieval 옵션(JSON 문자열)", example = "{\"top_k\":3}")
    private String retrievalOptions;
}
