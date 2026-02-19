package com.skax.aiplatform.controller.agent;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.agent.request.McpCatalogCreateReq;
import com.skax.aiplatform.dto.agent.request.McpCatalogUpdateReq;
import com.skax.aiplatform.dto.agent.request.McpTestConnectionReq;
import com.skax.aiplatform.dto.agent.response.McpCatalogCreateRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogInfoRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogPingRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogToolsRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogUpdateRes;
import com.skax.aiplatform.dto.agent.response.McpTestConnectionRes;
import com.skax.aiplatform.service.agent.AgentMcpService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent MCP 컨트롤러
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/agentMcp")
@RequiredArgsConstructor
@Tag(name = "Agent MCP", description = "Agent MCP 카탈로그 관리 API")
public class AgentMcpController {
    
    private final AgentMcpService agentMcpService;
    
    /**
     * MCP 카탈로그 목록 조회
     */
    @GetMapping("/ctlg")
    @Operation(
        summary = "MCP 카탈로그 목록 조회",
        description = "MCP 카탈로그 목록을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<McpCatalogInfoRes>> getCatalogs(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            
            @Parameter(description = "정렬 기준")
            @RequestParam(value = "sort", required = false) String sort,
            
            @Parameter(description = "필터")
            @RequestParam(value = "filter", required = false) String filter,
            
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("MCP 카탈로그 목록 조회 요청: page={}, size={}, sort={}, filter={}, search={}", 
                page, size, sort, filter, search);
        
        PageResponse<McpCatalogInfoRes>  response = agentMcpService.getCatalogs(page, size, sort, filter, search);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 목록 조회 성공");
    }
    
    /**
     * MCP 카탈로그 생성
     */
    @PostMapping("/ctlg")
    @Operation(
        summary = "MCP 카탈로그 생성",
        description = "새로운 MCP 카탈로그를 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogCreateRes> createCatalog(
            @Valid @RequestBody McpCatalogCreateReq request) {
        
        log.info("MCP 카탈로그 생성 요청: name={}, displayName={}", request.getName(), request.getDisplayName());
        
        McpCatalogCreateRes response = agentMcpService.createCatalog(request);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 생성 성공");
    }
    
    /**
     * MCP 카탈로그 조회
     */
    @GetMapping("/ctlg/{mcpId}")
    @Operation(
        summary = "MCP 카탈로그 조회",
        description = "특정 MCP 카탈로그의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogInfoRes> getCatalogById(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 조회 요청: mcpId={}", mcpId);
        
        McpCatalogInfoRes response = agentMcpService.getCatalogById(mcpId);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 조회 성공");
    }
    
    /**
     * MCP 카탈로그 수정
     */
    @PutMapping("/ctlg/{mcpId}")
    @Operation(
        summary = "MCP 카탈로그 수정",
        description = "기존 MCP 카탈로그의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogUpdateRes> updateCatalog(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId,
            
            @Valid @RequestBody McpCatalogUpdateReq request) {
        
        log.info("MCP 카탈로그 수정 요청: mcpId={}, name={}", mcpId, request.getName());
        
        McpCatalogUpdateRes response = agentMcpService.updateCatalog(mcpId, request);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 수정 성공");
    }
    
    /**
     * MCP 카탈로그 삭제
     */
    @DeleteMapping("/ctlg/{mcpId}")
    @Operation(
        summary = "MCP 카탈로그 삭제",
        description = "MCP 카탈로그를 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteCatalog(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 삭제 요청: mcpId={}", mcpId);
        
        agentMcpService.deleteCatalog(mcpId);
        
        return AxResponseEntity.ok(null, "MCP 카탈로그 삭제 성공");
    }
    
    /**
     * MCP 연결 테스트
     */
    @PostMapping("/test-connection")
    @Operation(
        summary = "MCP 연결 테스트",
        description = "MCP 서버 연결을 테스트합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "테스트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpTestConnectionRes> testConnection(
            @Valid @RequestBody McpTestConnectionReq request) {
        
        log.info("MCP 연결 테스트 요청: serverUrl={}, authType={}", 
                request.getServerUrl(), request.getAuthType());
        
        McpTestConnectionRes response = agentMcpService.testConnection(request);
        
        return AxResponseEntity.ok(response, "MCP 연결 테스트 성공");
    }
    
    /**
     * MCP 카탈로그 Ping
     */
    @Hidden
    @GetMapping("/ctlg/{mcpId}/ping")
    @Operation(
        summary = "MCP 카탈로그 Ping",
        description = "MCP 카탈로그의 연결 상태를 확인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ping 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogPingRes> pingCatalog(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 Ping 요청: mcpId={}", mcpId);
        
        McpCatalogPingRes response = agentMcpService.pingCatalog(mcpId);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 Ping 성공");
    }
    
    /**
     * MCP 카탈로그 활성화
     */
    @Hidden
    @PostMapping("/ctlg/{mcpId}/activate")
    @Operation(
        summary = "MCP 카탈로그 활성화",
        description = "MCP 카탈로그를 활성화합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "활성화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<String> activateCatalog(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId,
            @Parameter(description = "정책 요청 목록", required = true)
            @RequestBody List<PolicyRequest> policyRequests) {
        
        log.info("MCP 카탈로그 활성화 요청: mcpId={}", mcpId);
        
        String response = agentMcpService.activateCatalog(mcpId, policyRequests);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 활성화 성공");
    }
    
    /**
     * MCP 카탈로그 비활성화
     */
    @Hidden
    @PostMapping("/ctlg/{mcpId}/deactivate")
    @Operation(
        summary = "MCP 카탈로그 비활성화",
        description = "MCP 카탈로그를 비활성화합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비활성화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<String> deactivateCatalog(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 비활성화 요청: mcpId={}", mcpId);
        
        String response = agentMcpService.deactivateCatalog(mcpId);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 비활성화 성공");
    }
    
    /**
     * MCP 카탈로그 도구 조회
     */
    @GetMapping("/ctlg/{mcpId}/tools")
    @Operation(
        summary = "MCP 카탈로그 도구 조회",
        description = "MCP 카탈로그의 도구 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogToolsRes> getCatalogTools(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 도구 조회 요청: mcpId={}", mcpId);
        
        McpCatalogToolsRes response = agentMcpService.getCatalogTools(mcpId);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 도구 조회 성공");
    }
    
    /**
     * MCP 카탈로그 도구 동기화
     */
    @GetMapping("/ctlg/{mcpId}/sync-tools")
    @Operation(
        summary = "MCP 카탈로그 도구 동기화",
        description = "MCP 카탈로그의 도구를 동기화합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "동기화 성공"),
        @ApiResponse(responseCode = "404", description = "카탈로그를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<McpCatalogToolsRes> syncCatalogTools(
            @Parameter(description = "MCP 카탈로그 ID", required = true)
            @PathVariable("mcpId") String mcpId) {
        
        log.info("MCP 카탈로그 도구 동기화 요청: mcpId={}", mcpId);
        
        McpCatalogToolsRes response = agentMcpService.syncCatalogTools(mcpId);
        
        return AxResponseEntity.ok(response, "MCP 카탈로그 도구 동기화 성공");
    }
}
