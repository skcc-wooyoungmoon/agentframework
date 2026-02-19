# SKTAI Knowledge API 문서

## 개요
SKTAI Knowledge 시스템은 지식 기반 AI 서비스를 위한 종합 플랫폼입니다. Vector DB 관리, Knowledge Repository 운영, 문서 처리, 사용자 정의 스크립트, 검색 도구 등을 통해 효율적인 RAG(Retrieval-Augmented Generation) 환경을 제공합니다.

## Client 정보

### 1. SktaiVectorDbsClient
Vector Database 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- Vector DB 생성, 조회, 수정, 삭제
- Milvus, AzureAISearch, OpenSearch 등 지원
- 벡터 저장소 연결 상태 관리

### 2. SktaiReposClient
Knowledge Repository 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- Repository 생성, 조회, 수정, 삭제
- 문서 관리 (Document Management)
- 인덱싱 작업 관리
- 외부 Repository 연동

### 3. SktaiQueriesClient
지식 검색 및 조회를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 기본 지식 검색
- 고급 검색 기능
- 검색 테스트 기능

### 4. SktaiToolsClient
Knowledge Tool 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- Tool 생성, 조회, 수정, 삭제
- Knowledge 검색 도구 관리

### 5. SktaiCustomScriptsClient
사용자 정의 스크립트 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- Custom Script 업로드, 조회, 수정, 삭제
- Loader 및 Splitter 스크립트 테스트
- 파일 기반 스크립트 관리

### 6. SktaiChunkStoresClient
Chunk Store 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- Chunk Store 생성, 조회, 수정, 삭제
- 문서 청크 저장소 관리

## API 목록

### Vector DB Management APIs (SktaiVectorDbsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| Vector DB 목록 조회 | GET | `/api/v1/knowledge/vectordbs` | page, size, sort, filter, search | MultiResponse | 등록된 Vector DB 목록을 조회합니다 |
| Vector DB 신규 등록 | POST | `/api/v1/knowledge/vectordbs` | VectorDBCreate | VectorDBCreateResponse | 새로운 Vector DB를 등록합니다 |
| Vector DB 정보 조회 | GET | `/api/v1/knowledge/vectordbs/{vector_db_id}` | vectorDbId | VectorDBDetailResponse | 특정 Vector DB 상세 정보를 조회합니다 |
| Vector DB 정보 수정 | PUT | `/api/v1/knowledge/vectordbs/{vector_db_id}` | vectorDbId, VectorDBUpdate | VectorDBUpdateResponse | Vector DB 설정 정보를 수정합니다 |
| Vector DB 정보 삭제 | DELETE | `/api/v1/knowledge/vectordbs/{vector_db_id}` | vectorDbId | void | Vector DB를 완전히 삭제합니다 |

### Knowledge Repository APIs (SktaiReposClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| Repository 목록 조회 | GET | `/api/v1/knowledge/repos` | page, size, sort, filter, search | MultiResponse | Knowledge Repository 목록을 조회합니다 |
| Repository 생성 | POST | `/api/v1/knowledge/repos` | RepositoryCreate | RepositoryCreateResponse | 새로운 Knowledge Repository를 생성합니다 |
| Repository 상세 조회 | GET | `/api/v1/knowledge/repos/{repo_id}` | repoId | RepositoryDetailResponse | Repository 상세 정보를 조회합니다 |
| Repository 편집 모드 전환 | PUT | `/api/v1/knowledge/repos/{repo_id}/edit` | repoId | RepositoryEditResponse | Repository를 편집 모드로 전환합니다 |
| Repository 정보 수정 | PUT | `/api/v1/knowledge/repos/{repo_id}` | repoId, RepositoryUpdate | RepositoryUpdateResponse | Repository 정보를 수정합니다 |
| Repository 삭제 | DELETE | `/api/v1/knowledge/repos/{repo_id}` | repoId | void | Repository를 삭제합니다 |
| Retrieval 정보 조회 | GET | `/api/v1/knowledge/repos/retrieval_info` | - | RetrievalInfoResponse | Retrieval 관련 정보를 조회합니다 |
| Repository 인덱싱 시작 | POST | `/api/v1/knowledge/repos/{repo_id}/indexing` | repoId, IndexingRequest | IndexingResponse | Repository 인덱싱을 시작합니다 |
| Repository 인덱싱 중지 | POST | `/api/v1/knowledge/repos/{repo_id}/stop_indexing` | repoId | void | 진행 중인 인덱싱을 중지합니다 |
| Repository 수집 및 업데이트 | POST | `/api/v1/knowledge/repos/{repo_id}/collect_and_update` | repoId, CollectUpdateRequest | CollectUpdateResponse | Repository 데이터를 수집하고 업데이트합니다 |

