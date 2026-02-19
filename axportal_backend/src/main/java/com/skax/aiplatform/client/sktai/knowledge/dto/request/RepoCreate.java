package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Knowledge Repository 생성 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 새로운 Repository를 생성하기 위한 요청 데이터 구조입니다.
 * DataSource에 등록된 파일을 기반으로 완전한 지식 저장소를 구축하며, 임베딩 모델, 벡터 DB, 청크 설정 등을 포함합니다.</p>
 * 
 * <h3>Repository 생성 과정:</h3>
 * <ol>
 *   <li><strong>DataSource 연결</strong>: 지정된 DataSource의 파일들을 Repository로 가져옴</li>
 *   <li><strong>임베딩 모델 설정</strong>: 문서 벡터화를 위한 임베딩 모델 선택</li>
 *   <li><strong>벡터 DB 연결</strong>: 임베딩 벡터 저장을 위한 벡터 데이터베이스 설정</li>
 *   <li><strong>문서 처리 설정</strong>: 로더, 스플리터, 청크 크기 등 문서 처리 파라미터 설정</li>
 *   <li><strong>인덱싱 준비</strong>: 설정 완료 후 인덱싱을 통한 검색 가능 상태 구축</li>
 * </ol>
 * 
 * <h3>지원하는 로더 유형:</h3>
 * <ul>
 *   <li><strong>Default</strong>: 기본 문서 로더 (PDF, DOCX, TXT 등 지원)</li>
 *   <li><strong>DataIngestionTool</strong>: 등록된 Tool을 사용한 고급 문서 처리</li>
 *   <li><strong>CustomLoader</strong>: 사용자 정의 로더 스크립트 사용</li>
 * </ul>
 * 
 * <h3>지원하는 스플리터 유형:</h3>
 * <ul>
 *   <li><strong>RecursiveCharacter</strong>: 재귀적 문자 기반 분할 (권장)</li>
 *   <li><strong>Character</strong>: 단순 문자 기반 분할</li>
 *   <li><strong>Semantic</strong>: 의미 기반 분할</li>
 *   <li><strong>CustomSplitter</strong>: 사용자 정의 스플리터</li>
 *   <li><strong>NotSplit</strong>: 분할하지 않음</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoCreate repoRequest = RepoCreate.builder()
 *     .name("Customer Service KB")
 *     .description("고객 서비스 지식 베이스")
 *     .datasourceId("datasource-123")
 *     .embeddingModelName("GIP/text-embedding-3-large")
 *     .vectorDbId("vectordb-456")
 *     .loader("Default")
 *     .splitter("RecursiveCharacter")
 *     .chunkSize(1000)
 *     .chunkOverlap(50)
 *     .separator("\n")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoCreateResponse Repository 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Repository 생성 요청 정보",
    example = """
        {
          "name": "Customer Service KB",
          "description": "고객 서비스 지식 베이스",
          "datasource_id": "datasource-123",
          "embedding_model_name": "GIP/text-embedding-3-large",
          "vector_db_id": "vectordb-456",
          "loader": "Default",
          "splitter": "RecursiveCharacter",
          "chunk_size": 1000,
          "chunk_overlap": 50,
          "separator": "\\n"
        }
        """
)
public class RepoCreate {

    /**
     * Repository 이름
     * 
     * <p>Knowledge Repository의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, Repository의 목적이나 내용을 나타내는 명확한 이름을 사용합니다.</p>
     * 
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Repository 고유 이름 (프로젝트 내 유일해야 함)",
        example = "Customer Service KB",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;

    /**
     * Repository 설명
     * 
     * <p>Repository의 목적과 포함된 지식의 특성을 설명하는 텍스트입니다.
     * 다른 사용자들이 Repository의 용도를 이해할 수 있도록 명확하게 작성합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Repository 설명 (목적과 내용)",
        example = "고객 서비스 지식 베이스",
        maxLength = 500
    )
    private String description;

    /**
     * 데이터 소스 식별자
     * 
     * <p>Repository와 연결할 DataSource의 식별자입니다.
     * DataSource에 등록된 파일들이 Repository의 지식 콘텐츠 원본이 됩니다.</p>
     * 
     * @apiNote DataSource는 사전에 생성되어야 하며, 처리 가능한 파일들이 포함되어야 합니다.
     */
    @JsonProperty("datasource_id")
    @Schema(
        description = "연결할 DataSource ID (UUID 형식)",
        example = "datasource-123",
        required = true,
        format = "uuid"
    )
    private String datasourceId;

