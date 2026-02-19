package com.skax.aiplatform.client.sktai.knowledge;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectordbImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBCreateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBDetailResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBUpdateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDbsResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectordbImportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Knowledge Vector DB 관리 API 클라이언트
 * 
 * <p>
 * SKTAI Knowledge API의 Vector DB 관련 기능을 제공하는 Feign Client입니다.
 * Vector DB의 생성, 조회, 수정, 삭제 등의 작업을 수행할 수 있습니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Vector DB 생성</strong>: 새로운 Vector DB 등록</li>
 * <li><strong>Vector DB 목록 조회</strong>: 페이징된 Vector DB 목록 조회</li>
 * <li><strong>Vector DB 상세 조회</strong>: 특정 Vector DB 상세 정보 조회</li>
 * <li><strong>Vector DB 수정</strong>: Vector DB 정보 업데이트</li>
 * <li><strong>Vector DB 삭제</strong>: Vector DB 제거</li>
 * </ul>
 * 
 * <h3>지원하는 Vector DB 타입:</h3>
 * <ul>
 * <li><strong>Milvus</strong>: 오픈소스 벡터 데이터베이스</li>
 * <li><strong>AzureAISearch</strong>: Azure AI Search 서비스</li>
 * <li><strong>AzureAISearchShared</strong>: 공유 Azure AI Search</li>
 * <li><strong>OpenSearch</strong>: OpenSearch 벡터 검색</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge Vector DBs", description = "SKTAI Knowledge Vector DB 관리 API")
