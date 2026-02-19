package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI External Knowledge Repository Import 요청 DTO
 * 
 * <p>외부 Knowledge Repository를 SKTAI 시스템으로 Import하기 위한 요청 데이터 구조입니다.
 * 기존에 등록된 External Repository의 설정과 데이터를 기반으로 새로운 Internal Repository를 생성합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>External Repository Import</strong>: 외부 Knowledge Repository 데이터 가져오기</li>
 *   <li><strong>설정 복제</strong>: 외부 Repository의 설정을 Internal Repository로 복제</li>
 *   <li><strong>데이터 동기화</strong>: 외부 데이터를 내부 환경에 동기화</li>
 *   <li><strong>Vector DB 연결</strong>: 지정된 Vector Database와 연결</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>다른 프로젝트의 Knowledge Repository를 복제</li>
 *   <li>외부 시스템의 지식 데이터를 통합</li>
 *   <li>백업된 Repository를 복원</li>
 *   <li>테스트 환경에서 운영 환경으로 Repository 마이그레이션</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoExtImportRequest request = RepoExtImportRequest.builder()
 *     .id("11111111-1111-1111-1111-111111111111")
 *     .name("Imported Knowledge Repo")
 *     .description("외부에서 가져온 지식 저장소")
 *     .embeddingModelName("GIP/text-embedding-3-large")
 *     .vectorDbId("a0f59edd-6766-4758-92a3-13c066648bc0")
 *     .indexName("corus-rb086ea76-1451-4d4f-bec3-e8978645134d")
 *     .script("retrieval script content")
 *     .scriptHash("049130048ad04579a645eebba900dd12")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 * @see RepoResponse External Repository Import 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI External Knowledge Repository Import 요청 정보",
    example = """
        {
          "id": "11111111-1111-1111-1111-111111111111",
          "name": "test_ext_knowledge",
          "description": "Test external knowledge",
          "embedding_model_name": "GIP/text-embedding-3-large",
          "vector_db_id": "a0f59edd-6766-4758-92a3-13c066648bc0",
          "index_name": "corus-rb086ea76-1451-4d4f-bec3-e8978645134d",
          "script": "retrieval script content",
          "script_hash": "049130048ad04579a645eebba900dd12"
        }
        """
)
public class RepoExtImportRequest {
    
    /**
     * External Repository 식별자
     * 
     * <p>Import할 External Knowledge Repository의 고유 식별자입니다.
     * 사전에 등록된 External Repository의 UUID를 사용해야 합니다.</p>
     * 
     * @apiNote External Repository는 /api/v1/knowledge/repos/external 엔드포인트를 통해 조회 가능합니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "Import할 External Repository ID (UUID)", 
        example = "11111111-1111-1111-1111-111111111111",
        required = true,
        format = "uuid"
    )
    private String id;
    
    /**
     * Repository 이름
     * 
     * <p>Import할 Repository의 이름입니다.
     * 프로젝트 내에서 중복되지 않는 고유한 이름이어야 합니다.</p>
     * 
     * @implNote 최대 200자까지 입력 가능하며, 영문자, 숫자, 언더스코어를 사용합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Import할 Repository 이름 (최대 200자)", 
        example = "test_ext_knowledge",
        required = true,
        maxLength = 200
    )
    private String name;
    
    /**
     * Repository 설명
     * 
     * <p>Import한 Repository의 설명입니다.
     * Repository의 목적과 용도를 명확하게 작성합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Import할 Repository 설명", 
        example = "Test external knowledge",
        required = true
    )
    private String description;
    
    /**
     * 임베딩 모델 이름
     * 
     * <p>Import할 Repository에서 사용할 임베딩 모델의 Serving 이름입니다.
     * 벡터 검색을 위한 문서 임베딩 생성에 사용됩니다.</p>
     * 
     * @apiNote 모델 이름은 SKTAI 서빙 시스템에 등록된 모델명을 사용해야 합니다.
     */
    @JsonProperty("embedding_model_name")
    @Schema(
        description = "임베딩 모델 Serving 이름", 
        example = "GIP/text-embedding-3-large",
        required = true
    )
    private String embeddingModelName;
    
    /**
     * Vector Database 식별자
     * 
     * <p>Import한 Repository가 사용할 Vector Database의 식별자입니다.
     * 벡터 임베딩을 저장하고 검색하는 데 사용됩니다.</p>
     * 
     * @apiNote Vector Database는 /api/v1/knowledge/vectordbs 엔드포인트를 통해 조회 가능합니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "Vector Database ID (UUID)", 
        example = "a0f59edd-6766-4758-92a3-13c066648bc0",
        required = true,
        format = "uuid"
    )
    private String vectorDbId;
    
    /**
     * Vector Database 인덱스 이름
     * 
     * <p>Vector Database 내에서 사용할 인덱스 이름입니다.
     * 벡터 검색을 위한 인덱스 구조를 지정합니다.</p>
     * 
     * @implNote 인덱스 이름은 Vector Database 시스템에 따라 명명 규칙이 다를 수 있습니다.
     */
    @JsonProperty("index_name")
    @Schema(
        description = "Vector Database 인덱스 이름", 
        example = "corus-rb086ea76-1451-4d4f-bec3-e8978645134d",
        required = true
    )
    private String indexName;
    
    /**
     * Retrieval Script 내용
     * 
     * <p>External Repository의 문서 검색을 위한 커스텀 스크립트입니다.
     * Python으로 작성된 검색 로직을 포함합니다.</p>
     * 
     * @implNote 스크립트는 async def get_relevant_documents() 함수를 구현해야 합니다.
     */
    @JsonProperty("script")
    @Schema(
        description = "Retrieval Script 내용 (Python)", 
        example = "async def get_relevant_documents(...):\\n    return []",
        required = true
    )
    private String script;
    
    /**
     * Retrieval Script 해시값
     * 
     * <p>스크립트 내용의 해시값입니다.
     * 스크립트 변경 여부를 확인하기 위해 사용됩니다.</p>
     * 
     * @implNote MD5 또는 SHA 해시 알고리즘을 사용하여 생성됩니다.
     */
    @JsonProperty("script_hash")
    @Schema(
        description = "Retrieval Script 해시값", 
        example = "049130048ad04579a645eebba900dd12",
        required = true
    )
    private String scriptHash;
    
    /**
     * 생성자 식별자 (선택)
     * 
     * <p>Repository를 생성하는 사용자의 식별자입니다.
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
     * <p>Import한 Repository가 속할 프로젝트의 고유 식별자입니다.
     * 지정하지 않으면 현재 프로젝트로 자동 설정됩니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 쓰기 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Import한 Repository가 속할 프로젝트 ID (선택)", 
        example = "project-456"
    )
    private String projectId;
    
    /**
     * 태그 목록 (선택)
     * 
     * <p>Repository 분류 및 검색을 위한 태그 목록입니다.
     * JSON 배열 형태의 문자열로 전달됩니다.</p>
     * 
     * @implNote 태그는 Repository 관리 및 필터링에 사용됩니다.
     */
    @JsonProperty("tags")
    @Schema(
        description = "태그 목록 JSON 문자열 (선택)", 
        example = "[{\"name\":\"AI\"},{\"name\":\"RAG\"}]"
    )
    private List<Object> tags;
    
    /**
     * 정책 설정 (선택)
     * 
     * <p>Repository에 적용할 접근 권한 정책입니다.
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
