package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Repository 생성 응답 DTO
 * 
 * <p>Repository 생성 요청에 대한 응답 데이터 구조입니다.
 * 생성된 Repository의 기본 정보와 생성 결과를 포함합니다.</p>
 * 
 * <h3>응답 정보:</h3>
 * <ul>
 *   <li><strong>Repository 식별자</strong>: 새로 생성된 Repository의 고유 ID</li>
 *   <li><strong>생성 상태</strong>: 생성 성공 여부 및 상태 정보</li>
 *   <li><strong>기본 정보</strong>: 이름, 설명, 설정 요약</li>
 *   <li><strong>다음 단계</strong>: 인덱싱 등 후속 작업 안내</li>
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
@Schema(description = "SKTAI Knowledge Repository 생성 응답 정보")
public class RepoCreateResponse {

    /**
     * 생성된 Repository ID
     */
    @JsonProperty("repo_id")
    @Schema(description = "생성된 Repository 고유 식별자", example = "repo-12345")
    private String repoId;

    /**
     * Repository 이름
     */
    @JsonProperty("name")
    @Schema(description = "Repository 이름", example = "Customer Service KB")
    private String name;

    /**
     * Repository 설명
     */
    @JsonProperty("description")
    @Schema(description = "Repository 설명", example = "고객 서비스 지식 베이스")
    private String description;

    /**
     * 생성 상태
     */
    @JsonProperty("status")
    @Schema(description = "생성 상태", example = "CREATED")
    private String status;

    /**
     * 연결된 DataSource ID
     */
    @JsonProperty("datasource_id")
    @Schema(description = "연결된 DataSource ID", example = "datasource-123")
    private String datasourceId;

    /**
     * 임베딩 모델 이름
     */
    @JsonProperty("embedding_model_name")
    @Schema(description = "설정된 임베딩 모델", example = "GIP/text-embedding-3-large")
    private String embeddingModelName;

    /**
     * 벡터 DB ID
     */
    @JsonProperty("vector_db_id")
    @Schema(description = "연결된 벡터 DB ID", example = "vectordb-456")
    private String vectorDbId;

    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간 (ISO 8601)", example = "2024-01-15T10:30:00Z")
    private String createdAt;

    /**
     * 생성자 정보
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자 정보", example = "user@example.com")
    private String createdBy;

    /**
     * 인덱싱 준비 상태
     */
    @JsonProperty("indexing_ready")
    @Schema(description = "인덱싱 준비 완료 여부", example = "true")
    private Boolean indexingReady;

    /**
     * 다음 단계 안내
     */
    @JsonProperty("next_steps")
    @Schema(description = "권장되는 다음 단계", example = "인덱싱을 시작하여 검색 가능 상태로 만드세요")
    private String nextSteps;
}
