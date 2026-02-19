package com.skax.aiplatform.service.home.impl;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupRegistResult;
import com.skax.aiplatform.client.ione.api.service.IoneApiService;
import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.home.request.ProjBaseInfoCreateReq;
import com.skax.aiplatform.dto.home.request.ProjInfoReq;
import com.skax.aiplatform.dto.home.request.ProjJoinReq;
import com.skax.aiplatform.dto.home.request.ProjectCreateReq;
import com.skax.aiplatform.dto.home.request.ProjectInfo;
import com.skax.aiplatform.dto.home.response.ProjDetailRes;
import com.skax.aiplatform.dto.home.response.ProjPrivateRes;
import com.skax.aiplatform.dto.home.response.ProjUserRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;
import com.skax.aiplatform.entity.common.approval.GpoGyljMas;
import com.skax.aiplatform.entity.common.enums.YNStatus;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.user.User;
import com.skax.aiplatform.mapper.home.ProjectMapper;
import com.skax.aiplatform.repository.admin.GpoRoleAuthMapMasRepository;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.admin.RoleRepository;
import com.skax.aiplatform.repository.common.GpoGyljCallbackMasRepository;
import com.skax.aiplatform.repository.common.GpoGyljMasRepository;
import com.skax.aiplatform.repository.home.GpoAlarmsRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.repository.home.GpoRolesRepository;
import com.skax.aiplatform.repository.home.GpoUsersRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.home.ProjectService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service("ProjectService")
public class ProjectServiceImpl implements ProjectService {

    private static final long PUBLIC_PROJECT_SEQ = -999L; // 공개 프로젝트 고정 SEQ
    private static final long PROJECT_ADMIN_ROLE_SEQ = -299L; // 프로젝트 관리자 역할 고정 SEQ
    private static final long PORTAL_ADMIN_ROLE_SEQ = -199L; // 포탈 관리자 역할 고정 SEQ

    private final GpoUsersRepository gpoUsersRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final GpoProjectsRepository gpoProjectsRepository;
    private final GpoRolesRepository gpoRolesRepository;

    private final ProjectUserRoleRepository projectUserRoleRepository;
    private final GpoRoleAuthMapMasRepository gpoRoleAuthMapMasRepository;
    private final ProjectMgmtRepository projectMgmtRepository;
    private final RoleRepository roleRepository;
    private final GpoGyljMasRepository gpoGyljMasRepository;
    private final GpoGyljCallbackMasRepository gpoGyljCallbackMasRepository;

    private final ProjectMapper projectMapper;

    private final AdminAuthService adminAuthService;
    private final IoneApiService ioneApiService;
    private final TokenInfo tokenInfo;
    private final GpoAlarmsRepository gpoAlarmsRepository;

