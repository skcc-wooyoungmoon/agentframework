package com.skax.aiplatform.service.deploy.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.elastic.search.dto.request.SearchRequest;
import com.skax.aiplatform.client.elastic.search.dto.response.SearchResponse;
import com.skax.aiplatform.client.elastic.search.service.ElasticSearchService;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppApiKeyCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeyCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeysResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentAppsService;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGraphsService;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamRequest;
import com.skax.aiplatform.client.sktai.agentgateway.service.SktaiAgentGatewayService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.auth.service.SktaiProjectService;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskResourceResponse;
import com.skax.aiplatform.client.sktai.resource.service.SktaiResourceService;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.deploy.request.AgentSysLogSearchReq;
import com.skax.aiplatform.dto.deploy.request.AppCreateReq;
import com.skax.aiplatform.dto.deploy.request.AppUpdateReq;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.request.StreamReq;
import com.skax.aiplatform.dto.deploy.response.AgentAppRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployInfoRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployUpdateOrDeleteRes;
import com.skax.aiplatform.dto.deploy.response.AgentServingRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeyCreateRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeysRes;
import com.skax.aiplatform.dto.deploy.response.AppCreateRes;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.mapper.deploy.AgentDeployMapper;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.deploy.GpoMigMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.common.MigService;
import com.skax.aiplatform.service.deploy.AgentDeployService;
import com.skax.aiplatform.service.deploy.ApiGwService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent 배포 관리 서비스
 * 
 * <p>Agent 애플리케이션의 배포, 관리, 모니터링을 담당하는 비즈니스 로직을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-30
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentDeployServiceImpl implements AgentDeployService {

    private final SktaiAgentAppsService sktaiAgentAppsService;
    private final SktaiAgentGraphsService sktaiAgentGraphsService;
    private final SktaiServingService sktaiAgentServingService;
    private final SktaiAgentGatewayService sktaiAgentGatewayService;
    private final SktaiResourceService sktaiResourceService;
    private final SktaiProjectService sktaiProjectService;
    private final SktaiAuthService sktaiAuthService;
    private final AdminAuthService adminAuthService;
    private final ObjectMapper objectMapper;

    private final AgentDeployMapper agentDeployMapper;
    private final ElasticSearchService elasticSearchService;
    private final SktaiLineageService sktaiLineageService;
    private final ApiGwService apiGwService;
    private final MigService migService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final ProjectMgmtRepository projectMgmtRepository;
    private final GpoMigMasRepository gpoMigMasRepository;

    /**
     * Agent App(배포) 목록 조회
     * 
     * @param targetType 타겟 타입
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터
     * @param search 검색어
     * @return 배포 목록
     */
    @Override
    public PageResponse<AgentAppRes> getAgentAppList(String targetType, Integer page, Integer size, String sort, String filter, String search) {
        try {
            AppsResponse response = sktaiAgentAppsService.getApps(targetType, page, size, sort, filter, search);
            
            // Mapper를 통해 AppsResponse를 AgentAppRes 리스트로 변환
            List<AgentAppRes> appList = agentDeployMapper.toDeployResListFromApps(response.getData());
            
            // 각 앱의 targetId로 그래프 정보를 조회하여 builderName 설정 및 마이그레이션 여부 확인
            for (AgentAppRes app : appList) {
                // builderName 설정
                if (app.getTargetId() != null && !app.getTargetId().isEmpty()) {
                    try {
                        log.debug("Graph 정보 조회 시작 - targetId: '{}'", app.getTargetId());
                        GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(app.getTargetId());
                        if (graphResponse != null && graphResponse.getName() != null) {
                            app.setBuilderName(graphResponse.getName());
                            log.debug("Graph 정보 조회 완료 - targetId: '{}', builderName: '{}'", app.getTargetId(), graphResponse.getName());
                        } else {
                            log.debug("Graph 정보가 null이거나 name이 null - targetId: '{}'", app.getTargetId());
                            app.setBuilderName("");
                        }
                    } catch (FeignException e) {
                        log.debug("Graph 정보 조회 실패 (FeignException) - targetId: '{}', error: {}", app.getTargetId(), e.getMessage());
                        app.setBuilderName("");
                    } catch (Exception e) {
                        log.warn("Graph 정보 조회 실패 - targetId: '{}', error: {}", app.getTargetId(), e.getMessage());
                        app.setBuilderName("");
                    }
                } else {
                    log.debug("Graph 정보 조회 스킵 - targetId가 null 또는 비어있음");
                    app.setBuilderName("");
                }
                
                // 마이그레이션 여부 확인 (app의 id로 조회)
                if (app.getId() != null && !app.getId().isEmpty()) {
                    try {
                        log.debug("마이그레이션 여부 조회 시작 - appId: '{}', appName: '{}'", app.getId(), app.getName() != null ? app.getName() : "null");
                        boolean isActive = migService.isActive(app.getId());
                        app.setIsMigration(isActive);
                        log.debug("마이그레이션 여부 조회 완료 - appId: '{}', isMigration: {}", app.getId(), isActive);
                    } catch (RuntimeException e) {
                        log.warn("마이그레이션 여부 조회 실패 - appId: '{}', error: {}", app.getId(), e.getMessage());
                        app.setIsMigration(false);
                    }
                } else {
                    log.debug("마이그레이션 여부 조회 스킵 - appId가 null 또는 비어있음");
                    app.setIsMigration(false);
                }

                // 공개 여부 설정 (lst_prj_seq 값에 따라)
                GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/agent/agents/apps/" + app.getId()).orElse(null);
                String publicStatus = null;
                if (existing != null && existing.getLstPrjSeq() != null) {
                    // 음수면 "전체공유", 양수면 "내부공유"
                    publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                } else {
                    publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
                }
                app.setPublicStatus(publicStatus);
            }

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), appList);

        } catch (FeignException e) {
            throw new RuntimeException("Agent Apps 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    /**
     * Agent App 상세 조회
     * 
     * @param appId 앱 ID
     * @return 앱 배포 상세 정보
     */
    @Override
    public AgentAppRes getAgentAppById(String appId) {
        try {
            log.debug("Agent App 배포 상세 조회 요청 - appId: {}", appId);
            
            // SKTAI API를 호출하여 앱 상세 정보 조회
            AppResponse appResponse = sktaiAgentAppsService.getApp(appId);
            log.debug("getApp API 응답: {}", appResponse);
            
            if (appResponse == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "앱 정보를 조회할 수 없습니다.");
            }

            // 외부 API 응답 확인 로그
            log.debug("외부 API 응답 - createdBy: {}, updatedBy: {}", 
                    appResponse.getCreatedBy(), 
                    appResponse.getUpdatedBy());

            // MapStruct 매퍼 사용
            AgentAppRes deployRes = agentDeployMapper.toDeployResFromAppsData(appResponse);

            // targetId로 그래프 정보를 조회하여 builderName 설정
            if (deployRes.getTargetId() != null && !deployRes.getTargetId().isEmpty()) {
                try {
                    log.debug("Graph 정보 조회 시작 - targetId: '{}'", deployRes.getTargetId());
                    GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(deployRes.getTargetId());
                    if (graphResponse != null && graphResponse.getName() != null) {
                        deployRes.setBuilderName(graphResponse.getName());
                        log.debug("Graph 정보 조회 완료 - targetId: '{}', builderName: '{}'", deployRes.getTargetId(), graphResponse.getName());
                    } else {
                        log.debug("Graph 정보가 null이거나 name이 null - targetId: '{}'", deployRes.getTargetId());
                        deployRes.setBuilderName("");
                    }
                } catch (FeignException e) {
                    log.debug("Graph 정보 조회 실패 (FeignException) - targetId: '{}', error: {}", deployRes.getTargetId(), e.getMessage());
                    deployRes.setBuilderName("");
                } catch (Exception e) {
                    log.warn("Graph 정보 조회 실패 - targetId: '{}', error: {}", deployRes.getTargetId(), e.getMessage());
                    deployRes.setBuilderName("");
                }
            } else {
                log.warn("targetId가 null이거나 비어있음 - appId: {}", deployRes.getId());
                deployRes.setBuilderName("");
            }

            // 마이그레이션 여부 확인 (app의 id로 조회)
            if (deployRes.getId() != null && !deployRes.getId().isEmpty()) {
                try {
                    log.debug("마이그레이션 여부 조회 시작 - appId: '{}', appName: '{}'", deployRes.getId(), deployRes.getName() != null ? deployRes.getName() : "null");
                    boolean isActive = migService.isActive(deployRes.getId());
                    deployRes.setIsMigration(isActive);
                    log.debug("마이그레이션 여부 조회 완료 - appId: '{}', isMigration: {}", deployRes.getId(), isActive);
                } catch (RuntimeException e) {
                    log.warn("마이그레이션 여부 조회 실패 - appId: '{}', error: {}", deployRes.getId(), e.getMessage());
                    deployRes.setIsMigration(false);
                }
            } else {
                log.debug("마이그레이션 여부 조회 스킵 - appId가 null 또는 비어있음");
                deployRes.setIsMigration(false);
            }

            return deployRes;
        } catch (FeignException e) {
            log.error("Agent App 배포 상세 조회 실패 (FeignException) - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "앱 배포 상세 조회에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Agent App 배포 상세 조회 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "앱 배포 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent App별 배포 목록 조회
     * 
     * @param appId 앱 ID
     * @return 배포 목록 (페이징 포함)
     */
    @Override
    public PageResponse<AgentDeployRes> getAgentAppDeployListById(String appId, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Agent App별 배포 목록 조회 요청 - appId: {}", appId);
            
            // SKTAI API를 호출하여 실제 앱 목록을 조회
            AppDeploymentsResponse response = sktaiAgentAppsService.getAppDeployments(appId, page, size, sort, filter, search);
            log.debug("getAppDeployments API 응답: {}", response);

            // Mapper를 통해 AppDeploymentsResponse를 AgentDeployRes 리스트로 변환
            List<AgentDeployRes> deployResList = agentDeployMapper.toDeployResListFromAppDeployments(response);
            
            // 각 배포에 대해 운영 이행 활성 여부 설정
            deployResList.forEach(deploy -> {
                boolean isMigration = gpoMigMasRepository.findByPgmDescCtnt(deploy.getId()).isPresent();
                deploy.setIsMigration(isMigration);
            });
            
            log.debug("매핑된 배포 목록: {}", deployResList);

            // ADXP Pagination을 PageResponse로 변환
            PageResponse<AgentDeployRes> result = PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), deployResList);
            log.debug("Agent App별 배포 목록 조회 성공 - contentSize={}, totalElements={}, totalPages={}, hasNext={}", 
                    result.getContent().size(), result.getTotalElements(), result.getTotalPages());
            
            return result;

        } catch (FeignException e) {
            log.error("Agent App별 배포 목록 조회 실패: appId={}, 에러={}", appId, e.getMessage());
            log.debug("Agent App별 배포 목록 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Agent App별 배포 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    /**
     * Agent App내 배포별 상세 조회
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public AgentDeployRes getAgentAppDeployById(String deploymentId) {
        try {
            log.debug("Agent App내 배포별 상세 조회 요청 - deploymentId: {}", deploymentId);
            
            AppDeploymentResponse response = sktaiAgentAppsService.getDeployment(deploymentId);
            log.debug("API 응답 받음: {}", response);
            
            // MapStruct 매퍼 사용 (수정된 toDeployRes 메서드)
            AgentDeployRes deployRes = agentDeployMapper.toDeployRes(response);
            
            log.debug("MapStruct 매핑 결과: {}", deployRes);
            
            log.debug("Agent App내 배포별 상세 조회 성공 - deploymentId: {}", deploymentId);
            return deployRes;
            
        } catch (FeignException e) {
            log.error("Agent App내 배포별 상세 조회 실패 - deploymentId: {}", deploymentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "배포별 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 새로운 Agent App 배포 생성
     * 
     * @param request 배포 생성 요청 데이터
     * @return 생성된 배포 정보
     */
    @Override
    public AppCreateRes createAgentApp(AppCreateReq request) {
        try {
            AppCreateRequest appCreateRequest = agentDeployMapper.toAppCreateReq(request);

            List<PolicyRequest> policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
            
            // policy 필드 추가
            if (policyRequests != null && !policyRequests.isEmpty()) {
                appCreateRequest.setPolicy(policyRequests);
                log.debug("Agent App 생성 요청에 policy 추가: policyRequests={}", policyRequests);
            }

            AppCreateResponse response = sktaiAgentAppsService.createApp(appCreateRequest);

            // ADXP 권한부여
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/" + response.getData().getAppId());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/" + response.getData().getAppId() + "/deployments");
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent_servings/" + response.getData().getAgentServingId());
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/deployments/" + response.getData().getDeploymentId()); 

            AppCreateRes appRes = agentDeployMapper.toAppCreateRes(response);

            // AppCreateRes가 성공 응답이고 appId가 존재할 때만 lineage 생성
            if (appRes != null && appRes.getAppId() != null &&
                 request.getTargetType().equals("agent_graph")) {

                // Lineage 생성
                try {
                    LineageCreate.LineageItem lineageItem = LineageCreate.LineageItem.builder()
                        .sourceKey(appRes.getAppId())
                        .sourceType(ObjectType.AGENT_APP)
                        .targetKey(request.getTargetId())
                        .targetType(ObjectType.AGENT_GRAPH)
                        .action(ActionType.USE)
                        .build();
                    
                    LineageCreate lineageCreate = LineageCreate.builder()
                        .lineages(List.of(lineageItem))
                        .build();
                    
                    sktaiLineageService.createLineage(lineageCreate);
                    log.info("Lineage 생성 완료 - sourceKey: {}, targetKey: {}", appRes.getAppId(), request.getTargetId());
                } catch (FeignException e) {
                    log.warn("Lineage 생성 실패 (배포는 계속 진행) - sourceKey: {}, targetKey: {}, error: {}", 
                            appRes.getAppId(), request.getTargetId(), e.getMessage());
                } catch (Exception e) {
                    log.warn("Lineage 생성 실패 (배포는 계속 진행) - sourceKey: {}, targetKey: {}, error: {}", 
                            appRes.getAppId(), request.getTargetId(), e.getMessage());
                }

                // API GW 엔드포인트 생성
                try {
                    CreateApiReq createApiReq = CreateApiReq.builder()
                            .type("agent")
                            .uuid(appRes.getAppId())
                            .name(request.getName())
                            .description(request.getDescription()).build();

                    CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
                    log.info("API GW 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());
                } catch (FeignException e) {
                    log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                            appRes.getAppId(), e.getMessage());
                } catch (Exception e) {
                    log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                            appRes.getAppId(), e.getMessage());
                }
            }

            return appRes; 
            
        } catch (FeignException e) {
            log.error("Agent App 배포 생성 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "배포 생성에 실패했습니다: " + e.getMessage());
        }
    }

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
     * @return 생성된 앱 정보
     */
    @Override
    public AppCreateRes createCustomAgentApp(
            MultipartFile envFile,
            String name,
            String description,
            String versionDescription,
            String targetType,
            java.util.List<String> modelList,
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
            String projectName) {
        try {
            // targetType이 null이면 "external_graph"로 기본값 설정
            String finalTargetType = (targetType == null || targetType.trim().isEmpty()) 
                    ? "external_graph" 
                    : targetType;

            // projectName이 있으면 memberId와 projectName으로 정책 조회, 없으면 기존 로직 사용
            log.info("createCustomAgentApp - userId: [{}], projectName: [{}]", userId, projectName);
            List<PolicyRequest> policyRequests;
            if (projectName != null && !projectName.trim().isEmpty()) {
                log.info("createCustomAgentApp - getPolicyRequestsByMemberIdAndProjectName 호출: memberId=[{}], projectName=[{}]", userId, projectName);
                policyRequests = adminAuthService.getPolicyRequestsByMemberIdAndProjectName(userId, projectName);
            } else if (userId != null && !userId.trim().isEmpty()) {
                log.info("createCustomAgentApp - getPolicyRequestsByUserId 호출: userId=[{}]", userId);
                policyRequests = adminAuthService.getPolicyRequestsByUserId(userId);
            } else {
                log.info("createCustomAgentApp - getPolicyRequestsByCurrentGroup 호출 (userId와 projectName이 없음)");
                policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
            }
            log.info("createCustomAgentApp - policyRequests 조회 완료: size={}", policyRequests != null ? policyRequests.size() : 0);
        
            
            AppCreateResponse response = null;
            String appId = null;
            String agentServingId = null;
            String deploymentId = null;
            
            try {
                response = sktaiAgentAppsService.createCustomApp(
                    envFile,
                    name,
                    description,
                    versionDescription,
                    finalTargetType,
                    modelList,
                    imageUrl,
                    useExternalRegistry,
                    cpuRequest,
                    cpuLimit,
                    memRequest,
                    memLimit,
                    minReplicas,
                    maxReplicas,
                    workersPerCore,
                    safetyFilterOptions,
                    policyRequests
                );
                
                // response에서 appId 추출
                if (response != null && response.getData() != null) {
                    appId = response.getData().getAppId();
                    agentServingId = response.getData().getAgentServingId();
                    deploymentId = response.getData().getDeploymentId();
                }
            } catch (RuntimeException e) {
                log.warn("createCustomAgentApp - API 호출 실패, 정책 설정은 계속 진행: error={}", e.getMessage());
                // API 호출이 실패해도 정책 설정은 시도
            }

            Long projectSeq = null; // 프로젝트 시퀀스
            // ADXP 권한부여 (API 호출 성공/실패와 관계없이 appId가 있으면 정책 설정)
            if (appId != null && !appId.trim().isEmpty()) {
                // projectName이 있으면 해당 프로젝트 시퀀스로 정책 설정, 없으면 현재 사용자 기준
                if (projectName != null && !projectName.trim().isEmpty()) {
                    try {
                        Project project = projectMgmtRepository.findByPrjNm(projectName)
                                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                                        "프로젝트를 찾을 수 없습니다: " + projectName));
                        projectSeq = project.getPrjSeq();
                        log.info("createCustomAgentApp - 프로젝트 시퀀스 조회 완료: projectName=[{}], prjSeq={}", projectName, projectSeq);
                    } catch (RuntimeException e) {
                        log.warn("createCustomAgentApp - 프로젝트 시퀀스 조회 실패, 현재 사용자 기준으로 설정: projectName=[{}], error={}", 
                                projectName, e.getMessage());
                    }
                }
                
                log.info("createCustomAgentApp - projectSeq: [{}], appId: [{}]", projectSeq, appId);
                try {
                    if (projectSeq != null) {
                        // 프로젝트 시퀀스로 정책 설정
                        adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + appId, projectSeq);
                        // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + appId + "/deployments", projectSeq);
                        
                        if (agentServingId != null && !agentServingId.trim().isEmpty()) {
                            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent_servings/" + agentServingId, projectSeq);
                        }

                        if (deploymentId != null && !deploymentId.trim().isEmpty()) {
                            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/deployments/" + deploymentId, projectSeq);
                        }
                    } else {
                        // 현재 사용자 기준으로 정책 설정
                        adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/" + appId);
                        // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/" + appId + "/deployments");    
                        
                        if (agentServingId != null && !agentServingId.trim().isEmpty()) {
                            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent_servings/" + agentServingId);
                        }

                        if (deploymentId != null && !deploymentId.trim().isEmpty()) {
                            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/apps/deployments/" + deploymentId);
                        }
                    }
                    log.info("createCustomAgentApp - 정책 설정 완료: appId=[{}], projectSeq={}", appId, projectSeq);
                } catch (RuntimeException e) {
                    log.error("createCustomAgentApp - 정책 설정 실패 (계속 진행): appId=[{}], error={}", appId, e.getMessage(), e);
                }
            } else {
                log.warn("createCustomAgentApp - appId가 없어 정책 설정을 건너뜀: response={}", response);
            }
            
            // response가 null이면 예외 발생
            if (response == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "커스텀 앱 생성에 실패했습니다: API 응답이 null입니다.");
            }
            
            AppCreateRes appRes = agentDeployMapper.toAppCreateRes(response);
            
            // API GW 엔드포인트 생성
            if (appRes != null && appRes.getAppId() != null) {
                try {
                    // /custom 넣게 param 넣기?
                    CreateApiReq createApiReq = CreateApiReq.builder()
                            .type("agent")
                            .uuid(appRes.getAppId())
                            .name(appRes.getDeploymentName() != null ? appRes.getDeploymentName() : appRes.getAppId())
                            .description(appRes.getDescription())
                            .projectId(projectSeq.toString())
                            .build();
                    
                    CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
                    log.info("API GW 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());
                } catch (FeignException e) {
                    log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                            appRes.getAppId(), e.getMessage());
                } catch (Exception e) {
                    log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                            appRes.getAppId(), e.getMessage());
                }
            }
            
            log.debug("커스텀 Agent App 생성 성공 - appId: {}", appRes != null ? appRes.getAppId() : "null");
            return appRes;
            
        } catch (FeignException e) {
            log.error("커스텀 Agent App 생성 실패 - name: {}", name, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "커스텀 앱 생성에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("커스텀 Agent App 생성 실패 (예상치 못한 오류) - name: {}", name, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "커스텀 앱 생성에 실패했습니다: " + e.getMessage());
        }
    }

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
     * @return 생성된 배포 정보
     */
    @Override
    public AgentDeployRes addCustomDeploymentWithMultipart(
            String appId,
            MultipartFile envFile,
            String name,
            String description,
            String versionDescription,
            String targetType,
            java.util.List<String> modelList,
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
            String projectName) {
        try {
            log.debug("커스텀 배포 추가 요청 (Multipart) - appId: {}, name: {}", appId, name);
            
            // projectName이 있으면 memberId와 projectName으로 정책 조회, 없으면 기존 로직 사용
            log.info("addCustomDeploymentWithMultipart - userId: [{}], projectName: [{}]", userId, projectName);
            List<PolicyRequest> policyRequests;
            if (projectName != null && !projectName.trim().isEmpty()) {
                log.info("addCustomDeploymentWithMultipart - getPolicyRequestsByMemberIdAndProjectName 호출: memberId=[{}], projectName=[{}]", userId, projectName);
                policyRequests = adminAuthService.getPolicyRequestsByMemberIdAndProjectName(userId, projectName);
            } else if (userId != null && !userId.trim().isEmpty()) {
                log.info("addCustomDeploymentWithMultipart - getPolicyRequestsByUserId 호출: userId=[{}]", userId);
                policyRequests = adminAuthService.getPolicyRequestsByUserId(userId);
            } else {
                log.info("addCustomDeploymentWithMultipart - getPolicyRequestsByCurrentGroup 호출 (userId와 projectName이 없음)");
                policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
            }
            log.debug("커스텀 배포용 PolicyRequest 개수 - appId: {}, userId: {}, projectName: {}, size: {}",
                    appId, userId, projectName, policyRequests != null ? policyRequests.size() : 0);
            
            AppDeploymentResponse response = sktaiAgentAppsService.addCustomDeploymentWithMultipart(
                appId,
                envFile,
                name,
                description,
                versionDescription,
                targetType,
                modelList,
                imageUrl,
                useExternalRegistry,
                cpuRequest,
                cpuLimit,
                memRequest,
                memLimit,
                minReplicas,
                maxReplicas,
                workersPerCore,
                safetyFilterOptions,
                policyRequests
            );
            
            // MapStruct 매퍼 사용
            AgentDeployRes deployRes = agentDeployMapper.toDeployRes(response);

            // projectName이 있으면 해당 프로젝트 시퀀스로 정책 설정, 없으면 현재 사용자 기준
            Long projectSeq = null;
            if (projectName != null && !projectName.trim().isEmpty()) {
                try {
                    Project project = projectMgmtRepository.findByPrjNm(projectName)
                            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                                    "프로젝트를 찾을 수 없습니다: " + projectName));
                    projectSeq = project.getPrjSeq();
                    log.info("addCustomDeploymentWithMultipart - 프로젝트 시퀀스 조회 완료: projectName=[{}], prjSeq={}", projectName, projectSeq);
                } catch (RuntimeException e) {
                    log.warn("addCustomDeploymentWithMultipart - 프로젝트 시퀀스 조회 실패, 현재 사용자 기준으로 설정: projectName=[{}], error={}", 
                            projectName, e.getMessage());
                }
            }
            
            if (projectSeq != null) {
                // 프로젝트 시퀀스로 정책 설정
                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent_servings/" + deployRes.getServingId(), projectSeq);
            } else {
                // 현재 사용자 기준으로 정책 설정
                adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent_servings/" + deployRes.getServingId());
            }
            
            log.debug("커스텀 배포 추가 성공 - appId: {}, deploymentId: {}", 
                    appId, deployRes != null ? deployRes.getId() : "null");
            return deployRes;
            
        } catch (FeignException e) {
            log.error("커스텀 배포 추가 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "커스텀 배포 추가에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("커스텀 배포 추가 실패 (예상치 못한 오류) - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "커스텀 배포 추가에 실패했습니다: " + e.getMessage());
        }
    }

    /** 
     * Agent 앱 삭제
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public void deleteAgentApp(String appId) {
        try {
            log.debug("Agent App내 배포 삭제 요청 - appId: {}", appId);
            
            sktaiAgentAppsService.deleteApp(appId);
            sktaiLineageService.deleteLineage(appId);
            apiGwService.deleteApiEndpoint("agent", appId);

        } catch (FeignException e) {
            log.error("Agent App내 배포 삭제 요청 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent App내 배포 삭제에 실패했습 니다: " + e.getMessage());
        }
    }

    /** 
     * Agent 앱 삭제
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public void updateAgentApp(String appId, AppUpdateReq request) {
        try {
            log.debug("Agent App내 배포 수정 요청 - appId: {}", appId);
            
            AppUpdateRequest appUpdateRequest = agentDeployMapper.toAppUpdateReq(request);

            sktaiAgentAppsService.updateApp(appId, appUpdateRequest);
            
        } catch (FeignException e) {
            log.error("Agent App내 배포 삭제 요청 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent App내 배포 수정에 실패했습 니다: " + e.getMessage());
        }
    }

    /** 
     * Agent 배포 삭제
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public void deleteAgentAppDeploy(String deployId) {
        try {
            log.debug("Agent App내 배포 삭제 요청 - deployId: {}", deployId);
            
            sktaiAgentAppsService.deleteDeployment(deployId);
            
        } catch (FeignException e) {
            log.error("Agent App내 배포 삭제 요청 실패 - deploymentId: {}", deployId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent App내 배포 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent 배포 중지
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public AgentDeployUpdateOrDeleteRes stopAgentDeploy(String deployId) {
        try {
            log.debug("Agent 배포 중지 요청 - deploymentId: {}", deployId);
            
            AppUpdateOrDeleteResponse response = sktaiAgentAppsService.stopDeployment(deployId);
            log.debug("API 응답 받음: {}", response);

            AgentDeployUpdateOrDeleteRes deployRes = agentDeployMapper.toDeployResFromAppUpdateOrDeleteResponse(response);
            
            return deployRes;
            
        } catch (FeignException e) {
            log.error(" Agent 배포 중지 실패 - deploymentId: {}", deployId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, " Agent 배포 중지에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent 배포 재시작
     * 
     * @param deploymentId 배포 ID
     * @return 배포별 상세 정보
     */
    @Override
    public AgentDeployUpdateOrDeleteRes restartAgentDeploy(String deployId) {
        try {
            log.debug("Agent 배포 재시작 요청 - deployId: {}", deployId);
            
            AppUpdateOrDeleteResponse response = sktaiAgentAppsService.restartDeployment(deployId);
            log.debug("API 응답 받음: {}", response);

            AgentDeployUpdateOrDeleteRes deployRes = agentDeployMapper.toDeployResFromAppUpdateOrDeleteResponse(response);
            
            return deployRes;
            
        } catch (FeignException e) {
            log.error("Agent 배포 재시작 실패 - deployId: {}", deployId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 배포 재시작에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public AgentServingRes getAgentServing(String agentServingId) {
        try {
            AgentServingResponse response = sktaiAgentServingService.getAgentServing(agentServingId);

            AgentServingRes agentServingRes = agentDeployMapper.toAgentServingRes(response);

            // 에이전트 운영 배포 여부 조회
            boolean isActive = migService.isActive(agentServingRes.getAppId());
            agentServingRes.setIsMigration(isActive);
            
            return agentServingRes;
        } catch (FeignException e) {
            log.error("Agent 서빙 상세 조회 실패 - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 서빙 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public AppApiKeysRes getAgentAppApiKeyListById(String appId) {
        try {
            log.debug("Agent App API 키 목록 조회 요청 - appId: {}", appId);
            
            AppApiKeysResponse response = sktaiAgentAppsService.getAppApiKeys(appId);
            log.debug("API 응답 받음: {}", response);

            AppApiKeysRes appApiKeysRes = agentDeployMapper.toAppApiKeysRes(response);
            
            return appApiKeysRes;
        } catch (FeignException e) {
            log.error("Agent App API 키 목록 조회 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent App API 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public String getStreamAgentRaw(String agentId, String routerPath, StreamReq request, String authorization) {
        authorization = "Bearer " + authorization;
        try {
            // 배포 정보에서 graph_id 추출하여 그래프 업데이트
            AgentAppRes appInfo = getAgentAppById(agentId);
            String graphId = appInfo.getTargetId();
            
            if (graphId != null && !graphId.trim().isEmpty()) {
                
                // 그래프 데이터를 직접 조회
                GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(graphId);
                if (graphResponse != null && graphResponse.getNodes() != null) {
                    boolean hasChanges = false;
                    List<Object> nodeObjects = graphResponse.getNodes();
                    
                    for (Object nodeObj : nodeObjects) {
                        if (nodeObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> node = (Map<String, Object>) nodeObj;
                            String nodeType = (String) node.get("type");
                            
                            if ("agent__generator".equals(nodeType)) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> nodeData = (Map<String, Object>) node.get("data");
                                if (nodeData != null) {
                                    Object guardrailsPrompt = nodeData.get("guardrails_prompt");
                                    
                                    if (guardrailsPrompt instanceof Map) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> promptMap = (Map<String, Object>) guardrailsPrompt;
                                        
                                        // messages와 variables를 빈 배열로 초기화
                                        if (promptMap.get("messages") == null) {
                                            promptMap.put("messages", new java.util.ArrayList<>());
                                            hasChanges = true;
                                        }
                                        if (promptMap.get("variables") == null) {
                                            promptMap.put("variables", new java.util.ArrayList<>());
                                            hasChanges = true;
                                        }
                                    } else if (guardrailsPrompt == null) {
                                        // Few-shot이 없으면 null로 두고 넘어감 (모델만으로 진행)
                                        log.info("✅ 노드 {}에 Few-shot이 없음 - 모델만으로 진행", node.get("id"));
                                    }
                                }
                            }
                        }
                    }
                    
                    // 변경사항이 있으면 그래프 업데이트
                    if (hasChanges) {
                        try {
                            @SuppressWarnings("unchecked")
                            com.skax.aiplatform.client.sktai.agent.dto.request.GraphUpdateRequest updateRequest = 
                                com.skax.aiplatform.client.sktai.agent.dto.request.GraphUpdateRequest.builder()
                                    .nodes((List<Object>) (List<?>) nodeObjects)
                                    .edges((List<Object>) (List<?>) graphResponse.getEdges())
                                    .build();
                            
                            sktaiAgentGraphsService.updateGraph(graphId, updateRequest);

                        } catch (FeignException updateEx) {
                            log.warn("⚠️ 그래프 업데이트 실패 (계속 진행): {}", updateEx.getMessage());
                        }
                    } else {
                        log.info("✅ 변경사항 없음 - 그래프 업데이트 건너뜀");
                    }
                }
            }

            StreamRequest req = agentDeployMapper.toStreamReq(request);

            // Raw SSE 응답을 그대로 반환
            String rawResponse = sktaiAgentGatewayService.streamAgentRaw(authorization, agentId, req, routerPath);
            
            return rawResponse;
        } catch (FeignException e) {
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Agent App 스트리밍에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getClusterResources(String nodeType) {
        try {
            // projectId 가져오기
            
            // TaskResourceResponse 조회
            TaskResourceResponse taskResourceResponse = sktaiResourceService.getClusterResources(nodeType, "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5");
            log.debug("Agent 클러스터 리소스 조회 성공 - nodeType: {}, projectId: {}", nodeType);
            
            // TaskResourceResponse를 Map<String, Object>로 변환
            @SuppressWarnings("unchecked")
            Map<String, Object> response = objectMapper.convertValue(taskResourceResponse, Map.class);
            log.debug("Agent 클러스터 리소스 응답 변환 완료: {}", response);  

            return response;
        } catch (FeignException e) {
            log.error("Agent 클러스터 리소스 조회 실패 - nodeType: {}", nodeType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 클러스터 리소스 조회에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Agent 클러스터 리소스 조회 실패 (예상치 못한 오류) - nodeType: {}", nodeType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 클러스터 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public String getAgentSysLog(String index, AgentSysLogSearchReq request) {
        try {
            SearchRequest searchRequest = agentDeployMapper.toSearchRequest(request);

            SearchResponse response = elasticSearchService.searchWithDsl(index, searchRequest);

            String result = agentDeployMapper.toSearchResponse(response);
            
            return result;
        } catch (FeignException e) {
            // 인덱스가 존재하지 않는 경우 빈 결과 반환
            if (e.getMessage() != null && e.getMessage().contains("no such index")) {
                return "";
            }
            
            // 기타 오류는 그대로 예외 발생
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 시스템 로그 검색에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public AgentDeployInfoRes getAgentDeployInfo(String agentId) {
        try {
            log.debug("Agent 배포 정보 조회 요청 - agentId: {}", agentId);

            // 1. Agent ID로 배포 목록 조회
            PageResponse<AgentDeployRes> deployList = 
                getAgentAppDeployListById(agentId, 1, 1000, null, null, null);
            
            if (deployList.getContent().isEmpty()) {
                log.debug("Agent ID에 해당하는 배포가 없습니다: {}", agentId);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Agent ID에 해당하는 배포를 찾을 수 없습니다: " + agentId);
            }
            
            // 2. status가 "Available"인 배포 중 가장 높은 버전 선택
            AgentDeployRes activeDeploy = deployList.getContent().stream()
                .filter(deploy -> "Available".equals(deploy.getStatus()))
                .filter(deploy -> deploy.getVersion() != null)
                .sorted(java.util.Comparator.comparing(AgentDeployRes::getVersion).reversed())
                .findFirst()
                .orElse(null);
            
            if (activeDeploy == null) {
                // 디버깅을 위해 모든 배포 상태를 로그로 출력
                String allStatuses = deployList.getContent().stream()
                    .map(deploy -> deploy.getId() + ":" + deploy.getStatus())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("없음");
                log.debug("Available 상태의 배포를 찾을 수 없습니다: {} (사용 가능한 배포 상태: {})", agentId, allStatuses);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Available 상태의 배포를 찾을 수 없습니다: " + agentId + 
                    " (사용 가능한 배포 상태: " + allStatuses + ")");
            }
            
            // 3. 배포 상세 조회
            AgentDeployRes deployDetail = getAgentAppDeployById(activeDeploy.getId());
            
            // 4. servingId로 서빙 정보 조회
            if (deployDetail.getServingId() == null || deployDetail.getServingId().isEmpty()) {
                log.debug("서빙 ID가 없습니다: {}", deployDetail.getId());
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "서빙 ID가 없습니다: " + deployDetail.getServingId());
            }   
            
            AgentServingRes servingInfo = getAgentServing(deployDetail.getServingId());
            
            // 5. namespace와 isvcName 추출
            String namespace = servingInfo.getNamespace();
            String isvcName = servingInfo.getIsvcName();
            
            if (namespace == null || namespace.isEmpty()) {
                log.debug("namespace 정보가 없습니다: {}", deployDetail.getServingId());
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "namespace 정보가 없습니다: " + deployDetail.getServingId());
            }
            
            if (isvcName == null || isvcName.isEmpty()) {
                log.debug("isvcName 정보가 없습니다: {}", deployDetail.getServingId());
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "isvcName 정보가 없습니다: " + deployDetail.getServingId());
            }
            
            // isvcName의 처음 12자만 사용
            String shortIsvcName = isvcName.length() > 12 ? isvcName.substring(0, 12) : isvcName;
            
            // AgentDeployInfoRes 객체로 반환
            AgentDeployInfoRes result = AgentDeployInfoRes.builder()
                .agentId(agentId)
                .namespace(namespace)
                .isvcName(shortIsvcName)
                .status(deployDetail.getStatus())
                .deployId(deployDetail.getId())
                .deployDt(deployDetail.getDeployedDt())
                .build();

            log.debug("Agent 배포 정보 조회 완료 - agentId: {}", agentId);
            return result;
            
        } catch (FeignException e) {
            log.error("Agent 배포 정보 조회 실패 - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent 배포 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public AppApiKeyCreateRes createAgentAppApiKey(String appId) {
        try {
            log.info("Agent App API 키 생성 요청 - appId: {}", appId);
            
            List<PolicyRequest> policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
            log.info("정책 요청 목록 조회 완료 - policyRequests size: {}", policyRequests != null ? policyRequests.size() : 0);
            
            if (policyRequests == null || policyRequests.isEmpty()) {
                log.warn("정책 요청 목록이 비어있습니다. 빈 배열로 전송합니다.");
            } else {
                log.debug("정책 요청 상세: {}", policyRequests);
            }
            
            AppApiKeyCreateResponse response = sktaiAgentAppsService.createAppApiKey(appId, AppApiKeyCreateRequest.builder()
                    .policy(policyRequests != null ? policyRequests : new java.util.ArrayList<>())
                    .build());
            
            // data 필드에서 실제 데이터 추출
            AppApiKeyCreateResponse.AppApiKeyCreateData data = response.getData();
            if (data == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "API 응답에 데이터가 없습니다.");
            }
            
            // AppApiKeyCreateResponse의 data를 AppApiKeyCreateRes로 직접 매핑
            AppApiKeyCreateRes result = AppApiKeyCreateRes.builder()
                    .apiKey(data.getApiKey())
                    .startedAt(data.getStartedAt())
                    .tag(data.getTag())
                    .isMaster(data.getIsMaster())
                    .isActive(data.getIsActive())
                    .internalKey(data.getInternalKey())
                    .servingId(data.getServingId())
                    .expiresAt(data.getExpiresAt())
                    .createdAt(data.getCreatedAt())
                    .allowedHost(data.getAllowedHost())
                    .projectId(data.getProjectId())
                    .gatewayType(data.getGatewayType())
                    .apiKeyId(data.getApiKeyId())
                    .build();

            return result;
        } catch (FeignException e) {
            log.error("Agent App API 키 발급 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Agent App API 키 발급에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent 배포 Policy 설정
     */
    @Override
    @Transactional
    public List<PolicyRequest> setAgentDeployPolicy(String appId, String memberId, String projectName) {
        log.info("Agent 배포 Policy 설정 요청 - appId: {}, memberId: {}, projectName: {}", appId, memberId, projectName);

        // trainingId 검증
        if (!StringUtils.hasText(appId)) {
            log.error("Agent 배포 Policy 설정 실패 - appId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "Agent App ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("Agent 배포 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("Agent 배포 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // servingId 조회
            AgentDeployRes deployInfo = getAgentAppDeployById(appId);
            String servingId = deployInfo.getServingId();
            String deploymentId = deployInfo.getId();

            // ADXP 권한부여
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/agents/apps/" + appId, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/agents/apps/" + appId + "/deployments", memberId, projectName);
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent_servings/" + servingId, memberId, projectName);
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/agents/apps/deployments/" + deploymentId, memberId, projectName); 

            String resourceUrl = "/api/v1/agent/agents/apps/" + appId;
            log.info("Agent 배포 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("Agent 배포 Policy 조회 결과가 null - appId: {}, resourceUrl: {}", appId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent 배포 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
            }

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("Agent 배포 Policy 설정 완료 - appId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", appId, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("Agent 배포 Policy 설정 실패 (BusinessException) - appId: {}, errorCode: {}", appId, e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Agent 배포 Policy 설정 실패 (RuntimeException) - appId: {}, error: {}", appId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent 배포 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Agent 배포 Policy 설정 실패 (Exception) - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent 배포 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }
}