package com.skax.aiplatform.service.agent;

import java.util.List;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
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
/**
 * Agent MCP 서비스 인터페이스
 * 
 * @since 2025-10-01
 * @version 1.0
 */
public interface AgentMcpService {
    
    /**
     * MCP 카탈로그 목록 조회
     */
    PageResponse<McpCatalogInfoRes> getCatalogs(Integer page, Integer size, String sort, String filter, String search);
    
    /**
     * MCP 카탈로그 생성
     */
    McpCatalogCreateRes createCatalog(McpCatalogCreateReq request);
    
    /**
     * MCP 카탈로그 조회
     */
    McpCatalogInfoRes getCatalogById(String mcpId);
    
    /**
     * MCP 카탈로그 수정
     */
    McpCatalogUpdateRes updateCatalog(String mcpId, McpCatalogUpdateReq request);
    
    /**
     * MCP 카탈로그 삭제
     */
    void deleteCatalog(String mcpId);
    
    /**
     * MCP 연결 테스트
     */
    McpTestConnectionRes testConnection(McpTestConnectionReq request);
    
    /**
     * MCP 카탈로그 Ping
     */
    McpCatalogPingRes pingCatalog(String mcpId);
    
    /**
     * MCP 카탈로그 활성화
     */
    String activateCatalog(String mcpId, List<PolicyRequest> policyRequests);
    
    /**
     * MCP 카탈로그 비활성화
     */
    String deactivateCatalog(String mcpId);
    
    /**
     * MCP 카탈로그 도구 조회
     */
    McpCatalogToolsRes getCatalogTools(String mcpId);
    
    /**
     * MCP 카탈로그 도구 동기화
     */
    McpCatalogToolsRes syncCatalogTools(String mcpId);
}
