package com.skax.aiplatform.client.sktai.agent;

import com.skax.aiplatform.client.sktai.agent.dto.request.PermissionCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PermissionUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Agent Permissions API Feign Client
 * 
 * <p>SKTAI Agent 시스템의 권한 관리를 위한 Feign Client입니다.
 * Agent Permissions는 사용자 및 역할 기반 접근 제어를 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Permission CRUD</strong>: 권한 생성, 조회, 수정, 삭제</li>
 *   <li><strong>역할 기반 접근 제어</strong>: 사용자 역할에 따른 권한 관리</li>
 *   <li><strong>리소스 보호</strong>: API, 데이터, 기능에 대한 접근 제어</li>
 *   <li><strong>권한 상속</strong>: 계층적 권한 구조 지원</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>GET /api/v1/agent/common/Permissions</code>: 권한 목록 조회</li>
 *   <li><code>POST /api/v1/agent/common/Permissions</code>: 새 권한 생성</li>
 *   <li><code>GET /api/v1/agent/common/Permissions/{permissionId}</code>: 권한 상세 조회</li>
 *   <li><code>PUT /api/v1/agent/common/Permissions/{permissionId}</code>: 권한 수정</li>
 *   <li><code>DELETE /api/v1/agent/common/Permissions/{permissionId}</code>: 권한 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see PermissionCreateRequest 권한 생성 요청 DTO
 * @see PermissionResponse 권한 상세 응답 DTO
 */
@FeignClient(
    name = "sktai-agent-permissions-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiAgentPermissionsClient {

    /**
     * Agent Permissions 목록 조회
     * 
     * <p>등록된 Agent 권한들의 페이징된 목록을 조회합니다.
     * 필터링 및 정렬 옵션을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 옵션 (예: "created_at", "name")
     * @param filter 필터 조건
     * @param search 검색어
     * @return Agent Permissions 목록 응답
     */
    @Operation(
        summary = "Agent Permissions 목록 조회",
        description = "등록된 Agent 권한들의 페이징된 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/common/Permissions")
    PermissionsResponse getPermissions(
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
     * 새로운 Agent Permission 생성
     * 
     * <p>새로운 Agent 권한을 생성합니다.
     * 권한 이름, 설명, 허용되는 액션 등을 정의해야 합니다.</p>
     * 
     * @param request Permission 생성 요청 데이터
     * @return 생성된 Permission 정보
     */
    @Operation(
        summary = "Agent Permission 생성",
        description = "새로운 Agent 권한을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Permission 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "409", description = "Permission 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/api/v1/agent/common/Permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    PermissionCreateResponse createPermission(
        @Parameter(description = "Permission 생성 요청 데이터", required = true)
        @RequestBody PermissionCreateRequest request
    );

    /**
     * Agent Permission 상세 정보 조회
     * 
     * <p>특정 Agent 권한의 상세 정보를 조회합니다.
     * 권한 설정, 허용 액션, 적용 범위 등을 포함합니다.</p>
     * 
     * @param permissionId 조회할 Permission의 ID
     * @return Permission 상세 정보
     */
    @Operation(
        summary = "Agent Permission 상세 조회",
        description = "특정 Agent 권한의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permission 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Permission을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/common/Permissions/{permissionId}")
    PermissionResponse getPermission(
        @Parameter(description = "조회할 Permission의 ID", required = true, example = "perm-12345678")
        @PathVariable("permissionId") String permissionId
    );

    /**
     * Agent Permission 정보 수정
     * 
     * <p>기존 Agent 권한의 정보를 수정합니다.
     * 권한 설정, 허용 액션, 적용 범위 등을 업데이트할 수 있습니다.</p>
     * 
     * @param permissionId 수정할 Permission의 ID
     * @param request Permission 수정 요청 데이터
     * @return 수정 결과
     */
    @Operation(
        summary = "Agent Permission 수정",
        description = "기존 Agent 권한의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permission 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Permission을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "Permission 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping(value = "/api/v1/agent/common/Permissions/{permissionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PermissionUpdateOrDeleteResponse updatePermission(
        @Parameter(description = "수정할 Permission의 ID", required = true, example = "perm-12345678")
        @PathVariable("permissionId") String permissionId,
        
        @Parameter(description = "Permission 수정 요청 데이터", required = true)
        @RequestBody PermissionUpdateRequest request
    );

    /**
     * Agent Permission 삭제
     * 
     * <p>특정 Agent 권한을 시스템에서 삭제합니다.
     * 삭제된 Permission은 복구할 수 없습니다.</p>
     * 
     * @param permissionId 삭제할 Permission의 ID
     * @return 삭제 결과
     */
    @Operation(
        summary = "Agent Permission 삭제",
        description = "특정 Agent 권한을 시스템에서 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permission 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Permission을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "Permission이 사용 중이어서 삭제 불가"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/api/v1/agent/common/Permissions/{permissionId}")
    PermissionUpdateOrDeleteResponse deletePermission(
        @Parameter(description = "삭제할 Permission의 ID", required = true, example = "perm-12345678")
        @PathVariable("permissionId") String permissionId
    );
}
