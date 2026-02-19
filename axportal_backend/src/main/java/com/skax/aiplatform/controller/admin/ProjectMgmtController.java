package com.skax.aiplatform.controller.admin;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.InviteUserSearchReq;
import com.skax.aiplatform.dto.admin.request.ProjectRoleUserSearchReq;
import com.skax.aiplatform.dto.admin.request.ProjectSearchReq;
import com.skax.aiplatform.dto.admin.request.ProjectUpdateReq;
import com.skax.aiplatform.dto.admin.request.ProjectUserAssignReq;
import com.skax.aiplatform.dto.admin.request.ProjectUserDeleteReq;
import com.skax.aiplatform.dto.admin.request.ProjectUserRoleChangeReq;
import com.skax.aiplatform.dto.admin.request.RoleAuthoritySearchReq;
import com.skax.aiplatform.dto.admin.request.RoleAuthorityUpdateReq;
import com.skax.aiplatform.dto.admin.request.RoleCreateReq;
import com.skax.aiplatform.dto.admin.request.RoleDeleteReq;
import com.skax.aiplatform.dto.admin.request.RoleSearchReq;
import com.skax.aiplatform.dto.admin.request.RoleUpdateReq;
import com.skax.aiplatform.dto.admin.request.UserSearchReq;
import com.skax.aiplatform.dto.admin.response.CreateProjectRoleRes;
import com.skax.aiplatform.dto.admin.response.ProjectDetailRes;
import com.skax.aiplatform.dto.admin.response.ProjectListRes;
import com.skax.aiplatform.dto.admin.response.ProjectRoleDeleteRes;
import com.skax.aiplatform.dto.admin.response.ProjectUserAssignRes;
import com.skax.aiplatform.dto.admin.response.ProjectUserDeleteRes;
import com.skax.aiplatform.dto.admin.response.ProjectUserRes;
import com.skax.aiplatform.dto.admin.response.RoleAuthorityRes;
import com.skax.aiplatform.dto.admin.response.RoleDetailRes;
import com.skax.aiplatform.dto.admin.response.RoleRes;
import com.skax.aiplatform.dto.admin.response.RoleUserRes;
import com.skax.aiplatform.dto.admin.response.UserRes;
import com.skax.aiplatform.service.admin.ProjectMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
@Tag(name = "프로젝트 관리 API")
public class ProjectMgmtController {

    private final ProjectMgmtService projectMgmtService;

    // ================================
    // 1. 프로젝트
    // ================================

