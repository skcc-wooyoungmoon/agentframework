package com.skax.aiplatform.service.auth.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.request.RegisterUserPayload;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.client.sktai.auth.dto.response.UserRepresentation;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.auth.service.SktaiUserService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.security.JwtTokenProvider;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.TokenCacheData;
import com.skax.aiplatform.dto.auth.request.LoginReq;
import com.skax.aiplatform.dto.auth.request.RefreshTokenReq;
import com.skax.aiplatform.dto.auth.request.RegisterReq;
import com.skax.aiplatform.dto.auth.response.JwtTokenRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.repository.home.GpoRolesRepository;
import com.skax.aiplatform.repository.home.GpoUsersRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.auth.AuthService;
import com.skax.aiplatform.service.auth.TokenCacheService;
import com.skax.aiplatform.service.home.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 인증 서비스 구현체
 *
 * <p>사용자 로그인, 토큰 발급, 토큰 갱신 등의 인증 관련 비즈니스 로직을 처리합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-02
 */
@Slf4j
@Service("authService")
public class AuthServiceImpl implements AuthService {

    // JWT 관련 의존성
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenCacheService tokenCacheService;
    private final TokenInfo tokenInfo;

    // Repository 의존성
    private final GpoUsersMasRepository usersRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final GpoUsersRepository gpoUsersRepository;
    private final GpoProjectsRepository gpoProjectsRepository;
    private final GpoRolesRepository gpoRolesRepository;

    // 외부 API 클라이언트 의존성 (지연 로딩)
    private final SktaiAuthService sktaiAuthService;
    private final SktaiUserService sktaiUserService;

    // 계정 초기화 및 초기권한 부여 의존성
    private final AdminAuthService adminAuthService;

    // 사용자 프로젝트/역할 관리
    private final ProjectService projectService;

