package com.skax.aiplatform.service.agent;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.agent.request.AgentToolReq;
import com.skax.aiplatform.dto.agent.response.AgentToolCreateRes;
import com.skax.aiplatform.dto.agent.response.AgentToolRes;
import com.skax.aiplatform.dto.agent.response.AgentToolUpdateRes;

/**
 * Agent Tools 관리 서비스 인터페이스
 * 
 * <p>Agent Tools 데이터 관리를 위한 비즈니스 로직을 정의합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
public interface AgentToolService {

    /**
     * Agent Tools 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return Agent Tools 목록 (페이지네이션 포함)
     */
    PageResponse<AgentToolRes> getAgentToolsList(String name, Integer page, Integer size, String sort, String filter, String search);

    /**
     * Agent Tools 상세 정보 조회
     * 
     * @param agentToolId Agent Tools ID
     * @return Agent Tools 상세 정보
     */
    AgentToolRes getAgentToolById(String agentToolId);

    /**
     * Agent Tools 생성
     * 
     * @param request Agent Tools 생성 요청
     * @return 생성된 Agent Tools 정보
     */
    AgentToolCreateRes createAgentTool(AgentToolReq request);

    /**
     * Agent Tools 수정
     * 
     * @param agentToolId Agent Tools ID
     * @param request Agent Tools 수정 요청
     */
    AgentToolUpdateRes updateAgentToolById(String agentToolId, AgentToolReq request);

    /**
     * Agent Tools 삭제
     * 
     * @param agentToolId Agent Tools ID
     */
    void deleteAgentToolById(String agentToolId);
}
