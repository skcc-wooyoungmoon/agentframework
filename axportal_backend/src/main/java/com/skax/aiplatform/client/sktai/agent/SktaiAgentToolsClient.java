package com.skax.aiplatform.client.sktai.agent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.agent.dto.request.ToolRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolImportResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolsResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Agent Tools API Client
 * 
 * <p>SKTAI Agent 시스템의 Tool 관리 기능을 제공하는 Feign Client입니다.
 * Agent가 사용할 수 있는 도구들을 생성, 조회, 수정, 삭제할 수 있습니다.</p>
 * 
 * <h3>제공 기능:</h3>
 * <ul>
 *   <li><strong>Tool CRUD</strong>: Tool 생성, 조회, 수정, 삭제</li>
 *   <li><strong>Tool 목록 조회</strong>: 페이징, 정렬, 필터링, 검색 지원</li>
 *   <li><strong>Tool 하드 삭제</strong>: 삭제 마크된 Tool들의 완전 삭제</li>
 * </ul>
 * 
 * <h3>Tool 타입:</h3>
 * <ul>
 *   <li><strong>custom_code</strong>: 직접 코드를 작성하여 사용하는 Tool</li>
 *   <li><strong>custom_api</strong>: API 호출을 위한 Tool</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Tool 목록 조회
 * CommonResponse tools = toolsClient.getTools(1, 10, null, null, null, null);
 * 
 * // Tool 생성
 * ToolRequest request = ToolRequest.builder()
 *     .name("WikiSearch")
 *     .description("Wikipedia 검색 도구")
 *     .toolType("custom_api")
 *     .serverUrl("https://ko.wikipedia.org/w/api.php")
 *     .method("GET")
 *     .build();
 * CommonResponse response = toolsClient.createTool(request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-agent-tools-client",
    url = "${sktai.api.base-url}/api/v1/agent",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Agent Tools", description = "SKTAI Agent Tools Management API")
public interface SktaiAgentToolsClient {
    
    /**
     * Tool 목록 조회
     * 
     * <p>등록된 Tool들의 목록을 조회합니다.
     * 페이징, 정렬, 필터링, 검색 기능을 지원합니다.</p>
     * 
     * @param name Tool 이름으로 필터링
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return Tool 목록 응답
     * @since 1.0
     */
    @GetMapping("/tools")
    @Operation(
        summary = "Tool 목록 조회",
        description = "등록된 Tool들의 목록을 페이징, 정렬, 필터링, 검색 기능과 함께 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tool 목록 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ToolsResponse getTools(
        @Parameter(description = "Tool 이름으로 필터링") @RequestParam(required = false) String name,
        // @Parameter(description = "프로젝트 ID") @RequestParam(required = false) String project_id,
        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
        @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
        @Parameter(description = "검색어") @RequestParam(required = false) String search
    );
    
    /**
     * Tool 생성
     * 
     * <p>새로운 Tool을 생성합니다.
     * custom_code 타입과 custom_api 타입을 지원합니다.</p>
     * 
     * @param request Tool 생성 요청 데이터
     * @return Tool 생성 응답
     * @since 1.0
     */
    @PostMapping("/tools")
    @Operation(
        summary = "Tool 생성",
        description = "새로운 Tool을 생성합니다. custom_code 타입과 custom_api 타입을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tool 생성 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ToolCreateResponse createTool(@RequestBody ToolRequest request);
    
    /**
     * Tool 상세 조회
     * 
     * <p>특정 Tool의 상세 정보를 조회합니다.</p>
     * 
     * @param toolId Tool ID
     * @return Tool 상세 정보 응답
     * @since 1.0
     */
    @GetMapping("/tools/{toolId}")
    @Operation(
        summary = "Tool 상세 조회",
        description = "특정 Tool의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tool 상세 조회 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ToolResponse getToolById(@Parameter(description = "Tool ID") @PathVariable String toolId);
    
    /**
     * Tool 수정
     * 
     * <p>기존 Tool의 정보를 수정합니다.</p>
     * 
     * @param toolId Tool ID
     * @param request Tool 수정 요청 데이터
     * @return Tool 수정 응답
     * @since 1.0
     */
    @PutMapping("/tools/{toolId}")
    @Operation(
        summary = "Tool 수정",
        description = "기존 Tool의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tool 수정 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ToolUpdateResponse updateTool(
        @Parameter(description = "Tool ID") @PathVariable String toolId,
        @RequestBody ToolRequest request
    );
    
    /**
     * Tool 삭제
     * 
     * <p>특정 Tool을 논리적으로 삭제합니다 (삭제 마크).</p>
     * 
     * @param toolId Tool ID
     * @since 1.0
     */
    @DeleteMapping("/tools/{toolId}")
    @Operation(
        summary = "Tool 삭제",
        description = "특정 Tool을 논리적으로 삭제합니다 (삭제 마크 설정)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tool 삭제 성공"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    void deleteTool(@Parameter(description = "Tool ID") @PathVariable String toolId);
    
    /**
     * Tool 하드 삭제
     * 
     * <p>삭제 마크된 모든 Tool들을 데이터베이스에서 완전히 삭제합니다.</p>
     * 
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     * @since 1.0
     */
    @PostMapping("/tools/hard-delete")
    @Operation(
        summary = "Tool 하드 삭제",
        description = "삭제 마크된 모든 Tool들을 데이터베이스에서 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteTools();
    
    /**
     * Tool Import (JSON)
     * 
     * <p>JSON 데이터를 받아서 Tool을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param toolId Tool ID (query parameter)
     * @param jsonData JSON 형식의 Tool 데이터
     * @return 생성된 Tool 정보
     */
    @PostMapping("/tools/import")
    @Operation(
        summary = "Tool Import (JSON)",
        description = "JSON 데이터를 받아서 Tool을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tool Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ToolImportResponse importTool(
        @Parameter(description = "Tool ID", required = true) @RequestParam("tool_id") String toolId,
        @Parameter(description = "JSON 형식의 Tool 데이터", required = true)
        @RequestBody Object jsonData);
}
