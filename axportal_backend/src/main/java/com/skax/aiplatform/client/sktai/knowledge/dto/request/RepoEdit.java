package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Repository 편집 요청 DTO
 * 
 * <p>기존 Repository의 기본 설정을 수정하기 위한 요청 데이터 구조입니다.
 * Repository 이름, 기본 로더, 청크 설정 등 핵심 속성을 변경할 수 있습니다.</p>
 * 
 * <h3>편집 가능한 속성:</h3>
 * <ul>
 *   <li><strong>이름 및 설명</strong>: Repository의 식별 정보 변경</li>
 *   <li><strong>기본 로더</strong>: 새로운 문서 처리 시 사용할 기본 로더 변경</li>
 *   <li><strong>청크 설정</strong>: 분할 크기, 오버랩, 구분자 등 최적화</li>
 *   <li><strong>임베딩 모델</strong>: 다른 임베딩 모델로 변경 (재인덱싱 필요)</li>
 *   <li><strong>벡터 DB</strong>: 다른 벡터 데이터베이스로 마이그레이션</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li><strong>임베딩 모델 변경</strong>: 전체 Repository 재인덱싱이 필요하여 시간이 소요됩니다</li>
 *   <li><strong>벡터 DB 변경</strong>: 기존 벡터 데이터 마이그레이션이 필요합니다</li>
 *   <li><strong>청크 설정 변경</strong>: 검색 품질에 영향을 미칠 수 있습니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoEdit editRequest = RepoEdit.builder()
 *     .name("Updated KB Name")
 *     .description("업데이트된 지식 베이스 설명")
 *     .defaultLoader("DataIngestionTool")
 *     .defaultToolId("tool-advanced-ocr")
 *     .chunkSize(1200)
 *     .chunkOverlap(100)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse Repository 수정 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Repository 편집 요청 정보",
    example = """
        {
          "name": "Updated KB Name",
          "description": "업데이트된 지식 베이스 설명",
          "default_loader": "DataIngestionTool",
          "default_tool_id": "tool-advanced-ocr",
          "chunk_size": 1200,
          "chunk_overlap": 100,
          "separator": "\\n\\n"
        }
        """
)
public class RepoEdit {

    /**
     * Repository 이름 (선택적)
     * 
     * <p>변경할 Repository의 새로운 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 지정하지 않으면 기존 이름이 유지됩니다.</p>
     * 
     * @implNote 이름 변경 시 다른 시스템에서의 참조 관계를 확인해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "변경할 Repository 이름",
        example = "Updated KB Name",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    /**
     * Repository 설명 (선택적)
     * 
     * <p>변경할 Repository의 새로운 설명입니다.
     * 지정하지 않으면 기존 설명이 유지됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "변경할 Repository 설명",
        example = "업데이트된 지식 베이스 설명",
        maxLength = 500
    )
    private String description;

    /**
     * 기본 로더 유형 (선택적)
     * 
     * <p>새로 추가되는 문서에 적용될 기본 로더 유형입니다.
     * 기존 문서에는 영향을 주지 않으며, 새로운 인덱싱 시에만 적용됩니다.</p>
     * 
     * <h4>지원하는 로더 유형:</h4>
     * <ul>
     *   <li><strong>Default</strong>: 기본 문서 로더</li>
     *   <li><strong>DataIngestionTool</strong>: Tool을 사용한 고급 처리</li>
     *   <li><strong>CustomLoader</strong>: 사용자 정의 로더</li>
     * </ul>
     */
    @JsonProperty("default_loader")
    @Schema(
        description = "기본 문서 로더 유형",
        example = "DataIngestionTool",
        allowableValues = {"Default", "DataIngestionTool", "CustomLoader"}
    )
    private String defaultLoader;

    /**
     * 기본 스플리터 유형 (선택적)
     * 
     * <p>새로 추가되는 문서에 적용될 기본 스플리터 유형입니다.
     * 기존 문서에는 영향을 주지 않으며, 새로운 인덱싱 시에만 적용됩니다.</p>
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
     * 기존 문서에 영향을 주려면 재인덱싱이 필요합니다.</p>
     * 
     * @implNote 변경 시 검색 성능과 정확도에 영향을 미칠 수 있습니다.
     */
    @JsonProperty("chunk_size")
    @Schema(
        description = "청크 크기 (문자 수)",
        example = "1200",
        minimum = "100",
        maximum = "4000"
    )
    private Integer chunkSize;

    /**
     * 청크 오버랩 (선택적)
     * 
     * <p>인접한 청크 간의 중복되는 문자 수입니다.
     * 기존 문서에 영향을 주려면 재인덱싱이 필요합니다.</p>
     */
    @JsonProperty("chunk_overlap")
    @Schema(
        description = "청크 오버랩 (문자 수)",
        example = "100",
        minimum = "0",
        maximum = "1000"
    )
    private Integer chunkOverlap;

    /**
     * 분할 구분자 (선택적)
     * 
     * <p>문서 분할 시 우선적으로 사용할 구분자입니다.
     * 기존 문서에 영향을 주려면 재인덱싱이 필요합니다.</p>
     */
    @JsonProperty("separator")
    @Schema(
        description = "분할 구분자",
        example = "\\n\\n",
        maxLength = 50
    )
    private String separator;

    /**
     * 임베딩 모델 이름 (선택적)
     * 
     * <p>변경할 임베딩 모델의 이름입니다.
     * <strong>주의:</strong> 모델 변경 시 전체 Repository의 재인덱싱이 필요합니다.</p>
     * 
     * @apiNote 모델 변경은 시간이 많이 소요되는 작업이므로 신중하게 결정해야 합니다.
     */
    @JsonProperty("embedding_model_name")
    @Schema(
        description = "변경할 임베딩 모델 이름 (재인덱싱 필요)",
        example = "GIP/text-embedding-3-small"
    )
    private String embeddingModelName;

    /**
     * 벡터 데이터베이스 식별자 (선택적)
     * 
     * <p>변경할 벡터 데이터베이스의 식별자입니다.
     * <strong>주의:</strong> 벡터 DB 변경 시 데이터 마이그레이션이 필요합니다.</p>
     * 
     * @apiNote 벡터 DB 변경은 복잡한 마이그레이션 과정이 필요하므로 신중하게 결정해야 합니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "변경할 벡터 데이터베이스 ID (마이그레이션 필요)",
        example = "vectordb-new-789",
        format = "uuid"
    )
    private String vectorDbId;

    /**
     * 기본 Tool 식별자 (선택적)
     * 
     * <p>defaultLoader가 "DataIngestionTool"인 경우 사용할 Tool의 식별자입니다.
     * 새로 추가되는 문서에 적용될 기본 Tool을 설정합니다.</p>
     */
    @JsonProperty("default_tool_id")
    @Schema(
        description = "기본 Tool ID (defaultLoader가 DataIngestionTool인 경우 사용)",
        example = "tool-advanced-ocr",
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
        description = "기본 사용자 정의 로더 ID (defaultLoader가 CustomLoader인 경우 사용)",
        example = "custom-loader-456",
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
        description = "기본 사용자 정의 스플리터 ID (defaultSplitter가 CustomSplitter인 경우 사용)",
        example = "custom-splitter-789",
        format = "uuid"
    )
    private String defaultCustomSplitterId;

    /**
     * 수정자 정보 (선택적)
     * 
     * <p>Repository를 수정하는 사용자의 식별 정보입니다.
     * 지정하지 않으면 현재 인증된 사용자 정보가 사용됩니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
        description = "Repository 수정자 정보",
        example = "admin@example.com"
    )
    private String updatedBy;
}
