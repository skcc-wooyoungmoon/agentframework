package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Repository 업데이트 요청 DTO
 * 
 * <p>Repository의 설정을 부분적으로 업데이트하기 위한 요청 데이터 구조입니다.
 * 특정 설정 항목만을 변경하고 싶을 때 사용하며, 지정하지 않은 필드는 기존 값이 유지됩니다.</p>
 * 
 * <h3>업데이트 가능한 항목:</h3>
 * <ul>
 *   <li><strong>DataSource 연결</strong>: Repository와 연결된 DataSource 변경</li>
 *   <li><strong>기본 설정</strong>: 로더, 스플리터, 청크 설정 등</li>
 *   <li><strong>모델 설정</strong>: 임베딩 모델, 벡터 DB 등</li>
 * </ul>
 * 
 * <h3>업데이트 vs 편집 차이점:</h3>
 * <ul>
 *   <li><strong>RepoUpdateRequest</strong>: 설정 항목 중심의 부분 업데이트</li>
 *   <li><strong>RepoEdit</strong>: 전체적인 Repository 정보 편집</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoUpdateRequest updateRequest = RepoUpdateRequest.builder()
 *     .datasourceId("new-datasource-123")
 *     .embeddingModelName("GIP/text-embedding-3-small")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse Repository 업데이트 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Repository 업데이트 요청 정보",
    example = """
        {
          "datasource_id": "new-datasource-123",
          "embedding_model_name": "GIP/text-embedding-3-small",
          "vector_db_id": "vectordb-updated-456",
          "chunk_size": 800
        }
        """
)
public class RepoUpdateRequest {

    /**
     * 데이터 소스 식별자 (선택적)
     * 
     * <p>Repository와 연결할 새로운 DataSource의 식별자입니다.
     * DataSource 변경 시 기존 문서는 유지되며, 새로운 DataSource의 파일들을 추가로 인덱싱할 수 있습니다.</p>
     * 
     * @implNote DataSource 변경 후 수동으로 인덱싱을 실행해야 새로운 파일들이 Repository에 반영됩니다.
     */
    @JsonProperty("datasource_id")
    @Schema(
        description = "변경할 DataSource ID",
        example = "new-datasource-123",
        format = "uuid"
    )
    private String datasourceId;

    /**
     * 임베딩 모델 이름 (선택적)
     * 
     * <p>변경할 임베딩 모델의 이름입니다.
     * <strong>중요:</strong> 임베딩 모델 변경 시 기존 벡터들과 호환성 문제가 발생할 수 있으므로 재인덱싱을 권장합니다.</p>
     * 
     * @apiNote 모델 변경 후 검색 품질 저하를 방지하려면 전체 재인덱싱을 수행해야 합니다.
     */
    @JsonProperty("embedding_model_name")
    @Schema(
        description = "변경할 임베딩 모델 이름",
        example = "GIP/text-embedding-3-small"
    )
    private String embeddingModelName;

    /**
     * 벡터 데이터베이스 식별자 (선택적)
     * 
     * <p>변경할 벡터 데이터베이스의 식별자입니다.
     * <strong>중요:</strong> 벡터 DB 변경 시 기존 벡터 데이터의 마이그레이션이 필요합니다.</p>
     * 
     * @apiNote 벡터 DB 변경은 복잡한 과정이므로 변경 전 백업을 권장합니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "변경할 벡터 데이터베이스 ID",
        example = "vectordb-updated-456",
        format = "uuid"
    )
    private String vectorDbId;

    /**
     * 기본 로더 유형 (선택적)
     * 
     * <p>새로 추가되는 문서에 적용될 기본 로더 유형입니다.
     * 기존 문서에는 영향을 주지 않습니다.</p>
     */
    @JsonProperty("default_loader")
    @Schema(
        description = "기본 문서 로더 유형",
        example = "Default",
        allowableValues = {"Default", "DataIngestionTool", "CustomLoader"}
    )
    private String defaultLoader;

