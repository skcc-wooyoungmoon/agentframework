package com.skax.aiplatform.controller.deploy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.AgentSysLogSearchReq;
import com.skax.aiplatform.dto.deploy.request.AppCreateReq;
import com.skax.aiplatform.dto.deploy.request.AppUpdateReq;
import com.skax.aiplatform.dto.deploy.request.StreamReq;
import com.skax.aiplatform.dto.deploy.response.AgentAppRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployInfoRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployUpdateOrDeleteRes;
import com.skax.aiplatform.dto.deploy.response.AgentServingRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeyCreateRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeysRes;
import com.skax.aiplatform.dto.deploy.response.AppCreateRes;
import com.skax.aiplatform.service.deploy.AgentDeployService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent 배포 관리 컨트롤러
 * 
 * <p>Agent 애플리케이션의 배포, 관리, 모니터링을 위한 API 엔드포인트를 제공합니다.
 * Agent 배포 생성, 조회, 수정, 삭제 및 관련 기능을 포함합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/agentDeploy")
@RequiredArgsConstructor
@Tag(name = "Agent Deploy Management", description = "Agent 배포 관리 API")
public class AgentDeployController {

    private final AgentDeployService agentDeployService;
    
    @Value("${sktai.api.base-url}")
    private String baseUrl;

    /**
     * Agent Apps 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터
     * @param search 검색어
     * @return Agent 배포 목록 (페이지네이션 포함)
     */
    @GetMapping("/app")
    @Operation(
        summary = "Agent Apps 목록 조회",
        description = "Agent 애플리케이션 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent Apps 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<AgentAppRes>> getAgentAppList(
            @RequestParam(value = "targetType", defaultValue = "all") 
            @Parameter(description = "타겟 타입", example = "all") String targetType,
            @RequestParam(value = "page", defaultValue = "1") 
            @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") 
            @Parameter(description = "페이지 크기", example = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "created_at,desc") 
            @Parameter(description = "정렬 기준", example = "created_at,desc") String sort,
            @RequestParam(value = "filter", defaultValue = "") 
            @Parameter(description = "필터", example = "") String filter,
            @RequestParam(value = "search", defaultValue = "") 
            @Parameter(description = "검색어") String search) {
        
        PageResponse<AgentAppRes> agentDeployRes = agentDeployService.getAgentAppList(targetType, page, size, sort, filter, search);

        return AxResponseEntity.ok(agentDeployRes, "Agent Apps 목록을 성공적으로 조회했습니다.");
    }

    /**
     * Agent 배포 목록 조회 (앱별)
     * 
     * @param appId 앱 ID
     * @return 배포 목록
     */
    @GetMapping("/app/{appId}/deploy")
    @Operation(
        summary = "Agent 배포 목록 조회 (앱별)",
        description = "특정 앱의 배포 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "앱을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<AgentDeployRes>> getAgentAppDeployListById(
            @PathVariable("appId") String appId,
            @RequestParam(value = "page", defaultValue = "1") 
            @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "6") 
            @Parameter(description = "페이지 크기", example = "6") Integer size,
            @RequestParam(value = "sort", required = false) 
            @Parameter(description = "정렬 기준") String sort,
            @RequestParam(value = "filter", required = false) 
            @Parameter(description = "필터") String filter,
            @RequestParam(value = "search", required = false) 
            @Parameter(description = "검색어") String search) {
        
        PageResponse<AgentDeployRes> agentDeployRes = agentDeployService.getAgentAppDeployListById(appId, page, size, sort, filter, search);

        return AxResponseEntity.ok(agentDeployRes, "Agent 배포 목록 (앱별)을 성공적으로 조회했습니다.");
    }

