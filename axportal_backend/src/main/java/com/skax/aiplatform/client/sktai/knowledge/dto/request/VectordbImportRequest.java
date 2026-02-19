package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectordbImportResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector Database Import 요청 DTO
 * 
 * <p>외부 Vector Database를 SKTAI Knowledge 시스템으로 Import하기 위한 요청 데이터 구조입니다.
 * Milvus, Azure AI Search, OpenSearch, ElasticSearch 등 다양한 Vector Database를 지원합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Vector DB Import</strong>: 외부 Vector Database 연결 정보 등록</li>
 *   <li><strong>다중 DB 지원</strong>: Milvus, Azure AI Search, OpenSearch, ElasticSearch</li>
 *   <li><strong>연결 정보 관리</strong>: DB별 연결 설정 및 인증 정보 저장</li>
 *   <li><strong>기본 DB 설정</strong>: 프로젝트의 기본 Vector Database 지정</li>
 * </ul>
 * 
 * <h3>지원하는 Vector Database 타입:</h3>
 * <ul>
 *   <li><strong>Milvus</strong>: 오픈소스 벡터 데이터베이스</li>
 *   <li><strong>AzureAISearch</strong>: Microsoft Azure AI Search 서비스</li>
 *   <li><strong>AzureAISearchShare</strong>: 공유 Azure AI Search 인스턴스</li>
 *   <li><strong>OpenSearch</strong>: AWS OpenSearch 서비스</li>
 *   <li><strong>ElasticSearch</strong>: Elasticsearch 벡터 검색</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>기존 Vector Database를 SKTAI 시스템에 연결</li>
 *   <li>다른 프로젝트의 Vector Database 공유</li>
 *   <li>멀티 클라우드 환경에서 Vector Database 통합 관리</li>
 *   <li>백업 또는 복구를 위한 Vector Database 등록</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Milvus Vector Database Import
 * VectordbImportRequest request = VectordbImportRequest.builder()
 *     .name("Milvus Vector Store")
 *     .type("Milvus")
 *     .connectionInfo(Map.of(
 *         "host", "localhost",
 *         "port", 19530,
 *         "user", "root",
 *         "password", "milvus123"
 *     ))
 *     .isDefault(true)
 *     .build();
 * 
 * // Azure AI Search Import
 * VectordbImportRequest azureRequest = VectordbImportRequest.builder()
 *     .name("Azure AI Search")
 *     .type("AzureAISearch")
 *     .connectionInfo(Map.of(
 *         "endpoint", "https://my-search.search.windows.net",
 *         "key", "API_KEY_HERE"
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 * @see VectordbImportResponse Vector Database Import 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Vector Database Import 요청 정보",
    example = """
        {
          "name": "My Milvus DB",
          "type": "Milvus",
          "connection_info": {
            "host": "localhost",
            "port": 19530,
            "user": "root",
            "password": "milvus123"
          },
          "is_default": true
        }
        """
)
public class VectordbImportRequest {
    
    /**
     * Vector Database 이름
     * 
     * <p>Import할 Vector Database의 고유한 이름입니다.
     * 프로젝트 내에서 중복되지 않는 이름이어야 합니다.</p>
     * 
     * @implNote 최대 200자까지 입력 가능하며, 명확하고 설명적인 이름을 사용합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Vector Database 이름 (최대 200자)", 
        example = "My Milvus DB",
        required = true,
        maxLength = 200
    )
    private String name;
    
    /**
     * Vector Database 타입
     * 
     * <p>Import할 Vector Database의 종류입니다.
     * 지원하는 타입: Milvus, AzureAISearch, AzureAISearchShare, OpenSearch, ElasticSearch</p>
     * 
     * @apiNote 타입에 따라 connection_info의 필수 필드가 달라집니다.
     */
    @JsonProperty("type")
    @Schema(
        description = "Vector Database 타입", 
        example = "Milvus",
        required = true,
        allowableValues = {"Milvus", "AzureAISearch", "AzureAISearchShare", "OpenSearch", "ElasticSearch"}
    )
    private String type;
    
    /**
     * 연결 정보
     * 
     * <p>Vector Database에 연결하기 위한 설정 정보입니다.
     * Database 타입에 따라 필요한 필드가 다릅니다.</p>
     * 
     * <h4>Milvus 연결 정보:</h4>
     * <ul>
     *   <li>host: 호스트 주소 (예: localhost)</li>
     *   <li>port: 포트 번호 (예: 19530)</li>
     *   <li>user: 사용자명 (예: root)</li>
     *   <li>password: 비밀번호</li>
     * </ul>
     * 
     * <h4>Azure AI Search 연결 정보:</h4>
     * <ul>
     *   <li>endpoint: Azure Search 엔드포인트 URL</li>
     *   <li>key: API 키</li>
     * </ul>
     * 
     * <h4>OpenSearch / ElasticSearch 연결 정보:</h4>
     * <ul>
     *   <li>hosts: 호스트 목록 (배열)</li>
     *   <li>username: 사용자명</li>
     *   <li>password: 비밀번호</li>
     *   <li>use_ssl: SSL 사용 여부 (boolean)</li>
     * </ul>
     * 
     * @implNote 연결 정보는 암호화되어 저장되며, 민감한 정보는 로그에 출력되지 않습니다.
     */
    @JsonProperty("connection_info")
    @Schema(
        description = "Vector Database 연결 정보 (타입별로 필드가 다름)", 
        example = """
            {
              "host": "localhost",
              "port": 19530,
              "user": "root",
              "password": "milvus123"
            }
            """,
        required = true
    )
    private Map<String, Object> connectionInfo;
    
    /**
     * 기본 Vector Database 설정 여부
     * 
     * <p>이 Vector Database를 프로젝트의 기본 DB로 설정할지 여부입니다.
     * 기본값은 false입니다.</p>
     * 
     * @implNote 기본 DB는 새로운 Repository 생성 시 자동으로 선택됩니다.
     */
    @JsonProperty("is_default")
    @Schema(
        description = "기본 Vector Database 설정 여부", 
        example = "true",
        defaultValue = "false"
    )
    private Boolean isDefault;
    
    /**
     * 생성자 식별자 (선택)
     * 
     * <p>Vector Database를 등록하는 사용자의 식별자입니다.
     * 지정하지 않으면 현재 인증된 사용자로 자동 설정됩니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
        description = "생성자 식별자 (선택)", 
        example = "user@example.com"
    )
    private String createdBy;
    
    /**
     * 프로젝트 식별자 (선택)
     * 
     * <p>Vector Database가 속할 프로젝트의 고유 식별자입니다.
     * 지정하지 않으면 현재 프로젝트로 자동 설정됩니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 쓰기 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Vector Database가 속할 프로젝트 ID (선택)", 
        example = "project-456"
    )
    private String projectId;
    
    /**
     * Vector Database 식별자 (선택)
     * 
     * <p>특정 UUID를 지정하여 Vector Database를 생성합니다.
     * 지정하지 않으면 시스템이 자동으로 UUID를 생성합니다.</p>
     * 
     * @implNote 주로 마이그레이션이나 복구 작업 시 기존 UUID를 유지하기 위해 사용됩니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "Vector Database ID (선택, UUID)", 
        example = "a0f59edd-6766-4758-92a3-13c066648bc0",
        format = "uuid"
    )
    private String id;
    
    /**
     * 태그 목록 (선택)
     * 
     * <p>Vector Database 분류 및 검색을 위한 태그 목록입니다.
     * JSON 배열 형태의 문자열로 전달됩니다.</p>
     * 
     * @implNote 태그는 Database 관리 및 필터링에 사용됩니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "태그 목록 JSON 문자열 (선택)", 
        example = "[{\"name\":\"Production\"},{\"name\":\"Milvus\"}]"
    )
    private String tags;
    
    /**
     * 정책 설정 (선택)
     * 
     * <p>Vector Database에 적용할 접근 권한 정책입니다.
     * 지정하지 않으면 기본 정책이 적용됩니다.</p>
     * 
     * @implNote PolicyPayload 형식을 따르며, 사용자/그룹/역할 기반 권한을 설정할 수 있습니다.
     */
    @JsonProperty("policy")
    @Schema(
        description = "접근 권한 정책 설정 (선택)"
    )
    private Object policy;
}
