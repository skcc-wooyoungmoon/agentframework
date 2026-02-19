package com.skax.aiplatform.client.sktai.knowledge;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ToolsResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
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

/**
 * SKTAI Knowledge Tool 관리 API 클라이언트
 * 
 * <p>SKTAI Knowledge API의 Data Ingestion Tool 관리 기능을 제공하는 Feign Client입니다.
 * 문서 처리 및 데이터 수집을 위한 다양한 도구의 등록, 조회, 수정, 삭제 작업을 수행할 수 있습니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Tool 생성</strong>: 새로운 데이터 수집 도구 등록</li>
 *   <li><strong>Tool 목록 조회</strong>: 등록된 도구 목록 검색</li>
 *   <li><strong>Tool 상세 조회</strong>: 특정 도구 정보 확인</li>
 *   <li><strong>Tool 수정</strong>: 기존 도구 설정 변경</li>
 *   <li><strong>Tool 삭제</strong>: 도구 제거</li>
 * </ul>
 * 
 * <h3>지원하는 Tool 유형:</h3>
 * <ul>
 *   <li><strong>AzureDocumentIntelligence</strong>: Azure AI Document Intelligence (기본값)</li>
 *   <li><strong>NaverOCR</strong>: 네이버 클로바 OCR</li>
 *   <li><strong>Docling</strong>: IBM Docling 문서 처리</li>
 *   <li><strong>SynapsoftDA</strong>: Synapsoft 문서 분석</li>
 *   <li><strong>SKTDocumentInsight</strong>: SKT 문서 인사이트</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>다양한 문서 포맷 지원을 위한 처리 도구 관리</li>
 *   <li>OCR 및 문서 분석 서비스 통합</li>
 *   <li>프로젝트별 데이터 수집 도구 설정</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Tag(name = "SKTAI Knowledge Tools", description = "SKTAI Knowledge 데이터 수집 도구 관리 API")
@FeignClient(
    name = "sktai-knowledge-tools-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiToolsClient {

    /**
     * Data Ingestion Tool 목록 조회
     * 
     * <p>프로젝트에 등록된 데이터 수집 도구 목록을 페이징 형태로 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원하여 효율적인 도구 관리가 가능합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건 (예: "name,asc")
     * @param filter 필터 조건
     * @param search 검색어 (이름 및 설명에서 검색)
     * @return 페이징된 Tool 목록
     * 
     * @apiNote 검색어는 Tool 이름과 연결 정보에서 부분 일치로 검색됩니다.
     */
    @Operation(
        summary = "Data Ingestion Tool 목록 조회",
        description = "프로젝트에 등록된 데이터 수집 도구 목록을 페이징 형태로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/api/v1/knowledge/tools")
    ToolsResponse getTools(
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
     * 신규 Tool 등록
     * 
     * <p>프로젝트에 새로운 데이터 수집 도구를 등록합니다.
     * 도구 타입에 따른 연결 정보와 설정이 필요합니다.</p>
     * 
     * @param request Tool 생성 요청 정보
     * @return 생성된 Tool 정보 (ID 포함)
     * 
     * @apiNote 연결 정보는 도구 타입에 따라 다른 형식을 가집니다.
     */
    @Operation(
        summary = "신규 Tool 등록",
        description = "프로젝트에 새로운 데이터 수집 도구를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Tool 등록 성공",
            content = @Content(schema = @Schema(implementation = ToolResponse.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PostMapping("/api/v1/knowledge/tools")
    ToolResponse createTool(@RequestBody ToolCreate request);

    /**
     * Tool 상세 조회
     * 
     * <p>특정 Tool의 상세 정보를 조회합니다.
     * ID를 통해 도구의 설정과 연결 상태를 확인할 수 있습니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * @return Tool 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    @Operation(
        summary = "Tool 상세 조회",
        description = "특정 Tool의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tool 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/tools/{tool_id}")
    ToolResponse getTool(
        @Parameter(description = "Tool ID (UUID 형식)", required = true)
        @PathVariable("tool_id") String toolId
    );

    /**
     * Tool 수정
     * 
     * <p>기존 Tool의 설정 정보를 수정합니다.
     * 이름, 연결 정보 등을 변경할 수 있습니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * @param request Tool 수정 요청 정보
     * @return 수정 처리 결과
     * 
     * @apiNote 연결 정보 변경 시 기존 연결이 해제되고 새로운 연결이 설정됩니다.
     */
    @Operation(
        summary = "Tool 수정",
        description = "기존 Tool의 설정 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tool 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @PutMapping("/api/v1/knowledge/tools/{tool_id}")
    Object updateTool(
        @Parameter(description = "Tool ID (UUID 형식)", required = true)
        @PathVariable("tool_id") String toolId,
        
        @RequestBody ToolUpdate request
    );

    /**
     * Tool 삭제
     * 
     * <p>특정 Tool을 시스템에서 삭제합니다.
     * 연결된 데이터가 있는 경우 삭제가 제한될 수 있습니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * 
     * @apiNote 삭제 시 연관된 설정 데이터도 함께 제거됩니다.
     */
    @Operation(
        summary = "Tool 삭제",
        description = "특정 Tool을 시스템에서 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tool 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "404", description = "이미 삭제된 Tool로 삭제 실패")
    })
    @DeleteMapping("/api/v1/knowledge/tools/{tool_id}")
    void deleteTool(
        @Parameter(description = "Tool ID (UUID 형식)", required = true)
        @PathVariable("tool_id") String toolId
    );

    /**
     * Tool 연결 정보 조회
     * 
     * <p> Tool의 연결 정보 정보를 조회합니다.</p>
     * 
     * @return Tool 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    @Operation(
        summary = "Tool 연결 정보 조회",
        description = "Tool의 연결 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tool 연결 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    @GetMapping("/api/v1/knowledge/tools/connection_args")
    List<ArgResponse> getConnectionArgs();
}