### Document Management APIs (SktaiReposClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 문서 목록 조회 | GET | `/api/v1/knowledge/repos/{repo_id}/documents` | repoId, page, size, sort, filter, search | DocumentListResponse | Repository의 문서 목록을 조회합니다 |
| 문서 상세 조회 | GET | `/api/v1/knowledge/repos/{repo_id}/documents/{document_id}` | repoId, documentId | DocumentDetailResponse | 특정 문서의 상세 정보를 조회합니다 |
| 문서 메타데이터 수정 | PUT | `/api/v1/knowledge/repos/{repo_id}/documents` | repoId, DocumentBulkUpdate | DocumentBulkUpdateResponse | 여러 문서의 메타데이터를 수정합니다 |
| 문서 삭제 | DELETE | `/api/v1/knowledge/repos/{repo_id}/documents` | repoId, documentIds | void | 여러 문서를 삭제합니다 |
| 개별 문서 수정 | PUT | `/api/v1/knowledge/repos/{repo_id}/documents/{document_id}` | repoId, documentId, DocumentUpdate | DocumentUpdateResponse | 개별 문서를 수정합니다 |
| 개별 문서 삭제 | DELETE | `/api/v1/knowledge/repos/{repo_id}/documents/{document_id}` | repoId, documentId | void | 개별 문서를 삭제합니다 |
| 문서 인덱싱 | POST | `/api/v1/knowledge/repos/{repo_id}/documents/{document_id}/indexing` | repoId, documentId, DocumentIndexingRequest | DocumentIndexingResponse | 특정 문서를 인덱싱합니다 |

### External Repository APIs (SktaiReposClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 외부 Repository 목록 조회 | GET | `/api/v1/knowledge/repos/external` | page, size, sort, filter, search | ExternalRepoListResponse | 외부 Repository 목록을 조회합니다 |
| 외부 Repository 등록 | POST | `/api/v1/knowledge/repos/external` | ExternalRepoCreate | ExternalRepoCreateResponse | 외부 Repository를 등록합니다 |
| 외부 Repository 연결 테스트 | POST | `/api/v1/knowledge/repos/external/test` | ExternalRepoTest | ExternalRepoTestResponse | 외부 Repository 연결을 테스트합니다 |
| 외부 Repository 상세 조회 | GET | `/api/v1/knowledge/repos/external/{repo_id}` | repoId | ExternalRepoDetailResponse | 외부 Repository 상세 정보를 조회합니다 |
| 외부 Repository 수정 | PUT | `/api/v1/knowledge/repos/external/{repo_id}` | repoId, ExternalRepoUpdate | ExternalRepoUpdateResponse | 외부 Repository 정보를 수정합니다 |
| 외부 Repository 삭제 | DELETE | `/api/v1/knowledge/repos/external/{repo_id}` | repoId | void | 외부 Repository를 삭제합니다 |

### Knowledge Query APIs (SktaiQueriesClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 기본 지식 검색 | POST | `/api/v1/knowledge/queries` | QueryRequest | QueryResponse | 기본 지식 검색을 수행합니다 |
| 고급 지식 검색 | POST | `/api/v1/knowledge/queries/advanced` | AdvancedQueryRequest | AdvancedQueryResponse | 고급 지식 검색을 수행합니다 |
| 기본 검색 테스트 | POST | `/api/v1/knowledge/queries/test` | QueryTestRequest | QueryTestResponse | 기본 검색 기능을 테스트합니다 |
| 고급 검색 테스트 | POST | `/api/v1/knowledge/queries/test/advanced` | AdvancedQueryTestRequest | AdvancedQueryTestResponse | 고급 검색 기능을 테스트합니다 |

### Knowledge Tool APIs (SktaiToolsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| Tool 목록 조회 | GET | `/api/v1/knowledge/tools` | page, size, sort, filter, search | ToolListResponse | Knowledge Tool 목록을 조회합니다 |
| Tool 생성 | POST | `/api/v1/knowledge/tools` | ToolCreate | ToolCreateResponse | 새로운 Knowledge Tool을 생성합니다 |
| Tool 상세 조회 | GET | `/api/v1/knowledge/tools/{tool_id}` | toolId | ToolDetailResponse | Tool 상세 정보를 조회합니다 |
| Tool 수정 | PUT | `/api/v1/knowledge/tools/{tool_id}` | toolId, ToolUpdate | ToolUpdateResponse | Tool 정보를 수정합니다 |
| Tool 삭제 | DELETE | `/api/v1/knowledge/tools/{tool_id}` | toolId | void | Tool을 삭제합니다 |