    /**
     * 임베딩 모델 이름
     * 
     * <p>문서를 벡터로 변환할 때 사용할 임베딩 모델의 이름입니다.
     * 선택한 모델에 따라 검색 품질과 성능이 달라집니다.</p>
     * 
     * <h4>지원하는 주요 모델:</h4>
     * <ul>
     *   <li><strong>GIP/text-embedding-3-large</strong>: 고성능 다국어 모델 (권장)</li>
     *   <li><strong>GIP/text-embedding-3-small</strong>: 경량화된 빠른 모델</li>
     *   <li><strong>OpenAI/text-embedding-ada-002</strong>: OpenAI 표준 모델</li>
     * </ul>
     * 
     * @implNote 모델 선택 시 처리할 문서의 언어와 도메인 특성을 고려해야 합니다.
     */
    @JsonProperty("embedding_model_name")
    @Schema(
        description = "임베딩 모델 이름 (문서 벡터화에 사용)",
        example = "GIP/text-embedding-3-large",
        required = true
    )
    private String embeddingModelName;

    /**
     * 벡터 데이터베이스 식별자
     * 
     * <p>임베딩 벡터를 저장할 벡터 데이터베이스의 식별자입니다.
     * Repository의 모든 문서 벡터가 이 데이터베이스에 인덱싱됩니다.</p>
     * 
     * @apiNote 벡터 DB는 사전에 설정되어야 하며, 선택한 임베딩 모델과 호환되어야 합니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "벡터 데이터베이스 ID (UUID 형식)",
        example = "vectordb-456",
        required = true,
        format = "uuid"
    )
    private String vectorDbId;

    /**
     * 문서 로더 유형
     * 
     * <p>문서를 읽고 파싱할 때 사용할 로더의 유형입니다.
     * 문서 형식과 처리 요구사항에 따라 적절한 로더를 선택합니다.</p>
     * 
     * <h4>로더 유형별 특징:</h4>
     * <ul>
     *   <li><strong>Default</strong>: 범용 문서 로더, 대부분의 표준 문서 형식 지원</li>
     *   <li><strong>DataIngestionTool</strong>: 등록된 Tool을 사용한 고급 문서 처리 (OCR, 구조 분석 등)</li>
     *   <li><strong>CustomLoader</strong>: 사용자 정의 로더 스크립트 (특수 형식 처리)</li>
     * </ul>
     * 
     * @implNote Tool 사용 시 toolId, CustomLoader 사용 시 customLoaderId를 함께 지정해야 합니다.
     */
    @JsonProperty("loader")
    @Schema(
        description = "문서 로더 유형",
        example = "Default",
        required = true,
        allowableValues = {"Default", "DataIngestionTool", "CustomLoader"}
    )
    private String loader;

    /**
     * 문서 스플리터 유형
     * 
     * <p>문서를 검색 가능한 청크(chunk)로 분할할 때 사용할 스플리터의 유형입니다.
     * 문서의 특성과 검색 요구사항에 따라 적절한 분할 방식을 선택합니다.</p>
     * 
     * <h4>스플리터 유형별 특징:</h4>
     * <ul>
     *   <li><strong>RecursiveCharacter</strong>: 문단, 문장, 단어 순으로 재귀적 분할 (권장)</li>
     *   <li><strong>Character</strong>: 단순 문자 수 기반 분할</li>
     *   <li><strong>Semantic</strong>: 의미 단위 기반 지능형 분할</li>
     *   <li><strong>CustomSplitter</strong>: 사용자 정의 분할 스크립트</li>
     *   <li><strong>NotSplit</strong>: 분할하지 않고 전체 문서 사용</li>
     * </ul>
     * 
     * @implNote CustomSplitter 사용 시 customSplitterId를 함께 지정해야 합니다.
     */
    @JsonProperty("splitter")
    @Schema(
        description = "문서 스플리터 유형",
        example = "RecursiveCharacter",
        required = true,
        allowableValues = {"RecursiveCharacter", "Character", "Semantic", "CustomSplitter", "NotSplit"}
    )
    private String splitter;

    /**
     * 청크 크기
     * 
     * <p>문서를 분할할 때 각 청크의 최대 문자 수입니다.
     * 청크 크기는 검색 정확도와 성능에 직접적인 영향을 미칩니다.</p>
     * 
     * <h4>크기별 특성:</h4>
     * <ul>
     *   <li><strong>500-800자</strong>: 정확한 검색, 세밀한 매칭</li>
     *   <li><strong>1000-1500자</strong>: 균형잡힌 검색 (권장)</li>
     *   <li><strong>2000자 이상</strong>: 컨텍스트 유지, 넓은 범위 검색</li>
     * </ul>
     * 
     * @implNote 임베딩 모델의 최대 토큰 길이를 고려하여 설정해야 합니다.
     */
    @JsonProperty("chunk_size")
    @Schema(
        description = "청크 크기 (문자 수, 임베딩 모델의 토큰 제한 고려)",
        example = "1000",
        minimum = "100",
        maximum = "4000"
    )
    private Integer chunkSize;

