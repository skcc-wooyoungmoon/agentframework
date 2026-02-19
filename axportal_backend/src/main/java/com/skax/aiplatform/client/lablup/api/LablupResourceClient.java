package com.skax.aiplatform.client.lablup.api;

import com.skax.aiplatform.client.lablup.api.dto.request.GetScalingGroupsRequest;
import com.skax.aiplatform.client.lablup.api.dto.request.GetAgentListRequest;
import com.skax.aiplatform.client.lablup.api.dto.response.GetScalingGroupsResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetAgentListResponse;
import com.skax.aiplatform.client.lablup.config.LablupClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Lablup 리소스 관리 API Feign Client
 * 
 * <p>Lablup Backend.AI 시스템의 리소스 그룹 및 노드별 자원 할당량 조회를 위한 GraphQL API 클라이언트입니다.
 * 스케일링 그룹과 에이전트(노드) 정보를 통해 자원 현황을 모니터링할 수 있습니다.</p>
 * 
 * <h3>지원 API:</h3>
 * <ul>
 *   <li><strong>리소스 그룹별 자원 할당량 조회</strong>: POST /graphql (scaling_groups 쿼리)</li>
 *   <li><strong>노드별 자원 할당량 조회</strong>: POST /graphql (agent_list 쿼리)</li>
 * </ul>
 * 
 * <h3>GraphQL 쿼리:</h3>
 * <ul>
 *   <li><strong>scaling_groups</strong>: 리소스 그룹별 총 자원 정보</li>
 *   <li><strong>agent_list</strong>: 개별 노드별 자원 상태 (available_slots, occupied_slots)</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@FeignClient(
    name = "lablup-resource-client",
    url = "${lablup.api.backendai-base-url}",
    configuration = LablupClientConfig.class
)
@Tag(name = "Lablup Resource API", description = "Lablup 리소스 관리 API")
public interface LablupResourceClient {
    
    /**
     * 리소스 그룹별 자원 할당량 조회
     * 
     * <p>scaling_groups GraphQL 쿼리를 사용하여 리소스 그룹별 자원 할당량을 조회합니다.
     * agent_total_resource_slots_by_status 필드를 통해 상태별 자원 현황을 확인할 수 있습니다.</p>
     * 
     * <h3>조회 정보:</h3>
     * <ul>
     *   <li>리소스 그룹 이름 및 설명</li>
     *   <li>활성화 상태 및 생성일</li>
     *   <li>드라이버 및 스케줄러 정보</li>
     *   <li>상태별 총 자원 슬롯 현황</li>
     * </ul>
     * 
     * @param request 스케일링 그룹 조회 요청 (GraphQL 쿼리 포함)
     * @return 리소스 그룹별 자원 할당량 정보
     */
    @PostMapping(
        value = "/admin/graphql",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "리소스 그룹별 자원 할당량 조회",
        description = "scaling_groups GraphQL 쿼리를 통해 리소스 그룹별 자원 할당량과 상태 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "GraphQL 쿼리 오류"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GetScalingGroupsResponse getScalingGroups(
        @Parameter(description = "스케일링 그룹 조회 요청 (GraphQL 쿼리)", required = true)
        @RequestBody GetScalingGroupsRequest request
    );
    
    /**
     * 노드별 자원 할당량 조회
     * 
     * <p>agent_list GraphQL 쿼리를 사용하여 개별 노드(에이전트)별 자원 할당량을 조회합니다.
     * available_slots(총 보유 자원)과 occupied_slots(현재 할당된 자원) 정보를 제공합니다.</p>
     * 
     * <h3>조회 정보:</h3>
     * <ul>
     *   <li>에이전트 ID, 주소, 상태</li>
     *   <li>스케일링 그룹 및 스케줄링 가능 여부</li>
     *   <li>사용 가능한 자원 슬롯 (available_slots)</li>
     *   <li>현재 점유된 자원 슬롯 (occupied_slots)</li>
     * </ul>
     * 
     * @param request 에이전트 목록 조회 요청 (GraphQL 쿼리 포함)
     * @return 노드별 자원 할당량 정보
     */
    @PostMapping(
        value = "/admin/graphql",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "노드별 자원 할당량 조회",
        description = "agent_list GraphQL 쿼리를 통해 개별 노드별 자원 할당량과 사용 현황을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "GraphQL 쿼리 오류"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GetAgentListResponse getAgentList(
        @Parameter(description = "에이전트 목록 조회 요청 (GraphQL 쿼리)", required = true)
        @RequestBody GetAgentListRequest request
    );
}