    /**
     * Agent 배포 상세 조회
     * 
     * @param deployId 배포 ID
     * @return 배포 상세 정보
     */
    @GetMapping("/app/deploy/{deployId}")
    @Operation(
        summary = "Agent 배포 상세 조회",
        description = "UUID를 통해 특정 Agent 배포의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentDeployRes> getAgentAppDeployById(
            @PathVariable("deployId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String deployId) {

        AgentDeployRes agentDeployRes = agentDeployService.getAgentAppDeployById(deployId);

        return AxResponseEntity.ok(agentDeployRes, "Agent 배포 정보를 성공적으로 조회했습니다.");
    }

        /**
     * Agent App 배포 상세 조회
     * 
     * @param appId 앱 ID
     * @return 앱 배포 상세 정보
     */
    @GetMapping("/app/{appId}")
    @Operation(
        summary = "Agent App 배포 상세 조회",
        description = "특정 앱의 배포 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "앱 배포 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "앱을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentAppRes> getAgentAppById(
            @PathVariable("appId") 
            @Parameter(description = "앱 ID", example = "app-12345") String appId) {

        AgentAppRes agentAppRes = agentDeployService.getAgentAppById(appId);

        return AxResponseEntity.ok(agentAppRes, "Agent App 배포 상세 정보를 성공적으로 조회했습니다.");
    }

        /**
     * 새로운 Agent 배포 생성
     * 
     * @param request Agent 배포 생성 요청
     * @return 생성된 배포 정보
     */
    @PostMapping("/app")
    @Operation(
        summary = "새로운 Agent 배포 생성",
        description = "새로운 Agent 배포를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Agent 배포 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AppCreateRes> createAgentApp(
            @RequestBody AppCreateReq request) {

        AppCreateRes appCreateRes = agentDeployService.createAgentApp(request);

        return AxResponseEntity.ok(appCreateRes, "새로운 Agent 배포가 성공적으로 생성되었습니다.");
    }

    /**
     * 커스텀 Agent App 생성 및 배포
     * 
     * @param envFile 환경 파일 (선택)
     * @param request 커스텀 앱 생성 요청
     * @return 생성된 앱 정보
     */
    // @PostMapping(value = "/app/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @Operation(
    //     summary = "커스텀 Agent App 생성 및 배포",
    //     description = "커스텀 설정으로 Agent App을 생성하고 배포합니다."
    // )
    // @ApiResponses({
    @PostMapping(value = "/app/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "커스텀 Agent App 생성",
        description = "커스텀 Agent App을 생성하고 배포합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "커스텀 Agent App 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AppCreateRes> createCustomAgentApp(
            @Parameter(description = "환경 파일")
            @RequestPart(value = "env_file", required = false) MultipartFile envFile,
            @Parameter(description = "앱 이름", required = true)
            @RequestParam(value = "name", required = true) String name,
            @Parameter(description = "앱 설명", required = true)
            @RequestParam(value = "description", required = true) String description,
            @Parameter(description = "버전 설명")
            @RequestParam(value = "version_description", required = false) String versionDescription,
            @Parameter(description = "타겟 타입")
            @RequestParam(value = "target_type", required = false) String targetType,
            @Parameter(description = "모델 목록")
            @RequestParam(value = "model_list", required = false) List<String> modelList,
            @Parameter(description = "이미지 URL")
            @RequestParam(value = "image_url", required = false) String imageUrl,
            @Parameter(description = "외부 레지스트리 사용 여부")
            @RequestParam(value = "use_external_registry", required = false) Boolean useExternalRegistry,
            @Parameter(description = "CPU 요청")
            @RequestParam(value = "cpu_request", required = false) Integer cpuRequest,
            @Parameter(description = "CPU 제한")
            @RequestParam(value = "cpu_limit", required = false) Integer cpuLimit,
            @Parameter(description = "메모리 요청 (GB)")
            @RequestParam(value = "mem_request", required = false) Integer memRequest,
            @Parameter(description = "메모리 제한 (GB)")
            @RequestParam(value = "mem_limit", required = false) Integer memLimit,
            @Parameter(description = "최소 복제본 수")
            @RequestParam(value = "min_replicas", required = false) Integer minReplicas,
            @Parameter(description = "최대 복제본 수")
            @RequestParam(value = "max_replicas", required = false) Integer maxReplicas,
            @Parameter(description = "코어당 워커 수")
            @RequestParam(value = "workers_per_core", required = false) Integer workersPerCore,
            @Parameter(
                description = "안전 필터 옵션 (object | string | null)",
                schema = @Schema(oneOf = {Object.class, String.class}, nullable = true)
            )
            @RequestPart(value = "safety_filter_options", required = false) Object safetyFilterOptions,
            @Parameter(description = "정책 조회에 사용할 사용자 ID", required = false)
            @RequestParam(value = "user_id", required = false) String userId,
            @Parameter(description = "정책 조회에 사용할 프로젝트명 (GPO_PRJ_NM)", required = false)
            @RequestParam(value = "project_name", required = false) String projectName) {

        AppCreateRes appCreateRes = agentDeployService.createCustomAgentApp(
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
                userId,
                projectName);

        return AxResponseEntity.created(appCreateRes, "커스텀 Agent App이 성공적으로 생성되었습니다.");
    }

    /**
     * 커스텀 배포 추가 (Multipart)
     * 
     * @param appId 앱 ID (필수)
     * @param envFile 환경 파일 (선택)
     * @param name 앱 이름 (필수)
     * @param description 앱 설명 (필수)
     * @param versionDescription 버전 설명 (선택)
     * @param targetType 타겟 타입
     * @param modelList 모델 목록
     * @param imageUrl 이미지 URL
     * @param useExternalRegistry 외부 레지스트리 사용 여부
     * @param cpuRequest CPU 요청
     * @param cpuLimit CPU 제한
     * @param memRequest 메모리 요청 (GB)
     * @param memLimit 메모리 제한 (GB)
     * @param minReplicas 최소 복제본 수
     * @param maxReplicas 최대 복제본 수
     * @param workersPerCore 코어당 워커 수
     * @param safetyFilterOptions 안전 필터 옵션
     * @return 생성된 배포 정보
     */
    @PostMapping(value = "/app/deployments/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "커스텀 배포 추가",
        description = "기존 App에 커스텀 배포를 추가합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "커스텀 배포 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentDeployRes> addCustomDeploymentWithMultipart(
            @Parameter(description = "App ID", required = true)
            @RequestParam(value = "app_id", required = true) String appId,
            
            @Parameter(description = "환경 파일")
            @RequestPart(value = "env_file", required = false) MultipartFile envFile,
            
            @Parameter(description = "앱 이름", required = true)
            @RequestParam(value = "name", required = true) String name,
            
            @Parameter(description = "앱 설명", required = true)
            @RequestParam(value = "description", required = true) String description,
            
            @Parameter(description = "버전 설명")
            @RequestParam(value = "version_description", required = false) String versionDescription,
            
            @Parameter(description = "타겟 타입")
            @RequestParam(value = "target_type", required = false) String targetType,
            
            @Parameter(description = "모델 목록")
            @RequestParam(value = "model_list", required = false) List<String> modelList,
            
            @Parameter(description = "이미지 URL")
            @RequestParam(value = "image_url", required = false) String imageUrl,
            
            @Parameter(description = "외부 레지스트리 사용 여부")
            @RequestParam(value = "use_external_registry", required = false) Boolean useExternalRegistry,
            
            @Parameter(description = "CPU 요청")
            @RequestParam(value = "cpu_request", required = false) Integer cpuRequest,
            
            @Parameter(description = "CPU 제한")
            @RequestParam(value = "cpu_limit", required = false) Integer cpuLimit,
            
            @Parameter(description = "메모리 요청 (GB)")
            @RequestParam(value = "mem_request", required = false) Integer memRequest,
            
            @Parameter(description = "메모리 제한 (GB)")
            @RequestParam(value = "mem_limit", required = false) Integer memLimit,
            
            @Parameter(description = "최소 복제본 수")
            @RequestParam(value = "min_replicas", required = false) Integer minReplicas,
            
            @Parameter(description = "최대 복제본 수")
            @RequestParam(value = "max_replicas", required = false) Integer maxReplicas,
            
            @Parameter(description = "코어당 워커 수")
            @RequestParam(value = "workers_per_core", required = false) Integer workersPerCore,
            
            @Parameter(
                description = "안전 필터 옵션 (object | string | null)",
                schema = @Schema(oneOf = {Object.class, String.class}, nullable = true)
            )
            @RequestPart(value = "safety_filter_options", required = false) Object safetyFilterOptions,
            @Parameter(description = "정책 조회에 사용할 사용자 ID", required = false)
            @RequestParam(value = "user_id", required = false) String userId,
            @Parameter(description = "정책 조회에 사용할 프로젝트명 (GPO_PRJ_NM)", required = false)
            @RequestParam(value = "project_name", required = false) String projectName) {

        AgentDeployRes deployRes = agentDeployService.addCustomDeploymentWithMultipart(
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
                userId,
                projectName);

        return AxResponseEntity.created(deployRes, "커스텀 배포가 성공적으로 추가되었습니다.");
    }

    /**
     * Agent 배포 정보 수정
     * 
     * @param deployId 배포 ID
     * @param request Agent 배포 수정 요청
     * @return 수정된 배포 정보
     */
    @PutMapping("/app/{appId}")
    @Operation(
        summary = "Agent 배포 정보 수정",
        description = "기존 Agent 배포의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> updateAgentApp(
            @PathVariable("appId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String appId,
            @RequestBody AppUpdateReq request) {
        
        agentDeployService.updateAgentApp(appId, request);

        return AxResponseEntity.ok(null, "Agent 배포 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * Agent 배포 삭제
     * 
     * @param deployId 배포 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/app/{appId}")
    @Operation(
        summary = "Agent 배포 삭제",
        description = "특정 Agent 배포를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteAgentApp(
            @PathVariable("appId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String appId) {

        agentDeployService.deleteAgentApp(appId);

        return AxResponseEntity.ok(null, "Agent 배포 정보가 성공적으로 삭제되었습니다.");
    }

    /**
     * Agent App API 키 목록 조회
     * 
     * @param appId 앱 ID
     * @return API 키 목록을 포함한 배포 정보
     */
    @Hidden
    @GetMapping("/app/{appId}/apiKeys")
    @Operation(
        summary = "Agent App API 키 목록 조회",
        description = "특정 앱의 API 키 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "앱을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AppApiKeysRes> getAgentAppApiKeyListById(
            @PathVariable("appId") 
            @Parameter(description = "앱 ID") String appId) {

        AppApiKeysRes agentApiKeysRes = agentDeployService.getAgentAppApiKeyListById(appId);

        return AxResponseEntity.ok(agentApiKeysRes, "Agent App API 키 목록을 성공적으로 조회했습니다.");
    }

    /**
     * Agent App API 키 발급
     * 
     * @param appId 앱 ID
     * @return 생성된 API 키 정보
     */
    @Hidden
    @PostMapping("/app/{appId}/apiKeys")
    @Operation(
        summary = "Agent App API 키 발급",
        description = "특정 앱에 대한 새로운 API 키를 발급합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "API 키 발급 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "앱을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AppApiKeyCreateRes> createAgentAppApiKey(
            @PathVariable("appId") 
            @Parameter(description = "앱 ID", example = "d0416e75-14c0-411a-a975-e4d3f457d8c1") String appId) {

        AppApiKeyCreateRes appApiKeyCreateRes = agentDeployService.createAgentAppApiKey(appId);

        return AxResponseEntity.created(appApiKeyCreateRes, "Agent App API 키가 성공적으로 발급되었습니다.");
    }

    /**
     * Agent 배포 버전 삭제
     * 
     * @param deployId 배포 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/app/deploy/{deployId}")
    @Operation(
        summary = "Agent 배포 삭제",
        description = "특정 Agent 배포를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteAgentAppDeploy(
            @PathVariable("deployId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String deployId) {

        agentDeployService.deleteAgentAppDeploy(deployId);

        return AxResponseEntity.ok(null, "Agent 배포가 성공적으로 삭제되었습니다.");
    }

    /**
     * Agent 배포 버전 중지
     * 
     * @param deployId 배포 ID
     * @return 중지된 배포 정보
     */
    @PostMapping("/app/deploy/{deployId}/stop")
    @Operation(
        summary = "Agent 배포 중지",
        description = "특정 Agent 배포를 중지합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 중지 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentDeployUpdateOrDeleteRes> stopAgentDeploy(
            @PathVariable("deployId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String deployId) {

        AgentDeployUpdateOrDeleteRes agentDeployRes = agentDeployService.stopAgentDeploy(deployId);

        return AxResponseEntity.ok(agentDeployRes, "Agent 배포가 성공적으로 중지되었습니다.");
    }

    /**
     * Agent 배포 버전 재시작
     * 
     * @param deployId 배포 ID
     * @return 재시작된 배포 정보
     */
    @PostMapping("/app/deploy/{deployId}/restart")
    @Operation(
        summary = "Agent 배포 재시작",
        description = "특정 Agent 배포를 재시작합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 재시작 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentDeployUpdateOrDeleteRes> restartAgentDeploy(
            @PathVariable("deployId") 
            @Parameter(description = "배포 ID", example = "deploy-12345") String deployId) {
         
        AgentDeployUpdateOrDeleteRes agentDeployRes = agentDeployService.restartAgentDeploy(deployId);

        return AxResponseEntity.ok(agentDeployRes, "Agent 배포가 성공적으로 재시작되었습니다.");
    }

    /**
     * Agent 서빙 상세 조회
     * 
     * @param agentServingId 에이전트 서빙 ID
     * @return 에이전트 서빙 상세 정보
     */
    @GetMapping("/app/serving/{agentServingId}")
    @Operation(
        summary = "Agent 서빙 상세 조회",
        description = "특정 Agent 서빙의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 서빙 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 서빙을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentServingRes> getAgentServing(
            @PathVariable("agentServingId") 
            @Parameter(description = "에이전트 서빙 ID", example = "srv-12345") String agentServingId) {
 
        AgentServingRes agentServingRes = agentDeployService.getAgentServing(agentServingId);

        return AxResponseEntity.ok(agentServingRes, "Agent 서빙 상세 정보를 성공적으로 조회했습니다.");
    }

    /**
     * Agent 배포 버전 스트리밍
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 스트리밍 추론 요청 정보
     * @return 스트리밍 추론 실행 결과
     */
    @PostMapping(value = "/app/{deployId}/stream", 
                 produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Agent 스트리밍 추론", description = "Agent Gateway를 통해 스트리밍 추론을 실행합니다.")
    public ResponseEntity<String> getStreamAgent(
        @PathVariable("deployId") @Parameter(description = "배포 ID", example = "074c42fe-04ae-41a2-94f6-a3b6f722061e") String deployId,
        @RequestParam(value = "routerPath", required = false) @Parameter(description = "라우터 경로", example = "") String routerPath,
        @RequestBody @Valid StreamReq request,
        @RequestParam("authorization") @Parameter(description = "인증 토큰", example = "sk-853133bfec004bf9b39405f37f9dfcf6") String authorization) {
    
        // Raw SSE 응답을 그대로 반환
        String rawSSEResponse = agentDeployService.getStreamAgentRaw(deployId, routerPath, request, authorization);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(rawSSEResponse);
    }

    @GetMapping("/app/cluster/resources")
    @Operation(
        summary = "Agent 클러스터 리소스 조회",
        description = "Agent 클러스터 리소스를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 클러스터 리소스 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
     public AxResponseEntity<Map<String, Object>> getClusterResources(
        @Parameter(description = "노드 타입", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @RequestParam("nodeType") String nodeType
    ){
        Map<String, Object> agentClusterResources = agentDeployService.getClusterResources(nodeType);
        
        return AxResponseEntity.ok(agentClusterResources, "Agent 클러스터 리소스 조회가 성공적으로 완료되었습니다.");
    }

    /**
     * Elasticsearch 시스템 로그 검색
     * 
     * @param index 인덱스명
     * @param request 검색 요청
     * @return 검색 결과 (로그 문자열)
     */
    @PostMapping("/elastic/{index}/_search")
    @Operation(
        summary = "Elasticsearch 시스템 로그 검색",
        description = "Elasticsearch를 통해 Agent 시스템 로그를 검색합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그 검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<String> getAgentSysLog(
            @PathVariable("index") 
            @Parameter(description = "Elasticsearch 인덱스명", example = "app-log-2025.10.14") String index,
            @RequestBody @Valid AgentSysLogSearchReq request) {
        
        String logResult = agentDeployService.getAgentSysLog(index, request);
        
        return AxResponseEntity.ok(logResult, "시스템 로그 검색이 성공적으로 완료되었습니다.");
    }

    /**
     * Agent 배포 정보 조회
     * 
     * @param agentId Agent ID
     * @return Agent 배포 정보
     */
    @GetMapping("/app/{agentId}/info")
    @Operation(
        summary = "Agent 배포 정보 조회",
        description = "특정 Agent 배포의 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Agent 배포를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentDeployInfoRes> getAgentDeployInfo(
            @PathVariable("agentId") 
            @Parameter(description = "Agent ID", example = "agent-12345") String agentId) {

        AgentDeployInfoRes agentDeployInfoRes = agentDeployService.getAgentDeployInfo(agentId);

        return AxResponseEntity.ok(agentDeployInfoRes, "Agent 배포 정보를 성공적으로 조회했습니다.");
    }

    @PostMapping("/app/{appId}/policy")
    @Operation(
        summary = "Agent 배포 Policy 설정",
        description = "Agent 배포의 Policy를 설정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent 배포 Policy 설정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<PolicyRequest>> setAgentDeployPolicy(
            @PathVariable("appId") 
            @Parameter(description = "App ID", example = "app-12345") String appId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        List<PolicyRequest> policy = agentDeployService.setAgentDeployPolicy(appId, memberId, projectName);
        return AxResponseEntity.ok(policy, "Agent 배포 Policy를 성공적으로 설정했습니다.");
    }
}