    /**
     * 청크 오버랩
     * 
     * <p>인접한 청크 간의 중복되는 문자 수입니다.
     * 오버랩을 통해 청크 경계에서 의미가 단절되는 것을 방지합니다.</p>
     * 
     * <h4>오버랩 비율 권장사항:</h4>
     * <ul>
     *   <li><strong>5-10%</strong>: 일반적인 문서 (청크 크기의 5-10%)</li>
     *   <li><strong>10-20%</strong>: 기술 문서, 논문 등 연속성이 중요한 문서</li>
     *   <li><strong>0%</strong>: 독립적인 항목들로 구성된 문서</li>
     * </ul>
     * 
     * @implNote 오버랩이 클수록 검색 정확도는 향상되지만, 저장 공간과 처리 시간이 증가합니다.
     */
    @JsonProperty("chunk_overlap")
    @Schema(
        description = "청크 오버랩 (문자 수, 인접 청크 간 중복)",
        example = "50",
        minimum = "0",
        maximum = "1000"
    )
    private Integer chunkOverlap;

    /**
     * 분할 구분자
     * 
     * <p>문서를 분할할 때 우선적으로 사용할 구분자입니다.
     * 스플리터가 자연스러운 경계를 찾기 위해 사용하는 기준점입니다.</p>
     * 
     * <h4>일반적인 구분자:</h4>
     * <ul>
     *   <li><strong>\\n</strong>: 줄 바꿈 (일반 문서)</li>
     *   <li><strong>\\n\\n</strong>: 문단 구분 (긴 문서)</li>
     *   <li><strong>.</strong>: 문장 단위 (세밀한 분할)</li>
     *   <li><strong>사용자 정의</strong>: 특수 구분자 (구조화된 문서)</li>
     * </ul>
     * 
     * @implNote null인 경우 스플리터의 기본 구분자가 사용됩니다.
     */
    @JsonProperty("separator")
    @Schema(
        description = "분할 구분자 (우선 경계 기준)",
        example = "\\n",
        maxLength = 50
    )
    private String separator;

    /**
     * 사용자 정의 로더 식별자 (선택적)
     * 
     * <p>loader가 "CustomLoader"인 경우 사용할 사용자 정의 로더의 식별자입니다.
     * 특수한 문서 형식이나 복잡한 파싱 로직이 필요한 경우 사용합니다.</p>
     * 
     * @apiNote loader가 "CustomLoader"인 경우에만 필수입니다.
     */
    @JsonProperty("custom_loader_id")
    @Schema(
        description = "사용자 정의 로더 ID (loader가 CustomLoader인 경우 필수)",
        example = "custom-loader-789",
        format = "uuid"
    )
    private String customLoaderId;

    /**
     * 사용자 정의 스플리터 식별자 (선택적)
     * 
     * <p>splitter가 "CustomSplitter"인 경우 사용할 사용자 정의 스플리터의 식별자입니다.
     * 도메인별 특수한 분할 로직이 필요한 경우 사용합니다.</p>
     * 
     * @apiNote splitter가 "CustomSplitter"인 경우에만 필수입니다.
     */
    @JsonProperty("custom_splitter_id")
    @Schema(
        description = "사용자 정의 스플리터 ID (splitter가 CustomSplitter인 경우 필수)",
        example = "custom-splitter-012",
        format = "uuid"
    )
    private String customSplitterId;

    /**
     * Tool 식별자 (선택적)
     * 
     * <p>loader가 "DataIngestionTool"인 경우 사용할 Tool의 식별자입니다.
     * OCR, 문서 분석 등의 고급 처리 기능을 제공하는 Tool을 활용합니다.</p>
     * 
     * @apiNote loader가 "DataIngestionTool"인 경우에만 필수입니다.
     */
    @JsonProperty("tool_id")
    @Schema(
        description = "Tool ID (loader가 DataIngestionTool인 경우 필수)",
        example = "tool-345",
        format = "uuid"
    )
    private String toolId;

    /**
     * 프로젝트 식별자 (선택적)
     * 
     * <p>Repository가 속할 프로젝트의 식별자입니다.
     * 지정하지 않으면 현재 사용자의 기본 프로젝트에 생성됩니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Repository가 속할 프로젝트 ID",
        example = "project-999"
    )
    private String projectId;

    /**
     * 생성자 정보 (선택적)
     * 
     * <p>Repository를 생성하는 사용자의 식별 정보입니다.
     * 지정하지 않으면 현재 인증된 사용자 정보가 사용됩니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
        description = "Repository 생성자 정보",
        example = "user@example.com"
    )
    private String createdBy;
}
