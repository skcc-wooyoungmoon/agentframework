package com.skax.aiplatform.controller.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ms.safeon.agent.SafeonAgent;
import com.shinhan.convergence.GwCvgAgent;
import com.shinhan.convergence.dto.CvgIdLoginForm;
import com.shinhan.convergence.exception.CvgApiLoginException;
import com.skax.aiplatform.client.shinhan.ShinhanSwingClient;
import com.skax.aiplatform.client.shinhan.dto.SmsAuthCheckReq;
import com.skax.aiplatform.client.shinhan.dto.SmsAuthCheckRes;
import com.skax.aiplatform.client.shinhan.dto.SmsAuthReq;
import com.skax.aiplatform.client.shinhan.dto.SmsAuthRes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.request.*;
import com.skax.aiplatform.dto.auth.response.JwtTokenRes;
import com.skax.aiplatform.dto.auth.response.SwingSmsRes;
import com.skax.aiplatform.dto.common.request.SmsAuthCheckReqData;
import com.skax.aiplatform.dto.common.request.SmsAuthReqData;
import com.skax.aiplatform.dto.common.request.SwingReqCommon;
import com.skax.aiplatform.entity.GpoGroupcoJkwMas;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.common.GpoGroupcoJkwMasRepository;
import com.skax.aiplatform.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "AuthenticationLogin", description = "인증 로그인 관리 API")
public class LoginController {
    private final ShinhanSwingClient shinhanSwingClient;
    private final AuthService authService;
    private final GpoGroupcoJkwMasRepository gpoGroupcoJkwMasRepository;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final TokenInfo tokenInfo;
    private final GwCvgAgent gwCvgAgent;

    private final Cache<String, List<LocalDateTime>> smsRequestCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private static final String ADXP_PASSWORD = "Shinhan_demo1234!@#$"; // todo: 추후 관리체계 적용 필요 (아키와 상의)

    @Value("${molimate.base-url}")
    private String molimateBaseUrl;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${portal.admin.user-key}")
    private String adminUserKey;

    @Value("${portal.admin.username}")
    private String adminUsername;

    @Value("${portal.admin.password}")
    private String adminPassword;

    @Value("${gw.clientId}")
    private String gwClientId;

    @Value("${gw.clientSecret}")
    private String gwClientSecret;

