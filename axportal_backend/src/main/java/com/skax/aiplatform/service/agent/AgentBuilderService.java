package com.skax.aiplatform.service.agent;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.agent.response.AgentAppInfoRes;
import com.skax.aiplatform.dto.agent.response.AgentBuilderRes;

/**
 * 에이전트 빌더 서비스 인터페이스
 * 
 * <p>
 * SKT AI Platform의 에이전트 그래프 관련 비즈니스 로직을 담당합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-19
 * @version 1.0.0
 */
public interface AgentBuilderService {

    /**
     * 에이전트 빌더 목록 조회
     * 
     * @param projectId 프로젝트 ID (UUID)
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param sort      정렬 조건
     * @param filter    필터 조건
     * @param search    검색 키워드
     * @return 페이징된 에이전트 빌더 목록
     */
    PageResponse<AgentBuilderRes> getAgentBuilders(String projectId, Integer page, Integer size, String sort,
            String filter, String search);

    /**
     * 에이전트 빌더 상세 조회
     * 
     * @param agentId 에이전트 ID
     * @return 에이전트 빌더 상세 정보
     */
    AgentBuilderRes getAgentBuilder(String agentId);

    /**
     * 에이전트 빌더 삭제
     * 
     * @param agentId 에이전트 ID
     */
    void deleteAgentBuilder(String agentId);

    /**
     * 에이전트 이름/설명 수정
     * 
     * @param agentId   에이전트 ID
     * @param updateReq 수정 요청 데이터 (name, description)
     * @return 수정된 에이전트 빌더 정보
     */
    AgentBuilderRes updateAgentInfo(String agentId, Map<String, Object> updateReq);

    /**
     * 에이전트 그래프 전체 저장
     * 
     * @param agentId 에이전트 ID
     * @param saveReq 그래프 저장 요청 데이터 (name, description, graph)
     * @return 저장된 에이전트 빌더 정보
     */
    AgentBuilderRes saveAgentGraph(String agentId, Map<String, Object> saveReq);

    /**
     * 에이전트 빌더 템플릿 목록 조회
     * 
     * @return 에이전트 빌더 템플릿 목록
     */
    Object getAgentBuilderTemplates();

    /**
     * 특정 템플릿 상세 조회
     * 
     * @param templateId 템플릿 ID
     * @return 특정 템플릿 상세 정보
     */
    Object getAgentBuilderTemplate(String templateId);

    /**
     * 템플릿 기반 에이전트 생성
     * 
     * @param requestBody 생성 요청 데이터
     * @return 생성된 에이전트 정보
     */
    Object createAgentFromTemplate(Map<String, Object> requestBody);

    /**
     * SKT AI Platform Agent Graph 스트리밍 실행 (스트리밍 응답)
     * 
     * @param request      스트리밍 요청 데이터
     * @param outputStream 출력 스트림
     */
    void streamAgentGraph(Map<String, Object> request, OutputStream outputStream) throws Exception;

    /**
     * 에이전트 배포 정보 조회
     * 
     * @param agentId 에이전트 ID
     * @return 에이전트 배포 정보 조회
     */
    AgentAppInfoRes getAgentDeployInfo(String agentId);

    /**
     * 에이전트 Lineage 조회
     * 
     * @param graphId 그래프 ID
     * @return Lineage 목록
     */
    List<LineageRelationWithTypes> getAgentLineages(String graphId);

    /**
     * Phoenix Trace Project 식별자 조회
     *
     * @param type 리소스 타입 (graph/app)
     * @param id   그래프 또는 앱 ID
     * @return Phoenix 프로젝트 ID (없으면 null)
     */
    String getPhoenixProjectIdentifier(String type, String id);

    /**
     * Agent Graph Export (Python 코드 조회)
     *
     * @param graphId        그래프 ID
     * @param credentialType 인증 타입 (token/password)
     * @return Python 코드 문자열
     */
    String exportAgentGraphCode(String graphId, String credentialType);

    /**
     * 에이전트 빌더 Policy 설정
     *
     * @param agentId     에이전트 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return Void 설정된 Policy 목록
     */
    void setAgentBuilderPolicy(String agentId, String memberId, String projectName);

}