@FeignClient(name = "sktai-knowledge-vectordbs-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
public interface SktaiVectorDbsClient {

    /**
     * Vector DB 목록 조회
     * 
     * <p>
     * 등록된 Vector DB 목록을 페이징하여 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원합니다.
     * </p>
     * 
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Vector DB 목록 응답
     * 
     * @apiNote 결과는 페이징되어 반환되며, 접근 권한이 있는 Vector DB만 조회됩니다.
     */
    @Operation(summary = "Vector DB 목록 조회", description = "등록된 Vector DB 목록을 페이징하여 조회합니다. 검색, 필터링, 정렬 기능을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vector DB 목록 조회 성공", content = @Content(schema = @Schema(implementation = VectorDbsResponse.class))),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/vectordbs")
    VectorDbsResponse getVectorDbs(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "10") @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 조건") @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "필터 조건") @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색어") @RequestParam(value = "search", required = false) String search);

    /**
     * Vector DB 신규 등록
     * 
     * <p>
     * Knowledge에 사용할 Vector DB를 새로 등록합니다.
     * Vector DB 종류와 접속 정보 입력이 필요합니다.
     * </p>
     * 
     * @param request Vector DB 생성 요청 정보
     * @return 생성된 Vector DB ID
     * 
     * @apiNote 등록 후 다른 Knowledge 컴포넌트에서 사용할 수 있습니다.
     */
    @Operation(summary = "Vector DB 신규 등록", description = "Knowledge에 사용할 Vector DB를 등록합니다. Vector DB 종류와 접속 정보 입력이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "신규 Vector DB ID를 반환합니다.", content = @Content(schema = @Schema(implementation = VectorDBCreateResponse.class))),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/vectordbs")
    VectorDBCreateResponse addVectorDb(@RequestBody VectorDBCreate request);

    /**
     * Vector DB 정보 조회
     * 
     * <p>
     * 지정된 ID의 Vector DB 상세 정보를 조회합니다.
     * 연결 정보와 설정 상태를 확인할 수 있습니다.
     * </p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * @return Vector DB 상세 정보
     * 
     * @apiNote UUID 형식의 Vector DB ID가 필요합니다.
     */
    @Operation(summary = "Vector DB 정보 조회", description = "지정된 ID의 Vector DB 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vector DB 정보 조회 성공", content = @Content(schema = @Schema(implementation = VectorDBDetailResponse.class))),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/vectordbs/{vector_db_id}")
    VectorDBDetailResponse getVectorDb(
            @Parameter(description = "Vector DB 고유 식별자", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("vector_db_id") String vectorDbId);

    /**
     * Vector DB 정보 수정
     * 
     * <p>
     * 기존 Vector DB의 설정 정보를 업데이트합니다.
     * 이름, 연결 정보, 기본 설정 등을 변경할 수 있습니다.
     * </p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * @param request    Vector DB 수정 요청 정보
     * @return 수정된 Vector DB 정보
     * 
     * @apiNote 수정 시 연결된 Knowledge Repository에 영향을 줄 수 있습니다.
     */
    @Operation(summary = "Vector DB 정보 수정", description = "기존 Vector DB의 설정 정보를 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vector DB 수정 성공", content = @Content(schema = @Schema(implementation = VectorDBUpdateResponse.class))),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/vectordbs/{vector_db_id}")
    VectorDBUpdateResponse updateVectorDb(
            @Parameter(description = "Vector DB 고유 식별자", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("vector_db_id") String vectorDbId,

            @RequestBody VectorDBUpdate request);

    /**
     * Vector DB 정보 삭제
     * 
     * <p>
     * 지정된 Vector DB를 시스템에서 완전히 제거합니다.
     * 삭제 전 연결된 Knowledge Repository가 없는지 확인이 필요합니다.
     * </p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * 
     * @apiNote 삭제 후에는 복구할 수 없으므로 신중하게 사용해야 합니다.
     */
    @Operation(summary = "Vector DB 정보 삭제", description = "지정된 Vector DB를 시스템에서 완전히 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vector DB 삭제 성공"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/vectordbs/{vector_db_id}")
    void deleteVectorDb(
            @Parameter(description = "Vector DB 고유 식별자", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("vector_db_id") String vectorDbId);

    /**
     * vectorDB 연결 정보 조회
     * 
     * <p>
     * Tool의 연결 정보 정보를 조회합니다.
     * </p>
     * 
     * @return Tool 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    @Operation(summary = "vectorDB 연결 정보 조회", description = "Tool의 연결 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "vectorDB 연결 정보 조회 성공", content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/vectordbs/connection_args")
    List<ArgResponse> getConnectionArgs();

    /**
     * Vector Database Import
     * 
     * <p>
     * 외부 Vector Database를 SKTAI Knowledge 시스템으로 Import합니다.
     * Knowledge Repository 생성 시 사용할 수 있는 Vector Database를 등록합니다.
     * </p>
     * 
     * <h4>Milvus 연결 예시:</h4>
     * 
     * <pre>
     * {
     *   "name": "Milvus Vector Store",
     *   "type": "Milvus",
     *   "connection_info": {
     *     "host": "localhost",
     *     "port": 19530,
     *     "user": "root",
     *     "password": "milvus123"
     *   },
     *   "is_default": true
     * }
     * </pre>
     * 
     * <h4>Azure AI Search 연결 예시:</h4>
     * 
     * <pre>
     * {
     *   "name": "Azure AI Search",
     *   "type": "AzureAISearch",
     *   "connection_info": {
     *     "endpoint": "https://my-search.search.windows.net",
     *     "key": "API_KEY_HERE"
     *   }
     * }
     * </pre>
     * 
     * <h4>OpenSearch / ElasticSearch 연결 예시:</h4>
     * 
     * <pre>
     * {
     *   "name": "OpenSearch Cluster",
     *   "type": "OpenSearch",
     *   "connection_info": {
     *     "hosts": ["https://host1:9200", "https://host2:9200"],
     *     "username": "admin",
     *     "password": "admin123",
     *     "use_ssl": true
     *   }
     * }
     * </pre>
     * 
     * @param request Vector Database Import 요청 정보
     * @return Import된 Vector Database ID
     */
    @Operation(summary = "Vector Database Import", description = "Knowledge에 사용할 Vector Database를 Import합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vector Database Import 성공", content = @Content(schema = @Schema(implementation = VectordbImportResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "409", description = "동일한 이름의 Vector Database가 이미 존재함"),
            @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/vectordbs/import")
    VectordbImportResponse importVectordb(@RequestBody VectordbImportRequest request);
}
