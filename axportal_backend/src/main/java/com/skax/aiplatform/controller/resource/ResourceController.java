package com.skax.aiplatform.controller.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.skax.aiplatform.client.lablup.api.dto.response.GetAgentListResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.dto.resource.response.ResourceUsageRes;
import com.skax.aiplatform.dto.resource.response.ScalingGroupsResponse;
import com.skax.aiplatform.dto.resource.response.TaskPolicyListRes;
import com.skax.aiplatform.dto.resource.response.TaskResourceRes;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.resource.ResourceService;

/**
 * Resource Management Controller
 *
 * <p>
 * 리소스 관리 관련 API를 제공하는 컨트롤러입니다.
 * 클러스터 리소스 조회 및 Task Policy 관리 기능을 포함합니다.
 * </p>
 *
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Tag(name = "리소스 관리", description = "리소스 관리 API")
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * 클러스터 리소스 조회
     *
     * <p>
     * 클러스터의 리소스 정보를 조회합니다.
     * 노드 타입별로 클러스터 상태와 리소스 사용량을 확인할 수 있습니다.
     * </p>
     *
     * @param nodeType 노드 타입 (task, master, worker 등)
     * @return 클러스터 리소스 정보
     */
    @GetMapping("/cluster")
    @Operation(summary = "클러스터 리소스 조회", description = "클러스터의 리소스 정보를 노드 타입별로 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "404", description = "클러스터를 찾을 수 없음") })
    public AxResponseEntity<ResourceUsageRes> getClusterResources(
            @Parameter(description = "노드 타입", example = "task") @RequestParam(value = "node_type", defaultValue = "task") String nodeType) {
        log.info("=== 클러스터 리소스 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - nodeType: {}", nodeType);

        try {
            log.info("ResourceService.getClusterResources 호출 시작");
            ResourceUsageRes response = resourceService.getClusterResources(nodeType);

            log.info("ResourceService.getClusterResources 호출 완료");
            log.info("응답 데이터: {}", response);

            AxResponseEntity<ResourceUsageRes> result = AxResponseEntity.ok(response, "클러스터 리소스 정보를 성공적으로 조회했습니다.");

            log.info("=== 클러스터 리소스 조회 API 호출 완료 ===");
            return result;

        } catch (BusinessException e) {
            log.error("=== 클러스터 리소스 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - nodeType: {}, errorCode: {}", nodeType, e.getErrorCode(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("=== 클러스터 리소스 조회 API 호출 실패 (IllegalArgumentException) ===");
            log.error("요청 파라미터 - nodeType: {}", nodeType, e);
            throw e;
        } catch (NullPointerException e) {
            log.error("=== 클러스터 리소스 조회 API 호출 실패 (NullPointerException) ===");
            log.error("요청 파라미터 - nodeType: {}", nodeType, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 클러스터 리소스 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - nodeType: {}", nodeType, e);
            throw e;
        }
    }

    /**
     * Task Policy 전체 목록 조회
     *
     * <p>
     * 시스템에 등록된 모든 Task Policy 목록을 조회합니다.
     * 정책별 상세 정보와 설정값을 확인할 수 있습니다.
     * </p>
     *
     * @return Task Policy 목록
     */
    @GetMapping("/task_policy")
    @Operation(summary = "Task Policy 전체 목록 조회", description = "시스템에 등록된 모든 Task Policy 목록을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족") })
    public AxResponseEntity<TaskPolicyListRes> getTaskPolicyList() {
        log.info("=== Task Policy 목록 조회 API 호출 시작 ===");

        try {
            log.info("ResourceService.getTaskPolicyList 호출 시작");
            TaskPolicyListRes response = resourceService.getTaskPolicyList();

            log.info("ResourceService.getTaskPolicyList 호출 완료");
            log.info("응답 데이터: {}", response);

            // 응답 데이터 상세 분석
            if (response != null && response.getTaskPolicies() != null) {
                log.info("=== 응답 데이터 상세 정보 ===");
                log.info("총 {}개 Task Policy 반환", response.getTaskPolicies().size());
                log.info("전체 정책 개수: {}", response.getTotalCount());

                if (!response.getTaskPolicies().isEmpty()) {
                    var firstPolicy = response.getTaskPolicies().get(0);
                    log.info("첫 번째 정책 - TaskType: {}, Size: {}, CPU: {}, Memory: {}, GPU: {}",
                            firstPolicy.getTaskType(), firstPolicy.getSize(), firstPolicy.getCpu(),
                            firstPolicy.getMemory(), firstPolicy.getGpu());
                }
            } else {
                log.warn("=== 응답 데이터가 비어있음 ===");
            }

            AxResponseEntity<TaskPolicyListRes> result = AxResponseEntity.ok(response, "Task Policy 목록을 성공적으로 조회했습니다.");

            log.info("=== Task Policy 목록 조회 API 호출 완료 ===");
            return result;

        } catch (BusinessException e) {
            log.error("=== Task Policy 목록 조회 API 호출 실패 (BusinessException) ===");
            log.error("errorCode: {}", e.getErrorCode(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("=== Task Policy 목록 조회 API 호출 실패 (IllegalArgumentException) ===", e);
            throw e;
        } catch (NullPointerException e) {
            log.error("=== Task Policy 목록 조회 API 호출 실패 (NullPointerException) ===", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== Task Policy 목록 조회 API 호출 실패 (RuntimeException) ===", e);
            throw e;
        }
    }

    /**
     * 태스크 타입별 리소스 정보 조회
     *
     * <p>
     * 특정 태스크 타입에 대한 리소스 정보를 조회합니다.
     * 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.
     * </p>
     *
     * @param taskType 태스크 타입
     * @return 태스크 리소스 정보
     */
    @GetMapping("/task")
    @Operation(summary = "태스크 타입별 리소스 정보 조회", description = "특정 태스크 타입에 대한 리소스 정보를 조회합니다. 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "404", description = "태스크 타입을 찾을 수 없음") })
    public AxResponseEntity<TaskResourceRes> getTaskResource(
            @Parameter(description = "태스크 타입", example = "serving", required = true) @RequestParam(value = "task_type", defaultValue = "serving") String taskType

    ) {
        log.info("=== 태스크 리소스 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - taskType: {},", taskType);

        try {
            log.info("ResourceService.getTaskResource 호출 시작");
            TaskResourceRes response = resourceService.getTaskResource(taskType);

            log.info("ResourceService.getTaskResource 호출 완료");
            log.info("응답 데이터: {}", response);

            AxResponseEntity<TaskResourceRes> result = AxResponseEntity.ok(response, "태스크 리소스 정보를 성공적으로 조회했습니다.");

            log.info("=== 태스크 리소스 조회 API 호출 완료 ===");
            return result;

        } catch (BusinessException e) {
            log.error("=== 태스크 리소스 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - taskType: {}, errorCode: {}", taskType, e.getErrorCode(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("=== 태스크 리소스 조회 API 호출 실패 (IllegalArgumentException) ===");
            log.error("요청 파라미터 - taskType: {}", taskType, e);
            throw e;
        } catch (NullPointerException e) {
            log.error("=== 태스크 리소스 조회 API 호출 실패 (NullPointerException) ===");
            log.error("요청 파라미터 - taskType: {}", taskType, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 태스크 리소스 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - taskType: {}", taskType, e);
            throw e;
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
     * @param limit        조회할 최대 개수 (기본값: 50)
     * @param offset       페이징 오프셋 (기본값: 0)
     * @param status       에이전트 상태 필터 (기본값: ALIVE)
     * @param scalingGroup 스케일링 그룹 필터 (기본값: default)
     * @return 에이전트 목록
     */
    @GetMapping("/agents")
    @Operation(summary = "에이전트 목록 조회", description = "Lablup Backend.AI 시스템의 에이전트(노드) 목록을 조회합니다. 각 에이전트의 자원 현황, 상태, 스케줄링 가능 여부 등을 확인할 수 있습니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<GetAgentListResponse> getAgentList(
            @Parameter(description = "조회할 최대 개수", example = "50") @RequestParam(value = "limit", defaultValue = "50") int limit,
            @Parameter(description = "페이징 오프셋", example = "0") @RequestParam(value = "offset", defaultValue = "0") int offset,
            @Parameter(description = "에이전트 상태 필터", example = "ALIVE") @RequestParam(value = "status", defaultValue = "ALIVE") String status,
            @Parameter(description = "스케일링 그룹 필터", example = "default") @RequestParam(value = "scaling_group", defaultValue = "default") String scalingGroup) {
        log.info("=== 에이전트 목록 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}", limit, offset, status, scalingGroup);

        try {
            log.info("ResourceService.getAgentList 호출 시작");
            GetAgentListResponse response = resourceService.getAgentList(limit, offset, status, scalingGroup);

            log.info("ResourceService.getAgentList 호출 완료");
            log.info("응답 데이터: {}", response);

            AxResponseEntity<GetAgentListResponse> result = AxResponseEntity.ok(response, "에이전트 목록을 성공적으로 조회했습니다.");

            log.info("=== 에이전트 목록 조회 API 호출 완료 ===");
            return result;

        } catch (BusinessException e) {
            log.error("=== 에이전트 목록 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}, errorCode: {}", 
                    limit, offset, status, scalingGroup, e.getErrorCode(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("=== 에이전트 목록 조회 API 호출 실패 (IllegalArgumentException) ===");
            log.error("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}", 
                    limit, offset, status, scalingGroup, e);
            throw e;
        } catch (NullPointerException e) {
            log.error("=== 에이전트 목록 조회 API 호출 실패 (NullPointerException) ===");
            log.error("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}", 
                    limit, offset, status, scalingGroup, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 에이전트 목록 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - limit: {}, offset: {}, status: {}, scalingGroup: {}", 
                    limit, offset, status, scalingGroup, e);
            throw e;
        }
    }

    /**
     * 스케일링 그룹 목록 조회
     *
     * <p>
     * Lablup Backend.AI 시스템의 스케일링 그룹 목록을 조회합니다.
     * 각 그룹의 설정, 드라이버 옵션, 스케줄러 정보 및 자원 할당량을 확인할 수 있습니다.
     * </p>
     *
     * @param isActive 활성화 여부 필터 (기본값: true - 활성화된 그룹만 조회, false: 전체 조회)
     * @return 스케일링 그룹 목록
     */
    @GetMapping("/scaling_groups")
    @Operation(summary = "스케일링 그룹 목록 조회", description = "Lablup Backend.AI 시스템의 스케일링 그룹 목록을 조회합니다. 각 그룹의 설정, 드라이버 옵션, 스케줄러 정보 및 자원 할당량을 확인할 수 있습니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<ScalingGroupsResponse> getScalingGroups(
            @Parameter(description = "활성화 여부 필터", example = "true") @RequestParam(value = "is_active", required = false) Boolean isActive) {
        log.info("=== 스케일링 그룹 목록 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - isActive: {}", isActive);

        // isActive가 null이면 기본값 true로 설정
        Boolean activeFilter = isActive != null ? isActive : true;

        try {
            log.info("ResourceService.getScalingGroups 호출 시작");
            ScalingGroupsResponse response = resourceService.getScalingGroups(activeFilter);

            log.info("ResourceService.getScalingGroups 호출 완료");
            log.info("응답 데이터: {}", response);

            AxResponseEntity<ScalingGroupsResponse> result = AxResponseEntity.ok(response,
                    "스케일링 그룹 목록을 성공적으로 조회했습니다.");

            log.info("=== 스케일링 그룹 목록 조회 API 호출 완료 ===");
            return result;

        } catch (BusinessException e) {
            log.error("=== 스케일링 그룹 목록 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - isActive: {}, errorCode: {}", isActive, e.getErrorCode(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("=== 스케일링 그룹 목록 조회 API 호출 실패 (IllegalArgumentException) ===");
            log.error("요청 파라미터 - isActive: {}", isActive, e);
            throw e;
        } catch (NullPointerException e) {
            log.error("=== 스케일링 그룹 목록 조회 API 호출 실패 (NullPointerException) ===");
            log.error("요청 파라미터 - isActive: {}", isActive, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 스케일링 그룹 목록 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - isActive: {}", isActive, e);
            throw e;
        }
    }
}
