package com.skax.aiplatform.client.sktai.agent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentAppsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppApiKeyCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppApiKeyRegenerateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppCustomDeploymentAddRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppHardDeleteRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeyCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeysResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDetailResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppHardDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppsResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Agent Apps API 서비스
 * 
 * <p>SKTAI Agent 시스템의 애플리케이션 관리 API를 호출하는 비즈니스 로직을 담당합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentAppsService {
    
    private final SktaiAgentAppsClient sktaiAgentAppsClient;
    
    /**
     * Agent Apps 목록 조회
     */
    public AppsResponse getApps(String targetType, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Agent Apps 목록 조회 요청 - page: {}, size: {}", page, size);
            AppsResponse response = sktaiAgentAppsClient.getApps(targetType, page, size, sort, filter, search);
            log.debug("Agent Apps 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent Apps 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", 
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent Apps 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Apps 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 새로운 Agent App 생성
     */
    public AppCreateResponse createApp(AppCreateRequest request) {
        try {
            log.debug("Agent App 생성 요청 - name: {}", request.getName());
            AppCreateResponse response = sktaiAgentAppsClient.createApp(request);
            log.debug("Agent App 생성 성공 - appUuid: {}", response.getData().getAppId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            // "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황일 수 있음
            if (e.getMessage() != null && e.getMessage().contains("Entity를 찾을 수 없습니다")) {
                log.warn("Agent App 생성 실패 (BusinessException - Entity 없음, Import 시 정상적일 수 있음) - name: {}, message: {}", 
                        request.getName(), e.getMessage());
            } else {
                log.error("Agent App 생성 실패 (BusinessException) - name: {}, message: {}", 
                        request.getName(), e.getMessage());
            }
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App 생성 실패 (예상치 못한 오류) - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "App 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App 상세 정보 조회
     */
    public AppResponse getApp(String appUuid) {
        try {
            log.info("=== SKTAI Agent App 상세 조회 시작 ===");
            log.info("요청 appUuid: {}", appUuid);
            
            AppDetailResponse response = sktaiAgentAppsClient.getApp(appUuid);
            log.info("SKTAI API 응답: {}", response);
            
            if (response != null && response.getData() != null) {
                AppResponse appResponse = response.getData();
                log.info("응답 ID: {}", appResponse.getId());
                log.info("응답 Name: {}", appResponse.getName());
                return appResponse;
            } else {
                log.warn("API 응답 또는 data가 null입니다");
                return null;
            }
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App 상세 조회 실패 (BusinessException) - appUuid: {}, message: {}", 
                    appUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App 상세 조회 실패 (예상치 못한 오류) - appUuid: {}", appUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "App 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App 정보 수정
     */
    public AppUpdateOrDeleteResponse updateApp(String appUuid, AppUpdateRequest request) {
        try {
            log.debug("Agent App 수정 요청 - appUuid: {}, name: {}", appUuid, request.getName());
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.updateApp(appUuid, request);
            log.debug("Agent App 수정 성공 - appUuid: {}", appUuid);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App 수정 실패 (BusinessException) - appUuid: {}, message: {}", 
                    appUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App 수정 실패 (예상치 못한 오류) - appUuid: {}", appUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "App 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App 삭제
     */
    public AppUpdateOrDeleteResponse deleteApp(String appUuid) {
        try {
            log.debug("Agent App 삭제 요청 - appUuid: {}", appUuid);
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.deleteApp(appUuid);
            log.debug("Agent App 삭제 성공 - appUuid: {}", appUuid);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App 삭제 실패 (BusinessException) - appUuid: {}, message: {}", 
                    appUuid, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App 삭제 실패 (예상치 못한 오류) - appUuid: {}", appUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "App 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent Apps 하드 삭제
     */
    public AppHardDeleteResponse hardDeleteApps(AppHardDeleteRequest request) {
        try {
            log.debug("Agent Apps 하드 삭제 요청 - 앱 수: {}", request.getAppIds().size());
            AppHardDeleteResponse response = sktaiAgentAppsClient.hardDeleteApps(request);
            log.debug("Agent Apps 하드 삭제 성공 - 삭제된 앱 수: {}", response.getDeletedCount());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent Apps 하드 삭제 실패 (BusinessException) - 앱 수: {}, message: {}", 
                    request.getAppIds().size(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent Apps 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Apps 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App 배포 목록 조회
     */
    public AppDeploymentsResponse getAppDeployments(String appId, Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Agent App 배포 목록 조회 요청 - appId: {}", appId);
            AppDeploymentsResponse response = sktaiAgentAppsClient.getAppDeployments(appId, page, size, sort, filter, search);
            log.debug("Agent App 배포 목록 조회 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App 배포 목록 조회 실패 (BusinessException) - appId: {}, message: {}", 
                    appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App 배포 목록 조회 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "App 배포 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 커스텀 Agent App 생성 및 배포
     */
    public AppCreateResponse createCustomApp(
            org.springframework.web.multipart.MultipartFile envFile,
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
            List<PolicyRequest> policy) {
        try {
            log.debug("커스텀 Agent App 생성 요청 - name: {}", name);
            AppCreateResponse response = sktaiAgentAppsClient.createCustomApp(
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
                policy
            );
            log.debug("커스텀 Agent App 생성 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            // "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황일 수 있음
            if (e.getMessage() != null && e.getMessage().contains("Entity를 찾을 수 없습니다")) {
                log.warn("커스텀 Agent App 생성 실패 (BusinessException - Entity 없음, Import 시 정상적일 수 있음) - name: {}, message: {}", 
                        name, e.getMessage());
            } else {
                log.error("커스텀 Agent App 생성 실패 (BusinessException) - name: {}, message: {}", 
                        name, e.getMessage());
            }
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("커스텀 Agent App 생성 실패 - name: {}", name, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "커스텀 App 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 커스텀 배포 추가 (Multipart)
     */
    public AppDeploymentResponse addCustomDeploymentWithMultipart(
            String appId,
            org.springframework.web.multipart.MultipartFile envFile,
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
            java.util.List<com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest> policy) {
        try {
            log.debug("커스텀 배포 추가 요청 (Multipart) - appId: {}, name: {}", appId, name);
            AppDeploymentResponse response = sktaiAgentAppsClient.addCustomDeploymentWithMultipart(
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
                policy
            );
            log.debug("커스텀 배포 추가 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("커스텀 배포 추가 실패 (BusinessException) - appId: {}, message: {}", appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("커스텀 배포 추가 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "커스텀 배포 추가에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 커스텀 배포 추가 (JSON)
     */
    public AppDeploymentResponse addCustomDeployment(AppCustomDeploymentAddRequest request) {
        try {
            log.debug("커스텀 배포 추가 요청 - appId: {}, deploymentName: {}", request.getAppId(), request.getDeploymentName());
            AppDeploymentResponse response = sktaiAgentAppsClient.addCustomDeployment(request);
            log.debug("커스텀 배포 추가 성공 - deploymentId: {}", response.getData().getId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("커스텀 배포 추가 실패 (BusinessException) - appId: {}, message: {}", 
                    request.getAppId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("커스텀 배포 추가 실패 - appId: {}", request.getAppId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "커스텀 배포 추가에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 배포 중지
     */
    public AppUpdateOrDeleteResponse stopDeployment(String deploymentId) {
        try {
            log.debug("배포 중지 요청 - deploymentId: {}", deploymentId);
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.stopDeployment(deploymentId);
            log.debug("배포 중지 성공 - deploymentId: {}", deploymentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("배포 중지 실패 (BusinessException) - deploymentId: {}, message: {}", 
                    deploymentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("배포 중지 실패 - deploymentId: {}", deploymentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "배포 중지에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 배포 재시작
     */
    public AppUpdateOrDeleteResponse restartDeployment(String deploymentId) {
        try {
            log.debug("배포 재시작 요청 - deploymentId: {}", deploymentId);
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.restartDeployment(deploymentId);
            log.debug("배포 재시작 성공 - deploymentId: {}", deploymentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("배포 재시작 실패 (BusinessException) - deploymentId: {}, message: {}", 
                    deploymentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("배포 재시작 실패 - deploymentId: {}", deploymentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "배포 재시작에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 배포 삭제
     */
    public AppUpdateOrDeleteResponse deleteDeployment(String deploymentId) {
        try {
            log.debug("배포 삭제 요청 - deploymentId: {}", deploymentId);
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.deleteDeployment(deploymentId);
            log.debug("배포 삭제 성공 - deploymentId: {}", deploymentId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("배포 삭제 실패 (BusinessException) - deploymentId: {}, message: {}", 
                    deploymentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("배포 삭제 실패 - deploymentId: {}", deploymentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "배포 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 배포 상세 조회
     */
    public AppDeploymentResponse getDeployment(String deploymentId) {
        try {
            log.debug("배포 상세 조회 요청 - deploymentId: {}", deploymentId);
            AppDeploymentResponse response = sktaiAgentAppsClient.getDeployment(deploymentId);
            log.debug("배포 상세 조회 성공 - deploymentId response: {}", response);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("배포 상세 조회 실패 (BusinessException) - deploymentId: {}, message: {}", 
                    deploymentId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("배포 상세 조회 실패 - deploymentId: {}", deploymentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "배포 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App API 키 목록 조회
     */
    public AppApiKeysResponse getAppApiKeys(String appId) {
        try {
            log.debug("Agent App API 키 목록 조회 요청 - appId: {}", appId);
            AppApiKeysResponse response = sktaiAgentAppsClient.getAppApiKeys(appId);
            log.debug("Agent App API 키 목록 조회 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App API 키 목록 조회 실패 (BusinessException) - appId: {}, message: {}", 
                    appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App API 키 목록 조회 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App API 키 생성
     */
    public AppApiKeyCreateResponse createAppApiKey(String appId, AppApiKeyCreateRequest request) {
        try {
            log.info("Agent App API 키 생성 요청 - appId: {}, request: {}", appId, request);
            
            // AppApiKeyCreateRequest의 policy를 배열로 변환하여 전송
            List<PolicyRequest> policyList = request.getPolicy() != null ? request.getPolicy() : new java.util.ArrayList<>();
            log.info("정책 목록 변환 완료 - policyList size: {}", policyList.size());
            
            if (policyList.isEmpty()) {
                log.warn("정책 목록이 비어있습니다. 빈 배열로 API 키 생성 요청을 전송합니다.");
            } else {
                log.debug("정책 목록 상세: {}", policyList);
            }
            
            AppApiKeyCreateResponse response = sktaiAgentAppsClient.createAppApiKey(appId, policyList);
            
            log.info("Agent App API 키 생성 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App API 키 생성 실패 (BusinessException) - appId: {}, message: {}", 
                    appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App API 키 생성 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App API 키 재생성
     */
    public AppApiKeyCreateResponse regenerateAppApiKey(String appId, String apiKey, AppApiKeyRegenerateRequest request) {
        try {
            log.debug("Agent App API 키 재생성 요청 - appId: {}, apiKey: {}", appId, apiKey);
            AppApiKeyCreateResponse response = sktaiAgentAppsClient.regenerateAppApiKey(appId, apiKey, request);
            log.debug("Agent App API 키 재생성 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App API 키 재생성 실패 (BusinessException) - appId: {}, message: {}", 
                    appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App API 키 재생성 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 재생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Agent App API 키 삭제
     */
    public AppUpdateOrDeleteResponse deleteAppApiKey(String appId, String apiKey) {
        try {
            log.debug("Agent App API 키 삭제 요청 - appId: {}, apiKey: {}", appId, apiKey);
            AppUpdateOrDeleteResponse response = sktaiAgentAppsClient.deleteAppApiKey(appId, apiKey);
            log.debug("Agent App API 키 삭제 성공 - appId: {}", appId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Agent App API 키 삭제 실패 (BusinessException) - appId: {}, message: {}", 
                    appId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Agent App API 키 삭제 실패 - appId: {}", appId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델별 Agent App 목록 조회
     */
    public AppsResponse getAppsByModel(String modelName) {
        try {
            log.debug("모델별 Agent App 목록 조회 요청 - modelName: {}", modelName);
            AppsResponse response = sktaiAgentAppsClient.getAppsByModel(modelName);
            log.debug("모델별 Agent App 목록 조회 성공 - modelName: {}", modelName);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델별 Agent App 목록 조회 실패 (BusinessException) - modelName: {}, message: {}", 
                    modelName, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델별 Agent App 목록 조회 실패 - modelName: {}", modelName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델별 App 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 지식베이스별 Agent App 목록 조회
     */
    public AppsResponse getAppsByKnowledge(String knowledgeId) {
        try {
            log.debug("지식베이스별 Agent App 목록 조회 요청 - knowledgeId: {}", knowledgeId);
            AppsResponse response = sktaiAgentAppsClient.getAppsByKnowledge(knowledgeId);
            log.debug("지식베이스별 Agent App 목록 조회 성공 - knowledgeId: {}", knowledgeId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("지식베이스별 Agent App 목록 조회 실패 (BusinessException) - knowledgeId: {}, message: {}", 
                    knowledgeId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("지식베이스별 Agent App 목록 조회 실패 - knowledgeId: {}", knowledgeId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "지식베이스별 App 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Phoenix 프로젝트 ID 조회
     */
    public Object getPhoenixProjectId(String projectName) {
        try {
            log.debug("Phoenix 프로젝트 ID 조회 요청 - projectName: {}", projectName);
            Object response = sktaiAgentAppsClient.getPhoenixProjectId(projectName);
            log.debug("Phoenix 프로젝트 ID 조회 성공 - projectName: {}", projectName);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Phoenix 프로젝트 ID 조회 실패 (BusinessException) - projectName: {}, message: {}", 
                    projectName, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Phoenix 프로젝트 ID 조회 실패 - projectName: {}", projectName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Phoenix 프로젝트 ID 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
