package com.skax.aiplatform.service.auth.impl;

import com.skax.aiplatform.client.sktai.auth.dto.response.MeResponse;
import com.skax.aiplatform.client.sktai.auth.service.SktaiUserService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.response.AdxpProjectInfoRes;
import com.skax.aiplatform.dto.auth.response.ProjectInfoRes;
import com.skax.aiplatform.dto.auth.response.UserInfoRes;
import com.skax.aiplatform.dto.auth.response.UsersMeRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.repository.admin.GpoAuthorityMasRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.auth.UsersService;
import com.skax.aiplatform.service.home.AlarmService;
import com.skax.aiplatform.service.home.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 관리 서비스 구현체
 *
 * <p>SKTAI 사용자 정보 조회 및 관리 기능을 제공합니다.
 * 외부 SKTAI API와 연동하여 사용자 정보를 처리합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-08-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final SktaiUserService sktaiUserService;
    private final TokenInfo tokenInfo;
    private final ProjectService projectService;
    private final AlarmService alarmService;

    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoAuthorityMasRepository gpoAuthorityMasRepository;
    private final AdminAuthService adminAuthService;
    private final AuthServiceImpl authService;

    @Value("${sktai.api.client-id}")
    private String adxpPrjNm;

    /**
     * 현재 로그인한 사용자 정보를 조회합니다.
     *
     * <p>JWT 토큰을 통해 인증된 현재 사용자의 상세 정보를 SKTAI API에서 조회합니다.</p>
     *
     * @return 현재 사용자 정보
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    @Override
    @Transactional(readOnly = true)
    public MeResponse getMe() {
        try {
            log.info("현재 사용자 정보 조회 요청");
            MeResponse response = sktaiUserService.getCurrentUser();
            if (response == null) {
                log.error("현재 사용자 정보 조회 실패: response가 null입니다");
                throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다.");
            }
            log.info("현재 사용자 정보 조회 성공: userId={}", response.getId());
            return response;
        } catch (RuntimeException re) {
            log.error("현재 사용자 정보 조회 실패: {}", re.getMessage(), re);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다.");
        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다.");
        }
    }

    @Override
    public UsersMeRes getUserInfo() {
        return getUserInfo(tokenInfo.getUserName());
    }

    @Override
    public UsersMeRes getUserInfo(String memberId) {
        // 1. 사용자 프로젝트 목록 조회
        List<ProjectRes> projectList = projectService.getJoinProjectList(memberId);

        List<ProjectInfoRes> projectInfo = new ArrayList<>();
        ProjectInfoRes activeProject = null;

        // 2. 사용자의 프로젝트 목록 및 역할 포함
        if (projectList != null && !projectList.isEmpty()) {
            for (ProjectRes proj : projectList) {
                ProjectInfoRes.ProjectInfoResBuilder builder = ProjectInfoRes.builder()
                        .prjNm(proj.getPrjNm())
                        .prjSeq(proj.getPrjSeq())
                        .prjUuid(proj.getUuid())
                        .prjDesc(proj.getDtlCtnt())
                        .active(false);

                try {
                    String prjSeqStr = proj.getPrjSeq();
                    if (prjSeqStr == null || prjSeqStr.isEmpty()) {
                        log.warn("프로젝트 SEQ가 null 또는 빈 값입니다: username={}, prjNm={}", memberId, proj.getPrjNm());
                    } else {
                        long prjSeq = Long.parseLong(prjSeqStr);
                        // 🔧 Fetch Join을 사용하여 연관 엔티티를 미리 로딩 (LazyInitializationException 방지)
                        gpoPrjuserroleRepository.findByMemberIdAndPrjSeqWithRole(memberId, prjSeq)
                                .ifPresent(pur -> {
                                    if (pur.getRole() != null) {
                                        String adxpGroup = "P%s_R%s".formatted(prjSeqStr, String.valueOf(pur.getRole().getRoleSeq()));
                                        boolean isActive = pur.getStatusNm() == ProjectUserRoleStatus.ACTIVE;
                                        builder.prjRoleNm(pur.getRole().getRoleNm());
                                        builder.prjRoleSeq(String.valueOf(pur.getRole().getRoleSeq()));
                                        builder.active(isActive);
                                        builder.adxpGroupNm(adxpGroup);
                                        builder.adxpGroupPath("/" + adxpGroup);
                                    } else {
                                        builder.prjRoleNm(null);
                                        builder.prjRoleSeq(null);
                                        builder.active(false);
                                        builder.adxpGroupNm(null);
                                        builder.adxpGroupPath(null);
                                    }
                                });
                    }
                } catch (RuntimeException re) {
                    String prjSeqStr = (proj != null && proj.getPrjSeq() != null) ? proj.getPrjSeq() : "null";
                    log.warn("역할 조회 중 오류: username={}, prjSeq={}, error={}", memberId, prjSeqStr, re.getMessage());
                } catch (Exception ex) {
                    String prjSeqStr = (proj != null && proj.getPrjSeq() != null) ? proj.getPrjSeq() : "null";
                    log.warn("역할 조회 중 오류: username={}, prjSeq={}, error={}", memberId, prjSeqStr, ex.getMessage());
                }

                ProjectInfoRes item = builder.build();
                projectInfo.add(item);

                // 활성 프로젝트 별도 키 값으로 전달
                if (item.isActive()) {
                    activeProject = item;
                }
            }

            // 프로젝트 탈퇴/종료 등으로 활성프로젝트가 없을 경우
            if (projectInfo.stream().noneMatch(ProjectInfoRes::isActive)) {
                projectInfo.get(0).setActive(true);
                activeProject = projectInfo.get(0);

                Optional<ProjectUserRole> purOpt = gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(memberId, Long.parseLong(activeProject.getPrjSeq()));

                purOpt.ifPresent(pur -> {
                    pur.updateStatus(ProjectUserRoleStatus.ACTIVE);
                    gpoPrjuserroleRepository.save(pur);

                    // adxp 토큰 exchange
                    authService.exchangeAndSave();
                });
            }
        }

        // 3. 사용자 기본 정보 포함
        UserInfoRes userInfo;
        try {
            userInfo = gpoUsersMasRepository.findByMemberId(memberId)
                    .map(member -> UserInfoRes.builder()
                            .memberId(member.getMemberId())
                            .jkwNm(member.getJkwNm())
                            .retrJkwYn(String.valueOf(member.getRetrJkwYn()))
                            .deptNm(member.getDeptNm())
                            .adxpUserId(member.getUuid())
                            .build())
                    .orElseGet(() -> UserInfoRes.builder().build());
        } catch (RuntimeException re) {
            log.warn("userInfo 조회 중 오류: username={}, error={}", memberId, re.getMessage());
            userInfo = UserInfoRes.builder().build();
        } catch (Exception ex) {
            log.warn("userInfo 조회 중 오류: username={}, error={}", memberId, ex.getMessage());
            userInfo = UserInfoRes.builder().build();
        }

        // 4. ADXP 프로젝트 정보
        String adxpPrjUuid = null;
        try {
            var clientRead = adminAuthService.getProjectByName(adxpPrjNm);
            if (clientRead != null && clientRead.getProject() != null) {
                adxpPrjUuid = clientRead.getProject().getId();
            } else {
                log.warn("ADXP 프로젝트를 찾을 수 없습니다: projectName={}", adxpPrjNm);
            }
        } catch (RuntimeException re) {
            log.warn("ADXP 프로젝트 조회 중 오류 발생: projectName={}, error={}", adxpPrjNm, re.getMessage());
        } catch (Exception e) {
            log.warn("ADXP 프로젝트 조회 중 오류 발생: projectName={}, error={}", adxpPrjNm, e.getMessage());
        }
        
        AdxpProjectInfoRes adxpPrjInfo = AdxpProjectInfoRes.builder()
                .prjNm(adxpPrjNm)
                .prjUuid(adxpPrjUuid)
                .build();

        // 5. 활성 프로젝트가 없는 경우 안전한 처리
        if (activeProject == null) {
            log.warn("활성 프로젝트가 없습니다: memberId={}", memberId);
            return UsersMeRes.builder()
                    .projectList(projectInfo)
                    .activeProject(null)
                    .userInfo(userInfo)
                    .adxpProject(adxpPrjInfo)
                    .menuAuthList(new ArrayList<>())
                    .functionAuthList(new ArrayList<>())
                    .unreadAlarmCount(0)
                    .build();
        }

        // 6. 메뉴 접근권한 정보
        List<String> menuAuthKeysByRoleSeq = new ArrayList<>();
        List<String> functionAuthKeysByRoleSeq = new ArrayList<>();
        
        try {
            if ("admin".equals(memberId)) {
                menuAuthKeysByRoleSeq = gpoAuthorityMasRepository.findAll().stream().map(auth -> auth.getAuthorityId()).toList();
                functionAuthKeysByRoleSeq = gpoAuthorityMasRepository.findAll().stream().map(auth -> auth.getAuthorityId()).toList();
            } else if (activeProject.getPrjRoleSeq() != null) {
                menuAuthKeysByRoleSeq = gpoAuthorityMasRepository.findMenuAuthKeysByRoleSeq(Long.parseLong(activeProject.getPrjRoleSeq()));
                functionAuthKeysByRoleSeq = gpoAuthorityMasRepository.findAuthKeysByRoleSeq(Long.parseLong(activeProject.getPrjRoleSeq()));
            }
        } catch (RuntimeException re) {
            log.warn("권한 조회 중 오류: memberId={}, roleSeq={}, error={}", memberId, activeProject.getPrjRoleSeq(), re.getMessage());
        } catch (Exception ex) {
            log.warn("권한 조회 중 오류: memberId={}, roleSeq={}, error={}", memberId, activeProject.getPrjRoleSeq(), ex.getMessage());
        }

        // 7. 알람뱃지 정보
        List<GpoAlarmsMas> unreadAlarms = alarmService.getNewAlarms(memberId);
        int unreadAlarmCount = (unreadAlarms != null) ? unreadAlarms.size() : 0;

        return UsersMeRes.builder()
                .projectList(projectInfo)
                .activeProject(activeProject)
                .userInfo(userInfo)
                .adxpProject(adxpPrjInfo)
                .menuAuthList(menuAuthKeysByRoleSeq)
                .functionAuthList(functionAuthKeysByRoleSeq)
                .unreadAlarmCount(unreadAlarmCount)
                .build();
    }
}