    /**
     * 사용자 로그인 (JSON 요청)
     *
     * @param loginReq 로그인 요청 (JSON)
     * @param response HTTP 응답
     * @return JWT 토큰 응답
     */
    @PostMapping("/auth/login")
    @Operation(summary = "사용자 로그인 (JSON)", description = "JSON 형식으로 사용자명과 비밀번호를 전송하여 JWT 토큰을 발급받습니다.", security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<JwtTokenRes> login(@Valid @RequestBody LoginReq loginReq,
                                               HttpServletResponse response) throws Exception {

        log.info("JSON 로그인 요청: {}", loginReq.toString());
        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        JwtTokenRes tokenRes = authService.login(loginReq);

        log.info("JSON 로그인 성공: {}", loginReq.getUsername());

        return AxResponseEntity.ok(tokenRes, "로그인이 성공적으로 완료되었습니다.");
    }

    /**
     * OAuth2 로그인 (Form-data 요청)
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @param response HTTP 응답
     * @return JWT 토큰 응답 (OAuth2 표준 형식)
     */
    @PostMapping(value = "/auth/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "OAuth2 로그인 (Form-data)", description = "Form-data 형식으로 사용자명과 비밀번호를 전송하여 JWT 토큰을 발급받습니다. OAuth2PasswordBearer 전용 엔드포인트입니다.", security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OAuth2 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public JwtTokenRes oauthLogin(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  HttpServletResponse response) throws Exception {

        log.info("OAuth2 로그인 요청: username={}", username);

        // LoginReq 객체로 변환
        LoginReq loginReq = new LoginReq();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        JwtTokenRes tokenRes = authService.login(loginReq);

        log.info("OAuth2 로그인 성공: {}", username);

        // OAuth2 표준에 맞게 직접 토큰 객체 반환 (AxResponseEntity 래핑 없이)
        return tokenRes;
    }

    @PostMapping("/auth/login-molimate")
    @Operation(summary = "사용자 로그인", description = "사용자명과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "201", description = "사용자 신규등록 후 로그인 성공"),
            @ApiResponse(responseCode = "403", description = "퇴사한 사용자 로그인 불가"),
            @ApiResponse(responseCode = "404", description = "인사원장에 사용자정보 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public AxResponseEntity<JwtTokenRes> molimateLogin(@Valid @RequestBody MolimateLoginReq loginReq,
                                                       HttpServletResponse response) throws Exception {
        log.info("몰리메이트 로그인: {}", loginReq.getUsername());

        if (adminUserKey.equals(loginReq.getUsername())) {
            loginReq.setUsername(adminUsername);

            LoginReq login = new LoginReq();
            login.setUsername(adminUsername);
            login.setPassword(adminPassword);

            // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
            // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
            JwtTokenRes tokenRes = authService.login(login);

            return AxResponseEntity.ok(tokenRes, "로그인 성공");
        }

        // 1) 인사원장(HR) 사용자 확인
        GpoGroupcoJkwMas member = gpoGroupcoJkwMasRepository.findByMemberId(loginReq.getUsername()).orElse(null);

        if (member == null) {
            return AxResponseEntity.warning(null, ErrorCode.USER_NOT_FOUND.getCode());
        }

        // 2) 퇴사자/비활성 사용자 차단
        if (member.getRetrJkwYn() != null && member.getRetrJkwYn() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "퇴사한 사용자는 로그인할 수 없습니다.");
        }

        // 3) 장기 미사용자 차단 (1년 이상 미로그인)
        gpoUsersMasRepository.findByMemberId(loginReq.getUsername()).ifPresent(gpoUser -> {
            LocalDateTime lastLoginAt = gpoUser.getLstLoginAt();
            if (lastLoginAt != null) {
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
                if (lastLoginAt.isBefore(oneYearAgo)) {
                    throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE_LONG_TERM, "마지막 로그인일 이후 1년이 경과하여 로그인이 " +
                            "불가합니다. 계정 활성화를 위해 포탈 관리자에게 문의해주세요.");
                }
            }
        });

        // 테스트 환경 우회 처리
        if (List.of("edev", "elocal", "local").contains(activeProfile) && (loginReq.getUsername().startsWith("SGO"))) {
            log.info("테스트환경 계정 로그인 바이패스: {}", loginReq.getUsername());
        } else {
            if (loginReq.getNewJoinYn() == null || !loginReq.getNewJoinYn().equals("Y")) {
                // 4) 몰리메이트 연계
                SafeonAgent sa = new SafeonAgent(molimateBaseUrl);
                boolean ret = sa.requestSA(member.getHpNo().replace("-", ""), "00", "모바일 직원인증", "생성형 AI 플랫폼 로그인", "생성형 AI" +
                        " 플랫폼 로그인 인증건이 있습니다.", 120, 5);

                if (!ret) {
                    return AxResponseEntity.warning(null, ErrorCode.SSO_LOGIN_FAILED.getCode());
                } else {
                    log.info("몰리메이트 로그인 연동 성공");
                }
            }
        }

        // 5) 회원가입 여부 확인 후 처리
        boolean isRegistered = authService.existsUserById(loginReq.getUsername());
        boolean newlyRegistered = false;
        if (!isRegistered) {
            if (loginReq.getNewJoinYn() == null || !loginReq.getNewJoinYn().equals("Y")) {
                return AxResponseEntity.deleted(null, "사용자 신규등록 필요");
            } else {
                RegisterReq registerReq = new RegisterReq();
                registerReq.setUserNo(loginReq.getUsername());
                registerReq.setPassword(ADXP_PASSWORD);
                registerReq.setEmail(loginReq.getUsername() + "@shinhan.co.kr"); // todo: 인사원장에 메일주소 추가 필요
                registerReq.setDept(member.getDeptNm());
                registerReq.setDisplayName(member.getJkwNm());
                registerReq.setPosition(member.getJkwiNm());
                registerReq.setHpNo(member.getHpNo());

                try {
                    Object registeredUser = authService.registerUser(registerReq);
                    newlyRegistered = true;
                    log.info("사용자 등록 성공: {} > {}", loginReq.getUsername(), registeredUser);
                } catch (BusinessException e) {
                    log.error("사용자 등록 실패 (Business Exception) : {} - {}", loginReq.getUsername(), e.getMessage());
                    throw e;
                } catch (RuntimeException e) {
                    log.error("사용자 등록 실패 (Runtime Exception) : {} - {}", loginReq.getUsername(), e.getMessage(), e);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류", e);
                } catch (Exception e) {
                    log.error("사용자 등록 실패: {} - {}", loginReq.getUsername(), e.getMessage());
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "ADXP 서비스 오류");
                }
            }
        }

