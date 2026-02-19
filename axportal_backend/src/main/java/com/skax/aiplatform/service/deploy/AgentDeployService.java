package com.skax.aiplatform.service.deploy;

import java.util.List;
import java.util.Map;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.AgentSysLogSearchReq;
import com.skax.aiplatform.dto.deploy.request.AppCreateReq;
import com.skax.aiplatform.dto.deploy.request.AppUpdateReq;
import com.skax.aiplatform.dto.deploy.request.StreamReq;
import com.skax.aiplatform.dto.deploy.response.AgentAppRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployInfoRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployUpdateOrDeleteRes;
import com.skax.aiplatform.dto.deploy.response.AgentServingRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeyCreateRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeysRes;
import com.skax.aiplatform.dto.deploy.response.AppCreateRes;
/**
 * Agent 배포 관리 서비스
 * 
 * <p>Agent 애플리케이션의 배포, 관리, 모니터링을 담당하는 비즈니스 로직을 제공합니다.</p>
 * 
 * @since 2025-09-01
 * @version 1.0
 */
public interface AgentDeployService {

    /**
     * Agent App(배포) 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터
     * @param search 검색어
     * @return 배포 목록
     */
    PageResponse<AgentAppRes> getAgentAppList(String targetType, Integer page, Integer size, String sort, String filter, String search);

    /**
     * Agent App 상세 조회
     * 
     * @param appId 배포 ID
     * @return 배포 상세 정보
     */
    AgentAppRes getAgentAppById(String appId);

    /**
     * 새로운 Agent App 생성
     * 
     * @param request 앱 생성 요청 데이터
     * @return 생성된 앱 정보
     */
    AppCreateRes createAgentApp(AppCreateReq request);

    /**
     * 커스텀 Agent App 생성 및 배포
     * 
     * @param envFile 환경 파일 (선택)
     * @param name 앱 이름 (필수)
     * @param description 앱 설명 (필수)
     * @param versionDescription 버전 설명
     * @param targetType 타겟 타입
     * @param modelList 모델 목록
     * @param imageUrl 이미지 URL
     * @param useExternalRegistry 외부 레지스트리 사용 여부
     * @param cpuRequest CPU 요청
     * @param cpuLimit CPU 제한
     * @param memRequest 메모리 요청 (GB)
     * @param memLimit 메모리 제한 (GB)
     * @param minReplicas 최소 복제본 수
     * @param maxReplicas 최대 복제본 수
     * @param workersPerCore 코어당 워커 수
     * @param safetyFilterOptions 안전 필터 옵션
     * @param userId 정책 조회에 사용할 사용자 ID (선택, 없으면 현재 사용자 기준)
     * @param projectName 정책 조회에 사용할 프로젝트명 (선택, projectName이 있으면 memberId와 함께 사용)
     * @return 생성된 앱 정보
     */
    AppCreateRes createCustomAgentApp(
            org.springframework.web.multipart.MultipartFile envFile,
            String name,
            String description,
            String versionDescription,
            String targetType,
            List<String> modelList,
            String imageUrl,
            Boolean useExternalRegistry,
            Integer cpuRequest,
            Integer cpuLimit,
            Integer memRequest,
            Integer memLimit,
            Integer minReplicas,
            Integer maxReplicas,
            Integer workersPerCore,
            Object safetyFilterOptions,
            String userId,
            String projectName);

    /**
     * Agent 앱 수정
     * 
     * @param deployId 앱 ID
     * @param request 앱 수정 요청
     * @return 수정된 배포 정보
     */
    void updateAgentApp(String appId, AppUpdateReq request);

    /**
     * Agent 앱 삭제
     * 
     * @param deployId 배포 ID
     * @return Agent 앱 삭제
     */
    void deleteAgentApp(String appId);
    
    /**
     * Agent App API 키 목록 조회
     * 
     * @param appId 앱 ID
     * @return API 키 목록을 포함한 배포 정보
     */
    AppApiKeysRes getAgentAppApiKeyListById(String appId);


        /**
     * Agent App API 키 발급
     * 
     * @param appId 앱 ID
     * @return API 키 목록을 포함한 배포 정보
     */
    AppApiKeyCreateRes createAgentAppApiKey(String appId);