    @Value("${sktai.api.client-id}")
    private String sktaiApiClientId;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    // 생성자
    public AuthServiceImpl(
            JwtTokenProvider jwtTokenProvider,
            GpoUsersMasRepository usersRepository,
            SktaiAuthService sktaiAuthService,
            SktaiUserService sktaiUserService,
            AdminAuthService adminAuthService,
            TokenCacheService tokenCacheService,
            ProjectService projectService,
            GpoPrjuserroleRepository gpoPrjuserroleRepository,
            GpoUsersRepository gpoUsersRepository,
            GpoProjectsRepository gpoProjectsRepository,
            GpoRolesRepository gpoRolesRepository,
            TokenInfo tokenInfo) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.usersRepository = usersRepository;
        this.sktaiAuthService = sktaiAuthService;
        this.sktaiUserService = sktaiUserService;
        this.adminAuthService = adminAuthService;
        this.tokenCacheService = tokenCacheService;
        this.projectService = projectService;
        this.gpoPrjuserroleRepository = gpoPrjuserroleRepository;
        this.gpoUsersRepository = gpoUsersRepository;
        this.gpoProjectsRepository = gpoProjectsRepository;
        this.gpoRolesRepository = gpoRolesRepository;
        this.tokenInfo = tokenInfo;
    }

    /**
     * 사용자 로그인
     *
     * @param loginReq 로그인 요청
     * @return JWT 토큰 응답
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JwtTokenRes login(LoginReq loginReq) {
        log.info("사용자 로그인 시도: {}", loginReq.getUsername());

        try {
            // 1. 사용자 업데이트
            GpoUsersMas user = registerOrUpdateUser(loginReq);

            // 2. SKTAI 외부 인증 서비스 호출
            AccessTokenResponseWithProject sktaiResponse = authenticateWithSktai(loginReq);

            log.info("SKTAI 초기 인증 완료 - accessToken 획득");

            // 3. 액세스토큰 저장
            updateOrCreateToken(user, sktaiResponse);

            // 4. 사용자 프로젝트 목록 조회
            List<ProjectRes> projectList = projectService.getJoinProjectList(loginReq.getUsername());

            // 5. 프로젝트 선택 상태 관리
            if (projectList != null && !projectList.isEmpty()) {
                try {
                    // 5-1. 모든 프로젝트의 ProjectUserRole 조회 및 ACTIVE 상태 확인
                    List<ProjectUserRole> allProjectUserRoles = new ArrayList<>();
                    List<ProjectUserRole> activeRoles = new ArrayList<>();
                    ProjectUserRole publicProjectRole = null;

                    for (ProjectRes proj : projectList) {
                        try {
                            long prjSeq = Long.parseLong(proj.getPrjSeq());
                            Optional<ProjectUserRole> purOpt = gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(loginReq.getUsername(), prjSeq);
                            if (purOpt.isPresent()) {
                                ProjectUserRole pur = purOpt.get();
                                allProjectUserRoles.add(pur);
                                if (pur.getStatusNm() == ProjectUserRoleStatus.ACTIVE) {
                                    activeRoles.add(pur);
                                }
                                if (prjSeq == -999L) {
                                    publicProjectRole = pur;
                                }
                            }
                        } catch (NumberFormatException ex) {
                            // 숫자 파싱 오류
                            log.warn("프로젝트 조회 중 오류 (숫자 형식 오류): username={}, prjSeq={}, error={}", loginReq.getUsername(), proj.getPrjSeq(), ex.getMessage());
                        } catch (DataAccessException ex) {
                            // 데이터베이스 접근 오류
                            log.warn("프로젝트 조회 중 오류 (데이터베이스 오류): username={}, prjSeq={}, error={}", loginReq.getUsername(), proj.getPrjSeq(), ex.getMessage());
                        } catch (Exception ex) {
                            // 기타 예상치 못한 예외
                            log.warn("프로젝트 조회 중 오류: username={}, prjSeq={}, error={}", loginReq.getUsername(), proj.getPrjSeq(), ex.getMessage());
                        }
                    }

                    // 5-2. ACTIVE 상태가 여러 개인 경우 첫 번째만 유지하고 나머지는 INACTIVE
                    if (activeRoles.size() > 1) {
                        log.info("다수의 ACTIVE 프로젝트 발견: username={}, count={}", loginReq.getUsername(), activeRoles.size());
                        for (int i = 1; i < activeRoles.size(); i++) {
                            ProjectUserRole pur = activeRoles.get(i);
                            pur.updateStatus(ProjectUserRoleStatus.INACTIVE);
                            gpoPrjuserroleRepository.save(pur);
                            log.info("ACTIVE 상태 제거: username={}, prjSeq={}", loginReq.getUsername(), pur.getProject().getPrjSeq());
                        }
                    }
                    // 5-3. ACTIVE 상태가 없는 경우 prjSeq == -999 프로젝트를 ACTIVE로 설정
                    else if (activeRoles.isEmpty()) {
                        if (publicProjectRole != null) {
                            publicProjectRole.updateStatus(ProjectUserRoleStatus.ACTIVE);
                            gpoPrjuserroleRepository.save(publicProjectRole);
                            log.info("공개 프로젝트 ACTIVE 설정: username={}, prjSeq=-999", loginReq.getUsername());
                        } else {
                            log.warn("ACTIVE 프로젝트가 없으며 공개 프로젝트(-999)도 찾을 수 없음: username={}", loginReq.getUsername());
                        }
                    }
                } catch (BusinessException ex) {
                    // 비즈니스 예외는 그대로 전파
                    log.error("프로젝트 상태 관리 중 오류 (BusinessException): username={}, error={}", loginReq.getUsername(), ex.getMessage(), ex);
                    throw ex;
                } catch (DataAccessException ex) {
                    // 데이터베이스 접근 오류는 경고만 기록
                    log.warn("프로젝트 상태 관리 중 오류 (데이터베이스 오류): username={}, error={}", loginReq.getUsername(), ex.getMessage());
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // 잘못된 인자 예외는 경고만 기록
                    log.warn("프로젝트 상태 관리 중 오류 (잘못된 인자): username={}, error={}", loginReq.getUsername(), ex.getMessage());
                } catch (Exception ex) {
                    // 기타 예상치 못한 예외
                    log.error("프로젝트 상태 관리 중 오류: username={}, error={}", loginReq.getUsername(), ex.getMessage(), ex);
                }
            }

            // 6. 토큰 생성용 claim 생성
            Map<String, Object> extraClaims = new HashMap<>();

            // 6-1. 사용자 기본 정보 포함
            try {
                extraClaims.put("userInfo", Collections.EMPTY_MAP);
                usersRepository.findByMemberId(loginReq.getUsername()).ifPresent(member -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("memberId", member.getMemberId());
                    userInfo.put("jkwNm", member.getJkwNm());
                    userInfo.put("deptNm", member.getDeptNm());

                    extraClaims.put("userInfo", userInfo);
                });
            } catch (DataAccessException ex) {
                // 데이터베이스 접근 오류는 경고만 기록
                log.warn("userInfo 조회 중 오류 (데이터베이스 오류): username={}, error={}", loginReq.getUsername(), ex.getMessage());
            } catch (IllegalArgumentException | NullPointerException ex) {
                // 잘못된 인자 예외는 경고만 기록
                log.warn("userInfo 조회 중 오류 (잘못된 인자): username={}, error={}", loginReq.getUsername(), ex.getMessage());
            } catch (Exception ex) {
                // 기타 예상치 못한 예외
                log.warn("userInfo 조회 중 오류: username={}, error={}", loginReq.getUsername(), ex.getMessage());
            }

            // 7. JWT 토큰 생성
            JwtTokenRes jwtTokenRes = createInternalJwtTokens(loginReq.getUsername(), extraClaims);

            log.info("사용자 로그인 성공: {}", loginReq.getUsername());

            // 8. SKTAI 교환된 토큰 정보와 함께 응답 반환
            return jwtTokenRes;
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("사용자 로그인 실패 (BusinessException): username={}, error={}", loginReq.getUsername(), e.getMessage(), e);
            throw e;
        } catch (BadCredentialsException e) {
            // 인증 실패 예외
            log.error("사용자 로그인 실패 (BadCredentialsException): username={}, error={}", loginReq.getUsername(), e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("사용자 로그인 실패 (잘못된 인자): username={}, error={}", loginReq.getUsername(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("사용자 로그인 실패 (데이터베이스 오류): username={}, error={}", loginReq.getUsername(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "데이터베이스 오류로 인해 로그인에 실패했습니다.");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("사용자 로그인 실패 (예상치 못한 오류): username={}, error={}", loginReq.getUsername(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED);
        }
    }

    /**
     * SKTAI 토큰 교환 및 DB 저장 (기본 그룹)
     *
     * <p>기본 공개 프로젝트(-999) 기준으로 프로젝트별 메소드에 위임하여 토큰을 교환하고 저장합니다.</p>
     *
     * @return 교환된 토큰 응답
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessTokenResponseWithProject exchangeAndSave() {
        // 25.11.10 pjt - 로그아웃 이후 재로그인시 마지막 선택한 프로젝트가 유지되도록 수정
        ProjectUserRole projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndStatusNm(tokenInfo.getUserName(), ProjectUserRoleStatus.ACTIVE);

        // 함수오버로딩 호출부분으로 재귀호출이 아님
        // 시큐어코딩 검출 프로그램의 오탐 부분
        return exchangeAndSave(projectUserRole == null ? -999L : projectUserRole.getProject().getPrjSeq());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessTokenResponseWithProject exchangeAndSave(Long prjSeq) {
        String username = tokenInfo.getUserName();
        log.info("SKTAI 토큰 교환 시작: username={}, prjSeq={}", username, prjSeq);

        GpoUsersMas usersMas = usersRepository.findByMemberId(username).orElseThrow();
        ProjectUserRole projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(username, prjSeq).orElseThrow();
        String groupName = "P%s_R%s".formatted(
                String.valueOf(projectUserRole.getProject().getPrjSeq()),
                String.valueOf(projectUserRole.getRole().getRoleSeq())
        );

        log.info("토큰 교환 대상 그룹 확인: username={}, prjSeq={}, group={}", username, prjSeq, groupName);

        try {
            // 1. SKTAI 토큰 교환 API 호출하여 exchanged token 획득
            AccessTokenResponseWithProject exchangedToken =
                    sktaiAuthService.exchangeToken(sktaiApiClientId, "/" + groupName);

            log.info("exchangeToken 호출 성공: group={}", groupName);

            // 2. 교환된 토큰 정보 저장/업데이트
            updateOrCreateToken(usersMas, exchangedToken);

            // 3. 프로젝트 역할 활성/비활성 상태 업데이트
            try {
                List<ProjectRes> projectList = projectService.getJoinProjectList(username);
                if (projectList != null && !projectList.isEmpty()) {
                    for (ProjectRes proj : projectList) {
                        try {
                            long pSeq = Long.parseLong(proj.getPrjSeq());
                            gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(username, pSeq)
                                    .ifPresent(pur -> {
                                        ProjectUserRoleStatus newStatus = (pSeq == prjSeq)
                                                ? ProjectUserRoleStatus.ACTIVE
                                                : ProjectUserRoleStatus.INACTIVE;
                                        if (pur.getStatusNm() != newStatus) {
                                            pur.updateStatus(newStatus);
                                            gpoPrjuserroleRepository.save(pur);
                                        }
                                    });
                        } catch (NumberFormatException ex) {
                            // 숫자 파싱 오류
                            log.warn("프로젝트 상태 업데이트 중 오류 (숫자 형식 오류): username={}, prjSeq={}, error={}", username, proj.getPrjSeq(), ex.getMessage());
                        } catch (DataAccessException ex) {
                            // 데이터베이스 접근 오류
                            log.warn("프로젝트 상태 업데이트 중 오류 (데이터베이스 오류): username={}, prjSeq={}, error={}", username, proj.getPrjSeq(), ex.getMessage());
                        } catch (IllegalArgumentException | NullPointerException ex) {
                            // 잘못된 인자 예외
                            log.warn("프로젝트 상태 업데이트 중 오류 (잘못된 인자): username={}, prjSeq={}, error={}", username, proj.getPrjSeq(), ex.getMessage());
                        } catch (Exception ex) {
                            // 기타 예상치 못한 예외
                            log.warn("프로젝트 상태 업데이트 중 오류: username={}, prjSeq={}, error={}", username, proj.getPrjSeq(), ex.getMessage());
                        }
                    }
                }
            } catch (BusinessException statusEx) {
                // 비즈니스 예외는 경고만 기록 (일괄 업데이트이므로 하나 실패해도 계속 진행)
                log.warn("프로젝트 역할 상태 일괄 업데이트 중 오류 (BusinessException): username={}, error={}", username, statusEx.getMessage());
            } catch (DataAccessException statusEx) {
                // 데이터베이스 접근 오류는 경고만 기록
                log.warn("프로젝트 역할 상태 일괄 업데이트 중 오류 (데이터베이스 오류): username={}, error={}", username, statusEx.getMessage());
            } catch (Exception statusEx) {
                // 기타 예상치 못한 예외
                log.warn("프로젝트 역할 상태 일괄 업데이트 중 오류: username={}, error={}", username, statusEx.getMessage());
            }

            log.info("SKTAI 토큰 교환 및 저장 완료: username={}, prjSeq={}, group={}, expiresIn={}, refreshExpiresIn={}",
                    username, prjSeq, groupName, exchangedToken.getExpiresIn(), exchangedToken.getRefreshExpiresIn());

            return exchangedToken;
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("SKTAI 토큰 교환/저장 실패 (BusinessException): username={}, prjSeq={}, group={}, error={}",
                    username, prjSeq, groupName, e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("SKTAI 토큰 교환/저장 실패 (잘못된 인자): username={}, prjSeq={}, group={}, error={}",
                    username, prjSeq, groupName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "토큰 교환 및 저장 중 오류가 발생했습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("SKTAI 토큰 교환/저장 실패 (데이터베이스 오류): username={}, prjSeq={}, group={}, error={}",
                    username, prjSeq, groupName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "토큰 교환 및 저장 중 데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("SKTAI 토큰 교환/저장 실패 (예상치 못한 오류): username={}, prjSeq={}, group={}, error={}",
                    username, prjSeq, groupName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "토큰 교환 및 저장 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(RegisterReq registerReq) {
        try {
            log.info("현재 사용자 정보 조회 요청");

            RegisterUserPayload registerUserPayload = RegisterUserPayload.builder()
                    .username(registerReq.getUserNo())
                    .password(registerReq.getPassword())
                    .email(registerReq.getEmail())
                    .firstName("")
                    .lastName("")
                    .build();

            boolean existsedUser = usersRepository.existsByMemberId(registerReq.getUserNo());

            if (!existsedUser) {
                try {
                    // 관리자 권한으로 계정 삭제
                    adminAuthService.deleteUser(registerReq.getUserNo());
                } catch (BusinessException ex) {
                    // 계정이 없을 수 있으므로 무시
                    log.debug("계정 삭제 시도 실패 (무시): userId={}, error={}", registerReq.getUserNo(), ex.getMessage());
                } catch (Exception ex) {
                    // 기타 예외도 무시 (계정이 없을 수 있음)
                    log.debug("계정 삭제 시도 실패 (무시): userId={}, error={}", registerReq.getUserNo(), ex.getMessage());
                }
            }

            // 계정 신규 생성
            UserRepresentation response = sktaiUserService.registerUser(registerUserPayload);

            log.info("현재 사용자 정보 조회 성공: userId={}", response.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.convertValue(response, Map.class);

            // 사용자 등록 성공 시, 로컬 users 테이블에 신규 사용자 추가
            try {
                if (!existsedUser) {
                    GpoUsersMas newUser = GpoUsersMas.builder()
                            .memberId(registerReq.getUserNo())
                            .uuid(response.getId())
                            .jkwNm(registerReq.getDisplayName())
                            .userPassword(registerReq.getPassword())
                            .deptNm(registerReq.getDept())
                            .jkgpNm(registerReq.getPosition())
                            .hpNo(registerReq.getHpNo())
                            .retrJkwYn(0) // (0: 재직, 1: 퇴직)
                            .dmcStatus(DormantStatus.ACTIVE)
                            .lstLoginAt(LocalDateTime.now())
                            .build();
                    usersRepository.save(newUser);
                    log.info("로컬 users 테이블에 신규 사용자 저장 완료: {}", registerReq.getUserNo());
                } else {
                    log.info("로컬 users 테이블에 이미 존재하는 사용자: {}", registerReq.getUserNo());
                }
            } catch (DataAccessException ex) {
                // 데이터베이스 접근 오류
                log.error("로컬 users 테이블 저장 중 오류 발생 (데이터베이스 오류): {} - {}", registerReq.getUserNo(), ex.getMessage(), ex);
            } catch (IllegalArgumentException | NullPointerException ex) {
                // 잘못된 인자 예외
                log.error("로컬 users 테이블 저장 중 오류 발생 (잘못된 인자): {} - {}", registerReq.getUserNo(), ex.getMessage(), ex);
            } catch (Exception ex) {
                // 기타 예상치 못한 예외
                log.error("로컬 users 테이블 저장 중 오류 발생: {} - {}", registerReq.getUserNo(), ex.getMessage(), ex);
            }

            // 기본 그룹(public, 일반사용자 부여)
            adminAuthService.assignUserToGroup(registerReq.getUserNo(), "P-999_R-195"); // -999:공개프로젝트 고유 seq, -195:일반사용자 고유 seq

            // 기본 프로젝트 추가 (public)
            try {
                Long defaultPrjSeq = -999L;// -999:공개프로젝트 고유 seq
                Long defaultRoleSeq = -195L;// -195:일반사용자 고유 seq

                gpoPrjuserroleRepository.deleteByUser_MemberId(registerReq.getUserNo());
                gpoPrjuserroleRepository.save(ProjectUserRole.create(
                        gpoProjectsRepository.findById(defaultPrjSeq).get(),
                        gpoRolesRepository.findById(defaultRoleSeq).get(),
                        gpoUsersRepository.findByMemberId(registerReq.getUserNo()).get()));

                log.info("기본 프로젝트/역할 매핑 저장 완료: memberId={}, prjSeq={}, roleSeq={}",
                        registerReq.getUserNo(), defaultPrjSeq, defaultRoleSeq);
            } catch (BusinessException ex) {
                // 비즈니스 예외 (예: 프로젝트 또는 역할을 찾을 수 없음)
                log.error("기본 프로젝트 매핑 처리 중 오류 (BusinessException): memberId={}, error={}", registerReq.getUserNo(), ex.getMessage(), ex);
            } catch (DataAccessException ex) {
                // 데이터베이스 접근 오류
                log.error("기본 프로젝트 매핑 처리 중 오류 (데이터베이스 오류): memberId={}, error={}", registerReq.getUserNo(), ex.getMessage(), ex);
            } catch (IllegalArgumentException | NullPointerException ex) {
                // 잘못된 인자 예외
                log.error("기본 프로젝트 매핑 처리 중 오류 (잘못된 인자): memberId={}, error={}", registerReq.getUserNo(), ex.getMessage(), ex);
            } catch (Exception ex) {
                // 기타 예상치 못한 예외
                log.error("기본 프로젝트 매핑 처리 중 오류: memberId={}, error={}", registerReq.getUserNo(), ex.getMessage(), ex);
            }

            /* adxp 관리자 권한 추가 (관리자 정책이 모두 적용되기 전까지 임시 로직) */
            /* 25.11.12 pjt - 관리자 역할 부여 제거 */
            /* 25.11.13 pjt - 내부망은 유지 -> 다시 제거 */
            // if (List.of("dev", "local").contains(activeProfile)) {
            //     adminAuthService.getUserRoleAvailable(registerReq.getUserNo()).getData().stream()
            //             .filter(role -> role.getRole().getName().equals("admin"))
            //             .findAny()
            //             .ifPresent(item -> adminAuthService.updateUserRoleMappingsFromAvailable(registerReq.getUserNo(), List.of(item)));
            // }

            return ResponseEntity.ok(responseMap);
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("사용자 등록 실패 (BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("사용자 등록 실패 (잘못된 인자): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("사용자 등록 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("사용자 등록 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다.");
        }
    }

    /**
     * 사용자 등록 또는 업데이트
     *
     * @param loginReq 로그인 요청 정보
     * @return 등록/업데이트된 사용자 정보
     */
    private GpoUsersMas registerOrUpdateUser(LoginReq loginReq) {
        log.debug("사용자 등록/업데이트 처리: {}", loginReq.getUsername());

        Optional<GpoUsersMas> existingUser = usersRepository.findByMemberId(loginReq.getUsername());

        if (existingUser.isPresent()) {
            // 기존 사용자 업데이트
            GpoUsersMas user = existingUser.get();

            user.setLstLoginAt(LocalDateTime.now());
            log.debug("기존 사용자 정보 업데이트: {}", loginReq.getUsername());

            return usersRepository.save(user);
        } else {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 사용자 정보를 조회할 수 없습니다.");
        }
    }

    /**
     * SKTAI 외부 인증 서비스 호출
     *
     * @param loginReq 로그인 요청 정보
     * @return SKTAI 인증 응답
     */
    private AccessTokenResponseWithProject authenticateWithSktai(LoginReq loginReq) {
        log.debug("SKTAI 외부 인증 서비스 호출: {}", loginReq.getUsername());

        loginReq.setClientId(sktaiApiClientId);
        loginReq.setGrantType("password");

        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        return sktaiAuthService.login(loginReq);
    }

    /**
     * 토큰 정보 저장 또는 업데이트
     *
     * @param sktaiResponse SKTAI 인증 응답
     */
    private void updateOrCreateToken(GpoUsersMas user, AccessTokenResponseWithProject sktaiResponse) {
        log.debug("토큰 정보 저장/업데이트: {}", user.getMemberId());

        TokenCacheData token = createNewToken(user, sktaiResponse);

        tokenCacheService.cacheToken(token);
    }

    /**
     * 신규 토큰 생성
     *
     * @param sktaiResponse SKTAI 인증 응답
     * @return 생성된 토큰
     */
    private TokenCacheData createNewToken(GpoUsersMas user, AccessTokenResponseWithProject sktaiResponse) {
        return TokenCacheData.builder()
                .uuid(user.getUuid())
                .memberId(user.getMemberId())
                .accessToken(sktaiResponse.getAccessToken())
                .refreshToken(sktaiResponse.getRefreshToken())
                .expAt(LocalDateTime.now().plusSeconds(sktaiResponse.getExpiresIn()))
                .issueAt(LocalDateTime.now())
                .refreshTokenExpAt(LocalDateTime.now().plusSeconds(sktaiResponse.getRefreshExpiresIn()))
                .tokenExpTimes(Long.valueOf(sktaiResponse.getExpiresIn()))
                .refreshTokenExpTimes(Long.valueOf(sktaiResponse.getRefreshExpiresIn()))
                .build();
    }


    private JwtTokenRes createInternalJwtTokens(String username, Map<String, Object> extraClaims) {
        log.debug("내부 JWT 토큰 생성: {}", username);

        // JWT 토큰 생성 (추가 클레임 포함 가능)
        String accessToken = (extraClaims == null)
                ? jwtTokenProvider.createAccessToken(username, "ROLE_USER")
                : jwtTokenProvider.createAccessToken(username, "ROLE_USER", extraClaims);
        String refreshToken = jwtTokenProvider.createRefreshToken(username, "ROLE_USER", extraClaims);

        log.debug("JWT 토큰 생성 완료: username={}", username);

        return JwtTokenRes.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidityInMilliseconds()
        );
    }

    /*
    public JwtTokenRes login(LoginReq loginReq) throws Exception {
        log.info("사용자 로그인 시도: {}", loginReq.getUsername());

        ResponseEntity<String> returnString = sktAxAuthClient.postLogin(loginReq);
        JwtTokenRes response = null;
        if (returnString.getStatusCode().is2xxSuccessful() && returnString.getBody() != null) {
            // JSON 응답을 AuthLoginRes 객체로 변환
            response = objectMapper.readValue(
                    returnString.getBody(),
                    JwtTokenRes.class
            );

            // user 정보를 조회하고 있으면 token 만 입력 함
            Optional<Users> usersObj = usersRepository.findByUsername(loginReq.getUsername());
            if (usersObj.isPresent()) {
                // 기존 사용자가 존재하는 경우 - 데이터 업데이트
                Users existingUser = usersObj.get();
                existingUser.setUpdateAt(LocalDateTime.now());
                existingUser.setUpdateBy(loginReq.getUsername());
                usersRepository.save(existingUser);
            } else {
                // 신규 사용자인 경우 - 새로운 사용자 등록
                Users newUser = Users.builder()
                        .username(loginReq.getUsername())
                        .password(passwordEncoder.encode(loginReq.getPassword()))
                        .build();
                usersRepository.save(newUser);
                log.info("신규 사용자 등록 완료: {}", newUser.getUsername());
            }
        }

        Optional<Token> tokenObj = tokenRepository.findByUsername(loginReq.getUsername());
        if (tokenObj.isPresent()) {
            // 기존 토큰이 존재하는 경우 - 토큰 정보 업데이트
            Token existingToken = tokenObj.get();
            existingToken.setAccessToken(response.getAccessToken());
            existingToken.setRefreshToken(response.getRefreshToken());
            // existingToken.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn() / 1000));
            existingToken.setExpiresAt(LocalDateTime.now().plusHours(9).plusSeconds(response.getExpiresIn() / 1000));
           existingToken.setExpiresIn(response.getExpiresIn());
            existingToken.setRefreshExpiresIn(response.getRefreshExpiresIn());
            tokenRepository.save(existingToken);
            log.info("토큰 정보 업데이트 완료: {}", loginReq.getUsername());
        } else {
            // 새 토큰 생성
            Optional<Users> userOpt = usersRepository.findByUsername(loginReq.getUsername());
            if (userOpt.isPresent()) {
                Token newToken = Token.builder()
                        .username(loginReq.getUsername())
                        .accessToken(response.getAccessToken())
                        .refreshToken(response.getRefreshToken())
                        // .expiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn() / 1000))
                        .expiresAt(LocalDateTime.now().plusHours(9).plusSeconds(response.getExpiresIn() / 1000))
                        .expiresIn(response.getExpiresIn())
                        .refreshExpiresIn(response.getRefreshExpiresIn())
                        .build();
                tokenRepository.save(newToken);
                log.info("새 토큰 저장 완료: {}", loginReq.getUsername());
            } else {
                log.error("토큰 저장 실패: 사용자를 찾을 수 없음 {}", loginReq.getUsername());
                throw new Exception("사용자를 찾을 수 없습니다");
            }
        }

        // 권한 정보 조회
        List<String> authorities = userAuthorities.getOrDefault(loginReq.getUsername(), List.of("ROLE_USER"));
        String authoritiesStr = String.join(", ", authorities);

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(loginReq.getUsername(), authoritiesStr);
        String refreshToken = jwtTokenProvider.createRefreshToken(loginReq.getUsername());

        log.info("사용자 로그인 성공: {}, 권한: {}", loginReq.getUsername(), authoritiesStr);

        return JwtTokenRes.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidityInMilliseconds()
        ).withTemporaryField(response.getAccessToken(), response.getRefreshToken());
    }
    */

    /**
     * Access Token 갱신
     *
     * @param refreshTokenReq 토큰 갱신 요청
     * @return 새로운 JWT 토큰 응답
     */
    @Override
    public JwtTokenRes refreshToken(RefreshTokenReq refreshTokenReq) {
        log.info("토큰 갱신 요청");

        try {
            // 기존 Refresh Token의 사용자명 추출
            String username = jwtTokenProvider.getUsernameFromToken(refreshTokenReq.getRefreshToken());

            Map<String, Object> extraClaims = new HashMap<>();

            extraClaims.put("userInfo", Collections.EMPTY_MAP);
            usersRepository.findByMemberId(username).ifPresent(member -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("memberId", member.getMemberId());
                userInfo.put("jkwNm", member.getJkwNm());
                userInfo.put("deptNm", member.getDeptNm());

                extraClaims.put("userInfo", userInfo);
            });

            // 새로운 Access Token 생성
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshTokenReq.getRefreshToken(), "ROLE_USER", extraClaims);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username, "ROLE_USER", extraClaims);

            log.info("토큰 갱신 성공: {}", username);

            return JwtTokenRes.of(
                    newAccessToken,
                    newRefreshToken,
                    jwtTokenProvider.getAccessTokenValidityInMilliseconds()
            );

        } catch (BadCredentialsException e) {
            // 인증 실패 예외는 그대로 전파
            log.error("토큰 갱신 실패 (BadCredentialsException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외 (예: 토큰 형식 오류)
            log.error("토큰 갱신 실패 (잘못된 인자): {}", e.getMessage(), e);
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("토큰 갱신 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다");
        }
    }

    /**
     * 로그아웃 (토큰 무효화)
     *
     * @param username Access Token
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String username) {
        log.info("사용자 로그아웃: {}", username);
        tokenCacheService.removeTokenFromCache(username);
        // sktaiAuthService.logout(username);
        return;
    }

    @Override
    public boolean existsUserById(String username) {
        try {
            return usersRepository.existsByMemberId(username);
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류 발생 시 false 반환
            log.error("사용자 존재 여부 확인 실패 (데이터베이스 오류): {} - {}", username, e.getMessage(), e);
            return false;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자 예외 발생 시 false 반환
            log.error("사용자 존재 여부 확인 실패 (잘못된 인자): {} - {}", username, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // 기타 예상치 못한 예외 발생 시 false 반환
            log.error("사용자 존재 여부 확인 실패 (예상치 못한 오류): {} - {}", username, e.getMessage(), e);
            return false;
        }
    }
}
