package com.skax.aiplatform.client.sktai.agent;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * SKTAI Agent Apps API Feign Client
 * 
 * <p>SKTAI Agent 시스템의 Applications 관리를 위한 Feign Client입니다.
 * Agent Apps는 AI 에이전트 애플리케이션의 생성, 관리, 배포 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>App CRUD</strong>: 애플리케이션 생성, 조회, 수정, 삭제</li>
 *   <li><strong>App 목록 관리</strong>: 페이징 및 필터링된 애플리케이션 목록 조회</li>
 *   <li><strong>App 배포</strong>: 애플리케이션 빌드 및 배포 관리</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>GET /api/v1/agent/agents/apps</code>: 애플리케이션 목록 조회</li>
 *   <li><code>POST /api/v1/agent/agents/apps</code>: 새 애플리케이션 생성</li>
 *   <li><code>GET /api/v1/agent/agents/apps/{appUuid}</code>: 애플리케이션 상세 조회</li>
 *   <li><code>PUT /api/v1/agent/agents/apps/{appUuid}</code>: 애플리케이션 수정</li>
 *   <li><code>DELETE /api/v1/agent/agents/apps/{appUuid}</code>: 애플리케이션 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see AppCreateRequest App 생성 요청 DTO
 * @see AppResponse App 상세 응답 DTO
 */
