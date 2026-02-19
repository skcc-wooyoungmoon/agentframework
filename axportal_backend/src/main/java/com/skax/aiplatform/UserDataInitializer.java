package com.skax.aiplatform;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.common.enums.YNStatus;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.RoleScope;
import com.skax.aiplatform.entity.role.RoleStatus;
import com.skax.aiplatform.entity.role.RoleType;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.repository.home.GpoRolesRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.admin.ProjectMgmtService;
import com.skax.aiplatform.service.auth.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application startup data initializer.
 * - Ensures an 'admin' account exists in gpo_users_mas table.
 * - Ensures a default public project exists in gpo_projects_mas table.
 * - Ensures default roles exist in gpo_roles_mas table.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataInitializer {

    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoProjectsRepository gpoProjectsRepository;
    private final GpoRolesRepository gpoRolesRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final TokenCacheService tokenCacheService;

    private final AdminAuthService adminAuthService;
    private final ProjectMgmtService projectMgmtService;

    private static final String ADMIN_ID = "admin";
    private static final long DEFAULT_PROJECT_SEQ = -999L;
    private static final String DEFAULT_GROUP_PREFIX = "P-999_";
    private static final String GROUP_PORTAL_ADMIN = "P-999_R-199"; // 포탈 관리자
    private static final String GROUP_INFRA_ADMIN = "P-999_R-198"; // 인프라 관리자
    private static final String GROUP_INFO_ADMIN = "P-999_R-197"; // 정보보호 관리자
    private static final String GROUP_TECH_ADMIN = "P-999_R-196"; // Tech 운영 관리자
    private static final String GROUP_GENERAL_USER = "P-999_R-195"; // 일반 사용자

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        try {
            tokenCacheService.removeTokenFromCache(ADMIN_ID);

            // 관리자 유저정보 생성 (gpo_users_mas)
            if (!gpoUsersMasRepository.existsByMemberId(ADMIN_ID)) {
                String adxpUserId = adminAuthService.getCurrentUser().getId();
                gpoUsersMasRepository.save(GpoUsersMas.builder()
                        .memberId(ADMIN_ID)
                        .uuid(adxpUserId)
                        .jkwNm("시스템")
                        .userPassword("aisnb")
                        .deptNm("시스템관리팀")
                        .jkgpNm("관리자")
                        .hpNo("010-0000-0000")
                        .retrJkwYn(0)
                        .dmcStatus(DormantStatus.ACTIVE)
                        .lstLoginAt(LocalDateTime.now())
                        .build());

                log.info("@@@ [UserDataInitializer] Seeded default admin user '{}'.", ADMIN_ID);
            }

            // ADXP 기본 그룹 생성
            if (adminAuthService.findGroupNamesByKeyword(DEFAULT_GROUP_PREFIX).isEmpty()) {
                try {
                    String groupPortalAdminId = adminAuthService.createGroup(GROUP_PORTAL_ADMIN);
                    adminAuthService.createGroup(GROUP_INFRA_ADMIN);
                    adminAuthService.createGroup(GROUP_INFO_ADMIN);
                    adminAuthService.createGroup(GROUP_TECH_ADMIN);
                    adminAuthService.createGroup(GROUP_GENERAL_USER);

                    // admin 계정에 포탈 관리자 권한 부여
                    String adxpUserId = adminAuthService.getCurrentUser().getId();
                    adminAuthService.assignUserToGroupWithAdxpId(adxpUserId, groupPortalAdminId);

                    log.info("@@@ [UserDataInitializer] Create ADXP groups and assigned user {} to Portal Admin Group", adxpUserId);
                } catch (BusinessException be) {
                    // ADXP 그룹 생성 실패는 경고만 기록 (초기화 단계에서 일부 실패해도 계속 진행)
                    log.warn("@@@ [UserDataInitializer] Failed to create ADXP groups or assign user to group: {}", be.getMessage(), be);
                } catch (RuntimeException re) {
                    // ADXP 그룹 생성 실패는 경고만 기록 (초기화 단계에서 일부 실패해도 계속 진행)
                    log.warn("@@@ [UserDataInitializer] Failed to create ADXP groups or assign user to group: {}", re.getMessage(), re);
                } catch (Exception e) {
                    log.warn("@@@ [UserDataInitializer] Failed to create ADXP groups or assign user to group: {}", e.getMessage(), e);
                }
            }

            // 기본 프로젝트 생성 (gpo_projects_mas)
            if (!gpoProjectsRepository.existsByPrjSeq(DEFAULT_PROJECT_SEQ)) {
                gpoProjectsRepository.insertProjectWithSeq(
                        DEFAULT_PROJECT_SEQ,
                        "project-public",
                        "Public",
                        "공개 프로젝트",
                        ProjectStatus.ONGOING.name(),
                        YNStatus.N.name(),
                        null,
                        ADMIN_ID,
                        ADMIN_ID);
                log.info("@@@ [UserDataInitializer] Seeded default public project with prjSeq='{}'.", DEFAULT_PROJECT_SEQ);
            }

            // 기본 역할 생성 (gpo_roles_mas)
            seedRoleIfAbsent(-299L, null, "role-uuid-project-admin-004", RoleScope.PROJECT, "프로젝트 관리자", "프로젝트를 관리하는 관리자");
            seedRoleIfAbsent(-298L, null, "role-uuid-developer-005", RoleScope.PROJECT, "개발자", "프로젝트 개발을 담당하는 개발자");
            seedRoleIfAbsent(-297L, null, "role-uuid-tester-006", RoleScope.PROJECT, "테스터", "프로젝트 테스트를 담당하는 테스터");

            seedRoleIfAbsent(-199L, DEFAULT_PROJECT_SEQ, "role-uuid-portal-admin-001", RoleScope.PORTAL, "포탈 관리자", "포탈 전체를 관리하는 최고 관리자");
            seedRoleIfAbsent(-198L, DEFAULT_PROJECT_SEQ, "role-uuid-infra-admin-002", RoleScope.PORTAL, "인프라 관리자", "시스템 인프라를 관리하는 관리자");
            seedRoleIfAbsent(-197L, DEFAULT_PROJECT_SEQ, "role-uuid-info-admin-003", RoleScope.PORTAL, "정보보호 관리자", "정보보호 관리자");
            seedRoleIfAbsent(-196L, DEFAULT_PROJECT_SEQ, "role-uuid-tech-admin-004", RoleScope.PORTAL, "Tech 운영 관리자", "Tech 운영 관리자");
            seedRoleIfAbsent(-195L, DEFAULT_PROJECT_SEQ, "role-uuid-general-user-005", RoleScope.PORTAL, "일반 사용자", "포탈의 기본 사용자");

            // 관리자 프로젝트-역할 매핑 생성 (GPO_PRJUSERROLE_MAP_MAS)
            if (gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(ADMIN_ID, DEFAULT_PROJECT_SEQ).isEmpty()) {
                try {
                    gpoPrjuserroleRepository.insertMapping(
                            -1L,
                            "ACTIVE",
                            ADMIN_ID,
                            DEFAULT_PROJECT_SEQ,
                            -199L, // 포탈 관리자
                            ADMIN_ID,
                            ADMIN_ID
                    );
                    log.info("@@@ [UserDataInitializer] Seeded admin Project-Role mapping: memberId='{}', prjSeq={}, roleSeq={}", ADMIN_ID, DEFAULT_PROJECT_SEQ, -199L);
                } catch (BusinessException be) {
                    // 관리자 프로젝트-역할 매핑 생성 실패는 경고만 기록 (초기화 단계에서 일부 실패해도 계속 진행)
                    log.warn("@@@ [UserDataInitializer] Failed to seed admin Project-Role mapping: memberId='{}', prjSeq={}, roleSeq={}, error={}", 
                            ADMIN_ID, DEFAULT_PROJECT_SEQ, -199L, be.getMessage(), be);
                } catch (RuntimeException re) {
                    // 관리자 프로젝트-역할 매핑 생성 실패는 경고만 기록 (초기화 단계에서 일부 실패해도 계속 진행)
                    log.warn("@@@ [UserDataInitializer] Failed to seed admin Project-Role mapping: memberId='{}', prjSeq={}, roleSeq={}, error={}",
                            ADMIN_ID, DEFAULT_PROJECT_SEQ, -199L, re.getMessage(), re);
                } catch (Exception e) {
                    // 관리자 프로젝트-역할 매핑 생성 실패는 경고만 기록 (초기화 단계에서 일부 실패해도 계속 진행)
                    log.warn("@@@ [UserDataInitializer] Failed to seed admin Project-Role mapping: memberId='{}', prjSeq={}, roleSeq={}, error={}",
                            ADMIN_ID, DEFAULT_PROJECT_SEQ, -199L, e.getMessage(), e);
                }
            }

            // 내부망 배포환경에서만 권한 초기화 코드 수행
            if (List.of("dev", "prod").contains(activeProfile)) {
                projectMgmtService.refreshPolicyAdxpAPI();
            }
        } catch (BusinessException be) {
            log.error("@@@ [UserDataInitializer] Failed to seed initial data: {}", be.getMessage(), be);
        } catch (RuntimeException re) {
            log.error("@@@ [UserDataInitializer] Failed to seed initial data: {}", re.getMessage(), re);
        } catch (Exception e) {
            log.error("@@@ [UserDataInitializer] Failed to seed initial data: {}", e.getMessage(), e);
        }
    }

    private void seedRoleIfAbsent(Long roleSeq, Long prjSeq, String uuid, RoleScope scope, String name, String desc) {
        if (!gpoRolesRepository.existsByRoleSeq(roleSeq)) {
            gpoRolesRepository.insertRoleWithSeq(
                    roleSeq,
                    prjSeq,
                    uuid,
                    scope.name(),
                    name,
                    desc,
                    RoleStatus.ACTIVE.name(),
                    RoleType.DEFAULT.name(),
                    ADMIN_ID,
                    ADMIN_ID
            );

            log.info("@@@ [UserDataInitializer] Seeded default role roleSeq={}, name='{}', scope={}", roleSeq, name, scope);
        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void processAdxpAuthInitilize() {
        try {
            projectMgmtService.refreshPolicyAdxpAPI();
        } catch (RuntimeException re) {
            log.error("@@@ [UserDataInitializer] Failed to refresh policyAdxpAPI: {}", re.getMessage(), re);
        } catch (Exception e) {
            log.error("@@@ [UserDataInitializer] Failed to refresh policyAdxpAPI: {}", e.getMessage(), e);
        }
    }
}
