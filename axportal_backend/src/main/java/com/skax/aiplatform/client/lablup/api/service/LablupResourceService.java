package com.skax.aiplatform.client.lablup.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.lablup.api.LablupResourceClient;
import com.skax.aiplatform.client.lablup.api.dto.request.GetAgentListRequest;
import com.skax.aiplatform.client.lablup.api.dto.request.GetScalingGroupsRequest;
import com.skax.aiplatform.client.lablup.api.dto.response.GetAgentListResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetScalingGroupsResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Lablup 리소스 관리 서비스
 * 
 * <p>
 * Lablup Backend.AI 시스템의 리소스 그룹 및 노드별 자원 할당량 관리를 위한 비즈니스 로직을 제공합니다.
 * GraphQL API를 사용하여 스케일링 그룹과 에이전트 정보를 조회하고 자원 현황을 모니터링합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li>리소스 그룹별 자원 할당량 조회</li>
 * <li>노드별 자원 사용 현황 모니터링</li>
 * <li>GraphQL 쿼리 구성 및 실행</li>
 * <li>자원 데이터 분석 및 변환</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LablupResourceService {

    private final LablupResourceClient lablupResourceClient;

    /**
     * 활성화된 스케일링 그룹의 자원 할당량 조회
     * 
     * <p>
     * 활성화된 리소스 그룹들의 자원 할당량과 상태 정보를 조회합니다.
     * 기본적으로 구성된 GraphQL 쿼리를 사용하여 필요한 모든 정보를 가져옵니다.
     * </p>
     * 
     * @return 활성화된 스케일링 그룹 정보
     * @throws BusinessException GraphQL 쿼리 실행에 실패한 경우
     */
    public GetScalingGroupsResponse getActiveScalingGroups() {
        log.debug("활성화된 스케일링 그룹 자원 할당량 조회 요청");

        // GraphQL 쿼리 구성
        String query = """
                query($is_active: Boolean) {
                    scaling_groups(is_active: $is_active) {
                        name
                        description
                        is_active
                        created_at
                        driver
                        driver_opts
                        scheduler
                        scheduler_opts
                        use_host_network
                        wsproxy_addr
                        wsproxy_api_token
                        agent_total_resource_slots_by_status
                    }
                }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("is_active", true);

        GetScalingGroupsRequest request = GetScalingGroupsRequest.builder()
                .query(query.trim())
                .variables(variables)
                .build();

        return getScalingGroups(request);
    }

    /**
     * 모든 스케일링 그룹의 자원 할당량 조회
     * 
     * <p>
     * 활성화 여부와 관계없이 모든 리소스 그룹의 정보를 조회합니다.
     * 시스템 전체의 자원 현황을 파악할 때 사용합니다.
     * </p>
     * 
     * @return 모든 스케일링 그룹 정보
     * @throws BusinessException GraphQL 쿼리 실행에 실패한 경우
     */
    public GetScalingGroupsResponse getAllScalingGroups() {
        log.debug("모든 스케일링 그룹 자원 할당량 조회 요청");

        String query = """
                query {
                    scaling_groups {
                        name
                        description
                        is_active
                        created_at
                        driver
                        driver_opts
                        scheduler
                        scheduler_opts
                        use_host_network
                        wsproxy_addr
                        wsproxy_api_token
                        agent_total_resource_slots_by_status
                    }
                }
                """;

        GetScalingGroupsRequest request = GetScalingGroupsRequest.builder()
                .query(query.trim())
                .build();

        return getScalingGroups(request);
    }

    /**
     * 스케일링 그룹 자원 할당량 조회 (공통)
     * 
     * <p>
     * GraphQL 쿼리를 실행하여 스케일링 그룹 정보를 조회합니다.
     * API 호출 전후로 로깅을 수행하고 예외 처리를 담당합니다.
     * </p>
     * 
     * @param request GraphQL 쿼리 요청
     * @return 스케일링 그룹 조회 결과
     * @throws BusinessException API 호출에 실패한 경우
     */
    public GetScalingGroupsResponse getScalingGroups(GetScalingGroupsRequest request) {
        try {
            log.info("🔴 Lablup 스케일링 그룹 조회 요청 - hasVariables: {}",
                    request.getVariables() != null && !request.getVariables().isEmpty());

            GetScalingGroupsResponse response = lablupResourceClient.getScalingGroups(request);

            if (response != null && response.getScalingGroups() != null) {
                log.info("🔴 Lablup 스케일링 그룹 조회 성공 - groupCount: {}",
                        response.getScalingGroups().size());
            } else {
                log.warn("🔴 Lablup 스케일링 그룹 조회 결과가 비어있음");
            }

            // 응답 null 체크
            if (response == null) {
                log.error("🔴 Lablup API 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "Lablup API에서 응답을 받지 못했습니다");
            }

            // GraphQL 오류 체크
            if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                log.error("🔴 GraphQL 쿼리 실행 중 오류 발생: {}", response.getErrors());
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "GraphQL 쿼리 실행 중 오류가 발생했습니다");
            }

            return response;

        } catch (BusinessException e) {
            log.error("🔴 Lablup 스케일링 그룹 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 스케일링 그룹 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "스케일링 그룹 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 활성화된 에이전트 목록 조회
     * 
     * <p>
     * ALIVE 상태이고 스케줄링 가능한 에이전트들의 자원 현황을 조회합니다.
     * 기본적으로 50개씩 조회하며, default 스케일링 그룹을 대상으로 합니다.
     * </p>
     * 
     * @return 활성화된 에이전트 목록
     * @throws BusinessException GraphQL 쿼리 실행에 실패한 경우
     */
    public GetAgentListResponse getActiveAgents() {
        return getActiveAgents(50, 0, "default");
    }

    /**
     * 활성화된 에이전트 목록 조회 (페이징)
     * 
     * <p>
     * ALIVE 상태이고 스케줄링 가능한 에이전트들을 페이징하여 조회합니다.
     * 지정된 스케일링 그룹의 에이전트만 필터링하여 가져옵니다.
     * </p>
     * 
     * @param limit        조회할 최대 개수
     * @param offset       페이징 오프셋
     * @param scalingGroup 대상 스케일링 그룹
     * @return 에이전트 목록
     * @throws BusinessException GraphQL 쿼리 실행에 실패한 경우
     */
    public GetAgentListResponse getActiveAgents(int limit, int offset, String scalingGroup) {
        log.debug("활성화된 에이전트 목록 조회 요청 - limit: {}, offset: {}, scalingGroup: {}",
                limit, offset, scalingGroup);

        String query = """
                query(
                    $limit: Int!, $offset: Int!, $filter: String, $order: String,
                    $status: String, $scaling_group: String
                ) {
                    agent_list(
                        limit: $limit, offset: $offset, filter: $filter, order: $order,
                        status: $status, scaling_group: $scaling_group
                    ) {
                        items {
                            id
                            addr
                            status
                            scaling_group
                            schedulable
                            available_slots
                            occupied_slots
                        }
                        total_count
                    }
                }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("limit", limit);
        variables.put("offset", offset);
        variables.put("filter", "schedulable == true");
        variables.put("order", "id");
        variables.put("status", "ALIVE");
        variables.put("scaling_group", scalingGroup);

        GetAgentListRequest request = GetAgentListRequest.builder()
                .query(query.trim())
                .variables(variables)
                .build();

        return getAgentList(request);
    }

    /**
     * 에이전트 목록 조회 (파라미터 기반)
     * 
     * <p>
     * GraphQL 쿼리를 구성하여 에이전트 목록을 조회합니다.
     * 사용자가 제공한 가이드에 따라 필터, 정렬, 상태 등을 설정합니다.
     * </p>
     * 
     * @param limit        조회할 최대 개수
     * @param offset       페이징 오프셋
     * @param status       에이전트 상태 필터 (예: "ALIVE")
     * @param scalingGroup 스케일링 그룹 필터 (예: "default")
     * @return 에이전트 목록 조회 결과
     * @throws BusinessException API 호출에 실패한 경우
     */
    public GetAgentListResponse getAgentList(int limit, int offset, String status, String scalingGroup) {
        log.debug("에이전트 목록 조회 요청 - limit: {}, offset: {}, status: {}, scalingGroup: {}",
                limit, offset, status, scalingGroup);

        String query = """
                query(
                    $limit: Int!, $offset: Int!, $filter: String, $order: String,
                    $status: String, $scaling_group: String
                ) {
                    agent_list(
                        limit: $limit, offset: $offset, filter: $filter, order: $order,
                        status: $status, scaling_group: $scaling_group
                    ) {
                        items {
                            id
                            addr
                            status
                            scaling_group
                            schedulable
                            available_slots
                            occupied_slots
                        }
                        total_count
                    }
                }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("limit", limit);
        variables.put("offset", offset);
        variables.put("filter", "schedulable == true");
        variables.put("order", "id");
        variables.put("status", status);
        variables.put("scaling_group", scalingGroup);

        GetAgentListRequest request = GetAgentListRequest.builder()
                .query(query.trim())
                .variables(variables)
                .build();

        return getAgentList(request);
    }

    /**
     * 에이전트 목록 조회 (공통)
     * 
     * <p>
     * GraphQL 쿼리를 실행하여 에이전트 목록을 조회합니다.
     * API 호출 전후로 로깅을 수행하고 예외 처리를 담당합니다.
     * </p>
     * 
     * @param request GraphQL 쿼리 요청
     * @return 에이전트 목록 조회 결과
     * @throws BusinessException API 호출에 실패한 경우
     */
    public GetAgentListResponse getAgentList(GetAgentListRequest request) {
        try {
            log.info("🔴 Lablup 에이전트 목록 조회 요청 - hasVariables: {}",
                    request.getVariables() != null && !request.getVariables().isEmpty());

            GetAgentListResponse response = lablupResourceClient.getAgentList(request);

            if (response != null && response.getAgentList() != null &&
                    response.getAgentList().getItems() != null) {
                log.info("🔴 Lablup 에이전트 목록 조회 성공 - agentCount: {}, totalCount: {}",
                        response.getAgentList().getItems().size(),
                        response.getAgentList().getTotalCount());
            } else {
                log.warn("🔴 Lablup 에이전트 목록 조회 결과가 비어있음");
            }

            // 응답 null 체크
            if (response == null) {
                log.error("🔴 Lablup API 응답이 null입니다");
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "Lablup API에서 응답을 받지 못했습니다");
            }

            return response;

        } catch (BusinessException e) {
            log.error("🔴 Lablup 에이전트 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 에이전트 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모든 에이전트 목록 조회
     * 
     * <p>
     * 상태나 스케일링 그룹에 관계없이 모든 에이전트의 정보를 조회합니다.
     * 시스템 전체의 노드 현황을 파악할 때 사용합니다.
     * </p>
     * 
     * @param limit  조회할 최대 개수
     * @param offset 페이징 오프셋
     * @return 모든 에이전트 목록
     * @throws BusinessException GraphQL 쿼리 실행에 실패한 경우
     */
    public GetAgentListResponse getAllAgents(int limit, int offset) {
        log.debug("모든 에이전트 목록 조회 요청 - limit: {}, offset: {}", limit, offset);

        String query = """
                query($limit: Int!, $offset: Int!, $order: String) {
                    agent_list(limit: $limit, offset: $offset, order: $order) {
                        items {
                            id
                            addr
                            status
                            scaling_group
                            schedulable
                            available_slots
                            occupied_slots
                        }
                        total_count
                    }
                }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("limit", limit);
        variables.put("offset", offset);
        variables.put("order", "id");

        GetAgentListRequest request = GetAgentListRequest.builder()
                .query(query.trim())
                .variables(variables)
                .build();

        return getAgentList(request);
    }
}