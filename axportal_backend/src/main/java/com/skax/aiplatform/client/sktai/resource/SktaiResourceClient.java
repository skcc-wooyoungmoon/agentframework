package com.skax.aiplatform.client.sktai.resource;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
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
import com.skax.aiplatform.client.sktai.resource.service.SktaiResourceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Resource Management API Feign Client
 * 
 * <p>SKTAI 플랫폼의 리소스 관리 API와 통신하기 위한 Feign Client 인터페이스입니다.
 * 시스템 리소스 모니터링, 할당, 스케일링, 최적화 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능 영역:</h3>
 * <ul>
 *   <li><strong>Resource Monitoring</strong>: 시스템 리소스 사용량 모니터링</li>
 *   <li><strong>Resource Allocation</strong>: 컴퓨팅 리소스 할당 및 관리</li>
 *   <li><strong>Auto Scaling</strong>: 자동 스케일링 및 용량 조정</li>
 *   <li><strong>Cost Optimization</strong>: 비용 최적화 및 예산 관리</li>
 *   <li><strong>Performance Tuning</strong>: 성능 최적화 및 튜닝</li>
 * </ul>
 * 
 * <h3>지원 리소스:</h3>
 * <ul>
 *   <li><strong>컴퓨팅</strong>: CPU, GPU, 메모리 관리</li>
 *   <li><strong>스토리지</strong>: 디스크, 오브젝트 스토리지</li>
 *   <li><strong>네트워크</strong>: 대역폭, 로드밸런서</li>
 *   <li><strong>데이터베이스</strong>: RDS, 캐시, 연결풀</li>
 *   <li><strong>AI/ML</strong>: 모델 인스턴스, 추론 엔진</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 리소스 사용량 모니터링
 * ResourceMonitorRequest monitorRequest = ResourceMonitorRequest.builder()
 *     .resourceTypes(Arrays.asList("cpu", "memory", "gpu"))
 *     .interval("5m")
 *     .build();
 * ResourceUsageResponse usage = sktaiResourceClient.getResourceUsage(monitorRequest);
 * 
 * // GPU 리소스 할당
 * ResourceAllocationRequest allocRequest = ResourceAllocationRequest.builder()
 *     .resourceType("gpu")
 *     .quantity(2)
 *     .specifications(Map.of("type", "A100", "memory", "40GB"))
 *     .build();
 * ResourceAllocationResponse allocation = sktaiResourceClient.allocateResource(allocRequest);
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktaiResourceService 비즈니스 로직 서비스 래퍼
 */
@FeignClient(
    name = "sktai-resource-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Resource Management", description = "SKTAI 리소스 관리 API")
public interface SktaiResourceClient {
    
    // ==================== Resource Monitoring ====================
    
    /**
     * 리소스 사용량 조회
     * 
     * <p>시스템 리소스의 현재 사용량과 통계 정보를 조회합니다.
     * CPU, 메모리, 디스크, 네트워크 등의 실시간 사용률을 확인할 수 있습니다.</p>
     * 
     * @param request 모니터링 요청 정보
     * @return 리소스 사용량 정보
     */
    @PostMapping(value = "/api/v1/resources/usage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "리소스 사용량 조회",
        description = "시스템 리소스의 현재 사용량과 통계 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용량 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    ResourceUsageResponse getResourceUsage(@RequestBody ResourceMonitorRequest request);
    
    /**
     * 특정 리소스 사용량 조회
     * 
     * <p>특정 리소스 ID에 대한 상세한 사용량 정보를 조회합니다.
     * 실시간 메트릭과 기간별 통계를 제공합니다.</p>
     * 
     * @param resourceId 리소스 ID
     * @param interval 데이터 수집 간격 (1m, 5m, 15m, 1h, 1d)
     * @param duration 조회 기간 (1h, 6h, 24h, 7d, 30d)
     * @return 리소스 사용량 정보
     */
    @GetMapping("/api/v1/resources/{resource_id}/usage")
    @Operation(
        summary = "특정 리소스 사용량 조회",
        description = "특정 리소스의 상세한 사용량 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용량 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
    })
    ResourceUsageResponse getResourceUsageById(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId,
        
        @Parameter(description = "데이터 수집 간격", example = "5m")
        @RequestParam(value = "interval", defaultValue = "5m") String interval,
        
        @Parameter(description = "조회 기간", example = "1h")
        @RequestParam(value = "duration", defaultValue = "1h") String duration
    );
    
