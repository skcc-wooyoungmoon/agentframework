package com.skax.aiplatform.service.agent.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.agent.dto.request.ToolRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolsResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentToolsService;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.agent.request.AgentToolReq;
import com.skax.aiplatform.dto.agent.response.AgentToolCreateRes;
import com.skax.aiplatform.dto.agent.response.AgentToolRes;
import com.skax.aiplatform.dto.agent.response.AgentToolUpdateRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.agent.AgentToolsMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.agent.AgentToolService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent Tools 관리 서비스 구현체
 * 
 * <p>Agent Tools 데이터 관리를 위한 비즈니스 로직을 구현합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentToolServiceImpl implements AgentToolService {
        
    private final SktaiAgentToolsService sktaiAgentToolsService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;

    private final AgentToolsMapper agentToolsMapper;
    private final AdminAuthService adminAuthService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AgentToolRes> getAgentToolsList(String name, Integer page, Integer size, String sort, String filter, String search) {  
        try {
            // Agent Tools 목록 조회
            ToolsResponse response = sktaiAgentToolsService.getTools(name, page, size, sort, filter, search);

            // Agent Tools 목록 변환
            List<AgentToolRes> agentToolList = response.getData().stream()
                    .map(agentToolsMapper::from)
                    .collect(Collectors.toList());

            for (AgentToolRes agentToolRes : agentToolList) {
                // 공개 여부 설정 (lst_prj_seq 값에 따라)
                GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/agent/tools/" + agentToolRes.getId()).orElse(null);
                String publicStatus = null;
                if (existing != null && existing.getLstPrjSeq() != null) {
                    // 음수면 "전체공유", 양수면 "내부공유"
                    publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                } else {
                    publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
                }
                agentToolRes.setPublicStatus(publicStatus);
            }

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(response.getPayload(), agentToolList);


        } catch (FeignException e) {
            log.error("Agent Tools 목록 조회 실패 Service - name={}, page={}, size={}, sort={}, filter={}, search={}, 에러={}", name, page, size, sort, filter, search, e.getMessage());
            log.debug("Agent Tools 목록 조회 실패 상세 Service - name={}, page={}, size={}, sort={}, filter={}, search={}, 에러={}", name, page, size, sort, filter, search, e.getMessage());
            throw new RuntimeException("Agent Tools 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public AgentToolRes getAgentToolById(String agentToolId) {

        try {
            log.info("Agent Tool 상세 조회 요청 Service - agentToolId={}", agentToolId);

            // Agent Tool API 호출
            ToolResponse response = sktaiAgentToolsService.getToolById(agentToolId);
            
            // 외부 API 응답 확인 로그
            if (response != null && response.getData() != null) {
                log.debug("외부 API 응답 - createdBy: {}, updatedBy: {}", 
                        response.getData().getCreatedBy(), 
                        response.getData().getUpdatedBy());
            }
            
            // ToolResponse를 AgentToolRes로 변환
            AgentToolRes agentToolRes = agentToolsMapper.from(response);
            
            log.info("Agent Tool 상세 조회 성공 Service - agentToolId={}", agentToolId);
            
            return agentToolRes;
            
        } catch (FeignException e) {
            log.error("Agent Tool 상세 조회 실패: agentToolId={}, 에러={}", agentToolId, e.getMessage());
            log.debug("Agent Tool 상세 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Agent Tool 정보를 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AgentToolCreateRes createAgentTool(AgentToolReq request) {
        log.info("Agent Tool 생성 요청: name={}, toolType={}", request.getName(), request.getToolType());
        
        // AgentToolReq를 ToolRequest로 변환
        ToolRequest toolRequest = agentToolsMapper.toRequest(request);
        
        // Agent Tool 생성
        ToolCreateResponse response = sktaiAgentToolsService.createTool(toolRequest);
        
        // Agent Tool ADXP 권한부여
        adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/tools/" + response.getData().getId());
        log.info("Agent Tool 권한 설정 완료");

        // 응답 변환
        AgentToolCreateRes result = agentToolsMapper.toCreateResponse(response);
        
        log.info("Agent Tool 생성 완료: agentToolUuid={}", result.getAgentToolUuid());
        
        return result;
    }

    @Override
    @Transactional
    public AgentToolUpdateRes updateAgentToolById(String agentToolId, AgentToolReq request) {
        log.info("Agent Tool 수정 요청: agentToolId={}, name={}", agentToolId, request.getName());

        // AgentToolReq를 ToolRequest로 변환
        ToolRequest toolRequest = agentToolsMapper.toRequest(request);
        
        // projectId가 없으면 기존 Tool의 projectId를 조회하여 설정
        if (toolRequest.getProjectId() == null || toolRequest.getProjectId().isBlank()) {
            try {
                log.info("projectId가 없어서 기존 Tool의 projectId를 조회합니다: agentToolId={}", agentToolId);
                ToolResponse existingTool = sktaiAgentToolsService.getToolById(agentToolId);
                if (existingTool != null && existingTool.getData() != null && existingTool.getData().getProjectId() != null) {
                    toolRequest.setProjectId(existingTool.getData().getProjectId());
                    log.info("기존 Tool의 projectId를 설정했습니다: projectId={}", existingTool.getData().getProjectId());
                } else {
                    log.warn("기존 Tool을 찾을 수 없거나 projectId가 없습니다: agentToolId={}", agentToolId);
                }
            } catch (NullPointerException e) {
              log.error("기존 Tool의 projectId를 조회하는 중 오류 발생: agentToolId={}, error={}", agentToolId, e.getMessage());
                throw new RuntimeException("기존 Tool 정보를 조회할 수 없습니다: " + e.getMessage(), e);
            } catch (Exception e) {
                log.error("기존 Tool의 projectId를 조회하는 중 오류 발생: agentToolId={}, error={}", agentToolId, e.getMessage());
                throw new RuntimeException("기존 Tool 정보를 조회할 수 없습니다: " + e.getMessage(), e);
            }
        }
        
        // Agent Tool 수정
        ToolUpdateResponse response = sktaiAgentToolsService.updateTool(agentToolId, toolRequest);
        log.info("Agent Tool 수정 성공: agentToolId={}", agentToolId);
        
        // 응답 변환
        AgentToolUpdateRes result = agentToolsMapper.toUpdateResponse(response);

        log.info("Agent Tool 수정 완료: agentToolId={}", agentToolId);

        return result;
    }

    @Override
    @Transactional
    public void deleteAgentToolById(String agentToolId) {
        log.info("Agent Tool 삭제 요청: agentToolId={}", agentToolId);
        
        // Agent Tool 삭제
        sktaiAgentToolsService.deleteTool(agentToolId);
        
        log.info("Agent Tool 삭제 완료: agentToolId={}", agentToolId);
    }
}
