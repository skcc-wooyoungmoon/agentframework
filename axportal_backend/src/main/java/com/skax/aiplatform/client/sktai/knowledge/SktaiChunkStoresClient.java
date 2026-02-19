package com.skax.aiplatform.client.sktai.knowledge;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ChunkStoreCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ChunkStoreUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ChunkStoreResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.MultiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Knowledge ChunkStore 관리 API 클라이언트
 * 
 * <p>SKTAI Knowledge API의 ChunkStore 관리 기능을 제공하는 Feign Client입니다.
 * 청크 데이터 저장소의 생성, 조회, 수정, 삭제 등의 관리 작업을 수행할 수 있습니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>ChunkStore 생성</strong>: 새로운 청크 저장소 등록</li>
 *   <li><strong>ChunkStore 목록 조회</strong>: 등록된 청크 저장소 목록 검색</li>
 *   <li><strong>ChunkStore 상세 조회</strong>: 특정 청크 저장소 정보 확인</li>
 *   <li><strong>ChunkStore 수정</strong>: 기존 청크 저장소 설정 변경</li>
 *   <li><strong>ChunkStore 삭제</strong>: 청크 저장소 제거</li>
 * </ul>
 * 
 * <h3>지원하는 ChunkStore 유형:</h3>
 * <ul>
 *   <li><strong>SystemDB</strong>: 시스템 내장 데이터베이스</li>
 *   <li><strong>OpenSearch</strong>: 외부 OpenSearch 클러스터</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>프로젝트별 청크 저장소 관리</li>
 *   <li>다양한 저장소 타입 설정 및 연결</li>
 *   <li>청크 데이터 저장소 성능 최적화</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge ChunkStores", description = "SKTAI Knowledge 청크 저장소 관리 API")
@FeignClient(
    name = "sktai-knowledge-chunk-stores-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiChunkStoresClient {

    /**
     * ChunkStore 목록 조회
     * 
     * <p>프로젝트에 등록된 ChunkStore 목록을 페이징 형태로 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원하여 효율적인 저장소 관리가 가능합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건 (예: "name,asc")
     * @param filter 필터 조건
     * @param search 검색어 (이름 및 설명에서 검색)
     * @return 페이징된 ChunkStore 목록
     * 
     * @apiNote 검색어는 ChunkStore 이름과 연결 정보에서 부분 일치로 검색됩니다.
     */
    @Operation(
        summary = "ChunkStore 목록 조회",
        description = "프로젝트에 등록된 ChunkStore 목록을 페이징 형태로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "ChunkStore 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MultiResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/chunk_stores")
    MultiResponse getChunkStores(
        @Parameter(description = "페이지 번호 (1부터 시작)")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수")
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        
        @Parameter(description = "정렬 조건 (예: 'name,asc')")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어 (이름 및 연결정보에서 검색)")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * ChunkStore 신규 등록
     * 
     * <p>프로젝트에 새로운 ChunkStore를 등록합니다.
     * 청크 데이터 저장소 정보와 연결 설정이 필요합니다.</p>
     * 
     * @param request ChunkStore 생성 요청 정보
     * @return 생성된 ChunkStore 정보 (ID 포함)
     * 
     * @apiNote 연결 정보는 저장소 타입에 따라 다른 형식을 가집니다.
     */
    @Operation(
        summary = "ChunkStore 신규 등록",
        description = "프로젝트에 새로운 ChunkStore를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "신규 ChunkStore ID를 반환합니다.",
            content = @Content(schema = @Schema(implementation = ChunkStoreResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/chunk_stores")
    ChunkStoreResponse createChunkStore(@RequestBody ChunkStoreCreate request);

    /**
     * ChunkStore 정보 조회
     * 
     * <p>특정 ChunkStore의 상세 정보를 조회합니다.
     * ID를 통해 저장소의 설정과 연결 상태를 확인할 수 있습니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자 (UUID)
     * @return ChunkStore 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    @Operation(
        summary = "ChunkStore 정보 조회",
        description = "특정 ChunkStore의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "ChunkStore 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ChunkStoreResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/chunk_stores/{chunk_store_id}")
    ChunkStoreResponse getChunkStore(
        @Parameter(description = "ChunkStore ID (UUID 형식)", required = true)
        @PathVariable("chunk_store_id") String chunkStoreId
    );

    /**
     * ChunkStore 정보 수정
     * 
     * <p>기존 ChunkStore의 설정 정보를 수정합니다.
     * 이름, 연결 정보 등을 변경할 수 있습니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자 (UUID)
     * @param request ChunkStore 수정 요청 정보
     * @return 수정된 ChunkStore 정보
     * 
     * @apiNote 연결 정보 변경 시 기존 연결이 해제되고 새로운 연결이 설정됩니다.
     */
    @Operation(
        summary = "ChunkStore 정보 수정",
        description = "기존 ChunkStore의 설정 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "ChunkStore 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = ChunkStoreResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/chunk_stores/{chunk_store_id}")
    ChunkStoreResponse updateChunkStore(
        @Parameter(description = "ChunkStore ID (UUID 형식)", required = true)
        @PathVariable("chunk_store_id") String chunkStoreId,
        
        @RequestBody ChunkStoreUpdate request
    );

    /**
     * ChunkStore 정보 삭제
     * 
     * <p>특정 ChunkStore를 시스템에서 삭제합니다.
     * 연결된 데이터가 있는 경우 삭제가 제한될 수 있습니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자 (UUID)
     * 
     * @apiNote 삭제 시 연관된 청크 데이터도 함께 제거됩니다.
     */
    @Operation(
        summary = "ChunkStore 정보 삭제",
        description = "특정 ChunkStore를 시스템에서 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "ChunkStore 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @DeleteMapping("/api/v1/knowledge/chunk_stores/{chunk_store_id}")
    void deleteChunkStore(
        @Parameter(description = "ChunkStore ID (UUID 형식)", required = true)
        @PathVariable("chunk_store_id") String chunkStoreId
    );
}
