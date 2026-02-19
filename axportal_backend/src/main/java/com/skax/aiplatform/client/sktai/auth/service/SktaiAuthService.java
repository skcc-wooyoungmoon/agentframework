package com.skax.aiplatform.client.sktai.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.auth.SktaiAuthClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.DefaultPolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.DefaultRoleRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.DynamicPolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.LoginAccessTokenRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.PermissionResourceRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.SidebarMenuRequest;
import com.skax.aiplatform.client.sktai.auth.dto.request.SystemLoginPayload;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.client.sktai.auth.dto.response.DefaultPolicyResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.DefaultRoleResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.DynamicPolicyResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.PermissionResourceResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.SidebarMenuListResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.SidebarMenuResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.SsoCallbackResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.SsoLoginResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.auth.request.LoginReq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Auth 서비스
 *
 * <p>
 * SKTAI 인증 API 호출을 담당하는 서비스 클래스입니다.
 * 인증, 정책 관리, 역할 관리, 권한 관리 등의 비즈니스 로직을 제공합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 2.0
 * @since 2025-08-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAuthService {

    private final SktaiAuthClient sktaiAuthClient;

    @Value("${sktai.api.client-id}")
    private String sktaiApiClientId;

    // ================================
    // 인증 및 로그인 API
    // ================================

    /**
     * OAuth2 패스워드 기반 로그인
     *
     * @param request 로그인 요청
     * @return 액세스 토큰 응답
     */
    public AccessTokenResponseWithProject loginAccessToken(LoginAccessTokenRequest request) {
        try {
            log.info("SKTAI password 로그인 요청: username={}", request.getUsername());
            AccessTokenResponseWithProject response = sktaiAuthClient.loginAccessToken(request);
            log.info("SKTAI password 로그인 성공: username={}", request.getUsername());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI password 로그인 실패 (비즈니스 오류): username={}", request.getUsername(), e);
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("SKTAI password 로그인 실패 (잘못된 인자): username={}", request.getUsername(), e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "로그인 정보가 올바르지 않습니다.");
        } catch (RuntimeException e) {
            log.error("SKTAI password 로그인 실패 (런타임 오류): username={}", request.getUsername(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "OAuth2 로그인에 실패했습니다.");
        } catch (Exception e) {
            log.error("SKTAI password 로그인 실패 (예상치 못한 오류): username={}", request.getUsername(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "OAuth2 로그인 중 예상치 못한 오류가 발생했습니다.");
        }
    }

    /**
     * OAuth2 패스워드 기반 로그인 (편의 메서드)
     *
     * @return 액세스 토큰 응답
     */
    public AccessTokenResponseWithProject login(LoginReq loginReq) {
        LoginAccessTokenRequest request = LoginAccessTokenRequest.builder()
                .grantType(loginReq.getGrantType())
                .username(loginReq.getUsername())
                .password(loginReq.getPassword())
                .scope("")
                .clientId(sktaiApiClientId)
                .clientSecret("")
                .build();
        return loginAccessToken(request);
    }

    /**
     * SSO 로그인
     *
     * @return SSO 로그인 응답
     */
    public SsoLoginResponse ssoLogin() {
        try {
            log.info("SKTAI SSO 로그인 요청");
            SsoLoginResponse response = sktaiAuthClient.ssoLogin();
            log.info("SKTAI SSO 로그인 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI SSO 로그인 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI SSO 로그인 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SSO 로그인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SSO 콜백
     *
     * @return SSO 콜백 응답
     */
    public SsoCallbackResponse ssoCallback() {
        try {
            log.info("SKTAI SSO 콜백 요청");
            SsoCallbackResponse response = sktaiAuthClient.ssoCallback();
            log.info("SKTAI SSO 콜백 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI SSO 콜백 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI SSO 콜백 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SSO 콜백에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SAML IDP 메타데이터 조회
     *
     * @return SAML 메타데이터
     */
    public String getSamlIdpMetadata() {
        try {
            log.info("SKTAI SAML IDP 메타데이터 조회 요청");
            String response = sktaiAuthClient.getSamlIdpMetadata();
            log.info("SKTAI SAML IDP 메타데이터 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI SAML IDP 메타데이터 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI SAML IDP 메타데이터 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SAML 메타데이터 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SAML 로그인 폼
     *
     * @return 로그인 폼 응답
     */
    public String getSamlLoginForm() {
        try {
            log.info("SKTAI SAML 로그인 폼 요청");
            String response = sktaiAuthClient.getSamlLoginForm();
            log.info("SKTAI SAML 로그인 폼 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI SAML 로그인 폼 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI SAML 로그인 폼 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SAML 로그인 폼에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 시스템 간 로그인
     *
     * @param clientSecret       클라이언트 시크릿
     * @param clientName         클라이언트 이름
     * @param systemLoginPayload 시스템 로그인 요청 데이터
     * @return 액세스 토큰 응답
     */
    public AccessTokenResponse loginSystem(String clientSecret, String clientName,
            SystemLoginPayload systemLoginPayload) {
        try {
            log.info("SKTAI 시스템 로그인 요청: clientName={}", clientName);
            AccessTokenResponse response = sktaiAuthClient.loginSystem(clientSecret, clientName, systemLoginPayload);
            log.info("SKTAI 시스템 로그인 성공: clientName={}", clientName);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 시스템 로그인 실패 (BusinessException) - clientName: {}, message: {}", clientName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 시스템 로그인 실패 (예상치 못한 오류) - clientName: {}", clientName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "시스템 로그인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 시스템 간 로그인 (편의 메서드)
     *
     * @param username     시스템 사용자명
     * @param roles        사용자 역할 목록
     * @param groups       사용자 그룹 목록
     * @param clientSecret 클라이언트 시크릿
     * @param clientName   클라이언트 이름
     * @return 액세스 토큰 응답
     */
    public AccessTokenResponse systemLogin(String username, List<Object> roles, List<Object> groups,
            String clientSecret, String clientName) {
        SystemLoginPayload systemLoginPayload = SystemLoginPayload.builder()
                .username(username)
                .roles(roles)
                .groups(groups)
                .build();

        return loginSystem(clientSecret, clientName, systemLoginPayload);
    }

    /**
     * 시스템 간 로그인 (기본 클라이언트)
     *
     * @param username     시스템 사용자명
     * @param roles        사용자 역할 목록
     * @param groups       사용자 그룹 목록
     * @param clientSecret 클라이언트 시크릿
     * @return 액세스 토큰 응답
     */
    public AccessTokenResponse systemLogin(String username, List<Object> roles, List<Object> groups,
            String clientSecret) {
        return systemLogin(username, roles, groups, clientSecret, sktaiApiClientId);
    }

    /**
     * 로그아웃
     *
     * @param username 로그아웃할 사용자명
     */
    public void logoutFromSystem(String username) {
        try {
            log.info("SKTAI 로그아웃 요청: username={}", username);
            sktaiAuthClient.logoutFromSystem(username.toLowerCase());
            log.info("SKTAI 로그아웃 성공: username={}", username);
        } catch (BusinessException e) {
            log.error("SKTAI 로그아웃 실패 (BusinessException) - username: {}, message: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 로그아웃 실패 (예상치 못한 오류) - username: {}", username, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "로그아웃에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그아웃 (별칭)
     *
     * @param username 로그아웃할 사용자명
     */
    public void logout(String username) {
        logoutFromSystem(username);
    }

    /**
     * 리프레시 토큰을 사용한 액세스 토큰 갱신
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰 응답 (프로젝트 정보 포함)
     */
    public AccessTokenResponseWithProject refreshToken(String refreshToken) {
        try {
            log.info("SKTAI 토큰 갱신 요청");
            AccessTokenResponseWithProject response = sktaiAuthClient.refreshToken(refreshToken);
            log.info("SKTAI 토큰 갱신 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 토큰 갱신 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 토큰 갱신 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "토큰 갱신에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 토큰 교환
     *
     * @param toExchangeClientName 교환할 클라이언트 이름
     * @param currentGroup         현재 그룹 경로
     * @return 새로운 액세스 토큰 응답 (프로젝트 정보 포함)
     */
    public AccessTokenResponseWithProject exchangeToken(String toExchangeClientName, String currentGroup) {
        log.info("SKTAI 토큰 교환 요청: clientName={}, group={}", toExchangeClientName, currentGroup);

        // 첫 번째 시도
        try {
            AccessTokenResponseWithProject response = sktaiAuthClient.exchangeToken(toExchangeClientName, currentGroup);
            log.info("SKTAI 토큰 교환 성공: clientName={}", toExchangeClientName);
            return response;
        } catch (BusinessException e) {
            log.warn("SKTAI 토큰 교환 1차 시도 실패 (BusinessException) - clientName: {}, message: {}, 재시도 진행",
                    toExchangeClientName, e.getMessage());
        } catch (Exception e) {
            log.warn("SKTAI 토큰 교환 1차 시도 실패 (예상치 못한 오류) - clientName: {}, 재시도 진행", toExchangeClientName, e);
        }

        // 두 번째 시도
        try {
            log.info("SKTAI 토큰 교환 2차 시도: clientName={}, group={}", toExchangeClientName, currentGroup);
            AccessTokenResponseWithProject response = sktaiAuthClient.exchangeToken(toExchangeClientName, currentGroup);
            log.info("SKTAI 토큰 교환 2차 시도 성공: clientName={}", toExchangeClientName);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 토큰 교환 2차 시도 실패 (BusinessException) - clientName: {}, message: {}",
                    toExchangeClientName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 토큰 교환 2차 시도 실패 (예상치 못한 오류) - clientName: {}", toExchangeClientName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "토큰 교환에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 정책 관리 API
    // ================================

    /**
     * 정책 생성
     *
     * @param request 정책 생성 요청
     * @return 생성된 정책 정보
     */
    public List<PolicyRequest> createPolicy(String resourceUrl, List<PolicyRequest> requests) {
        try {
            log.info("SKTAI 정책 생성 요청");
            List<PolicyRequest> response = sktaiAuthClient.createPolicy(resourceUrl, requests);
            log.info("SKTAI 정책 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 정책 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 정책 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "정책 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 정책 조회
     *
     * @return 정책 목록
     */
    public List<PolicyRequest> getPolicy(String resourceUrl) {
        try {
            log.info("SKTAI 정책 조회 요청");
            List<PolicyRequest> response = sktaiAuthClient.getPolicy(resourceUrl);
            log.info("SKTAI 정책 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 정책 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 정책 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "정책 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 정책 수정
     *
     * @param resourceUrl 리소스 URL
     * @param requests    정책 수정 요청 목록
     * @return 수정된 정책 정보 목록
     */
    public List<PolicyRequest> updatePolicy(String resourceUrl, List<PolicyRequest> requests) throws Exception {
        log.info("SKTAI 정책 수정 요청: resourceUrl={}", resourceUrl);
        List<PolicyRequest> response = sktaiAuthClient.updatePolicy(resourceUrl, requests);
        log.info("SKTAI 정책 수정 성공: resourceUrl={}", resourceUrl);
        return response;
    }

    /**
     * 기본 정책 생성
     *
     * @param request 기본 정책 생성 요청
     * @return 생성된 기본 정책 정보
     */
    public DefaultPolicyResponse createDefaultPolicies(DefaultPolicyRequest request) {
        try {
            log.info("SKTAI 기본 정책 생성 요청");
            DefaultPolicyResponse response = sktaiAuthClient.createDefaultPolicies(request);
            log.info("SKTAI 기본 정책 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 기본 정책 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 기본 정책 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "기본 정책 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 기본 정책 삭제
     */
    public void deleteDefaultPolicies() {
        try {
            log.info("SKTAI 기본 정책 삭제 요청");
            sktaiAuthClient.deleteDefaultPolicies();
            log.info("SKTAI 기본 정책 삭제 성공");
        } catch (BusinessException e) {
            log.error("SKTAI 기본 정책 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 기본 정책 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "기본 정책 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 동적 정책 수정
     *
     * @param request 동적 정책 수정 요청
     * @return 수정된 동적 정책 정보
     */
    public DynamicPolicyResponse updateDynamicPolicies(DynamicPolicyRequest request) {
        try {
            log.info("SKTAI 동적 정책 수정 요청");
            DynamicPolicyResponse response = sktaiAuthClient.updateDynamicPolicies(request);
            log.info("SKTAI 동적 정책 수정 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 동적 정책 수정 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 동적 정책 수정 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "동적 정책 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 동적 정책 삭제
     */
    public void deleteDynamicPolicies() {
        try {
            log.info("SKTAI 동적 정책 삭제 요청");
            sktaiAuthClient.deleteDynamicPolicies();
            log.info("SKTAI 동적 정책 삭제 성공");
        } catch (BusinessException e) {
            log.error("SKTAI 동적 정책 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 동적 정책 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "동적 정책 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 역할 관리 API
    // ================================

    /**
     * 기본 역할 생성
     *
     * @param request 기본 역할 생성 요청
     * @return 생성된 기본 역할 정보
     */
    public DefaultRoleResponse createDefaultRoles(DefaultRoleRequest request) {
        try {
            log.info("SKTAI 기본 역할 생성 요청");
            DefaultRoleResponse response = sktaiAuthClient.createDefaultRoles(request);
            log.info("SKTAI 기본 역할 생성 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 기본 역할 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 기본 역할 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "기본 역할 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사이드바 메뉴 생성
     *
     * @param roleName 역할 이름
     * @param request  사이드바 메뉴 생성 요청
     * @return 생성된 사이드바 메뉴 정보
     */
    public SidebarMenuResponse createSidebarMenu(String roleName, SidebarMenuRequest request) {
        try {
            log.info("SKTAI 사이드바 메뉴 생성 요청: roleName={}", roleName);
            SidebarMenuResponse response = sktaiAuthClient.createSidebarMenu(roleName, request);
            log.info("SKTAI 사이드바 메뉴 생성 성공: roleName={}", roleName);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 사이드바 메뉴 생성 실패 (BusinessException) - roleName: {}, message: {}", roleName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사이드바 메뉴 생성 실패 (예상치 못한 오류) - roleName: {}", roleName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사이드바 메뉴 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사이드바 메뉴 조회
     *
     * @param roleName 역할 이름
     * @return 사이드바 메뉴 정보
     */
    public SidebarMenuResponse getSidebarMenu(String roleName) {
        try {
            log.info("SKTAI 사이드바 메뉴 조회 요청: roleName={}", roleName);
            SidebarMenuResponse response = sktaiAuthClient.getSidebarMenu(roleName);
            log.info("SKTAI 사이드바 메뉴 조회 성공: roleName={}", roleName);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 사이드바 메뉴 조회 실패 (BusinessException) - roleName: {}, message: {}", roleName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사이드바 메뉴 조회 실패 (예상치 못한 오류) - roleName: {}", roleName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사이드바 메뉴 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사이드바 메뉴 수정
     *
     * @param roleName 역할 이름
     * @param request  사이드바 메뉴 수정 요청
     * @return 수정된 사이드바 메뉴 정보
     */
    public SidebarMenuResponse updateSidebarMenu(String roleName, SidebarMenuRequest request) {
        try {
            log.info("SKTAI 사이드바 메뉴 수정 요청: roleName={}", roleName);
            SidebarMenuResponse response = sktaiAuthClient.updateSidebarMenu(roleName, request);
            log.info("SKTAI 사이드바 메뉴 수정 성공: roleName={}", roleName);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 사이드바 메뉴 수정 실패 (BusinessException) - roleName: {}, message: {}", roleName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사이드바 메뉴 수정 실패 (예상치 못한 오류) - roleName: {}", roleName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사이드바 메뉴 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사이드바 메뉴 삭제
     *
     * @param roleName 역할 이름
     */
    public void deleteSidebarMenu(String roleName) {
        try {
            log.info("SKTAI 사이드바 메뉴 삭제 요청: roleName={}", roleName);
            sktaiAuthClient.deleteSidebarMenu(roleName);
            log.info("SKTAI 사이드바 메뉴 삭제 성공: roleName={}", roleName);
        } catch (BusinessException e) {
            log.error("SKTAI 사이드바 메뉴 삭제 실패 (BusinessException) - roleName: {}, message: {}", roleName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사이드바 메뉴 삭제 실패 (예상치 못한 오류) - roleName: {}", roleName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사이드바 메뉴 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 프로젝트별 사이드바 메뉴 조회
     *
     * @return 프로젝트별 사이드바 메뉴 목록
     */
    public SidebarMenuListResponse getSidebarMenuByProject() {
        try {
            log.info("SKTAI 프로젝트별 사이드바 메뉴 조회 요청");
            SidebarMenuListResponse response = sktaiAuthClient.getSidebarMenuByProject();
            log.info("SKTAI 프로젝트별 사이드바 메뉴 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트별 사이드바 메뉴 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트별 사이드바 메뉴 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프로젝트별 사이드바 메뉴 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // 권한 관리 API
    // ================================

    /**
     * 권한 정책 리소스 조회
     *
     * @return 권한 정책 리소스 목록
     */
    public PermissionResourceResponse getPermissionPoliciesApi() {
        try {
            log.info("SKTAI 권한 정책 리소스 조회 요청");
            PermissionResourceResponse response = sktaiAuthClient.getPermissionPoliciesApi();
            log.info("SKTAI 권한 정책 리소스 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 권한 정책 리소스 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 권한 정책 리소스 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "권한 정책 리소스 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 권한 리소스 추가
     *
     * @param request 권한 리소스 추가 요청
     * @return 추가된 권한 리소스 정보
     */
    public PermissionResourceResponse appendPermissionResources(PermissionResourceRequest request) {
        try {
            log.info("SKTAI 권한 리소스 추가 요청");
            PermissionResourceResponse response = sktaiAuthClient.appendPermissionResources(request);
            log.info("SKTAI 권한 리소스 추가 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 권한 리소스 추가 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 권한 리소스 추가 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "권한 리소스 추가에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 권한 리소스 덮어쓰기
     *
     * @param request 권한 리소스 덮어쓰기 요청
     * @return 덮어쓴 권한 리소스 정보
     */
    public PermissionResourceResponse overwritePermissionResources(PermissionResourceRequest request) {
        try {
            log.info("SKTAI 권한 리소스 덮어쓰기 요청");
            PermissionResourceResponse response = sktaiAuthClient.overwritePermissionResources(request);
            log.info("SKTAI 권한 리소스 덮어쓰기 성공");
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 권한 리소스 덮어쓰기 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 권한 리소스 덮어쓰기 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "권한 리소스 덮어쓰기에 실패했습니다: " + e.getMessage());
        }
    }
}
