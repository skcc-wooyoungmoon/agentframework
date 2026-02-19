package com.skax.aiplatform.service.admin.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.EnumUtils;
import com.skax.aiplatform.dto.admin.request.ProjectSearchReq;
import com.skax.aiplatform.dto.admin.request.UserAssignableRoleSearchReq;
import com.skax.aiplatform.dto.admin.request.UserSearchReq;
import com.skax.aiplatform.dto.admin.response.RoleRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRes;
import com.skax.aiplatform.dto.admin.response.UserProjectRoleRes;
import com.skax.aiplatform.dto.admin.response.UserRes;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.role.RoleStatus;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.entity.user.User;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.admin.RoleRepository;
import com.skax.aiplatform.repository.admin.UserMgmtRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.admin.UserMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMgmtServiceImpl implements UserMgmtService {

    private static final Long PORTAL_ADMIN_ROLE_SEQ = -199L;

    private final UserMgmtRepository userMgmtRepository;
    private final RoleRepository roleRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;

    private final AdminAuthService adminAuthService;

    /**
     * 사용자 전체 조회
     */
    @Override
    public Page<UserRes> getUsers(UserSearchReq searchReq) {
        Page<User> usersPage = userMgmtRepository.findUsersBySearch(
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                searchReq.getRetrJkwYn(),
                EnumUtils.valueOf(searchReq.getDmcStatus(), DormantStatus.class)
        );

        return usersPage.map(UserRes::of);
    }

    /**
     * 사용자 상세 조회
     */
    @Override
    public UserRes getUserById(String userId) {
        User user = userMgmtRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserRes.of(user);
    }

    /**
     * 사용자가 참여한 프로젝트 조회
     */
    @Override
    public Page<UserProjectRes> getUserProjects(String memberID, ProjectSearchReq searchReq) {
        Page<Project> userProjects = userMgmtRepository.findUserProjectsBySearch(
                memberID,
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                ProjectStatus.ONGOING
        );

        return userProjects.map(UserProjectRes::from);
    }

    /**
     * 사용자가 참여한 프로젝트의 상세 정보 및 역할 조회
     */
    @Override
    public UserProjectRoleRes getUserProjectDetail(String userId, String projectId) {
        log.info("사용자가 참여한 프로젝트의 상세 정보 및 역할 조회: userId={}, projectId={}", userId, projectId);

        ProjectUserRole projectUserRole = userMgmtRepository.findUserProjectDetail(userId, projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROJECT_NOT_FOUND));

        // updatedBy 사용자 정보 조회
        User updatedByUser = null;

        if (projectUserRole.getUpdatedBy() != null) {
            updatedByUser = userMgmtRepository.findById(projectUserRole.getUpdatedBy()).orElse(null);
        }

        return UserProjectRoleRes.of(projectUserRole, updatedByUser);
    }

    /**
     * 프로젝트의 할당 가능한 역할 목록 조회
     */
    @Override
    public Page<RoleRes> getAssignableProjectRoles(String projectId, UserAssignableRoleSearchReq searchReq) {
        log.info(">> 서비스 << 프로젝트 할당 가능한 역할 목록 조회 시작.  프로젝트 아이디={} 요청 정보={}", projectId, searchReq);

        // todo 포탈 관리자, 인프라 관리자, 일반 사용자, 포탈 단위 커스텀 역할 제거
        Page<Role> assignableRoles = userMgmtRepository.findAssignableProjectRoles(
                projectId,
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                RoleStatus.ACTIVE
        );

        return assignableRoles.map(RoleRes::of);
    }

    /**
     * 사용자가 참여한 프로젝트내 역할 수정
     */
    @Override
    @Transactional
    public void updateUserRole(String userId, String projectId, String uuid) {
        log.info("사용자 역할 수정 시작: userId={}, projectId={}, role uuid={}", userId, projectId, uuid);

        ProjectUserRole projectUserRole = userMgmtRepository.findUserProjectDetail(userId, projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROJECT_NOT_FOUND));
        Role newRole = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        // 포탈관리자에서 -> 다른 역할로 변경 시 검증
        //  ㄴ 포탈 관리자는 최소 한명은 있어야 한다.
        if (PORTAL_ADMIN_ROLE_SEQ.equals(projectUserRole.getRole().getRoleSeq())) {
            List<ProjectUserRole> portalAdminRoles = projectUserRoleRepository.findByRoleRoleSeq(PORTAL_ADMIN_ROLE_SEQ);

            if (portalAdminRoles.size() <= 1) {
                throw new BusinessException(ErrorCode.LAST_PORTAL_ADMIN_CANNOT_CHANGE_ROLE);
            }
        }

        // ADXP 그룹 권한 변경 (베스트 에포트)
        Role oldRole = projectUserRole.getRole();
        Long prjSeq = projectUserRole.getProject() != null ? projectUserRole.getProject().getPrjSeq() : null;
        String memberId = projectUserRole.getUser() != null ? projectUserRole.getUser().getMemberId() : null;

        boolean roleChanged = (oldRole == null) || projectUserRole.isDifferentRole(newRole);

        // DB 역할 업데이트
        projectUserRole.updateRole(newRole);

        // 이전 역할 그룹 권한 제거
        if (roleChanged && prjSeq != null && memberId != null && oldRole != null) {
            String oldGroupName = "P" + prjSeq + "_R" + oldRole.getRoleSeq();
            try {
                adminAuthService.unassignUserFromGroup(memberId, oldGroupName);
                log.info("ADXP 그룹 권한 제거 완료: memberId={}, groupName={}", memberId, oldGroupName);
            } catch (BusinessException ex) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getMessage());
            } catch (Exception ex) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getMessage());
            }
        }

        // 신규 역할 그룹 권한 부여
        if (roleChanged && prjSeq != null && memberId != null && newRole != null) {
            String newGroupName = "P" + prjSeq + "_R" + newRole.getRoleSeq();
            try {
                adminAuthService.assignUserToGroup(memberId, newGroupName);
                log.info("ADXP 그룹 권한 부여 완료: memberId={}, groupName={}", memberId, newGroupName);
            } catch (BusinessException ex) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getMessage());
            } catch (Exception ex) {
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getMessage());
            }
        }

        log.info("사용자 역할 수정 완료: userId={}, projectId={}, newRoleUuid={}", userId, projectId, uuid);
    }

    /**
     * 사용자 계정 활성화
     */
    @Override
    @Transactional
    public void activateUserStatus(String userId) {
        log.info("사용자 계정 활성화 시작: memberId={}", userId);

        User user = userMgmtRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.activateStatus();

        log.info("사용자 계정 활성화 완료: memberId={}", userId);
    }

}