    // ==================== Resource Management ====================
    
    /**
     * 리소스 목록 조회
     * 
     * <p>사용자가 접근 가능한 리소스 목록을 조회합니다.
     * 필터링과 정렬을 통해 원하는 리소스를 찾을 수 있습니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (최대 100)
     * @param resourceType 리소스 타입 필터
     * @param status 상태 필터
     * @param projectId 프로젝트 ID 필터
     * @param region 지역 필터
     * @param sortBy 정렬 기준
     * @param sortOrder 정렬 순서
     * @return 리소스 목록
     */
    @GetMapping("/api/v1/resources")
    @Operation(
        summary = "리소스 목록 조회",
        description = "접근 가능한 리소스 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    ResourceListResponse getResources(
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지 크기 (1-100)", example = "20")
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        
        @Parameter(description = "리소스 타입 필터", example = "gpu")
        @RequestParam(value = "resource_type", required = false) String resourceType,
        
        @Parameter(description = "상태 필터", example = "active")
        @RequestParam(value = "status", required = false) String status,
        
        @Parameter(description = "프로젝트 ID 필터", example = "project-123")
        @RequestParam(value = "project_id", required = false) String projectId,
        
        @Parameter(description = "지역 필터", example = "kr-central-1")
        @RequestParam(value = "region", required = false) String region,
        
        @Parameter(description = "정렬 기준", example = "usage_percent")
        @RequestParam(value = "sort_by", defaultValue = "created_at") String sortBy,
        
        @Parameter(description = "정렬 순서", example = "desc")
        @RequestParam(value = "sort_order", defaultValue = "desc") String sortOrder
    );
    
