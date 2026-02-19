package com.skax.aiplatform.client.sktai.auth;

import com.skax.aiplatform.client.sktai.auth.dto.request.CreateClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.CreateProjectRole;
import com.skax.aiplatform.client.sktai.auth.dto.request.UpdateClient;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientsRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.CreatedClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ProjectRoleMappingsRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleBase;
import com.skax.aiplatform.client.sktai.auth.dto.response.UsersRead;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SKTAI 프로젝트 관리 API Feign Client
 * 
 * <p>SKTAI Auth API의 프로젝트 관리 기능을 위한 Feign Client입니다.
 * 프로젝트 CRUD, 프로젝트 역할 관리, 사용자-역할 매핑 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>프로젝트 관리</strong>: 프로젝트 생성, 조회, 수정, 삭제</li>
 *   <li><strong>역할 관리</strong>: 프로젝트별 역할 생성 및 관리</li>
 *   <li><strong>사용자 매핑</strong>: 프로젝트-역할-사용자 간의 매핑 관리</li>
 *   <li><strong>페이징 지원</strong>: 대용량 데이터에 대한 효율적인 조회</li>
 * </ul>
 * 
 * <h3>인증 요구사항:</h3>
 * <p>모든 API 호출에는 OAuth2 Bearer 토큰이 필요합니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 프로젝트 목록 조회
 * ClientsRead projects = sktaiProjectClient.getProjects(1, 10, null, null, null);
 * 
 * // 새 프로젝트 생성
 * CreateClient request = CreateClient.builder()
 *     .project(CreateProject.builder().name("MyProject").build())
 *     .namespace(CreateNamespace.builder()
 *         .cpuQuota(2.0)
 *         .memQuota(4.0)
 *         .gpuQuota(1.0)
 *         .build())
 *     .build();
 * CreatedClientRead result = sktaiProjectClient.createProject(request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiClientConfig Feign Client 설정
 * @see CreateClient 프로젝트 생성 요청
 * @see ClientsRead 프로젝트 목록 응답
 */
@FeignClient(
    name = "sktai-project-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Projects", description = "SKTAI 프로젝트 관리 API")
public interface SktaiProjectClient {
    
    /**
     * 새 프로젝트 생성
     * 
     * <p>새로운 프로젝트와 관련 네임스페이스를 생성합니다.
     * 프로젝트는 사용자 및 리소스 관리의 기본 단위입니다.</p>
     * 
     * @param request 프로젝트 생성 요청 (프로젝트 정보 + 네임스페이스 설정)
     * @return 생성된 프로젝트의 상세 정보
     * 
     * @apiNote 관리자 권한이 필요합니다.
     * @implNote 네임스페이스는 프로젝트와 함께 자동으로 생성됩니다.
     */
    @PostMapping("/api/v1/projects")
    @Operation(
        summary = "프로젝트 생성",
        description = "새로운 프로젝트와 관련 네임스페이스를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "프로젝트 생성 성공",
            content = @Content(schema = @Schema(implementation = CreatedClientRead.class))
        ),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    CreatedClientRead createProject(
        @RequestBody 
        @Schema(description = "프로젝트 생성 요청 정보")
        CreateClient request
    );
    
    /**
     * 프로젝트 목록 조회
     * 
     * <p>사용자가 접근 가능한 프로젝트 목록을 페이징하여 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건 (예: "name", "created_at desc")
     * @param filter 필터 조건
     * @param search 검색어 (프로젝트명 기준)
     * @return 페이징된 프로젝트 목록
     * 
     * @implNote 사용자의 권한에 따라 조회 가능한 프로젝트가 제한됩니다.
     */
    @GetMapping("/api/v1/projects")
    @Operation(
        summary = "프로젝트 목록 조회",
        description = "사용자가 접근 가능한 프로젝트 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "프로젝트 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ClientsRead.class))
        ),
        @ApiResponse(responseCode = "422", description = "요청 파라미터 오류")
    })
    ClientsRead getProjects(
        @RequestParam(value = "page", defaultValue = "1")
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer page,
        
        @RequestParam(value = "size", defaultValue = "10")
        @Parameter(description = "페이지당 항목 수", example = "10")
        Integer size,
        
        @RequestParam(value = "sort", required = false)
        @Parameter(description = "정렬 조건 (예: name, created_at desc)")
        String sort,
        
        @RequestParam(value = "filter", required = false)
        @Parameter(description = "필터 조건")
        String filter,
        
        @RequestParam(value = "search", required = false)
        @Parameter(description = "검색어 (프로젝트명 기준)")
        String search
    );
    
    /**
     * 특정 프로젝트 상세 조회
     * 
     * <p>프로젝트 ID를 통해 특정 프로젝트의 상세 정보를 조회합니다.
     * 네임스페이스 정보도 함께 반환됩니다.</p>
     * 
     * @param projectId 조회할 프로젝트의 고유 ID
     * @return 프로젝트 상세 정보
     * 
     * @apiNote 해당 프로젝트에 대한 읽기 권한이 필요합니다.
     */
    @GetMapping("/api/v1/projects/{project_id}")
    @Operation(
        summary = "프로젝트 상세 조회",
        description = "프로젝트 ID를 통해 특정 프로젝트의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "프로젝트 조회 성공",
            content = @Content(schema = @Schema(implementation = ClientRead.class))
        ),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "잘못된 프로젝트 ID")
    })
    ClientRead getProject(
        @PathVariable("project_id")
        @Parameter(description = "프로젝트 고유 ID", required = true, example = "proj-123")
        String projectId
    );
    
    /**
     * 프로젝트 정보 수정
     * 
     * <p>기존 프로젝트의 정보를 수정합니다.
     * 프로젝트명과 네임스페이스 리소스 할당량을 변경할 수 있습니다.</p>
     * 
     * @param projectId 수정할 프로젝트의 고유 ID
     * @param request 프로젝트 수정 요청 정보
     * @return 수정된 프로젝트 정보
     * 
     * @apiNote 프로젝트 관리자 권한이 필요합니다.
     */
    @PutMapping("/api/v1/projects/{project_id}")
    @Operation(
        summary = "프로젝트 정보 수정",
        description = "기존 프로젝트의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "프로젝트 수정 성공",
            content = @Content(schema = @Schema(implementation = CreatedClientRead.class))
        ),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    CreatedClientRead updateProject(
        @PathVariable("project_id")
        @Parameter(description = "프로젝트 고유 ID", required = true, example = "proj-123")
        String projectId,
        
        @RequestBody
        @Schema(description = "프로젝트 수정 요청 정보")
        UpdateClient request
    );
    
    /**
     * 프로젝트 삭제
     * 
     * <p>지정된 프로젝트를 완전히 삭제합니다.
     * 관련된 모든 리소스와 데이터가 함께 삭제됩니다.</p>
     * 
     * @param projectId 삭제할 프로젝트의 고유 ID
     * 
     * @apiNote 프로젝트 소유자 권한이 필요합니다.
     * @implNote 삭제된 프로젝트는 복구할 수 없습니다.
     */
    @DeleteMapping("/api/v1/projects/{project_id}")
    @Operation(
        summary = "프로젝트 삭제",
        description = "지정된 프로젝트를 완전히 삭제합니다. 관련된 모든 리소스와 데이터가 함께 삭제됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "잘못된 프로젝트 ID")
    })
    void deleteProject(
        @PathVariable("project_id")
        @Parameter(description = "프로젝트 고유 ID", required = true, example = "proj-123")
        String projectId
    );
    
    /**
     * 프로젝트 역할 목록 조회
     * 
     * <p>특정 프로젝트에서 사용 가능한 역할 목록을 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원합니다.</p>
     * 
     * @param clientId 프로젝트(클라이언트) ID
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어 (역할명 기준)
     * @return 페이징된 역할 목록
     */
    @GetMapping("/api/v1/projects/{client_id}/roles")
    @Operation(
        summary = "프로젝트 역할 목록 조회",
        description = "특정 프로젝트에서 사용 가능한 역할 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "역할 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ProjectRoleMappingsRead.class))
        ),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "요청 파라미터 오류")
    })
    ProjectRoleMappingsRead getProjectRoles(
        @PathVariable("client_id")
        @Parameter(description = "프로젝트(클라이언트) ID", required = true, example = "proj-123")
        String clientId,
        
        @RequestParam(value = "page", defaultValue = "1")
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer page,
        
        @RequestParam(value = "size", defaultValue = "10")
        @Parameter(description = "페이지당 항목 수", example = "10")
        Integer size,
        
        @RequestParam(value = "sort", required = false)
        @Parameter(description = "정렬 조건")
        String sort,
        
        @RequestParam(value = "filter", required = false)
        @Parameter(description = "필터 조건")
        String filter,
        
        @RequestParam(value = "search", required = false)
        @Parameter(description = "검색어 (역할명 기준)")
        String search
    );
    
    /**
     * 프로젝트 역할 생성
     * 
     * <p>특정 프로젝트에 새로운 역할을 생성합니다.
     * 생성된 역할은 해당 프로젝트 내에서만 유효합니다.</p>
     * 
     * @param clientId 프로젝트(클라이언트) ID
     * @param request 역할 생성 요청 정보
     * @return 생성된 역할 정보
     */
    @PostMapping("/api/v1/projects/{client_id}/roles")
    @Operation(
        summary = "프로젝트 역할 생성",
        description = "특정 프로젝트에 새로운 역할을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "역할 생성 성공",
            content = @Content(schema = @Schema(implementation = RoleBase.class))
        ),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    RoleBase createProjectRole(
        @PathVariable("client_id")
        @Parameter(description = "프로젝트(클라이언트) ID", required = true, example = "proj-123")
        String clientId,
        
        @RequestBody
        @Schema(description = "역할 생성 요청 정보")
        CreateProjectRole request
    );
    
    /**
     * 프로젝트 역할 수정
     * 
     * <p>기존 프로젝트 역할의 설명을 수정합니다.</p>
     * 
     * @param clientId 프로젝트(클라이언트) ID
     * @param roleName 수정할 역할명
     * @param description 새로운 역할 설명
     */
    @PutMapping("/api/v1/projects/{client_id}/roles/{role_name}")
    @Operation(
        summary = "프로젝트 역할 수정",
        description = "기존 프로젝트 역할의 설명을 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "역할 수정 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    void updateProjectRole(
        @PathVariable("client_id")
        @Parameter(description = "프로젝트(클라이언트) ID", required = true, example = "proj-123")
        String clientId,
        
        @PathVariable("role_name")
        @Parameter(description = "수정할 역할명", required = true, example = "admin")
        String roleName,
        
        @RequestParam("description")
        @Parameter(description = "새로운 역할 설명", required = true, example = "관리자 역할")
        String description
    );
    
    /**
     * 프로젝트 역할 삭제
     * 
     * <p>프로젝트에서 특정 역할을 삭제합니다.
     * 해당 역할이 할당된 모든 사용자 매핑도 함께 제거됩니다.</p>
     * 
     * @param clientId 프로젝트(클라이언트) ID
     * @param roleName 삭제할 역할명
     */
    @DeleteMapping("/api/v1/projects/{client_id}/roles/{role_name}")
    @Operation(
        summary = "프로젝트 역할 삭제",
        description = "프로젝트에서 특정 역할을 삭제합니다. 해당 역할이 할당된 모든 사용자 매핑도 함께 제거됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "역할 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "잘못된 요청")
    })
    void deleteProjectRole(
        @PathVariable("client_id")
        @Parameter(description = "프로젝트(클라이언트) ID", required = true, example = "proj-123")
        String clientId,
        
        @PathVariable("role_name")
        @Parameter(description = "삭제할 역할명", required = true, example = "admin")
        String roleName
    );
    
    /**
     * 특정 역할 상세 조회
     * 
     * <p>역할 ID를 통해 특정 역할의 상세 정보를 조회합니다.</p>
     * 
     * @param roleId 조회할 역할의 고유 ID
     * @return 역할 상세 정보
     */
    @GetMapping("/api/v1/projects/roles/{role_id}")
    @Operation(
        summary = "특정 역할 상세 조회",
        description = "역할 ID를 통해 특정 역할의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "역할 조회 성공",
            content = @Content(schema = @Schema(implementation = RoleBase.class))
        ),
        @ApiResponse(responseCode = "404", description = "역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "잘못된 역할 ID")
    })
    RoleBase getRole(
        @PathVariable("role_id")
        @Parameter(description = "역할 고유 ID", required = true, example = "role-123")
        String roleId
    );
    
    /**
     * 프로젝트-역할별 사용자 목록 조회
     * 
     * <p>특정 프로젝트의 특정 역할에 할당된 사용자 목록을 조회합니다.</p>
     * 
     * @param clientId 프로젝트(클라이언트) ID
     * @param roleName 역할명
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어 (사용자명 기준)
     * @return 페이징된 사용자 목록
     */
    @GetMapping("/api/v1/projects/{client_id}/roles/{role_name}/users")
    @Operation(
        summary = "프로젝트-역할별 사용자 목록 조회",
        description = "특정 프로젝트의 특정 역할에 할당된 사용자 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UsersRead.class))
        ),
        @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "요청 파라미터 오류")
    })
    UsersRead getProjectRoleUsers(
        @PathVariable("client_id")
        @Parameter(description = "프로젝트(클라이언트) ID", required = true, example = "proj-123")
        String clientId,
        
        @PathVariable("role_name")
        @Parameter(description = "역할명", required = true, example = "admin")
        String roleName,
        
        @RequestParam(value = "page", defaultValue = "1")
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer page,
        
        @RequestParam(value = "size", defaultValue = "10")
        @Parameter(description = "페이지당 항목 수", example = "10")
        Integer size,
        
        @RequestParam(value = "sort", required = false)
        @Parameter(description = "정렬 조건")
        String sort,
        
        @RequestParam(value = "filter", required = false)
        @Parameter(description = "필터 조건")
        String filter,
        
        @RequestParam(value = "search", required = false)
        @Parameter(description = "검색어 (사용자명 기준)")
        String search
    );
}