    /**
     * 전체 프로젝트 조회 (페이징)
     *
     * @param searchReq 검색 조건
     * @return 프로젝트 목록 (페이징)
     */
    @GetMapping
    @Operation(
            summary = "전체 프로젝트 조회",
            description = "전체 프로젝트를 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 유형 (name, description)", example = "name"),
                    @Parameter(name = "keyword", description = "검색어", example = "AI 개발")
            }
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공")
    public AxResponseEntity<PageResponse<ProjectListRes>> getProjects(ProjectSearchReq searchReq) {
        log.info("[컨트롤러] 프로젝트 목록 조회 시작 : {}", searchReq);

        Page<ProjectListRes> projects = projectMgmtService.getProjects(searchReq);

        return AxResponseEntity.okPage(projects, "전체 프로젝트 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 상세 조회
     *
     * @param projectId 프로젝트 아이디
     * @return 프로젝트 정보
     */
    @GetMapping("/{project_id}")
    @Operation(
            summary = "프로젝트 상세 조회",
            description = "프로젝트 ID를 통해 프로젝트 정보를 상세 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    public AxResponseEntity<ProjectDetailRes> getProjectById(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId
    ) {
        log.info("ID로 프로젝트 상세 조회: {}", projectId);

        ProjectDetailRes project = projectMgmtService.getProjectById(projectId);

        return AxResponseEntity.ok(project, "프로젝트 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 수정
     *
     * @param projectId 프로젝트 아이디
     * @param updateReq 프로젝트 수정 정보
     * @return 수정된 프로젝트 정보
     */
    @PutMapping("/{project_id}")
    @Operation(
            summary = "프로젝트 수정",
            description = "프로젝트의 이름, 설명, 민감정보 포함 여부를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 수정 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public AxResponseEntity<Void> updateProject(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            @Valid @RequestBody ProjectUpdateReq updateReq
    ) {
        log.info("프로젝트 수정 요청. uuid: {}, updateReq: {}", projectId, updateReq);

        projectMgmtService.updateProject(projectId, updateReq);

        return AxResponseEntity.success("프로젝트가 성공적으로 수정되었습니다.");
    }

    /**
     * 프로젝트 종료
     *
     * @param projectId 프로젝트 아이디
     */
    @DeleteMapping("/{project_id}")
    @Operation(
            summary = "프로젝트 종료",
            description = "프로젝트를 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 종료 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "이미 종료된 프로젝트")
    public AxResponseEntity<Void> deleteProject(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId
    ) {
        log.info("프로젝트 종료 요청: {}", projectId);

        projectMgmtService.deleteProject(projectId);

        return AxResponseEntity.success("프로젝트가 성공적으로 종료되었습니다.");
    }

    // ================================
    // 2. 프로젝트 - 역할
    // ================================

    /**
     * 프로젝트내 역할 목록 조회
     *
     * @param projectId 프로젝트 아이디
     * @param searchReq 검색 조건 및 페이징 정보
     * @return 프로젝트에서 사용 가능한 역할 목록
     */
    @GetMapping("/{project_id}/roles")
    @Operation(
            summary = "프로젝트내 역할 목록 조회",
            description = "특정 프로젝트에서 사용 가능한 역할 목록을 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "status", description = "역할 상태 (ACTIVE, INACTIVE)", example = "ACTIVE"),
                    @Parameter(name = "type", description = "역할 유형 (DEFAULT, CUSTOM)", example = "DEFAULT"),
                    @Parameter(name = "filterType", description = "검색 유형 (name, description)", example = "name"),
                    @Parameter(name = "keyword", description = "검색어", example = "역할명")
            }
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 역할 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    public AxResponseEntity<PageResponse<RoleRes>> getProjectRoles(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            RoleSearchReq searchReq
    ) {
        log.info(">> 컨트롤러 << 프로젝트 역할 목록 조회 - [프로젝트 아이디] {}, [요청 정보] {}", projectId, searchReq);

        Page<RoleRes> projectRoles = projectMgmtService.getProjectRoles(projectId, searchReq);

        return AxResponseEntity.okPage(projectRoles, "프로젝트 역할 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트내 역할 생성
     *
     * @param projectId 프로젝트 아이디
     * @param createReq 역할 생성 요청 정보
     * @return 생성된 역할 정보
     */
    @PostMapping("/{project_id}/roles")
    @Operation(
            summary = "프로젝트 역할 생성",
            description = "특정 프로젝트 내에 새로운 역할을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "역할 생성 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public AxResponseEntity<CreateProjectRoleRes> createProjectRole(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            @Valid @RequestBody RoleCreateReq createReq
    ) {
        log.info("프로젝트 역할 생성 요청 - [프로젝트 아이디] {}, [생성 요청] {}", projectId, createReq);

        String uuid = projectMgmtService.createProjectRole(projectId, createReq);
        projectMgmtService.refreshPolicyAdxpAPI();

        return AxResponseEntity.ok(CreateProjectRoleRes.of(uuid), "프로젝트 역할이 성공적으로 생성되었습니다.");
    }

    /**
     * 프로젝트내 역할 다중 삭제
     */
    @DeleteMapping("/{project_id}/roles")
    @Operation(
            summary = "프로젝트내 역할 다중 삭제",
            description = "프로젝트 내에서 특정 역할들을 다중 삭제합니다. 기본 역할은 삭제할 수 없습니다. 요청 본문에는 역할 ID(UUID) 목록을 전달합니다."
    )
    @ApiResponse(responseCode = "200", description = "역할 삭제 처리 완료")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "기본 역할은 삭제할 수 없거나, 잘못된 요청입니다.")
    public AxResponseEntity<ProjectRoleDeleteRes> deleteProjectRoles(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            @Valid @RequestBody RoleDeleteReq deleteReq
    ) {
        log.info("프로젝트 역할 다중 삭제 요청 - [프로젝트 아이디] {}, [삭제 요청] {}", projectId, deleteReq);

        ProjectRoleDeleteRes result = projectMgmtService.deleteProjectRoles(projectId, deleteReq);
        projectMgmtService.refreshPolicyAdxpAPI();

        return AxResponseEntity.ok(result, "역할 삭제 처리가 완료되었습니다.");
    }

    /**
     * 프로젝트내 특정 역할 상세 조회
     *
     * @param projectId 프로젝트 아이디
     * @param roleId    역할 아이디
     * @return 역할 상세 정보
     */
    @GetMapping("/{project_id}/roles/{role_id}")
    @Operation(
            summary = "프로젝트내 역할 상세 조회",
            description = "특정 프로젝트의 특정 역할에 대한 상세 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "역할 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할을 찾을 수 없음")
    @ApiResponse(responseCode = "403", description = "해당 프로젝트의 역할이 아님")
    public AxResponseEntity<RoleDetailRes> getProjectRoleDetail(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            @PathVariable(name = "role_id")
            @Parameter(description = "역할 ID", example = "role_000001") String roleId
    ) {
        log.info("프로젝트 역할 상세 조회 - [프로젝트 아이디] {}, [역할 아이디] {}", projectId, roleId);

        RoleDetailRes roleDetail = projectMgmtService.getProjectRoleDetail(projectId, roleId);

        return AxResponseEntity.ok(roleDetail, "역할 상세 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트내 특정 역할 수정
     *
     * @param projectId 프로젝트 아이디
     * @param roleId    역할 아이디
     * @param updateReq 역할 수정 정보
     */
    @PutMapping("/{project_id}/roles/{role_id}")
    @Operation(
            summary = "프로젝트내 역할 수정",
            description = "특정 프로젝트의 특정 역할의 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "역할 수정 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할을 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "해당 프로젝트의 역할이 아님")
    public AxResponseEntity<Void> updateProjectRole(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project_000001") String projectId,

            @PathVariable(name = "role_id")
            @Parameter(description = "역할 ID", example = "role_000001") String roleId,

            @Valid @RequestBody RoleUpdateReq updateReq
    ) {
        log.info("프로젝트 역할 수정 - [프로젝트 아이디] {}, [역할 아이디] {}, [수정 정보] {}", projectId, roleId, updateReq);

        projectMgmtService.updateProjectRole(projectId, roleId, updateReq);

        return AxResponseEntity.success("역할이 성공적으로 수정되었습니다.");
    }

    // ================================
    // 2-1. 프로젝트 - 역할 - 권한
    // ================================

    /**
     * 프로젝트내 특정 역할에 연결된 권한 목록 조회
     */
    @GetMapping("/{project_id}/roles/{role_id}/authorities")
    @Operation(
            summary = "프로젝트 역할 권한 조회",
            description = "특정 프로젝트 역할에 연결된 권한 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "12"),
                    @Parameter(name = "filterType", description = "검색 유형 (authorityNm, dtlCtnt)", example =
                            "authorityNm"),
                    @Parameter(name = "keyword", description = "검색어", example = "데이터"),
                    @Parameter(name = "oneDepthMenu", description = "상위 메뉴명", example = "데이터 관리"),
                    @Parameter(name = "twoDepthMenu", description = "하위 메뉴명", example = "데이터 조회")
            }
    )
    @ApiResponse(responseCode = "200", description = "역할 권한 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할 정보를 찾을 수 없음")
    public AxResponseEntity<PageResponse<RoleAuthorityRes>> getProjectRoleAuthorities(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "proj-123") String projectId,

            @PathVariable(name = "role_id")
            @Parameter(description = "역할 ID", example = "role-123") String roleId,

            RoleAuthoritySearchReq searchReq
    ) {
        log.info("프로젝트 역할 권한 조회 요청 - projectId={}, roleId={}, searchReq={}", projectId, roleId, searchReq);

        Page<RoleAuthorityRes> authorities = projectMgmtService.getProjectRoleAuthorities(projectId, roleId,
                searchReq);

        return AxResponseEntity.okPage(authorities, "프로젝트 역할 권한을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 역할 권한 수정
     */
    @PutMapping("/{project_id}/roles/{role_id}/authorities")
    @Operation(
            summary = "프로젝트 역할 권한 수정",
            description = "요청된 권한 목록을 기준으로 프로젝트 역할 권한을 동기화합니다."
    )
    @ApiResponse(responseCode = "200", description = "역할 권한 수정 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할 정보를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public AxResponseEntity<Void> updateProjectRoleAuthorities(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "proj-123") String projectId,

            @PathVariable(name = "role_id")
            @Parameter(description = "역할 ID", example = "role-123") String roleId,

            @Valid @RequestBody RoleAuthorityUpdateReq updateReq
    ) {
        log.info("프로젝트 역할 권한 수정 요청 - projectId={}, roleId={}, updateReq={}", projectId, roleId, updateReq);

        projectMgmtService.updateProjectRoleAuthorities(projectId, roleId, updateReq);
        projectMgmtService.refreshPolicyAdxpAPI();

        return AxResponseEntity.success("프로젝트 역할 권한을 성공적으로 수정했습니다.");
    }

    // ================================
    // 2-2. 프로젝트 - 역할 - 구성원
    // ================================

    /**
     * 프로젝트 역할에 배정된 구성원 목록 조회
     */
    @GetMapping("/{project_id}/roles/{role_id}/users")
    @Operation(
            summary = "프로젝트 역할 구성원 조회",
            description = "특정 프로젝트의 역할에 속한 구성원 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 조건 (jkwNm, deptNm)", example = "jkwNm"),
                    @Parameter(name = "keyword", description = "검색어", example = "김신한"),
                    @Parameter(name = "dmcStatus", description = "계정 상태 (ACTIVE, DORMANT)", example = "ACTIVE"),
                    @Parameter(name = "retrJkwYn", description = "퇴직 여부 (1: 재직, 0: 퇴직)", example = "1")
            }
    )
    @ApiResponse(responseCode = "200", description = "역할 구성원 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트 또는 역할 정보를 찾을 수 없음")
    public AxResponseEntity<PageResponse<RoleUserRes>> getProjectRoleUsers(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "proj-123") String projectId,

            @PathVariable(name = "role_id")
            @Parameter(description = "역할 ID", example = "admin") String roleId,

            ProjectRoleUserSearchReq searchReq
    ) {
        log.info("프로젝트 역할 구성원 조회 요청 - projectId={}, roleId={}, searchReq={}", projectId, roleId, searchReq);

        Page<RoleUserRes> users = projectMgmtService.getProjectRoleUsers(projectId, roleId, searchReq);

        return AxResponseEntity.okPage(users, "프로젝트 역할 구성원 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 구성원 역할 변경
     */
    @PutMapping("/{project_id}/user-role-mappings")
    @Operation(
            summary = "프로젝트 구성원 역할 변경",
            description = "선택한 사용자에게 새로운 프로젝트 역할을 부여합니다."
    )
    @ApiResponse(responseCode = "200", description = "구성원 역할 변경 처리 완료")
    public AxResponseEntity<ProjectUserAssignRes> updateProjectUserRoles(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId,

            @Valid @RequestBody ProjectUserRoleChangeReq roleChangeReq
    ) {
        log.info("프로젝트 구성원 역할 변경 요청 - projectId={}, request={}", projectId, roleChangeReq);

        ProjectUserAssignRes result = projectMgmtService.updateProjectUserRoles(projectId, roleChangeReq);

        return AxResponseEntity.ok(result, "프로젝트 구성원 역할을 성공적으로 변경했습니다.");
    }

    // ================================
    // 3. 프로젝트 - 구성원
    // ================================

    /**
     * 프로젝트 구성원 목록 조회
     *
     * @param projectId 프로젝트 아이디
     * @param searchReq 검색 조건 및 페이징 정보
     * @return 프로젝트 구성원 목록
     */
    @GetMapping("/{project_id}/users")
    @Operation(
            summary = "프로젝트 구성원 조회",
            description = "특정 프로젝트에 속한 구성원 목록을 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 조건 필드명 (jkwNm, deptNm, memberId)", example =
                            "jkwNm"),
                    @Parameter(name = "keyword", description = "검색어", example = "김신한"),
                    @Parameter(name = "dmcStatus", description = "계정 상태 (ACTIVE, DORMANT)", example = "ACTIVE"),
                    @Parameter(name = "retrJkwYn", description = "퇴직 여부 (1: 재직, 0: 퇴직)", example = "1")
            }
    )
    @ApiResponse(responseCode = "200", description = "프로젝트 구성원 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    public AxResponseEntity<PageResponse<ProjectUserRes>> getProjectUsers(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId,

            UserSearchReq searchReq
    ) {
        log.info("프로젝트 구성원 조회: projectId={}, req={}", projectId, searchReq);

        Page<ProjectUserRes> projectUsers = projectMgmtService.getProjectUsers(projectId, searchReq);

        return AxResponseEntity.okPage(projectUsers, "프로젝트 구성원 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 구성원 삭제
     */
    @DeleteMapping("/{project_id}/users")
    @Operation(
            summary = "프로젝트 구성원 삭제",
            description = "프로젝트내 구성원을 삭제 합니다."
    )
    @ApiResponse(responseCode = "200", description = "구성원 삭제 처리 완료")
    public AxResponseEntity<ProjectUserDeleteRes> deleteProjectUsers(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId,

            @Valid @RequestBody ProjectUserDeleteReq request
    ) {
        log.info("프로젝트 구성원 삭제 요청 - projectId={}, request={}", projectId, request);

        ProjectUserDeleteRes result = projectMgmtService.deleteProjectUsers(projectId, request);

        return AxResponseEntity.ok(result, "프로젝트 구성원 삭제를 완료했습니다.");
    }

    /**
     * 프로젝트 구성원 초대하기 - 사용자 목록 ( 프로젝트내에 참여하지 않은 사용자 )
     *
     * @param projectId 프로젝트 아이디
     * @param searchReq 검색 조건 및 페이징 정보
     * @return 프로젝트에 참여하지 않은 사용자 목록
     */
    @GetMapping("/{project_id}/user-available")
    @Operation(
            summary = "초대 가능한 사용자 목록 조회",
            description = "특정 프로젝트에 참여하지 않은 사용자 목록을 페이징 및 검색 조건으로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "filterType", description = "검색 유형 (profile, dept, status, is_dormant ",
                            example = "profile"),
                    @Parameter(name = "keyword", description = "검색어", example = "홍길동"),
                    @Parameter(name = "status", description = "인사 상태 (EMPLOYED, RESIGNED)", example = "EMPLOYED"),
            }
    )
    @ApiResponse(responseCode = "200", description = "초대 가능한 사용자 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    public AxResponseEntity<PageResponse<UserRes>> getAvailableUsers(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId,

            InviteUserSearchReq searchReq
    ) {
        log.info("초대 가능한 사용자 목록 조회: projectId={}, 요청정보={}", projectId, searchReq);

        Page<UserRes> availableUsers = projectMgmtService.getAvailableUsers(projectId, searchReq);

        return AxResponseEntity.okPage(availableUsers, "초대 가능한 사용자 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 프로젝트 구성원 초대하기 - 프로젝트 구성원 역할 할당
     */
    @PostMapping("/{project_id}/user-role-mappings")
    @Operation(
            summary = "프로젝트 구성원 역할 할당",
            description = "선택한 사용자에게 프로젝트 역할을 할당합니다."
    )
    @ApiResponse(responseCode = "200", description = "구성원 역할 할당 처리 완료")
    public AxResponseEntity<ProjectUserAssignRes> assignProjectUsers(
            @PathVariable(name = "project_id")
            @Parameter(description = "프로젝트 ID", example = "project-000001") String projectId,

            @Valid @RequestBody ProjectUserAssignReq assignReq
    ) {
        log.info("프로젝트 구성원 역할 할당 요청 - projectId={}, request={}", projectId, assignReq);

        ProjectUserAssignRes result = projectMgmtService.assignProjectUsers(projectId, assignReq);

        return AxResponseEntity.ok(result, "프로젝트 구성원 역할을 성공적으로 처리했습니다.");
    }

}