        // 6) 로그인 처리 및 응답 코드 분기
        LoginReq login = new LoginReq();
        login.setUsername(loginReq.getUsername());
        // 기본 비밀번호 사용
        login.setPassword(ADXP_PASSWORD);

        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        JwtTokenRes tokenRes = authService.login(login);

        log.info("몰리메이트 로그인 성공: {} (신규등록 여부: {})", loginReq.getUsername(), newlyRegistered);
        Thread.sleep(1_000); // 키클락 이슈로 인해 강제 타임아웃

        if (newlyRegistered) {
            return AxResponseEntity.created(tokenRes, "사용자 신규등록 후 로그인 성공");
        }
        return AxResponseEntity.ok(tokenRes, "로그인 성공");
    }

    @PostMapping("/auth/swing-sms")
    public AxResponseEntity<SwingSmsRes> swingSmsRequest(@RequestBody SwingSmsReq swingSmsReq) {
        // Username Rate Limiting
        smsRequestCache.asMap().compute(swingSmsReq.getUsername(), (key, timestamps) -> {
            if (timestamps == null) {
                timestamps = new ArrayList<>();
            }
            LocalDateTime now = LocalDateTime.now();
            timestamps.removeIf(t -> t.isBefore(now.minusMinutes(5)));

            if (timestamps.size() >= 5) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "5분 내 5회 초과 요청");
            }
            timestamps.add(now);
            return timestamps;
        });

        SmsAuthRes smsAuthRes;

        // 1) 인사원장(HR) 사용자 확인
        GpoGroupcoJkwMas member = gpoGroupcoJkwMasRepository.findByMemberId(swingSmsReq.getUsername()).orElse(null);

        if (member == null) {
            return AxResponseEntity.warning(null, ErrorCode.USER_NOT_FOUND.getCode());
        }

        // swing 연동 불가능한 환경 우회처리
        if (List.of("edev", "elocal", "local").contains(activeProfile)) {
            log.info("SMS인증 바이패스: {}", swingSmsReq.getUsername());

            return AxResponseEntity.ok(SwingSmsRes.builder()
                    .authEventId("temp-auth-event-id")
                    .authRdnVdTm("2023-01-01T00:00:00+09:00")
                    .build(), "로그인 성공");
        }

        try {
            smsAuthRes = shinhanSwingClient.smsAuthCodeRequest(SmsAuthReq.builder()
                    .common(SwingReqCommon.builder()
                            .clientId(gwClientId)
                            .clientSecret(gwClientSecret)
                            .companyCode("SH")
                            .employeeNo(swingSmsReq.getUsername())
                            .build())
                    .data(SmsAuthReqData.builder()
                            .authRequestMethod("SMS")
                            .build())
                    .build());
        } catch (RuntimeException re) {
            throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, "서버 오류", re);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, "서버 오류", e);
        }

        if (smsAuthRes.getCommon().getResultCode() != 200) {
            throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, smsAuthRes.getCommon().getErrorMessage());
        }

        String checksum = DigestUtils.sha256Hex(smsAuthRes.getData().getAuthEventId() + swingSmsReq.getUsername() + "rk").substring(0, 6);

        return AxResponseEntity.ok(SwingSmsRes.builder()
                .authEventId(String.format("%s.%s", smsAuthRes.getData().getAuthEventId(), checksum))
                .authRdnVdTm(smsAuthRes.getData().getAuthRdnVdTm())
                .build(), "로그인 성공");
    }

    @PostMapping("/auth/swing-sms-check")
    public AxResponseEntity<JwtTokenRes> swingSmsCheck(@RequestBody SwingSmsReq swingSmsReq) throws Exception {
        // 1) 인사원장(HR) 사용자 확인
        GpoGroupcoJkwMas member = gpoGroupcoJkwMasRepository.findByMemberId(swingSmsReq.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "인사원장에 사용자정보 없음"));

        // 2) 퇴사자/비활성 사용자 차단
        if (member.getRetrJkwYn() != null && member.getRetrJkwYn() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "퇴사한 사용자는 로그인할 수 없습니다.");
        }

        // 3) 장기 미사용자 차단 (1년 이상 미로그인)
        gpoUsersMasRepository.findByMemberId(swingSmsReq.getUsername()).ifPresent(gpoUser -> {
            LocalDateTime lastLoginAt = gpoUser.getLstLoginAt();
            if (lastLoginAt != null) {
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
                if (lastLoginAt.isBefore(oneYearAgo)) {
                    throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE_LONG_TERM, "로그인 불가 안내");
                }
            }
        });

        // 4) Swing SMS 인증연계 연계
        SmsAuthCheckRes smsAuthCheckRes;

        // swing 연동 불가능한 환경 우회처리
        if (List.of("edev", "elocal", "local").contains(activeProfile)) {
            log.info("SMS인증 바이패스: {}", swingSmsReq.getUsername());
        } else {
            String authEventId;
            String checksum;

            if (swingSmsReq.getAuthEventId() != null && swingSmsReq.getAuthEventId().contains(".")) {
                authEventId = swingSmsReq.getAuthEventId().split("\\.")[0];
                checksum = swingSmsReq.getAuthEventId().split("\\.")[1];

                String calculatedChecksum = DigestUtils.sha256Hex(authEventId + swingSmsReq.getUsername() + "rk").substring(0, 6);

                if (!calculatedChecksum.equals(checksum)) {
                    throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "SMS 인증 오류");
                }
            } else {
                throw new BusinessException(ErrorCode.AUTH_LOGIN_FAILED, "SMS 인증 오류");
            }

            if (swingSmsReq.getNewJoinYn() == null || !swingSmsReq.getNewJoinYn().equals("Y")) {
                try {
                    smsAuthCheckRes = shinhanSwingClient.smsAuthCodeCheckRequest(SmsAuthCheckReq.builder()
                            .common(SwingReqCommon.builder()
                                    .clientId(gwClientId)
                                    .clientSecret(gwClientSecret)
                                    .companyCode("SH")
                                    .employeeNo(swingSmsReq.getUsername())
                                    .build())
                            .data(SmsAuthCheckReqData.builder()
                                    .authEventId(authEventId)
                                    .randomNumber(swingSmsReq.getRandomNumber())
                                    .build())
                            .build());
                } catch (RuntimeException re) {
                    return AxResponseEntity.warning(null, ErrorCode.SMS_AUTH_FAILED.getCode());
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, "서버 오류", e);
                }

                if (smsAuthCheckRes.getCommon().getResultCode() != 200) {
                    String errorCode = smsAuthCheckRes.getCommon().getErrorCode();

                    // 인증번호 불일치 에러
                    if (errorCode != null && errorCode.equals("ECAU028")) {
                        return AxResponseEntity.warning(null, ErrorCode.SMS_AUTH_FAILED.getCode());
                    }

                    throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, smsAuthCheckRes.getCommon().getErrorMessage());
                }
            }
        }

        log.info("Swing SMS 인증 성공");

        // 5) 회원가입 여부 확인 후 처리
        boolean isRegistered = authService.existsUserById(swingSmsReq.getUsername());
        boolean newlyRegistered = false;
        if (!isRegistered) {
            if (swingSmsReq.getNewJoinYn() == null || !swingSmsReq.getNewJoinYn().equals("Y")) {
                return AxResponseEntity.deleted(null, "사용자 신규등록 필요");
            } else {
                RegisterReq registerReq = new RegisterReq();
                registerReq.setUserNo(swingSmsReq.getUsername());
                registerReq.setPassword(ADXP_PASSWORD);
                registerReq.setEmail(swingSmsReq.getUsername() + "@shinhan.com"); // 추후 메일주소 로직은 제거 예정
                registerReq.setDept(member.getDeptNm());
                registerReq.setDisplayName(member.getJkwNm());
                registerReq.setPosition(member.getJkwiNm());
                registerReq.setHpNo(member.getHpNo());

                try {
                    Object registeredUser = authService.registerUser(registerReq);
                    newlyRegistered = true;
                    log.info("사용자 등록 성공: {} > {}", swingSmsReq.getUsername(), registeredUser);
                } catch (BusinessException e) {
                    log.error("사용자 등록 실패 (Business Exception): {} - {}", registerReq.getUserNo(), e.getMessage());
                    throw e;

                } catch (RuntimeException e) {
                    log.error("사용자 등록 실패 (Runtime Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류", e);

                } catch (Exception e) {
                    log.error("사용자 등록 실패 (Checked Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "사용자 등록 중 오류가 발생했습니다.", e);
                }
            }
        }

        // 6) 로그인 처리 및 응답 코드 분기
        LoginReq login = new LoginReq();
        login.setUsername(swingSmsReq.getUsername());
        // 기본 비밀번호 사용
        login.setPassword(ADXP_PASSWORD);

        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        JwtTokenRes tokenRes = authService.login(login);

        log.info("Swing 로그인 성공: {} (신규등록 여부: {})", swingSmsReq.getUsername(), newlyRegistered);
        Thread.sleep(1_000); // 키클락 이슈로 인해 강제 타임아웃

        if (newlyRegistered) {
            return AxResponseEntity.created(tokenRes, "사용자 신규등록 후 로그인 성공");
        }
        return AxResponseEntity.ok(tokenRes, "로그인 성공");
    }

    @PostMapping("/auth/login-swing")
    @Operation(summary = "사용자 로그인", description = "사용자명과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "201", description = "사용자 신규등록 후 로그인 성공"),
            @ApiResponse(responseCode = "403", description = "퇴사한 사용자 로그인 불가"),
            @ApiResponse(responseCode = "404", description = "인사원장에 사용자정보 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public AxResponseEntity<JwtTokenRes> swingLogin(@Valid @RequestBody SwingLoginReq loginReq,
                                                    HttpServletResponse response) throws Exception {

        // 1) 인사원장(HR) 사용자 확인
        GpoGroupcoJkwMas member = gpoGroupcoJkwMasRepository.findByMemberId(loginReq.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "인사원장에 사용자정보 없음"));

        // 2) 퇴사자/비활성 사용자 차단
        if (member.getRetrJkwYn() != null && member.getRetrJkwYn() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "퇴사한 사용자는 로그인할 수 없습니다.");
        }

        // 3) 장기 미사용자 차단 (1년 이상 미로그인)
        gpoUsersMasRepository.findByMemberId(loginReq.getUsername()).ifPresent(gpoUser -> {
            LocalDateTime lastLoginAt = gpoUser.getLstLoginAt();
            if (lastLoginAt != null) {
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
                if (lastLoginAt.isBefore(oneYearAgo)) {
                    throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE_LONG_TERM, "로그인 불가 안내");
                }
            }
        });

        // 4) Swing (id/pw) 연계
        if (loginReq.getSsoAuthCode() == null && !adminUserKey.equals(loginReq.getPassword())) {
            try {
                gwCvgAgent.idpwAuth(new CvgIdLoginForm("SH", loginReq.getUsername(), loginReq.getPassword()));
            } catch (CvgApiLoginException loginException) {
                throw new BusinessException(ErrorCode.SSO_LOGIN_FAILED, loginException.getMessage());
            }
        }

        log.info("Swing 로그인 연동 성공");

        // 5) 회원가입 여부 확인 후 처리
        boolean isRegistered = authService.existsUserById(loginReq.getUsername());
        boolean newlyRegistered = false;
        if (!isRegistered) {
            if (loginReq.getNewJoinYn() == null || !loginReq.getNewJoinYn().equals("Y")) {
                return AxResponseEntity.deleted(null, "사용자 신규등록 필요");
            } else {
                RegisterReq registerReq = new RegisterReq();
                registerReq.setUserNo(loginReq.getUsername());
                registerReq.setPassword(ADXP_PASSWORD);
                registerReq.setEmail(loginReq.getUsername() + "@shinhan.com"); // 추후 메일주소 로직은 제거 예정
                registerReq.setDept(member.getDeptNm());
                registerReq.setDisplayName(member.getJkwNm());
                registerReq.setPosition(member.getJkwiNm());
                registerReq.setHpNo(member.getHpNo());

                try {
                    Object registeredUser = authService.registerUser(registerReq);
                    newlyRegistered = true;
                    log.info("사용자 등록 성공: {} > {}", loginReq.getUsername(), registeredUser);
                } catch (BusinessException e) {
                    log.error("사용자 등록 실패 (Business Exception): {} - {}", registerReq.getUserNo(), e.getMessage());
                    throw e;

                } catch (RuntimeException e) {
                    log.error("사용자 등록 실패 (Runtime Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류", e);

                } catch (Exception e) {
                    log.error("사용자 등록 실패 (Checked Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "사용자 등록 중 오류가 발생했습니다.", e);
                }
            }
        }

        // 6) 로그인 처리 및 응답 코드 분기
        LoginReq login = new LoginReq();
        login.setUsername(loginReq.getUsername());
        // 기본 비밀번호 사용
        login.setPassword(ADXP_PASSWORD);

        // 포탈 로그인 성공 후, adxp 토큰 획득을 위한 로그인 처리
        // 로그인 횟수제한 등의 제어는 몰리메이트/Swing 자체적으로 처리하므로 포탈은 별도 제어하지 않음
        JwtTokenRes tokenRes = authService.login(login);

        log.info("Swing 로그인 성공: {} (신규등록 여부: {})", loginReq.getUsername(), newlyRegistered);
        Thread.sleep(1_000); // 키클락 이슈로 인해 강제 타임아웃

        if (newlyRegistered) {
            return AxResponseEntity.created(tokenRes, "사용자 신규등록 후 로그인 성공");
        }
        return AxResponseEntity.ok(tokenRes, "로그인 성공");
    }

    /**
     * 로그아웃
     *
     * @param request HTTP 요청 객체 (토큰 추출용)
     * @return 로그아웃 응답
     */
    @PostMapping("/auth/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃하고 토큰을 무효화합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public AxResponseEntity<Void> logout(HttpServletRequest request) throws Exception {
        log.info("로그아웃 요청");
        if (tokenInfo == null || tokenInfo.getUserName() == null) {
            log.warn("로그아웃 실패: 토큰 정보가 없습니다.");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        authService.logout(tokenInfo.getUserName());

        log.info("로그아웃 성공");

        return AxResponseEntity.deleted("로그아웃이 성공적으로 완료되었습니다.");
    }

    /**
     * 사용자 등록
     *
     * @param registerReq 등록 요청
     * @return 등록 결과
     */
    @PostMapping("/auth/register")
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    public AxResponseEntity<?> registerUser(@Valid @RequestBody RegisterReq registerReq) {
        log.info("사용자 등록 요청: {}", registerReq.getUserNo());

        try {
            Object registeredUser = authService.registerUser(registerReq);
            log.info("사용자 등록 성공: {}", registerReq.getUserNo());

            return AxResponseEntity.created(registeredUser, "사용자가 성공적으로 등록되었습니다.");
        } catch (BusinessException e) {
            log.error("사용자 등록 실패 (Business Exception): {} - {}", registerReq.getUserNo(), e.getMessage());
            throw e;

        } catch (RuntimeException e) {
            log.error("사용자 등록 실패 (Runtime Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류", e);

        } catch (Exception e) {
            log.error("사용자 등록 실패 (Checked Exception): {} - {}", registerReq.getUserNo(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "사용자 등록 중 오류가 발생했습니다.", e);
        }
    }

}