    /**
     * 리소스 상세 조회
     * 
     * <p>특정 리소스의 상세 정보를 조회합니다.
     * 사양, 상태, 사용량, 비용 등의 모든 정보를 포함합니다.</p>
     * 
     * @param resourceId 리소스 ID
     * @return 리소스 상세 정보
     */
    @GetMapping("/api/v1/resources/{resource_id}")
    @Operation(
        summary = "리소스 상세 조회",
        description = "특정 리소스의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
    })
    ResourceAllocationResponse getResource(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId
    );
    
    // ==================== Resource Allocation ====================
    
    /**
     * 리소스 할당
     * 
     * <p>새로운 리소스를 할당합니다.
     * CPU, GPU, 메모리, 스토리지 등 다양한 타입의 리소스를 요청할 수 있습니다.</p>
     * 
     * @param request 리소스 할당 요청
     * @return 할당된 리소스 정보
     */
    @PostMapping(value = "/api/v1/resources/allocate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "리소스 할당",
        description = "새로운 리소스를 할당합니다. 다양한 타입의 컴퓨팅 리소스를 요청할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "할당 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "409", description = "리소스 부족"),
        @ApiResponse(responseCode = "429", description = "할당 한도 초과")
    })
    ResourceAllocationResponse allocateResource(@RequestBody ResourceAllocationRequest request);
    
    /**
     * 리소스 할당 해제
     * 
     * <p>할당된 리소스를 해제합니다.
     * 데이터 백업이 필요한 경우 미리 백업을 완료해야 합니다.</p>
     * 
     * @param resourceId 리소스 ID
     * @param force 강제 해제 여부 (기본값: false)
     * @param backup 해제 전 백업 여부 (기본값: true)
     */
    @DeleteMapping("/api/v1/resources/{resource_id}")
    @Operation(
        summary = "리소스 할당 해제",
        description = "할당된 리소스를 해제합니다. 필요시 백업을 수행한 후 해제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "해제 성공"),
        @ApiResponse(responseCode = "400", description = "해제할 수 없는 상태"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
    })
    void deallocateResource(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId,
        
        @Parameter(description = "강제 해제 여부", example = "false")
        @RequestParam(value = "force", defaultValue = "false") Boolean force,
        
        @Parameter(description = "해제 전 백업 여부", example = "true")
        @RequestParam(value = "backup", defaultValue = "true") Boolean backup
    );
    
    // ==================== Resource Scaling ====================
    
    /**
     * 리소스 스케일링
     * 
     * <p>기존 리소스의 용량을 조정합니다.
     * 수직 스케일링(성능 향상)과 수평 스케일링(인스턴스 증감)을 지원합니다.</p>
     * 
     * @param resourceId 리소스 ID
     * @param request 스케일링 요청
     * @return 스케일링된 리소스 정보
     */
    @PostMapping(value = "/api/v1/resources/{resource_id}/scale", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "리소스 스케일링",
        description = "기존 리소스의 용량을 조정합니다. 수직/수평 스케일링을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스케일링 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 스케일링 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "스케일링 불가능한 상태")
    })
    ResourceAllocationResponse scaleResource(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId,
        
        @RequestBody ResourceScalingRequest request
    );
    
    /**
     * 자동 스케일링 설정
     * 
     * <p>리소스의 자동 스케일링 정책을 설정합니다.
     * CPU, 메모리 사용률 등의 메트릭 기반으로 자동 스케일링이 수행됩니다.</p>
     * 
     * @param resourceId 리소스 ID
     * @param policy 자동 스케일링 정책
     */
    @PutMapping(value = "/api/v1/resources/{resource_id}/auto-scaling", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "자동 스케일링 설정",
        description = "리소스의 자동 스케일링 정책을 설정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "설정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 정책 설정"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
    })
    void setAutoScalingPolicy(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId,
        
        @RequestBody Object policy
    );
    
    /**
     * 자동 스케일링 해제
     * 
     * <p>리소스의 자동 스케일링을 비활성화합니다.
     * 수동으로만 스케일링이 가능해집니다.</p>
     * 
     * @param resourceId 리소스 ID
     */
    @DeleteMapping("/api/v1/resources/{resource_id}/auto-scaling")
    @Operation(
        summary = "자동 스케일링 해제",
        description = "리소스의 자동 스케일링을 비활성화합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "해제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
    })
    void disableAutoScaling(
        @Parameter(description = "리소스 ID", example = "resource-123")
        @PathVariable("resource_id") String resourceId
    );
    
    // ==================== Resource Optimization ====================
    
    /**
     * 리소스 최적화 제안
     * 
     * <p>현재 리소스 사용 패턴을 분석하여 최적화 제안을 제공합니다.
     * 비용 절감, 성능 향상, 효율성 개선 방안을 포함합니다.</p>
     * 
     * @param projectId 프로젝트 ID (선택사항)
     * @param resourceType 리소스 타입 필터 (선택사항)
     * @param days 분석 기간 (일 단위, 기본값: 7)
     * @return 최적화 제안 목록
     */
    @GetMapping("/api/v1/resources/optimization/recommendations")
    @Operation(
        summary = "리소스 최적화 제안",
        description = "현재 리소스 사용 패턴을 분석하여 최적화 제안을 제공합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "제안 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    OptimizationRecommendationsResponse getOptimizationRecommendations(
        @Parameter(description = "프로젝트 ID", example = "project-123")
        @RequestParam(value = "project_id", required = false) String projectId,
        
        @Parameter(description = "리소스 타입", example = "gpu")
        @RequestParam(value = "resource_type", required = false) String resourceType,
        
        @Parameter(description = "분석 기간 (일)", example = "7")
        @RequestParam(value = "days", defaultValue = "7") Integer days
    );
    
    /**
     * 리소스 비용 분석
     * 
     * <p>리소스 사용 비용을 상세 분석합니다.
     * 프로젝트별, 리소스별, 시간대별 비용 분석을 제공합니다.</p>
     * 
     * @param projectId 프로젝트 ID (선택사항)
     * @param startDate 시작 날짜 (YYYY-MM-DD)
     * @param endDate 종료 날짜 (YYYY-MM-DD)
     * @param groupBy 그룹화 기준 (project, resource_type, region, day, hour)
     * @return 비용 분석 결과
     */
    @GetMapping("/api/v1/resources/cost-analysis")
    @Operation(
        summary = "리소스 비용 분석",
        description = "리소스 사용 비용을 상세 분석합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "분석 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    CostAnalysisResponse getCostAnalysis(
        @Parameter(description = "프로젝트 ID", example = "project-123")
        @RequestParam(value = "project_id", required = false) String projectId,
        
        @Parameter(description = "시작 날짜", example = "2025-08-01")
        @RequestParam(value = "start_date") String startDate,
        
        @Parameter(description = "종료 날짜", example = "2025-08-15")
        @RequestParam(value = "end_date") String endDate,
        
        @Parameter(description = "그룹화 기준", example = "resource_type")
        @RequestParam(value = "group_by", defaultValue = "day") String groupBy
    );

       // ==================== Cluster Management ====================
    
    /**
     * 클러스터 리소스 조회
     * 
     * <p>클러스터의 리소스 정보를 조회합니다.
     * 노드 타입별로 클러스터 상태와 리소스 사용량을 확인할 수 있습니다.</p>
     * 
     * @param nodeType 노드 타입 (task, master, worker 등)
     * @param projectId 프로젝트 ID (선택적)
     * @return 클러스터 리소스 정보
     */
    @GetMapping("/api/v1/resources/task/{node_type}")
    @Operation(
        summary = "클러스터 리소스 조회",
        description = "클러스터의 리소스 정보를 노드 타입별로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "클러스터를 찾을 수 없음")
    })
    TaskResourceResponse getClusterResources(
        @Parameter(description = "노드 타입", example = "agent")
        @PathVariable("node_type") String nodeType,
        @Parameter(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        @RequestParam(value = "project_id", required = false) String projectId
    );

     
     /**
      * Task Policy 전체 목록 조회
      * 
      * <p>시스템에 등록된 모든 Task Policy 목록을 조회합니다.
      * 정책별 상세 정보와 설정값을 확인할 수 있습니다.</p>
      * 
      * @return Task Policy 목록
      */
    @GetMapping("/api/v1/resources/task_policy")
    @Operation(
        summary = "Task Policy 전체 목록 조회",
        description = "시스템에 등록된 모든 Task Policy 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    List<TaskPolicyResponse> getTaskPolicyList();

    /**
     * 태스크 타입별 리소스 정보 조회
     *
     * <p>
     * 특정 태스크 타입에 대한 리소스 정보를 조회합니다.
     * 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.
     * </p>
     *
     * @param taskType  태스크 타입 (finetuning, serving, evaluation, test 등)
     * @param projectId 프로젝트 ID
     * @return 태스크 리소스 정보
     */
    @GetMapping("/api/v1/resources/task/{task_type}")
    @Operation(summary = "태스크 타입별 리소스 정보 조회", description = "특정 태스크 타입에 대한 리소스 정보를 조회합니다. 노드별 리소스 사용량, 네임스페이스 리소스, 태스크 정책, 할당량 정보를 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "404", description = "태스크 타입을 찾을 수 없음")
    })
    TaskResourceResponse getTaskResource(
            @Parameter(description = "태스크 타입", example = "finetuning", required = true) @PathVariable("task_type") String taskType,

            @Parameter(description = "프로젝트 ID", example = "e66bb3b1-95b5-4dce-b326-eacc48b0f387", required = true) @RequestParam("project_id") String projectId);

}