### Custom Script APIs (SktaiCustomScriptsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| Custom Script 목록 조회 | GET | `/api/v1/knowledge/custom_scripts` | page, size, sort, filter, search | CustomScriptListResponse | Custom Script 목록을 조회합니다 |
| Custom Script 업로드 | POST | `/api/v1/knowledge/custom_scripts` | name, scriptType, description, file | CustomScriptCreateResponse | 새로운 Custom Script를 업로드합니다 |
| Custom Script 상세 조회 | GET | `/api/v1/knowledge/custom_scripts/{script_id}` | scriptId | CustomScriptDetailResponse | Custom Script 상세 정보를 조회합니다 |
| Custom Script 수정 | PUT | `/api/v1/knowledge/custom_scripts/{script_id}` | scriptId, name, description, file | CustomScriptUpdateResponse | Custom Script를 수정합니다 |
| Custom Script 삭제 | DELETE | `/api/v1/knowledge/custom_scripts/{script_id}` | scriptId | void | Custom Script를 삭제합니다 |
| Loader Script 테스트 | POST | `/api/v1/knowledge/custom_scripts/test/loader` | script, testData | ScriptTestResponse | Loader Script를 테스트합니다 |
| Splitter Script 테스트 | POST | `/api/v1/knowledge/custom_scripts/test/splitter` | script, testData | ScriptTestResponse | Splitter Script를 테스트합니다 |

### Chunk Store APIs (SktaiChunkStoresClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| Chunk Store 목록 조회 | GET | `/api/v1/knowledge/chunk_stores` | page, size, sort, filter, search | ChunkStoreListResponse | Chunk Store 목록을 조회합니다 |
| Chunk Store 생성 | POST | `/api/v1/knowledge/chunk_stores` | ChunkStoreCreate | ChunkStoreCreateResponse | 새로운 Chunk Store를 생성합니다 |
| Chunk Store 상세 조회 | GET | `/api/v1/knowledge/chunk_stores/{chunk_store_id}` | chunkStoreId | ChunkStoreDetailResponse | Chunk Store 상세 정보를 조회합니다 |
| Chunk Store 수정 | PUT | `/api/v1/knowledge/chunk_stores/{chunk_store_id}` | chunkStoreId, ChunkStoreUpdate | ChunkStoreUpdateResponse | Chunk Store 정보를 수정합니다 |
| Chunk Store 삭제 | DELETE | `/api/v1/knowledge/chunk_stores/{chunk_store_id}` | chunkStoreId | void | Chunk Store를 삭제합니다 |

## DTO 클래스

### Vector DB DTOs
- **VectorDBCreate**: Vector DB 생성 요청
- **VectorDBUpdate**: Vector DB 수정 요청
- **VectorDBCreateResponse**: Vector DB 생성 응답
- **VectorDBDetailResponse**: Vector DB 상세 정보
- **VectorDBUpdateResponse**: Vector DB 수정 응답

### Repository DTOs
- **RepositoryCreate**: Repository 생성 요청
- **RepositoryUpdate**: Repository 수정 요청
- **RepositoryCreateResponse**: Repository 생성 응답
- **RepositoryDetailResponse**: Repository 상세 정보
- **RepositoryUpdateResponse**: Repository 수정 응답
- **RepositoryEditResponse**: Repository 편집 응답

### Document DTOs
- **DocumentBulkUpdate**: 문서 일괄 수정 요청
- **DocumentUpdate**: 개별 문서 수정 요청
- **DocumentIndexingRequest**: 문서 인덱싱 요청
- **DocumentListResponse**: 문서 목록 응답
- **DocumentDetailResponse**: 문서 상세 정보
- **DocumentUpdateResponse**: 문서 수정 응답
- **DocumentIndexingResponse**: 문서 인덱싱 응답

### Query DTOs
- **QueryRequest**: 기본 검색 요청
- **AdvancedQueryRequest**: 고급 검색 요청
- **QueryTestRequest**: 검색 테스트 요청
- **AdvancedQueryTestRequest**: 고급 검색 테스트 요청
- **QueryResponse**: 검색 응답
- **AdvancedQueryResponse**: 고급 검색 응답
- **QueryTestResponse**: 검색 테스트 응답
- **AdvancedQueryTestResponse**: 고급 검색 테스트 응답

### Tool DTOs
- **ToolCreate**: Tool 생성 요청
- **ToolUpdate**: Tool 수정 요청
- **ToolCreateResponse**: Tool 생성 응답
- **ToolDetailResponse**: Tool 상세 정보
- **ToolUpdateResponse**: Tool 수정 응답
- **ToolListResponse**: Tool 목록 응답

