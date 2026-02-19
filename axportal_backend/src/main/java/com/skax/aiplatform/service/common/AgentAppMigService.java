package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentAppsService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.entity.deploy.GpoMigMas;
import com.skax.aiplatform.repository.deploy.GpoMigMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.deploy.ApiGwService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent App 마이그레이션 서비스
 * 
 * <p>Agent App 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentAppMigService {
    
    private final SktaiAgentAppsService sktaiAgentAppsService;
    private final SktaiServingService sktaiServingService;
    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    private final GpoMigMasRepository gpoMigMasRepository;
    private final ApiGwService apiGwService;
    
    @Value("${migration.base-dir:}")
    private String migrationBaseDir;
    
    /**
     * Agent App이 커스텀 AGENT_APP인지 확인
     * 
     * <p>deployments에서 target_type이 "external_graph"인 배포가 있는지 확인합니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @return 커스텀 AGENT_APP 여부 (true: 커스텀, false: 일반)
     */
    public boolean isCustomAgentApp(String appUuid) {
        try {
            log.debug("Agent App 커스텀 여부 확인 시작 - appUuid: {}", appUuid);
            
            AppResponse appResponse = sktaiAgentAppsService.getApp(appUuid);
            if (appResponse == null) {
                log.warn("Agent App 상세 조회 실패 - appUuid: {}. 기본값(일반 AGENT_APP) 반환", appUuid);
                return false;
            }
            
            if (appResponse.getDeployments() == null || appResponse.getDeployments().isEmpty()) {
                log.debug("Agent App에 배포가 없음 - appUuid: {}. 기본값(일반 AGENT_APP) 반환", appUuid);
                return false;
            }
            
            // deployments에서 target_type이 "external_graph"인 배포가 있는지 확인
            boolean isCustom = appResponse.getDeployments().stream()
                    .anyMatch(deployment -> deployment != null
                            && "external_graph".equalsIgnoreCase(deployment.getTargetType()));
            
            log.info("Agent App 커스텀 여부 확인 완료 - appUuid: {}, isCustom: {}", appUuid, isCustom);
            return isCustom;
            
        } catch (FeignException e) {
            log.warn("Agent App 커스텀 여부 확인 실패 (FeignException) - appUuid: {}, error: {}. 기본값(일반 AGENT_APP) 반환",
                    appUuid, e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Agent App 커스텀 여부 확인 실패 (Exception) - appUuid: {}, error: {}. 기본값(일반 AGENT_APP) 반환",
                    appUuid, e.getMessage());
            return false;
        }
    }
    
    /**
     * 1. Export 형태를 만드는 것
     * 
     * <p>Agent App을 조회하고 Import 형식으로 변환합니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String appUuid, String projectId) {
        try {
            log.info("Agent App Export → Import 형식 변환 시작 - appUuid: {}", appUuid);
            
            // 1. 커스텀 AGENT_APP 여부 확인
            boolean isCustom = isCustomAgentApp(appUuid);
            log.info("Agent App 커스텀 여부 확인 - appUuid: {}, isCustom: {}", appUuid, isCustom);
            
            // 2. Agent App 데이터 수집
            Map<String, Object> data = collectAgentAppData(appUuid);
            if (data == null) {
                throw new RuntimeException("Agent App 데이터를 수집할 수 없습니다: " + appUuid);
            }
            
            // 3. Policy 조회 (projectId가 있으면)
            java.util.List<PolicyRequest> policyRequests = null;
            if (projectId != null && !projectId.trim().isEmpty()) {
                try {
                    Long projectSeq = Long.parseLong(projectId);
                    policyRequests = adminAuthService.getPolicyRequestsByCurrentProjectSequence(projectSeq);
                    log.info("Policy 조회 완료 - projectId: {}, policyCount: {}", projectId, 
                            policyRequests != null ? policyRequests.size() : 0);
                } catch (Exception e) {
                    log.warn("Policy 조회 실패 (계속 진행) - projectId: {}, error: {}", projectId, e.getMessage());
                    // Policy 조회 실패해도 Export는 계속 진행
                }
            }
            
            // 4. Import 형식으로 변환 (커스텀 여부, policy 전달)
            String importJson = convertAgentAppToImportFormat(data, isCustom, policyRequests);
            
            log.info("Agent App Export → Import 형식 변환 완료 - appUuid: {}, isCustom: {}, jsonLength: {}", 
                    appUuid, isCustom, importJson.length());
            
            return importJson;
            
        } catch (BusinessException e) {
            log.error("Agent App API 호출 실패 (BusinessException) - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            throw e; // BusinessException은 그대로 재throw
        } catch (FeignException e) {
            log.error("Agent App API 호출 실패 (FeignException) - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            throw new RuntimeException("Agent App Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Agent App Export → Import 형식 변환 실패 - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            throw new RuntimeException("Agent App Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 2. Export 형태를 Import 거래 날리는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String appUuid) {
        try {
            log.info("Agent App Export → Import 거래 시작 - appUuid: {}", appUuid);
            
            // 1. Export → Import 형식으로 변환 (projectId는 null)
            String importJson = exportToImportFormat(appUuid, null);
            
            // 2. Import 거래 호출
            return importFromJsonString(null, importJson, null);
            
        } catch (BusinessException e) {
            log.error("Agent App API 호출 실패 (BusinessException) - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            throw e; // BusinessException은 그대로 재throw
        } catch (FeignException e) {
            log.error("Agent App API 호출 실패 (FeignException) - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("Agent App Export → Import 거래 실패 - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * JSON 문자열로부터 Import 수행
     * 
     * @param appUuid Agent App UUID (선택사항, 새로 생성할 경우 null 가능)
     * @param importJson Import 형식의 JSON 문자열
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String appUuid, String importJson, Long projectId) {
        try {
            log.info("Agent App JSON 문자열에서 Import 시작 - appUuid: {}, jsonLength: {}", 
                    appUuid, importJson != null ? importJson.length() : 0);
            
            if (importJson == null || importJson.trim().isEmpty()) {
                log.error("Agent App Import JSON이 비어있습니다 - appUuid: {}", appUuid);
                return false;
            }
            
            // 1. JSON 문자열을 Map으로 파싱하여 null 필드 제거
            Map<String, Object> jsonMap = objectMapper.readValue(importJson, new TypeReference<Map<String, Object>>() {});
            
            // 2. target_type 확인하여 커스텀 AGENT_APP 여부 판단
            String targetType = jsonMap.get("target_type") != null ? String.valueOf(jsonMap.get("target_type")) : null;
            boolean isCustom = targetType != null && "external_graph".equalsIgnoreCase(targetType);
            log.info("Agent App Import - targetType: {}, isCustom: {}", targetType, isCustom);
            
            // null 필드 제거
            Map<String, Object> cleanedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                if (entry.getValue() != null) {
                    cleanedMap.put(entry.getKey(), entry.getValue());
                }
            }
            
            AppCreateResponse response;
            
            if (isCustom) {
                // 3-1. 커스텀 AGENT_APP인 경우
                log.info("커스텀 AGENT_APP Import 시작 - appUuid: {}", appUuid);
                
                // app_id 확인 (JSON에서)
                String appId = cleanedMap.get("app_id") != null ? String.valueOf(cleanedMap.get("app_id")) : null;
                boolean hasAppId = appId != null && !appId.trim().isEmpty();
                log.info("커스텀 AGENT_APP app_id 확인 - appUuid: {}, appId: {}, hasAppId: {}", appUuid, appId, hasAppId);
                
                // 커스텀 App 생성을 위한 필드 추출
                String name = cleanedMap.get("name") != null ? String.valueOf(cleanedMap.get("name")) : null;
                String description = cleanedMap.get("description") != null ? String.valueOf(cleanedMap.get("description")) : null;
                String versionDescription = cleanedMap.get("version_description") != null ? String.valueOf(cleanedMap.get("version_description")) : null;
                String finalTargetType = targetType != null ? targetType : "external_graph";
                
                @SuppressWarnings("unchecked")
                java.util.List<String> modelList = cleanedMap.get("model_list") instanceof java.util.List 
                        ? (java.util.List<String>) cleanedMap.get("model_list") : null;
                String imageUrl = cleanedMap.get("image_url") != null ? String.valueOf(cleanedMap.get("image_url")) : null;
                Boolean useExternalRegistry = cleanedMap.get("use_external_registry") instanceof Boolean 
                        ? (Boolean) cleanedMap.get("use_external_registry") : null;
                Integer cpuRequest = cleanedMap.get("cpu_request") instanceof Number 
                        ? ((Number) cleanedMap.get("cpu_request")).intValue() : null;
                Integer cpuLimit = cleanedMap.get("cpu_limit") instanceof Number 
                        ? ((Number) cleanedMap.get("cpu_limit")).intValue() : null;
                Integer memRequest = cleanedMap.get("mem_request") instanceof Number 
                        ? ((Number) cleanedMap.get("mem_request")).intValue() : null;
                Integer memLimit = cleanedMap.get("mem_limit") instanceof Number 
                        ? ((Number) cleanedMap.get("mem_limit")).intValue() : null;
                Integer minReplicas = cleanedMap.get("min_replicas") instanceof Number 
                        ? ((Number) cleanedMap.get("min_replicas")).intValue() : null;
                Integer maxReplicas = cleanedMap.get("max_replicas") instanceof Number 
                        ? ((Number) cleanedMap.get("max_replicas")).intValue() : null;
                Integer workersPerCore = cleanedMap.get("workers_per_core") instanceof Number 
                        ? ((Number) cleanedMap.get("workers_per_core")).intValue() : null;
                Object safetyFilterOptions = cleanedMap.get("safety_filter_options");
                
                // Policy 추출 (JSON에서 읽어오기)
                java.util.List<PolicyRequest> policyList = cleanedMap.get("policy") instanceof java.util.List
                        ? objectMapper.convertValue(cleanedMap.get("policy"), 
                                new TypeReference<java.util.List<PolicyRequest>>() {})
                        : java.util.Collections.emptyList();
                
                if (policyList != null && !policyList.isEmpty()) {
                    log.info("Policy 추출 완료 - policyCount: {}", policyList.size());
                } else {
                    log.info("Policy가 없거나 비어있음 - 빈 리스트 사용");
                }
                
                // .env 파일 읽기 (custom_agent/{appUuid}/.env)
                MultipartFile envFile = readEnvFile(appUuid);
                if (envFile != null) {
                    log.info("AGENT_APP :: .env 파일 발견 및 로드 완료 - fileName: {}, size: {}", 
                            envFile.getOriginalFilename(), envFile.getSize());
                } else {
                    log.info("AGENT_APP :: .env 파일 없음 - envFile을 null로 전달합니다.");
                }
                
                try {
                    if (hasAppId) {
                        // app_id가 있으면 addCustomDeploymentWithMultipart 호출
                        log.info("커스텀 AGENT_APP 배포 추가 (app_id 있음) - appId: {}, appUuid: {}", appId, appUuid);
                        com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentResponse deploymentResponse = 
                                sktaiAgentAppsService.addCustomDeploymentWithMultipart(
                                appId,
                                envFile, // .env 파일 (있으면 전달, 없으면 null)
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
                                policyList != null ? policyList : java.util.Collections.emptyList()
                        );
                        
                        // AppCreateResponse로 변환 (호환성을 위해)
                        AppDeploymentResponse.AppDeploymentInfo deploymentInfo = deploymentResponse.getData();
                        response = AppCreateResponse.builder()
                                .data(AppCreateResponse.AppCreateData.builder()
                                        .appId(appId)
                                        .deploymentId(deploymentInfo != null ? deploymentInfo.getId() : null)
                                        .agentServingId(deploymentInfo != null ? deploymentInfo.getServingId() : null)
                                        .build())
                                .build();
                        
                        log.info("커스텀 AGENT_APP 배포 추가 성공 - appId: {}, appUuid: {}", appId, appUuid);
                    } else {
                        // app_id가 없으면 createCustomApp 호출
                        log.info("커스텀 AGENT_APP 생성 (app_id 없음) - appUuid: {}", appUuid);
                        response = sktaiAgentAppsService.createCustomApp(
                                envFile, // .env 파일 (있으면 전달, 없으면 null)
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
                                policyList != null ? policyList : java.util.Collections.emptyList()
                        );
                        
                        log.info("커스텀 AGENT_APP 생성 성공 - appUuid: {}", appUuid);
                    }

                    // ADXP 권한부여
                    if (projectId != null && response != null && response.getData() != null) {
                        String finalAppId = response.getData().getAppId();
                        if (finalAppId != null) {
                            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + finalAppId, projectId);
                            // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + finalAppId + "/deployments", projectId);
                            if (response.getData().getAgentServingId() != null) {
                                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent_servings/" + response.getData().getAgentServingId(), projectId);
                            }
                            if (response.getData().getDeploymentId() != null) {
                                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/deployments/" + response.getData().getDeploymentId(), projectId);
                            }
                        }
                    }

                    // API GW 엔드포인트 생성
                    try {
                        CreateApiReq createApiReq = CreateApiReq.builder()
                                .type("agent")
                                .uuid(response.getData().getAppId())
                                .name(name)
                                .description(description)
                                .projectId(projectId.toString()).build();

                        CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
                        log.info("API GW 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());
                    } catch (FeignException e) {
                        log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                                response.getData().getAppId(), e.getMessage());
                    } catch (Exception e) {
                        log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                                response.getData().getAppId(), e.getMessage());
                    }

                    log.info("커스텀 AGENT_APP Import 성공 - appUuid: {}", appUuid);
                } catch (BusinessException e) {
                    // "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
                    // (예: 참조하는 리소스가 없을 때) - false를 반환하고 계속 진행
                    log.warn("커스텀 AGENT_APP Import 실패 (BusinessException) - appUuid: {}, error: {}. Import 실패로 처리", 
                            appUuid, e.getMessage());
                    return false;
                } catch (Exception e) {
                    log.warn("커스텀 AGENT_APP Import 실패 (Exception) - appUuid: {}, error: {}. Import 실패로 처리", 
                            appUuid, e.getMessage());
                    return false;
                }
            } else {
                // 3-2. 일반 AGENT_APP인 경우: createApp 호출
                log.info("일반 AGENT_APP Import 시작 - appUuid: {}", appUuid);
                
                // Policy 확인 (cleanedMap에 있으면 AppCreateRequest에 자동 포함됨)
                if (cleanedMap.containsKey("policy") && cleanedMap.get("policy") != null) {
                    log.info("Policy가 JSON에 포함되어 있음 - policyCount: {}", 
                            cleanedMap.get("policy") instanceof java.util.List 
                                    ? ((java.util.List<?>) cleanedMap.get("policy")).size() : 0);
                } else {
                    log.info("Policy가 JSON에 없음 - 빈 리스트 사용");
                }
                
                // 정리된 Map을 AppCreateRequest로 변환
                AppCreateRequest request = objectMapper.convertValue(cleanedMap, AppCreateRequest.class);
                
                if (request == null) {
                    log.error("Agent App Import JSON 파싱 실패 - appUuid: {}, importJson이 null로 변환됨", appUuid);
                    return false;
                }
                
                log.debug("Agent App Import 요청 파싱 완료 - name: {}, targetId: {}, targetType: {}", 
                        request.getName(), request.getTargetId(), request.getTargetType());
                
                // AppCreateRequest를 Map으로 변환하여 null 필드 제거 후 다시 변환 (Feign Client 전달 전 최종 정리)
                Map<String, Object> requestMap = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> finalMap = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
                    if (entry.getValue() != null) {
                        finalMap.put(entry.getKey(), entry.getValue());
                    }
                }
                AppCreateRequest finalRequest = objectMapper.convertValue(finalMap, AppCreateRequest.class);
                
                // createApp API 호출
                try {
                    response = sktaiAgentAppsService.createApp(finalRequest);

                    // ADXP 권한부여
                    if (projectId != null && response != null && response.getData() != null) {
                        String finalAppId = response.getData().getAppId();
                        if (finalAppId != null) {
                            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + finalAppId, projectId);
                            // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/" + finalAppId + "/deployments", projectId);
                            if (response.getData().getAgentServingId() != null) {
                                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent_servings/" + response.getData().getAgentServingId(), projectId);
                            }
                            if (response.getData().getDeploymentId() != null) {
                                adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/agents/apps/deployments/" + response.getData().getDeploymentId(), projectId);
                            }
                        }
                    }

                    log.info("일반 AGENT_APP Import 성공 - appUuid: {}", appUuid);

                    // API GW 엔드포인트 생성
                    try {
                        CreateApiReq createApiReq = CreateApiReq.builder()
                                .type("agent")
                                .uuid(response.getData().getAppId())
                                .name(request.getName())
                                .description(request.getDescription())
                                .projectId(projectId.toString()).build();

                        CreateApiRes createApiRes = apiGwService.createApiEndpoint(createApiReq);
                        log.info("API GW 엔드포인트 생성 성공: infWorkSeq={}", createApiRes.getInfWorkSeq());
                    } catch (FeignException e) {
                        log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                                response.getData().getAppId(), e.getMessage());
                    } catch (Exception e) {
                        log.warn("API GW 엔드포인트 생성 실패 (배포는 계속 진행) - uuid: {}, error: {}", 
                                response.getData().getAppId(), e.getMessage());
                    }

                } catch (BusinessException e) {
                    // "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
                    // (예: 참조하는 리소스가 없을 때) - false를 반환하고 계속 진행
                    log.warn("일반 AGENT_APP Import 실패 (BusinessException) - appUuid: {}, error: {}. Import 실패로 처리", 
                            appUuid, e.getMessage());
                    return false;
                } catch (Exception e) {
                    log.warn("일반 AGENT_APP Import 실패 (Exception) - appUuid: {}, error: {}. Import 실패로 처리", 
                            appUuid, e.getMessage());
                    return false;
                }
            }
            
            if (response == null || response.getData() == null) {
                log.error("Agent App Import 응답이 null입니다 - appUuid: {}", appUuid);
                return false;
            }
            
            String createdAppId = response.getData().getAppId();
            log.info("Agent App Import 성공 - appUuid: {}, createdAppId: {}, isCustom: {}", 
                    appUuid, createdAppId, isCustom);
            
            // 4. 이전 배포 버전들 중지 (최신 배포 제외)
            // try {
            //     log.info("이전 배포 버전 중지 시작 - createdAppId: {}", createdAppId);
            //     java.util.List<String> stoppedDeployments = deleteOldDeployments(createdAppId);
            //     log.info("이전 배포 버전 중지 완료 - createdAppId: {}, 중지된 배포 수: {}", 
            //             createdAppId, stoppedDeployments.size());
            // } catch (Exception e) {
            //     // 이전 배포 중지 실패해도 Import는 성공으로 처리
            //     log.warn("이전 배포 버전 중지 중 오류 발생 (무시하고 계속 진행) - createdAppId: {}, error: {}", 
            //             createdAppId, e.getMessage());
            // }
            deleteOldDeploymentsAfterAvailable(createdAppId, response.getData().getDeploymentId());
            log.info("이전 배포 버전 비동기 삭제 시작 - createdAppId: {}, newDeploymentId: {}", 
                    createdAppId, response.getData().getDeploymentId());
            
            return true;
            
        } catch (JsonProcessingException e) {
            log.error("Agent App Import JSON 파싱 실패 - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            return false;
        } catch (BusinessException e) {
            // "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황일 수 있음
            // (예: 참조하는 리소스가 없을 때) - false를 반환하고 계속 진행
            log.warn("Agent App Import 실패 (BusinessException) - appUuid: {}, error: {}. Import 실패로 처리", 
                    appUuid, e.getMessage());
            return false;
        } catch (FeignException e) {
            log.error("Agent App API 호출 실패 (FeignException) - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // 모든 예외를 catch하여 Import가 중단되지 않도록 함
            log.error("Agent App JSON 문자열에서 Import 실패 - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     * 
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String appUuid, boolean saveToFile) {
        try {
            log.info("Agent App Export → JSON 파일 저장 시작 - appUuid: {}, saveToFile: {}", 
                    appUuid, saveToFile);
            
            // 1. Export → Import 형식으로 변환 (projectId는 null)
            String importJson = exportToImportFormat(appUuid, null);
            
            // 2. JSON 파일로 저장 (조건 처리)
            if (!saveToFile) {
                log.info("파일 저장 옵션이 false이므로 파일 저장을 건너뜁니다.");
                return null;
            }
            
            // 저장 디렉토리 생성
            String baseDir = "data/exports";
            Path exportDir = Paths.get(baseDir);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // 파일명 생성: AGENT_APP_{uuid}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("AGENT_APP_%s_%s.json", appUuid, timestamp);
            Path filePath = exportDir.resolve(fileName);
            
            // JSON 파일 저장 (UTF-8 인코딩 명시)
            try (FileWriter writer = new FileWriter(
                    filePath.toFile(), 
                    java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(importJson);
                writer.flush();
            }
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Agent App Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("Agent App Export → JSON 파일 저장 실패 (IOException) - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            throw new RuntimeException("Agent App Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Agent App Export → JSON 파일 저장 실패 - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            throw new RuntimeException("Agent App Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * Agent App 데이터 수집
     * 
     * <p>Agent App 상세 정보를 조회하여 필요한 데이터를 수집합니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @return 수집된 Agent App 데이터 (Map 형태)
     */
    private Map<String, Object> collectAgentAppData(String appUuid) {
        try {
            log.info("Agent App 데이터 수집 시작 - appUuid: {}", appUuid);
            
            // 1. Agent App 상세 조회
            log.debug("[1단계] Agent App 상세 조회 - appUuid: {}", appUuid);
            AppResponse appResponse = sktaiAgentAppsService.getApp(appUuid);
            
            if (appResponse == null) {
                log.error("Agent App 상세 조회 실패 - appUuid: {}", appUuid);
                return null;
            }
            
            log.info("[1단계] Agent App 상세 조회 완료 - id: {}, name: {}", 
                    appResponse.getId(), appResponse.getName());
            
            // 2. App 배포 목록 조회
            log.debug("[2단계] App 배포 목록 조회 - appId: {}", appUuid);
            AppDeploymentsResponse deploymentsResponse = sktaiAgentAppsService.getAppDeployments(
                    appUuid, 1, 1000, null, null, null);
            
            if (deploymentsResponse == null || deploymentsResponse.getData() == null || 
                    deploymentsResponse.getData().isEmpty()) {
                log.error("App 배포 목록 조회 실패 또는 배포가 없음 - appUuid: {}", appUuid);
                return null;
            }
            
            log.info("[2단계] App 배포 목록 조회 완료 - 배포 개수: {}", deploymentsResponse.getData().size());
            
            // 3. status가 "Available"이고 version이 가장 큰 배포 찾기
            log.debug("[2-1단계] 배포 목록 필터링 시작 - 전체 배포 수: {}", deploymentsResponse.getData().size());
            
            // 디버깅을 위해 모든 배포 정보 로깅
            for (AppDeploymentsResponse.AppDeploymentInfo deployment : deploymentsResponse.getData()) {
                log.debug("배포 정보 - id: {}, status: {}, version: {}, servingId: {}, targetId: {}, targetType: {}", 
                        deployment.getId(), deployment.getStatus(), deployment.getVersion(), 
                        deployment.getServingId(), deployment.getTargetId(), deployment.getTargetType());
            }
            
            AppDeploymentsResponse.AppDeploymentInfo targetDeployment = deploymentsResponse.getData().stream()
                    .filter(deployment -> deployment != null
                            && "Available".equals(deployment.getStatus())
                            && deployment.getVersion() != null
                            && deployment.getServingId() != null
                            && !deployment.getServingId().trim().isEmpty())
                    .max((d1, d2) -> Integer.compare(d1.getVersion(), d2.getVersion()))
                    .orElse(null);
            
            if (targetDeployment == null) {
                log.error("Available 상태의 배포를 찾을 수 없음 - appUuid: {}", appUuid);
                // 사용 가능한 배포 상태 목록 로깅
                String availableStatuses = deploymentsResponse.getData().stream()
                        .map(d -> d.getStatus() + " (servingId: " + d.getServingId() + ", version: " + d.getVersion() + ")")
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("없음");
                log.error("사용 가능한 배포 상태 목록: {}", availableStatuses);
                return null;
            }
            
            String servingId = targetDeployment.getServingId();
            log.info("[2단계] Available 배포 찾음 - servingId: {}, version: {}, deploymentId: {}", 
                    servingId, targetDeployment.getVersion(), targetDeployment.getId());
            
            // 4. Agent Serving 정보 조회
            // getAgentServing을 먼저 시도 (더 안정적인 엔드포인트)
            log.debug("[3단계] Agent Serving 정보 조회 - servingId: {}", servingId);
            AgentServingInfo agentServingInfo = null;
            AgentServingResponse agentServingResponse = null;
            
            // 먼저 getAgentServing 시도 (더 안정적인 엔드포인트)
            try {
                agentServingResponse = sktaiServingService.getAgentServing(servingId);
                log.info("[3단계] Agent Serving 조회 성공 (getAgentServing) - servingId: {}", servingId);
            } catch (BusinessException e) {
                // getAgentServing 실패 시 getAgentServingInfo 시도
                if (e.getErrorCode() != null && e.getErrorCode().name().contains("NOT_FOUND")) {
                    log.debug("Agent Serving 조회 실패 (404) - servingId: {}. getAgentServingInfo를 시도합니다.", servingId);
                    try {
                        agentServingInfo = sktaiServingService.getAgentServingInfo(servingId);
                        log.info("[3단계] Agent Serving Info 조회 성공 (getAgentServingInfo) - servingId: {}", servingId);
                    } catch (BusinessException e2) {
                        log.warn("Agent Serving Info 조회도 실패 (404) - servingId: {}가 존재하지 않습니다. " +
                                "배포 정보에서 servingId를 가져왔지만 실제 서빙이 삭제되었을 수 있습니다.", servingId);
                        log.warn("배포 정보 - deploymentId: {}, targetId: {}, targetType: {}", 
                                targetDeployment.getId(), targetDeployment.getTargetId(), targetDeployment.getTargetType());
                    }
                } else {
                    log.warn("Agent Serving 조회 실패 (BusinessException) - servingId: {}, error: {}. " +
                            "getAgentServingInfo를 시도합니다.", servingId, e.getMessage());
                    try {
                        agentServingInfo = sktaiServingService.getAgentServingInfo(servingId);
                        log.info("[3단계] Agent Serving Info 조회 성공 (getAgentServingInfo) - servingId: {}", servingId);
                    } catch (BusinessException e2) {
                        log.warn("Agent Serving Info 조회도 실패 - servingId: {}, error: {}", servingId, e2.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Agent Serving 조회 실패 (Exception) - servingId: {}, error: {}. " +
                        "getAgentServingInfo를 시도합니다.", servingId, e.getMessage());
                try {
                    agentServingInfo = sktaiServingService.getAgentServingInfo(servingId);
                    log.info("[3단계] Agent Serving Info 조회 성공 (getAgentServingInfo) - servingId: {}", servingId);
                } catch (Exception e2) {
                    log.warn("Agent Serving Info 조회도 실패 - servingId: {}, error: {}", servingId, e2.getMessage());
                }
            }
            
            if (agentServingInfo == null && agentServingResponse == null) {
                log.warn("Agent Serving 정보 조회 실패 - servingId: {}. 배포 정보만으로 데이터 수집을 시도합니다.", servingId);
                // agentServingInfo가 null이어도 배포 정보에서 필요한 데이터를 추출할 수 있도록 계속 진행
            }
            
            if (agentServingInfo != null) {
                log.info("[3단계] Agent Serving Info 조회 완료 - agentServingId: {}, deploymentName: {}", 
                        agentServingInfo.getAgentServingId(), agentServingInfo.getDeploymentName());
                log.debug("[3단계] Agent Serving Info 리소스 정보 - cpuRequest: {}, cpuLimit: {}, memRequest: {}, memLimit: {}, gpuRequest: {}, gpuLimit: {}", 
                        agentServingInfo.getCpuRequest(), agentServingInfo.getCpuLimit(), 
                        agentServingInfo.getMemRequest(), agentServingInfo.getMemLimit(),
                        agentServingInfo.getGpuRequest(), agentServingInfo.getGpuLimit());
            } else if (agentServingResponse != null) {
                log.info("[3단계] Agent Serving Response 조회 완료 - agentServingId: {}, agentServingName: {}", 
                        agentServingResponse.getAgentServingId(), agentServingResponse.getAgentServingName());
                log.debug("[3단계] Agent Serving Response 리소스 정보 - cpuRequest: {}, cpuLimit: {}, memRequest: {}, memLimit: {}, gpuRequest: {}, gpuLimit: {}", 
                        agentServingResponse.getCpuRequest(), agentServingResponse.getCpuLimit(), 
                        agentServingResponse.getMemRequest(), agentServingResponse.getMemLimit(),
                        agentServingResponse.getGpuRequest(), agentServingResponse.getGpuLimit());
            } else {
                log.warn("[3단계] Agent Serving 정보 조회 실패 - servingId: {}가 존재하지 않지만 배포 정보로 계속 진행", servingId);
            }
            
            // 5. AppCreateRequest에 필요한 값만 추출
            Map<String, Object> result = new LinkedHashMap<>();
            
            // 기본 정보
            result.put("name", appResponse.getName());
            if (agentServingInfo != null && agentServingInfo.getDescription() != null) {
                result.put("description", agentServingInfo.getDescription());
            } else if (agentServingResponse != null && agentServingResponse.getDescription() != null) {
                result.put("description", agentServingResponse.getDescription());
            } else if (appResponse.getDescription() != null) {
                result.put("description", appResponse.getDescription());
            } else if (targetDeployment.getDescription() != null) {
                result.put("description", targetDeployment.getDescription());
            }
            
            // 배포 대상 정보 (AppResponse에서 가져오거나 AgentServingInfo에서 추출)
            // targetId와 targetType은 AppResponse의 deployment 정보에서 가져올 수 있음
            if (targetDeployment.getTargetId() != null) {
                result.put("target_id", targetDeployment.getTargetId());
            }
            if (targetDeployment.getTargetType() != null) {
                result.put("target_type", targetDeployment.getTargetType());
            }
            
            // 서빙 타입
            if (agentServingInfo != null && agentServingInfo.getServingType() != null) {
                result.put("serving_type", agentServingInfo.getServingType());
            } else if (agentServingResponse != null && agentServingResponse.getServingType() != null) {
                result.put("serving_type", agentServingResponse.getServingType());
            } else if (targetDeployment.getServingType() != null) {
                result.put("serving_type", targetDeployment.getServingType());
            }
            
            // 리소스 정보 (agentServingInfo가 있으면 사용, 없으면 agentServingResponse 사용)
            if (agentServingInfo != null) {
                extractResourceInfoFromServing(result, agentServingInfo, "AgentServingInfo");
            } else if (agentServingResponse != null) {
                extractResourceInfoFromServing(result, agentServingResponse, "AgentServingResponse");
            } else {
                // 둘 다 없으면 기본값 또는 null 설정
                log.warn("Agent Serving 정보가 없어 리소스 정보를 설정할 수 없습니다. 기본값을 사용합니다.");
                result.put("cpu_request", null);
                result.put("cpu_limit", null);
                result.put("gpu_request", null);
                result.put("gpu_limit", null);
                result.put("mem_request", null);
                result.put("mem_limit", null);
                result.put("min_replicas", null);
                result.put("max_replicas", null);
            }
            
            // 커스텀 AGENT_APP에 필요한 추가 필드들
            // 1. model_list (AgentServingInfo에 있음)
            if (agentServingInfo != null && agentServingInfo.getModelList() != null) {
                result.put("model_list", agentServingInfo.getModelList());
                log.debug("model_list 추출 - 개수: {}", agentServingInfo.getModelList().size());
            } else if (agentServingResponse != null && agentServingResponse.getModelList() != null) {
                result.put("model_list", agentServingResponse.getModelList());
                log.debug("model_list 추출 (Response) - 개수: {}", agentServingResponse.getModelList().size());
            }
            
            // 2. image_url (AgentServingInfo의 agentAppImage 사용)
            if (agentServingInfo != null && agentServingInfo.getAgentAppImage() != null) {
                result.put("image_url", agentServingInfo.getAgentAppImage());
                log.debug("image_url 추출 - value: {}", agentServingInfo.getAgentAppImage());
            } else if (agentServingResponse != null && agentServingResponse.getAgentAppImage() != null) {
                result.put("image_url", agentServingResponse.getAgentAppImage());
                log.debug("image_url 추출 (Response) - value: {}", agentServingResponse.getAgentAppImage());
            }
            
            // 3. use_external_registry (AgentServingInfo의 agentAppImageRegistry가 있으면 true)
            if (agentServingInfo != null && agentServingInfo.getAgentAppImageRegistry() != null 
                    && !agentServingInfo.getAgentAppImageRegistry().trim().isEmpty()) {
                result.put("use_external_registry", true);
                log.debug("use_external_registry 추출 - value: true (agentAppImageRegistry: {})", 
                        agentServingInfo.getAgentAppImageRegistry());
            } else if (agentServingResponse != null && agentServingResponse.getAgentAppImageRegistry() != null 
                    && !agentServingResponse.getAgentAppImageRegistry().trim().isEmpty()) {
                result.put("use_external_registry", true);
                log.debug("use_external_registry 추출 (Response) - value: true (agentAppImageRegistry: {})", 
                        agentServingResponse.getAgentAppImageRegistry());
            } else {
                // agentAppImageRegistry가 없으면 기본값 true 설정 (커스텀 AGENT_APP의 기본값)
                result.put("use_external_registry", true);
                log.debug("use_external_registry 기본값 설정 - value: true");
            }
            
            // 4. workers_per_core (AgentServingInfo에 없으므로 기본값 3 설정 또는 null)
            
            // 버전 설명
            if (targetDeployment.getDescription() != null) {
                result.put("version_description", targetDeployment.getDescription());
            }
            
            log.info("Agent App 데이터 수집 완료 - appUuid: {}, 추출된 필드 수: {}", appUuid, result.size());
            
            return result;
            
        } catch (BusinessException e) {
            log.error("Agent App 데이터 수집 실패 (BusinessException) - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            throw e; // BusinessException은 그대로 재throw
        } catch (FeignException e) {
            log.error("Agent App 데이터 수집 실패 (FeignException) - appUuid: {}, error: {}", 
                    appUuid, e.getMessage(), e);
            return null;
        } catch (RuntimeException e) {
            log.error("Agent App 데이터 수집 실패 - appUuid: {}, error: {}", appUuid, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Agent Serving 객체에서 리소스 정보 추출
     * 
     * <p>AgentServingInfo 또는 AgentServingResponse에서 리소스 정보를 추출하여 result Map에 추가합니다.</p>
     * 
     * @param result 결과를 저장할 Map
     * @param servingObject AgentServingInfo 또는 AgentServingResponse 객체
     * @param objectType 객체 타입 (로깅용)
     */
    private void extractResourceInfoFromServing(Map<String, Object> result, Object servingObject, String objectType) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> servingMap = objectMapper.convertValue(servingObject, Map.class);
            log.debug("{}를 Map으로 변환 - keys: {}", objectType, servingMap.keySet());
            
            // 리소스 정보 추출 (문자열 키로 접근하여 타입 변환 문제 해결)
            result.put("cpu_request", extractIntegerValue(servingMap, "cpu_request"));
            result.put("cpu_limit", extractIntegerValue(servingMap, "cpu_limit"));
            result.put("gpu_request", extractIntegerValue(servingMap, "gpu_request"));
            result.put("gpu_limit", extractIntegerValue(servingMap, "gpu_limit"));
            result.put("mem_request", extractIntegerValue(servingMap, "mem_request"));
            result.put("mem_limit", extractIntegerValue(servingMap, "mem_limit"));
            
            // 스케일링 정보
            result.put("min_replicas", extractIntegerValue(servingMap, "min_replicas"));
            result.put("max_replicas", extractIntegerValue(servingMap, "max_replicas"));
            
            // 안전 필터 옵션
            Object safetyFilterInput = servingMap.get("safety_filter_input");
            Object safetyFilterOutput = servingMap.get("safety_filter_output");
            if (safetyFilterInput != null || safetyFilterOutput != null) {
                Map<String, Boolean> safetyFilterOptions = new LinkedHashMap<>();
                safetyFilterOptions.put("safety_filter_input", 
                        safetyFilterInput instanceof Boolean ? (Boolean) safetyFilterInput : 
                        (safetyFilterInput != null ? Boolean.parseBoolean(safetyFilterInput.toString()) : false));
                safetyFilterOptions.put("safety_filter_output", 
                        safetyFilterOutput instanceof Boolean ? (Boolean) safetyFilterOutput : 
                        (safetyFilterOutput != null ? Boolean.parseBoolean(safetyFilterOutput.toString()) : false));
                result.put("safety_filter_options", safetyFilterOptions);
            }
        } catch (RuntimeException e) {
            log.warn("{}를 Map으로 변환 실패 (RuntimeException) - 직접 getter 사용: {}", objectType, e.getMessage());
            // 변환 실패 시 직접 getter 사용 (타입별로 처리)
        } catch (Exception e) {
            log.warn("{}를 Map으로 변환 실패 - 직접 getter 사용: {}", objectType, e.getMessage());
            // 변환 실패 시 직접 getter 사용 (타입별로 처리)
            if (servingObject instanceof AgentServingInfo) {
                AgentServingInfo info = (AgentServingInfo) servingObject;
                result.put("cpu_request", info.getCpuRequest());
                result.put("cpu_limit", info.getCpuLimit());
                result.put("gpu_request", info.getGpuRequest());
                result.put("gpu_limit", info.getGpuLimit());
                result.put("mem_request", info.getMemRequest());
                result.put("mem_limit", info.getMemLimit());
                result.put("min_replicas", info.getMinReplicas());
                result.put("max_replicas", info.getMaxReplicas());
            } else if (servingObject instanceof AgentServingResponse) {
                AgentServingResponse response = (AgentServingResponse) servingObject;
                result.put("cpu_request", response.getCpuRequest());
                result.put("cpu_limit", response.getCpuLimit());
                result.put("gpu_request", response.getGpuRequest());
                result.put("gpu_limit", response.getGpuLimit());
                result.put("mem_request", response.getMemRequest());
                result.put("mem_limit", response.getMemLimit());
                result.put("min_replicas", response.getMinReplicas());
                result.put("max_replicas", response.getMaxReplicas());
            }
        }
    }
    
    /**
     * Map에서 Integer 값 추출 (타입 변환 지원)
     * 
     * <p>Map에서 값을 가져와서 Integer로 변환합니다.
     * 값이 이미 Integer이면 그대로 반환하고,
     * String이면 Integer로 파싱하고,
     * Number이면 intValue()로 변환합니다.</p>
     * 
     * @param map Map 객체
     * @param key 키
     * @return Integer 값 (없으면 null)
     */
    private Integer extractIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.warn("정수 변환 실패 - key: {}, value: {}", key, value);
                return null;
            }
        } else {
            log.warn("지원하지 않는 타입 - key: {}, value: {}, type: {}", key, value, value.getClass().getName());
            return null;
        }
    }
    
    /**
     * Agent App 데이터를 Import 형식으로 변환
     * 
     * <p>수집된 Agent App 데이터를 Import API에 맞는 형식으로 변환합니다.</p>
     * 
     * @param data Agent App 데이터 (Map 형태, collectAgentAppData의 결과)
     * @param isCustom 커스텀 AGENT_APP 여부
     * @return Import 형식의 JSON 문자열
     */
    @SuppressWarnings("unchecked")
    private String convertAgentAppToImportFormat(Object data, boolean isCustom, java.util.List<PolicyRequest> policyRequests) {
        try {
            log.info("Agent App Import 형식으로 변환 시작 - isCustom: {}", isCustom);
            
            if (!(data instanceof Map)) {
                throw new IllegalArgumentException("Agent App 데이터는 Map 형태여야 합니다.");
            }
            
            Map<String, Object> dataMap = (Map<String, Object>) data;
            
            log.debug("변환할 데이터 Map - keys: {}", dataMap.keySet());
            
            // AppCreateRequest 형식으로 변환 (null이 아닌 필드만 복사)
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            
            // 커스텀 AGENT_APP인 경우 추가 필드 확인 및 설정
            if (isCustom) {
                log.info("커스텀 AGENT_APP 형식으로 변환 - 추가 필드 확인");
                
                // target_type이 없으면 "external_graph"로 설정
                if (!result.containsKey("target_type") || result.get("target_type") == null) {
                    result.put("target_type", "external_graph");
                    log.info("커스텀 AGENT_APP - target_type을 'external_graph'로 설정");
                }
                
                // 커스텀 AGENT_APP에 필요한 추가 필드들이 없으면 기본값 설정 (필요시)
                // model_list, image_url, use_external_registry, workers_per_core 등은
                // collectAgentAppData에서 수집하지 않으므로, 여기서는 기본적으로 null로 두고
                // Import 시에 처리하도록 함
            }
            
            // Policy 추가 (있으면)
            if (policyRequests != null && !policyRequests.isEmpty()) {
                result.put("policy", policyRequests);
                log.debug("Policy 추가 - policyCount: {}", policyRequests.size());
            }
            
            log.debug("변환된 결과 Map - keys: {}, isCustom: {}", result.keySet(), isCustom);
            
            // JSON으로 변환
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            
            log.info("Agent App Import 형식으로 변환 완료 - isCustom: {}, jsonLength: {}", isCustom, json.length());
            log.info("=== Agent App Import JSON 생성 완료 ===");
            log.info("생성된 JSON 전체 내용:");
            log.info("{}", json);
            log.info("=== Agent App Import JSON 끝 ===");
            
            return json;
            
        } catch (JsonProcessingException e) {
            log.error("Agent App JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Agent App Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Agent App Import 형식 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("Agent App Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * App의 이전 배포 버전들 중지
     * 
     * <p>appId로 deployments 리스트를 조회하고, 최신 배포된 버전을 제외한 
     * Available 상태인 배포들을 중지합니다.</p>
     * 
     * @param appId Agent App UUID
     * @return 중지된 배포 ID 목록
     */
    public java.util.List<String> deleteOldDeployments(String appId) {
        try {
            log.info("이전 배포 버전 삭제 시작 - appId: {}", appId);
            
            // 1. App 배포 목록 조회
            AppDeploymentsResponse deploymentsResponse = sktaiAgentAppsService.getAppDeployments(
                    appId, 1, 1000, null, null, null);
            
            if (deploymentsResponse == null || deploymentsResponse.getData() == null || 
                    deploymentsResponse.getData().isEmpty()) {
                log.warn("배포 목록이 비어있습니다 - appId: {}", appId);
                return java.util.Collections.emptyList();
            }
            
            log.info("배포 목록 조회 완료 - 전체 배포 수: {}", deploymentsResponse.getData().size());
            
            // 2. Available 상태와 Stopped 상태인 배포들 필터링
            java.util.List<AppDeploymentsResponse.AppDeploymentInfo> availableDeployments = 
                    deploymentsResponse.getData().stream()
                    .filter(deployment -> deployment != null
                            && "Available".equals(deployment.getStatus() ) || "Stopped".equals(deployment.getStatus())
                            && deployment.getVersion() != null
                            && deployment.getId() != null)
                    .sorted((d1, d2) -> Integer.compare(d2.getVersion(), d1.getVersion())) // 버전 내림차순 정렬
                    .collect(java.util.stream.Collectors.toList());
            
            if (availableDeployments.isEmpty()) {
                log.info("Available 상태인 배포가 없습니다 - appId: {}", appId);
                return java.util.Collections.emptyList();
            }
            
            log.info("Available 배포 수: {}, 최신 버전: {}", 
                    availableDeployments.size(), 
                    availableDeployments.get(0).getVersion());
            
            // 3. 최신 배포(첫 번째) 제외하고 나머지 중지
            java.util.List<String> stoppedDeploymentIds = new java.util.ArrayList<>();
            
            // i = 1부터 시작 (인덱스 0은 최신 배포이므로 제외)
            for (int i = 1; i < availableDeployments.size(); i++) {
                AppDeploymentsResponse.AppDeploymentInfo deployment = availableDeployments.get(i);
                String deploymentId = deployment.getId();
                Integer version = deployment.getVersion();
                
                try {
                    log.info("배포 삭제 시도 - deploymentId: {}, version: {}", deploymentId, version);
                    sktaiAgentAppsService.deleteDeployment(deploymentId);
                    stoppedDeploymentIds.add(deploymentId);
                    log.info("배포 삭제 성공 - deploymentId: {}, version: {}", deploymentId, version);
                } catch (BusinessException e) {
                    log.error("배포 삭제 실패 (BusinessException) - deploymentId: {}, version: {}, error: {}", 
                            deploymentId, version, e.getMessage(), e);
                    // BusinessException은 그대로 재throw하지 않고 계속 진행
                } catch (FeignException e) {
                    log.error("배포 삭제 실패 (FeignException) - deploymentId: {}, version: {}, error: {}", 
                            deploymentId, version, e.getMessage(), e);
                    // FeignException도 계속 진행
                } catch (Exception e) {
                    log.error("배포 삭제 실패 (Exception) - deploymentId: {}, version: {}, error: {}", 
                            deploymentId, version, e.getMessage(), e);
                    // 예외 발생해도 다음 배포 계속 처리
                }
            }
            
            log.info("이전 배포 버전 중지 완료 - appId: {}, 중지된 배포 수: {}/{}", 
                    appId, stoppedDeploymentIds.size(), availableDeployments.size() - 1);
            
            return stoppedDeploymentIds;
            
        } catch (BusinessException e) {
            log.error("이전 배포 버전 중지 실패 (BusinessException) - appId: {}, error: {}", 
                    appId, e.getMessage(), e);
            throw e; // BusinessException은 그대로 재throw
        } catch (FeignException e) {
            log.error("이전 배포 버전 중지 실패 (FeignException) - appId: {}, error: {}", 
                    appId, e.getMessage(), e);
            throw new RuntimeException("이전 배포 버전 중지 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("이전 배포 버전 중지 실패 - appId: {}, error: {}", 
                    appId, e.getMessage(), e);
            throw new RuntimeException("이전 배포 버전 중지 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * AGENT_APP 타입 필드 추출
     * 
     * <p>isCustomAgentApp일 때만 image_url 필드를 추출합니다.</p>
     * 
     * @param jsonNode JSON 노드
     * @param id 파일 ID
     * @param fields 추출할 필드 목록 (image_url)
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, 
            List<String> fields, Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // isCustomAgentApp 확인
            boolean isCustom = isCustomAgentApp(id);
            
            log.info("AGENT_APP 필드 추출 시작 - id: {}, isCustom: {}, fields: {}", id, isCustom, fields);
            
            for (String field : fields) {
                // image_url과 app_id는 커스텀일 때만 추출
                if ("image_url".equals(field)) {
                    if (!isCustom) {
                        log.debug("AGENT_APP image_url 추출 건너뜀 (일반 AGENT_APP) - id: {}", id);
                        continue;
                    }
                    // image_url은 커스텀일 때만 추출
                    String fileValue = null;
                    
                    // JSON에서 image_url 추출 시도
                    // 1. 최상위 레벨에서 확인
                    if (jsonNode.has("image_url") && !jsonNode.get("image_url").isNull()) {
                        fileValue = jsonNode.get("image_url").asText();
                        log.debug("AGENT_APP image_url 추출 (최상위) - id: {}, value: {}", id, fileValue);
                    } 
                    // 2. deployments 배열에서 external_graph 타입의 deployment 찾기
                    else if (jsonNode.has("deployments") && jsonNode.get("deployments").isArray()) {
                        JsonNode deployments = jsonNode.get("deployments");
                        for (JsonNode deployment : deployments) {
                            if (deployment.has("target_type") 
                                    && "external_graph".equalsIgnoreCase(deployment.get("target_type").asText())) {
                                if (deployment.has("image_url") && !deployment.get("image_url").isNull()) {
                                    fileValue = deployment.get("image_url").asText();
                                    log.debug("AGENT_APP image_url 추출 (deployments) - id: {}, value: {}", id, fileValue);
                                    break;
                                }
                            }
                        }
                    }
                    
                    String dbValue = getValueFromDb.apply(field);
                    
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("dev", fileValue != null ? fileValue : "");
                    fieldMap.put("prod", dbValue != null ? dbValue : "");
                    result.put(field, fieldMap);
                    
                    log.info("AGENT_APP image_url 추출 완료 - id: {}, dev: {}, prod: {}", 
                            id, fileValue != null ? fileValue : "(없음)", dbValue != null ? dbValue : "(없음)");
                } else if ("app_id".equals(field)) {
                    if (!isCustom) {
                        log.debug("AGENT_APP app_id 추출 건너뜀 (일반 AGENT_APP) - id: {}", id);
                        continue;
                    }
                    // app_id는 커스텀일 때만 추출
                    // JSON에는 없고, DB에서만 조회 (GpoMigMas에서 직접 조회)
                    String fileValue = null; // JSON에는 없으므로 항상 null
                    
                    // DB에서 app_id 조회 (GpoMigMas에서 직접 조회)
                    String dbValue = getAppIdFromMigMas(id);
                    String dbValue2 = getValueFromDb.apply(field);
                    log.info("AGENT_APP app_id DB 조회 - id: {}, dbValue: {}", id, dbValue != null ? dbValue : "(없음)");
                    
                    // dbValue가 있으면 결과에 포함
                    // app_id는 DB에서 조회한 값이므로 dev에 넣음
                    if (dbValue != null && !dbValue.trim().isEmpty()) {
                        Map<String, String> fieldMap = new HashMap<>();
                        fieldMap.put("dev", dbValue != null ? dbValue : "");
                        fieldMap.put("prod", dbValue2 != null ? dbValue2 : "");
                        result.put(field, fieldMap);
                        
                        log.info("AGENT_APP app_id 추출 완료 - id: {}, dev: {}, prod: {}", 
                                id, dbValue, fileValue != null ? fileValue : "(없음)");
                    } else {
                        log.info("AGENT_APP app_id 추출 건너뜀 - DB에 값이 없음 - id: {}", id);
                    }
                } else {
                    // image_url과 app_id가 아닌 다른 필드들은 모두 추출
                    String fileValue = null;
                    if (jsonNode.has(field) && !jsonNode.get(field).isNull()) {
                        fileValue = jsonNode.get(field).asText();
                    }
                    
                    String dbValue = getValueFromDb.apply(field);
                    
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("dev", fileValue != null ? fileValue : "");
                    fieldMap.put("prod", dbValue != null ? dbValue : "");
                    result.put(field, fieldMap);
                    
                    log.debug("AGENT_APP {} 추출 완료 - id: {}, dev: {}, prod: {}", 
                            field, id, fileValue != null ? fileValue : "(없음)", dbValue != null ? dbValue : "(없음)");
                }
            }
            
        } catch (RuntimeException e) {
            log.error("AGENT_APP 필드 추출 실패 (RuntimeException) - id: {}, error: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("AGENT_APP 필드 추출 실패 - id: {}, error: {}", id, e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * GpoMigMas에서 app_id 조회
     * 
     * <p>
     * GpoMigMas의 uuid가 app_id와 매칭됩니다.
     * asstUuid로 GpoMigMas를 조회하여 uuid를 app_id로 반환합니다.
     * </p>
     * 
     * @param asstUuid 어시스트 UUID (파일의 id)
     * @return app_id (GpoMigMas의 uuid), 없으면 null
     */
    private String getAppIdFromMigMas(String asstUuid) {
        try {
            log.info("AGENT_APP :: app_id 조회 시작 - asstUuid: {}", asstUuid);
            
            // asstUuid로 GpoMigMas 직접 조회 (uuid가 asstUuid와 일치하는 경우)
            java.util.Optional<GpoMigMas> migMasOpt = gpoMigMasRepository.findByUuid(asstUuid);
            
            if (migMasOpt.isPresent()) {
                GpoMigMas migMas = migMasOpt.get();
                // GpoMigMas의 uuid가 app_id
                String appId = migMas.getUuid();
                log.info("AGENT_APP :: app_id 조회 성공 (GpoMigMas 직접 조회) - asstUuid: {}, appId: {}", 
                        asstUuid, appId);
                return appId != null ? appId : null;
            }
            
            log.warn("AGENT_APP :: app_id 조회 실패 - GpoMigMas를 찾을 수 없음 - asstUuid: {}", asstUuid);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // 여러 레코드가 있는 경우 예외 발생, 첫 번째 레코드 조회 시도
            log.warn("AGENT_APP :: app_id 조회 중 여러 레코드 발견 - asstUuid: {}, error: {}", asstUuid, e.getMessage());
            // Native Query로 첫 번째 레코드만 조회하거나, 다른 방법 사용
            // 일단 null 반환하고 나중에 개선 가능
            return null;
        } catch (Exception e) {
            log.error("AGENT_APP :: GpoMigMas에서 app_id 조회 실패 - asstUuid: {}, error: {}", asstUuid, e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * .env 파일 읽기 (custom_agent/{appUuid}/.env)
     * 
     * <p>base-dir 경로 아래 gapdat\migration\custom_agent\{appUuid}\.env 파일을 읽습니다.</p>
     * 
     * @param appUuid Agent App UUID
     * @return MultipartFile 객체 (파일이 없으면 null)
     */
    private MultipartFile readEnvFile(String appUuid) {
        try {
            // base-dir 경로 구성
            // String baseDir = getBaseDir();
            String baseDir = "/gapdat/migration";
            String separator = baseDir.contains("\\") ? "\\" : "/";
            
            // base-dir이 이미 gapdat/migration을 포함하는지 확인
            // base-dir이 비어있거나 기본값이면 gapdat/migration 추가
            String customAppBaseDir;
            if (baseDir.endsWith("gapdat" + separator + "migration") 
                    || baseDir.endsWith("gapdat\\migration")) {
                // 이미 포함되어 있으면 그대로 사용
                customAppBaseDir = baseDir;
            } else {
                // 포함되어 있지 않으면 추가
                customAppBaseDir = baseDir + separator + "gapdat" + separator + "migration";
            }
            
            // custom_agent/{appUuid}/.env 경로 구성
            String envFilePath = String.format("%s%scustom_agent%s%s%s.env",
                    customAppBaseDir, separator, separator, appUuid, separator);
            
            Path envFile = Paths.get(envFilePath);
            
            log.info("AGENT_APP :: .env 파일 경로 확인 - path: {}", envFile.toAbsolutePath());
            
            if (!Files.exists(envFile) || !Files.isRegularFile(envFile)) {
                log.debug("AGENT_APP :: .env 파일이 없습니다 - path: {}", envFile.toAbsolutePath());
                return null;
            }
            
            // 파일 읽기
            byte[] fileContent = Files.readAllBytes(envFile);
            log.info("AGENT_APP :: .env 파일 읽기 완료 - path: {}, size: {} bytes", 
                    envFile.toAbsolutePath(), fileContent.length);
            
            // MultipartFile로 변환
            return new ByteArrayMultipartFile("env_file", ".env", "text/plain", fileContent);
            
        } catch (IOException e) {
            log.warn("AGENT_APP :: .env 파일 읽기 실패 - appUuid: {}, error: {}", appUuid, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("AGENT_APP :: .env 파일 읽기 중 예외 발생 - appUuid: {}, error: {}", appUuid, e.getMessage());
            return null;
        }
    }
    
    /**
     * ByteArray 기반의 간단한 MultipartFile 구현
     */
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        
        public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name != null ? name : "file";
            this.originalFilename = originalFilename != null ? originalFilename : "file";
            this.contentType = contentType != null ? contentType : "application/octet-stream";
            this.content = content != null ? content : new byte[0];
        }
        
        @Override
        public String getName() {
            return this.name != null ? this.name : "file";
        }
        
        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }
        
        @Override
        public String getContentType() {
            return this.contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return this.content == null || this.content.length == 0;
        }
        
        @Override
        public long getSize() {
            return this.content != null ? this.content.length : 0;
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            if (this.content == null) {
                return new byte[0];
            }
            return java.util.Arrays.copyOf(this.content, this.content.length);
        }
        
        @Override
        public java.io.InputStream getInputStream() throws IOException {
            if (this.content == null) {
                return new java.io.ByteArrayInputStream(new byte[0]);
            }
            return new java.io.ByteArrayInputStream(this.content);
        }
        
        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            if (dest == null) {
                throw new IllegalArgumentException("Destination file must not be null");
            }
            if (this.content == null) {
                return;
            }
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                fos.write(this.content);
            }
        }
    }
    
    // ========== 배포 상태 Enum ==========
    
    /**
     * 배포 상태를 나타내는 열거형
     */
    public enum DeploymentStatus {
        Available,  // 배포 성공
        Failed,     // 배포 실패
        Timeout     // 타임아웃
    }
    
    // ========== 비동기 배포 삭제 로직 ==========
    
    /**
     * 새 배포가 Available 상태가 될 때까지 대기 후 이전 배포들을 삭제합니다.
     * 
     * <p>비동기로 실행되며, 실패 시 이전 배포를 유지합니다.</p>
     * 
     * @param appId Agent App ID
     * @param newDeploymentId 새로 생성된 배포 ID
     */
    @Async
    public void deleteOldDeploymentsAfterAvailable(String appId, String newDeploymentId) {
        log.info("비동기 배포 삭제 시작 - appId: {}, newDeploymentId: {}", appId, newDeploymentId);
        
        try {
            // 1. 새 배포가 Available 될 때까지 대기 (최대 5분)
            DeploymentStatus status = waitForDeploymentStatus(newDeploymentId, 300);
            
            switch (status) {
                case Available:
                    // 성공 → 이전 배포들 삭제
                    log.info("새 배포 Available 확인 - deploymentId: {}, 이전 배포 삭제 시작", newDeploymentId);
                    deleteOldDeployments(appId);
                    log.info("이전 배포 삭제 완료 - appId: {}", appId);
                    break;
                    
                case Failed:
                    // 배포 실패 → 삭제 안함, 로그만
                    log.error("새 배포 실패 - deploymentId: {}, 이전 배포 유지", newDeploymentId);
                    break;
                    
                default:
                    // 나머지 → 삭제 안함
                    log.warn("배포 상태 확인시 예상치 못한 상태 - deploymentId: {}, 이전 배포 유지", newDeploymentId);
                    break;
            }
            
        } catch (Exception e) {
            // 예외 발생 → 안전하게 이전 배포 유지
            log.error("비동기 배포 삭제 중 예외 발생 - appId: {}, newDeploymentId: {}, error: {}", 
                    appId, newDeploymentId, e.getMessage(), e);
        }
    }
    
    /**
     * 배포가 특정 상태가 될 때까지 대기합니다.
     * 
     * @param deploymentId 배포 ID
     * @param maxWaitSeconds 최대 대기 시간(초)
     * @return 배포 상태 (AVAILABLE, FAILED, TIMEOUT)
     */
    private DeploymentStatus waitForDeploymentStatus(String deploymentId, int maxWaitSeconds) {
        int waited = 0;
        int pollIntervalMs = 5000; // 5초마다 확인
        
        log.info("배포 상태 대기 시작 - deploymentId: {}, maxWait: {}초", deploymentId, maxWaitSeconds);
        
        while (waited < maxWaitSeconds * 1000) {
            try {
                AppDeploymentResponse response = sktaiAgentAppsService.getDeployment(deploymentId);
                String status = response.getData().getStatus();
                
                log.debug("배포 상태 확인 - deploymentId: {}, 현재상태: {}, 경과: {}초", 
                        deploymentId, status, waited / 1000);
                
                if ("Available".equals(status)) {
                    log.info("배포 Available 상태 확인 - deploymentId: {}, 소요시간: {}초", 
                            deploymentId, waited / 1000);
                    return DeploymentStatus.Available;
                } else if ("Failed".equals(status) || "Error".equals(status)) {
                    log.error("배포 실패 상태 확인 - deploymentId: {}, status: {}", deploymentId, status);
                    return DeploymentStatus.Failed;
                }
                
                Thread.sleep(pollIntervalMs);
                waited += pollIntervalMs;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("배포 상태 대기 중 인터럽트 - deploymentId: {}", deploymentId);
                return DeploymentStatus.Failed;
            } catch (Exception e) {
                log.warn("배포 상태 확인 실패, 재시도 - deploymentId: {}, error: {}", deploymentId, e.getMessage());
                // 일시적 오류는 재시도
                try {
                    Thread.sleep(pollIntervalMs);
                    waited += pollIntervalMs;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return DeploymentStatus.Failed;
                }
            }
        }
        
        log.warn("배포 상태 확인 타임아웃 - deploymentId: {}, maxWait: {}초", deploymentId, maxWaitSeconds);
        return DeploymentStatus.Timeout;
    }
}

