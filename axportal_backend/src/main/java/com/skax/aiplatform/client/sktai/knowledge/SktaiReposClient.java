package com.skax.aiplatform.client.sktai.knowledge;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.*;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * SKTAI Knowledge Repository 관리 API 클라이언트
 * 
 * <p>SKTAI Knowledge API의 Knowledge Repository 관리 기능을 제공하는 Feign Client입니다.
 * 지식 저장소의 생성, 조회, 수정, 삭제와 함께 문서 관리, 인덱싱, 검색 최적화 등의 고급 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Repository 관리</strong>: Knowledge Repository 생성, 조회, 수정, 삭제</li>
 *   <li><strong>Document 관리</strong>: 문서 목록 조회, 상세 조회, 메타데이터 수정</li>
 *   <li><strong>Indexing 관리</strong>: 인덱싱 실행, 중지, 상태 확인</li>
 *   <li><strong>External Repository</strong>: 외부 지식 저장소 연동 관리</li>
 *   <li><strong>Chunk 관리</strong>: 문서 청크 편집, 병합, 분할</li>
 * </ul>
 * 
 * <h3>Repository 생명주기:</h3>
 * <ol>
 *   <li><strong>생성</strong>: DataSource 기반 Repository 생성</li>
 *   <li><strong>인덱싱</strong>: 문서 처리 및 벡터 인덱스 구축</li>
 *   <li><strong>검색</strong>: Retrieval API를 통한 문서 검색</li>
 *   <li><strong>관리</strong>: 문서 수정, 메타데이터 업데이트, 재인덱싱</li>
 * </ol>
 * 
 * <h3>지원하는 작업:</h3>
 * <ul>
 *   <li><strong>Batch Processing</strong>: 대량 문서 처리 및 인덱싱</li>
 *   <li><strong>Real-time Updates</strong>: 개별 문서 수정 및 즉시 반영</li>
 *   <li><strong>External Integration</strong>: 외부 벡터 DB 연동</li>
 *   <li><strong>Advanced Chunking</strong>: 사용자 정의 청크 편집</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge Repositories", description = "SKTAI Knowledge Repository 관리 API")
@FeignClient(
    name = "sktai-knowledge-repos-client",
    url = "${sktai.api.base-url}/api/v1/knowledge",
    configuration = SktaiClientConfig.class
)
public interface SktaiReposClient {

    // =========================================
    // Repository 기본 관리
    // =========================================

