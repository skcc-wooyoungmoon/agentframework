package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * SKTAI Knowledge External Repository 생성 요청 DTO
 * 
 * <p>외부 Knowledge Repository를 등록하기 위한 요청 데이터 구조입니다.
 * 다른 시스템의 지식 저장소를 SKTAI Knowledge 시스템에 연동하여 통합 검색이 가능하도록 합니다.</p>
 * 
 * <h3>External Repository 유형:</h3>
 * <ul>
 *   <li><strong>REST API</strong>: RESTful API를 통한 외부 시스템 연동</li>
 *   <li><strong>GraphQL</strong>: GraphQL 엔드포인트를 통한 데이터 조회</li>
 *   <li><strong>Database</strong>: 직접 데이터베이스 연결</li>
 *   <li><strong>File System</strong>: 파일 시스템 기반 지식 저장소</li>
 *   <li><strong>S3 Compatible</strong>: S3 호환 스토리지 연동</li>
 * </ul>
 * 
 * <h3>연동 프로세스:</h3>
 * <ol>
 *   <li><strong>연결 설정</strong>: 외부 시스템의 엔드포인트 및 인증 정보 설정</li>
 *   <li><strong>스키마 매핑</strong>: 외부 데이터 구조를 SKTAI 스키마로 매핑</li>
 *   <li><strong>연결 테스트</strong>: 설정된 정보로 외부 시스템 접근 가능성 확인</li>
 *   <li><strong>동기화 설정</strong>: 데이터 동기화 주기 및 방식 설정</li>
 * </ol>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoExtCreateRequest extRequest = RepoExtCreateRequest.builder()
 *     .name("HR Knowledge Base")
 *     .description("인사 관련 지식 베이스")
 *     .type("REST_API")
 *     .endpoint("https://hr-kb.company.com/api/v1")
 *     .authConfig(Map.of(
 *         "type", "bearer_token",
 *         "token", "eyJhbGciOiJIUzI1NiIs..."
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoExtInfo External Repository 정보 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge External Repository 생성 요청 정보")
public class RepoExtCreateRequest {

    /**
     * External Repository 이름
     * 
     * <p>외부 Repository의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 연동되는 시스템의 특성을 나타내는 명확한 이름을 사용합니다.</p>
     */
    @JsonProperty("name")
    @Schema(description = "External Repository 이름", example = "HR Knowledge Base", required = true)
    private String name;

    /**
     * External Repository 설명
     * 
     * <p>외부 Repository의 목적과 포함된 데이터의 특성을 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(description = "External Repository 설명", example = "인사 관련 지식 베이스")
    private String description;

    /**
     * External Repository 유형
     * 
     * <p>연동할 외부 시스템의 유형입니다.
     * 유형에 따라 필요한 설정 정보가 달라집니다.</p>
     * 
     * <h4>지원하는 유형:</h4>
     * <ul>
     *   <li><strong>REST_API</strong>: RESTful API 기반 시스템</li>
     *   <li><strong>GRAPHQL</strong>: GraphQL 엔드포인트</li>
     *   <li><strong>DATABASE</strong>: 직접 데이터베이스 연결</li>
     *   <li><strong>FILE_SYSTEM</strong>: 파일 시스템 기반</li>
     *   <li><strong>S3_COMPATIBLE</strong>: S3 호환 스토리지</li>
     * </ul>
     */
    @JsonProperty("type")
    @Schema(
        description = "External Repository 유형",
        example = "REST_API",
        required = true,
        allowableValues = {"REST_API", "GRAPHQL", "DATABASE", "FILE_SYSTEM", "S3_COMPATIBLE"}
    )
    private String type;

    /**
     * 엔드포인트 URL
     * 
     * <p>외부 시스템에 접근하기 위한 기본 URL입니다.
     * 유형에 따라 API 엔드포인트, 데이터베이스 연결 문자열, 파일 경로 등이 될 수 있습니다.</p>
     */
    @JsonProperty("endpoint")
    @Schema(description = "외부 시스템 엔드포인트 URL", example = "https://hr-kb.company.com/api/v1", required = true)
    private String endpoint;

    /**
     * 인증 설정
     * 
     * <p>외부 시스템에 접근하기 위한 인증 정보입니다.
     * 유형에 따라 API 키, Bearer 토큰, 데이터베이스 자격증명 등이 포함됩니다.</p>
     * 
     * <h4>REST API 인증 예시:</h4>
     * <pre>
     * {
     *   "type": "bearer_token",
     *   "token": "eyJhbGciOiJIUzI1NiIs..."
     * }
     * </pre>
     * 
     * <h4>Database 인증 예시:</h4>
     * <pre>
     * {
     *   "type": "username_password",
     *   "username": "kb_user",
     *   "password": "secure_password"
     * }
     * </pre>
     */
    @JsonProperty("auth_config")
    @Schema(description = "인증 설정 정보")
    private Map<String, Object> authConfig;

    /**
     * 데이터 매핑 설정
     * 
     * <p>외부 시스템의 데이터 구조를 SKTAI Knowledge 스키마로 매핑하는 설정입니다.
     * 필드 매핑, 데이터 변환 규칙 등이 포함됩니다.</p>
     * 
     * <h4>매핑 설정 예시:</h4>
     * <pre>
     * {
     *   "title_field": "document_title",
     *   "content_field": "document_content",
     *   "metadata_fields": {
     *     "category": "doc_category",
     *     "author": "created_by"
     *   }
     * }
     * </pre>
     */
    @JsonProperty("data_mapping")
    @Schema(description = "데이터 매핑 설정")
    private Map<String, Object> dataMapping;

    /**
     * 동기화 설정
     * 
     * <p>외부 시스템과의 데이터 동기화 방식과 주기를 설정합니다.</p>
     * 
     * <h4>동기화 설정 예시:</h4>
     * <pre>
     * {
     *   "sync_mode": "incremental",
     *   "sync_interval": "1h",
     *   "batch_size": 100,
     *   "enable_auto_sync": true
     * }
     * </pre>
     */
    @JsonProperty("sync_config")
    @Schema(description = "동기화 설정")
    private Map<String, Object> syncConfig;

    /**
     * 연결 옵션
     * 
     * <p>외부 시스템 연결 시 사용할 추가 옵션들입니다.
     * 타임아웃, 재시도 설정, SSL 옵션 등이 포함됩니다.</p>
     * 
     * <h4>연결 옵션 예시:</h4>
     * <pre>
     * {
     *   "timeout": 30000,
     *   "retry_count": 3,
     *   "ssl_verify": true,
     *   "connection_pool_size": 10
     * }
     * </pre>
     */
    @JsonProperty("connection_options")
    @Schema(description = "연결 옵션")
    private Map<String, Object> connectionOptions;

    /**
     * 활성화 상태
     * 
     * <p>External Repository의 활성화 상태입니다.
     * false인 경우 연동이 중단되며, 검색 시 제외됩니다.</p>
     */
    @JsonProperty("enabled")
    @Schema(description = "활성화 상태", example = "true")
    private Boolean enabled;

    /**
     * 우선순위
     * 
     * <p>검색 시 외부 Repository들 간의 우선순위입니다.
     * 낮은 숫자가 높은 우선순위를 가집니다.</p>
     */
    @JsonProperty("priority")
    @Schema(description = "검색 우선순위 (낮을수록 높은 우선순위)", example = "1")
    private Integer priority;

    /**
     * 메타데이터
     * 
     * <p>External Repository에 대한 추가 메타데이터입니다.
     * 분류, 태그, 관리 정보 등을 포함할 수 있습니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터")
    private Map<String, Object> metadata;

    /**
     * 프로젝트 ID (선택적)
     * 
     * <p>External Repository가 속할 프로젝트의 식별자입니다.
     * 지정하지 않으면 현재 사용자의 기본 프로젝트에 생성됩니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "project-123")
    private String projectId;

    /**
     * 생성자 정보 (선택적)
     * 
     * <p>External Repository를 생성하는 사용자의 식별 정보입니다.
     * 지정하지 않으면 현재 인증된 사용자 정보가 사용됩니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자 정보", example = "admin@example.com")
    private String createdBy;

    /**
     * 테스트 모드 여부 (선택적)
     * 
     * <p>생성 시 연결 테스트를 수행할지 여부입니다.
     * true인 경우 생성 전에 외부 시스템 접근 가능성을 확인합니다.</p>
     */
    @JsonProperty("test_connection")
    @Schema(description = "생성 시 연결 테스트 수행 여부", example = "true")
    private Boolean testConnection;

    /**
     * 임베딩 모델 이름 (필수)
     * 
     * <p>External Repository에서 사용할 임베딩 모델의 이름입니다.</p>
     */
    @JsonProperty("embedding_model_name")
    @Schema(description = "임베딩 모델 이름", example = "GIP/text-embedding-3-large-new", required = true)
    private String embeddingModelName;

    /**
     * Vector DB ID (필수)
     * 
     * <p>External Repository에서 사용할 Vector DB의 식별자입니다.</p>
     */
    @JsonProperty("vector_db_id")
    @Schema(description = "Vector DB ID", example = "78c05a11-cdbe-43a4-887e-66fc3b6d1d16", required = true)
    private String vectorDbId;

    /**
     * 인덱스 이름 (필수)
     * 
     * <p>Vector DB에서 사용할 인덱스 이름입니다.</p>
     */
    @JsonProperty("index_name")
    @Schema(description = "인덱스 이름", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000", required = true)
    private String indexName;

    /**
     * 검색 스크립트 (선택적)
     * 
     * <p>External Repository에서 사용할 검색 스크립트입니다.</p>
     */
    @JsonProperty("script")
    @Schema(description = "검색 스크립트")
    private String script;
}