    /**
     * 기본 스플리터 유형 (선택적)
     * 
     * <p>새로 추가되는 문서에 적용될 기본 스플리터 유형입니다.
     * 기존 문서에는 영향을 주지 않습니다.</p>
     */
    @JsonProperty("default_splitter")
    @Schema(
        description = "기본 문서 스플리터 유형",
        example = "RecursiveCharacter",
        allowableValues = {"RecursiveCharacter", "Character", "Semantic", "CustomSplitter", "NotSplit"}
    )
    private String defaultSplitter;

    /**
     * 청크 크기 (선택적)
     * 
     * <p>문서 분할 시 사용할 청크의 최대 문자 수입니다.
     * 기존 문서에 적용하려면 재인덱싱이 필요합니다.</p>
     */
    @JsonProperty("chunk_size")
    @Schema(
        description = "청크 크기 (문자 수)",
        example = "800",
        minimum = "100",
        maximum = "4000"
    )
    private Integer chunkSize;

    /**
     * 청크 오버랩 (선택적)
     * 
     * <p>인접한 청크 간의 중복되는 문자 수입니다.
     * 기존 문서에 적용하려면 재인덱싱이 필요합니다.</p>
     */
    @JsonProperty("chunk_overlap")
    @Schema(
        description = "청크 오버랩 (문자 수)",
        example = "80",
        minimum = "0",
        maximum = "1000"
    )
    private Integer chunkOverlap;

    /**
     * 분할 구분자 (선택적)
     * 
     * <p>문서 분할 시 우선적으로 사용할 구분자입니다.
     * 기존 문서에 적용하려면 재인덱싱이 필요합니다.</p>
     */
    @JsonProperty("separator")
    @Schema(
        description = "분할 구분자",
        example = "\\n",
        maxLength = 50
    )
    private String separator;

    /**
     * 기본 Tool 식별자 (선택적)
     * 
     * <p>defaultLoader가 "DataIngestionTool"인 경우 사용할 Tool의 식별자입니다.</p>
     */
    @JsonProperty("default_tool_id")
    @Schema(
        description = "기본 Tool ID (defaultLoader가 DataIngestionTool인 경우)",
        example = "tool-123",
        format = "uuid"
    )
    private String defaultToolId;

    /**
     * 기본 사용자 정의 로더 식별자 (선택적)
     * 
     * <p>defaultLoader가 "CustomLoader"인 경우 사용할 사용자 정의 로더의 식별자입니다.</p>
     */
    @JsonProperty("default_custom_loader_id")
    @Schema(
        description = "기본 사용자 정의 로더 ID",
        example = "custom-loader-123",
        format = "uuid"
    )
    private String defaultCustomLoaderId;

    /**
     * 기본 사용자 정의 스플리터 식별자 (선택적)
     * 
     * <p>defaultSplitter가 "CustomSplitter"인 경우 사용할 사용자 정의 스플리터의 식별자입니다.</p>
     */
    @JsonProperty("default_custom_splitter_id")
    @Schema(
        description = "기본 사용자 정의 스플리터 ID",
        example = "custom-splitter-123",
        format = "uuid"
    )
    private String defaultCustomSplitterId;

    /**
     * 업데이트 수행자 정보 (선택적)
     * 
     * <p>Repository를 업데이트하는 사용자의 식별 정보입니다.
     * 지정하지 않으면 현재 인증된 사용자 정보가 사용됩니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "Repository 업데이트 수행자 정보",
        example = "admin@example.com"
    )
    private String updatedBy;

    /**
     * 업데이트 사유 (선택적)
     * 
     * <p>Repository 업데이트의 사유나 목적을 설명하는 텍스트입니다.
     * 변경 이력 관리를 위해 권장됩니다.</p>
     */
    @JsonProperty("update_reason")
    @Schema(
        description = "업데이트 사유 (변경 이력 관리용)",
        example = "성능 최적화를 위한 청크 크기 조정",
        maxLength = 200
    )
    private String updateReason;
}
