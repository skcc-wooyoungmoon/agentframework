package com.skax.aiplatform.client.sktai.resource.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.resource.SktaiResourceClient;
import com.skax.aiplatform.client.sktai.resource.dto.request.ResourceAllocationRequest;
import com.skax.aiplatform.client.sktai.resource.dto.request.ResourceMonitorRequest;
import com.skax.aiplatform.client.sktai.resource.dto.request.ResourceScalingRequest;
import com.skax.aiplatform.client.sktai.resource.dto.response.CostAnalysisResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.OptimizationRecommendationsResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.ResourceAllocationResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.ResourceListResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.ResourceUsageResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskPolicyResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskResourceResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



/**
 * SKTAI Resource Management Service
 * 
 * <p>SKTAI Resource Management API 호출을 위한 비즈니스 로직 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 로직을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Resource Monitoring</strong>: 리소스 사용량 모니터링 및 분석</li>
 *   <li><strong>Resource Allocation</strong>: 컴퓨팅 리소스 할당 및 해제</li>
 *   <li><strong>Auto Scaling</strong>: 자동 스케일링 관리</li>
 *   <li><strong>Cost Management</strong>: 비용 분석 및 최적화</li>
 *   <li><strong>Performance Optimization</strong>: 성능 최적화 및 튜닝</li>
 *   <li><strong>Error Handling</strong>: 외부 API 오류 처리</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>인프라 관리</strong>: 클라우드 리소스 자동화</li>
 *   <li><strong>AI/ML 워크로드</strong>: 모델 학습/추론 리소스 관리</li>
 *   <li><strong>마이크로서비스</strong>: 서비스별 리소스 최적화</li>
 *   <li><strong>데이터 파이프라인</strong>: 배치 처리 리소스 스케줄링</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiResourceService {
    
    private final SktaiResourceClient resourceClient;
    
    // ==================== Resource Monitoring ====================
    
    /**
     * 리소스 사용량 조회
     * 
     * @param request 모니터링 요청
     * @return 리소스 사용량 정보
     */
    public ResourceUsageResponse getResourceUsage(ResourceMonitorRequest request) {
        log.debug("리소스 사용량 조회 요청 - types: {}, interval: {}", 
                 request.getResourceTypes(), request.getInterval());
        
        try {
            ResourceUsageResponse response = resourceClient.getResourceUsage(request);
            log.debug("리소스 사용량 조회 성공 - resourceId: {}, status: {}, cpu: {}%", 
                     response.getResourceId(), response.getStatus(),
                     response.getCurrentUsage() != null ? 
                         response.getCurrentUsage().get("cpu_percent") : "N/A");
            
            // 임계값 위반 경고 로깅
            if (response.getAlerts() != null && !response.getAlerts().isEmpty()) {
                log.warn("리소스 알림 발생 - resourceId: {}, alerts: {}", 
                        response.getResourceId(), response.getAlerts().size());
            }
            
            return response;
        } catch (BusinessException e) {
            log.error("리소스 사용량 조회 실패 (BusinessException) - types: {}, message: {}", 
                     request.getResourceTypes(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 사용량 조회 실패 (예상치 못한 오류) - types: {}", request.getResourceTypes(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 사용량 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 리소스 사용량 조회
     * 
     * @param resourceId 리소스 ID
     * @param interval 수집 간격
     * @param duration 조회 기간
     * @return 리소스 사용량 정보
     */
    public ResourceUsageResponse getResourceUsageById(String resourceId, String interval, String duration) {
        log.debug("특정 리소스 사용량 조회 - resourceId: {}, interval: {}, duration: {}", 
                 resourceId, interval, duration);
        
        try {
            ResourceUsageResponse response = resourceClient.getResourceUsageById(resourceId, interval, duration);
            log.debug("특정 리소스 사용량 조회 성공 - resourceId: {}, status: {}", 
                     response.getResourceId(), response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("특정 리소스 사용량 조회 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("특정 리소스 사용량 조회 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 사용량 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Resource Management ====================
    
    /**
     * 리소스 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param resourceType 리소스 타입 필터
     * @param status 상태 필터
     * @param projectId 프로젝트 ID 필터
     * @param region 지역 필터
     * @param sortBy 정렬 기준
     * @param sortOrder 정렬 순서
     * @return 리소스 목록
     */
    public ResourceListResponse getResources(Integer page, Integer size, String resourceType, 
                                           String status, String projectId, String region, 
                                           String sortBy, String sortOrder) {
        log.debug("리소스 목록 조회 - page: {}, size: {}, type: {}, status: {}, project: {}", 
                 page, size, resourceType, status, projectId);
        
        try {
            ResourceListResponse response = resourceClient.getResources(
                page, size, resourceType, status, projectId, region, sortBy, sortOrder);
            log.debug("리소스 목록 조회 성공 - total: {}, count: {}, hasMore: {}", 
                     response.getTotalCount(), 
                     response.getData() != null ? response.getData().size() : 0,
                     response.getHasMore());
            return response;
        } catch (BusinessException e) {
            log.error("리소스 목록 조회 실패 (BusinessException) - page: {}, type: {}, project: {}, message: {}", 
                     page, resourceType, projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 목록 조회 실패 (예상치 못한 오류) - page: {}, type: {}, project: {}", 
                     page, resourceType, projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 리소스 상세 조회
     * 
     * @param resourceId 리소스 ID
     * @return 리소스 상세 정보
     */
    public ResourceAllocationResponse getResource(String resourceId) {
        log.debug("리소스 상세 조회 - resourceId: {}", resourceId);
        
        try {
            ResourceAllocationResponse response = resourceClient.getResource(resourceId);
            log.debug("리소스 상세 조회 성공 - resourceId: {}, type: {}, status: {}", 
                     response.getResourceId(), response.getResourceType(), response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("리소스 상세 조회 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 상세 조회 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Resource Allocation ====================
    
    /**
     * 리소스 할당
     * 
     * @param request 리소스 할당 요청
     * @return 할당된 리소스 정보
     */
    public ResourceAllocationResponse allocateResource(ResourceAllocationRequest request) {
        log.debug("리소스 할당 요청 - type: {}, quantity: {}, project: {}", 
                 request.getResourceType(), request.getQuantity(), request.getProjectId());
        
        try {
            ResourceAllocationResponse response = resourceClient.allocateResource(request);
            log.debug("리소스 할당 성공 - allocationId: {}, resourceId: {}, status: {}", 
                     response.getAllocationId(), response.getResourceId(), response.getStatus());
            
            // 비용 정보 로깅
            if (response.getCostInfo() != null) {
                Object hourlyRate = ((Map<String, Object>) response.getCostInfo()).get("hourly_rate");
                log.debug("할당된 리소스 시간당 비용: {}", hourlyRate);
            }
            
            return response;
        } catch (BusinessException e) {
            log.error("리소스 할당 실패 (BusinessException) - type: {}, quantity: {}, project: {}, message: {}", 
                     request.getResourceType(), request.getQuantity(), request.getProjectId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 할당 실패 (예상치 못한 오류) - type: {}, quantity: {}, project: {}", 
                     request.getResourceType(), request.getQuantity(), request.getProjectId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 할당에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 리소스 할당 해제
     * 
     * @param resourceId 리소스 ID
     * @param force 강제 해제 여부
     * @param backup 백업 여부
     */
    public void deallocateResource(String resourceId, Boolean force, Boolean backup) {
        log.debug("리소스 할당 해제 요청 - resourceId: {}, force: {}, backup: {}", 
                 resourceId, force, backup);
        
        try {
            resourceClient.deallocateResource(resourceId, force, backup);
            log.debug("리소스 할당 해제 성공 - resourceId: {}", resourceId);
        } catch (BusinessException e) {
            log.error("리소스 할당 해제 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 할당 해제 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 할당 해제에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Resource Scaling ====================
    
    /**
     * 리소스 스케일링
     * 
     * @param resourceId 리소스 ID
     * @param request 스케일링 요청
     * @return 스케일링된 리소스 정보
     */
    public ResourceAllocationResponse scaleResource(String resourceId, ResourceScalingRequest request) {
        log.debug("리소스 스케일링 요청 - resourceId: {}, action: {}, type: {}, target: {}", 
                 resourceId, request.getScalingAction(), request.getScalingType(), 
                 request.getTargetCapacity());
        
        try {
            ResourceAllocationResponse response = resourceClient.scaleResource(resourceId, request);
            log.debug("리소스 스케일링 성공 - resourceId: {}, status: {}", 
                     response.getResourceId(), response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("리소스 스케일링 실패 (BusinessException) - resourceId: {}, action: {}, message: {}", 
                     resourceId, request.getScalingAction(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 스케일링 실패 (예상치 못한 오류) - resourceId: {}, action: {}", 
                     resourceId, request.getScalingAction(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "리소스 스케일링에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 자동 스케일링 설정
     * 
     * @param resourceId 리소스 ID
     * @param policy 스케일링 정책
     */
    public void setAutoScalingPolicy(String resourceId, Object policy) {
        log.debug("자동 스케일링 설정 - resourceId: {}", resourceId);
        
        try {
            resourceClient.setAutoScalingPolicy(resourceId, policy);
            log.debug("자동 스케일링 설정 성공 - resourceId: {}", resourceId);
        } catch (BusinessException e) {
            log.error("자동 스케일링 설정 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("자동 스케일링 설정 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "자동 스케일링 설정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 자동 스케일링 해제
     * 
     * @param resourceId 리소스 ID
     */
    public void disableAutoScaling(String resourceId) {
        log.debug("자동 스케일링 해제 - resourceId: {}", resourceId);
        
        try {
            resourceClient.disableAutoScaling(resourceId);
            log.debug("자동 스케일링 해제 성공 - resourceId: {}", resourceId);
        } catch (BusinessException e) {
            log.error("자동 스케일링 해제 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("자동 스케일링 해제 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "자동 스케일링 해제에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Resource Optimization ====================
    
    /**
     * 리소스 최적화 제안 조회
     * 
     * @param projectId 프로젝트 ID
     * @param resourceType 리소스 타입
     * @param days 분석 기간
     * @return 최적화 제안 목록
     */
    public OptimizationRecommendationsResponse getOptimizationRecommendations(String projectId, String resourceType, Integer days) {
        log.debug("리소스 최적화 제안 조회 - project: {}, type: {}, days: {}", 
                 projectId, resourceType, days);
        
        try {
            OptimizationRecommendationsResponse recommendations = resourceClient.getOptimizationRecommendations(
                projectId, resourceType, days);
            log.debug("리소스 최적화 제안 조회 성공 - project: {}, type: {}", 
                     projectId, resourceType);
            return recommendations;
        } catch (BusinessException e) {
            log.error("리소스 최적화 제안 조회 실패 (BusinessException) - project: {}, type: {}, message: {}", 
                     projectId, resourceType, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 최적화 제안 조회 실패 (예상치 못한 오류) - project: {}, type: {}", 
                     projectId, resourceType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "최적화 제안 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 리소스 비용 분석
     * 
     * @param projectId 프로젝트 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param groupBy 그룹화 기준
     * @return 비용 분석 결과
     */
    public CostAnalysisResponse getCostAnalysis(String projectId, String startDate, String endDate, String groupBy) {
        log.debug("리소스 비용 분석 - project: {}, period: {} ~ {}, groupBy: {}", 
                 projectId, startDate, endDate, groupBy);
        
        try {
            CostAnalysisResponse analysis = resourceClient.getCostAnalysis(projectId, startDate, endDate, groupBy);
            log.debug("리소스 비용 분석 성공 - project: {}, period: {} ~ {}", 
                     projectId, startDate, endDate);
            return analysis;
        } catch (BusinessException e) {
            log.error("리소스 비용 분석 실패 (BusinessException) - project: {}, period: {} ~ {}, message: {}", 
                     projectId, startDate, endDate, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("리소스 비용 분석 실패 (예상치 못한 오류) - project: {}, period: {} ~ {}", 
                     projectId, startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "비용 분석에 실패했습니다: " + e.getMessage());
        }
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * 리소스 상태 확인
     * 
     * @param resourceId 리소스 ID
     * @return 리소스 활성 상태 여부
     */
    public boolean isResourceActive(String resourceId) {
        try {
            ResourceAllocationResponse resource = getResource(resourceId);
            return "active".equals(resource.getStatus());
        } catch (BusinessException e) {
            log.warn("리소스 상태 확인 실패 (BusinessException) - resourceId: {}, message: {}", 
                     resourceId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("리소스 상태 확인 실패 (예상치 못한 오류) - resourceId: {}", resourceId, e);
            return false;
        }
    }
    
    /**
     * 프로젝트별 리소스 요약 조회
     * 
     * @param projectId 프로젝트 ID
     * @return 리소스 요약 정보
     */
    public ResourceListResponse getProjectResourceSummary(String projectId) {
        return getResources(1, 100, null, "active", projectId, null, "usage_percent", "desc");
    }
    
    /**
     * 고사용률 리소스 조회
     * 
     * @param threshold 사용률 임계값 (기본값: 80%)
     * @return 고사용률 리소스 목록
     */
    public ResourceListResponse getHighUtilizationResources(Double threshold) {
        log.debug("고사용률 리소스 조회 - threshold: {}%", threshold);
        
        try {
            // 모든 활성 리소스를 조회한 후 필터링
            ResourceListResponse allResources = getResources(1, 100, null, "active", 
                                                           null, null, "usage_percent", "desc");
            
            if (allResources.getData() != null) {
                List<ResourceListResponse.ResourceSummary> highUtilResources = 
                    allResources.getData().stream()
                        .filter(resource -> resource.getUsagePercent() != null && 
                                          resource.getUsagePercent() >= (threshold != null ? threshold : 80.0))
                        .toList();
                
                allResources.setData(highUtilResources);
                allResources.setTotalCount(highUtilResources.size());
                
                log.debug("고사용률 리소스 조회 성공 - count: {}", highUtilResources.size());
            }
            
            return allResources;
        } catch (BusinessException e) {
            log.error("고사용률 리소스 조회 실패 (BusinessException) - threshold: {}, message: {}", 
                     threshold, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("고사용률 리소스 조회 실패 (예상치 못한 오류) - threshold: {}", threshold, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "고사용률 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 미사용 리소스 조회
     * 
     * @param threshold 저사용률 임계값 (기본값: 10%)
     * @return 저사용률 리소스 목록
     */
    public ResourceListResponse getUnderutilizedResources(Double threshold) {
        log.debug("미사용 리소스 조회 - threshold: {}%", threshold);
        
        try {
            ResourceListResponse allResources = getResources(1, 100, null, "active", 
                                                           null, null, "usage_percent", "asc");
            
            if (allResources.getData() != null) {
                List<ResourceListResponse.ResourceSummary> underutilizedResources = 
                    allResources.getData().stream()
                        .filter(resource -> resource.getUsagePercent() != null && 
                                          resource.getUsagePercent() <= (threshold != null ? threshold : 10.0))
                        .toList();
                
                allResources.setData(underutilizedResources);
                allResources.setTotalCount(underutilizedResources.size());
                
                log.debug("미사용 리소스 조회 성공 - count: {}", underutilizedResources.size());
            }
            
            return allResources;
        } catch (BusinessException e) {
            log.error("미사용 리소스 조회 실패 (BusinessException) - threshold: {}, message: {}", 
                     threshold, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("미사용 리소스 조회 실패 (예상치 못한 오류) - threshold: {}", threshold, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "미사용 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
/**
     * 클러스터 리소스 조회
     * 
     * @param nodeType 노드 타입
     * @param projectId 프로젝트 ID (선택적)
     * @return 클러스터 리소스 정보
     */
    public TaskResourceResponse getClusterResources(String nodeType, String projectId) {
        log.info("SKTAI 클러스터 리소스 조회 요청 - nodeType: {}, projectId: {}", nodeType, projectId);
        
        try {
            TaskResourceResponse response = resourceClient.getClusterResources(nodeType, projectId);
            
            log.info("SKTAI 클러스터 리소스 조회 성공 - nodeType: {}, projectId: {}", nodeType, projectId);
            
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 클러스터 리소스 조회 실패 (BusinessException) - nodeType: {}, projectId: {}, message: {}", 
                     nodeType, projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 클러스터 리소스 조회 실패 (예상치 못한 오류) - nodeType: {}, projectId: {}", nodeType, projectId, e);
            log.error("에러 상세 정보: {}", e.getMessage());
            log.error("에러 타입: {}", e.getClass().getSimpleName());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "클러스터 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Task Policy 목록 조회
     * 
     * @return Task Policy 목록
     */
    public List<TaskPolicyResponse> getTaskPolicyList() {
        log.info("SKTAI Task Policy 목록 조회 요청");
        
        try {
            List<TaskPolicyResponse> response = resourceClient.getTaskPolicyList();
            
            log.info("SKTAI Task Policy 목록 조회 성공 - 총 {}개 정책", 
                    response != null ? response.size() : 0);
            
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI Task Policy 목록 조회 실패 (BusinessException) - message: {}", 
                     e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Task Policy 목록 조회 실패 (예상치 못한 오류)", e);
            log.error("에러 상세 정보: {}", e.getMessage());
            log.error("에러 타입: {}", e.getClass().getSimpleName());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Task Policy 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 태스크 타입별 리소스 정보 조회
     *
     * @param taskType  태스크 타입 (finetuning, serving, evaluation, test 등)
     * @param projectId 프로젝트 ID
     * @return 태스크 리소스 정보
     */
    public TaskResourceResponse getTaskResource(String taskType, String projectId) {
        log.info("SKTAI 태스크 리소스 조회 요청 - taskType: {}, projectId: {}", taskType, projectId);

        try {
            TaskResourceResponse response = resourceClient.getTaskResource(taskType, projectId);
            log.info("SKTAI 태스크 리소스 조회 성공 - taskType: {}, projectId: {}", taskType, projectId);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 태스크 리소스 조회 실패 (BusinessException) - taskType: {}, projectId: {}, message: {}", 
                     taskType, projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 태스크 리소스 조회 실패 (예상치 못한 오류) - taskType: {}, projectId: {}", taskType, projectId, e);
            log.error("에러 상세 정보: {}", e.getMessage());
            log.error("에러 타입: {}", e.getClass().getSimpleName());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "태스크 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }
}





