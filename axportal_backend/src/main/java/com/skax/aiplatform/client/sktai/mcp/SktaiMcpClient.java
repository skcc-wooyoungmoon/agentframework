package com.skax.aiplatform.client.sktai.mcp;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogCreateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogUpdateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpTestConnectionRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogCreateResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogImportResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogListResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogPingResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogToolsResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpTestConnectionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI MCP (Model Context Protocol) API Feign Client
 * 
 * <p>SKTAI MCP API와 통신하기 위한 Feign Client 인터페이스입니다.
 * MCP 카탈로그 관리, 연결 테스트, 도구 동기화 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>카탈로그 관리</strong>: MCP 카탈로그 CRUD 작업</li>
 *   <li><strong>연결 테스트</strong>: MCP 서버 연결 상태 확인</li>
 *   <li><strong>도구 관리</strong>: MCP 도구 조회 및 동기화</li>
 *   <li><strong>활성화/비활성화</strong>: 카탈로그 상태 관리</li>
 * </ul>
 * 
 * @since 2025-09-30
 * @version 1.0
 */
@FeignClient(
    name = "sktai-mcp-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI MCP", description = "SKTAI MCP 카탈로그 관리 API")
public interface SktaiMcpClient {

    /**
     * MCP 카탈로그 목록 조회
     */
    @GetMapping("/api/v1/mcp/catalogs")
    @Operation(
        summary = "MCP 카탈로그 목록 조회",
        description = "MCP 카탈로그 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카탈로그 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    McpCatalogListResponse getCatalogs(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * MCP 카탈로그 생성
     */
    @PostMapping(value = "/api/v1/mcp/catalogs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "MCP 카탈로그 생성",
        description = "새로운 MCP 카탈로그를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카탈로그 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogCreateResponse createCatalog(@RequestBody McpCatalogCreateRequest request);

    /**
     * MCP 카탈로그 조회
     */
    @GetMapping("/api/v1/mcp/catalogs/{mcp_id}")
    @Operation(
        summary = "MCP 카탈로그 조회",
        description = "특정 MCP 카탈로그를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카탈로그 조회 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogResponse getCatalogById(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 수정
     */
    @PutMapping(value = "/api/v1/mcp/catalogs/{mcp_id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "MCP 카탈로그 수정",
        description = "기존 MCP 카탈로그를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카탈로그 수정 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogResponse updateCatalog(
        @PathVariable("mcp_id") String mcpId,
        @RequestBody McpCatalogUpdateRequest request
    );

    /**
     * MCP 카탈로그 삭제
     */
    @DeleteMapping("/api/v1/mcp/catalogs/{mcp_id}")
    @Operation(
        summary = "MCP 카탈로그 삭제",
        description = "MCP 카탈로그를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "카탈로그 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void deleteCatalog(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 Hard Delete
     */
    @PostMapping("/api/v1/mcp/catalogs/hard-delete")
    @Operation(
        summary = "MCP 카탈로그 Hard Delete",
        description = "MCP 카탈로그를 완전히 삭제합니다 (영구 삭제)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카탈로그 Hard Delete 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void hardDeleteCatalog();

    /**
     * MCP 연결 테스트
     */
    @PostMapping(value = "/api/v1/mcp/catalogs/test-connection",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "MCP 연결 테스트",
        description = "MCP 서버 연결을 테스트합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "연결 테스트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpTestConnectionResponse testConnection(@RequestBody McpTestConnectionRequest request);

    /**
     * MCP 카탈로그 Ping
     */
    @GetMapping("/api/v1/mcp/catalogs/{mcp_id}/ping")
    @Operation(
        summary = "MCP 카탈로그 Ping",
        description = "MCP 카탈로그 연결 상태를 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ping 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogPingResponse pingCatalog(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 활성화
     */
    @PostMapping("/api/v1/mcp/catalogs/{mcp_id}/activate")
    @Operation(
        summary = "MCP 카탈로그 활성화",
        description = "MCP 카탈로그를 활성화합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "활성화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    String activateCatalog(@PathVariable("mcp_id") String mcpId, @RequestBody List<PolicyRequest> policyRequests);

    /**
     * MCP 카탈로그 비활성화
     */
    @PostMapping("/api/v1/mcp/catalogs/{mcp_id}/deactivate")
    @Operation(
        summary = "MCP 카탈로그 비활성화",
        description = "MCP 카탈로그를 비활성화합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비활성화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    String deactivateCatalog(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 도구 조회
     */
    @GetMapping("/api/v1/mcp/catalogs/{mcp_id}/tools")
    @Operation(
        summary = "MCP 카탈로그 도구 조회",
        description = "MCP 카탈로그의 도구 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "도구 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogToolsResponse getCatalogTools(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 도구 동기화
     */
    @GetMapping("/api/v1/mcp/catalogs/{mcp_id}/sync-tools")
    @Operation(
        summary = "MCP 카탈로그 도구 동기화",
        description = "MCP 카탈로그의 도구를 동기화합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "도구 동기화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    McpCatalogToolsResponse syncCatalogTools(@PathVariable("mcp_id") String mcpId);

    /**
     * MCP 카탈로그 Import
     */
    @PostMapping(value = "/api/v1/mcp/catalogs/import",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "MCP 카탈로그 Import (JSON)",
        description = "JSON 데이터를 받아서 MCP 카탈로그를 생성하거나 검증합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "카탈로그 Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    McpCatalogImportResponse importCatalog(
        @Parameter(description = "MCP 카탈로그 ID", required = true) @RequestParam("mcp_id") String mcpId,
        @Parameter(description = "JSON 형식의 Catalog 데이터", required = true)
        @RequestBody Object jsonData);
}
