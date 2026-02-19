package com.skax.aiplatform.client.sktai.agent.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.SktaiAgentToolsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.ToolRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolImportResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolsResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Agent Tools API 서비스
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentToolsService {
    
    private final SktaiAgentToolsClient sktaiAgentToolsClient;
    
    public ToolsResponse getTools(String name, Integer page, Integer size, String sort, String filter, String search) {
    // public ToolsResponse getTools(String name, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Tools 목록 조회 요청 - name: {}, page: {}, size: {}", name, page, size);
            
            // // project_id 파라미터 추가 (기본값: default project)
            // String projectId = "{\"id\":\"24ba585a-02fc-43d8-b9f1-f7ca9e020fe5\",\"name\":\"default\"}";
            // log.debug("Tools 목록 조회 - projectId: {}", projectId);
            
            // ToolsResponse response = sktaiAgentToolsClient.getTools(name, projectId, page, size, sort, filter, search);
            
            ToolsResponse response = sktaiAgentToolsClient.getTools(name, page, size, sort, filter, search);
            log.debug("Tools 목록 조회 성공 - response: {}", response.getData().toString());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Tools 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tools 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tools 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public ToolCreateResponse createTool(ToolRequest request) {
        ToolCreateResponse response;
        try {
            log.debug("Tool 생성 요청");
            response = sktaiAgentToolsClient.createTool(request);
            log.debug("Tool 생성 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Tool 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 생성 실패", e);
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Tool 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    public ToolResponse getToolById(String toolId) {
        try {
            log.debug("Tool 상세 조회 요청 - toolId: {}", toolId);
            ToolResponse response = sktaiAgentToolsClient.getToolById(toolId);
            log.debug("Tool 상세 조회 성공 - toolId: {}", toolId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Tool 상세 조회 실패 (BusinessException) - toolId: {}, message: {}", 
                    toolId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 상세 조회 실패 - toolId: {}", toolId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public ToolUpdateResponse updateTool(String toolId, ToolRequest request) {
        try {
            log.debug("Tool 수정 요청");
            ToolUpdateResponse response = sktaiAgentToolsClient.updateTool(toolId, request);
            log.debug("Tool 수정 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Tool 수정 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 수정 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool 수정에 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteTool(String toolId) {
        try {
            log.debug("Tool 삭제 요청");
            sktaiAgentToolsClient.deleteTool(toolId);
            log.debug("Tool 삭제 성공");
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Tool 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 삭제 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Tool Import (JSON)
     * 
     * <p>JSON 문자열을 받아서 Tool을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param toolId Tool ID
     * @param json JSON 문자열
     * @return 생성된 Tool 정보
     */
    public ToolImportResponse importTool(String toolId, String json) {
        try {
            log.info("Tool Import 요청 - toolId: {}, jsonLength: {}", toolId, json != null ? json.length() : 0);
            
            // JSON 문자열을 Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonData = objectMapper.readValue(json, Object.class);
            
            ToolImportResponse response = sktaiAgentToolsClient.importTool(toolId, jsonData);
            log.info("Tool Import 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Tool Import 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Tool Import 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool Import에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 하드 삭제
     * 
     * <p>삭제 마크된 모든 Tool들을 데이터베이스에서 완전히 삭제합니다.</p>
     * 
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     */
    public void hardDeleteTools() {
        try {
            log.debug("Tool 하드 삭제 요청");
            sktaiAgentToolsClient.hardDeleteTools();
            log.debug("Tool 하드 삭제 성공");
        } catch (BusinessException e) {
            log.error("Tool 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        }
    }
}
