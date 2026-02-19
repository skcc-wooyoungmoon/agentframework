package com.skax.aiplatform.service.resource;

import com.skax.aiplatform.client.lablup.api.dto.response.GetAgentListResponse;
import com.skax.aiplatform.dto.resource.response.ResourceUsageRes;
import com.skax.aiplatform.dto.resource.response.ScalingGroupsResponse;
import com.skax.aiplatform.dto.resource.response.TaskPolicyListRes;
import com.skax.aiplatform.dto.resource.response.TaskResourceRes;

/**
 * Resource Management Service Interface
 * 
 * <p>
 * 리소스 관리 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * SKTAI Resource Client를 통해 외부 API와 연동하여 리소스 정보를 조회합니다.
 * </p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
public interface ResourceService {

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
    ResourceUsageRes getClusterResources(String nodeType);

    /**
     * Task Policy 목록 조회
     * 
     * <p>
     * SKTAI Resource Service를 통해 Task Policy 목록을 조회합니다.
     * </p>
     * 
     * @return Task Policy 목록
     */
    TaskPolicyListRes getTaskPolicyList();

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
    TaskResourceRes getTaskResource(String taskType);

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
    GetAgentListResponse getAgentList(int limit, int offset, String status, String scalingGroup);

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
    ScalingGroupsResponse getScalingGroups(Boolean isActive);
}