    @Override
    @Transactional
    public ProjBaseInfoCreateReq createProject(ProjBaseInfoCreateReq projBaseInfoCreateReq) {
        log.info("createProject: {}", projBaseInfoCreateReq.toString());

        // 동일한 프로젝트명이 존재하는지 검증
        validateProjectNameNotDuplicate(projBaseInfoCreateReq.getName());

        String projectId = UUID.randomUUID().toString();
        projBaseInfoCreateReq.setProjectId(projectId);

        Project project = Project.builder()
                .uuid(projectId)
                .prjNm(projBaseInfoCreateReq.getName())
                .dtlCtnt(projBaseInfoCreateReq.getDescription())
                .sstvInfInclYn(YNStatus.valueOf(projBaseInfoCreateReq.getIs_sensitive()))
                .sstvInfInclDesc(projBaseInfoCreateReq.getSensitive_reason())
                // 1. 포탈관리자는 프로젝트 즉시 생성
                // 2. 포탈관리자 이외의 경우 REQUESTED 로 생성 후 결재완료 시 ONGOING 으로 변경
                .statusNm("Y".equals(projBaseInfoCreateReq.getIs_portal_admin()) ? ProjectStatus.ONGOING :
                        ProjectStatus.REQUESTED) // ProjectStatus.REQUESTED 하고 / 승인시 ONGOING
                .build();
        Project savedProject = gpoProjectsRepository.save(project);
        log.debug("Project created: {}", savedProject);

        projBaseInfoCreateReq.setPrjSeq(savedProject.getPrjSeq());

        // ADXP 그룹 생성 (기본 역할 3개, 관리자/개발자/테스터)
        String projectSeq = String.valueOf(savedProject.getPrjSeq());
        String adminGroupNm = "P%s_R-299".formatted(projectSeq); // -299:프로젝트관리자 고유 seq
        String devGroupNm = "P%s_R-298".formatted(projectSeq); // -298:개발자 고유 seq
        String testGroupNm = "P%s_R-297".formatted(projectSeq); // -297:테스터 고유 seq

        adminAuthService.createGroup(adminGroupNm); // 관리자 그룹
        adminAuthService.createGroup(devGroupNm); // 개발자 그룹
        adminAuthService.createGroup(testGroupNm); // 테스터 그룹

        User user = gpoUsersRepository.findById(projBaseInfoCreateReq.getUsername()).orElse(null);
        Role adminRole = gpoRolesRepository.findById(-299L).orElse(null); // -299:프로젝트관리자 고유 seq

        ProjectUserRole newRow = ProjectUserRole.builder()
                .statusNm(ProjectUserRoleStatus.INACTIVE)
                .user(user)
                .project(project)
                .role(adminRole)
                .build();
        gpoPrjuserroleRepository.save(newRow);

        // 프로젝트 생성자에게 ADXP 관리자 권한 부여
        adminAuthService.assignUserToGroup(projBaseInfoCreateReq.getUsername(), adminGroupNm);

        // 포탈 관리자 목록 조회 (prjSeq = -999, roleSeq = -199)
        List<String> portalAdminIds = gpoPrjuserroleRepository.findMemberIdsByPrjSeqAndRoleSeq(-999L, -199L)
                .stream()
                .filter(id -> !id.equals(projBaseInfoCreateReq.getUsername()))
                .toList();
        log.info("Portal admin count: {}", portalAdminIds != null ? portalAdminIds.size() : 0);

        /* 포탈관리자에게 신규 프로젝트의 프로젝트관리자(-299) 역할 부여 및 ADXP 그룹 추가 */
        /* 25.12.18 포탈관리자에게 프로젝트관리자 역할 부여하는 코드 제거 (현업 요구사항) */
        // if (portalAdminIds != null) {
        //     for (String portalAdminId : portalAdminIds) {
        //         try {
        //             // 사용자 조회
        //             User portalAdmin = gpoUsersRepository.findById(portalAdminId).orElse(null);
        //
        //             if (portalAdmin == null) {
        //                 log.warn("Portal admin user not found: {}", portalAdminId);
        //                 continue;
        //             }
        //
        //             gpoPrjuserroleRepository.save(ProjectUserRole.builder()
        //                     .statusNm(ProjectUserRoleStatus.INACTIVE)
        //                     .user(portalAdmin)
        //                     .project(project)
        //                     .role(adminRole)
        //                     .build());
        //
        //             log.debug("Created admin mapping for portal admin: {} in project: {}", portalAdminId,
        //                     savedProject.getPrjSeq());
        //
        //             // ADXP 그룹에 사용자 추가
        //             adminAuthService.assignUserToGroup(portalAdminId, adminGroupNm);
        //         } catch (BusinessException e) {
        //             // 비즈니스 예외는 경고만 기록 (일부 실패해도 계속 진행)
        //             log.warn("Failed to grant project admin to portal admin (BusinessException): {} for project: {}, " +
        //                             "error: {}",
        //                     portalAdminId, savedProject.getPrjSeq(), e.getMessage());
        //         } catch (DataAccessException e) {
        //             // 데이터베이스 접근 오류는 경고만 기록
        //             log.warn("Failed to grant project admin to portal admin (데이터베이스 오류): {} for project: {}, error: {}",
        //                     portalAdminId, savedProject.getPrjSeq(), e.getMessage());
        //         } catch (IllegalArgumentException | NullPointerException e) {
        //             // 잘못된 인자나 null 참조 예외는 경고만 기록
        //             log.warn("Failed to grant project admin to portal admin (잘못된 인자): {} for project: {}, error: {}",
        //                     portalAdminId, savedProject.getPrjSeq(), e.getMessage());
        //         } catch (Exception e) {
        //             // 기타 예상치 못한 예외
        //             log.error("Failed to grant project admin to portal admin (예상치 못한 오류): {} for project: {}",
        //                     portalAdminId, savedProject.getPrjSeq(), e);
        //         }
        //     }
        // }

        /**
         * 1. project_user 의 유저로 입력 함
         * 2. project_role 의 역할을 입력 함
         * 3. 프로젝트 관리자 에 대한 정보는 미정
         */
        Role testRole = gpoRolesRepository.findById(-297L).orElse(null); // -297:테스터 고유 seq
        Object[] members = projBaseInfoCreateReq.getMember_ids();

        if (members != null) {
            for (Object memberObj : members) {
                log.debug("1 members >>> : {}", members);

                if (!(memberObj instanceof Map)) {
                    if (memberObj instanceof String) {
                        Map<String, Object> memberMap = new HashMap<>();
                        memberMap.put("memberId", memberObj);
                        memberObj = memberMap;
                    } else {
                        continue;
                    }
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> memberMap = (Map<String, Object>) memberObj;
                log.debug("2 memberMap >>> : {}", memberMap.toString());
                // id 값만 추출
                String memberId = (String) memberMap.get("memberId");
                User dev = gpoUsersRepository.findById(memberId).orElse(null);

                ProjectUserRole newDev = ProjectUserRole.builder()
                        .statusNm(ProjectUserRoleStatus.INACTIVE)
                        .user(dev)
                        .project(project)
                        .role(testRole)
                        .build();
                gpoPrjuserroleRepository.save(newDev);

                // 프로젝트 참여자에게 ADXP 테스터 권한 부여
                adminAuthService.assignUserToGroup(dev.getMemberId(), testGroupNm);
            }
        }

        // API GW 업무 코드 등록
        try {
            WorkGroupRegistResult workGroupRegistResult =
                    ioneApiService.registWorkGroup(WorkGroupRegistRequest.builder()
                            .businessCode(project.getPrjSeq().toString())
                            .businessName(project.getPrjSeq().toString())
                            .build());
            log.info("API GW 업무 코드 등록 결과: {}", workGroupRegistResult);
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("API GW 업무 코드 등록 실패 (BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("API GW 업무 코드 등록 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.API_GW_WORK_GROUP_REGIST_FAILED, e.getMessage());
        }

        return projBaseInfoCreateReq;
    }

    /* 프로젝트 생성 간편결재 완료 후 작업 */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProjectAfterProcess(boolean isApproval, ProjectCreateReq projectCreateReq) {
        if (isApproval) {
            Project project = gpoProjectsRepository.getByPrjSeq(projectCreateReq.getPrjSeq());

            // 결재 대기중인 상태를 ONGOING 으로 업데이트
            project.setStatusNm(ProjectStatus.ONGOING);

            // updated_by, updated_at 컬럼을 null로 설정
            try {
                Field updatedByField = project.getClass().getSuperclass().getSuperclass().getDeclaredField("updatedBy");
                updatedByField.setAccessible(true);
                updatedByField.set(project, null);

                Field lstUpdatedAtField =
                        project.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField(
                                "lstUpdatedAt");
                lstUpdatedAtField.setAccessible(true);
                lstUpdatedAtField.set(project, null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Failed to set updated_by and updated_at to null: {}", e.getMessage());
            }

            gpoProjectsRepository.save(project);
        }
    }

    @Override
    public String joinProject(ProjInfoReq projJoinReq) {
        String username = tokenInfo.getUserName();
        User user = gpoUsersRepository.findByMemberId(username).orElse(null);
        String memberId = user.getMemberId();// 포탈 관리자 목록 조회 (prjSeq = -999, roleSeq = -199)

        // 포탈관리자는 프로젝트참여시 프로젝트관리자로 가입하기 위함
        boolean isPortalAdmin = gpoPrjuserroleRepository.findMemberIdsByPrjSeqAndRoleSeq(-999L, -199L)
                .stream()
                .anyMatch(id -> id.equals(memberId));

        ProjectInfo projectInfo = projJoinReq.getProject();
        Long projectId = Long.parseLong(projectInfo.getId());
        log.debug("joinProject: {}, {}", memberId, projectId);

        // 이미 가입되어있으면 에러 리턴
        if (gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(memberId, projectId).isPresent()) {
            throw new BusinessException(ErrorCode.PROJECT_NAME_ALREADY_EXISTS);
        }

        // project  user role 기준으로 한 row 를 새성 합니다.
        Project project = gpoProjectsRepository.findById(projectId).orElse(null);
        Role adminRole = gpoRolesRepository.findById(-299L).orElse(null); // -299:프로젝트관리자 고유 seq
        Role testRole = gpoRolesRepository.findById(-297L).orElse(null); // -297:테스터 고유 seq

        ProjectUserRole newRow = ProjectUserRole.builder()
                .statusNm(ProjectUserRoleStatus.INACTIVE)
                .user(user)
                .project(project)
                .role(isPortalAdmin ? adminRole : testRole)
                .build();
        gpoPrjuserroleRepository.save(newRow);

        // 프로젝트 참여자에게 ADXP 테스터 권한 부여
        String projectSeq = String.valueOf(project.getPrjSeq());
        String adminGroupNm = "P%s_R-299".formatted(projectSeq);
        String testGroupNm = "P%s_R-297".formatted(projectSeq);

        adminAuthService.assignUserToGroup(username, isPortalAdmin ? adminGroupNm : testGroupNm);

        return project.getUuid();
    }

    @Override
    @Transactional
    public void joinProjectAfterProcess(boolean isApproval, ProjJoinReq projJoinReq) {
        if (isApproval) {
            ProjInfoReq projInfoReq = new ProjInfoReq();
            ProjectInfo projectInfo = new ProjectInfo();

            projectInfo.setId(String.valueOf(projJoinReq.getPrjSeq()));
            projInfoReq.setProject(projectInfo);
            projInfoReq.setUsername(projJoinReq.getUsername());

            joinProject(projInfoReq);
        }
    }

    /**
     * 프로젝트 종료
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProject(long prjSeq) {
        log.info("프로젝트 종료: prjSeq={}", prjSeq);

        Project project = gpoProjectsRepository.getByPrjSeq(prjSeq);

        if (prjSeq == PUBLIC_PROJECT_SEQ) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PUBLIC_PROJECT);
        }

        // 결재요청중인 프로젝트 검증
        if (!project.getStatusNm().equals(ProjectStatus.REQUESTED)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ONGOING_PROJECT);
        }

        // =============================
        // ADXP 그룹/권한 정리 (베스트 에포트)
        // =============================

        try {
            String groupKeyword = "P" + prjSeq + "_";

            // 1) 프로젝트 참여자 목록 조회 (ACTIVE)
            List<ProjectUserRole> userMappings = projectUserRoleRepository.findByProjectPrjSeq(prjSeq);
            List<String> memberIds = userMappings.stream()
                    .map(m -> m.getUser().getMemberId())
                    .distinct()
                    .toList();

            // 1. 역할-권한 매핑 정보 삭제
            gpoRoleAuthMapMasRepository.deleteByRolePrjSeq(prjSeq);
            log.info("역할-권한 매핑 정보 삭제 완료: prjSeq={}", prjSeq);

            // 2. 프로젝트-사용자-역할 매핑 정보 삭제
            projectUserRoleRepository.deleteByPrjSeq(prjSeq);
            log.info("프로젝트 구성원 매핑 정보 삭제 완료: prjSeq={}", prjSeq);

            // 3. 프로젝트 역할 정보 삭제
            roleRepository.deleteByPrjSeq(prjSeq);
            log.info("프로젝트 역할 정보 삭제 완료: prjSeq={}", prjSeq);

            // 4. 프로젝트 삭제
            projectMgmtRepository.deleteByPrjSeq(prjSeq);
            log.info("프로젝트 삭제 완료: prjSeq={}", prjSeq);

            // ADXP 권한 동기화 (비동기)
            CompletableFuture.runAsync(() -> {
                List<String> groupNames = adminAuthService.findGroupNamesByKeyword(groupKeyword);

                if (!groupNames.isEmpty() && !memberIds.isEmpty()) {
                    for (String participantMemberId : memberIds) {
                        for (String groupName : groupNames) {
                            try {
                                adminAuthService.unassignUserFromGroup(participantMemberId, groupName);
                            } catch (BusinessException e) {
                                log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}",
                                        participantMemberId, groupName, e.getMessage());
                            } catch (Exception ex) {
                                log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}",
                                        participantMemberId, groupName, ex.getMessage());
                            }
                        }
                    }
                }

                try {
                    adminAuthService.deleteGroupsByKeyword(groupKeyword);
                } catch (BusinessException e) {
                    log.warn("ADXP 그룹 일괄 삭제 실패(무시): keyword={}, reason={}", groupKeyword, e.getMessage());
                } catch (Exception ex) {
                    log.warn("ADXP 그룹 일괄 삭제 실패(무시): keyword={}, reason={}", groupKeyword, ex.getMessage());
                }
            });
        } catch (BusinessException e) {
            // 외부 연동 실패는 로컬 DB 삭제를 막지 않음
            log.warn("ADXP 그룹/권한 정리 중 오류(무시): prjSeq={}, reason={}", prjSeq, e.getMessage());
        } catch (Exception ex) {
            // 외부 연동 실패는 로컬 DB 삭제를 막지 않음
            log.warn("ADXP 그룹/권한 정리 중 오류(무시): prjSeq={}, reason={}", prjSeq, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public int quitProject(ProjInfoReq projQuitReq) {
        String username = tokenInfo.getUserName();
        User user = gpoUsersRepository.findByMemberId(username).orElse(null);
        String memberId = user.getMemberId();

        ProjectInfo project = projQuitReq.getProject();
        Long projectId = Long.parseLong(project.getId());
        log.debug("quitProject: {}, {}", memberId, projectId);

        gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(memberId, projectId).ifPresent((projectUserRole) -> {
            gpoPrjuserroleRepository.delete(projectUserRole);

            String projectSeq = String.valueOf(projectUserRole.getProject().getPrjSeq());
            String roleSeq = String.valueOf(projectUserRole.getRole().getRoleSeq());
            String groupNm = "P%s_R%s".formatted(projectSeq, roleSeq);

            // ADXP 권한 제거
            adminAuthService.unassignUserFromGroup(username, groupNm);
        });

        return 0;
    }

    @Override
    public ProjUserRes getProjectUserInfo(String memberId) {
        log.info("+++++++++++++++++++++++ getProjectUserInfo: {}", memberId);
        User user = gpoUsersRepository.findById(memberId).orElse(null);

        ProjUserRes projUserRes = ProjUserRes.builder()
                .memberId(user.getMemberId())
                .jkwNm(user.getJkwNm())
                .deptNm(user.getDeptNm())
                .build();

        return projUserRes;
    }


    @Override
    public List<ProjectRes> getJoinProjectList(String username) {
        log.info("getJoinProjectList: {}", username);
        // 마이바티스 매퍼를 통한 조인 쿼리 실행
        List<ProjectRes> projectList = projectMapper.findJoinProjectList(username);

        log.info("조회된 프로젝트 수: {}", projectList.size());
        log.debug("projects : {}", projectList.toString());

        return projectList;
    }

    @Override
    public List<ProjPrivateRes> getJoinPrivateProjectList(String username) {
        log.info("getJoinProjectList: {}", username);
        // 마이바티스 매퍼를 통한 조인 쿼리 실행
        List<ProjPrivateRes> projectList = projectMapper.findJoinPrivateProjectList(username);

        log.info("조회된 프로젝트 수: {}", projectList.size());
        log.debug("projects : {}", projectList.toString());

        return projectList;
    }

    @Override
    public List<ProjPrivateRes> getNotJoinProjectList(String username, String condition, String keyword) {

        log.info("getJoinProjectList: {}", username);
        // 마이바티스 매퍼를 통한 조인 쿼리 실행
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        if (!"null".equals(condition) && condition != null && !"".equals(condition)
                && !"null".equals(keyword) && keyword != null && !"".equals(keyword)) {
            if ("name".equals(condition)) {
                params.put("name", keyword);
            } else {
                params.put("project", keyword);
            }
        }

        log.info("getJoinProjectList: {}", params);
        // 마이바티스 매퍼를 통한 조인 쿼리 실행
        List<ProjPrivateRes> projectList = projectMapper.findNotJoinProjectList(params).stream()
                .filter(prj -> !isApprovalInProgress("02" + username + prj.getPrjSeq()))
                .toList();

        log.info("조회된 프로젝트 수: {}", projectList.size());
        log.debug("projects : {}", projectList.toString());

        return projectList;
    }

    @Override
    public List<ProjDetailRes> getNotJoinProjectDetail(String projectId) {
        log.info("getJoinProjectList: {}", projectId);
        List<ProjDetailRes> projectList = projectMapper.findNotJoinProjectDetail(Long.parseLong(projectId));
        return projectList;
    }

    @Override
    public List<ProjUserRes> getProjectUserList(String username, String condition, String keyword) {
        log.info("getJoinProjectList: {}", username);
        // 마이바티스 매퍼를 통한 조인 쿼리 실행
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        if (!"null".equals(condition) && condition != null && !"".equals(condition)
                && !"null".equals(keyword) && keyword != null && !"".equals(keyword)) {
            if ("profile".equals(condition)) {
                params.put("name", keyword);
            } else {
                params.put("department", keyword);
            }
        }
        List<ProjUserRes> projectList = projectMapper.findProjectUserList(params);

        log.info("조회된 프로젝트 수: {}", projectList.size());
        log.debug("projects : {}", projectList.toString());

        return projectList;
    }

    /**
     * 프로젝트명 중복 검증
     *
     * @param projectName 검증할 프로젝트명
     * @throws BusinessException 동일한 프로젝트명이 존재하는 경우
     */
    private void validateProjectNameNotDuplicate(String projectName) {
        List<Project> existingProjects = gpoProjectsRepository.findByPrjNmContainingIgnoreCase(projectName);

        if (!existingProjects.isEmpty()) {
            log.warn("동일한 프로젝트명 존재: {}", projectName);
            throw new BusinessException(ErrorCode.PROJECT_NAME_ALREADY_EXISTS);
        }
    }

    private boolean isApprovalInProgress(String approvalUniqueKey) {
        // 1. approvalUniqueKey와 일치하는 모든 레코드 조회
        List<GpoGyljMas> approvalList = gpoGyljMasRepository.findByEroCtnt(approvalUniqueKey);

        if (approvalList.isEmpty()) {
            return false;
        }

        // 2. dtl_ctnt의 앞 36자로 그룹핑
        Map<String, List<GpoGyljMas>> groupedByDocumentId = approvalList.stream()
                .filter(approval -> approval.getDtlCtnt() != null && approval.getDtlCtnt().length() >= 36)
                .collect(java.util.stream.Collectors.groupingBy(
                        approval -> approval.getDtlCtnt().substring(0, 36)));

        // 3. 각 그룹별로 종결 여부 확인
        for (Map.Entry<String, List<GpoGyljMas>> entry : groupedByDocumentId.entrySet()) {
            List<GpoGyljMas> group = entry.getValue();

            // 자체 취소건 확인
            if (group.stream()
                    .filter(approval -> approval.getGyljRespId() != null)
                    .anyMatch(approval -> gpoAlarmsRepository.existsByApiRstMsgAndStatusNm(entry.getKey(), CommCode.AlarmStatus.CANCELED))) {
                continue;
            }

            // 그룹 내의 모든 gylj_id가 callback 테이블에 있는지 확인 (하나라도 종결되지 않은 그룹이 있으면 진행중)
            if (!group.stream()
                    .filter(approval -> approval.getGyljRespId() != null)
                    .anyMatch(approval -> gpoGyljCallbackMasRepository.existsByGyljRespId(approval.getGyljRespId()))) {
                return true;
            }
        }

        // 모든 그룹이 종결되었으면 진행중인 것 없음
        return false;
    }
}
