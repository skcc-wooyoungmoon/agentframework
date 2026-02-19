package com.skax.aiplatform.service.admin.impl;

import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.sktai.resrcMgmt.ResrcMgmtClient;
import com.skax.aiplatform.client.sktai.resrcMgmt.ResrcMgmtGpuClient;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingStatus;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.ResrcMgmtNamespaceEnum;
import com.skax.aiplatform.dto.admin.request.ResrcMgmtQueryEnum;
import com.skax.aiplatform.dto.admin.response.ResrcMgmtSessionResourceInfo;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;
import com.skax.aiplatform.dto.deploy.response.AgentAppRes;
import com.skax.aiplatform.dto.home.response.IdeStatusDto;
import com.skax.aiplatform.dto.model.request.GetModelDeployReq;
import com.skax.aiplatform.dto.model.response.GetModelDeployRes;
import com.skax.aiplatform.dto.model.response.GetModelDeploySessionRes;
import com.skax.aiplatform.repository.home.GpoIdeStatusMasRepository;
import com.skax.aiplatform.repository.home.UserIdeStatusRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.admin.ResrcMgmtService;
import com.skax.aiplatform.service.common.ProjectInfoService;
import com.skax.aiplatform.service.deploy.AgentDeployService;
import com.skax.aiplatform.service.model.ModelDeployService;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 자원 관리 서비스 구현체
 *
 * @author SonMunWoo
 * @version 1.0
 * @since 2025-09-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResrcMgmtServiceImpl implements ResrcMgmtService {

    // Prometheus 쿼리 step 값 (초 단위)
    private static final String PROMETHEUS_STEP = "30";

    @Value("${prometheus.api.base-url}")
    private String prometheusApiBaseUrl;

    @Value("${prometheus.api.gpu-base-url}")
    private String prometheusGpuApiBaseUrl;

    private final Environment environment;

    private final ResrcMgmtClient resrcMgmtClient;
    private final ResrcMgmtGpuClient resrcMgmtGpuClient;

    private final AgentDeployService agentDeployService;
    private final ModelDeployService modelDeployService;
    private final ProjectInfoService projectInfoService;
    private final AdminAuthService adminAuthService;

    private final UserIdeStatusRepository userIdeStatusRepository;
    private final GpoIdeStatusMasRepository gpoIdeStatusMasRepository;

    private final Map<String, String> projectNameCache = new ConcurrentHashMap<>();

    /**
     * 서비스 초기화 - Prometheus 설정 로그 출력
     */
    @PostConstruct
    public void init() {
        log.info("=================================================");
        log.info("Prometheus API URL: {}", prometheusApiBaseUrl);
        log.info("Prometheus GPU API URL: {}", prometheusGpuApiBaseUrl);
        log.info("=================================================");
    }

    /**
     * 활성 프로파일 확인 (prod 또는 나머지)
     *
     * @return 활성 프로파일 (prod이면 "prod", 그 외는 "dev" 반환)
     */
    private String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles == null || activeProfiles.length == 0) {
            log.info("🔍 [프로파일 확인] 활성 프로파일이 없음, 기본값(dev) 사용");
            return "dev"; // 기본값은 dev
        }

        String profilesStr = Arrays.toString(activeProfiles);
        log.info("🔍 [프로파일 확인] 활성 프로파일: {}", profilesStr);

        for (String profile : activeProfiles) {
            // prod 프로파일이 있으면 prod 반환
            if ("prod".equals(profile)) {
                log.info("✅ [프로파일 확인] 운영 환경(prod) - prod 네임스페이스 사용");
                return "prod";
            }
        }

        // prod가 아닌 모든 프로파일(local, elocal, dev, edev, staging 등)은 dev로 처리
        log.info("✅ [프로파일 확인] 개발 환경(dev/elocal/local 등) - dev 네임스페이스 사용");
        return "dev";
    }

    @Override
    public Map<String, Object> getPortalIdeResources(String searchType, String searchValue) {
        log.info("포탈 IDE 자원 현황 조회 시작");
        Map<String, Object> ideData = new HashMap<>();
        try {

            List<Map<String, Object>> ideResources = getIdeResources();

            // searchType에 따라 LIKE 검색으로 필터링
            if (searchValue != null && !searchValue.isEmpty()) {
                if ("userName".equals(searchType)) {
                    // userName일 경우 username으로 LIKE 검색
                    ideResources = ideResources.stream()
                            .filter(ide -> {
                                Object usernameObj = ide.get("username");
                                if (usernameObj == null) {
                                    return false;
                                }
                                String username = String.valueOf(usernameObj);
                                return username.contains(searchValue);
                            })
                            .collect(Collectors.toList());
                } else if ("BankNum".equals(searchType)) {
                    // BankNum일 경우 userId로 LIKE 검색
                    ideResources = ideResources.stream()
                            .filter(ide -> {
                                Object userIdObj = ide.get("userId");
                                if (userIdObj == null) {
                                    return false;
                                }
                                String userId = String.valueOf(userIdObj);
                                return userId.contains(searchValue);
                            })
                            .collect(Collectors.toList());
                } else if ("dwAccountId".equals(searchType)) {
                    // dwAccountId일 경우 dwAccountId일로 LIKE 검색
                    ideResources = ideResources.stream()
                            .filter(ide -> {
                                Object dwAccountIdObj = ide.get("dwAccountId");
                                if (dwAccountIdObj == null) {
                                    return false;
                                }
                                String dwAccountId = String.valueOf(dwAccountIdObj);
                                return dwAccountId.contains(searchValue);
                            })
                            .collect(Collectors.toList());
                }
            }

            ideData.put("components", null); // 프론트와 맞추기위해 기존 서비스 형태와 동일하도록 빈 데이터 추가
            ideData.put("ideResources", ideResources);

        } catch (BusinessException e) {
            log.warn("포탈 IDE 자원 현황 조회 실패 (BusinessException): {}", e.getMessage());
        } catch (FeignException e) {
            log.warn("포탈 IDE 자원 현황 조회 실패 (FeignException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("포탈 IDE 자원 현황 조회 실패 (RuntimeException): {}", e.getMessage());
        }

        return ideData;
    }


    @Override
    @Transactional(readOnly = true, noRollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> getPortalResources() {
        log.info("포탈 자원 현황 조회 시작");

        try {
            Map<String, Object> portalData = new HashMap<>();

            // Agent 자원 데이터 조회 - 파드별 데이터를 합산하여 전체 에이전트 리소스 계산
            Object agentCpuUsageResponse = null;
            Object agentCpuRequestsResponse = null;
            Object agentCpuLimitResponse = null;
            Object agentMemoryUsageResponse = null;
            Object agentMemoryRequestsResponse = null;
            Object agentMemoryLimitResponse = null;

            try {
                // agentCpuUsageResponse =
                // resrcMgmtClient.executeQuery(String.format(ResrcMgmtQueryEnum.PORTAL_AGENT_CPU_USAGE.getQuery(),
                // ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                // ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                // agentCpuRequestsResponse =
                // resrcMgmtClient.executeQuery(String.format(ResrcMgmtQueryEnum.PORTAL_AGENT_CPU_REQUESTS.getQuery(),
                // ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                // ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                // agentMemoryUsageResponse =
                // resrcMgmtClient.executeQuery(String.format(ResrcMgmtQueryEnum.PORTAL_AGENT_MEMORY_USAGE.getQuery(),
                // ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                // ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                // agentMemoryRequestsResponse =
                // resrcMgmtClient.executeQuery(String.format(ResrcMgmtQueryEnum.PORTAL_AGENT_MEMORY_REQUESTS
                // .getQuery(),
                // ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                // ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));

                // 파드별 자원 데이터 조회
                Map<String, Object> agentPodData = getPortalAgentPodResources();

                // 파드별 데이터에서 전체 에이전트 리소스 합산
                Map<String, Double> aggregatedAgentResources = aggregateAgentPodResources(agentPodData);

                // 합산된 값을 Prometheus 응답 형식으로 변환하여 기존 로직과 호환
                agentCpuUsageResponse = createSingleValueResponse(aggregatedAgentResources.get("cpu_usage"));
                agentCpuRequestsResponse = createSingleValueResponse(aggregatedAgentResources.get("cpu_request"));
                agentCpuLimitResponse = createSingleValueResponse(aggregatedAgentResources.get("cpu_limit"));
                agentMemoryUsageResponse = createSingleValueResponse(aggregatedAgentResources.get("memory_usage"));
                agentMemoryRequestsResponse = createSingleValueResponse(aggregatedAgentResources.get("memory_request"));
                agentMemoryLimitResponse = createSingleValueResponse(aggregatedAgentResources.get("memory_limit"));

                log.info(
                        "Agent 자원 데이터 조회 완료 (파드별 데이터 합산) - CPU 사용량: {}, CPU 요청량: {}, CPU 제한량: {}, Memory 사용량: {}, " +
                                "Memory 요청량: {}, Memory 제한량: {}",
                        aggregatedAgentResources.get("cpu_usage"),
                        aggregatedAgentResources.get("cpu_request"),
                        aggregatedAgentResources.get("cpu_limit"),
                        aggregatedAgentResources.get("memory_usage"),
                        aggregatedAgentResources.get("memory_request"),
                        aggregatedAgentResources.get("memory_limit"));
            } catch (BusinessException e) {
                log.warn("Agent 자원 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Agent 자원 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Agent 자원 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // Model Deploy 서비스 호출 - Deploying 상태 모델 조회
            List<GetModelDeploySessionRes> sessionDeployList = new ArrayList<>();

            try {
                GetModelDeployReq modelDeployReq = new GetModelDeployReq();
                modelDeployReq.setPage(0);
                modelDeployReq.setSize(100);
                String statusFilterValues = Arrays.stream(ServingStatus.values())
                        .map(Enum::name)
                        .filter(status -> !ServingStatus.Error.name().equals(status))
                        .filter(status -> !"Destroy".equalsIgnoreCase(status))
                        .collect(Collectors.joining("|"));
                String statusFilter = "status[]:" + statusFilterValues;
                modelDeployReq.setFilter(statusFilter + ",serving_type:self_hosting");
                // 배포가 되고 서빙 타입이 self_hosting인 모델 조회.

                PageResponse<GetModelDeployRes> modelDeployResponse = modelDeployService.getModelDeploy(modelDeployReq);

                long totalDeploys = modelDeployResponse != null ? modelDeployResponse.getTotalElements() : 0L;
                Integer currentPage = (modelDeployResponse != null && modelDeployResponse.getPageable() != null)
                        ? modelDeployResponse.getPageable().getPage()
                        : null;

                log.info("Model Deploy 조회 완료 - 총 {}건, 현재 페이지 {}",
                        totalDeploys,
                        currentPage != null ? currentPage : "알 수 없음");

                // Deploying 상태의 servingId 로그 출력 및 endpoint-info 병렬 조회
                List<GetModelDeployRes> deployList = (modelDeployResponse != null
                        && modelDeployResponse.getContent() != null)
                        ? modelDeployResponse.getContent()
                        : Collections.emptyList();

                // 1. GetModelDeployRes -> GetModelDeploySessionRes 변환
                sessionDeployList = deployList.stream()
                        .map(deploy -> {
                            GetModelDeploySessionRes sessionRes = new GetModelDeploySessionRes();
                            BeanUtils.copyProperties(deploy, sessionRes);
                            return sessionRes;
                        })
                        .collect(Collectors.toList());

                // 2. 모든 Model Deploy 정보 로그 출력
                for (GetModelDeploySessionRes modelDeploy : sessionDeployList) {
                    log.info("Deploying 상태 Model - servingId: {}, name: {}, status: {}",
                            modelDeploy.getServingId(),
                            modelDeploy.getName(),
                            modelDeploy.getStatus());
                }

                // 3. endpoint-info를 병렬로 조회
                log.info("Endpoint 정보 병렬 조회 시작 - 대상 {}건", sessionDeployList.size());
                long startTime = System.currentTimeMillis();

                List<CompletableFuture<Void>> futures = sessionDeployList.stream()
                        .map(modelDeploy -> CompletableFuture.runAsync(() -> {
                            try {
                                GetEndpointResponse endpointInfo = modelDeployService
                                        .getEndpointInfoById(modelDeploy.getServingId());

                                if (endpointInfo != null && endpointInfo.getEndpoint() != null) {
                                    List<GetEndpointResponse.Routing> routings = endpointInfo.getEndpoint()
                                            .getRoutings();

                                    if (routings != null && !routings.isEmpty()) {
                                        GetEndpointResponse.Routing firstRouting = routings.get(0);
                                        String sessionId = firstRouting.getSession();

                                        if (sessionId != null && !sessionId.trim().isEmpty()) {
                                            // GetModelDeploySessionRes 객체에 sessionId 설정
                                            modelDeploy.setSessionId(sessionId);

                                            log.info(
                                                    "Endpoint Session 정보 - servingId: {}, routings[0].session: {}, " +
                                                            "status: {}",
                                                    modelDeploy.getServingId(),
                                                    sessionId,
                                                    firstRouting.getStatus());
                                        } else {
                                            log.debug("Endpoint Session 없음 - servingId: {}",
                                                    modelDeploy.getServingId());
                                        }
                                    } else {
                                        log.debug("Routings 정보 없음 - servingId: {}", modelDeploy.getServingId());
                                    }
                                } else {
                                    log.debug("Endpoint 정보 없음 - servingId: {}", modelDeploy.getServingId());
                                }

                            } catch (BusinessException endpointEx) {
                                log.warn("Endpoint 정보 조회 실패 (BusinessException) - servingId: {}, error: {}",
                                        modelDeploy.getServingId(), endpointEx.getMessage());
                            } catch (FeignException endpointEx) {
                                log.warn("Endpoint 정보 조회 실패 (FeignException) - servingId: {}, error: {}",
                                        modelDeploy.getServingId(), endpointEx.getMessage());
                            } catch (RuntimeException endpointEx) {
                                log.warn("Endpoint 정보 조회 실패 (RuntimeException) - servingId: {}, error: {}",
                                        modelDeploy.getServingId(), endpointEx.getMessage());
                            }
                        }))
                        .collect(Collectors.toList());

                // 4. 모든 병렬 조회 완료 대기
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long endTime = System.currentTimeMillis();
                log.info("Endpoint 정보 병렬 조회 완료 - 소요시간: {}ms", (endTime - startTime));

                // 5. sessionId가 설정된 모델 확인
                long sessionIdCount = sessionDeployList.stream()
                        .filter(deploy -> deploy.getSessionId() != null && !deploy.getSessionId().trim().isEmpty())
                        .count();
                log.info("SessionId 설정 완료 - 전체: {}건, SessionId 보유: {}건", sessionDeployList.size(), sessionIdCount);

            } catch (BusinessException e) {
                log.warn("Model Deploy 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Model Deploy 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Model Deploy 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // Model 세션별 자원 데이터 조회
            List<ResrcMgmtSessionResourceInfo> sessionResourceList = new ArrayList<>();

            try {
                // sessionId가 있는 모델들만 필터링
                List<GetModelDeploySessionRes> validSessionList = sessionDeployList.stream()
                        .filter(deploy -> deploy.getSessionId() != null && !deploy.getSessionId().trim().isEmpty())
                        .collect(Collectors.toList());

                log.info("세션별 자원 데이터 조회 시작 - 유효한 세션: {}건", validSessionList.size());

                // 각 세션별로 자원 데이터 조회
                for (GetModelDeploySessionRes modelDeploy : validSessionList) {
                    try {
                        ResrcMgmtSessionResourceInfo sessionResource = getSessionResourceBySessionId(
                                modelDeploy.getSessionId(),
                                modelDeploy.getName(),
                                modelDeploy.getServingId(),
                                modelDeploy.getStatus(),
                                modelDeploy);

                        if (sessionResource != null) {
                            sessionResourceList.add(sessionResource);
                        }

                    } catch (BusinessException e) {
                        log.warn("세션 자원 데이터 조회 실패 (BusinessException) - sessionId: {}, error: {}",
                                modelDeploy.getSessionId(), e.getMessage());
                    } catch (FeignException e) {
                        log.warn("세션 자원 데이터 조회 실패 (FeignException) - sessionId: {}, error: {}",
                                modelDeploy.getSessionId(), e.getMessage());
                    } catch (RuntimeException e) {
                        log.warn("세션 자원 데이터 조회 실패 (RuntimeException) - sessionId: {}, error: {}",
                                modelDeploy.getSessionId(), e.getMessage());
                    }
                }

                log.info("Model 세션별 자원 데이터 조회 완료 - 총 {}건", sessionResourceList.size());

            } catch (BusinessException e) {
                log.warn("Model 세션별 자원 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Model 세션별 자원 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Model 세션별 자원 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // Agent와 Model 자원 데이터 그룹화
            Map<String, Map<String, Object>> portalGroupedData = groupPortalDataByComponent(
                    agentCpuUsageResponse, agentCpuRequestsResponse, agentCpuLimitResponse,
                    agentMemoryUsageResponse, agentMemoryRequestsResponse, agentMemoryLimitResponse,
                    sessionResourceList);

            // Prometheus 데이터가 없으면 빈 데이터 유지 (더미 데이터 사용 안 함)
            if (portalGroupedData.isEmpty()) {
                log.info("Prometheus 데이터가 없어 포탈 빈 데이터를 유지합니다.");
            }

            portalData.put("components", portalGroupedData);

            // IDE 자원 현황 추가
            List<Map<String, Object>> ideResources = getIdeResources();
            portalData.put("ideResources", ideResources);

            return portalData;

        } catch (FeignException e) {
            log.error("포탈 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "포탈 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("포탈 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "포탈 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> getGpuNodeResources() {
        log.info("GPU 노드별 자원 현황 조회 시작");

        try {
            Map<String, Object> gpuNodeData = new HashMap<>();

            // 1) 노드별 CPU 데이터 조회
            Object cpuUsageResponse = null;
            Object cpuRequestResponse = null;
            try {
                cpuUsageResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_CPU_USAGE.getQuery()); // CPU 사용량
                cpuRequestResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_TOTAL_CPU.getQuery()); // CPU 총량
                log.info("GPU 노드별 CPU 데이터 조회 완료");
            } catch (BusinessException e) {
                log.warn("GPU 노드별 CPU 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("GPU 노드별 CPU 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("GPU 노드별 CPU 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 2) 노드별 Memory 데이터 조회
            Object memoryUsageResponse = null;
            Object memoryRequestResponse = null;
            try {
                memoryRequestResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_TOTAL_MEMORY.getQuery()); // Memory 총량
                memoryUsageResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_MEMORY_USAGE.getQuery()); // Memory 사용량
                log.info("GPU 노드별 Memory 데이터 조회 완료");
            } catch (BusinessException e) {
                log.warn("GPU 노드별 Memory 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("GPU 노드별 Memory 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("GPU 노드별 Memory 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 3) 노드별 GPU 데이터 조회
            Object gpuUsageResponse = null;
            Object gpuRequestResponse = null;
            try {
                gpuRequestResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_TOTAL_GPU.getQuery()); // GPU 개수 1코어
                // 100%로 계산
                gpuUsageResponse = resrcMgmtGpuClient
                        .executeQuery(ResrcMgmtQueryEnum.GPU_NODES_BY_INSTANCE_GPU_USAGE.getQuery()); // GPU 사용률
                log.info("GPU 노드별 GPU 데이터 조회 완료");
            } catch (BusinessException e) {
                log.warn("GPU 노드별 GPU 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("GPU 노드별 GPU 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("GPU 노드별 GPU 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 응답 파싱 (display_name 기준)
            Map<String, Object> cpuRequestData = extractNodeData(cpuRequestResponse);
            Map<String, Object> cpuUsageData = extractNodeData(cpuUsageResponse);
            Map<String, Object> memoryRequestData = extractNodeData(memoryRequestResponse);
            Map<String, Object> memoryUsageData = extractNodeData(memoryUsageResponse);
            Map<String, Object> gpuRequestData = extractNodeData(gpuRequestResponse);
            Map<String, Object> gpuUsageData = extractNodeData(gpuUsageResponse);

            // 모든 display_name 수집
            Set<String> displayNames = new HashSet<>();
            displayNames.addAll(cpuUsageData.keySet());
            displayNames.addAll(cpuRequestData.keySet());
            displayNames.addAll(memoryUsageData.keySet());
            displayNames.addAll(memoryRequestData.keySet());
            displayNames.addAll(gpuUsageData.keySet());
            displayNames.addAll(gpuRequestData.keySet());

            // service_group 정보 추출 (모든 응답에서 수집)
            Map<String, String> serviceGroupMap = new HashMap<>();
            serviceGroupMap.putAll(extractServiceGroupMap(cpuUsageResponse));
            serviceGroupMap.putAll(extractServiceGroupMap(cpuRequestResponse));
            serviceGroupMap.putAll(extractServiceGroupMap(memoryUsageResponse));
            serviceGroupMap.putAll(extractServiceGroupMap(memoryRequestResponse));
            serviceGroupMap.putAll(extractServiceGroupMap(gpuUsageResponse));
            serviceGroupMap.putAll(extractServiceGroupMap(gpuRequestResponse));

            List<Map<String, Object>> nodeList = new ArrayList<>();
            for (String displayName : displayNames) {
                double cpuUsage = getDoubleValue(cpuUsageData.get(displayName));
                double cpuRequest = getDoubleValue(cpuRequestData.get(displayName));
                double cpuLimit = cpuRequest;

                double memoryUsage = getDoubleValue(memoryUsageData.get(displayName));
                double memoryRequest = getDoubleValue(memoryRequestData.get(displayName));
                double memoryLimit = memoryRequest;

                double gpuUsagePercent = getDoubleValue(gpuUsageData.get(displayName)); // GPU 사용률 (0.1% 단위)
                double gpuRequest = getDoubleValue(gpuRequestData.get(displayName));
                double gpuUsage = gpuUsagePercent * gpuRequest; // GPU 사용률(%) x gpuRequest = gpu_usage
                double gpuLimit = gpuRequest;

                Map<String, Object> nodeData = new HashMap<>();
                nodeData.put("display_name", displayName);
                String serviceGroup = serviceGroupMap.get(displayName);
                if (serviceGroup != null && !serviceGroup.isEmpty()) {
                    nodeData.put("service_group", serviceGroup);
                }
                nodeData.put("cpu_usage", cpuUsage);
                nodeData.put("cpu_request", cpuRequest);
                nodeData.put("cpu_limit", cpuLimit);
                nodeData.put("memory_usage", memoryUsage);
                nodeData.put("memory_request", memoryRequest);
                nodeData.put("memory_limit", memoryLimit);
                nodeData.put("gpu_usage", gpuUsage);
                nodeData.put("gpu_request", gpuRequest);
                nodeData.put("gpu_limit", gpuLimit);

                nodeList.add(nodeData);
            }

            // Prometheus 데이터가 없으면 빈 리스트 유지 (더미 데이터 사용 안 함)
            if (nodeList.isEmpty()) {
                log.info("Prometheus 데이터가 없어 GPU 노드 빈 리스트를 유지합니다.");
            }

            gpuNodeData.put("nodes", nodeList);

            return gpuNodeData;

        } catch (FeignException e) {
            log.error("GPU 노드별 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "GPU 노드별 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("GPU 노드별 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "GPU 노드별 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getSolutionResources() {
        log.info("솔루션 자원 현황 조회 시작");

        try {
            Map<String, Object> solutionData = new HashMap<>();

            // 솔루션 목록 정보 추가 및 자원 데이터 조회
            List<Map<String, Object>> solutionList = new ArrayList<>();
            boolean hasAnyPrometheusData = false;

            // 활성 프로파일 확인 (prod 또는 나머지)
            String activeProfile = getActiveProfile();
            log.info("🔍 [솔루션 자원 조회] 활성 프로파일: {}, 네임스페이스 타입: {}",
                    Arrays.toString(environment.getActiveProfiles()), activeProfile);

            for (ResrcMgmtNamespaceEnum solution : ResrcMgmtNamespaceEnum.values()) {
                // 포탈 자원(AGENT, MODEL)은 제외하고 솔루션만 포함
                if (solution != ResrcMgmtNamespaceEnum.AGENT &&
                        solution != ResrcMgmtNamespaceEnum.MODEL) {

                    // 프로파일 기반 네임스페이스 조회
                    String namespace = solution.getNamespace(activeProfile);
                    List<String> namespaceList = solution.getNamespaceList(activeProfile);
                    log.debug("📦 [솔루션 자원 조회] 솔루션: {}, 프로파일: {}, 선택된 네임스페이스: {}",
                            solution.getDisplayName(), activeProfile, namespace);

                    Map<String, Object> solutionInfo = new HashMap<>();
                    solutionInfo.put("id", solution.name());
                    solutionInfo.put("name", solution.getDisplayName());
                    solutionInfo.put("namespaces", namespaceList);

                    // 네임스페이스별 자원 데이터 조회 및 추가
                    Map<String, Object> resourceData = querySolutionResources(namespace, solution.getDisplayName());
                    if (resourceData != null) {
                        solutionInfo.putAll(resourceData);
                        hasAnyPrometheusData = true;
                    } else {
                        // 데이터가 없으면 기본값 설정
                        addDefaultResourceData(solutionInfo);
                    }

                    solutionList.add(solutionInfo);
                }
            }

            // Prometheus 데이터가 없으면 기본값 유지 (더미 데이터 사용 안 함)
            if (!hasAnyPrometheusData) {
                log.info("Prometheus 데이터가 없어 솔루션 기본값을 유지합니다.");
            }

            solutionData.put("solutionList", solutionList);

            return solutionData;

        } catch (FeignException e) {
            log.error("솔루션 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "솔루션 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("솔루션 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "솔루션 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPortalAgentPodResources() {
        log.info("포탈 에이전트 파드별 자원 현황 조회 시작");

        try {
            // 에이전트 파드별 자원 데이터 조회
            Object agentPodCpuUsageResponse = null;
            Object agentPodCpuRequestsResponse = null;
            Object agentPodCpuLimitResponse = null;
            Object agentPodMemoryUsageResponse = null;
            Object agentPodMemoryRequestsResponse = null;
            Object agentPodMemoryLimitResponse = null;

            try {
                agentPodCpuUsageResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_CPU_USAGE.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(), ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                agentPodCpuRequestsResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_CPU_REQUESTS.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(), ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                agentPodCpuLimitResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_CPU_LIMITS.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                        ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                agentPodMemoryUsageResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_MEMORY_USAGE.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(), ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                agentPodMemoryRequestsResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_MEMORY_REQUESTS.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(), ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                agentPodMemoryLimitResponse = resrcMgmtClient.executeQuery(String.format(
                        ResrcMgmtQueryEnum.PORTAL_AGENT_POD_MEMORY_LIMITS.getQuery(),
                        ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                        ResrcMgmtNamespaceEnum.AGENT.getPodPattern()));
                log.info("에이전트 파드별 자원 데이터 조회 완료 (namespace: {}, pod: {})", ResrcMgmtNamespaceEnum.AGENT.getNamespace(),
                        ResrcMgmtNamespaceEnum.AGENT.getPodPattern());
            } catch (BusinessException e) {
                log.warn("에이전트 파드별 자원 데이터 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("에이전트 파드별 자원 데이터 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("에이전트 파드별 자원 데이터 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 에이전트 파드별 데이터 그룹화
            // pods:{ pod_name: { cpu_usage: 0.0, cpu_requests: 0.0, cpu_limit: 0.0,
            // memory_usage: 0.0,
            // memory_requests: 0.0, memory_limit: 0.0 } }
            Map<String, Object> agentPodGroupedData = groupAgentPodDataByPod(
                    agentPodCpuUsageResponse, agentPodCpuRequestsResponse, agentPodCpuLimitResponse,
                    agentPodMemoryUsageResponse, agentPodMemoryRequestsResponse, agentPodMemoryLimitResponse);

            // Prometheus 데이터가 없으면 빈 데이터 반환 (더미 데이터 사용 안 함)
            if (agentPodGroupedData.isEmpty() ||
                    (agentPodGroupedData.get("pods") instanceof List
                            && ((List<?>) agentPodGroupedData.get("pods")).isEmpty())) {
                log.info("Prometheus 데이터가 없어 에이전트 파드별 빈 데이터를 반환합니다.");
                // 빈 데이터 구조 유지
                if (!agentPodGroupedData.containsKey("pods")) {
                    agentPodGroupedData.put("pods", new ArrayList<>());
                }
            }

            // Pod 정보에 에이전트 앱 이름과 빌더 이름 추가
            enrichAgentPodWithDeploymentInfo(agentPodGroupedData);

            return agentPodGroupedData;

        } catch (FeignException e) {
            log.error("포탈 에이전트 파드별 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "포탈 에이전트 파드별 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("포탈 에이전트 파드별 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "포탈 에이전트 파드별 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getGpuNodeDetailResources(String nodeName, String fromDate, String toDate,
                                                         String durationParam, long fromTimestamp, long toTimestamp, String workloadName) {
        log.info("GPU 노드별 상세 자원 현황 조회 시작 - 노드: {}, 기간: {} ~ {}, duration: {}, 워크로드: {}", nodeName, fromDate, toDate,
                durationParam, workloadName);

        try {
            Map<String, Object> gpuNodeDetailData = new HashMap<>();

            // 1. 배포 워크로드 조회 (session_id 벡터 응답 파싱)
            Object workloadCountResponse = null;
            List<String> workloads = new ArrayList<>();
            try {
                String workloadCountQuery = String.format(ResrcMgmtQueryEnum.GPU_NODE_WORKLOAD_LIST.getQuery(),
                        nodeName);
                workloadCountResponse = resrcMgmtGpuClient.executeQuery(workloadCountQuery);
                workloads = extractSessionIdList(workloadCountResponse);
                gpuNodeDetailData.put("workloads", workloads);
                gpuNodeDetailData.put("workload_count", workloads.size());
                log.info("GPU 노드 {} 워크로드 세션 목록/수 조회 완료 - {}건", nodeName, workloads.size());
            } catch (BusinessException e) {
                log.warn("GPU 노드 {} 워크로드 세션 조회 실패 (BusinessException): {}", nodeName, e.getMessage());
                // 더미 데이터 사용 안함: 빈 목록과 0 카운트로 처리
                workloads = new ArrayList<>();
            } catch (FeignException e) {
                log.warn("GPU 노드 {} 워크로드 세션 조회 실패 (FeignException): {}", nodeName, e.getMessage());
                // 더미 데이터 사용 안함: 빈 목록과 0 카운트로 처리
                workloads = new ArrayList<>();
            } catch (RuntimeException e) {
                log.warn("GPU 노드 {} 워크로드 세션 조회 실패 (RuntimeException): {}", nodeName, e.getMessage());
                // 더미 데이터 사용 안함: 빈 목록과 0 카운트로 처리
                workloads = new ArrayList<>();
                gpuNodeDetailData.put("workloads", workloads);
                gpuNodeDetailData.put("workload_count", 0);
                log.info("GPU 노드 {} 워크로드 데이터 없음 - 실데이터만 표시(0)", nodeName);
            }

            // 워크로드가 비어있어도 더미 데이터 사용 안함 (빈 목록 유지)

            // 2. GPU 노드 instance별 평균 사용률 조회 (CPU, Memory, GPU)
            Object instanceCpuAvgUtilResponse = null;
            Object instanceMemoryAvgUtilResponse = null;
            Object instanceGpuAvgUtilResponse = null;

            try {
                // CPU 요청량 대비 사용률 조회 (할당량 대비)
                String cpuUsageVsRequestsQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_CPU_USAGE_VS_REQUESTS.getQuery(),
                        nodeName,
                        workloadName,
                        durationParam,
                        nodeName,
                        workloadName
                );
                instanceCpuAvgUtilResponse = resrcMgmtGpuClient.executeQuery(cpuUsageVsRequestsQuery);

                // Memory 요청량 대비 사용률 조회 (할당량 대비)
                String memoryUsageVsRequestsQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_MEMORY_USAGE_VS_REQUESTS.getQuery(),
                        nodeName,
                        workloadName,
                        nodeName,
                        workloadName);
                instanceMemoryAvgUtilResponse = resrcMgmtGpuClient.executeQuery(memoryUsageVsRequestsQuery);

                // GPU 요청량 대비 사용률 조회 (할당량 대비)
                String gpuUsageVsRequestsQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_GPU_USAGE_VS_REQUESTS.getQuery(),
                        nodeName,
                        workloadName);
                instanceGpuAvgUtilResponse = resrcMgmtGpuClient.executeQuery(gpuUsageVsRequestsQuery);

            } catch (BusinessException e) {
                log.warn("GPU 노드 {} 요청량 대비 사용률 조회 실패 (BusinessException): {}", nodeName, e.getMessage());
            } catch (FeignException e) {
                log.warn("GPU 노드 {} 요청량 대비 사용률 조회 실패 (FeignException): {}", nodeName, e.getMessage());
            } catch (RuntimeException e) {
                log.warn("GPU 노드 {} 요청량 대비 사용률 조회 실패 (RuntimeException): {}", nodeName, e.getMessage());
            }

            // 3. GPU 노드 워크로드별 시계열 사용률 조회 (CPU, Memory, GPU - session_id별 그래프용)
            Object workloadCpuTimeseriesResponse = null;
            Object workloadMemoryTimeseriesResponse = null;
            Object workloadGpuTimeseriesResponse = null;

            // 시간 파라미터 준비 (그래프 데이터 처리에서도 사용)
            String step = "30";

            try {
                // 유닉스 timestamp로 변환 (GPU 노드 상세는 timestamp 형식 사용)

                String startTime = String.valueOf(fromTimestamp);
                String endTime = String.valueOf(toTimestamp);

                // CPU 사용량 그래프 조회 (session_id별)
                String workloadCpuUsageGraphQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_WORKLOAD_CPU_USAGE_GRAPH.getQuery(),
                        nodeName, nodeName);
                workloadCpuTimeseriesResponse = resrcMgmtGpuClient.executeQueryRange(
                        workloadCpuUsageGraphQuery, startTime, endTime, step);
                log.info("GPU 노드 {} 워크로드별 CPU 사용량 그래프 조회 완료 (start: {}, end: {})", nodeName, startTime, endTime);

                // Memory 사용량 그래프 조회 (session_id별)
                String workloadMemoryUsageGraphQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_WORKLOAD_MEMORY_USAGE_GRAPH.getQuery(),
                        nodeName, nodeName);
                workloadMemoryTimeseriesResponse = resrcMgmtGpuClient.executeQueryRange(
                        workloadMemoryUsageGraphQuery, startTime, endTime, step);
                log.info("GPU 노드 {} 워크로드별 Memory 사용량 그래프 조회 완료 (start: {}, end: {})", nodeName, startTime, endTime);

                // GPU 사용량 그래프 조회 (session_id별)
                String workloadGpuUsageGraphQuery = String.format(
                        ResrcMgmtQueryEnum.GPU_NODE_WORKLOAD_GPU_USAGE_GRAPH.getQuery(),
                        nodeName);
                workloadGpuTimeseriesResponse = resrcMgmtGpuClient.executeQueryRange(
                        workloadGpuUsageGraphQuery, startTime, endTime, step);
                log.info("GPU 노드 {} 워크로드별 GPU 사용량 그래프 조회 완료 (start: {}, end: {})", nodeName, startTime, endTime);

            } catch (BusinessException e) {
                log.warn("GPU 노드 {} 워크로드별 시계열 사용률 조회 실패 (BusinessException): {}", nodeName, e.getMessage());
            } catch (FeignException e) {
                log.warn("GPU 노드 {} 워크로드별 시계열 사용률 조회 실패 (FeignException): {}", nodeName, e.getMessage());
            } catch (RuntimeException e) {
                log.warn("GPU 노드 {} 워크로드별 시계열 사용률 조회 실패 (RuntimeException): {}", nodeName, e.getMessage());
            }

            // 상세 정보 구성 (workloads와 workload_count는 이미 gpuNodeDetailData에 추가됨)
            Map<String, Object> nodeInfo = new HashMap<>();

            // 사용률 정보 구성
            Map<String, Object> usageRates = new HashMap<>();
            usageRates.put("cpu_usage_vs_requests", 0.0);
            usageRates.put("cpu_usage_vs_limits", 0.0);
            usageRates.put("memory_usage_vs_requests", 0.0);
            usageRates.put("memory_usage_vs_limits", 0.0);
            usageRates.put("gpu_usage_vs_requests", 0.0);
            usageRates.put("gpu_usage_vs_limits", 0.0);

            // GPU 노드 instance별 평균 사용률 추가 (할당량 대비 및 상한량 대비 - 동일 값)
            Double cpuAvgUtil = extractNumericValue(instanceCpuAvgUtilResponse);
            Double memoryAvgUtil = extractNumericValue(instanceMemoryAvgUtilResponse);
            Double gpuAvgUtil = extractNumericValue(instanceGpuAvgUtilResponse);

            // CPU/Memory/GPU 사용률: 실데이터만 사용, 없으면 0.0 처리
            usageRates.put("cpu_allocation_usage_rate", cpuAvgUtil != null ? cpuAvgUtil : 0.0);
            usageRates.put("cpu_limit_usage_rate", cpuAvgUtil != null ? cpuAvgUtil : 0.0);
            usageRates.put("memory_allocation_usage_rate", memoryAvgUtil != null ? memoryAvgUtil : 0.0);
            usageRates.put("memory_limit_usage_rate", memoryAvgUtil != null ? memoryAvgUtil : 0.0);
            usageRates.put("gpu_allocation_usage_rate", gpuAvgUtil != null ? gpuAvgUtil : 0.0);
            usageRates.put("gpu_limit_usage_rate", gpuAvgUtil != null ? gpuAvgUtil : 0.0);

            // 워크로드별 시계열 사용률 그래프 데이터 처리 (session_id별)
            // 실데이터만 사용, 데이터가 없을 경우 빈 데이터로 처리
            Map<String, Object> workloadCpuGraphData = processWorkloadTimeSeriesGraphData(
                    workloadCpuTimeseriesResponse);
            if (workloadCpuTimeseriesResponse == null || workloadCpuGraphData == null
                    || workloadCpuGraphData.isEmpty()) {
                log.info("GPU 노드 {} 워크로드별 CPU 그래프 데이터가 없습니다. (실데이터만 표시)", nodeName);
                workloadCpuGraphData = new HashMap<>();
            } else {
                log.info("GPU 노드 {} 워크로드별 CPU 그래프 실데이터 사용 - {}개 세션", nodeName, workloadCpuGraphData.size());
            }

            Map<String, Object> workloadMemoryGraphData = processWorkloadTimeSeriesGraphData(
                    workloadMemoryTimeseriesResponse);
            if (workloadMemoryTimeseriesResponse == null || workloadMemoryGraphData == null
                    || workloadMemoryGraphData.isEmpty()) {
                log.info("GPU 노드 {} 워크로드별 Memory 그래프 데이터가 없습니다. (실데이터만 표시)", nodeName);
                workloadMemoryGraphData = new HashMap<>();
            } else {
                log.info("GPU 노드 {} 워크로드별 Memory 그래프 실데이터 사용 - {}개 세션", nodeName, workloadMemoryGraphData.size());
            }

            Map<String, Object> workloadGpuGraphData = processWorkloadTimeSeriesGraphData(
                    workloadGpuTimeseriesResponse);
            if (workloadGpuTimeseriesResponse == null || workloadGpuGraphData == null
                    || workloadGpuGraphData.isEmpty()) {
                log.info("GPU 노드 {} 워크로드별 GPU 그래프 데이터가 없습니다. (실데이터만 표시)", nodeName);
                workloadGpuGraphData = new HashMap<>();
            } else {
                log.info("GPU 노드 {} 워크로드별 GPU 그래프 실데이터 사용 - {}개 세션", nodeName, workloadGpuGraphData.size());
            }

            gpuNodeDetailData.put("nodeName", nodeName);
            gpuNodeDetailData.put("fromDate", fromDate);
            gpuNodeDetailData.put("toDate", toDate);
            gpuNodeDetailData.put("nodeInfo", nodeInfo);
            gpuNodeDetailData.put("usageRates", usageRates);

            gpuNodeDetailData.put("workloadCpuGraph", workloadCpuGraphData);
            gpuNodeDetailData.put("workloadMemoryGraph", workloadMemoryGraphData);
            gpuNodeDetailData.put("workloadGpuGraph", workloadGpuGraphData);

            // 4. 세션별 Quota 그리드 조회 (CPU, Memory, GPU)
            List<Map<String, Object>> sessionCpuQuotaGrid = processSessionQuotaGrid(
                    nodeName, durationParam, resrcMgmtGpuClient,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_CPU_CAPACITY,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_CPU_USAGE,
                    "cpu");

            List<Map<String, Object>> sessionMemoryQuotaGrid = processSessionQuotaGrid(
                    nodeName, durationParam, resrcMgmtGpuClient,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_MEMORY_CAPACITY,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_MEMORY_USAGE,
                    "memory");

            List<Map<String, Object>> sessionGpuQuotaGrid = processSessionQuotaGrid(
                    nodeName, durationParam, resrcMgmtGpuClient,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_GPU_CAPACITY,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_GPU_MEMORY_USAGE,
                    ResrcMgmtQueryEnum.GPU_NODE_SESSION_GPU_UTILIZATION,
                    "gpu");

            // Quota 그리드가 비어있으면 빈 리스트 유지 (더미 데이터 사용 안 함)
            if (sessionCpuQuotaGrid.isEmpty()) {
                log.info("GPU 노드 {} 세션별 CPU Quota 그리드가 비어있습니다.", nodeName);
            }

            if (sessionMemoryQuotaGrid.isEmpty()) {
                log.info("GPU 노드 {} 세션별 Memory Quota 그리드가 비어있습니다.", nodeName);
            }

            if (sessionGpuQuotaGrid.isEmpty()) {
                log.info("GPU 노드 {} 세션별 GPU Quota 그리드가 비어있습니다.", nodeName);
            }

            gpuNodeDetailData.put("sessionCpuQuotaGrid", sessionCpuQuotaGrid);
            gpuNodeDetailData.put("sessionMemoryQuotaGrid", sessionMemoryQuotaGrid);
            gpuNodeDetailData.put("sessionGpuQuotaGrid", sessionGpuQuotaGrid);

            return gpuNodeDetailData;

        } catch (FeignException e) {
            log.error("GPU 노드별 상세 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "GPU 노드별 상세 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("GPU 노드별 상세 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "GPU 노드별 상세 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 워크로드별 시계열 그래프 데이터 처리 (session_id별)
     *
     * @param graphResponse Prometheus query_range 응답 데이터
     * @return session_id를 키로 하고 시계열 데이터를 값으로 하는 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> processWorkloadTimeSeriesGraphData(Object graphResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (graphResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) graphResponse;
                if (responseMap.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                    if (data.containsKey("result")) {
                        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");

                        for (Map<String, Object> metric : results) {
                            if (metric.containsKey("metric") && metric.containsKey("values")) {
                                Map<String, Object> metricInfo = (Map<String, Object>) metric.get("metric");
                                String sessionId = (String) metricInfo.get("session_id");

                                if (sessionId != null) {
                                    // Prometheus values를 숫자로 변환 (ApexCharts 호환)
                                    List<List<Object>> rawValues = (List<List<Object>>) metric.get("values");
                                    List<List<Object>> convertedValues = new ArrayList<>();

                                    for (List<Object> valueArray : rawValues) {
                                        if (valueArray.size() >= 2) {
                                            // Timestamp를 밀리초 단위로 변환 (ApexCharts는 밀리초 단위 사용)
                                            Long timestamp = parseLongValue(valueArray.get(0));
                                            if (timestamp != null) {
                                                timestamp = timestamp * 1000; // 초 -> 밀리초 변환
                                            }

                                            // 값을 숫자로 변환
                                            Double value = parseDoubleValue(valueArray.get(1));

                                            // NaN, Infinity 체크 및 변환
                                            if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
                                                value = 0.0;
                                            }

                                            List<Object> convertedValue = new ArrayList<>();
                                            convertedValue.add(timestamp != null ? timestamp : 0L);
                                            convertedValue.add(value);
                                            convertedValues.add(convertedValue);
                                        }
                                    }

                                    result.put(sessionId, convertedValues);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.error("워크로드별 시계열 그래프 데이터 처리 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("워크로드별 시계열 그래프 데이터 처리 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("워크로드별 시계열 그래프 데이터 처리 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("워크로드별 시계열 그래프 데이터 처리 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * IDE 자원 현황 조회
     */
    private List<Map<String, Object>> getIdeResources() {
        try {

            // 전체 IDE 목록 조회 (조건없이)
//            List<UserIdeStatus> allIdeList = userIdeStatusRepository.findAllIdeStatus();
            List<IdeStatusDto> allIdeList = gpoIdeStatusMasRepository.findAllIdeStatus();
            List<Map<String, Object>> ideList = new ArrayList<>();

            for (IdeStatusDto ide : allIdeList) {
                Map<String, Object> ideMap = new HashMap<>();
//                ideMap.put("ideId", ide.getUuid());
                ideMap.put("userId", ide.getMemberId());
                ideMap.put("username", ide.getJkwNm());
                ideMap.put("imageType", ide.getImgG());
                ideMap.put("imageName", ide.getImgNm());
//                ideMap.put("status", ide.getStatusNm());
//                ideMap.put("projectId", ide.getPrjSeq());
                ideMap.put("ideStatusId", ide.getIdeStatusId());
                ideMap.put("dwAccountId", ide.getDwAccountId());
                ideMap.put("cpu", ide.getCpuUseHaldngV().doubleValue());
                ideMap.put("memory", ide.getMemUseHaldngV().doubleValue());
//                ideMap.put("image", ide.getTagCtnt());
//                ideMap.put("createdAt", ide.getFstCreatedAt());
//                ideMap.put("updatedAt", ide.getLstUpdatedAt());
                ideMap.put("expireAt", ide.getExpAt());
//                ideMap.put("ingressUrl", ide.getSvrUrlNm());
//                ideMap.put("pythonVersion", ide.getPgmVersionNo());
                ideList.add(ideMap);
            }

            // IDE 자원을 배열로 직접 반환
            return ideList;
        } catch (DataAccessException e) {
            log.error("IDE 자원 현황 조회 실패 (DataAccessException): {}", e.getMessage(), e);
            return new ArrayList<>();
        } catch (IllegalArgumentException e) {
            log.error("IDE 자원 현황 조회 실패 (IllegalArgumentException): {}", e.getMessage(), e);
            return new ArrayList<>();
        } catch (RuntimeException e) {
            log.error("IDE 자원 현황 조회 실패 (RuntimeException): {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Prometheus 응답에서 단일 값을 추출하는 메서드
     *
     * @param response Prometheus 응답 데이터
     * @return 추출된 값
     */

    /**
     * 포탈 컴포넌트별 데이터를 컴포넌트별로 그룹화하는 메서드
     *
     * @param agentCpuUsage       Agent CPU 사용량 응답
     * @param agentCpuRequests    Agent CPU 요청량 응답
     * @param agentMemoryUsage    Agent Memory 사용량 응답
     * @param agentMemoryRequests Agent Memory 요청량 응답
     * @param modelCpuUsage       Model CPU 사용량 응답
     * @param modelCpuRequests    Model CPU 요청량 응답
     * @param modelMemoryUsage    Model Memory 사용량 응답
     * @param modelMemoryRequests Model Memory 요청량 응답
     * @param modelGpuUsage       Model GPU 사용량 응답
     * @param modelGpuRequests    Model GPU 요청량 응답
     * @return 컴포넌트별로 그룹화된 데이터
     */
    private Map<String, Map<String, Object>> groupPortalDataByComponent(
            Object agentCpuUsage, Object agentCpuRequests, Object agentCpuLimit, Object agentMemoryUsage,
            Object agentMemoryRequests, Object agentMemoryLimit,
            List<ResrcMgmtSessionResourceInfo> sessionResourceList) {

        Map<String, Map<String, Object>> portalGroupedData = new HashMap<>();

        try {
            // Agent 컴포넌트 데이터 처리
            Map<String, Object> agentData = new HashMap<>();
            agentData.put("cpu_usage", extractSingleValue(agentCpuUsage));
            agentData.put("cpu_request", extractSingleValue(agentCpuRequests));
            agentData.put("cpu_limit", extractSingleValue(agentCpuLimit));
            agentData.put("memory_usage", extractSingleValue(agentMemoryUsage));
            agentData.put("memory_request", extractSingleValue(agentMemoryRequests));
            agentData.put("memory_limit", extractSingleValue(agentMemoryLimit));
            portalGroupedData.put("Agent", agentData);

            // Model 세션별 자원 데이터 처리
            if (sessionResourceList != null && !sessionResourceList.isEmpty()) {
                // 세션별 자원 데이터를 리스트로 추가
                List<Map<String, Object>> sessionDataList = new ArrayList<>();

                for (ResrcMgmtSessionResourceInfo sessionResource : sessionResourceList) {
                    Map<String, Object> sessionData = new HashMap<>();
                    sessionData.put("sessionId", sessionResource.getSessionId());
                    sessionData.put("modelName", sessionResource.getModelName());
                    sessionData.put("servingId", sessionResource.getServingId());
                    sessionData.put("status", sessionResource.getStatus());
                    sessionData.put("projectId", sessionResource.getProjectId());
                    sessionData.put("projectName", sessionResource.getProjectName());

                    // CPU 자원 (Core 단위)
                    sessionData.put("cpu_usage", sessionResource.getCpuUsage());
                    sessionData.put("cpu_utilization", sessionResource.getCpuUtilization());
                    sessionData.put("cpu_request", sessionResource.getCpuRequest());
                    sessionData.put("cpu_limit", sessionResource.getCpuLimit());

                    // Memory 자원 (GiB 단위)
                    sessionData.put("memory_usage", sessionResource.getMemoryUsage());
                    sessionData.put("memory_utilization", sessionResource.getMemoryUtilization());
                    sessionData.put("memory_request", sessionResource.getMemoryRequest());
                    sessionData.put("memory_limit", sessionResource.getMemoryLimit());

                    // GPU 자원 (MiB 단위)
                    sessionData.put("gpu_usage", sessionResource.getGpuUsage());
                    sessionData.put("gpu_utilization", sessionResource.getGpuUtilization());
                    sessionData.put("gpu_request", sessionResource.getGpuRequest());
                    sessionData.put("gpu_limit", sessionResource.getGpuLimit());

                    sessionDataList.add(sessionData);
                }

                // Model 컴포넌트에 세션별 데이터 추가
                Map<String, Object> modelData = new HashMap<>();
                modelData.put("sessions", sessionDataList);
                List<String> modelProjectIds = sessionResourceList.stream()
                        .map(ResrcMgmtSessionResourceInfo::getProjectId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
                modelData.put("projectIds", modelProjectIds);

                // 전체 합계 계산
                double totalCpuUsage = sessionResourceList.stream()
                        .mapToDouble(s -> s.getCpuUsage() != null ? s.getCpuUsage() : 0.0)
                        .sum();
                double totalCpuRequest = sessionResourceList.stream()
                        .mapToDouble(s -> s.getCpuRequest() != null ? s.getCpuRequest() : 0.0)
                        .sum();
                double totalCpuLimit = sessionResourceList.stream()
                        .mapToDouble(s -> s.getCpuLimit() != null ? s.getCpuLimit() : 0.0)
                        .sum();
                double totalMemoryUsage = sessionResourceList.stream()
                        .mapToDouble(s -> s.getMemoryUsage() != null ? s.getMemoryUsage() : 0.0)
                        .sum();
                double totalMemoryRequest = sessionResourceList.stream()
                        .mapToDouble(s -> s.getMemoryRequest() != null ? s.getMemoryRequest() : 0.0)
                        .sum();
                double totalMemoryLimit = sessionResourceList.stream()
                        .mapToDouble(s -> s.getMemoryLimit() != null ? s.getMemoryLimit() : 0.0)
                        .sum();
                double totalGpuUsage = sessionResourceList.stream()
                        .mapToDouble(s -> s.getGpuUsage() != null ? s.getGpuUsage() : 0.0)
                        .sum();
                double totalGpuRequest = sessionResourceList.stream()
                        .mapToDouble(s -> s.getGpuRequest() != null ? s.getGpuRequest() : 0.0)
                        .sum();
                double totalGpuLimit = sessionResourceList.stream()
                        .mapToDouble(s -> s.getGpuLimit() != null ? s.getGpuLimit() : 0.0)
                        .sum();

                modelData.put("cpu_usage", totalCpuUsage);
                modelData.put("cpu_request", totalCpuRequest);
                modelData.put("cpu_limit", totalCpuLimit);
                modelData.put("memory_usage", totalMemoryUsage);
                modelData.put("memory_request", totalMemoryRequest);
                modelData.put("memory_limit", totalMemoryLimit);
                modelData.put("gpu_usage", totalGpuUsage);
                modelData.put("gpu_request", totalGpuRequest);
                modelData.put("gpu_limit", totalGpuLimit);

                portalGroupedData.put("Model", modelData);
            } else {
                // Model 데이터가 없으면 빈 데이터 구조 생성 (더미 데이터 사용 안 함)
                Map<String, Object> emptyModelData = new HashMap<>();
                emptyModelData.put("sessions", Collections.emptyList());
                emptyModelData.put("projectIds", Collections.emptyList());
                emptyModelData.put("cpu_usage", 0.0);
                emptyModelData.put("cpu_request", 0.0);
                emptyModelData.put("cpu_limit", 0.0);
                emptyModelData.put("memory_usage", 0.0);
                emptyModelData.put("memory_request", 0.0);
                emptyModelData.put("memory_limit", 0.0);
                emptyModelData.put("gpu_usage", 0.0);
                emptyModelData.put("gpu_request", 0.0);
                emptyModelData.put("gpu_limit", 0.0);
                portalGroupedData.put("Model", emptyModelData);
            }

        } catch (ClassCastException e) {
            log.error("포탈 데이터 그룹화 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("포탈 데이터 그룹화 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("포탈 데이터 그룹화 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("포탈 데이터 그룹화 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return portalGroupedData;
    }


    /**
     * 에이전트 파드별 데이터를 파드별로 그룹화하는 메서드
     *
     * @param agentPodCpuUsage       에이전트 파드별 CPU 사용량 응답
     * @param agentPodCpuRequests    에이전트 파드별 CPU 요청량 응답
     * @param agentPodCpuLimit       에이전트 파드별 CPU 제한량 응답
     * @param agentPodMemoryUsage    에이전트 파드별 Memory 사용량 응답
     * @param agentPodMemoryRequests 에이전트 파드별 Memory 요청량 응답
     * @param agentPodMemoryLimit    에이전트 파드별 Memory 제한량 응답
     * @return 파드별로 그룹화된 데이터
     */
    private Map<String, Object> groupAgentPodDataByPod(
            Object agentPodCpuUsage, Object agentPodCpuRequests, Object agentPodCpuLimit,
            Object agentPodMemoryUsage, Object agentPodMemoryRequests, Object agentPodMemoryLimit) {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> podsList = new ArrayList<>();

        try {
            // CPU 사용량 데이터 처리
            Map<String, Object> cpuUsageData = extractPodMetrics(agentPodCpuUsage, "cpu_usage");
            // CPU 요청량 데이터 처리
            Map<String, Object> cpuRequestsData = extractPodMetrics(agentPodCpuRequests, "cpu_request");
            // CPU 제한량 데이터 처리
            Map<String, Object> cpuLimitData = extractPodMetrics(agentPodCpuLimit, "cpu_limit");
            // Memory 사용량 데이터 처리
            Map<String, Object> memoryUsageData = extractPodMetrics(agentPodMemoryUsage, "memory_usage");
            // Memory 요청량 데이터 처리
            Map<String, Object> memoryRequestsData = extractPodMetrics(agentPodMemoryRequests, "memory_request");
            // Memory 제한량 데이터 처리
            Map<String, Object> memoryLimitData = extractPodMetrics(agentPodMemoryLimit, "memory_limit");

            // 모든 파드 이름 수집 (중복 제거)
            Set<String> allPodNames = new HashSet<>();
            cpuUsageData.keySet().forEach(key -> allPodNames.add(key.replace("_cpu_usage", "")));
            cpuRequestsData.keySet().forEach(key -> allPodNames.add(key.replace("_cpu_request", "")));
            cpuLimitData.keySet().forEach(key -> allPodNames.add(key.replace("_cpu_limit", "")));
            memoryUsageData.keySet().forEach(key -> allPodNames.add(key.replace("_memory_usage", "")));
            memoryRequestsData.keySet().forEach(key -> allPodNames.add(key.replace("_memory_request", "")));
            memoryLimitData.keySet().forEach(key -> allPodNames.add(key.replace("_memory_limit", "")));

            // 파드별로 데이터 그룹화
            for (String podName : allPodNames) {
                Map<String, Object> podData = new HashMap<>();
                podData.put("pod_name", podName);
                podData.put("cpu_usage", cpuUsageData.getOrDefault(podName + "_cpu_usage", 0));
                podData.put("cpu_request", cpuRequestsData.getOrDefault(podName + "_cpu_request", 0));
                podData.put("cpu_limit", cpuLimitData.getOrDefault(podName + "_cpu_limit", 0));
                podData.put("memory_usage", memoryUsageData.getOrDefault(podName + "_memory_usage", 0));
                podData.put("memory_request", memoryRequestsData.getOrDefault(podName + "_memory_request", 0));
                podData.put("memory_limit", memoryLimitData.getOrDefault(podName + "_memory_limit", 0));

                podsList.add(podData);
            }

            result.put("pods", podsList);

        } catch (ClassCastException e) {
            log.error("에이전트 파드별 데이터 그룹화 실패 (ClassCastException): {}", e.getMessage(), e);
            result.put("pods", new ArrayList<>());
        } catch (IllegalArgumentException e) {
            log.error("에이전트 파드별 데이터 그룹화 실패 (IllegalArgumentException): {}", e.getMessage(), e);
            result.put("pods", new ArrayList<>());
        } catch (NullPointerException e) {
            log.error("에이전트 파드별 데이터 그룹화 실패 (NullPointerException): {}", e.getMessage(), e);
            result.put("pods", new ArrayList<>());
        } catch (RuntimeException e) {
            log.error("에이전트 파드별 데이터 그룹화 실패 (RuntimeException): {}", e.getMessage(), e);
            result.put("pods", new ArrayList<>());
        }

        return result;
    }

    /**
     * Prometheus 응답에서 파드별 메트릭을 추출하는 메서드
     *
     * @param response   Prometheus 응답 데이터
     * @param metricType 메트릭 타입 (cpu_usage, cpu_request 등)
     * @return 파드별 메트릭 데이터
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPodMetrics(Object response, String metricType) {
        Map<String, Object> podMetrics = new HashMap<>();

        try {
            if (response == null) {
                return podMetrics;
            }

            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && "vector".equals(data.get("resultType"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");

                if (result != null) {
                    for (Map<String, Object> item : result) {
                        Map<String, Object> metric = (Map<String, Object>) item.get("metric");
                        List<Object> value = (List<Object>) item.get("value");

                        if (metric != null && value != null && value.size() >= 2) {
                            String pod = (String) metric.get("pod");
                            if (pod != null) {
                                Object podValue = value.get(1);
                                podMetrics.put(pod + "_" + metricType, podValue);
                            }
                        }
                    }
                }
            }

        } catch (ClassCastException e) {
            log.warn("파드별 메트릭 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("파드별 메트릭 추출 실패: {}", e.getMessage());
        }

        return podMetrics;
    }


    /**
     * GPU 노드별 데이터를 노드별로 그룹화하는 메서드
     *
     * @param cpuRequestsResponse    CPU 할당량 응답
     * @param cpuLimitsResponse      CPU 제한량 응답
     * @param cpuUsageResponse       CPU 사용량 응답
     * @param memoryRequestsResponse Memory 할당량 응답
     * @param memoryLimitsResponse   Memory 제한량 응답
     * @param memoryUsageResponse    Memory 사용량 응답
     * @param gpuRequestsResponse    GPU 할당량 응답
     * @param gpuLimitsResponse      GPU 제한량 응답
     * @param gpuUsageResponse       GPU 사용량 응답
     * @return 노드별로 그룹화된 데이터
     */
    /**
     * Prometheus 응답에서 노드별 데이터를 추출하는 메서드
     *
     * @param response Prometheus 응답 데이터
     * @return 노드별 데이터 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractNodeData(Object response) {
        Map<String, Object> nodeData = new HashMap<>();

        try {
            if (response == null) {
                return nodeData;
            }

            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && "vector".equals(data.get("resultType"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");

                if (result != null) {
                    for (Map<String, Object> item : result) {
                        Map<String, Object> metric = (Map<String, Object>) item.get("metric");
                        List<Object> value = (List<Object>) item.get("value");

                        if (metric != null && value != null && value.size() >= 2) {
                            String node = resolveMetricNodeIdentifier(metric);
                            if (node != null) {
                                // value의 두 번째 요소(인덱스 1)가 실제 값
                                Object nodeValue = value.get(1);
                                nodeData.put(node, nodeValue);
                            }
                        }
                    }
                }
            }

        } catch (ClassCastException e) {
            log.warn("노드 데이터 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("노드 데이터 추출 실패: {}", e.getMessage());
        }

        return nodeData;
    }

    /**
     * Prometheus 응답에서 instance/display_name 매핑을 추출
     * (현재 display_name을 직접 키로 사용하므로 미사용)
     */
    /*
     * @SuppressWarnings("unchecked")
     * private Map<String, String> extractInstanceDisplayNameMap(Object response) {
     * Map<String, String> map = new HashMap<>();
     * try {
     * if (response == null) {
     * return map;
     * }
     * Map<String, Object> responseMap = (Map<String, Object>) response;
     * Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
     * if (data != null && "vector".equals(data.get("resultType"))) {
     * List<Map<String, Object>> result = (List<Map<String, Object>>)
     * data.get("result");
     * if (result != null) {
     * for (Map<String, Object> item : result) {
     * Map<String, Object> metric = (Map<String, Object>) item.get("metric");
     * if (metric != null) {
     * String key = (String) metric.get("node");
     * if (key == null) {
     * key = (String) metric.get("instance");
     * }
     * Object dn = metric.get("display_name");
     * if (key != null && dn != null) {
     * map.put(key, String.valueOf(dn));
     * }
     * }
     * }
     * }
     * }
     * } catch (ClassCastException e) {
     * log.debug("display_name 추출 실패 (ClassCastException): {}", e.getMessage());
     * } catch (RuntimeException e) {
     * log.debug("display_name 추출 실패: {}", e.getMessage());
     * }
     * return map;
     * }
     */

    /**
     * Prometheus vector 응답에서 session_id만 추출하여 문자열 리스트로 반환
     * value는 사용하지 않음
     */
    @SuppressWarnings("unchecked")
    private List<String> extractSessionIdList(Object response) {
        List<String> list = new ArrayList<>();
        try {
            if (response == null) {
                return list;
            }
            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            if (data != null && "vector".equals(data.get("resultType"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");
                if (result != null) {
                    for (Map<String, Object> item : result) {
                        Map<String, Object> metric = (Map<String, Object>) item.get("metric");
                        if (metric != null) {
                            String sessionId = (String) metric.get("session_id");
                            if (sessionId != null && !sessionId.trim().isEmpty()) {
                                list.add(sessionId);
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.debug("session_id 리스트 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("session_id 리스트 추출 실패: {}", e.getMessage());
        }
        return list;
    }

    /**
     * Object에서 Double 값 추출
     *
     * @param value 변환할 값
     * @return Double 값
     */
    private Double getDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        return 0.0;
    }

    @Override
    public Map<String, Object> getSolutionDetailResources(String nameSpace, String podName, String fromDate,
                                                          String toDate, String durationParam) {
        log.info("솔루션별 상세 자원 현황 조회 시작: nameSpace={}, podName={}, fromDate={}, toDate={}, duration={}", nameSpace,
                podName, fromDate, toDate, durationParam);

        try {
            Map<String, Object> solutionDetailData = new HashMap<>();

            log.info("날짜 파라미터: fromDate={}, toDate={}, step={}", fromDate, toDate, durationParam);

            // 0. from ~ to 기간 해당 네임스페이스 전체 cpu, memory 각각 평균 사용률 - 요청량 대비 사용률, 상한량 대비 사용률
            // 조회
            // 시간 범위를 Prometheus duration 형식으로 변환 (예: "6h", "1d", "2d12h")
            String durationRange = calculatePrometheusDuration(fromDate, toDate);
            log.info("계산된 Prometheus duration: {} (fromDate: {}, toDate: {})", durationRange, fromDate, toDate);

            Object cpuRequestUsageRateResponse = null;
            Object cpuLimitUsageRateResponse = null;
            Object memoryRequestUsageRateResponse = null;
            Object memoryLimitUsageRateResponse = null;

            try {
                // podName 여부에 따라 쿼리 선택
                boolean hasPodName = podName != null && !podName.trim().isEmpty();

                // CPU 요청량 대비 사용률 (전체 기간 평균 - 단일값)
                String cpuRequestUsageRateQuery =
                        String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_CPU_REQUEST_AVG_RATE.getQuery()
                                , nameSpace, podName, durationRange, nameSpace, podName);

                cpuRequestUsageRateResponse = resrcMgmtClient.executeQuery(cpuRequestUsageRateQuery);
                log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@cpuRequestUsageRateQuery: {}", cpuRequestUsageRateQuery);
                // CPU 상한량 대비 사용률 (전체 기간 평균 - 단일값)
                String cpuLimitUsageRateQuery =
                        String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_CPU_LIMIT_AVG_RATE.getQuery(),
                                nameSpace, podName, durationRange, nameSpace, podName);
                cpuLimitUsageRateResponse = resrcMgmtClient.executeQuery(cpuLimitUsageRateQuery);
                log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@cpuLimitUsageRateQuery: {}", cpuLimitUsageRateQuery);
                // Memory 요청량 대비 사용률 (전체 기간 평균 - 단일값)
                String memoryRequestUsageRateQuery = hasPodName
                        ? String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_MEMORY_REQUEST_AVG_RATE.getQuery(),
                        nameSpace, podName, nameSpace, podName, durationRange)
                        : String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_NS_MEMORY_REQUEST_AVG_RATE.getQuery(),
                        nameSpace, nameSpace, durationRange);
                memoryRequestUsageRateResponse = resrcMgmtClient.executeQuery(memoryRequestUsageRateQuery);

                // Memory 상한량 대비 사용률 (전체 기간 평균 - 단일값)
                String memoryLimitUsageRateQuery = hasPodName
                        ? String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_MEMORY_LIMIT_AVG_RATE.getQuery(),
                        nameSpace, podName, nameSpace, podName, durationRange)
                        : String.format(ResrcMgmtQueryEnum.SOLUTION_DETAIL_NS_MEMORY_LIMIT_AVG_RATE.getQuery(),
                        nameSpace, nameSpace, durationRange);
                memoryLimitUsageRateResponse = resrcMgmtClient.executeQuery(memoryLimitUsageRateQuery);

                log.info("평균 사용률 조회 완료 (단일값) - namespace: {}, podName: {}, duration: {}",
                        nameSpace, hasPodName ? podName : "전체", durationRange);
            } catch (BusinessException e) {
                log.warn("평균 사용률 조회 실패 (BusinessException) - namespace: {}, podName: {}: {}", nameSpace, podName,
                        e.getMessage());
            } catch (FeignException e) {
                log.warn("평균 사용률 조회 실패 (FeignException) - namespace: {}, podName: {}: {}", nameSpace, podName,
                        e.getMessage());
            } catch (RuntimeException e) {
                log.warn("평균 사용률 조회 실패 (RuntimeException) - namespace: {}, podName: {}: {}", nameSpace, podName,
                        e.getMessage());
            }

            // 1. CPU 사용량 그래프 쿼리 (시계열 데이터)
            Object cpuUsageGraphResponse = null;
            try {
                String cpuUsageGraphQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_CPU_USAGE_GRAPH.getQuery(),
                        nameSpace);
                log.info("CPU 사용량 그래프 쿼리: query={}, start={}, end={}, step={}",
                        cpuUsageGraphQuery, fromDate, toDate, PROMETHEUS_STEP);
                cpuUsageGraphResponse = resrcMgmtClient.executeQueryRange(cpuUsageGraphQuery, fromDate, toDate,
                        PROMETHEUS_STEP);
                log.info("네임스페이스 CPU 사용량 그래프 조회 완료: {}", nameSpace);
                // log.debug("CPU 그래프 response: {}", cpuUsageGraphResponse);
            } catch (BusinessException e) {
                log.error("네임스페이스 CPU 사용량 그래프 조회 실패 (BusinessException): {}", e.getMessage(), e);
            } catch (FeignException e) {
                log.error("네임스페이스 CPU 사용량 그래프 조회 실패 (FeignException): {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error("네임스페이스 CPU 사용량 그래프 조회 실패 (RuntimeException): {}", e.getMessage(), e);
            }

            // 6. Pod별 CPU 그리드 데이터
            // 6-1. Pod별 CPU 요청량
            Object podCpuRequestsResponse = null;
            try {
                String podCpuRequestsQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_CPU_REQUESTS.getQuery(),
                        nameSpace);
                podCpuRequestsResponse = resrcMgmtClient.executeQuery(podCpuRequestsQuery);
                log.info("Pod별 CPU 요청량 조회 완료: {}", nameSpace);
            } catch (BusinessException e) {
                log.warn("Pod별 CPU 요청량 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Pod별 CPU 요청량 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Pod별 CPU 요청량 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 6-2. Pod별 CPU 할당량
            Object podCpuLimitsResponse = null;
            try {
                String podCpuLimitsQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_CPU_LIMITS.getQuery(),
                        nameSpace);
                podCpuLimitsResponse = resrcMgmtClient.executeQuery(podCpuLimitsQuery);
                log.info("Pod별 CPU 할당량 조회 완료: {}", nameSpace);
            } catch (BusinessException e) {
                log.warn("Pod별 CPU 할당량 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Pod별 CPU 할당량 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Pod별 CPU 할당량 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 6-3. Pod별 CPU 실제 사용량
            Object podCpuUsageResponse = null;
            try {
                String podCpuUsageQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_CPU_USAGE.getQuery(),
                        nameSpace);
                log.info("Pod별 CPU 실제 사용량 쿼리 실행: {}", podCpuUsageQuery);
                podCpuUsageResponse = resrcMgmtClient.executeQuery(podCpuUsageQuery);
                log.info("Pod별 CPU 실제 사용량 조회 완료: {}", nameSpace);
                // log.debug("CPU 실제 사용량 response: {}", podCpuUsageResponse);
            } catch (BusinessException e) {
                log.error("Pod별 CPU 실제 사용량 조회 실패 (BusinessException): {}", e.getMessage(), e);
            } catch (FeignException e) {
                log.error("Pod별 CPU 실제 사용량 조회 실패 (FeignException): {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error("Pod별 CPU 실제 사용량 조회 실패 (RuntimeException): {}", e.getMessage(), e);
            }

            // 7. 메모리 사용량 그래프 (시계열, Pod별)
            Object memoryUsageGraphResponse = null;
            try {
                String memoryUsageGraphQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_MEMORY_USAGE_GRAPH.getQuery(),
                        nameSpace);
                log.info("메모리 사용량 그래프 쿼리: query={}, start={}, end={}, step={}",
                        memoryUsageGraphQuery, fromDate, toDate, PROMETHEUS_STEP);
                memoryUsageGraphResponse = resrcMgmtClient.executeQueryRange(memoryUsageGraphQuery, fromDate, toDate,
                        PROMETHEUS_STEP);
                log.info("네임스페이스 메모리 사용량 그래프 조회 완료: {}", nameSpace);
                // log.debug("메모리 그래프 response: {}", memoryUsageGraphResponse);
            } catch (BusinessException e) {
                log.error("네임스페이스 메모리 사용량 그래프 조회 실패 (BusinessException): {}", e.getMessage(), e);
            } catch (FeignException e) {
                log.error("네임스페이스 메모리 사용량 그래프 조회 실패 (FeignException): {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error("네임스페이스 메모리 사용량 그래프 조회 실패 (RuntimeException): {}", e.getMessage(), e);
            }

            // 8. Pod별 메모리 그리드 데이터
            // 8-1. Pod별 메모리 요청량
            Object podMemoryRequestsResponse = null;
            try {
                String podMemoryRequestsQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_MEMORY_REQUESTS.getQuery(),
                        nameSpace);
                podMemoryRequestsResponse = resrcMgmtClient.executeQuery(podMemoryRequestsQuery);
                log.info("Pod별 메모리 요청량 조회 완료: {}", nameSpace);
            } catch (BusinessException e) {
                log.warn("Pod별 메모리 요청량 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Pod별 메모리 요청량 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Pod별 메모리 요청량 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 8-2. Pod별 메모리 할당량
            Object podMemoryLimitsResponse = null;
            try {
                String podMemoryLimitsQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_MEMORY_LIMITS.getQuery(),
                        nameSpace);
                podMemoryLimitsResponse = resrcMgmtClient.executeQuery(podMemoryLimitsQuery);
                log.info("Pod별 메모리 할당량 조회 완료: {}", nameSpace);
            } catch (BusinessException e) {
                log.warn("Pod별 메모리 할당량 조회 실패 (BusinessException): {}", e.getMessage());
            } catch (FeignException e) {
                log.warn("Pod별 메모리 할당량 조회 실패 (FeignException): {}", e.getMessage());
            } catch (RuntimeException e) {
                log.warn("Pod별 메모리 할당량 조회 실패 (RuntimeException): {}", e.getMessage());
            }

            // 8-3. Pod별 메모리 실제 사용량
            Object podMemoryUsageResponse = null;
            try {
                String podMemoryUsageQuery = String.format(
                        ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_MEMORY_USAGE.getQuery(),
                        nameSpace);
                log.info("Pod별 메모리 실제 사용량 쿼리 실행: {}", podMemoryUsageQuery);
                podMemoryUsageResponse = resrcMgmtClient.executeQuery(podMemoryUsageQuery);
                log.info("Pod별 메모리 실제 사용량 조회 완료: {}", nameSpace);
                // log.debug("메모리 실제 사용량 response: {}", podMemoryUsageResponse);
            } catch (BusinessException e) {
                log.error("Pod별 메모리 실제 사용량 조회 실패 (BusinessException): {}", e.getMessage(), e);
            } catch (FeignException e) {
                log.error("Pod별 메모리 실제 사용량 조회 실패 (FeignException): {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error("Pod별 메모리 실제 사용량 조회 실패 (RuntimeException): {}", e.getMessage(), e);
            }

            // CPU 사용량 그래프 데이터 처리 (Pod별 시계열)
            Map<String, Object> cpuGraphData = processTimeSeriesGraphData(cpuUsageGraphResponse);

            // 메모리 사용량 그래프 데이터 처리 (Pod별 시계열)
            Map<String, Object> memoryGraphData = processTimeSeriesGraphData(memoryUsageGraphResponse);

            // Pod별 CPU 그리드 데이터 처리
            List<Map<String, Object>> podCpuGridData = processPodCpuGridData(
                    podCpuRequestsResponse,
                    podCpuLimitsResponse,
                    podCpuUsageResponse);

            // Pod별 메모리 그리드 데이터 처리
            List<Map<String, Object>> podMemoryGridData = processPodMemoryGridData(
                    podMemoryRequestsResponse,
                    podMemoryLimitsResponse,
                    podMemoryUsageResponse);

            // 사용률 데이터 처리 (네임스페이스 통합 값 - 단일값)
            Map<String, Object> usageRates = new HashMap<>();
            usageRates.put("cpuRequestUsageRate", extractValueFromPrometheusResponse(cpuRequestUsageRateResponse));
            usageRates.put("cpuLimitUsageRate", extractValueFromPrometheusResponse(cpuLimitUsageRateResponse));
            usageRates.put("memoryRequestUsageRate",
                    extractValueFromPrometheusResponse(memoryRequestUsageRateResponse));
            usageRates.put("memoryLimitUsageRate", extractValueFromPrometheusResponse(memoryLimitUsageRateResponse));

            // 응답 데이터 구성

            solutionDetailData.put("nameSpace", nameSpace);
            solutionDetailData.put("podName", podName);
            solutionDetailData.put("fromDate", fromDate);
            solutionDetailData.put("toDate", toDate);
            solutionDetailData.put("step", PROMETHEUS_STEP);
            solutionDetailData.put("cpuGraph", cpuGraphData); // 처리된 시계열 데이터
            solutionDetailData.put("memoryGraph", memoryGraphData); // 처리된 시계열 데이터
            solutionDetailData.put("usageRates", usageRates);
            solutionDetailData.put("podCpuGrid", podCpuGridData); // Pod별 CPU 그리드
            solutionDetailData.put("podMemoryGrid", podMemoryGridData); // Pod별 메모리 그리드

            log.info("솔루션 상세 데이터 구성 완료 - CPU Grid: {} pods, Memory Grid: {} pods",
                    podCpuGridData.size(), podMemoryGridData.size());

            return solutionDetailData;

        } catch (FeignException e) {
            log.error("솔루션별 상세 자원 현황 조회 실패 (FeignException): HTTP {}, {}", e.status(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "솔루션별 상세 자원 현황 조회 중 외부 API 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("솔루션별 상세 자원 현황 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "솔루션별 상세 자원 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // /**
    // * 솔루션 시계열 데이터를 Pod별로 그룹화
    // */
    // private Map<String, Object> groupSolutionTimeSeriesDataByPod(
    // Object cpuUsageGraphResponse,
    // Object cpuRequestUsageRateResponse,
    // Object cpuLimitUsageRateResponse,
    // Object memoryRequestUsageRateResponse,
    // Object memoryLimitUsageRateResponse,
    // String podName) {

    // Map<String, Object> result = new HashMap<>();

    // // 1. CPU 사용량 그래프 데이터
    // result.put("cpuUsageGraph", extractTimeSeriesData(cpuUsageGraphResponse,
    // podName));

    // // 2. CPU 요청량 대비 사용률
    // result.put("cpuRequestUsageRate",
    // extractTimeSeriesData(cpuRequestUsageRateResponse, podName));

    // // 3. CPU 할당량 대비 사용률
    // result.put("cpuLimitUsageRate",
    // extractTimeSeriesData(cpuLimitUsageRateResponse, podName));

    // // 4. 메모리 요청량 대비 사용률
    // result.put("memoryRequestUsageRate",
    // extractTimeSeriesData(memoryRequestUsageRateResponse, podName));

    // // 5. 메모리 할당량 대비 사용률
    // result.put("memoryLimitUsageRate",
    // extractTimeSeriesData(memoryLimitUsageRateResponse, podName));

    // return result;
    // }

    /**
     * Prometheus 응답에서 메트릭 데이터 추출
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractMetricDataFromResponse(Map<String, Object> response) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data.containsKey("result")) {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");
                    for (Map<String, Object> metric : results) {
                        Map<String, Object> extracted = new HashMap<>();

                        // metric 정보 추출
                        if (metric.containsKey("metric")) {
                            Map<String, Object> metricInfo = (Map<String, Object>) metric.get("metric");
                            extracted.putAll(metricInfo);
                        }

                        // value 정보 추출
                        if (metric.containsKey("value")) {
                            List<Object> valueArray = (List<Object>) metric.get("value");
                            if (valueArray.size() > 1) {
                                extracted.put("value", valueArray.get(1));
                            }
                        }

                        result.add(extracted);
                    }
                }
            }
        } catch (ClassCastException e) {
            log.warn("메트릭 데이터 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("메트릭 데이터 추출 실패: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Pod별 CPU 그리드 데이터 처리
     * <p>
     * 반환 데이터 구조:
     * [
     * {
     * "podName": "pod-1", // a. Pod명
     * "cpuLimits": 1.0, // b. CPU 할당량 (Limits)
     * "cpuRequests": 0.5, // c. CPU 요청량 (Requests)
     * "cpuUsage": 0.3, // d. CPU 실제 사용량
     * "cpuRequestUsageRate": 60.0, // e. CPU 요청량 대비 사용률 (%)
     * "cpuLimitUsageRate": 30.0 // f. CPU 할당량 대비 사용률 (%)
     * }
     * ]
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> processPodCpuGridData(
            Object cpuRequestsResponse,
            Object cpuLimitsResponse,
            Object cpuUsageResponse) {

        List<Map<String, Object>> gridData = new ArrayList<>();
        Map<String, Map<String, Object>> podDataMap = new HashMap<>();

        try {
            // 1. Pod별 CPU 실제 사용량 처리 (먼저 처리해서 모든 Pod를 기준으로 설정)
            if (cpuUsageResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) cpuUsageResponse;
                List<Map<String, Object>> usageData = extractMetricDataFromResponse(responseMap);
                log.debug("CPU 사용량 데이터에서 {} 개의 Pod 발견", usageData.size());
                for (Map<String, Object> data : usageData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("cpuUsage", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 2. Pod별 CPU 요청량 처리 (있으면 추가)
            if (cpuRequestsResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) cpuRequestsResponse;
                List<Map<String, Object>> requestsData = extractMetricDataFromResponse(responseMap);
                log.debug("CPU 요청량 데이터에서 {} 개의 Pod 발견", requestsData.size());
                for (Map<String, Object> data : requestsData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("cpuRequests", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 3. Pod별 CPU 할당량 처리 (있으면 추가)
            if (cpuLimitsResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) cpuLimitsResponse;
                List<Map<String, Object>> limitsData = extractMetricDataFromResponse(responseMap);
                log.debug("CPU 할당량 데이터에서 {} 개의 Pod 발견", limitsData.size());
                for (Map<String, Object> data : limitsData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("cpuLimits", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 4. 사용률 계산 및 그리드 데이터 생성
            for (Map.Entry<String, Map<String, Object>> entry : podDataMap.entrySet()) {
                Map<String, Object> podData = entry.getValue();

                Double cpuRequests = (Double) podData.get("cpuRequests");
                Double cpuLimits = (Double) podData.get("cpuLimits");
                Double cpuUsage = (Double) podData.get("cpuUsage");

                // e. CPU 요청량 대비 사용률 계산 (cpuUsage / cpuRequests * 100)
                if (cpuRequests != null && cpuRequests > 0 && cpuUsage != null) {
                    double requestUsageRate = (cpuUsage / cpuRequests) * 100;
                    podData.put("cpuRequestUsageRate", Math.round(requestUsageRate * 100.0) / 100.0);
                } else {
                    podData.put("cpuRequestUsageRate", 0.0);
                }

                // f. CPU 할당량 대비 사용률 계산 (cpuUsage / cpuLimits * 100)
                if (cpuLimits != null && cpuLimits > 0 && cpuUsage != null) {
                    double limitUsageRate = (cpuUsage / cpuLimits) * 100;
                    podData.put("cpuLimitUsageRate", Math.round(limitUsageRate * 100.0) / 100.0);
                } else {
                    podData.put("cpuLimitUsageRate", 0.0);
                }

                // 기본값 설정
                podData.putIfAbsent("cpuRequests", 0.0);
                podData.putIfAbsent("cpuLimits", 0.0);
                podData.putIfAbsent("cpuUsage", 0.0);

                gridData.add(podData);
            }

            log.info("Pod별 CPU 그리드 데이터 처리 완료: {} pods", gridData.size());

        } catch (ClassCastException e) {
            log.error("Pod별 CPU 그리드 데이터 처리 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Pod별 CPU 그리드 데이터 처리 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("Pod별 CPU 그리드 데이터 처리 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Pod별 CPU 그리드 데이터 처리 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return gridData;
    }

    /**
     * 문자열 값을 Double로 변환
     */
    private Double parseDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (NumberFormatException e) {
            log.debug("Double 변환 실패 (NumberFormatException): {} - {}", value, e.getMessage());
        } catch (ClassCastException e) {
            log.debug("Double 변환 실패 (ClassCastException): {} - {}", value, e.getMessage());
        }

        return 0.0;
    }

    /**
     * 문자열 값을 Long으로 변환 (timestamp 변환용)
     */
    private Long parseLongValue(Object value) {
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        } catch (NumberFormatException e) {
            log.debug("Long 변환 실패 (NumberFormatException): {} - {}", value, e.getMessage());
        } catch (ClassCastException e) {
            log.debug("Long 변환 실패 (ClassCastException): {} - {}", value, e.getMessage());
        }

        return null;
    }

    /**
     * 시계열 그래프 데이터 처리 (Pod별)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> processTimeSeriesGraphData(Object graphResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (graphResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) graphResponse;
                if (responseMap.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                    if (data.containsKey("result")) {
                        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");

                        for (Map<String, Object> metric : results) {
                            if (metric.containsKey("metric") && metric.containsKey("values")) {
                                Map<String, Object> metricInfo = (Map<String, Object>) metric.get("metric");
                                String podName = (String) metricInfo.get("pod");

                                if (podName != null) {
                                    // Prometheus values를 숫자로 변환 (ApexCharts 호환)
                                    List<List<Object>> rawValues = (List<List<Object>>) metric.get("values");
                                    List<List<Object>> convertedValues = new ArrayList<>();

                                    for (List<Object> valueArray : rawValues) {
                                        if (valueArray.size() >= 2) {
                                            // Timestamp를 밀리초 단위로 변환 (ApexCharts는 밀리초 단위 사용)
                                            Long timestamp = parseLongValue(valueArray.get(0));
                                            if (timestamp != null) {
                                                timestamp = timestamp * 1000; // 초 -> 밀리초 변환
                                            }

                                            // 값을 숫자로 변환
                                            Double value = parseDoubleValue(valueArray.get(1));

                                            // NaN, Infinity 체크 및 변환
                                            if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
                                                value = 0.0;
                                            }

                                            List<Object> convertedValue = new ArrayList<>();
                                            convertedValue.add(timestamp != null ? timestamp : 0L);
                                            convertedValue.add(value);
                                            convertedValues.add(convertedValue);
                                        }
                                    }

                                    result.put(podName, convertedValues);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.error("시계열 그래프 데이터 처리 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("시계열 그래프 데이터 처리 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("시계열 그래프 데이터 처리 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("시계열 그래프 데이터 처리 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 단일 값 추출 (네임스페이스 통합 값)
     */
    @SuppressWarnings("unchecked")
    private Object extractSingleValue(Object response) {
        try {
            if (response == null) {
                return 0.0;
            }

            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;
                if (responseMap.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                    if (data.containsKey("result")) {
                        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("result");

                        if (!results.isEmpty()) {
                            Map<String, Object> firstResult = results.get(0);
                            if (firstResult.containsKey("value")) {
                                List<Object> valueArray = (List<Object>) firstResult.get("value");
                                if (valueArray.size() > 1) {
                                    return parseDoubleValue(valueArray.get(1));
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.warn("단일 값 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("단일 값 추출 실패 (IllegalArgumentException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.warn("단일 값 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("단일 값 추출 실패 (RuntimeException): {}", e.getMessage());
        }

        return 0.0;
    }

    /**
     * 에이전트 파드별 자원 데이터를 합산하여 전체 에이전트 리소스 계산
     *
     * @param agentPodData 파드별 자원 데이터 (getPortalAgentPodResources()의 반환값)
     * @return 합산된 에이전트 리소스 (cpu_usage, cpu_request, memory_usage, memory_request)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Double> aggregateAgentPodResources(Map<String, Object> agentPodData) {
        Map<String, Double> aggregated = new HashMap<>();
        aggregated.put("cpu_usage", 0.0);
        aggregated.put("cpu_request", 0.0);
        aggregated.put("cpu_limit", 0.0);
        aggregated.put("memory_usage", 0.0);
        aggregated.put("memory_request", 0.0);
        aggregated.put("memory_limit", 0.0);

        try {
            if (agentPodData == null) {
                log.warn("에이전트 파드 데이터가 null입니다.");
                return aggregated;
            }

            Object podsObj = agentPodData.get("pods");
            if (podsObj == null || !(podsObj instanceof List)) {
                log.warn("에이전트 파드 목록이 없거나 유효하지 않습니다.");
                return aggregated;
            }

            List<Map<String, Object>> pods = (List<Map<String, Object>>) podsObj;

            for (Map<String, Object> pod : pods) {
                if (pod == null) {
                    continue;
                }

                String podName = (String) pod.get("pod_name");

                // CPU 사용량 합산
                Object cpuUsageObj = pod.get("cpu_usage");
                if (cpuUsageObj != null) {
                    Double cpuUsage = parseDoubleValue(cpuUsageObj);
                    if (cpuUsage != null) {
                        aggregated.put("cpu_usage", aggregated.get("cpu_usage") + cpuUsage);
                    }
                }

                // CPU 요청량 합산
                Object cpuRequestObj = pod.get("cpu_request");
                if (cpuRequestObj != null) {
                    Double cpuRequest = parseDoubleValue(cpuRequestObj);
                    if (cpuRequest != null) {
                        log.debug("파드별 CPU 요청량 합산 - pod_name: {}, cpu_request 원본값: {}, 변환값: {}, 누적합: {}",
                                podName, cpuRequestObj, cpuRequest, aggregated.get("cpu_request") + cpuRequest);
                        aggregated.put("cpu_request", aggregated.get("cpu_request") + cpuRequest);
                    }
                } else {
                    log.debug("파드별 CPU 요청량 없음 - pod_name: {}", podName);
                }

                // CPU 제한량 합산
                Object cpuLimitObj = pod.get("cpu_limit");
                if (cpuLimitObj != null) {
                    Double cpuLimit = parseDoubleValue(cpuLimitObj);
                    if (cpuLimit != null) {
                        aggregated.put("cpu_limit", aggregated.get("cpu_limit") + cpuLimit);
                    }
                }

                // Memory 사용량 합산
                Object memoryUsageObj = pod.get("memory_usage");
                if (memoryUsageObj != null) {
                    Double memoryUsage = parseDoubleValue(memoryUsageObj);
                    if (memoryUsage != null) {
                        aggregated.put("memory_usage", aggregated.get("memory_usage") + memoryUsage);
                    }
                }

                // Memory 요청량 합산
                Object memoryRequestObj = pod.get("memory_request");
                if (memoryRequestObj != null) {
                    Double memoryRequest = parseDoubleValue(memoryRequestObj);
                    if (memoryRequest != null) {
                        aggregated.put("memory_request", aggregated.get("memory_request") + memoryRequest);
                    }
                }

                // Memory 제한량 합산
                Object memoryLimitObj = pod.get("memory_limit");
                if (memoryLimitObj != null) {
                    Double memoryLimit = parseDoubleValue(memoryLimitObj);
                    if (memoryLimit != null) {
                        aggregated.put("memory_limit", aggregated.get("memory_limit") + memoryLimit);
                    }
                }
            }

            log.info(
                    "에이전트 파드별 자원 합산 완료 - 파드 수: {}, CPU 사용량: {}, CPU 요청량: {}, CPU 제한량: {}, Memory 사용량: {}, Memory 요청량:" +
                            " {}, Memory 제한량: {}",
                    pods.size(),
                    aggregated.get("cpu_usage"),
                    aggregated.get("cpu_request"),
                    aggregated.get("cpu_limit"),
                    aggregated.get("memory_usage"),
                    aggregated.get("memory_request"),
                    aggregated.get("memory_limit"));

        } catch (ClassCastException e) {
            log.error("에이전트 파드별 자원 합산 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("에이전트 파드별 자원 합산 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("에이전트 파드별 자원 합산 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("에이전트 파드별 자원 합산 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return aggregated;
    }

    /**
     * 단일 값을 Prometheus 응답 형식으로 변환
     *
     * @param value 변환할 값
     * @return Prometheus 응답 형식의 객체
     */
    private Object createSingleValueResponse(Double value) {
        if (value == null) {
            value = 0.0;
        }

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultItem = new HashMap<>();
        List<Object> valueArray = new ArrayList<>();

        // Prometheus 응답 형식: { "data": { "resultType": "vector", "result": [ { "value":
        // [timestamp, value] } ] } }
        valueArray.add(String.valueOf(System.currentTimeMillis() / 1000)); // timestamp (초 단위)
        valueArray.add(String.valueOf(value)); // value

        resultItem.put("value", valueArray);
        result.add(resultItem);

        data.put("resultType", "vector");
        data.put("result", result);

        response.put("data", data);

        return response;
    }

    /**
     * Pod별 메모리 그리드 데이터 처리
     * <p>
     * 반환 데이터 구조:
     * [
     * {
     * "podName": "pod-1", // a. Pod명
     * "memoryLimits": 2048.00, // b. 메모리 할당량 (Limits, MB)
     * "memoryRequests": 1024.00, // c. 메모리 요청량 (Requests, MB)
     * "memoryUsage": 819.20, // d. 메모리 실제 사용량 (MB)
     * "memoryRequestUsageRate": 80.0, // e. 메모리 요청량 대비 사용률 (%)
     * "memoryLimitUsageRate": 40.0 // f. 메모리 할당량 대비 사용률 (%)
     * }
     * ]
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> processPodMemoryGridData(
            Object memoryRequestsResponse,
            Object memoryLimitsResponse,
            Object memoryUsageResponse) {

        List<Map<String, Object>> gridData = new ArrayList<>();
        Map<String, Map<String, Object>> podDataMap = new HashMap<>();

        try {
            // 1. Pod별 메모리 실제 사용량 처리 (먼저 처리해서 모든 Pod를 기준으로 설정)
            if (memoryUsageResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) memoryUsageResponse;
                List<Map<String, Object>> usageData = extractMetricDataFromResponse(responseMap);
                log.debug("메모리 사용량 데이터에서 {} 개의 Pod 발견", usageData.size());
                for (Map<String, Object> data : usageData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("memoryUsage", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 2. Pod별 메모리 요청량 처리 (있으면 추가)
            if (memoryRequestsResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) memoryRequestsResponse;
                List<Map<String, Object>> requestsData = extractMetricDataFromResponse(responseMap);
                log.debug("메모리 요청량 데이터에서 {} 개의 Pod 발견", requestsData.size());
                for (Map<String, Object> data : requestsData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("memoryRequests", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 3. Pod별 메모리 할당량 처리 (있으면 추가)
            if (memoryLimitsResponse instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) memoryLimitsResponse;
                List<Map<String, Object>> limitsData = extractMetricDataFromResponse(responseMap);
                log.debug("메모리 할당량 데이터에서 {} 개의 Pod 발견", limitsData.size());
                for (Map<String, Object> data : limitsData) {
                    String podName = (String) data.get("pod");
                    if (podName != null) {
                        podDataMap.putIfAbsent(podName, new HashMap<>());
                        podDataMap.get(podName).put("podName", podName);
                        podDataMap.get(podName).put("memoryLimits", parseDoubleValue(data.get("value")));
                    }
                }
            }

            // 4. 사용률 계산 및 그리드 데이터 생성
            for (Map.Entry<String, Map<String, Object>> entry : podDataMap.entrySet()) {
                Map<String, Object> podData = entry.getValue();

                Double memoryRequestsBytes = (Double) podData.get("memoryRequests");
                Double memoryLimitsBytes = (Double) podData.get("memoryLimits");
                Double memoryUsageBytes = (Double) podData.get("memoryUsage");

                // 메모리 단위 변환: Bytes → MB (소수점 2자리까지)
                Double memoryRequests = memoryRequestsBytes != null
                        ? Math.round((memoryRequestsBytes / 1024.0 / 1024.0) * 100.0) / 100.0
                        : 0.0;
                Double memoryLimits = memoryLimitsBytes != null
                        ? Math.round((memoryLimitsBytes / 1024.0 / 1024.0) * 100.0) / 100.0
                        : 0.0;
                Double memoryUsage = memoryUsageBytes != null
                        ? Math.round((memoryUsageBytes / 1024.0 / 1024.0) * 100.0) / 100.0
                        : 0.0;

                // MB 단위로 변환된 값 저장
                podData.put("memoryRequests", memoryRequests);
                podData.put("memoryLimits", memoryLimits);
                podData.put("memoryUsage", memoryUsage);

                // e. 메모리 요청량 대비 사용률 계산 (memoryUsage / memoryRequests * 100)
                if (memoryRequests != null && memoryRequests > 0 && memoryUsage != null) {
                    double requestUsageRate = (memoryUsage / memoryRequests) * 100;
                    podData.put("memoryRequestUsageRate", Math.round(requestUsageRate * 100.0) / 100.0);
                } else {
                    podData.put("memoryRequestUsageRate", 0.0);
                }

                // f. 메모리 할당량 대비 사용률 계산 (memoryUsage / memoryLimits * 100)
                if (memoryLimits != null && memoryLimits > 0 && memoryUsage != null) {
                    double limitUsageRate = (memoryUsage / memoryLimits) * 100;
                    podData.put("memoryLimitUsageRate", Math.round(limitUsageRate * 100.0) / 100.0);
                } else {
                    podData.put("memoryLimitUsageRate", 0.0);
                }

                gridData.add(podData);
            }

            log.info("Pod별 메모리 그리드 데이터 처리 완료: {} pods", gridData.size());

        } catch (ClassCastException e) {
            log.error("Pod별 메모리 그리드 데이터 처리 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Pod별 메모리 그리드 데이터 처리 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("Pod별 메모리 그리드 데이터 처리 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Pod별 메모리 그리드 데이터 처리 실패 (RuntimeException): {}", e.getMessage(), e);
        }

        return gridData;
    }

    /**
     * 솔루션 데이터를 API Gateway와 동일한 형태로 구성
     */
    private Map<String, Object> createSimpleSolutionData(
            Double cpuRequests, Double cpuLimits, Double cpuUsage,
            Double memoryRequests, Double memoryLimits, Double memoryUsage) {

        Map<String, Object> solutionData = new HashMap<>();

        // CPU 데이터 (cores 단위로 변환, 소수점 4자리까지)
        solutionData.put("cpu_request", cpuRequests != null ? Math.round(cpuRequests * 10000.0) / 10000.0 : 0.0);
        solutionData.put("cpu_limit", cpuLimits != null ? Math.round(cpuLimits * 10000.0) / 10000.0 : 0.0);
        solutionData.put("cpu_usage", cpuUsage != null ? Math.round(cpuUsage * 10000.0) / 10000.0 : 0.0);

        // Memory 데이터 (MB 단위로 변환, 소수점 4자리까지)
        solutionData.put("memory_request",
                memoryRequests != null ? Math.round((memoryRequests / 1024.0 / 1024.0) * 10000.0) / 10000.0 : 0.0);
        solutionData.put("memory_limit",
                memoryLimits != null ? Math.round((memoryLimits / 1024.0 / 1024.0) * 10000.0) / 10000.0 : 0.0);
        solutionData.put("memory_usage",
                memoryUsage != null ? Math.round((memoryUsage / 1024.0 / 1024.0) * 10000.0) / 10000.0 : 0.0);

        return solutionData;
    }

    /**
     * Prometheus 응답에서 value 배열의 2번째 값(실제 값) 추출
     */
    @SuppressWarnings("unchecked")
    private Double extractValueFromPrometheusResponse(Object response) {
        if (response == null) {
            return 0.0;
        }

        try {
            // JSON 응답을 Map으로 파싱
            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && data.get("result") != null) {
                List<Object> results = (List<Object>) data.get("result");
                if (!results.isEmpty()) {
                    Map<String, Object> firstResult = (Map<String, Object>) results.get(0);
                    List<Object> valueArray = (List<Object>) firstResult.get("value");
                    if (valueArray != null && valueArray.size() > 1) {
                        String valueStr = valueArray.get(1).toString();
                        return Double.parseDouble(valueStr);
                    }
                }
            }
        } catch (ClassCastException e) {
            log.warn("Prometheus 응답에서 값 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("Prometheus 응답에서 값 추출 실패: {}", e.getMessage());
        }

        return 0.0;
    }

    /**
     * Prometheus 응답에서 파드 이름들을 추출하여 배열로 반환
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractPodNamesFromResponse(Object response) {
        List<Map<String, Object>> podList = new ArrayList<>();

        if (response == null) {
            return podList;
        }

        try {
            // JSON 응답을 Map으로 파싱
            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && data.get("result") != null) {
                List<Object> results = (List<Object>) data.get("result");

                for (Object resultObj : results) {
                    Map<String, Object> result = (Map<String, Object>) resultObj;
                    Map<String, Object> metric = (Map<String, Object>) result.get("metric");

                    if (metric != null && metric.get("pod") != null) {
                        Map<String, Object> podInfo = new HashMap<>();
                        podInfo.put("pod", metric.get("pod"));
                        podInfo.put("namespace", metric.get("namespace"));
                        podInfo.put("phase", metric.get("phase"));

                        // value 배열에서 값 추출
                        List<Object> valueArray = (List<Object>) result.get("value");
                        if (valueArray != null && valueArray.size() > 1) {
                            podInfo.put("value", valueArray.get(1).toString());
                            podInfo.put("timestamp", valueArray.get(0).toString());
                        }

                        podList.add(podInfo);
                    }
                }
            }
        } catch (ClassCastException e) {
            log.warn("Prometheus 응답에서 파드 이름 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("Prometheus 응답에서 파드 이름 추출 실패: {}", e.getMessage());
        }

        return podList;
    }

    @Override
    public Map<String, Object> getSolutionInfo(String nameSpace) {
        log.info("솔루션 정보 조회 시작: {}", nameSpace);

        Map<String, Object> solutionInfo = new HashMap<>();

        // 0. pod 개수 조회
        Object podCountResponse = null;
        try {
            String podCountQuery = String.format(
                    ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_COUNT.getQuery(), nameSpace);
            podCountResponse = resrcMgmtClient.executeQuery(podCountQuery);
            log.info("네임스페이스 Pod 개수 조회 완료: {}", nameSpace);
        } catch (BusinessException e) {
            log.warn("네임스페이스 Pod 개수 조회 실패 (BusinessException): {}", e.getMessage());
        } catch (FeignException e) {
            log.warn("네임스페이스 Pod 개수 조회 실패 (FeignException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("네임스페이스 Pod 개수 조회 실패 (RuntimeException): {}", e.getMessage());
        }

        // 0. pod 네임 조회
        Object podNameResponse = null;
        try {
            String podNameQuery = String.format(
                    ResrcMgmtQueryEnum.SOLUTION_DETAIL_POD_NAME.getQuery(), nameSpace);
            podNameResponse = resrcMgmtClient.executeQuery(podNameQuery);
            log.info("네임스페이스 Pod 이름 조회 완료: {}", nameSpace);
        } catch (BusinessException e) {
            log.warn("네임스페이스 Pod 이름 조회 실패 (BusinessException): {}", e.getMessage());
        } catch (FeignException e) {
            log.warn("네임스페이스 Pod 이름 조회 실패 (FeignException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("네임스페이스 Pod 이름 조회 실패 (RuntimeException): {}", e.getMessage());
        }

        solutionInfo.put("podCount", extractValueFromPrometheusResponse(podCountResponse));
        solutionInfo.put("podNames", extractPodNamesFromResponse(podNameResponse));

        return solutionInfo;
    }

    /**
     * Pod 정보에 에이전트 배포 정보 추가
     *
     * @param agentPodData Pod 데이터
     */
    @SuppressWarnings("unchecked")
    private void enrichAgentPodWithDeploymentInfo(Map<String, Object> agentPodData) {
        // Admin 모드로 모든 앱 목록 조회 (사용자별 필터링 방지)
        String adminUsername = "admin";
        try {
            AdminContext.setAdminMode(adminUsername);
            adminAuthService.ensureAdminToken();

            Object podsObj = agentPodData.get("pods");
            if (!(podsObj instanceof List)) {
                return;
            }

            List<Map<String, Object>> pods = (List<Map<String, Object>>) podsObj;
            if (pods.isEmpty()) {
                return;
            }

            Map<String, String> projectNameCache = new HashMap<>();

            // 에이전트 앱 목록 조회 (Admin 모드로 모든 앱 조회)
            PageResponse<AgentAppRes> agentAppsResponse = agentDeployService.getAgentAppList("all", 1, 1000,
                    "created_at,desc", "", "");

            if (agentAppsResponse == null || agentAppsResponse.getContent() == null) {
                log.warn("에이전트 앱 목록 조회 실패");
                return;
            }


            // 각 pod에 대해 처리 (Iterator를 사용하여 안전하게 제거)
            Iterator<Map<String, Object>> podIterator = pods.iterator();

            while (podIterator.hasNext()) {
                Map<String, Object> pod = podIterator.next();
                String podName = (String) pod.get("pod_name");

                if (podName == null || !podName.startsWith("svc-")) {
                    continue;
                }

                // pod_id 추출: svc-86cf6a62-6a20-4f518832eb3b291e0c025390a2cf320a30-deplojk88h
                // -> 86cf6a62-6a20
                String podId = extractPodId(podName);
                pod.put("pod_id", podId);

                boolean deploymentFound = false;
                String deploymentId = null;
                String servingId = null;

                // 각 앱의 deployments에서 status가 Available이고 podId와 매칭되는 deployment의 정보 추출
                for (AgentAppRes app : agentAppsResponse.getContent()) {
                    try {
                        // 앱의 deployments 목록 확인
                        if (app.getDeployments() == null || app.getDeployments().isEmpty()) {
                            continue;
                        }

                        // status가 "Available"인 deployment 중 servingId가 podId를 포함하는 deployment 찾기
                        AgentAppRes.DeploymentInfo targetDeployment = app.getDeployments().stream()
                                .filter(deployment -> deployment != null
                                        && "Available".equals(deployment.getStatus())
                                        && deployment.getServingId() != null
                                        && deployment.getServingId().contains(podId))
                                .findFirst()
                                .orElse(null);

                        if (targetDeployment == null) {
                            continue;
                        }

                        servingId = targetDeployment.getServingId();
                        if (servingId != null && servingId.contains(podId)) {
                            // 매칭되는 deployment 발견
                            deploymentId = targetDeployment.getId();

                            // 배포명이 없는 경우 해당 pod 삭제
                            if (deploymentId == null || deploymentId.trim().isEmpty()) {
                                log.debug("Pod {} 삭제 - 배포명이 없음", podName);
                                podIterator.remove();
                                deploymentFound = false;
                                break;
                            }

                            pod.put("name", app.getName());
                            pod.put("builderName", app.getBuilderName());
                            pod.put("agentServingId", servingId);
                            pod.put("deploymentId", deploymentId);
                            pod.put("pod_version", targetDeployment.getVersion());

                            String projectName = projectNameCache.computeIfAbsent(app.getId(), sid -> {
                                try {
                                    AssetProjectInfoRes assetInfo = projectInfoService
                                            .getAssetProjectInfoByUuid(sid);
                                    if (assetInfo != null && assetInfo.getLstPrjNm() != null
                                            && !assetInfo.getLstPrjNm().isBlank()) {
                                        return assetInfo.getLstPrjNm();
                                    }
                                } catch (BusinessException ex) {
                                    log.debug(
                                            "에이전트 프로젝트 정보 조회 실패 (BusinessException) - servingId: {}, error: {}",
                                            sid, ex.getMessage());
                                } catch (FeignException ex) {
                                    log.debug("에이전트 프로젝트 정보 조회 실패 (FeignException) - servingId: {}, error: {}",
                                            sid, ex.getMessage());
                                } catch (RuntimeException ex) {
                                    log.debug(
                                            "에이전트 프로젝트 정보 조회 실패 (RuntimeException) - servingId: {}, error: {}",
                                            sid, ex.getMessage());
                                }
                                return null;
                            });

                            if (projectName != null) {
                                pod.put("lstPrjNm", projectName);
                            }

                            log.debug("Pod {} enriched with app: {}, builder: {}, deploymentId: {}, servingId: {}",
                                    podName, app.getName(), app.getBuilderName(), deploymentId, servingId);
                            deploymentFound = true;
                            break;
                        }
                    } catch (RuntimeException e) {
                        log.debug("앱 {}의 배포 정보 처리 실패 (RuntimeException): {}", app.getId(), e.getMessage());
                    }
                }

                // 배포명을 찾지 못한 경우 해당 pod 삭제
                if (!deploymentFound) {
                    log.debug("Pod {} 삭제 - 배포 정보를 찾을 수 없음", podName);
                    podIterator.remove();
                }
            }

        } catch (BusinessException e) {
            log.warn("Pod 정보 enrichment 실패 (BusinessException): {}", e.getMessage(), e);
        } catch (FeignException e) {
            log.warn("Pod 정보 enrichment 실패 (FeignException): {}", e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Pod 정보 enrichment 실패 (ClassCastException): {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.warn("Pod 정보 enrichment 실패 (NullPointerException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.warn("Pod 정보 enrichment 실패 (RuntimeException): {}", e.getMessage(), e);
        } finally {
            // Admin 모드 해제
            AdminContext.clear();
        }
    }

    /**
     * Pod 이름에서 Pod ID 추출
     *
     * @param podName Pod 이름 (예:
     *                svc-86cf6a62-6a20-4f518832eb3b291e0c025390a2cf320a30-deplojk88h)
     * @return Pod ID (예: 86cf6a62-6a20)
     */
    private String extractPodId(String podName) {
        // svc- 제거
        String withoutPrefix = podName.substring(4);

        // "-"로 split하고 첫 2개 부분만 사용
        String[] parts = withoutPrefix.split("-");
        if (parts.length >= 2) {
            return parts[0] + "-" + parts[1];
        }

        return withoutPrefix;
    }

    /**
     * 기본 자원 데이터 추가 (데이터가 없을 때)
     *
     * @param solutionInfo 솔루션 정보 맵
     */
    private void addDefaultResourceData(Map<String, Object> solutionInfo) {
        solutionInfo.put("cpu_request", 0.0);
        solutionInfo.put("cpu_limit", 0.0);
        solutionInfo.put("cpu_usage", 0.0);
        solutionInfo.put("memory_request", 0.0);
        solutionInfo.put("memory_limit", 0.0);
        solutionInfo.put("memory_usage", 0.0);
    }

    /**
     * fromDate와 toDate 차이를 Prometheus duration 형식으로 변환
     *
     * @param fromDate 시작 날짜 (yyyy-MM-dd)
     * @param toDate   종료 날짜 (yyyy-MM-dd)
     * @return Prometheus duration 형식 (예: "6h", "1d", "2d12h")
     */
    private String calculatePrometheusDuration(String fromDate, String toDate) {
        try {
            // 현재 시간 가져오기
            LocalDateTime now = LocalDateTime.now();

            // 날짜 문자열 정규화 (RFC3339 형식 지원)
            String normalizedFromDate = normalizeDateForDuration(fromDate);
            String normalizedToDate = normalizeDateForDuration(toDate);

            // ISO 8601 형식으로 파싱
            LocalDateTime start = parseDateTimeForDuration(normalizedFromDate, true);
            LocalDateTime parsedToDate = parseDateTimeForDuration(normalizedToDate, false);

            // fromDate가 현재 시간보다 크면 5m 반환
            if (start.isAfter(now)) {
                log.debug("Duration 계산: fromDate({})가 현재 시간({})보다 큼, 5m 반환", fromDate, now);
                return "5m";
            }

            // toDate와 현재 시간 중 작은 값을 end로 사용
            LocalDateTime end = parsedToDate.isBefore(now) ? parsedToDate : now;

            // 시간 차이 계산 (초 단위)
            long durationSeconds = java.time.Duration.between(start, end).getSeconds();

            // 음수인 경우 5m 반환
            if (durationSeconds <= 0) {
                log.debug("Duration 계산: start({}) >= end({}), 5m 반환", start, end);
                return "5m";
            }

            // Prometheus duration 형식으로 변환
            long days = durationSeconds / 86400;
            long hours = (durationSeconds % 86400) / 3600;
            long minutes = (durationSeconds % 3600) / 60;

            StringBuilder duration = new StringBuilder();
            if (days > 0) {
                duration.append(days).append("d");
            }
            if (hours > 0) {
                duration.append(hours).append("h");
            }
            if (minutes > 0 && days == 0) { // 분은 일(day)이 없을 때만 표시
                duration.append(minutes).append("m");
            }

            // 최소 5분으로 설정
            if (duration.length() == 0) {
                duration.append("5m");
            }

            log.debug("Duration 계산: fromDate={}, toDate={}, start={}, end={}, duration={} ({}초)",
                    fromDate, toDate, start, end, duration.toString(), durationSeconds);
            return duration.toString();

        } catch (java.time.format.DateTimeParseException e) {
            log.warn("Duration 계산 실패 (날짜 파싱 오류), 기본값 5m 사용: fromDate={}, toDate={}, error={}", fromDate, toDate,
                    e.getMessage());
            return "5m"; // 기본값
        } catch (RuntimeException e) {
            log.warn("Duration 계산 실패, 기본값 5m 사용: fromDate={}, toDate={}, error={}", fromDate, toDate, e.getMessage());
            return "5m"; // 기본값
        }
    }

    /**
     * 날짜 문자열을 duration 계산용으로 정규화
     *
     * @param dateString 날짜 문자열 (다양한 형식 지원)
     * @return 정규화된 날짜 문자열
     */
    private String normalizeDateForDuration(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return dateString;
        }

        // RFC3339 형식 (Z로 끝남) - Z 제거
        if (dateString.endsWith("Z")) {
            return dateString.substring(0, dateString.length() - 1);
        }

        // 이미 yyyy-MM-dd 형식인 경우 그대로 반환
        if (dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return dateString;
        }

        // T로 구분된 형식인 경우 날짜 부분만 추출
        if (dateString.contains("T")) {
            return dateString.split("T")[0];
        }

        // 공백으로 구분된 형식인 경우 날짜 부분만 추출
        if (dateString.contains(" ")) {
            return dateString.split(" ")[0];
        }

        return dateString;
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 파싱 (duration 계산용)
     *
     * @param dateString 날짜 문자열
     * @param isStart    시작 날짜 여부 (true면 00:00:00, false면 23:59:59)
     * @return LocalDateTime
     */
    private LocalDateTime parseDateTimeForDuration(String dateString, boolean isStart) {
        // yyyy-MM-dd 형식인 경우 시간 추가
        if (dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return LocalDateTime.parse(dateString + (isStart ? "T00:00:00" : "T23:59:59"));
        }

        // 이미 시간 정보가 포함된 경우
        try {
            // ISO 형식 파싱 시도
            if (dateString.contains("T")) {
                String[] parts = dateString.split("T");
                if (parts.length == 2) {
                    String timePart = parts[1];
                    if (timePart.length() <= 8) { // HH:mm:ss 형식
                        return LocalDateTime.parse(dateString);
                    } else {
                        // 초 이하 제거
                        String timeOnly = timePart.substring(0, 8);
                        return LocalDateTime.parse(parts[0] + "T" + timeOnly);
                    }
                }
            }

            // 기본 파싱 시도
            return LocalDateTime.parse(dateString);
        } catch (Exception e) {
            // 파싱 실패 시 날짜 부분만 추출하여 시간 추가
            String dateOnly = dateString.split("T")[0].split(" ")[0];
            return LocalDateTime.parse(dateOnly + (isStart ? "T00:00:00" : "T23:59:59"));
        }
    }

    /**
     * 솔루션 자원 데이터 조회 (공통 메서드)
     *
     * @param namespace    Kubernetes 네임스페이스
     * @param solutionName 솔루션명 (로깅용)
     * @return 솔루션 자원 데이터 (CPU, Memory)
     */
    private Map<String, Object> querySolutionResources(String namespace, String solutionName) {
        Object cpuRequestsResponse = null;
        Object cpuLimitsResponse = null;
        Object cpuUsageResponse = null;
        Object memoryRequestsResponse = null;
        Object memoryLimitsResponse = null;
        Object memoryUsageResponse = null;

        try {
            // 공통 쿼리를 사용하여 각 메트릭 조회
            cpuRequestsResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_REQUESTS.getQuery(), namespace));
            cpuLimitsResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_LIMITS.getQuery(), namespace));
            cpuUsageResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_CPU_USAGE_WITH_CONTAINER.getQuery(), namespace));
            memoryRequestsResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_REQUESTS.getQuery(), namespace));
            memoryLimitsResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_LIMITS.getQuery(), namespace));
            memoryUsageResponse = resrcMgmtClient.executeQuery(
                    String.format(ResrcMgmtQueryEnum.SOLUTION_MEMORY_USAGE_WITH_CONTAINER.getQuery(), namespace));

            log.info("{} 솔루션 데이터 조회 완료 (namespace: {})", solutionName, namespace);

            // 데이터가 하나라도 있으면 포맷하여 반환
            if (cpuRequestsResponse != null || cpuLimitsResponse != null || cpuUsageResponse != null ||
                    memoryRequestsResponse != null || memoryLimitsResponse != null || memoryUsageResponse != null) {

                return createSimpleSolutionData(
                        extractValueFromPrometheusResponse(cpuRequestsResponse),
                        extractValueFromPrometheusResponse(cpuLimitsResponse),
                        extractValueFromPrometheusResponse(cpuUsageResponse),
                        extractValueFromPrometheusResponse(memoryRequestsResponse),
                        extractValueFromPrometheusResponse(memoryLimitsResponse),
                        extractValueFromPrometheusResponse(memoryUsageResponse));
            }
        } catch (BusinessException e) {
            log.warn("{} 솔루션 데이터 조회 실패 (BusinessException) (namespace: {}): {}", solutionName, namespace,
                    e.getMessage());
        } catch (FeignException e) {
            log.warn("{} 솔루션 데이터 조회 실패 (FeignException) (namespace: {}): {}", solutionName, namespace, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("{} 솔루션 데이터 조회 실패 (RuntimeException) (namespace: {}): {}", solutionName, namespace,
                    e.getMessage());
        }

        return null;
    }

    /**
     * 특정 세션의 자원 데이터 조회
     *
     * @param sessionId   세션 ID
     * @param modelName   모델명
     * @param servingId   서빙 ID
     * @param status      상태
     * @param modelDeploy 모델 배포 정보 (자원 할당량/제한량 포함)
     * @return 세션 자원 데이터
     */
    private ResrcMgmtSessionResourceInfo getSessionResourceBySessionId(String sessionId, String modelName,
                                                                       String servingId, String status, GetModelDeployRes modelDeploy) {
        log.debug("세션 자원 데이터 조회 시작 - sessionId: {}, modelName: {}, servingId: {}", sessionId, modelName, servingId);

        // 기본값 설정 (GPU Prometheus 서버 접근 불가 시 사용)
        Double cpuUsage = 0.0;
        Double cpuRequest = 0.0;
        Double cpuUtilization = 0.0;
        Double memoryUsage = 0.0;
        Double memoryRequest = 0.0;
        Double gpuUsage = 0.0;
        Double gpuUtilization = 0.0;

        // 1. 자원 할당량과 제한량 설정 (serving ID로 조회한 GetModelDeployRes 객체에서 가져옴)
        cpuRequest = modelDeploy.getCpuRequest() != null ? modelDeploy.getCpuRequest().doubleValue() : 0.0;
        Double cpuLimit = modelDeploy.getCpuLimit() != null ? modelDeploy.getCpuLimit().doubleValue() : 0.0;
        memoryRequest = modelDeploy.getMemRequest() != null ? modelDeploy.getMemRequest().doubleValue() : 0.0;
        Double memoryLimit = modelDeploy.getMemLimit() != null ? modelDeploy.getMemLimit().doubleValue() : 0.0;
        Double gpuRequest = modelDeploy.getGpuRequest() != null ? modelDeploy.getGpuRequest().doubleValue() : 0.0;
        Double gpuLimit = modelDeploy.getGpuLimit() != null ? modelDeploy.getGpuLimit().doubleValue() : 0.0;

        try {
            // 2. CPU 사용량 조회 (Core 단위)
            String cpuUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_CPU_USAGE.getQuery(), sessionId);
            Object cpuUsageResponse = resrcMgmtGpuClient.executeQuery(cpuUsageQuery);
            cpuUsage = extractNumericValue(cpuUsageResponse);

            // 3. CPU 사용률 조회 (%)
            if (cpuRequest != null && cpuRequest > 0) {
                cpuUtilization = cpuUsage / cpuRequest * 100.0;
            } else {
                cpuUtilization = 0.0;
            }

            // 4. Memory 사용량 조회 (GiB 단위)
            String memoryUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_MEMORY_USAGE.getQuery(), sessionId);
            Object memoryUsageResponse = resrcMgmtGpuClient.executeQuery(memoryUsageQuery);
            Double memoryUsageGiB = extractNumericValue(memoryUsageResponse);
            memoryUsage = memoryUsageGiB != null ? memoryUsageGiB : 0.0;

            // 5. GPU 사용량 조회 (메모리 관련부분이라 사용하지 않음)
            String gpuUsageQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_GPU_USAGE.getQuery(), sessionId);
            Object gpuUsageResponse = resrcMgmtGpuClient.executeQuery(gpuUsageQuery);
            gpuUsage = extractNumericValue(gpuUsageResponse);

            // 6. GPU 사용률 조회 (모니터링용; 요청/제한은 GetModelDeployRes 값을 사용)
            String gpuUtilQuery = String.format(ResrcMgmtQueryEnum.PORTAL_MODEL_GPU_UTILIZATION.getQuery(), sessionId);
            Object gpuUtilResponse = resrcMgmtGpuClient.executeQuery(gpuUtilQuery);
            gpuUtilization = extractNumericValue(gpuUtilResponse);

        } catch (BusinessException e) {
            log.warn("GPU Prometheus 서버 접근 실패 (BusinessException) - sessionId: {}, error: {} (기본값 0으로 설정)", sessionId,
                    e.getMessage());
            // 기본값은 이미 위에서 설정됨 (모두 0.0)
        } catch (FeignException e) {
            log.warn("GPU Prometheus 서버 접근 실패 (FeignException) - sessionId: {}, error: {} (기본값 0으로 설정)", sessionId,
                    e.getMessage());
            // 기본값은 이미 위에서 설정됨 (모두 0.0)
        } catch (RuntimeException e) {
            log.warn("GPU Prometheus 서버 접근 실패 (RuntimeException) - sessionId: {}, error: {} (기본값 0으로 설정)", sessionId,
                    e.getMessage());
            // 기본값은 이미 위에서 설정됨 (모두 0.0)
        }

        // 7. GPU 사용량을 사용률 기반으로 계산: 사용량 = 요청량 × (사용률/100)
        if (gpuRequest != null && gpuUtilization != null) {
            gpuUsage = gpuRequest * (gpuUtilization / 100.0);
        }

        // 8. SessionResourceInfo 생성 (serving ID로 조회한 배포 모델 정보의 request 사용)
        String projectName = null;
        if (servingId != null && !servingId.isBlank()) {
            projectName = projectNameCache.computeIfAbsent(servingId, sid -> {
                try {
                    AssetProjectInfoRes assetInfo = projectInfoService.getAssetProjectInfoByUuid(sid);
                    if (assetInfo != null && assetInfo.getLstPrjNm() != null && !assetInfo.getLstPrjNm().isBlank()) {
                        return assetInfo.getLstPrjNm();
                    }
                } catch (BusinessException ex) {
                    log.debug("모델 프로젝트 정보 조회 실패 (BusinessException) - servingId: {}, error: {}", sid, ex.getMessage());
                } catch (FeignException ex) {
                    log.debug("모델 프로젝트 정보 조회 실패 (FeignException) - servingId: {}, error: {}", sid, ex.getMessage());
                } catch (RuntimeException ex) {
                    log.debug("모델 프로젝트 정보 조회 실패 (RuntimeException) - servingId: {}, error: {}", sid, ex.getMessage());
                }
                return null;
            });
        }

        ResrcMgmtSessionResourceInfo sessionResource = ResrcMgmtSessionResourceInfo.builder()
                .sessionId(sessionId)
                .modelName(modelName)
                .servingId(servingId)
                .status(status)
                .projectId(modelDeploy.getProjectId())
                .projectName(projectName)
                .cpuUsage(cpuUsage)
                .cpuUtilization(cpuUtilization)
                .cpuRequest(cpuRequest)
                .cpuLimit(cpuLimit) // GetModelDeployRes에서 가져온 값
                .memoryUsage(memoryUsage)
                .memoryUtilization(null) // Memory 사용률은 현재 없음
                .memoryRequest(memoryRequest)
                .memoryLimit(memoryLimit) // GetModelDeployRes에서 가져온 값
                .gpuUsage(gpuUsage)
                .gpuUtilization(gpuUtilization)
                .gpuRequest(gpuRequest)
                .gpuLimit(gpuLimit) // GetModelDeployRes에서 가져온 값
                .build();

        log.debug("세션 자원 데이터 조회 완료 - sessionId: {}, cpuUsage: {}, memoryUsage: {}, gpuUsage: {}",
                sessionId, cpuUsage, memoryUsage, gpuUsage);

        return sessionResource;
    }

    /**
     * Prometheus 응답에서 숫자 값 추출
     *
     * @param response Prometheus 응답
     * @return 숫자 값
     */
    @SuppressWarnings("unchecked")
    private Double extractNumericValue(Object response) {
        if (response == null) {
            return 0.0;
        }

        try {
            // Prometheus 응답 구조에 따라 값 추출
            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;
                Object data = responseMap.get("data");

                if (data instanceof Map) {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    Object result = dataMap.get("result");

                    if (result instanceof List) {
                        List<?> resultList = (List<?>) result;
                        if (!resultList.isEmpty()) {
                            Object firstResult = resultList.get(0);
                            if (firstResult instanceof Map) {
                                Map<String, Object> firstResultMap = (Map<String, Object>) firstResult;
                                Object value = firstResultMap.get("value");

                                if (value instanceof List) {
                                    List<?> valueList = (List<?>) value;
                                    if (valueList.size() >= 2) {
                                        Object numericValue = valueList.get(1);
                                        if (numericValue instanceof Number) {
                                            return ((Number) numericValue).doubleValue();
                                        } else if (numericValue instanceof String) {
                                            return Double.parseDouble((String) numericValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.debug("숫자 값 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (NumberFormatException e) {
            log.debug("숫자 값 추출 실패 (NumberFormatException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("숫자 값 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("숫자 값 추출 실패 (RuntimeException): {}", e.getMessage());
        }

        return 0.0;
    }

    /**
     * Prometheus 응답에서 display_name별 service_group 매핑을 추출하는 메서드
     *
     * @param response Prometheus 응답 데이터
     * @return display_name을 키로 하고 service_group을 값으로 하는 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> extractServiceGroupMap(Object response) {
        Map<String, String> serviceGroupMap = new HashMap<>();

        try {
            if (response == null) {
                return serviceGroupMap;
            }

            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && "vector".equals(data.get("resultType"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");

                if (result != null) {
                    for (Map<String, Object> item : result) {
                        Map<String, Object> metric = (Map<String, Object>) item.get("metric");

                        if (metric != null) {
                            // display_name을 우선 사용, 없으면 node, instance 순으로
                            String displayName = resolveMetricNodeIdentifier(metric);
                            if (displayName != null) {
                                String serviceGroup = (String) metric.get("service_group");
                                if (serviceGroup != null && !serviceGroup.isEmpty()) {
                                    serviceGroupMap.put(displayName, serviceGroup);
                                }
                            }
                        }
                    }
                }
            }

        } catch (ClassCastException e) {
            log.warn("service_group 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.warn("service_group 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("service_group 추출 실패 (RuntimeException): {}", e.getMessage());
        }

        return serviceGroupMap;
    }

    /**
     * 세션별 Quota 그리드 처리 (CPU, Memory)
     *
     * @param nodeName      노드 이름 (display_name)
     * @param durationParam 기간 파라미터 (예: "300s")
     * @param client        Prometheus 클라이언트
     * @param capacityQuery 할당량 쿼리 Enum
     * @param usageQuery    사용량 쿼리 Enum
     * @param resourceType  리소스 타입 ("cpu" 또는 "memory")
     * @return 세션별 Quota 그리드 리스트
     */
    private List<Map<String, Object>> processSessionQuotaGrid(
            String nodeName,
            String durationParam,
            ResrcMgmtGpuClient client,
            ResrcMgmtQueryEnum capacityQuery,
            ResrcMgmtQueryEnum usageQuery,
            String resourceType) {

        List<Map<String, Object>> quotaGrid = new ArrayList<>();

        try {
            // 할당량 쿼리 실행
            String capacityQueryStr = String.format(capacityQuery.getQuery(), nodeName);
            Object capacityResponse = client.executeQuery(capacityQueryStr);

            // 사용량 쿼리 실행 (CPU는 durationParam 필요)
            String usageQueryStr;
            if (resourceType.equals("cpu")) {
                usageQueryStr = String.format(usageQuery.getQuery(), nodeName, durationParam);
            } else {
                usageQueryStr = String.format(usageQuery.getQuery(), nodeName);
            }
            Object usageResponse = client.executeQuery(usageQueryStr);

            // 세션별 데이터 추출
            Map<String, Double> capacityMap = extractSessionMetricsAsDouble(capacityResponse);
            Map<String, Double> usageMap = extractSessionMetricsAsDouble(usageResponse);

            // 모든 세션 수집
            Set<String> allSessions = new HashSet<>();
            allSessions.addAll(capacityMap.keySet());
            allSessions.addAll(usageMap.keySet());

            // 세션별 데이터 처리
            for (String sessionKey : allSessions) {
                Map<String, Object> sessionData = new HashMap<>();

                double capacity = capacityMap.getOrDefault(sessionKey, 0.0);
                double usage = usageMap.getOrDefault(sessionKey, 0.0);
                double request = capacity; // 요청량은 할당량과 동일

                // 사용률 계산
                double requestUsageRate = capacity > 0 ? (usage / request) * 100.0 : 0.0;
                double capacityUsageRate = capacity > 0 ? (usage / capacity) * 100.0 : 0.0;

                // sessionKey 파싱 (user_id:session_id 형식)
                String[] parts = sessionKey.split(":");
                String userId = parts.length > 0 ? parts[0] : "";
                String sessionId = parts.length > 1 ? parts[1] : sessionKey;

                sessionData.put("session_id", sessionId);
                sessionData.put("user_id", userId);
                sessionData.put("allocation", capacity); // 할당량
                sessionData.put("request", request); // 요청량
                sessionData.put("usage", usage); // 실제 사용량
                sessionData.put("request_usage_rate", requestUsageRate); // 요청량 대비 사용률
                sessionData.put("allocation_usage_rate", capacityUsageRate); // 할당량 대비 사용률

                quotaGrid.add(sessionData);
            }

            log.info("세션별 {} Quota 그리드 처리 완료 - {}건", resourceType, quotaGrid.size());

        } catch (ClassCastException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (ClassCastException): {}", resourceType, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (IllegalArgumentException): {}", resourceType, e.getMessage());
        } catch (NullPointerException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (NullPointerException): {}", resourceType, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (RuntimeException): {}", resourceType, e.getMessage());
        }

        return quotaGrid;
    }

    /**
     * 세션별 Quota 그리드 처리 (GPU)
     *
     * @param nodeName         노드 이름 (display_name)
     * @param durationParam    기간 파라미터 (사용하지 않지만 시그니처 일관성 유지)
     * @param client           Prometheus 클라이언트
     * @param capacityQuery    할당량 쿼리 Enum (GPU 메모리 capacity)
     * @param usageQuery       사용량 쿼리 Enum (GPU 메모리 usage)
     * @param utilizationQuery 사용률 쿼리 Enum (GPU utilization)
     * @param resourceType     리소스 타입 ("gpu")
     * @return 세션별 Quota 그리드 리스트
     */
    private List<Map<String, Object>> processSessionQuotaGrid(
            String nodeName,
            String durationParam,
            ResrcMgmtGpuClient client,
            ResrcMgmtQueryEnum capacityQuery,
            ResrcMgmtQueryEnum usageQuery,
            ResrcMgmtQueryEnum utilizationQuery,
            String resourceType) {

        List<Map<String, Object>> quotaGrid = new ArrayList<>();

        try {
            // GPU 메모리 할당량 쿼리 실행
            String capacityQueryStr = String.format(capacityQuery.getQuery(), nodeName);
            Object capacityResponse = client.executeQuery(capacityQueryStr);

            // GPU 메모리 사용량 쿼리 실행
            String usageQueryStr = String.format(usageQuery.getQuery(), nodeName);
            Object usageResponse = client.executeQuery(usageQueryStr);

            // GPU 사용률 쿼리 실행
            String utilizationQueryStr = String.format(utilizationQuery.getQuery(), nodeName);
            Object utilizationResponse = client.executeQuery(utilizationQueryStr);

            // 세션별 데이터 추출
            Map<String, Double> capacityMap = extractSessionMetricsAsDouble(capacityResponse);
            Map<String, Double> usageMap = extractSessionMetricsAsDouble(usageResponse);
            Map<String, Double> utilizationMap = extractSessionMetricsAsDouble(utilizationResponse);

            // 모든 세션 수집
            Set<String> allSessions = new HashSet<>();
            allSessions.addAll(capacityMap.keySet());
            allSessions.addAll(usageMap.keySet());
            allSessions.addAll(utilizationMap.keySet());

            // 세션별 데이터 처리
            for (String sessionKey : allSessions) {
                Map<String, Object> sessionData = new HashMap<>();

                double capacity = capacityMap.getOrDefault(sessionKey, 0.0);
                double memoryUsage = usageMap.getOrDefault(sessionKey, 0.0);
                double utilization = utilizationMap.getOrDefault(sessionKey, 0.0);
                double request = capacity; // 요청량은 할당량과 동일

                // GPU는 utilization을 사용률로 사용 (0-100%)
                double requestUsageRate = utilization;
                double capacityUsageRate = utilization;

                // GPU에서 사용률 데이터가 0이면 실제 사용량도 0으로 처리
                if (Double.compare(requestUsageRate, 0.0) == 0) {
                    memoryUsage = 0.0;
                }

                // sessionKey 파싱 (user_id:session_id 형식)
                String[] parts = sessionKey.split(":");
                String userId = parts.length > 0 ? parts[0] : "";
                String sessionId = parts.length > 1 ? parts[1] : sessionKey;

                sessionData.put("session_id", sessionId);
                sessionData.put("user_id", userId);
                sessionData.put("allocation", capacity); // 할당량 (GB)
                sessionData.put("request", request); // 요청량 (GB)
                sessionData.put("usage", memoryUsage); // 실제 사용량 (GB)
                sessionData.put("request_usage_rate", requestUsageRate); // 요청량 대비 사용률 (%)
                sessionData.put("allocation_usage_rate", capacityUsageRate); // 할당량 대비 사용률 (%)

                quotaGrid.add(sessionData);
            }

            log.info("세션별 {} Quota 그리드 처리 완료 - {}건", resourceType, quotaGrid.size());

        } catch (ClassCastException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (ClassCastException): {}", resourceType, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (IllegalArgumentException): {}", resourceType, e.getMessage());
        } catch (NullPointerException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (NullPointerException): {}", resourceType, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("세션별 {} Quota 그리드 처리 실패 (RuntimeException): {}", resourceType, e.getMessage());
        }

        return quotaGrid;
    }

    /**
     * 세션별 메트릭 데이터 추출 (user_id:session_id를 키로 하는 Map, Double 값 반환)
     *
     * @param response Prometheus 응답 데이터
     * @return 세션별 메트릭 값 Map (키: "user_id:session_id", 값: 메트릭 값)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Double> extractSessionMetricsAsDouble(Object response) {
        Map<String, Double> sessionMetrics = new HashMap<>();

        try {
            if (response == null) {
                return sessionMetrics;
            }

            Map<String, Object> responseMap = (Map<String, Object>) response;
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null && "vector".equals(data.get("resultType"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");

                if (result != null) {
                    for (Map<String, Object> item : result) {
                        Map<String, Object> metric = (Map<String, Object>) item.get("metric");
                        Object value = item.get("value");

                        if (metric != null && value != null) {
                            String userId = (String) metric.get("user_id");
                            String sessionId = (String) metric.get("session_id");

                            if (userId != null && sessionId != null) {
                                String sessionKey = userId + ":" + sessionId;

                                // 값 추출
                                double metricValue = 0.0;
                                if (value instanceof List) {
                                    List<?> valueList = (List<?>) value;
                                    if (valueList.size() >= 2) {
                                        Object numericValue = valueList.get(1);
                                        if (numericValue instanceof Number) {
                                            metricValue = ((Number) numericValue).doubleValue();
                                        } else if (numericValue instanceof String) {
                                            try {
                                                metricValue = Double.parseDouble((String) numericValue);
                                            } catch (NumberFormatException e) {
                                                log.debug("숫자 파싱 실패: {}", numericValue);
                                            }
                                        }
                                    }
                                }

                                sessionMetrics.put(sessionKey, metricValue);
                            }
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.warn("세션별 메트릭 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (NumberFormatException e) {
            log.warn("세션별 메트릭 추출 실패 (NumberFormatException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.warn("세션별 메트릭 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("세션별 메트릭 추출 실패 (RuntimeException): {}", e.getMessage());
        }

        return sessionMetrics;
    }

    private String resolveMetricNodeIdentifier(Map<String, Object> metric) {
        if (metric == null) {
            return null;
        }

        Object[] candidates = {metric.get("display_name"), metric.get("node"), metric.get("instance")};
        for (Object candidate : candidates) {
            if (candidate == null) {
                continue;
            }

            String value = String.valueOf(candidate).trim();
            if (!value.isEmpty() && !"0".equals(value) && !"null".equalsIgnoreCase(value)
                    && !"undefined".equalsIgnoreCase(value)) {
                return value;
            }
        }

        return null;
    }

}