### Custom Script DTOs
- **CustomScriptCreateResponse**: Custom Script 생성 응답
- **CustomScriptDetailResponse**: Custom Script 상세 정보
- **CustomScriptUpdateResponse**: Custom Script 수정 응답
- **CustomScriptListResponse**: Custom Script 목록 응답
- **ScriptTestResponse**: 스크립트 테스트 응답

### Chunk Store DTOs
- **ChunkStoreCreate**: Chunk Store 생성 요청
- **ChunkStoreUpdate**: Chunk Store 수정 요청
- **ChunkStoreCreateResponse**: Chunk Store 생성 응답
- **ChunkStoreDetailResponse**: Chunk Store 상세 정보
- **ChunkStoreUpdateResponse**: Chunk Store 수정 응답
- **ChunkStoreListResponse**: Chunk Store 목록 응답

### 공통 DTOs
- **MultiResponse**: 페이징된 목록 응답
- **MultipartFile**: 파일 업로드용 Spring 표준 타입
- **List<String>**: 문자열 리스트

## API 상세 정보

### Vector DB 관리
Vector Database는 Knowledge Repository의 핵심 구성 요소로, 문서의 벡터 임베딩을 저장하고 검색하는 역할을 합니다.

**지원 Vector DB 타입**:
- **Milvus**: 오픈소스 벡터 데이터베이스
- **AzureAISearch**: Azure AI Search 서비스
- **AzureAISearchShared**: 공유 Azure AI Search
- **OpenSearch**: OpenSearch 벡터 검색

### Knowledge Repository 관리
Knowledge Repository는 문서를 저장하고 관리하는 중앙 저장소입니다.

**Repository 생명주기**:
1. 생성: DataSource 기반 Repository 생성
2. 인덱싱: 문서 처리 및 벡터 인덱스 구축
3. 검색: Retrieval API를 통한 문서 검색
4. 관리: 문서 수정, 메타데이터 업데이트, 재인덱싱

### Document 관리
Repository 내의 문서들을 효율적으로 관리할 수 있습니다.

**주요 기능**:
- 개별/일괄 문서 메타데이터 수정
- 문서별 인덱싱 제어
- 문서 상태 및 처리 이력 관리

### 검색 기능
Knowledge 시스템의 핵심 기능인 검색을 제공합니다.

**검색 타입**:
- **기본 검색**: 단순 키워드 기반 검색
- **고급 검색**: 복합 조건 및 필터링 지원
- **테스트 검색**: 검색 성능 및 결과 검증

### Custom Script 관리
사용자 정의 로직을 통해 문서 처리를 커스터마이징할 수 있습니다.

**스크립트 타입**:
- **Loader**: 문서 로딩 로직
- **Splitter**: 문서 분할 로직

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.
- Authorization: Bearer {access_token}

## 오류 코드
- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 리소스를 찾을 수 없음
- **422 Unprocessable Entity**: 입력값 검증 실패

## 사용 예시

### Java (Spring Boot)
```java
@Autowired
private SktaiVectorDbsClient vectorDbsClient;

@Autowired
private SktaiReposClient reposClient;

@Autowired
private SktaiQueriesClient queriesClient;

// Vector DB 생성
VectorDBCreate createRequest = VectorDBCreate.builder()
    .name("My Vector DB")
    .type("Milvus")
    .connectionInfo(connectionInfo)
    .build();
VectorDBCreateResponse vectorDb = vectorDbsClient.addVectorDb(createRequest);

// Repository 생성
RepositoryCreate repoRequest = RepositoryCreate.builder()
    .name("My Knowledge Repo")
    .vectorDbId(vectorDb.getId())
    .build();
RepositoryCreateResponse repo = reposClient.createRepository(repoRequest);

// 지식 검색
QueryRequest queryRequest = QueryRequest.builder()
    .repositoryId(repo.getId())
    .query("검색할 내용")
    .build();
QueryResponse result = queriesClient.query(queryRequest);
```

## 설정
application.yml에서 SKTAI API 설정을 구성합니다:

```yaml
sktai:
  api:
    base-url: ${sktai.api.base-url}
    timeout:
      connect: 10000
      read: 60000
```

## 참고사항
- Knowledge 시스템은 RAG(Retrieval-Augmented Generation) 아키텍처를 기반으로 합니다
- Vector DB는 Repository 생성 전에 먼저 설정되어야 합니다
- 인덱싱 작업은 비동기로 처리되며, 상태 확인이 필요합니다
- Custom Script는 Python 기반이며, 보안상 제한된 라이브러리만 사용 가능합니다
- 대용량 문서 처리 시 청킹 및 배치 처리를 권장합니다
