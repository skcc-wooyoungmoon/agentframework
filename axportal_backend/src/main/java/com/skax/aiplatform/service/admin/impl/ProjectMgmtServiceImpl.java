package com.skax.aiplatform.service.admin.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupDeleteResponse;
import com.skax.aiplatform.client.ione.api.service.IoneApiService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyItem;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.EnumUtils;
import com.skax.aiplatform.common.util.TokenInfo;
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
import com.skax.aiplatform.dto.model.request.DeleteModelDeployReq;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.auth.GpoAdxpResourceMas;
import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import com.skax.aiplatform.entity.common.enums.YNStatus;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.mapping.GpoRoleAuthMapMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.RoleAuthorityStatus;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.role.RoleStatus;
import com.skax.aiplatform.entity.role.RoleType;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.entity.user.User;
import com.skax.aiplatform.repository.admin.GpoAdxpResourceMasRepository;
import com.skax.aiplatform.repository.admin.GpoAuthorityMasRepository;
import com.skax.aiplatform.repository.admin.GpoRoleAuthMapMasRepository;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.admin.RoleRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoUsersRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.admin.ProjectMgmtService;
import com.skax.aiplatform.service.deploy.AgentDeployService;
import com.skax.aiplatform.service.model.ModelDeployService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 프로젝트 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMgmtServiceImpl implements ProjectMgmtService {

    private static final long PUBLIC_PROJECT_SEQ = -999L; // 공개 프로젝트 고정 SEQ
    private static final long PROJECT_ADMIN_ROLE_SEQ = -299L; // 프로젝트 관리자 역할 고정 SEQ
    private static final long PORTAL_ADMIN_ROLE_SEQ = -199L; // 포탈 관리자 역할 고정 SEQ

    private final GpoUsersMasRepository userRepository;
    private final GpoUsersRepository usersRepository2;

    private final ProjectMgmtRepository projectMgmtRepository;
    private final RoleRepository roleRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;
    private final GpoAdxpResourceMasRepository gpoAdxpResourceMasRepository;
    private final GpoRoleAuthMapMasRepository gpoRoleAuthMapMasRepository;
    private final GpoAuthorityMasRepository gpoAuthorityMasRepository;
    private final GpoAssetPrjMapMasRepository assetRepository;

    // Admin 권한/그룹 연동 서비스 (ADXP)
    private final AdminAuthService adminAuthService;
    private final IoneApiService ioneApiService;
    private final RestTemplate restTemplate = createSslBypassingRestTemplate();

    // 프로젝트 삭제 시 serving/app 에셋은 내부 삭제 서비스 호출용
    private final ModelDeployService modelDeployService;
    private final AgentDeployService agentDeployService;

    private final TokenInfo tokenInfo;

    @Value("${sktai.api.base-url}")
    private String sktaiBaseUrl;

    // ================================
    // 1. 프로젝트
    // ================================

    /**
     * 전체 프로젝트 조회
     */
    @Override
    public Page<ProjectListRes> getProjects(ProjectSearchReq searchReq) {
        String memberId = tokenInfo.getUserName();

        log.info("요청 memberId : {}", memberId);

        // 1. 포탈 관리자 역할 확인
        boolean isPortalAdmin = isPortalAdmin(memberId);

        Page<Project> projects;

        if (isPortalAdmin) {
            // 2-1. 포탈 관리자인 경우: 모든 프로젝트 조회
            log.info("포탈 관리자로 전체 프로젝트 조회 - memberId: {}", memberId);

            projects = projectMgmtRepository.findProjectsBySearch(
                    searchReq.toPageable(),
                    searchReq.getFilterType(),
                    searchReq.getKeyword(),
                    ProjectStatus.ONGOING
            );
        } else {
            // 2-2. 프로젝트 관리자인 경우: 프로젝트 관리자 역할을 가진 프로젝트만 조회
            log.info("프로젝트 관리자 프로젝트만 조회 - memberId: {}", memberId);

            projects = projectMgmtRepository.findProjectsByProjectManagerRole(
                    searchReq.toPageable(),
                    searchReq.getFilterType(),
                    searchReq.getKeyword(),
                    ProjectStatus.ONGOING,
                    memberId,
                    PROJECT_ADMIN_ROLE_SEQ
            );
        }

        log.info("프로젝트 목록 조회 완료 - memberId: {}, 조회된 프로젝트 수: {}", memberId, projects.getTotalElements());
        return projects.map(ProjectListRes::of);
    }

    /**
     * 프로젝트 상세 조회
     */
    @Override
    public ProjectDetailRes getProjectById(String projectId) {
        log.info("프로젝트 상세 조회: {}", projectId);

        Project project = findProjectByUuid(projectId);

        GpoUsersMas createdBy = userRepository.findByMemberId(project.getCreatedBy())
                .orElse(null);
        GpoUsersMas updatedBy = userRepository.findByMemberId(project.getUpdatedBy())
                .orElse(null);

        return ProjectDetailRes.of(project, createdBy, updatedBy);
    }

    /**
     * 프로젝트 수정
     */
    @Override
    @Transactional
    public void updateProject(String projectId, ProjectUpdateReq updateReq) {
        log.info("프로젝트 수정: projectId={}, updateReq={}", projectId, updateReq);

        Project project = findProjectByUuid(projectId);
        String projectName = project.getPrjNm();

        String newProjectName = updateReq.getPrjNm().trim();
        String newProjectDesc = Optional.ofNullable(updateReq.getDtlCtnt()).orElse("");
        YNStatus sensitiveYN = EnumUtils.valueOf(updateReq.getSstvInfInclYn(), YNStatus.class);

        // 널 값 역참조 방지: 개인정보 포함 여부는 필수 입력값
        if (sensitiveYN == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "개인정보 포함 여부는 필수입니다. 'Y' 또는 'N' 값을 입력해주세요.");
        }

        String sensitiveDesc = StringUtils.hasText(updateReq.getSstvInfInclDesc())
                ? updateReq.getSstvInfInclDesc().trim()
                : null;

        // 프로젝트명이 변경되었고, 새로운 프로젝트명이 이미 존재하는지 확인
        if (!projectName.equals(newProjectName)) {
            // 결재 진행중인 프로젝트에서 해당 프로젝트명이 이미 존재하는지 확인
            if (project.getStatusNm() == ProjectStatus.REQUESTED && projectMgmtRepository.findByPrjNm(newProjectName).isPresent()) {
                throw new BusinessException(ErrorCode.PROJECT_NAME_IN_APPROVAL);
            }

            // 기존 사용중인 프로젝트에서 해당 프로젝트명이 이미 존재하는지 확인
            if (project.getStatusNm() == ProjectStatus.ONGOING && projectMgmtRepository.findByPrjNm(newProjectName).isPresent()) {
                throw new BusinessException(ErrorCode.PROJECT_NAME_ALREADY_EXISTS);
            }

        }

        // 개인정보 포함 여부 확인
        if (sensitiveYN == YNStatus.Y && sensitiveDesc == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "개인정보 포함 시 사유는 필수입니다.");
        }

        // 이미 개인정보를 포함한 경우, 미포함으로 변경할 수 없음
        if (YNStatus.Y.equals(project.getSstvInfInclYn()) && YNStatus.N.equals(sensitiveYN)) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "이미 개인정보를 포함한 프로젝트는 미포함으로 변경할 수 없습니다.");
        }

        project.update(newProjectName, newProjectDesc, sensitiveYN, sensitiveDesc);
    }

    /**
     * 프로젝트 종료
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProject(String projectId) {
        log.info("프로젝트 종료: projectId={}", projectId);

        String memberId = tokenInfo.getUserName();
        Project project = findProjectByUuid(projectId);
        Long prjSeq = project.getPrjSeq();

        // 공개 프로젝트인 경우 종료 불가
        if (prjSeq == PUBLIC_PROJECT_SEQ) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PUBLIC_PROJECT);
        }

        boolean hasPortalAdminRole = isPortalAdmin(memberId);
        boolean hasProjectManagerRole = isProjectManager(memberId, prjSeq);
        boolean hasProjectManagerRoleOnly = hasProjectManagerRole && !hasPortalAdminRole;

        // 포탈 관리자, 프로젝트 관리자 아닌 경우에는 종료 불가
        if (!hasPortalAdminRole && !hasProjectManagerRole) {
            log.warn("프로젝트 종료 권한 없음 - projectId={}, memberId={}", projectId, memberId);

            throw new BusinessException(ErrorCode.INSUFFICIENT_PRIVILEGES,
                    "프로젝트를 종료할 권한이 없습니다. 포탈 관리자 또는 프로젝트 관리자만 가능합니다.");
        }

        log.info("프로젝트 종료 권한 확인 완료 - projectId={}, memberId={}, hasPortalAdminRole={}, hasProjectManagerRole={}",
                projectId, memberId, hasPortalAdminRole, hasProjectManagerRole);

        // 포탈 관리자는 모든 프로젝트에 프로젝트 관리자 역할을 가지고 있으므로
        // 실제로 프로젝트 관리자 자격으로만 종료하려는 경우에만 공개 이력 검사를 수행한다.
        if (hasProjectManagerRoleOnly) {
            List<GpoAssetPrjMapMas> assets = assetRepository.findByFstPrjSeq(Math.toIntExact(prjSeq));

            assets.forEach(asset -> {
                if (PUBLIC_PROJECT_SEQ == asset.getLstPrjSeq()) {
                    throw new BusinessException(ErrorCode.CANNOT_DELETE_PROJECT_HAS_PUBLIC_ASSETS);
                }
            });
        }


        // =============================
        // API GW 업무 코드 삭제
        // =============================
        try {
            WorkGroupDeleteResponse workGroupDeleteResponse =
                    ioneApiService.deleteWorkGroup(WorkGroupDeleteRequest.builder()
                            .businessCode(project.getPrjSeq().toString())
                            .build());
            log.info("API GW 업무 코드 삭제 결과: {}", workGroupDeleteResponse);
        } catch (BusinessException e) {
            log.warn("API GW 업무 코드 삭제 실패(무시): prjSeq={}, reason={}", prjSeq, e.getMessage());
        } catch (Exception ex) {
            log.warn("API GW 업무 코드 삭제 실패(무시): prjSeq={}, reason={}", prjSeq, ex.getMessage());
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

            // 4. 프로젝트 삭제 (상태만 변경)
            project.setStatusNm(ProjectStatus.COMPLETED);
            projectMgmtRepository.save(project);
            log.info("프로젝트 삭제 완료(상태만 변경): prjSeq={}", prjSeq);

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

        // ====================================================================================

        // 공개 설정으로 전환한 에셋을 제외하고 해당 프로젝트에 속한 모든 에셋 삭제 (비동기)
        List<GpoAssetPrjMapMas> nonPublicAssets =
                assetRepository.findNonPublicAssetsByFstPrjSeq(Math.toIntExact(prjSeq));

        log.info("nonPublicAssets size : {}", nonPublicAssets.size());

        if (!nonPublicAssets.isEmpty()) {
            String accessToken = null;

            try {
                accessToken = requestAdminAccessToken();
            } catch (BusinessException e) {
                log.warn("토큰 발급에 실패했습니다. : {}", e.getMessage());
            }

            if (StringUtils.hasText(accessToken)) {
                String finalAccessToken = accessToken;

                CompletableFuture.runAsync(() -> nonPublicAssets.forEach(asset -> {
                    String assetUrl = asset.getAsstUrl();

                    String normalizedAssetUrl = assetUrl.startsWith("/api/v1")
                            ? assetUrl
                            : "/api/v1" + assetUrl;

                    // serving 또는 app 경로는 ModelDeployService / AgentDeployService 로 내부 삭제
                    if (normalizedAssetUrl.contains("/api/v1/servings/")) {
                        String servingId = normalizedAssetUrl.replaceFirst(".*/api/v1/servings/", "").split("/")[0];
                        try {
                            DeleteModelDeployReq req = DeleteModelDeployReq.builder()
                                    .servingId(servingId)
                                    .servingType("serverless")
                                    .build();
                            modelDeployService.deleteModelDeployBulk(List.of(req));
                            log.info("프로젝트 자산 삭제 완료(내부 ModelDeploy) - servingId={}", servingId);
                        } catch (Exception e) {
                            log.warn("프로젝트 자산 삭제 실패(무시) - servingId={}, reason={}", servingId, e.getMessage());
                            // 실패하더라도 Asset이라도 제거
                            deleteAsset(normalizedAssetUrl, finalAccessToken);
                        }
                    } else if (normalizedAssetUrl.contains("/api/v1/agent/agents/apps/")) {
                        // 1) .../agents/apps/deployments/{deploymentId} → 개별 배포 삭제
                        if (normalizedAssetUrl.contains("/api/v1/agent/agents/apps/deployments/") || normalizedAssetUrl.matches(".*/api/v1/agent/agents/apps/[^/]+/deployments$")) {     
                            deleteAsset(normalizedAssetUrl, finalAccessToken);
                        }
                        // 3) .../agents/apps/{appId} → 앱 삭제
                        else {
                            String appId = normalizedAssetUrl.replaceFirst(".*/api/v1/agent/agents/apps/", "").split("/")[0];
                            try {
                                agentDeployService.deleteAgentApp(appId);
                                log.info("프로젝트 자산 삭제 완료(내부 app) - appId={}", appId);
                            } catch (Exception e) {
                                log.warn("프로젝트 자산 삭제 실패(무시) - appId={}, reason={}", appId, e.getMessage());
                                deleteAsset(normalizedAssetUrl, finalAccessToken);
                            }
                        }
                    } else {
                        // 그 외 에셋은 외부 에셋 삭제 API 호출
                        deleteAsset(normalizedAssetUrl, finalAccessToken);
                    }
                }));
            }

            // 내부 private 에셋 전체 삭제
            assetRepository.deleteAllInBatch(nonPublicAssets);
            log.info("프로젝트 자산 매핑 삭제 완료 - count={}", nonPublicAssets.size());
        }
    }

    // ================================
    // 2. 프로젝트 - 역할
    // ================================

    /**
     * 프로젝트내 역할 목록 조회
     */
    @Override
    public Page<RoleRes> getProjectRoles(String projectId, RoleSearchReq searchReq) {
        log.info("프로젝트내 역할 목록 조회: projectId={}, searchReq={}", projectId, searchReq);

        Project project = findProjectByUuid(projectId);

        Page<Role> roles;

        // prjSeq가 -999L인 경우: 포탈 전역 역할만 노출
        if (Long.valueOf(PUBLIC_PROJECT_SEQ).equals(project.getPrjSeq())) {
            roles = roleRepository.findPortalRoles(
                    searchReq.toPageable(),
                    EnumUtils.valueOf(searchReq.getRoleType(), RoleType.class),
                    searchReq.getFilterType(),
                    searchReq.getKeyword()
            );
        } else {
            // 그 외: 포탈 전역 역할 제외하고 해당 프로젝트 역할만 노출
            roles = roleRepository.findProjectRolesExcludePortal(
                    project.getPrjSeq(),
                    RoleStatus.ACTIVE,
                    searchReq.toPageable(),
                    EnumUtils.valueOf(searchReq.getRoleType(), RoleType.class),
                    searchReq.getFilterType(),
                    searchReq.getKeyword()
            );
        }

        return roles.map(RoleRes::of);
    }

    /**
     * 프로젝트 역할 생성
     */
    @Override
    @Transactional
    public String createProjectRole(String projectId, RoleCreateReq createReq) {
        Project project = findProjectByUuid(projectId);
        String roleName = createReq.getRoleNm().trim();
        String roleDesc = Optional.ofNullable(createReq.getDtlCtnt()).orElse("");

        List<String> authorityIds = createReq.getAuthorityIds();

        log.info("프로젝트 역할 생성: projectId={}, prjSeq={}, roleName={}, authorityCount={}",
                projectId, project.getPrjSeq(), roleName, authorityIds.size());

        // 역할명 중복 Validation
        Optional<Role> optionalRole = roleRepository.findByPrjSeqAndRoleNm(project.getPrjSeq(), roleName);
        if (optionalRole.isPresent()) {
            log.warn("프로젝트 내 중복 역할명 감지: prjSeq={}, roleId={}, roleName={}",
                    project.getPrjSeq(), optionalRole.get().getUuid(), optionalRole.get().getRoleNm());
            throw new BusinessException(ErrorCode.ROLE_NAME_ALREADY_EXISTS);
        }

        // 신규 역할 생성
        var role = Role.create(project.getPrjSeq(), UUID.randomUUID().toString(), roleName, roleDesc.trim());
        Role savedRole = roleRepository.save(role);

        // ADXP 그룹 생성: P{prjSeq}_R{roleSeq}
        try {
            String groupName = "P" + project.getPrjSeq() + "_R" + savedRole.getRoleSeq();
            adminAuthService.createGroup(groupName);
            log.info("ADXP 그룹 생성 완료: {}", groupName);
        } catch (BusinessException e) {
            log.warn("ADXP 그룹 생성 실패(무시): prjSeq={}, roleSeq={}, reason={}",
                    project.getPrjSeq(), savedRole.getRoleSeq(), e.getMessage());
        } catch (Exception ex) {
            log.warn("ADXP 그룹 생성 실패(무시): prjSeq={}, roleSeq={}, reason={}",
                    project.getPrjSeq(), savedRole.getRoleSeq(), ex.getMessage());
        }

        // --- 권한 업데이트 ---
        updateRoleAuthorities(savedRole, authorityIds, false);
        log.info("역할 권한 업데이트 완료: roleSeq={}, roleUuid={}", savedRole.getRoleSeq(), savedRole.getUuid());

        return savedRole.getUuid();
    }

    /**
     * 프로젝트 역할 삭제
     */
    @Override
    @Transactional
    public ProjectRoleDeleteRes deleteProjectRoles(String projectId, RoleDeleteReq deleteReq) {
        log.info("프로젝트 역할 다중 삭제 요청. projectId: {}, roleUuids: {}", projectId, deleteReq.getRoleUuids());

        Project project = findProjectByUuid(projectId);

        int successCount = 0;
        int failureCount = 0;
        String errorMessage = null;

        List<String> roleUuids = deleteReq.getRoleUuids();

        for (String roleUuid : roleUuids) {
            try {
                Role role = findRoleByIdAndProject(roleUuid, project);

                // 1. 기본 역할은 삭제할 수 없음
                if (role.getRoleType() == RoleType.DEFAULT) {
                    log.warn("프로젝트 역할 삭제 실패 - 기본 역할은 삭제할 수 없습니다: roleId={}", roleUuid);
                    throw new BusinessException(ErrorCode.CANNOT_DELETE_DEFAULT_ROLE);
                }

                // 2. 해당 역할을 사용하고 있는 구성원이 존재하는지 확인
                if (projectUserRoleRepository.existsByRoleRoleSeq(role.getRoleSeq())) {
                    log.warn("프로젝트 역할 삭제 실패 - 해당 역할을 사용하는 구성원이 존재합니다: roleId={}", roleUuid);
                    throw new BusinessException(ErrorCode.ROLE_HAS_ACTIVE_USERS);
                }

                // 3. 프로젝트-사용자-역할 매핑 삭제
                List<ProjectUserRole> projectUserRoles =
                        projectUserRoleRepository.findByRoleRoleSeq(role.getRoleSeq());
                projectUserRoleRepository.deleteAll(projectUserRoles);
                log.info("프로젝트-사용자-역할 매핑 삭제 완료: roleSeq={}, count={}", role.getRoleSeq(),
                        projectUserRoles.size());

                // 4. 역할-권한 매핑 삭제
                List<GpoRoleAuthMapMas> roleAuthMappings =
                        gpoRoleAuthMapMasRepository.findByRoleRoleSeq(role.getRoleSeq());
                gpoRoleAuthMapMasRepository.deleteAll(roleAuthMappings);
                log.info("역할-권한 매핑 삭제 완료: roleSeq={}, count={}", role.getRoleSeq(), roleAuthMappings.size());

                // 5. 역할 삭제
                roleRepository.delete(role);
                log.info("역할 삭제 완료: roleSeq={}, roleUuid={}", role.getRoleSeq(), role.getUuid());

                // =============================
                // ADXP 그룹/권한 정리 (베스트 에포트)
                // =============================
                try {
                    String groupName = "P" + project.getPrjSeq() + "_R" + role.getRoleSeq();

                    // 1) 역할을 사용중인 유저 조회 후 그룹 권한 제거
                    if (projectUserRoles != null && !projectUserRoles.isEmpty()) {
                        for (ProjectUserRole pur : projectUserRoles) {
                            try {
                                String memberId = pur.getUser().getMemberId();
                                adminAuthService.unassignUserFromGroup(memberId, groupName);
                            } catch (BusinessException e) {
                                log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}",
                                        pur.getUser().getMemberId(), groupName, e.getMessage());
                            } catch (Exception ex) {
                                log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}",
                                        pur.getUser().getMemberId(), groupName, ex.getMessage());
                            }
                        }
                    }

                    // 2) 그룹 삭제
                    try {
                        adminAuthService.deleteGroup(groupName);
                        log.info("ADXP 그룹 삭제 완료: {}", groupName);
                    } catch (BusinessException e) {
                        log.warn("ADXP 그룹 삭제 실패(무시): groupName={}, reason={}", groupName, e.getMessage());
                    } catch (Exception ex) {
                        log.warn("ADXP 그룹 삭제 실패(무시): groupName={}, reason={}", groupName, ex.getMessage());
                    }
                } catch (BusinessException e) {
                    log.warn("ADXP 그룹/권한 정리 중 오류(무시): prjSeq={}, roleSeq={}, reason={}",
                            project.getPrjSeq(), role.getRoleSeq(), e.getMessage());
                } catch (Exception ex) {
                    log.warn("ADXP 그룹/권한 정리 중 오류(무시): prjSeq={}, roleSeq={}, reason={}",
                            project.getPrjSeq(), role.getRoleSeq(), ex.getMessage());
                }

                successCount++;
                log.info("역할 삭제 성공: {}", roleUuid);
            } catch (BusinessException ex) {
                log.warn("프로젝트 역할 삭제 실패 - roleId={}, reason={}", roleUuid, ex.getMessage());
                failureCount++;
                if (errorMessage == null) {
                    errorMessage = ex.getMessage();
                }
            } catch (Exception ex) {
                log.error("프로젝트 역할 삭제 중 예기치 못한 오류 - roleId={}", roleUuid, ex);
                failureCount++;
                if (errorMessage == null) {
                    errorMessage = "역할 삭제 중 예기치 못한 오류: " + ex.getMessage();
                }
            }
        }

        return ProjectRoleDeleteRes.of(successCount, failureCount, errorMessage);
    }

    /**
     * 프로젝트 역할 상세 조회
     */
    @Override
    public RoleDetailRes getProjectRoleDetail(String projectId, String roleId) {
        log.info("프로젝트 역할 상세 조회: projectId={}, roleId={}", projectId, roleId);

        Project project = findProjectByUuid(projectId);
        Role role = findRoleByIdAndProject(roleId, project);

        GpoUsersMas createdBy = userRepository.findByMemberId(role.getCreatedBy())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        GpoUsersMas updatedBy = userRepository.findByMemberId(role.getUpdatedBy())
                .orElse(null);

        return RoleDetailRes.of(role, createdBy, updatedBy);
    }

    /**
     * 프로젝트 역할 수정
     */
    @Override
    @Transactional
    public void updateProjectRole(String projectId, String roleId, RoleUpdateReq updateReq) {
        log.info("프로젝트 역할 수정: projectId={}, roleId={}, updateReq={}", projectId, roleId, updateReq);

        Project project = findProjectByUuid(projectId);
        Role role = findRoleByIdAndProject(roleId, project);

        String newRoleName = updateReq.getRoleNm().trim();
        String roleDesc = Optional.ofNullable(updateReq.getDtlCtnt())
                .map(String::trim)
                .orElse("");

        // 역할명 중복 Validation
        if (!role.getRoleNm().equals(newRoleName)) {
            Optional<Role> optionalRole = roleRepository.findByPrjSeqAndRoleNm(project.getPrjSeq(), newRoleName);
            if (optionalRole.isPresent()) {
                log.warn("프로젝트 역할 수정중 중복 역할명 감지: prjSeq={}, roleId={}, roleName={}",
                        project.getPrjSeq(), optionalRole.get().getUuid(), optionalRole.get().getRoleNm());
                throw new BusinessException(ErrorCode.ROLE_NAME_ALREADY_EXISTS);
            }
        }

        role.update(newRoleName, roleDesc);
    }

    // ================================
    // 2-1. 프로젝트 - 역할 - 권한
    // ================================

    /**
     * 프로젝트 역할 권한 목록 조회
     */
    @Override
    public Page<RoleAuthorityRes> getProjectRoleAuthorities(String projectId, String roleId,
                                                            RoleAuthoritySearchReq searchReq) {
        log.info("프로젝트 역할 권한 목록 조회: projectId={}, roleId={}, searchReq={}", projectId, roleId, searchReq);

        Project project = findProjectByUuid(projectId);
        Role role = findRoleByIdAndProject(roleId, project);

        Page<GpoRoleAuthMapMas> authorities = gpoRoleAuthMapMasRepository.searchRoleAuthorities(
                role.getRoleSeq(),
                RoleAuthorityStatus.ACTIVE,
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                searchReq.getTwoDepthMenu()
        );

        return authorities.map(RoleAuthorityRes::of);
    }

    /**
     * 프로젝트 역할 권한 수정
     */
    @Override
    @Transactional
    public void updateProjectRoleAuthorities(String projectId, String roleId, RoleAuthorityUpdateReq updateReq) {
        log.info("프로젝트 역할 권한 수정: projectId={}, roleId={}, updateReq={}", projectId, roleId, updateReq);

        Project project = findProjectByUuid(projectId);
        Role role = findRoleByIdAndProject(roleId, project);

        updateRoleAuthorities(role, updateReq.getAuthorityIds(), true);
    }

    // ================================
    // 2-2. 프로젝트 - 역할 - 구성원
    // ================================

    /**
     * 프로젝트 역할에 배정된 구성원 목록 조회
     */
    @Override
    public Page<RoleUserRes> getProjectRoleUsers(String projectId, String roleId, ProjectRoleUserSearchReq searchReq) {
        log.info("[서비스] 프로젝트 역할 구성원 목록 조회 - projectId={}, roleId={}, searchReq={}", projectId, roleId, searchReq);

        Project project = findProjectByUuid(projectId);
        Role role = findRoleByIdAndProject(roleId, project);

        Page<User> users = projectMgmtRepository.getProjectRoleUsers(
                project.getPrjSeq(),
                role.getRoleSeq(),
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                EnumUtils.valueOf(searchReq.getDmcStatus(), DormantStatus.class),
                searchReq.getRetrJkwYn()
        );

        return users.map(RoleUserRes::of);
    }

    /**
     * 프로젝트 구성원 역할 변경
     */
    @Override
    @Transactional
    public ProjectUserAssignRes updateProjectUserRoles(String projectId, ProjectUserRoleChangeReq request) {
        log.info("프로젝트 구성원 역할 변경 요청 - projectId={}, request={}", projectId, request);

        // 1. 사전 준비 (Preparation)
        Project project = findProjectByUuid(projectId);
        Map<String, Role> roleMap = findRoleMapBy(request);
        Map<String, ProjectUserRole> currentMappings = getCurrentMappingsAsMap(project);

        // 2. 변경 후 상태 계산 및 유효성 검증 (Calculation & Validation)
        Map<String, Long> futureUserRoles = calculateFutureRoles(project, request, roleMap, currentMappings);

        if (project.getPrjSeq() == PUBLIC_PROJECT_SEQ) { // 공개 프로젝트
            // 포탈 관리자 한명 이상인지 검증
            validatePortalAdminExists(futureUserRoles);
        } else {
            // 프로젝트 관리자 한명이상인지 검증
            validateProjectManagerExists(futureUserRoles);
        }

        // 3. 실제 변경 적용 (Application)
        return applyRoleChanges(projectId, request, roleMap, currentMappings);
    }

    // ================================
    // 3. 프로젝트 - 구성원
    // ================================

    /**
     * 프로젝트 구성원 목록 조회
     */
    @Override
    public Page<ProjectUserRes> getProjectUsers(String projectId, UserSearchReq searchReq) {
        log.info("프로젝트 구성원 목록 조회: projectId={}, searchReq={}", projectId, searchReq);

        Project project = findProjectByUuid(projectId);

        Long prjSeq = project.getPrjSeq();

        // 1. 사용자 목록을 페이징하여 조회
        Page<User> usersPage = projectMgmtRepository.findProjectUsers(
                prjSeq,
                searchReq.toPageable(),
                searchReq.getFilterType(),
                searchReq.getKeyword(),
                EnumUtils.valueOf(searchReq.getDmcStatus(), DormantStatus.class),
                searchReq.getRetrJkwYn()
        );

        List<User> users = usersPage.getContent();
        if (users.isEmpty()) {
            return Page.empty(searchReq.toPageable());
        }

        // 2. 조회된 사용자들의 역할 정보를 한 번에 조회
        List<String> memberIds = users.stream().map(User::getMemberId).toList();
        Map<String, ProjectUserRole> userMappingMap =
                projectUserRoleRepository.findByProjectPrjSeqAndUserMemberIdIn(prjSeq, memberIds)
                        .stream()
                        .collect(toMap(
                                pur -> pur.getUser().getMemberId(),
                                Function.identity()
                        ));

        // 3. 사용자 정보와 역할 정보를 조합하여 DTO 생성
        return usersPage.map(user -> {
            ProjectUserRole mapping = userMappingMap.get(user.getMemberId());

            if (mapping == null) {
                log.error("사용자에게 할당된 역할을 찾을 수 없습니다. projectId={}, memberId={}",
                        projectId, user.getMemberId());
                throw new BusinessException(ErrorCode.USER_PROJECT_NOT_FOUND);
            }

            return ProjectUserRes.of(user, mapping.getRole(), mapping.getFstCreatedAt());
        });
    }

    /**
     * 프로젝트 구성원 삭제
     */
    @Override
    @Transactional
    public ProjectUserDeleteRes deleteProjectUsers(String projectId, ProjectUserDeleteReq request) {
        log.info("[서비스] 프로젝트 구성원 삭제 요청 - projectId={}, request={}", projectId, request);

        Project project = findProjectByUuid(projectId);
        List<String> userUuidsToDelete = request.getUserUuids();

        // 1. 검증 (Validation)
        // 1-1. Helper 메소드를 재사용하여 현재 매핑 정보와 PM 역할 정보 가져오기
        Map<String, ProjectUserRole> currentMappingsMap = getCurrentMappingsAsMap(project);

        // 1-2. "미래 상태"의 역할 맵 구성 (삭제될 사용자 제외)
        Map<String, Long> futureUserRoles = currentMappingsMap.values().stream()
                .filter(mapping -> !userUuidsToDelete.contains(mapping.getUser().getUuid()))
                .collect(toMap(
                        mapping -> mapping.getUser().getUuid(),
                        mapping -> mapping.getRole().getRoleSeq()
                ));

        // 1-3. Helper 메소드를 재사용하여 "미래 상태" 검증
        if (project.getPrjSeq() == PUBLIC_PROJECT_SEQ) { // 공개 프로젝트
            validatePortalAdminExists(futureUserRoles);
        } else { // 프라이빗 프로젝트
            validateProjectManagerExists(futureUserRoles);
        }

        // 2. 삭제 처리 (DB 삭제 후 ADXP 그룹 권한 제거)
        int successCount = 0;
        int failureCount = 0;

        for (String userUuid : userUuidsToDelete) {
            try {
                // DB를 재조회하는 대신, 미리 가져온 맵에서 매핑 정보 조회 (성능 개선)
                ProjectUserRole mapping = currentMappingsMap.get(userUuid);

                if (mapping == null) {
                    log.warn("프로젝트 구성원 삭제 실패 - 활성 매핑 없음 projectSeq={}, userUuid={}", project.getPrjSeq(), userUuid);
                    failureCount++;
                    continue;
                }

                // 삭제 전 필요한 정보 확보
                Long prjSeq = mapping.getProject() != null ? mapping.getProject().getPrjSeq() : null;
                String memberId = mapping.getUser() != null ? mapping.getUser().getMemberId() : null;

                // 2-1) DB에서 매핑 실제 삭제
                projectUserRoleRepository.delete(mapping);
                log.debug("프로젝트 구성원 매핑 삭제 완료 - projectSeq={}, userUuid={}", project.getPrjSeq(), userUuid);
                successCount++;

                // 2-2) ADXP 그룹 권한 제거 (베스트 에포트) - 프로젝트 관련 모든 그룹에서 제거
                if (prjSeq != null && memberId != null) {
                    try {
                        String groupKeyword = "P" + prjSeq + "_"; // 예: P123_
                        List<String> groupNames = adminAuthService.findGroupNamesByKeyword(groupKeyword);
                        if (groupNames != null && !groupNames.isEmpty()) {
                            for (String groupName : groupNames) {
                                try {
                                    adminAuthService.unassignUserFromGroup(memberId, groupName);
                                } catch (BusinessException e) {
                                    log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                            groupName, e.getMessage());
                                } catch (Exception ex) {
                                    log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                            groupName, ex.getMessage());
                                }
                            }
                        }
                    } catch (BusinessException e) {
                        log.warn("ADXP 그룹 조회/제거 중 오류(무시): prjSeq={}, memberId={}, reason={}", prjSeq, memberId,
                                e.getMessage());
                    } catch (Exception ex) {
                        log.warn("ADXP 그룹 조회/제거 중 오류(무시): prjSeq={}, memberId={}, reason={}", prjSeq, memberId,
                                ex.getMessage());
                    }
                }
            } catch (BusinessException e) {
                log.error("프로젝트 구성원 삭제 중 오류 - projectSeq={}, userUuid={}", project.getPrjSeq(), userUuid, e);
                failureCount++;
            } catch (Exception ex) {
                log.error("프로젝트 구성원 삭제 중 오류 - projectSeq={}, userUuid={}", project.getPrjSeq(), userUuid, ex);
                failureCount++;
            }
        }

        return ProjectUserDeleteRes.of(successCount, failureCount);
    }

    /**
     * 구성원 초대하기 - 프로젝트에 참여하지 않은 구성원 목록 조회
     */
    @Override
    public Page<UserRes> getAvailableUsers(String projectId, InviteUserSearchReq searchReq) {
        log.info("프로젝트 초대 가능 구성원 조회 - projectId={}, searchReq={}", projectId, searchReq);

        Project project = findProjectByUuid(projectId);

        Page<User> users = projectMgmtRepository.getProjectInvitableUsers(
                project.getPrjSeq(),
                searchReq.toPageable(),
                DormantStatus.ACTIVE,
                0,
                searchReq.getFilterType(),
                searchReq.getKeyword()
        );

        return users.map(UserRes::of);
    }

    /**
     * 프로젝트 구성원 역할 할당
     */
    @Override
    @Transactional
    public ProjectUserAssignRes assignProjectUsers(String projectId, ProjectUserAssignReq request) {
        log.info("프로젝트 구성원 역할 할당 요청 - projectId={}, request={}", projectId, request);

        Project project = findProjectByUuid(projectId);

        Map<String, Role> roleCache = new HashMap<>();
        Map<String, User> userCache = new HashMap<>();

        int successCount = 0;
        int failureCount = 0;

        // =====================================================================
        // [A.X API 구성원 초대, 역할 할당 호출 필요]
        //  역할 할당 API List 통째로 보냄.
        //  - 트랜잭션 분리 필요
        // =====================================================================

        for (ProjectUserAssignReq.Assignment assignment : request.getAssignments()) {
            String userUuid = assignment.getUserUuid();
            String roleUuid = assignment.getRoleUuid();

            try {
                Role role = roleCache.computeIfAbsent(roleUuid, uuid -> findRoleByIdAndProject(uuid, project));
                User user = userCache.computeIfAbsent(userUuid, this::findUserByUuid);

                Long prjSeq = project.getPrjSeq();
                String memberId = user.getMemberId();

                // 1) 활성 매핑 조회 시도
                Optional<ProjectUserRole> activeOpt =
                        projectUserRoleRepository.findByProjectPrjSeqAndUserMemberId(prjSeq, memberId);
                ProjectUserRole mapping = null;

                if (activeOpt.isPresent()) {
                    mapping = activeOpt.get();
                }

                // 3) 매핑 없으면 생성 및 ADXP 그룹 권한 부여
                if (mapping == null) {
                    mapping = ProjectUserRole.create(project, role, user);
                    projectUserRoleRepository.save(mapping);

                    // ADXP 그룹 권한 부여 (베스트 에포트)
                    if (prjSeq != null && role != null) {
                        String newGroupName = "P" + prjSeq + "_R" + role.getRoleSeq();
                        try {
                            adminAuthService.assignUserToGroup(memberId, newGroupName);
                            log.info("ADXP 그룹 권한 부여 완료: memberId={}, groupName={}", memberId, newGroupName);
                        } catch (BusinessException e) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, e.getMessage());
                        } catch (Exception ex) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, ex.getMessage());
                        }
                    }

                    successCount++;
                    continue;
                }

                // 4) 기존 매핑 존재: 역할이 다르면 변경 (이전 그룹 제거 후 신규 그룹 부여)
                if (mapping.isDifferentRole(role)) {
                    Role oldRole = mapping.getRole();

                    // a) 이전 역할 그룹 권한 제거 (베스트 에포트)
                    if (prjSeq != null && oldRole != null) {
                        String oldGroupName = "P" + prjSeq + "_R" + oldRole.getRoleSeq();
                        try {
                            adminAuthService.unassignUserFromGroup(memberId, oldGroupName);
                            log.info("ADXP 그룹 권한 제거 완료: memberId={}, groupName={}", memberId, oldGroupName);
                        } catch (BusinessException e) {
                            log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    oldGroupName, e.getMessage());
                        } catch (Exception ex) {
                            log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    oldGroupName, ex.getMessage());
                        }
                    }

                    // b) DB 매핑 역할 변경
                    mapping.changeRole(role);

                    // c) 신규 역할 그룹 권한 부여 (베스트 에포트)
                    if (prjSeq != null && role != null) {
                        String newGroupName = "P" + prjSeq + "_R" + role.getRoleSeq();
                        try {
                            adminAuthService.assignUserToGroup(memberId, newGroupName);
                            log.info("ADXP 그룹 권한 부여 완료: memberId={}, groupName={}", memberId, newGroupName);
                        } catch (BusinessException e) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, e.getMessage());
                        } catch (Exception ex) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, ex.getMessage());
                        }
                    }
                }

                successCount++;
            } catch (BusinessException e) {
                log.warn("프로젝트 구성원 역할 할당 실패 - projectId={}, userUuid={}, reason={}", projectId, userUuid,
                        e.getMessage());
                failureCount++;
            } catch (Exception e) {
                log.error("프로젝트 구성원 역할 할당 중 예상치 못한 오류 - projectId={}, userUuid={}", projectId, userUuid, e);
                failureCount++;
            }
        }

        return ProjectUserAssignRes.of(successCount, failureCount);
    }

    // ================================
    //     Private helper methods
    // ================================

    /**
     * UUID로 프로젝트 조회
     *
     * @param projectUuid 프로젝트 UUID
     * @return 조회된 프로젝트 엔티티
     * @throws BusinessException 프로젝트를 찾을 수 없는 경우
     */
    private Project findProjectByUuid(String projectUuid) {
        return projectMgmtRepository.findByUuid(projectUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
    }

    /**
     * UUID로 사용자 조회
     *
     * @param userUuid 사용자 UUID
     * @return 조회된 사용자 엔티티
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    private User findUserByUuid(String userUuid) {
        return usersRepository2.findByUuid(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 역할 UUID와 프로젝트를 통해 역할 조회
     *
     * @param roleUuid 역할 UUID
     * @param project  프로젝트 엔티티
     * @return 조회된 역할 엔티티
     * @throws BusinessException 역할을 찾을 수 없거나 프로젝트에 속하지 않는 경우
     */
    private Role findRoleByIdAndProject(String roleUuid, Project project) {
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        Long roleProjectSeq = role.getPrjSeq();
        Long requestProjectSeq = project.getPrjSeq();

        // 다른 프로젝트에 속한 역할을 현재 프로젝트에서 조작 불가하게 처리
        // 프로젝트에 속한 역할이지만, 공통 역할(프로젝트 시퀀스가 null)인 경우는 전역으로 허용
        if (roleProjectSeq != null && !roleProjectSeq.equals(requestProjectSeq)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_BELONG_TO_PROJECT);
        }

        return role;
    }

    /**
     * 프로젝트 역할 권한을 요청 목록과 비교해 갱신합니다.
     * <p>
     * 요청된 권한이 이미 존재하면 활성화만 하고, 없으면 신규 매핑을 생성합니다.
     * removeMissing 이 true 인 경우에는 요청 목록에 포함되지 않은 기존 매핑을 모두 삭제합니다.
     */
    private void updateRoleAuthorities(Role role, List<String> requestedAuthorityIds, boolean removeMissing) {
        List<String> normalizedAuthorityIds = Optional.ofNullable(requestedAuthorityIds)
                .orElseGet(Collections::emptyList);

        List<GpoRoleAuthMapMas> existingMappings = gpoRoleAuthMapMasRepository.findByRoleRoleSeq(role.getRoleSeq());

        if (normalizedAuthorityIds.isEmpty()) {
            if (removeMissing && !existingMappings.isEmpty()) {
                gpoRoleAuthMapMasRepository.deleteAllInBatch(existingMappings);
                log.info("모든 역할 권한 매핑 삭제: roleSeq={}, deletedCount={}", role.getRoleSeq(), existingMappings.size());
            }
            return;
        }

        // 1. 요청된 권한 ID 목록의 유효성 검사
        List<GpoAuthorityMas> authorities = gpoAuthorityMasRepository.findAllById(normalizedAuthorityIds);

        if (authorities.size() != normalizedAuthorityIds.size()) {
            List<String> foundIds = authorities.stream()
                    .map(GpoAuthorityMas::getAuthorityId)
                    .toList();

            List<String> notFoundIds = normalizedAuthorityIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            log.warn("역할 권한 매핑 중 유효하지 않은 권한 식별자 발견: 요청={}, 찾을 수 없는 ID={}", normalizedAuthorityIds, notFoundIds);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 권한 아이디가 포함되어 있습니다: " + notFoundIds);
        }

        // 2. 해당 역할에 대한 모든 기존 권한 매핑 정보 조회
        Map<String, GpoRoleAuthMapMas> existingMappingMap = existingMappings.stream()
                .collect(toMap(m -> m.getId().getAuthorityId(), Function.identity()));

        // 3. 요청된 권한 목록을 기준으로 매핑 활성화 또는 신규 생성
        List<GpoAuthorityMas> authoritiesToCreate = new ArrayList<>();

        for (GpoAuthorityMas authority : authorities) {
            GpoRoleAuthMapMas existingMapping = existingMappingMap.get(authority.getAuthorityId());

            if (existingMapping == null) {
                authoritiesToCreate.add(authority);
            } else if (existingMapping.getStatusNm() == RoleAuthorityStatus.INACTIVE) {
                existingMapping.activate();
                log.info("기존 역할 권한 활성화: roleSeq={}, roleName={}, authorityId={}",
                        role.getRoleSeq(), role.getRoleNm(), existingMapping.getId().getAuthorityId());
            }
        }

        if (!authoritiesToCreate.isEmpty()) {
            List<GpoRoleAuthMapMas> newMappings = authoritiesToCreate.stream()
                    .map(authority -> GpoRoleAuthMapMas.create(role, authority))
                    .toList();
            gpoRoleAuthMapMasRepository.saveAll(newMappings);
            log.info("신규 역할 권한 매핑 완료: roleSeq={}, count={}", role.getRoleSeq(), newMappings.size());
        }

        if (removeMissing) {
            Set<String> requestedAuthoritySet = new HashSet<>(normalizedAuthorityIds);
            List<GpoRoleAuthMapMas> mappingsToRemove = existingMappings.stream()
                    .filter(mapping -> !requestedAuthoritySet.contains(mapping.getId().getAuthorityId()))
                    .toList();

            if (!mappingsToRemove.isEmpty()) {
                gpoRoleAuthMapMasRepository.deleteAllInBatch(mappingsToRemove);
                log.info("역할 권한 삭제 완료(하드 삭제): roleSeq={}, deletedCount={}", role.getRoleSeq(), mappingsToRemove.size());
            }
        }
    }

    private String requestAdminAccessToken() {
        String loginUrl = sktaiBaseUrl + "/api/v1/auth/login";

        // application/x-www-form-urlencoded 형식의 요청 바디 생성
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", "admin");
        formData.add("password", "aisnb");
        formData.add("scope", "");
        formData.add("client_id", "default");
        formData.add("client_secret", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("accept", "application/json");

        // HTTP 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // POST 요청 수행
        ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.postForEntity(
                loginUrl,
                requestEntity,
                AccessTokenResponseWithProject.class
        );

        AccessTokenResponseWithProject body = response.getBody();

        if (body == null || body.getAccessToken() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SKT AI 토큰 발급에 실패했습니다.");
        }

        return body.getAccessToken();
    }

    /**
     * 프로젝트 자산 삭제 API 호출
     */
    private void deleteAsset(String assetPath, String accessToken) {
        String url = sktaiBaseUrl + assetPath;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
            log.info("프로젝트 자산 삭제 완료 - url={}", assetPath);
        } catch (RestClientException e) {
            log.warn("프로젝트 자산 삭제 실패(무시) - url={}, reason={}", assetPath, e.getMessage());
        }
    }

    /**
     * SSL 인증서 검증을 우회하는 RestTemplate을 생성
     */
    private RestTemplate createSslBypassingRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // SSL 컨텍스트 초기화
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 기본 SSL 소켓 팩토리 설정
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // 호스트네임 검증 우회
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // RestTemplate 생성 및 반환
            RestTemplate template = new RestTemplate();
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            template.setRequestFactory(requestFactory);

            return template;
        } catch (RuntimeException re) {
            throw new RuntimeException("Failed to create RestTemplate with SSL bypass", re);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with SSL bypass", e);
        }
    }

    /**
     * '프로젝트 관리자' 역할을 조회합니다.
     */
    private Role findProjectManagerRole() {
        return roleRepository.findByRoleSeq(PROJECT_ADMIN_ROLE_SEQ)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    /**
     * '포탈 관리자' 역할을 조회합니다.
     */
    private Role findPortalAdminRole() {
        return roleRepository.findByRoleSeq(PORTAL_ADMIN_ROLE_SEQ)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    /**
     * 요청에 포함된 모든 역할 UUID를 일괄 조회하여 Map으로 반환합니다.
     */
    private Map<String, Role> findRoleMapBy(ProjectUserRoleChangeReq request) {
        List<String> roleUuids = request.getUsers().stream()
                .map(ProjectUserRoleChangeReq.UserRoleChange::getRoleUuid)
                .distinct()
                .toList();

        return roleRepository.findByUuidIn(roleUuids).stream()
                .collect(toMap(Role::getUuid, Function.identity()));
    }

    /**
     * 현재 프로젝트의 활성 사용자-역할 매핑 정보를 조회하여 사용자 UUID를 키로 하는 Map으로 반환합니다.
     */
    private Map<String, ProjectUserRole> getCurrentMappingsAsMap(Project project) {
        return projectUserRoleRepository.findByProjectPrjSeq(project.getPrjSeq())
                .stream()
                .collect(toMap(mapping -> mapping.getUser().getUuid(), Function.identity()));
    }

    /**
     * 역할 변경 요청을 기반으로 변경 후의 역할 상태를 미리 계산하여 Map으로 반환합니다.
     */
    private Map<String, Long> calculateFutureRoles(Project project, ProjectUserRoleChangeReq request,
                                                   Map<String, Role> roleCache,
                                                   Map<String, ProjectUserRole> currentMappings) {
        // 현재 상태를 기반으로 미래 상태 Map 초기화
        Map<String, Long> futureUserRoles = currentMappings.values().stream()
                .collect(toMap(mapping -> mapping.getUser().getUuid(),
                        mapping -> mapping.getRole().getRoleSeq()));

        // 요청된 변경사항을 미래 상태 Map에 적용
        for (ProjectUserRoleChangeReq.UserRoleChange change : request.getUsers()) {
            Role targetRole = Optional.ofNullable(roleCache.get(change.getRoleUuid()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

            // 역할이 해당 프로젝트에 속하는지 검증
            if (targetRole.getPrjSeq() != null && !targetRole.getPrjSeq().equals(project.getPrjSeq())) {
                throw new BusinessException(ErrorCode.ROLE_NOT_BELONG_TO_PROJECT);
            }
            futureUserRoles.put(change.getUserUuid(), targetRole.getRoleSeq());
        }
        return futureUserRoles;
    }

    /**
     * 변경 후의 역할 상태를 기반으로 포탈 관리자가 최소 1명 이상 존재하는지 검증합니다.
     */
    private void validatePortalAdminExists(Map<String, Long> futureUserRoles) {
        Role portalAdminRole = findPortalAdminRole();
        long adminCount = futureUserRoles.values().stream()
                .filter(roleSeq -> roleSeq.equals(portalAdminRole.getRoleSeq()))
                .count();

        if (adminCount < 1) {
            throw new BusinessException(ErrorCode.LAST_PORTAL_ADMIN_CANNOT_CHANGE_ROLE);
        }
    }

    /**
     * 변경 후의 역할 상태를 기반으로 프로젝트 관리자가 최소 1명 이상 존재하는지 검증합니다.
     */
    private void validateProjectManagerExists(Map<String, Long> futureUserRoles) {
        Role projectManagerRole = findProjectManagerRole();
        long pmCount = futureUserRoles.values().stream()
                .filter(roleSeq -> roleSeq.equals(projectManagerRole.getRoleSeq()))
                .count();

        if (pmCount < 1) {
            throw new BusinessException(ErrorCode.PROJECT_NEEDS_AT_LEAST_ONE_MANAGER);
        }
    }

    /**
     * 계산되고 검증된 역할 변경을 실제 데이터베이스에 적용합니다.
     */
    private ProjectUserAssignRes applyRoleChanges(String projectId, ProjectUserRoleChangeReq request,
                                                  Map<String, Role> roleCache,
                                                  Map<String, ProjectUserRole> currentMappings) {
        int successCount = 0;
        int failureCount = 0;

        for (ProjectUserRoleChangeReq.UserRoleChange userRoleChange : request.getUsers()) {
            String userUuid = userRoleChange.getUserUuid();

            try {
                ProjectUserRole mapping = Optional.ofNullable(currentMappings.get(userUuid))
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROJECT_NOT_FOUND));

                Role newRole = roleCache.get(userRoleChange.getRoleUuid());

                if (mapping.isDifferentRole(newRole)) {
                    // ADXP 그룹 권한 변경을 위해 기존 역할 정보, 프로젝트 정보, 사용자 ID 확보
                    Role oldRole = mapping.getRole();
                    Long prjSeq = mapping.getProject() != null ? mapping.getProject().getPrjSeq() : null;
                    String memberId = mapping.getUser().getMemberId();

                    // 1) 기존 역할 그룹 권한 제거 (베스트 에포트)
                    if (prjSeq != null && oldRole != null) {
                        String oldGroupName = "P" + prjSeq + "_R" + oldRole.getRoleSeq();
                        try {
                            adminAuthService.unassignUserFromGroup(memberId, oldGroupName);
                            log.info("ADXP 그룹 권한 제거 완료: memberId={}, groupName={}", memberId, oldGroupName);
                        } catch (BusinessException e) {
                            log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    oldGroupName, e.getMessage());
                        } catch (Exception ex) {
                            log.warn("ADXP 그룹 권한 제거 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    oldGroupName, ex.getMessage());
                        }
                    }

                    // 2) DB 매핑 역할 변경
                    mapping.changeRole(newRole);

                    // 3) 새 역할 그룹 권한 부여 (베스트 에포트)
                    if (prjSeq != null && newRole != null) {
                        String newGroupName = "P" + prjSeq + "_R" + newRole.getRoleSeq();
                        try {
                            adminAuthService.assignUserToGroup(memberId, newGroupName);
                            log.info("ADXP 그룹 권한 부여 완료: memberId={}, groupName={}", memberId, newGroupName);
                        } catch (BusinessException e) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, e.getMessage());
                        } catch (Exception ex) {
                            log.warn("ADXP 그룹 권한 부여 실패(무시): memberId={}, groupName={}, reason={}", memberId,
                                    newGroupName, ex.getMessage());
                        }
                    }
                }
                successCount++;
            } catch (BusinessException e) {
                log.warn("프로젝트 구성원 역할 변경 실패 - projectId={}, userUuid={}, reason={}", projectId, userUuid,
                        e.getMessage());
                failureCount++;
            } catch (Exception e) {
                log.error("프로젝트 구성원 역할 변경 중 오류 - projectId={}, userUuid={}", projectId, userUuid, e);
                failureCount++;
            }
        }
        return ProjectUserAssignRes.of(successCount, failureCount);
    }

    /**
     * 역할별로 API 권한 부여
     */
    public void refreshPolicyAdxpAPI() {
        // 권한 설정은 시간이 소요되므로 비동기 수행
        CompletableFuture.runAsync(() -> {
            gpoAdxpResourceMasRepository.findAll().stream()
                    .collect(groupingBy(GpoAdxpResourceMas::getResourceUrl))
                    .forEach((resourceUrl, resources) -> {
                        List<PolicyRequest> policyRequestList = new ArrayList<>();

                        // 빈 정책을 세팅할 경우 반영이 안되는 ADXP 이슈로 관리정책(admin) 강제 추가
                        policyRequestList.add(PolicyRequest.builder()
                                .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                                .policies(List.of(PolicyItem.builder()
                                        .type("role")
                                        .logic("POSITIVE")
                                        .names(List.of("admin"))
                                        .build()))
                                .logic("POSITIVE")
                                .decisionStrategy("AFFIRMATIVE")
                                .cascade(false)
                                .build());

                        // scope 별로 그룹핑하여 pattern을 수집 (중복 제거를 위해 Set 사용)
                        Map<String, Set<String>> scopeToPatternsMap = new HashMap<>();

                        for (GpoAdxpResourceMas resource : resources) {
                            String scope = resource.getScope();

                            // 해당 scope에 대한 pattern Set을 가져오거나 생성
                            Set<String> patternSet = scopeToPatternsMap.computeIfAbsent(scope, k -> new HashSet<>());

                            // 해당 권한에 대한 역할-권한 매핑 정보 조회 후 pattern 추가
                            gpoRoleAuthMapMasRepository.findAllByAuthority_AuthorityIdAndStatusNm(resource.getAuthorityId(), RoleAuthorityStatus.ACTIVE)
                                    .forEach(item -> {
                                        String patternStr =
                                                "^/.+_R" + String.valueOf(item.getRole().getRoleSeq()).replaceAll("-",
                                                        "\\\\-") + "$";
                                        patternSet.add(patternStr);
                                    });

                            // 기본권한 적용
                            if ("A000001".equals(resource.getAuthorityId())) {
                                roleRepository.findAll().forEach(role -> {
                                    String patternStr =
                                            "^/.+_R" + String.valueOf(role.getRoleSeq()).replaceAll("-",
                                                    "\\\\-") + "$";
                                    patternSet.add(patternStr);
                                });
                            }
                        }

                        // scope별로 하나의 PolicyRequest 생성 (pattern Set을 PolicyItem 리스트로 변환)
                        scopeToPatternsMap.forEach((scope, patterns) -> {
                            List<PolicyItem> policyItems = patterns.stream()
                                    .map(pattern -> PolicyItem.builder()
                                            .type("regex")
                                            .targetClaim("current_group")
                                            .pattern(pattern)
                                            .logic("POSITIVE")
                                            .build())
                                    .toList();

                            policyRequestList.add(PolicyRequest.builder()
                                    .scopes(Arrays.asList(scope))
                                    .policies(policyItems)
                                    .logic("POSITIVE")
                                    .decisionStrategy("AFFIRMATIVE")
                                    .cascade(false)
                                    .build());
                        });

                        try {
                            adminAuthService.updateResourcePolicy(resourceUrl, policyRequestList);
                        } catch (RuntimeException re) {
                            log.warn("ADXP 정책 업데이트 실패 - resourceUrl={}", resourceUrl, re);
                        } catch (Exception e) {
                            log.warn("ADXP 정책 업데이트 실패 - resourceUrl={}", resourceUrl, e);
                        }
                    });
        });
    }

    /**
     * 사용자가 포탈 관리자 역할을 가지고 있는지 확인
     *
     * @param memberId 사용자 ID
     * @return 포탈 관리자 여부
     */
    private boolean isPortalAdmin(String memberId) {
        List<ProjectUserRole> userRoles = projectUserRoleRepository.findByUserMemberId(memberId);
        return userRoles.stream()
                .filter(pur -> pur != null && pur.getRole() != null && pur.getRole().getRoleSeq() != null)
                .anyMatch(pur -> pur.getRole().getRoleSeq().equals(PORTAL_ADMIN_ROLE_SEQ));
    }

    /**
     * 사용자가 특정 프로젝트의 프로젝트 관리자인지 확인
     *
     * @param memberId 사용자 ID
     * @param prjSeq   프로젝트 시퀀스
     * @return 프로젝트 관리자 여부
     */
    private boolean isProjectManager(String memberId, Long prjSeq) {
        List<ProjectUserRole> userRoles = projectUserRoleRepository.findByUserMemberId(memberId);
        return userRoles.stream()
                .anyMatch(pur ->
                        pur.getProject().getPrjSeq().equals(prjSeq) &&
                                pur.getRole().getRoleSeq().equals(PROJECT_ADMIN_ROLE_SEQ)
                );
    }

}
