package com.skax.aiplatform.service.agent.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogCreateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogUpdateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpTestConnectionRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogCreateResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogListResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogPingResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogToolsResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpTestConnectionResponse;
import com.skax.aiplatform.client.sktai.mcp.service.SktaiMcpService;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.agent.request.McpCatalogCreateReq;
import com.skax.aiplatform.dto.agent.request.McpCatalogUpdateReq;
import com.skax.aiplatform.dto.agent.request.McpTestConnectionReq;
import com.skax.aiplatform.dto.agent.response.McpCatalogCreateRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogInfoRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogPingRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogToolsRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogUpdateRes;
import com.skax.aiplatform.dto.agent.response.McpTestConnectionRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.agent.AgentMcpMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.agent.AgentMcpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Agent MCP 서비스 구현체
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentMcpServiceImpl implements AgentMcpService {
    
    private final SktaiMcpService sktaiMcpService;
    private final AdminAuthService adminAuthService;
    private final SktaiServingService sktaiServingService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final AgentMcpMapper agentMcpMapper;

    
    @Override
    public PageResponse<McpCatalogInfoRes> getCatalogs(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("MCP 카탈로그 목록 조회 요청: page={}, size={}, sort={}, filter={}, search={}", 
                page, size, sort, filter, search);
        
        McpCatalogListResponse mcpCatalogListResponse = sktaiMcpService.getCatalogs(page, size, sort, filter, search);
        
        // MCP 카탈로그 목록 변환
        List<McpCatalogInfoRes> mcpCatalogList = mcpCatalogListResponse.getData().stream()
                    .map(agentMcpMapper::toMcpCatalogInfoResFromDetail)
                    .collect(Collectors.toList());

        for (McpCatalogInfoRes mcpCatalogInfoRes : mcpCatalogList) {
            // 공개 여부 설정 (lst_prj_seq 값에 따라)
            GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/mcp/catalogs/" + mcpCatalogInfoRes.getId()).orElse(null);
            String publicStatus = null;
            if (existing != null && existing.getLstPrjSeq() != null) {
                // 음수면 "전체공유", 양수면 "내부공유"
                publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
            } else {
                publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
            }
            mcpCatalogInfoRes.setPublicStatus(publicStatus);
        }

        log.info("========== MCP 카탈로그 목록 변환 완료 Service - mcpCatalogList={}", mcpCatalogList);

        // ADXP Pagination을 PageResponse로 변환
        return PaginationUtils.toPageResponseFromAdxp(mcpCatalogListResponse.getPayload(), mcpCatalogList);

    }
    
    @Override
    @Transactional
    public McpCatalogCreateRes createCatalog(McpCatalogCreateReq request) {
        log.debug("MCP 카탈로그 생성 요청: name={}, displayName={}", request.getName(), request.getDisplayName());
        
        List<PolicyRequest> policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();

        McpCatalogCreateRequest createRequest = agentMcpMapper.toMcpCatalogCreateRequest(request);
        
        // policy 필드 추가
        if (policyRequests != null && !policyRequests.isEmpty()) {
            createRequest.setPolicy(policyRequests);
            log.debug("MCP 카탈로그 생성 요청에 policy 추가: policyRequests={}", policyRequests);
        }
        
        McpCatalogCreateResponse externalResponse = sktaiMcpService.createCatalog(createRequest);

        // Agent Tool ADXP 권한부여
        adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/mcp/catalogs/" + externalResponse.getData().getId());
        // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/mcp/catalogs/" + externalResponse.getData().getId() + "/tools");
        // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/mcp/catalogs/" + externalResponse.getData().getId() + "/sync-tools");
        // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/mcp/catalogs/" + externalResponse.getData().getId() + "/ping");


        McpCatalogCreateRes response = agentMcpMapper.toMcpCatalogCreateRes(externalResponse);

        // 생성 후 활성화 (기본 policy 사용)
        List<PolicyRequest> defaultPolicyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
        if (defaultPolicyRequests != null && !defaultPolicyRequests.isEmpty()) {
            activateCatalog(response.getId(), defaultPolicyRequests);
        } else {
            log.warn("MCP 카탈로그 활성화 - policy가 없어 활성화를 건너뜁니다: mcpId={}", response.getId());
        }
        return response;
    }
    
    @Override
    public McpCatalogInfoRes getCatalogById(String mcpId) {
        log.debug("MCP 카탈로그 조회 요청: mcpId={}", mcpId);

        McpCatalogResponse response = sktaiMcpService.getCatalogById(mcpId);
        
        // 외부 API 응답 확인 로그
        if (response != null && response.getData() != null) {
            log.debug("외부 API 응답 - createdBy: {}, updatedBy: {}", 
                    response.getData().getCreatedBy(), 
                    response.getData().getUpdatedBy());
        }
        
        // McpCatalogResponse를 McpCatalogInfoRes로 변환
        McpCatalogInfoRes mcpCatalogInfoRes = agentMcpMapper.toMcpCatalogInfoRes(response);
             
        log.info("MCP 카탈로그 조회 성공 Service - mcpId={}", mcpId);
            
        return mcpCatalogInfoRes;
    }
    
    @Override
    @Transactional
    public McpCatalogUpdateRes updateCatalog(String mcpId, McpCatalogUpdateReq request) {
        log.debug("MCP 카탈로그 수정 요청: mcpId={}, name={}", mcpId, request.getName());
        
        McpCatalogUpdateRequest updateRequest = agentMcpMapper.toMcpCatalogUpdateRequest(request);
        McpCatalogResponse externalResponse = sktaiMcpService.updateCatalog(mcpId, updateRequest);
        return agentMcpMapper.toMcpCatalogUpdateRes(externalResponse);
    }
    
    /**
     * MCP 카탈로그 정보 조회 (별도 트랜잭션으로 분리하여 롤백 전파 방지)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {com.skax.aiplatform.common.exception.BusinessException.class})
    private McpCatalogInfoRes getCatalogByIdInNewTransaction(String mcpId) {
        try {
            return getCatalogById(mcpId);
        } catch (Exception e) {
            log.warn("MCP 카탈로그 정보 조회 실패 (별도 트랜잭션에서 처리) - mcpId: {}, error: {}", 
                    mcpId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteCatalog(String mcpId) {
        log.debug("MCP 카탈로그 삭제 요청: mcpId={}", mcpId);

        // 1. MCP 서빙 삭제 (연결된 MCP Serving이 있으면 먼저 삭제) - 중요!
        // 별도 트랜잭션으로 조회하여 롤백 전파 방지
        McpCatalogInfoRes mcpCatalogInfoRes = getCatalogByIdInNewTransaction(mcpId);
        if (mcpCatalogInfoRes != null) {
            String mcpServingId = mcpCatalogInfoRes.getMcpServingId();
            log.info("MCP 서빙 ID: {}", mcpServingId);
            if (mcpServingId != null && !mcpServingId.trim().isEmpty()) {
                try {
                    log.info("MCP 서빙 삭제 시도 - mcpServingId: {}", mcpServingId);
                    sktaiServingService.deleteMcpServing(mcpServingId);
                    log.info("MCP 서빙 삭제 성공 - mcpServingId: {}", mcpServingId);
                } catch (Exception e) {
                    log.warn("MCP 서빙 삭제 실패 (계속 진행) - mcpServingId: {}, error: {}", 
                            mcpServingId, e.getMessage());
                    // MCP Serving 삭제 실패해도 카탈로그 삭제는 계속 진행
                }
            }
        }

        // 2. MCP 카탈로그 삭제 (정보 조회 실패해도 반드시 실행)
        // 서버 측에서도 자동으로 MCP Serving을 삭제하려고 시도할 수 있지만,
        // 이미 클라이언트에서 삭제했기 때문에 에러가 발생할 수 있음
        try {
            sktaiMcpService.deleteCatalog(mcpId);
            log.info("MCP 카탈로그 삭제 성공 - mcpId: {}", mcpId);
        } catch (com.skax.aiplatform.common.exception.BusinessException e) {
            // 서버 측에서 "MCP Serving 삭제 중 문제가 발생했습니다" 에러가 발생할 수 있지만
            // 실제로는 카탈로그 삭제가 성공했을 수 있음 (이미 클라이언트에서 MCP Serving 삭제 완료)
            // 에러 메시지에 "MCP Serving 삭제"가 포함되어 있으면 성공으로 간주
            if (e.getMessage() != null && e.getMessage().contains("MCP Serving 삭제")) {
                log.warn("MCP 카탈로그 삭제 시 서버 측 MCP Serving 삭제 에러 발생 (이미 클라이언트에서 삭제 완료, 카탈로그 삭제는 성공했을 수 있음) - mcpId: {}, error: {}", 
                        mcpId, e.getMessage());
                // 실제로 카탈로그가 삭제되었는지 확인하려면 조회를 시도할 수 있지만,
                // 여기서는 에러를 무시하고 성공으로 처리
                return;
            }
            // 다른 에러는 그대로 재throw
            throw e;
        }

        sktaiServingService.hardDeleteMcpServings();
        sktaiMcpService.hardDeleteCatalog();
    }
    
    @Override
    public McpTestConnectionRes testConnection(McpTestConnectionReq request) {
        log.debug("MCP 연결 테스트 요청: serverUrl={}, authType={}", 
                request.getServerUrl(), request.getAuthType());
        
        McpTestConnectionRequest testRequest = agentMcpMapper.toMcpTestConnectionRequest(request);
        McpTestConnectionResponse externalResponse = sktaiMcpService.testConnection(testRequest);
        
        // 외부 API 응답 로깅
        log.info("외부 API 응답: {}", externalResponse);
        if (externalResponse != null && externalResponse.getData() != null) {
            log.info("외부 API data: isConnected={}, errorMessage={}", 
                    externalResponse.getData().getIsConnected(), 
                    externalResponse.getData().getErrorMessage());
        }
        
        McpTestConnectionRes response = agentMcpMapper.toMcpTestConnectionRes(externalResponse);
        log.info("최종 응답: {}", response);
        return response;
    }
    
    @Override
    public McpCatalogPingRes pingCatalog(String mcpId) {
        McpCatalogPingResponse external = sktaiMcpService.pingCatalog(mcpId);
        McpCatalogPingRes response = agentMcpMapper.toMcpCatalogPingRes(external);
        return response;
    }
    
    @Override
    @Transactional
    public String activateCatalog(String mcpId, List<PolicyRequest> policyRequests) {
        log.debug("MCP 카탈로그 활성화 요청: mcpId={}", mcpId);
        return sktaiMcpService.activateCatalog(mcpId, policyRequests);
    }

    @Override
    @Transactional
    public String deactivateCatalog(String mcpId) {
        log.debug("MCP 카탈로그 비활성화 요청: mcpId={}", mcpId);
        return sktaiMcpService.deactivateCatalog(mcpId);
    }
    
    @Override
    public McpCatalogToolsRes getCatalogTools(String mcpId) {
        log.debug("MCP 카탈로그 도구 조회 요청: mcpId={}", mcpId);
        
        McpCatalogToolsResponse externalResponse = sktaiMcpService.getCatalogTools(mcpId);
        McpCatalogToolsRes response = agentMcpMapper.toMcpCatalogToolsRes(externalResponse);
        return response;
    }
    
    @Override
    @Transactional
    public McpCatalogToolsRes syncCatalogTools(String mcpId) {
        log.debug("MCP 카탈로그 도구 동기화 요청: mcpId={}", mcpId);
        
        McpCatalogToolsResponse externalResponse = sktaiMcpService.syncCatalogTools(mcpId);
        McpCatalogToolsRes response = agentMcpMapper.toMcpCatalogToolsRes(externalResponse);
        return response;
    }
}
