package com.skax.aiplatform.service.admin;

import com.skax.aiplatform.dto.admin.request.ProjectSearchReq;
import com.skax.aiplatform.dto.admin.request.UserAssignableRoleSearchReq;
import com.skax.aiplatform.dto.admin.request.UserSearchReq;
import com.skax.aiplatform.dto.admin.response.RoleRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRoleRes;
import com.skax.aiplatform.dto.admin.response.UserRes;
import org.springframework.data.domain.Page;

/**
 * 사용자 관리 인터페이스
 */
public interface UserMgmtService {

    /**
     * 사용자 전체 조회
     */
    Page<UserRes> getUsers(UserSearchReq searchReq);

    /**
     * 사용자 상세 조회
     */
    UserRes getUserById(String memberId);

    /**
     * 사용자가 참여한 프로젝트 조회
     */
    Page<UserProjectRes> getUserProjects(String memberId, ProjectSearchReq searchReq);

    /**
     * 사용자가 참여한 프로젝트의 상세 정보 및 역할 조회
     */
    UserProjectRoleRes getUserProjectDetail(String userId, String projectId);

    /**
     * 프로젝트의 할당 가능한 역할 목록 조회
     */
    Page<RoleRes> getAssignableProjectRoles(String projectId, UserAssignableRoleSearchReq searchReq);

    /**
     * 사용자가 참여한 프로젝트내 역할 수정
     */
    void updateUserRole(String userId, String projectId, String uuid);

    /**
     * 사용자 계정 상태 수정
     */
    void activateUserStatus(String uuid);

}
