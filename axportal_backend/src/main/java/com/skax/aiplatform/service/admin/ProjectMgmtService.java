package com.skax.aiplatform.service.admin;

import org.springframework.data.domain.Page;

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

/**
 * 프로젝트 관리 서비스 인터페이스
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-24
 */
public interface ProjectMgmtService {

    // ================================
    // 1. 프로젝트
    // ================================

    /**
     * 전체 프로젝트 조회
     */
    Page<ProjectListRes> getProjects(ProjectSearchReq searchReq);

    /**
     * 프로젝트 상세 조회
     */
    ProjectDetailRes getProjectById(String projectId);

    /**
     * 프로젝트 수정
     */
    void updateProject(String projectId, ProjectUpdateReq updateReq);

    /**
     * 프로젝트 종료
     */
    void deleteProject(String projectId);

    // ================================
    // 2. 프로젝트 - 역할
    // ================================

    /**
     * 프로젝트 역할 목록 조회
     */
    Page<RoleRes> getProjectRoles(String projectId, RoleSearchReq searchReq);

    /**
     * 프로젝트내 역할 생성
     * @return 생성된 역할 UUID
     */
    String createProjectRole(String projectId, RoleCreateReq createReq);

    /**
     * 프로젝트 역할 다중 삭제
     */
    ProjectRoleDeleteRes deleteProjectRoles(String projectId, RoleDeleteReq deleteReq);

    /**
     * 프로젝트 역할 상세 조회
     */
    RoleDetailRes getProjectRoleDetail(String projectId, String roleId);

    /**
     * 프로젝트 역할 수정
     */
    void updateProjectRole(String projectId, String roleId, RoleUpdateReq updateReq);

    // ================================
    // 2-1. 프로젝트 - 역할 - 권한
    // ================================

    /**
     * 프로젝트 역할 권한 목록 조회
     */
    Page<RoleAuthorityRes> getProjectRoleAuthorities(String projectId, String roleId, RoleAuthoritySearchReq searchReq);

    /**
     * 프로젝트 역할 권한 수정
     */
    void updateProjectRoleAuthorities(String projectId, String roleId, RoleAuthorityUpdateReq updateReq);

    // ================================
    // 2-2. 프로젝트 - 역할 - 구성원
    // ================================

    /**
     * 프로젝트 역할에 배정된 구성원 목록 조회
     */
    Page<RoleUserRes> getProjectRoleUsers(String projectId, String roleId, ProjectRoleUserSearchReq searchReq);

    /**
     * 프로젝트 구성원 역할 변경
     */
    ProjectUserAssignRes updateProjectUserRoles(String projectId, ProjectUserRoleChangeReq request);

    // ================================
    // 3. 프로젝트 - 구성원
    // ================================

    /**
     * 프로젝트 구성원 목록 조회
     */
    Page<ProjectUserRes> getProjectUsers(String projectId, UserSearchReq searchReq);

    /**
     * 프로젝트 구성원 삭제
     */
    ProjectUserDeleteRes deleteProjectUsers(String projectId, ProjectUserDeleteReq request);

    /**
     * 구성원 초대하기 - 프로젝트에 참여하지 않은 구성원 목록 조회
     */
    Page<UserRes> getAvailableUsers(String projectId, InviteUserSearchReq searchReq);

    /**
     * 프로젝트 구성원 역할 할당
     */
    ProjectUserAssignRes assignProjectUsers(String projectId, ProjectUserAssignReq request);

    void refreshPolicyAdxpAPI();

}