    /**
     * Knowledge Repository 목록 조회
     * 
     * <p>프로젝트에 등록된 Knowledge Repository 목록을 페이징 형태로 조회합니다.
     * 활성 상태 필터링, 검색, 정렬 기능을 지원하여 효율적인 Repository 관리가 가능합니다.</p>
     * 
     * @param isActive 활성 상태 필터 (true: 사용 가능한 Repository만 조회)
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건 (예: "name,asc")
     * @param filter 필터 조건
     * @param search 검색어 (이름 및 설명에서 검색)
     * @return 페이징된 Repository 목록
     */
    @Operation(
        summary = "Knowledge Repository 목록 조회",
        description = "프로젝트에 등록된 Knowledge Repository 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Repository 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MultiResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/repos")
    RepoListResponse getRepos(
        @Parameter(description = "활성 상태 필터 (true: 활성화된 Repository만)")
        @RequestParam(value = "is_active", required = false) Boolean isActive,
        
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 조건 (예: 'name,asc')")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어 (이름 및 설명에서 검색)")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * Knowledge Repository 신규 생성
     * 
     * <p>DataSource에 등록된 파일을 기반으로 새로운 Knowledge Repository를 생성합니다.
     * 임베딩 모델, 벡터 DB, 청크 설정 등을 포함한 완전한 지식 저장소를 구축합니다.</p>
     * 
     * @param request Repository 생성 요청 정보
     * @return 생성된 Repository ID
     */
    @Operation(
        summary = "Knowledge Repository 신규 생성",
        description = "DataSource 기반으로 새로운 Knowledge Repository를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Repository 생성 성공",
            content = @Content(schema = @Schema(implementation = RepoCreateResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos")
    RepoCreateResponse createRepo(@RequestBody RepoCreate request);

    /**
     * Repository ID로 Knowledge Repository 상세 조회
     * 
     * <p>특정 Repository의 상세 정보를 조회합니다.
     * Collection 정보, 최신 작업 상태, 벡터 DB 연결 정보 등을 포함합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @return Repository 상세 정보
     */
    @Operation(
        summary = "Repository 상세 조회",
        description = "특정 Knowledge Repository의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Repository 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = RepoWithCollection.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/repos/{repo_id}")
    RepoWithCollection getRepo(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId
    );

    /**
     * Repository 기본 설정 수정 (이름, 로더, 청크 정보)
     * 
     * <p>기존 Repository의 기본 설정을 수정합니다.
     * 이름, 기본 로더, 청크 설정 등을 변경할 수 있으며, 새로 추가되는 파일에 적용됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param request Repository 수정 요청 정보
     * @return 수정 처리 결과
     */
    @Operation(
        summary = "Repository 기본 설정 수정",
        description = "Repository 이름 및 기본 loader, chunk 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Repository 설정 수정 성공",
            content = @Content(schema = @Schema(implementation = RepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/repos/{repo_id}/edit")
    RepoResponse updateRepoSettings(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody RepoEdit request
    );

    /**
     * Repository DataSource 변경사항 반영
     * 
     * <p>Repository와 연결된 DataSource의 파일 목록이 변경된 경우,
     * 변경된 파일 정보를 Repository에 반영합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param request DataSource 변경사항 반영 요청
     * @return 반영 처리 결과
     */
    @Operation(
        summary = "Repository DataSource 변경사항 반영",
        description = "DataSource 파일 변경사항을 Repository에 반영합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "변경사항 반영 성공",
            content = @Content(schema = @Schema(implementation = RepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/repos/{repo_id}")
    RepoResponse updateRepoDataSource(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody RepoUpdateRequest request
    );

    /**
     * Repository 삭제
     * 
     * <p>특정 Repository를 시스템에서 삭제합니다.
     * 연관된 벡터 인덱스, 문서 데이터도 함께 제거됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     */
    @Operation(
        summary = "Repository 삭제",
        description = "특정 Knowledge Repository를 시스템에서 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Repository 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/repos/{repo_id}")
    void deleteRepo(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") UUID repoId
    );

    // =========================================
    // Retrieval 정보 조회
    // =========================================

    /**
     * Repository Retrieval 정보 조회
     * 
     * <p>Repository에서 검색(Retrieval) 작업을 수행하기 위한 상세 정보를 조회합니다.
     * 벡터 DB 연결 정보, 임베딩 모델 정보, Collection ID 등을 포함합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param projectId 프로젝트 고유 식별자
     * @param isExternal 외부 Repository 여부 (기본값: false)
     * @return Retrieval 정보
     */
    @Operation(
        summary = "Repository Retrieval 정보 조회",
        description = "Repository에서 검색을 위한 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Retrieval 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = RepoRetrievalInfo.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/repos/retrieval_info")
    RepoRetrievalInfo getRepoRetrievalInfo(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @RequestParam("repo_id") String repoId,
        
        @Parameter(description = "프로젝트 ID", required = true)
        @RequestParam("project_id") String projectId,
        
        @Parameter(description = "외부 Repository 여부")
        @RequestParam(value = "is_external", defaultValue = "false") Boolean isExternal
    );

    // =========================================
    // 인덱싱 관리
    // =========================================

    /**
     * Repository 인덱싱 실행
     * 
     * <p>지정한 Repository의 문서들에 대해 인덱싱을 실행합니다.
     * 인덱싱 완료 후 Retrieval API를 통한 문서 검색이 가능해집니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param targetStep 인덱싱 대상 단계 (기본값: embedding_and_indexing)
     * @return 인덱싱 작업 정보
     */
    @Operation(
        summary = "Repository 인덱싱 실행",
        description = "Repository의 문서들에 대해 인덱싱을 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "인덱싱 시작 성공",
            content = @Content(schema = @Schema(implementation = IndexingRepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/{repo_id}/indexing")
    IndexingRepoResponse startIndexing(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "인덱싱 대상 단계")
        @RequestParam(value = "target_step", defaultValue = "embedding_and_indexing") String targetStep
    );

    /**
     * Repository 인덱싱 중지
     * 
     * <p>진행 중인 Repository 인덱싱 작업을 중지합니다.
     * 진행 전 문서에 대한 작업을 취소하고 인덱싱을 안전하게 중단합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @return 인덱싱 중지 결과
     */
    @Operation(
        summary = "Repository 인덱싱 중지",
        description = "진행 중인 Repository 인덱싱 작업을 중지합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인덱싱 중지 요청 성공",
            content = @Content(schema = @Schema(implementation = IndexingRepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/{repo_id}/stop_indexing")
    IndexingRepoResponse stopIndexing(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId
    );

    /**
     * S3 수집 후 Repository 업데이트 및 인덱싱
     * 
     * <p>S3 버킷에서 변경된 문서를 수집하여 DataSource에 저장한 후,
     * Repository에 변경사항을 반영하고 인덱싱을 수행합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param request S3 수집 및 업데이트 요청 정보
     * @return 수집 및 인덱싱 작업 정보
     */
    @Operation(
        summary = "S3 수집 후 Repository 업데이트 및 인덱싱",
        description = "S3에서 문서를 수집하여 Repository에 반영 후 인덱싱을 수행합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "S3 수집 및 인덱싱 시작 성공",
            content = @Content(schema = @Schema(implementation = IndexingRepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/{repo_id}/collect_and_update")
    IndexingRepoResponse collectAndUpdate(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody CollectAndUpdateRepo request
    );

    // =========================================
    // Document 관리
    // =========================================

    /**
     * Repository Document 목록 조회
     * 
     * <p>Repository에 포함된 Document 목록을 조회합니다.
     * 각 Document의 진행 상태, 처리 결과 등을 확인할 수 있습니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 Document 목록
     */
    @Operation(
        summary = "Repository Document 목록 조회",
        description = "Repository에 포함된 Document 목록과 진행 상태를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MultiResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/repos/{repo_id}/documents")
    MultiResponse getDocuments(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 조건")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * Document 상세 조회
     * 
     * <p>특정 Document의 상세 정보를 조회합니다.
     * Loader, Splitter, Chunking 설정 정보 등이 포함됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param documentId Document 고유 식별자 (UUID)
     * @return Document 상세 정보
     */
    @Operation(
        summary = "Document 상세 조회",
        description = "특정 Document의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/repos/{repo_id}/documents/{document_id}")
    Object getDocument(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "Document ID (UUID 형식)", required = true)
        @PathVariable("document_id") String documentId
    );

    /**
     * Document 설정 일괄 수정
     * 
     * <p>여러 Document의 Loader, Splitter, Chunking 설정을 일괄적으로 수정합니다.
     * 대량의 문서에 대해 동일한 설정을 적용할 때 유용합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param request Document 설정 수정 요청 목록
     * @return 수정 처리 결과
     */
    @Operation(
        summary = "Document 설정 일괄 수정",
        description = "여러 Document의 Loader, Splitter, Chunking 설정을 일괄 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document 설정 수정 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/repos/{repo_id}/documents")
    Object updateDocuments(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody DocumentUpdateRequest request
    );

    /**
     * Document 일괄 삭제
     * 
     * <p>선택한 Document들을 삭제합니다.
     * DataSource 파일 및 임베딩 결과도 함께 삭제됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param documentIds 삭제할 Document ID 목록
     */
    @Operation(
        summary = "Document 일괄 삭제",
        description = "선택한 Document들을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/repos/{repo_id}/documents")
    void deleteDocuments(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody List<String> documentIds
    );

    /**
     * Document 활성화/비활성화 상태 변경
     * 
     * <p>특정 Document의 활성화/비활성화 상태를 변경합니다.
     * 비활성화된 Document는 검색 결과에서 제외됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param documentId Document 고유 식별자 (UUID)
     * @param isActive 활성화 상태 (true: 활성화, false: 비활성화)
     * @return 상태 변경 결과
     */
    @Operation(
        summary = "Document 활성화/비활성화 상태 변경",
        description = "Document의 활성화/비활성화 상태를 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document 상태 변경 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/repos/{repo_id}/documents/{document_id}")
    Object updateDocumentStatus(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "Document ID (UUID 형식)", required = true)
        @PathVariable("document_id") String documentId,
        
        @Parameter(description = "활성화 상태", required = true)
        @RequestParam("is_active") Boolean isActive
    );

    /**
     * Document 개별 삭제
     * 
     * <p>특정 Document를 삭제합니다.
     * DataSource 파일 및 임베딩 결과도 함께 삭제됩니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param documentId Document 고유 식별자 (UUID)
     */
    @Operation(
        summary = "Document 개별 삭제",
        description = "특정 Document를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/repos/{repo_id}/documents/{document_id}")
    void deleteDocument(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "Document ID (UUID 형식)", required = true)
        @PathVariable("document_id") String documentId
    );

    /**
     * Document 개별 인덱싱
     * 
     * <p>특정 Document에 대해 인덱싱을 실행합니다.
     * 문서 수정 후 즉시 검색에 반영하고 싶을 때 사용합니다.</p>
     * 
     * @param repoId Repository 고유 식별자 (UUID)
     * @param documentId Document 고유 식별자 (UUID)
     * @param targetStep 인덱싱 대상 단계 (기본값: embedding_and_indexing)
     * @return 인덱싱 작업 정보
     */
    @Operation(
        summary = "Document 개별 인덱싱",
        description = "특정 Document에 대해 인덱싱을 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document 인덱싱 시작 성공",
            content = @Content(schema = @Schema(implementation = IndexingRepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/{repo_id}/documents/{document_id}/indexing")
    IndexingRepoResponse indexDocument(
        @Parameter(description = "Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @Parameter(description = "Document ID (UUID 형식)", required = true)
        @PathVariable("document_id") String documentId,
        
        @Parameter(description = "인덱싱 대상 단계")
        @RequestParam(value = "target_step", defaultValue = "embedding_and_indexing") String targetStep
    );

    // /**
    //  * Document Chunk 목록 조회
    //  * 
    //  * <p>특정 Document의 Chunk 목록을 조회합니다.
    //  * Document Chunking 결과, 즉 Chunk 목록을 페이징 형태로 조회할 수 있습니다.</p>
    //  * 
    //  * @param repoId Repository 고유 식별자 (UUID)
    //  * @param documentId Document 고유 식별자 (UUID)
    //  * @param page 페이지 번호 (1부터 시작, 기본값: 1)
    //  * @param size 페이지당 항목 수 (기본값: 10)
    //  * @param sort 정렬 조건
    //  * @param filter 필터 조건
    //  * @param search 검색어
    //  * @return 페이징된 Chunk 목록
    //  */
    // @Operation(
    //     summary = "Document Chunk 목록 조회",
    //     description = "Document Chunking 결과, 즉 Chunk 목록을 조회합니다."
    // )
    // @ApiResponses({
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "Chunk 목록 조회 성공",
    //         content = @Content(schema = @Schema(implementation = ChunkListResponse.class))
    //     ),
    //     @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    // })
    // @GetMapping("/api/v1/knowledge/repos/{repo_id}/documents/{document_id}/chunks")
    // ChunkListResponse getDocumentChunks(
    //     @Parameter(description = "Repository ID (UUID 형식)", required = true)
    //     @PathVariable("repo_id") String repoId,
        
    //     @Parameter(description = "Document ID (UUID 형식)", required = true)
    //     @PathVariable("document_id") String documentId,
        
    //     @Parameter(description = "페이지 번호 (1부터 시작)")
    //     @RequestParam(value = "page", defaultValue = "1") Integer page,
        
    //     @Parameter(description = "페이지당 항목 수")
    //     @RequestParam(value = "size", defaultValue = "10") Integer size,
        
    //     @Parameter(description = "정렬 조건")
    //     @RequestParam(value = "sort", required = false) String sort,
        
    //     @Parameter(description = "필터 조건")
    //     @RequestParam(value = "filter", required = false) String filter,
        
    //     @Parameter(description = "검색어")
    //     @RequestParam(value = "search", required = false) String search
    // );

    // =========================================
    // External Repository 관리
    // =========================================

    /**
     * External Knowledge Repository 목록 조회
     * 
     * <p>프로젝트에 등록된 External Knowledge Repository 목록을 조회합니다.
     * 외부에서 생성된 벡터 인덱스를 연동하여 사용하는 Repository들입니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어 (이름 및 설명에서 검색)
     * @return 페이징된 External Repository 목록
     */
    @Operation(
        summary = "External Knowledge Repository 목록 조회",
        description = "프로젝트에 등록된 External Knowledge Repository 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "External Repository 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MultiResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/repos/external")
    MultiResponse getExternalRepos(
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 조건")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어 (이름 및 설명에서 검색)")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * External Knowledge Repository 등록
     * 
     * <p>외부에서 생성된 벡터 DB 인덱스를 조회하기 위해 등록합니다.
     * 기존에 구축된 벡터 인덱스를 SKTAI Knowledge 시스템에서 활용할 수 있게 합니다.</p>
     * 
     * @param request External Repository 생성 요청 정보
     * @return 등록된 External Repository 정보
     */
    @Operation(
        summary = "External Knowledge Repository 등록",
        description = "외부에서 생성된 벡터 DB 인덱스를 등록하여 활용합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "External Repository 등록 성공",
            content = @Content(schema = @Schema(implementation = RepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/external")
    RepoResponse createExternalRepo(@RequestBody RepoExtCreateRequest request);

    /**
     * External Knowledge Repository 동작 테스트
     * 
     * <p>외부에서 생성된 Knowledge 설정이 올바른지 확인합니다.
     * 연결 정보, 스크립트, 인덱스 구조 등을 검증합니다.</p>
     * 
     * @param request External Repository 테스트 요청 정보
     * @return 테스트 결과
     */
    @Operation(
        summary = "External Knowledge Repository 동작 테스트",
        description = "외부 Knowledge Repository 설정의 유효성을 검증합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "External Repository 테스트 성공",
            content = @Content(schema = @Schema(implementation = RepoExtTestResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/repos/external/test")
    RepoExtTestResponse testExternalRepo(@RequestBody RepoExtTestRequest request);

    /**
     * External Knowledge Repository 상세 조회
     * 
     * <p>특정 External Knowledge Repository의 상세 정보를 조회합니다.
     * 연결 정보, 스크립트, 상태 등을 확인할 수 있습니다.</p>
     * 
     * @param repoId External Repository 고유 식별자 (UUID)
     * @return External Repository 상세 정보
     */
    @Operation(
        summary = "External Knowledge Repository 상세 조회",
        description = "특정 External Knowledge Repository의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "External Repository 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = RepoExtInfo.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/repos/external/{repo_id}")
    RepoExtInfo getExternalRepo(
        @Parameter(description = "External Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId
    );

    /**
     * External Knowledge Repository 수정
     * 
     * <p>External Knowledge Repository의 정보를 수정합니다.
     * 연결 정보, 스크립트, 설명 등을 업데이트할 수 있습니다.</p>
     * 
     * @param repoId External Repository 고유 식별자 (UUID)
     * @param request External Repository 수정 요청 정보
     * @return 수정 처리 결과
     */
    @Operation(
        summary = "External Knowledge Repository 수정",
        description = "External Knowledge Repository 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "External Repository 수정 성공",
            content = @Content(schema = @Schema(implementation = RepoResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/repos/external/{repo_id}")
    RepoResponse updateExternalRepo(
        @Parameter(description = "External Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId,
        
        @RequestBody RepoExtUpdateRequest request
    );

    /**
     * External Knowledge Repository 삭제
     * 
     * <p>특정 External Knowledge Repository를 삭제합니다.
     * 외부 벡터 인덱스는 그대로 유지되며, 연동 정보만 제거됩니다.</p>
     * 
     * @param repoId External Repository 고유 식별자 (UUID)
     */
    @Operation(
        summary = "External Knowledge Repository 삭제",
        description = "특정 External Knowledge Repository를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "External Repository 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/repos/external/{repo_id}")
    void deleteExternalRepo(
        @Parameter(description = "External Repository ID (UUID 형식)", required = true)
        @PathVariable("repo_id") String repoId
    );

    /**
     * External Knowledge Repository Import
     * 
     * <p>외부에서 생성된 VectorDB Index를 조회하기 위한 External Knowledge Repository를 Import합니다.
     * 기존 External Repository의 설정과 데이터를 기반으로 새로운 Internal Repository를 생성합니다.</p>
     * 
     * @param request External Repository Import 요청 정보
     * @return Import된 Repository ID
     */
    @Operation(
        summary = "External Knowledge Repository Import",
        description = "외부에서 생성된 VectorDB Index를 조회하기 위한 External Knowledge를 Import합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "External Repository Import 성공",
            content = @Content(schema = @Schema(implementation = RepoImportResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "External Repository를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/repos/external/import")
    RepoImportResponse importExternalRepo(@RequestBody RepoExtImportRequest request);

    /**
     * Document Chunks 목록 조회
     * 
     * @param repoId Repository ID
     * @param documentId Document ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return ChunkListResponse
     */
    @GetMapping("/repos/{repo_id}/documents/{document_id}/chunks")
    @Operation(
        summary = "Document Chunks 목록 조회",
        description = "지정된 Document의 Chunks 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Chunks 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChunkListResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Repository 또는 Document를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ChunkListResponse getDocumentChunks(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable("repo_id") String repoId,
        @Parameter(description = "Document ID", required = true)
        @PathVariable("document_id") String documentId,
        @Parameter(description = "페이지 번호", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "12")
        @RequestParam(value = "size", defaultValue = "12") Integer size,
        @Parameter(description = "정렬 조건", example = "chunk_sequence,asc")
        @RequestParam(value = "sort", required = false) String sort,
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        @Parameter(description = "검색 키워드")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * Document Chunks 병합
     * 
     * @param repoId Repository ID
     * @param documentId Document ID
     * @param request 병합 요청 데이터
     * @return 병합 결과
     */
    @PutMapping("/repos/{repo_id}/documents/{document_id}/chunks/merge")
    @Operation(
        summary = "Document Chunks 병합",
        description = "선택된 여러 청크들을 하나로 병합합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "청크 병합 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Object.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Repository 또는 Document를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    Object mergeDocumentChunks(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable("repo_id") String repoId,
        @Parameter(description = "Document ID", required = true)
        @PathVariable("document_id") String documentId,
        @RequestBody Object request
    );

    /**
     * Document Chunk 분할
     * 
     * @param repoId Repository ID
     * @param documentId Document ID
     * @param chunkId Chunk ID
     * @param request 분할 요청 데이터
     * @return 분할 결과
     */
    @PutMapping("/repos/{repo_id}/documents/{document_id}/chunks/{chunk_id}/split")
    @Operation(
        summary = "Document Chunk 분할",
        description = "선택된 청크를 두 개로 분할합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "청크 분할 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Object.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Repository, Document 또는 Chunk를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    Object splitDocumentChunk(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable("repo_id") String repoId,
        @Parameter(description = "Document ID", required = true)
        @PathVariable("document_id") String documentId,
        @Parameter(description = "Chunk ID", required = true)
        @PathVariable("chunk_id") String chunkId,
        @RequestBody Object request
    );

    /**
     * Document Chunk 삭제
     * 
     * @param repoId Repository ID
     * @param documentId Document ID
     * @param chunkId Chunk ID
     * @return 삭제 결과
     */
    @DeleteMapping("/repos/{repo_id}/documents/{document_id}/chunks/{chunk_id}")
    @Operation(
        summary = "Document Chunk 삭제",
        description = "지정된 청크를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "청크 삭제 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Object.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Repository, Document 또는 Chunk를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    Object deleteDocumentChunk(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable("repo_id") String repoId,
        @Parameter(description = "Document ID", required = true)
        @PathVariable("document_id") String documentId,
        @Parameter(description = "Chunk ID", required = true)
        @PathVariable("chunk_id") String chunkId
    );

}
