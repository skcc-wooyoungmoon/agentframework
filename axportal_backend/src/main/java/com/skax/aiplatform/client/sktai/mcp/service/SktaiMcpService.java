package com.skax.aiplatform.client.sktai.mcp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.mcp.SktaiMcpClient;
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
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI MCP (Model Context Protocol) Service
 * 
 * <p>SKTAI MCP API 호출을 위한 비즈니스 로직 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 로직을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>카탈로그 관리</strong>: MCP 카탈로그 CRUD 작업</li>
 *   <li><strong>연결 테스트</strong>: MCP 서버 연결 상태 확인</li>
 *   <li><strong>도구 관리</strong>: MCP 도구 조회 및 동기화</li>
 *   <li><strong>상태 관리</strong>: 카탈로그 활성화/비활성화</li>
 *   <li><strong>에러 처리</strong>: 외부 API 오류 처리</li>
 *   <li><strong>모니터링</strong>: 요청/응답 로깅</li>
 * </ul>
 *
 * @since 2025-09-30
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SktaiMcpService {
    
    private final SktaiMcpClient sktaiMcpClient;
    
    // ==================== 카탈로그 관리 ====================
    
    /**
     * MCP 카탈로그 목록 조회
     */
    public McpCatalogListResponse getCatalogs(Integer page, Integer size, String sort, String filter, String search) {
        log.info("MCP 카탈로그 목록 조회 요청: page={}, size={}, sort={}, filter={}, search={}", 
                page, size, sort, filter, search);
        try {
            McpCatalogListResponse response = sktaiMcpClient.getCatalogs(page, size, sort, filter, search);
            log.info("MCP 카탈로그 목록 조회 성공: 총 {}건", 
                    response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", 
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 생성
     */
    @Transactional
    public McpCatalogCreateResponse createCatalog(McpCatalogCreateRequest request) {
        log.info("MCP 카탈로그 생성 요청: name={}, display_name={}", request.getName(), request.getDisplayName());
        try {
            McpCatalogCreateResponse response = sktaiMcpClient.createCatalog(request);
            log.info("MCP 카탈로그 생성 성공: catalog_id={}", 
                    response.getData() != null ? response.getData().getId() : "null");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 사용자 친화적인 메시지로 변환
            log.error("MCP 카탈로그 생성 실패 (BusinessException) - name: {}, display_name: {}, message: {}", 
                    request.getName(), request.getDisplayName(), e.getMessage());
            
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("MCP 카탈로그 생성 실패 (예상치 못한 오류) - name: {}, display_name: {}", 
                    request.getName(), request.getDisplayName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 조회
     */
    public McpCatalogResponse getCatalogById(String mcpId) {
        log.info("MCP 카탈로그 조회 요청: mcp_id={}", mcpId);
        try {
            McpCatalogResponse response = sktaiMcpClient.getCatalogById(mcpId);
            log.info("MCP 카탈로그 조회 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 조회 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 조회 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 수정
     */
    @Transactional
    public McpCatalogResponse updateCatalog(String mcpId, McpCatalogUpdateRequest request) {
        log.info("MCP 카탈로그 수정 요청: mcp_id={}, name={}", mcpId, request.getName());
        try {
            McpCatalogResponse response = sktaiMcpClient.updateCatalog(mcpId, request);
            log.info("MCP 카탈로그 수정 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 수정 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 수정 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 삭제
     */
    @Transactional
    public void deleteCatalog(String mcpId) {
        log.info("MCP 카탈로그 삭제 요청: mcp_id={}", mcpId);
        try {
            sktaiMcpClient.deleteCatalog(mcpId);
            log.info("MCP 카탈로그 삭제 성공: mcp_id={}", mcpId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 삭제 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 삭제 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 Hard Delete
     */
    @Transactional
    public void hardDeleteCatalog() {
        log.info("MCP 카탈로그 Hard Delete 요청");
        try {
            sktaiMcpClient.hardDeleteCatalog();
            log.info("MCP 카탈로그 Hard Delete 성공");
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 Hard Delete 실패 (BusinessException) - message: {}", 
                    e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 Hard Delete 실패 (예상치 못한 오류) - message: {}", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 Hard Delete에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== 연결 테스트 ====================
    
    /**
     * MCP 연결 테스트
     */
    public McpTestConnectionResponse testConnection(McpTestConnectionRequest request) {
        log.info("MCP 연결 테스트 요청: server_url={}, auth_type={}", 
                request.getServerUrl(), request.getAuthType());
        try {
            McpTestConnectionResponse response = sktaiMcpClient.testConnection(request);
            log.info("MCP 연결 테스트 성공: server_url={}", request.getServerUrl());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 연결 테스트 실패 (BusinessException) - server_url: {}, message: {}", 
                    request.getServerUrl(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 연결 테스트 실패 (예상치 못한 오류) - server_url: {}", 
                    request.getServerUrl(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 연결 테스트에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 Ping
     */
    public McpCatalogPingResponse pingCatalog(String mcpId) {
        log.info("MCP 카탈로그 Ping 요청: mcp_id={}", mcpId);
        try {
            McpCatalogPingResponse response = sktaiMcpClient.pingCatalog(mcpId);
            log.info("MCP 카탈로그 Ping 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 Ping 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 Ping 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 Ping에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== 상태 관리 ====================
    
    /**
     * MCP 카탈로그 활성화
     */
    @Transactional
    public String activateCatalog(String mcpId, List<PolicyRequest> policyRequests) {
        log.info("MCP 카탈로그 활성화 요청: mcp_id={}", mcpId);
        try {
            String response = sktaiMcpClient.activateCatalog(mcpId, policyRequests);
            log.info("MCP 카탈로그 활성화 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 활성화 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 활성화 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 활성화에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 비활성화
     */
    @Transactional
    public String deactivateCatalog(String mcpId) {
        log.info("MCP 카탈로그 비활성화 요청: mcp_id={}", mcpId);
        try {
            String response = sktaiMcpClient.deactivateCatalog(mcpId);
            log.info("MCP 카탈로그 비활성화 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 비활성화 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 비활성화 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 비활성화에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== 도구 관리 ====================
    
    /**
     * MCP 카탈로그 도구 조회
     */
    public McpCatalogToolsResponse getCatalogTools(String mcpId) {
        log.info("MCP 카탈로그 도구 조회 요청: mcp_id={}", mcpId);
        try {
            McpCatalogToolsResponse response = sktaiMcpClient.getCatalogTools(mcpId);
            log.info("MCP 카탈로그 도구 조회 성공: mcp_id={}, 도구 수={}", 
                    mcpId, response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 도구 조회 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 도구 조회 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 도구 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MCP 카탈로그 도구 동기화
     */
    @Transactional
    public McpCatalogToolsResponse syncCatalogTools(String mcpId) {
        log.info("MCP 카탈로그 도구 동기화 요청: mcp_id={}", mcpId);
        try {
            McpCatalogToolsResponse response = sktaiMcpClient.syncCatalogTools(mcpId);
            log.info("MCP 카탈로그 도구 동기화 성공: mcp_id={}", mcpId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 카탈로그 도구 동기화 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 카탈로그 도구 동기화 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 도구 동기화에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Import ====================
    
    /**
     * MCP 카탈로그 Import
     */
    @Transactional
    public McpCatalogImportResponse importCatalog(String mcpId, String json) {
        log.info("MCP 카탈로그 Import 요청: mcp_id={}, jsonLength: {}", mcpId, json != null ? json.length() : 0);
        try {
            // JSON 문자열을 Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonData = objectMapper.readValue(json, Object.class);
            
            McpCatalogImportResponse response = sktaiMcpClient.importCatalog(mcpId, jsonData);
            log.info("MCP 카탈로그 Import 성공 - mcp_id: {}, code: {}", mcpId, response.getCode());
            return response;
        } catch (BusinessException e) {
            log.error("MCP 카탈로그 Import 실패 (BusinessException) - mcp_id: {}, message: {}", 
                    mcpId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("MCP 카탈로그 Import 실패 (예상치 못한 오류) - mcp_id: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "MCP 카탈로그 Import에 실패했습니다: " + e.getMessage());
        }
    }
}