@FeignClient(
    name = "sktai-agent-apps-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiAgentAppsClient {

    /**
     * Agent Apps 목록 조회
     * 
     * <p>등록된 Agent 애플리케이션들의 페이징된 목록을 조회합니다.
     * 필터링 및 정렬 옵션을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 옵션 (예: "created_at", "name")
     * @param filter 필터 조건
     * @param search 검색어
     * @return Agent Apps 목록 응답
     */
    @Operation(
        summary = "Agent Apps 목록 조회",
        description = "등록된 Agent 애플리케이션들의 페이징된 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apps 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/agents/apps")
    AppsResponse getApps(
        @Parameter(description = "타겟 타입", example = "all")
        @RequestParam(value = "target_type", defaultValue = "all") String targetType,
        
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        
        @Parameter(description = "정렬 옵션", example = "created_at")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * 새로운 Agent App 생성
     * 
     * <p>새로운 Agent 애플리케이션을 생성합니다.
     * 애플리케이션 기본 정보와 설정을 포함해야 합니다.</p>
     * 
     * @param request App 생성 요청 데이터
     * @return 생성된 App 정보
     */
    @Operation(
        summary = "Agent App 생성",
        description = "새로운 Agent 애플리케이션을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "App 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "409", description = "App 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/api/v1/agent/agents/apps", consumes = MediaType.APPLICATION_JSON_VALUE)
    AppCreateResponse createApp(
        @Parameter(description = "App 생성 요청 데이터", required = true)
        @RequestBody AppCreateRequest request
    );

    /**
     * Agent App 상세 정보 조회
     * 
     * <p>특정 Agent 애플리케이션의 상세 정보를 조회합니다.
     * 설정, 상태, 메타데이터 등을 포함합니다.</p>
     * 
     * @param appUuid 조회할 App의 UUID
     * @return App 상세 정보
     */
    @Operation(
        summary = "Agent App 상세 조회",
        description = "특정 Agent 애플리케이션의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "App 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/agents/apps/{appUuid}")
    AppDetailResponse getApp(
        @Parameter(description = "조회할 App의 UUID", required = true, example = "app-12345678-1234-1234-1234-123456789abc")
        @PathVariable("appUuid") String appUuid
    );

    /**
     * Agent App 정보 수정
     * 
     * <p>기존 Agent 애플리케이션의 정보를 수정합니다.
     * 이름, 설명, 설정 등을 업데이트할 수 있습니다.</p>
     * 
     * @param appUuid 수정할 App의 UUID
     * @param request App 수정 요청 데이터
     * @return 수정 결과
     */
    @Operation(
        summary = "Agent App 수정",
        description = "기존 Agent 애플리케이션의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "App 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "App 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping(value = "/api/v1/agent/agents/apps/{appUuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    AppUpdateOrDeleteResponse updateApp(
        @Parameter(description = "수정할 App의 UUID", required = true, example = "app-12345678-1234-1234-1234-123456789abc")
        @PathVariable("appUuid") String appUuid,
        
        @Parameter(description = "App 수정 요청 데이터", required = true)
        @RequestBody AppUpdateRequest request
    );

    /**
     * Agent App 삭제
     * 
     * <p>특정 Agent 애플리케이션을 시스템에서 삭제합니다.
     * 삭제된 App은 복구할 수 없습니다.</p>
     * 
     * @param appUuid 삭제할 App의 UUID
     * @return 삭제 결과
     */
    @Operation(
        summary = "Agent App 삭제",
        description = "특정 Agent 애플리케이션을 시스템에서 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "App 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "App이 사용 중이어서 삭제 불가"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/api/v1/agent/agents/apps/{appUuid}")
    AppUpdateOrDeleteResponse deleteApp(
        @Parameter(description = "삭제할 App의 UUID", required = true, example = "app-12345678-1234-1234-1234-123456789abc")
        @PathVariable("appUuid") String appUuid
    );

    /**
     * Agent Apps 하드 삭제 (완전 삭제)
     */
    @PostMapping("/api/v1/agent/agents/apps/hard-delete")
    @Operation(
        summary = "Agent Apps 하드 삭제",
        description = "삭제 표시된 Agent App들을 완전히 제거합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "하드 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppHardDeleteResponse hardDeleteApps(
        @Parameter(description = "하드 삭제 요청", required = true)
        @RequestBody AppHardDeleteRequest request
    );

    /**
     * Agent App 배포 목록 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/{appId}/deployments")
    @Operation(
        summary = "Agent App 배포 목록 조회",
        description = "특정 Agent App의 배포 목록을 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배포 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppDeploymentsResponse getAppDeployments(
        @Parameter(description = "App ID", required = true)
        @PathVariable("appId") String appId,

        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        
        @Parameter(description = "정렬 옵션", example = "created_at")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search


    );

    /**
     * 커스텀 Agent App 생성 및 배포
     */
    @PostMapping(value = "/api/v1/agent/agents/apps/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "커스텀 Agent App 생성 및 배포",
        description = "커스텀 설정으로 Agent App을 생성하고 배포합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "커스텀 App 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppCreateResponse createCustomApp(
        @Parameter(description = "환경 파일")
        @RequestPart(value = "env_file", required = false) MultipartFile envFile,
        
        @Parameter(description = "앱 이름", required = true)
        @RequestPart("name") String name,
        
        @Parameter(description = "앱 설명", required = true)
        @RequestPart("description") String description,
        
        @Parameter(description = "버전 설명")
        @RequestPart(value = "version_description", required = false) String versionDescription,
        
        @Parameter(description = "타겟 타입")
        @RequestPart(value = "target_type", required = false) String targetType,
        
        @Parameter(description = "모델 목록")
        @RequestPart(value = "model_list", required = false) java.util.List<String> modelList,
        
        @Parameter(description = "이미지 URL")
        @RequestPart(value = "image_url", required = false) String imageUrl,
        
        @Parameter(description = "외부 레지스트리 사용 여부")
        @RequestPart(value = "use_external_registry", required = false) Boolean useExternalRegistry,
        
        @Parameter(description = "CPU 요청")
        @RequestPart(value = "cpu_request", required = false) Integer cpuRequest,
        
        @Parameter(description = "CPU 제한")
        @RequestPart(value = "cpu_limit", required = false) Integer cpuLimit,
        
        @Parameter(description = "메모리 요청 (GB)")
        @RequestPart(value = "mem_request", required = false) Integer memRequest,
        
        @Parameter(description = "메모리 제한 (GB)")
        @RequestPart(value = "mem_limit", required = false) Integer memLimit,
        
        @Parameter(description = "최소 복제본 수")
        @RequestPart(value = "min_replicas", required = false) Integer minReplicas,
        
        @Parameter(description = "최대 복제본 수")
        @RequestPart(value = "max_replicas", required = false) Integer maxReplicas,
        
        @Parameter(description = "코어당 워커 수")
        @RequestPart(value = "workers_per_core", required = false) Integer workersPerCore,
        
        @Parameter(description = "안전 필터 옵션")
        @RequestPart(value = "safety_filter_options", required = false) Object safetyFilterOptions,

        @Parameter(description = "정책 목록")
        @RequestPart(value = "policy", required = false) List<PolicyRequest> policy
    );

    /**
     * 커스텀 배포 추가 (Multipart)
     */
    @PostMapping(value = "/api/v1/agent/agents/apps/deployments/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "커스텀 배포 추가",
        description = "기존 App에 커스텀 배포를 추가합니다 (multipart/form-data)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "커스텀 배포 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppDeploymentResponse addCustomDeploymentWithMultipart(
        @Parameter(description = "App ID", required = true)
        @RequestParam("app_id") String appId,
        
        @Parameter(description = "환경 파일")
        @RequestPart(value = "env_file", required = false) MultipartFile envFile,
        
        @Parameter(description = "앱 이름", required = true)
        @RequestPart("name") String name,
        
        @Parameter(description = "앱 설명", required = true)
        @RequestPart("description") String description,
        
        @Parameter(description = "버전 설명")
        @RequestPart(value = "version_description", required = false) String versionDescription,
        
        @Parameter(description = "타겟 타입")
        @RequestPart(value = "target_type", required = false) String targetType,
        
        @Parameter(description = "모델 목록")
        @RequestPart(value = "model_list", required = false) java.util.List<String> modelList,
        
        @Parameter(description = "이미지 URL")
        @RequestPart(value = "image_url", required = false) String imageUrl,
        
        @Parameter(description = "외부 레지스트리 사용 여부")
        @RequestPart(value = "use_external_registry", required = false) Boolean useExternalRegistry,
        
        @Parameter(description = "CPU 요청")
        @RequestPart(value = "cpu_request", required = false) Integer cpuRequest,
        
        @Parameter(description = "CPU 제한")
        @RequestPart(value = "cpu_limit", required = false) Integer cpuLimit,
        
        @Parameter(description = "메모리 요청 (GB)")
        @RequestPart(value = "mem_request", required = false) Integer memRequest,
        
        @Parameter(description = "메모리 제한 (GB)")
        @RequestPart(value = "mem_limit", required = false) Integer memLimit,
        
        @Parameter(description = "최소 복제본 수")
        @RequestPart(value = "min_replicas", required = false) Integer minReplicas,
        
        @Parameter(description = "최대 복제본 수")
        @RequestPart(value = "max_replicas", required = false) Integer maxReplicas,
        
        @Parameter(description = "코어당 워커 수")
        @RequestPart(value = "workers_per_core", required = false) Integer workersPerCore,
        
        @Parameter(description = "안전 필터 옵션")
        @RequestPart(value = "safety_filter_options", required = false) Object safetyFilterOptions,
        
        @Parameter(description = "정책 요청 목록")
        @RequestPart(value = "policy", required = false) java.util.List<com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest> policy
    );
    
    /**
     * 커스텀 배포 추가 (JSON)
     */
    @PostMapping("/api/v1/agent/agents/apps/deployments/custom")
    @Operation(
        summary = "커스텀 배포 추가",
        description = "기존 App에 커스텀 배포를 추가합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "커스텀 배포 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppDeploymentResponse addCustomDeployment(
        @Parameter(description = "커스텀 배포 추가 요청", required = true)
        @RequestBody AppCustomDeploymentAddRequest request
    );

    /**
     * 배포 중지
     */
    @PostMapping("/api/v1/agent/agents/apps/deployments/stop/{deploymentId}")
    @Operation(
        summary = "배포 중지",
        description = "실행 중인 배포를 중지합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배포 중지 성공"),
        @ApiResponse(responseCode = "404", description = "배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppUpdateOrDeleteResponse stopDeployment(
        @Parameter(description = "중지할 배포 ID", required = true)
        @PathVariable String deploymentId
    );

    /**
     * 배포 재시작
     */
    @PostMapping("/api/v1/agent/agents/apps/deployments/restart/{deploymentId}")
    @Operation(
        summary = "배포 재시작",
        description = "중지된 배포를 재시작합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배포 재시작 성공"),
        @ApiResponse(responseCode = "404", description = "배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppUpdateOrDeleteResponse restartDeployment(
        @Parameter(description = "재시작할 배포 ID", required = true)
        @PathVariable String deploymentId
    );

    /**
     * 배포 삭제
     */
    @DeleteMapping("/api/v1/agent/agents/apps/deployments/{deploymentId}")
    @Operation(
        summary = "배포 삭제",
        description = "특정 배포를 삭제합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배포 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppUpdateOrDeleteResponse deleteDeployment(
        @Parameter(description = "삭제할 배포 ID", required = true)
        @PathVariable String deploymentId
    );

    /**
     * 배포 상세 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/deployments/{deploymentId}")
    @Operation(
        summary = "배포 상세 조회",
        description = "특정 배포의 상세 정보를 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배포 조회 성공"),
        @ApiResponse(responseCode = "404", description = "배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppDeploymentResponse getDeployment(
        @Parameter(description = "조회할 배포 ID", required = true)
        @PathVariable String deploymentId
    );

    /**
     * Agent App API 키 목록 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/{appId}/apikeys")
    @Operation(
        summary = "Agent App API 키 목록 조회",
        description = "특정 Agent App의 API 키 목록을 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppApiKeysResponse getAppApiKeys(
        @Parameter(description = "App ID", required = true)
        @PathVariable String appId
    );

    /**
     * Agent App API 키 생성
     */
    @PostMapping("/api/v1/agent/agents/apps/{appId}/apikeys")
    @Operation(
        summary = "Agent App API 키 생성",
        description = "특정 Agent App에 새로운 API 키를 생성합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "API 키 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "App을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppApiKeyCreateResponse createAppApiKey(
        @Parameter(description = "App ID", required = true)
        @PathVariable String appId,
        @Parameter(description = "API 키 생성 요청 (정책 배열)", required = true)
        @RequestBody List<PolicyRequest> request
    );

    /**
     * Agent App API 키 재생성
     */
    @PutMapping("/api/v1/agent/agents/apps/{appId}/apikeys/{apiKey}/regenerate")
    @Operation(
        summary = "Agent App API 키 재생성",
        description = "특정 Agent App의 API 키를 재생성하거나 서빙 ID를 업데이트합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 키 재생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "App 또는 API 키를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppApiKeyCreateResponse regenerateAppApiKey(
        @Parameter(description = "App ID", required = true)
        @PathVariable String appId,
        @Parameter(description = "재생성할 API 키", required = true)
        @PathVariable String apiKey,
        @Parameter(description = "API 키 재생성 요청", required = true)
        @RequestBody AppApiKeyRegenerateRequest request
    );

    /**
     * Agent App API 키 삭제
     */
    @DeleteMapping("/api/v1/agent/agents/apps/{appId}/apikeys/{apiKey}")
    @Operation(
        summary = "Agent App API 키 삭제",
        description = "특정 Agent App의 API 키를 삭제합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API 키 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "App 또는 API 키를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppUpdateOrDeleteResponse deleteAppApiKey(
        @Parameter(description = "App ID", required = true)
        @PathVariable String appId,
        @Parameter(description = "삭제할 API 키", required = true)
        @PathVariable String apiKey
    );

    /**
     * 모델별 Agent App 목록 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/model/{modelName}")
    @Operation(
        summary = "모델별 Agent App 목록 조회",
        description = "특정 모델을 사용하는 Agent App 목록을 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "App 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppsResponse getAppsByModel(
        @Parameter(description = "모델 이름", required = true)
        @PathVariable String modelName
    );

    /**
     * 지식베이스별 Agent App 목록 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/knowledge/{knowledgeId}")
    @Operation(
        summary = "지식베이스별 Agent App 목록 조회",
        description = "특정 지식베이스를 사용하는 Agent App 목록을 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "App 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "지식베이스를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppsResponse getAppsByKnowledge(
        @Parameter(description = "지식베이스 ID", required = true)
        @PathVariable String knowledgeId
    );

    /**
     * Phoenix 프로젝트 ID 조회
     */
    @GetMapping("/api/v1/agent/agents/apps/phoenix/projects/{projectName}")
    @Operation(
        summary = "Phoenix 프로젝트 ID 조회",
        description = "Phoenix 프로젝트 이름으로 프로젝트 ID를 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 ID 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    Object getPhoenixProjectId(
        @Parameter(description = "프로젝트 이름", required = true)
        @PathVariable String projectName
    );
}