    /**
     * Agent App별 배포 목록 조회
     * 
     * @param appId 앱 ID
     * @return 배포 목록
     */
    PageResponse<AgentDeployRes> getAgentAppDeployListById(String appId, Integer page, Integer size, String sort, String filter, String search);

    /**
     * Agent App내 배포별 상세 조회
     * 
     * @param appId 배포 ID
     * @return Agent App내 배포별 상세 조회
     */
    AgentDeployRes getAgentAppDeployById(String deploymentId);

    /**
     * Agent 배포 버전 삭제
     * 
     * @param deployId 배포 ID
     */
    void deleteAgentAppDeploy(String deployId);
    
    /**
     * Agent 배포 버전 중지
     * 
     * @param deployId 배포 ID
     * @return 중지된 배포 정보
     */
    AgentDeployUpdateOrDeleteRes stopAgentDeploy(String deployId);
    
    /**
     * Agent 배포 버전 재시작
     * 
     * @param deployId 배포 ID
     * @return 재시작된 배포 정보
     */
    AgentDeployUpdateOrDeleteRes restartAgentDeploy(String deployId);


    /**
     * Agent 서빙 상세 조회
     * 
     * @param agentServingId 에이전트 서빙 ID
     * @return 에이전트 서빙 상세 정보
     */
    AgentServingRes getAgentServing(String agentServingId); 

    /**
     * Agent 스트리밍 추론 (Raw SSE)
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 스트리밍 요청 정보
     * @param authorization SKTAI API 인증 헤더
     * @return Raw SSE 응답 문자열
     */
    String getStreamAgentRaw(String agentId, String routerPath, StreamReq request, String authorization);

    /**
     * Agent 클러스터 리소스 조회
     * 
     * @param nodeType 노드 타입
     * @return 클러스터 리소스 정보
     */
    Map<String, Object> getClusterResources(String nodeType);

    /**
     * Elasticsearch 시스템 로그 검색
     * 
     * @param index 인덱스명
     * @param request 검색 요청 데이터
     * @return 검색 결과
     */
    String getAgentSysLog(String index, AgentSysLogSearchReq request);
    
    /**
     * Agent 배포 정보 조회
     * 
     * @param agentId Agent ID
     * @return Agent 배포 정보
     */
    AgentDeployInfoRes getAgentDeployInfo(String agentId);
    
    /**
     * 커스텀 배포 추가 (Multipart)
     * 
     * @param appId 앱 ID (필수)
     * @param envFile 환경 파일 (선택)
     * @param name 앱 이름 (필수)
     * @param description 앱 설명 (필수)
     * @param versionDescription 버전 설명 (선택)
     * @param targetType 타겟 타입
     * @param modelList 모델 목록
     * @param imageUrl 이미지 URL
     * @param useExternalRegistry 외부 레지스트리 사용 여부
     * @param cpuRequest CPU 요청
     * @param cpuLimit CPU 제한
     * @param memRequest 메모리 요청 (GB)
     * @param memLimit 메모리 제한 (GB)
     * @param minReplicas 최소 복제본 수
     * @param maxReplicas 최대 복제본 수
     * @param workersPerCore 코어당 워커 수
     * @param safetyFilterOptions 안전 필터 옵션
     * @param userId 정책 조회에 사용할 사용자 ID (선택, 없으면 현재 사용자 기준)
     * @param projectName 정책 조회에 사용할 프로젝트명 (선택, projectName이 있으면 memberId와 함께 사용)
     * @return 생성된 배포 정보
     */
    AgentDeployRes addCustomDeploymentWithMultipart(
            String appId,
            org.springframework.web.multipart.MultipartFile envFile,
            String name,
            String description,
            String versionDescription,
            String targetType,
            List<String> modelList,
            String imageUrl,
            Boolean useExternalRegistry,
            Integer cpuRequest,
            Integer cpuLimit,
            Integer memRequest,
            Integer memLimit,
            Integer minReplicas,
            Integer maxReplicas,
            Integer workersPerCore,
            Object safetyFilterOptions,
            String userId,
            String projectName);
 
    /**
     * Agent 배포 Policy 설정
     * 
     * @param appId App ID
     * @param request Policy 요청 데이터
     * @return 설정된 Policy 목록
     */
    List<PolicyRequest> setAgentDeployPolicy(String appId, String memberId, String projectName);
}
