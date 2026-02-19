package com.skax.aiplatform.service.resource.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.lablup.api.dto.response.GetAgentListResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetScalingGroupsResponse;
import com.skax.aiplatform.client.lablup.api.service.LablupResourceService;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientsRead;
import com.skax.aiplatform.client.sktai.auth.service.SktaiProjectService;
import com.skax.aiplatform.client.sktai.resource.dto.response.NamespaceResource;
import com.skax.aiplatform.client.sktai.resource.dto.response.NodeResource;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskPolicyResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskResourceResponse;
import com.skax.aiplatform.client.sktai.resource.service.SktaiResourceService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.resource.response.ResourceUsageRes;
import com.skax.aiplatform.dto.resource.response.ScalingGroupsResponse;
import com.skax.aiplatform.dto.resource.response.TaskPolicyListRes;
import com.skax.aiplatform.dto.resource.response.TaskPolicyRes;
import com.skax.aiplatform.dto.resource.response.TaskResourceRes;
import com.skax.aiplatform.mapper.resource.ResourceMapper;
import com.skax.aiplatform.service.resource.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource Management Service Implementation
 *
 * <p>
 * 리소스 관리 관련 비즈니스 로직을 구현하는 서비스입니다.
 * SKTAI Resource Service를 통해 외부 API와 연동하여 리소스 정보를 조회합니다.
 * </p>
 *
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {

    private final SktaiResourceService sktaiResourceService;
    private final ResourceMapper resourceMapper;
    private final SktaiProjectService sktaiProjectService;
    private final LablupResourceService lablupResourceService;

    /**
     * 클러스터 리소스 조회
     *
     * <p>
     * SKTAI Resource Service를 통해 클러스터 리소스 정보를 조회합니다.
     * </p>
     *
     * @param nodeType 노드 타입 (task, master, worker 등)
     * @return 클러스터 리소스 정보
     */
    @Override
    public ResourceUsageRes getClusterResources(String nodeType) {
        log.info("=== 클러스터 리소스 조회 시작 ===");
        log.info("요청 파라미터 - nodeType: {}", nodeType);

        // 필수 필드 검증
        validateNodeType(nodeType);

        try {
            log.info("클러스터 리소스 조회 시작 - nodeType: {}", nodeType);

            TaskResourceResponse sktaiResult = sktaiResourceService.getClusterResources(nodeType, "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5");

            // SKTAI 응답 검증
            if (sktaiResult == null) {
                log.error("클러스터 리소스 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SKTAI API 응답이 null입니다");
            }

            // TaskResourceResponse를 ResourceUsageRes로 변환
            ResourceUsageRes response = convertTaskResourceResponseToResourceUsageRes(sktaiResult);

            log.info("클러스터 리소스 조회 완료 - nodeType: {}", nodeType);
            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("클러스터 리소스 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("클러스터 리소스 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "클러스터 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Task Policy 목록 조회
     *
     * <p>
     * SKTAI Resource Service를 통해 Task Policy 목록을 조회합니다.
     * </p>
     *
     * @return Task Policy 목록
     */
    @Override
    public TaskPolicyListRes getTaskPolicyList() {
        log.info("=== Task Policy 목록 조회 시작 ===");

        try {
            log.info("Task Policy 목록 조회 시작");

            var sktaiResult = sktaiResourceService.getTaskPolicyList();

            // SKTAI 응답 검증
            if (sktaiResult == null) {
                log.error("Task Policy 목록 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SKTAI API 응답이 null입니다");
            }

            // List<TaskPolicyResponse>를 TaskPolicyListRes로 변환
            TaskPolicyListRes response = convertToTaskPolicyListRes(sktaiResult);

            log.info("Task Policy 목록 조회 완료 - 총 {}개 정책", response.getTotalCount());

            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("Task Policy 목록 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("Task Policy 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Task Policy 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 노드 타입 검증
     *
     * <p>
     * 필수 필드와 비즈니스 규칙을 검증합니다.
     * </p>
     *
     * @param nodeType 노드 타입
     * @throws BusinessException 검증 실패 시
     */
    private void validateNodeType(String nodeType) {
        if (nodeType == null || nodeType.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "노드 타입은 필수입니다");
        }

        // 허용된 노드 타입 검증
        if (!isValidNodeType(nodeType)) {
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "지원하지 않는 노드 타입입니다: " + nodeType);
        }

        // 필요시 추가 필드 검증
        log.debug("노드 타입 검증 완료 - nodeType: {}", nodeType);
    }

    /**
     * 유효한 노드 타입인지 확인
     *
     * @param nodeType 노드 타입
     * @return 유효 여부
     */
    private boolean isValidNodeType(String nodeType) {
        return "task".equals(nodeType) || "master".equals(nodeType) || "worker".equals(nodeType)
                || "all".equals(nodeType);
    }

    /**
     * List<TaskPolicyResponse>를 TaskPolicyListRes로 변환
     *
     * @param taskPolicyList SKTAI Task Policy 목록
     * @return 내부 응답 DTO
     */
    private TaskPolicyListRes convertToTaskPolicyListRes(List<TaskPolicyResponse> taskPolicyList) {
        if (taskPolicyList == null) {
            return TaskPolicyListRes.builder().taskPolicies(List.of()).totalCount(0).build();
        }

        List<TaskPolicyRes> convertedPolicies = taskPolicyList.stream().map(resourceMapper::toTaskPolicyResponse)
                .collect(Collectors.toList());

        return TaskPolicyListRes.builder().taskPolicies(convertedPolicies).totalCount(taskPolicyList.size()).build();
    }

    /**
     * Map<String, Object>를 ResourceUsageRes로 변환
     *
     * @param map SKTAI API 응답 Map
     * @return 내부 응답 DTO
     */
    @SuppressWarnings("unchecked")
    private ResourceUsageRes convertMapToResourceUsageRes(Map<String, Object> map) {
        if (map == null) {
            return ResourceUsageRes.builder().build();
        }

        // node_resource 변환
        List<Map<String, Object>> nodeResourceList = (List<Map<String, Object>>) map.get("node_resource");
        List<ResourceUsageRes.NodeResourceInfo> nodeResources = null;
        if (nodeResourceList != null) {
            nodeResources = nodeResourceList.stream().map(this::convertToNodeResourceInfo).collect(Collectors.toList());
        }

        // cluster_resource 변환
        Map<String, Object> clusterResourceMap = (Map<String, Object>) map.get("cluster_resource");
        ResourceUsageRes.ClusterResourceInfo clusterResource = null;
        if (clusterResourceMap != null) {
            clusterResource = convertToClusterResourceInfo(clusterResourceMap);
        }

        return ResourceUsageRes.builder().nodeResource(nodeResources).clusterResource(clusterResource).build();
    }

    /**
     * Map을 NodeResourceInfo로 변환
     *
     * @param nodeMap 노드 정보 Map
     * @return NodeResourceInfo
     */
    @SuppressWarnings("unchecked")
    private ResourceUsageRes.NodeResourceInfo convertToNodeResourceInfo(Map<String, Object> nodeMap) {
        return ResourceUsageRes.NodeResourceInfo.builder().nodeName((String) nodeMap.get("node_name"))
                .nodeLabel((List<String>) nodeMap.get("node_label")).cpuQuota(convertToDouble(nodeMap.get("cpu_quota")))
                .memQuota(convertToDouble(nodeMap.get("mem_quota"))).gpuQuota(convertToDouble(nodeMap.get("gpu_quota")))
                .cpuUsed(convertToDouble(nodeMap.get("cpu_used"))).memUsed(convertToDouble(nodeMap.get("mem_used")))
                .gpuUsed(convertToDouble(nodeMap.get("gpu_used"))).cpuUsable(convertToDouble(nodeMap.get("cpu_usable")))
                .memUsable(convertToDouble(nodeMap.get("mem_usable")))
                .gpuUsable(convertToDouble(nodeMap.get("gpu_usable"))).build();
    }

    /**
     * Map을 ClusterResourceInfo로 변환
     *
     * @param clusterMap 클러스터 정보 Map
     * @return ClusterResourceInfo
     */
    private ResourceUsageRes.ClusterResourceInfo convertToClusterResourceInfo(Map<String, Object> clusterMap) {
        return ResourceUsageRes.ClusterResourceInfo.builder().cpuTotal(convertToDouble(clusterMap.get("cpu_total")))
                .cpuUsed(convertToDouble(clusterMap.get("cpu_used")))
                .cpuUsable(convertToDouble(clusterMap.get("cpu_usable")))
                .memoryTotal(convertToDouble(clusterMap.get("memory_total")))
                .memoryUsed(convertToDouble(clusterMap.get("memory_used")))
                .memoryUsable(convertToDouble(clusterMap.get("memory_usable")))
                .gpuTotal(convertToDouble(clusterMap.get("gpu_total")))
                .gpuUsed(convertToDouble(clusterMap.get("gpu_used")))
                .gpuUsable(convertToDouble(clusterMap.get("gpu_usable"))).build();
    }

    /**
     * TaskResourceResponse를 ResourceUsageRes로 변환
     *
     * @param taskResourceResponse TaskResourceResponse
     * @return ResourceUsageRes
     */
    private ResourceUsageRes convertTaskResourceResponseToResourceUsageRes(TaskResourceResponse taskResourceResponse) {
        if (taskResourceResponse == null) {
            return ResourceUsageRes.builder().build();
        }

        // node_resource 변환
        List<ResourceUsageRes.NodeResourceInfo> nodeResources = null;
        if (taskResourceResponse.getNodeResource() != null) {
            nodeResources = taskResourceResponse.getNodeResource().stream()
                    .map(this::convertNodeResourceToNodeResourceInfo)
                    .collect(Collectors.toList());
        }

        // namespace_resource를 cluster_resource로 변환
        ResourceUsageRes.ClusterResourceInfo clusterResource = null;
        if (taskResourceResponse.getNamespaceResource() != null) {
            clusterResource = convertNamespaceResourceToClusterResourceInfo(taskResourceResponse.getNamespaceResource());
        }

        return ResourceUsageRes.builder()
                .nodeResource(nodeResources)
                .clusterResource(clusterResource)
                .build();
    }

    /**
     * NodeResource를 NodeResourceInfo로 변환
     *
     * @param nodeResource NodeResource
     * @return NodeResourceInfo
     */
    private ResourceUsageRes.NodeResourceInfo convertNodeResourceToNodeResourceInfo(NodeResource nodeResource) {
        return ResourceUsageRes.NodeResourceInfo.builder()
                .nodeName(nodeResource.getNodeName())
                .nodeLabel(nodeResource.getNodeLabel())
                .cpuQuota(nodeResource.getCpuQuota() != null ? nodeResource.getCpuQuota().doubleValue() : null)
                .memQuota(nodeResource.getMemQuota())
                .gpuQuota(nodeResource.getGpuQuota() != null ? nodeResource.getGpuQuota().doubleValue() : null)
                .cpuUsed(nodeResource.getCpuUsed() != null ? nodeResource.getCpuUsed().doubleValue() : null)
                .memUsed(nodeResource.getMemUsed())
                .gpuUsed(nodeResource.getGpuUsed() != null ? nodeResource.getGpuUsed().doubleValue() : null)
                .cpuUsable(nodeResource.getCpuUsable() != null ? nodeResource.getCpuUsable().doubleValue() : null)
                .memUsable(nodeResource.getMemUsable())
                .gpuUsable(nodeResource.getGpuUsable() != null ? nodeResource.getGpuUsable().doubleValue() : null)
                .build();
    }

    /**
     * NamespaceResource를 ClusterResourceInfo로 변환
     *
     * @param namespaceResource NamespaceResource
     * @return ClusterResourceInfo
     */
    private ResourceUsageRes.ClusterResourceInfo convertNamespaceResourceToClusterResourceInfo(NamespaceResource namespaceResource) {
        return ResourceUsageRes.ClusterResourceInfo.builder()
                .cpuTotal(namespaceResource.getCpuQuota() != null ? namespaceResource.getCpuQuota().doubleValue() : null)
                .cpuUsed(namespaceResource.getCpuUsed() != null ? namespaceResource.getCpuUsed().doubleValue() : null)
                .cpuUsable(namespaceResource.getCpuUsable() != null ? namespaceResource.getCpuUsable().doubleValue() : null)
                .memoryTotal(namespaceResource.getMemQuota() != null ? namespaceResource.getMemQuota().doubleValue() : null)
                .memoryUsed(namespaceResource.getMemUsed() != null ? namespaceResource.getMemUsed().doubleValue() : null)
                .memoryUsable(namespaceResource.getMemUsable() != null ? namespaceResource.getMemUsable().doubleValue() : null)
                .gpuTotal(namespaceResource.getGpuQuota() != null ? namespaceResource.getGpuQuota().doubleValue() : null)
                .gpuUsed(namespaceResource.getGpuUsed() != null ? namespaceResource.getGpuUsed().doubleValue() : null)
                .gpuUsable(namespaceResource.getGpuUsable() != null ? namespaceResource.getGpuUsable().doubleValue() : null)
                .build();
    }

    /**
     * Object를 Double로 안전하게 변환
     *
     * @param value 변환할 값
     * @return Double 값 또는 null
     */
    private Double convertToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                log.warn("숫자 변환 실패: {}", value);
                return null;
            }
        }
        return null;
    }

    /**
     * 태스크 타입별 리소스 정보 조회
     *
     * <p>
     * SKTAI Resource Service를 통해 특정 태스크 타입의 리소스 정보를 조회합니다.
     * </p>
     *
     * @param taskType 태스크 타입 (finetuning, serving, evaluation, test 등)
     * @return 태스크 리소스 정보
     */
    @Override
    public TaskResourceRes getTaskResource(String taskType) {
        log.info("=== 태스크 리소스 조회 시작 ===");
        log.info("요청 파라미터 - taskType: {}", taskType);

        try {
            String projectId = getFirstProjectId();

            log.info("태스크 리소스 조회 시작 - taskType: {}, projectId: {}", taskType, projectId);
            TaskResourceResponse sktaiResult = sktaiResourceService.getTaskResource(taskType, projectId);

            // SKTAI 응답 검증
            if (sktaiResult == null) {
                log.error("태스크 리소스 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SKTAI API 응답이 null입니다");
            }

            // TaskResourceResponse를 TaskResourceRes로 변환
            TaskResourceRes response = resourceMapper.toTaskResourceRes(sktaiResult);

            log.info("태스크 리소스 조회 완료 - taskType: {}, projectId: {}", taskType, projectId);
            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("태스크 리소스 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("태스크 리소스 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "태스크 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 첫 번째 프로젝트 ID 조회
     *
     * <p>
     * 사용자가 접근 가능한 첫 번째 프로젝트의 ID를 안전하게 조회합니다.
     * 프로젝트가 없는 경우 적절한 예외를 발생시킵니다.
     * </p>
     *
     * @return 첫 번째 프로젝트 ID
     * @throws BusinessException 프로젝트가 없거나 조회 실패 시
     */
    private String getFirstProjectId() {
        try {
            // 최소한의 데이터만 요청 (첫 번째 프로젝트만)
            ClientsRead clientsRead = sktaiProjectService.getProjects(1, 1, null, null, null);

            // 안전한 배열 접근을 위한 검증
            if (clientsRead.getData() == null || clientsRead.getData().isEmpty()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "접근 가능한 프로젝트가 없습니다.");
            }

            ClientRead firstClient = clientsRead.getData().get(0);
            if (firstClient.getProject() == null || firstClient.getProject().getId() == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "프로젝트 정보가 올바르지 않습니다.");
            }

            String projectId = firstClient.getProject().getId();
            log.debug("첫 번째 프로젝트 ID 조회 성공: {}", projectId);

            return projectId;

        } catch (BusinessException e) {
            log.error("프로젝트 ID 조회 실패 (BusinessException): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("프로젝트 ID 조회 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "프로젝트 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 목록 조회
     *
     * <p>
     * Lablup Backend.AI 시스템의 에이전트(노드) 목록을 조회합니다.
     * 각 에이전트의 자원 현황, 상태, 스케줄링 가능 여부 등을 확인할 수 있습니다.
     * </p>
     *
     * @param limit        조회할 최대 개수
     * @param offset       페이징 오프셋
     * @param status       에이전트 상태 필터
     * @param scalingGroup 스케일링 그룹 필터
     * @return 에이전트 목록
     */
    @Override
    public GetAgentListResponse getAgentList(int limit, int offset, String status, String scalingGroup) {
        log.info("=== 에이전트 목록 조회 시작 ===");
        log.info("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}", limit, offset, status, scalingGroup);

        try {
            log.info("LablupResourceService.getAgentList 호출 시작");
            GetAgentListResponse response = lablupResourceService.getAgentList(limit, offset, status, scalingGroup);

            // Lablup 응답 검증
            if (response == null) {
                log.error("에이전트 목록 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Lablup API 응답이 null입니다");
            }

            log.info("에이전트 목록 조회 완료 - totalCount: {}",
                    response.getAgentList() != null
                            ? response.getAgentList().getTotalCount()
                            : 0);

            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("에이전트 목록 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 스케일링 그룹 목록 조회
     *
     * <p>
     * Lablup Backend.AI 시스템의 스케일링 그룹 목록을 조회합니다.
     * 각 그룹의 설정과 자원 할당량 정보를 확인할 수 있습니다.
     * </p>
     *
     * @param isActive 활성화 여부 필터 (true: 활성화된 그룹만, false: 전체 조회)
     * @return 스케일링 그룹 목록
     */
    @Override
    public ScalingGroupsResponse getScalingGroups(Boolean isActive) {
        log.info("=== 스케일링 그룹 목록 조회 시작 ===");
        log.info("요청 파라미터 - isActive: {}", isActive);

        try {
            log.info("LablupResourceService.getScalingGroups 호출 시작");

            GetScalingGroupsResponse clientResponse;
            // isActive 값에 따라 호출 분기
            if (isActive) {
                clientResponse = lablupResourceService.getActiveScalingGroups();
            } else {
                clientResponse = lablupResourceService.getAllScalingGroups();
            }

            // Lablup 응답 검증
            if (clientResponse == null) {
                log.error("스케일링 그룹 목록 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Lablup API 응답이 null입니다");
            }

            // 클라이언트 DTO를 내부 DTO로 변환 (스네이크케이스 -> 카멜케이스)
            ScalingGroupsResponse response = resourceMapper.toScalingGroupsResponse(clientResponse);

            List<ScalingGroupsResponse.ScalingGroup> scalingGroups = response.getScalingGroups();
            for(ScalingGroupsResponse.ScalingGroup sg : scalingGroups){
                GetAgentListResponse agentListResponse = getAgentList(100, 0, "ALIVE", sg.getName());
                sg.setAgentList(agentListResponse.getAgentList().getItems());
            }

            log.info("스케일링 그룹 목록 조회 완료 - groupCount: {}",
                    response.getScalingGroups() != null
                            ? response.getScalingGroups().size()
                            : 0);

            return response;

        } catch (BusinessException e) {
            // 이미 처리된 비즈니스 예외는 그대로 전파
            log.error("스케일링 그룹 목록 조회 중 비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("스케일링 그룹 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "스케일링 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

}