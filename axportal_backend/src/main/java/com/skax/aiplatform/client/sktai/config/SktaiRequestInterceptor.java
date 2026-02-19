package com.skax.aiplatform.client.sktai.config;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.dto.auth.TokenCacheData;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.service.auth.TokenCacheService;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI API 요청 인터셉터
 *
 * <p>
 * SKTAI API 호출 시 공통 헤더를 자동으로 추가하고 토큰 기반 인증을 처리합니다.
 * OAuth2 로그인 요청과 일반 API 요청을 구분하여 적절한 Content-Type을 설정합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>공통 헤더 자동 설정 (User-Agent, Accept, Content-Type)</li>
 *   <li>현재 사용자의 토큰을 이용한 Authorization 헤더 자동 추가</li>
 *   <li>OAuth2 로그인 요청과 일반 API 요청 구분 처리</li>
 * </ul>
 *
 * <p><strong>중요:</strong> 이 클래스는 @Component를 사용하지 않아 전역 Bean으로 등록되지 않습니다.
 * SKTAI FeignClient 설정에서만 사용되어 다른 FeignClient에 영향을 주지 않습니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.2
 * @updated 2025-10-13 - @Component 제거하여 Bean 충돌 방지
 * @since 2025-08-15
 */
@Slf4j
public class SktaiRequestInterceptor implements RequestInterceptor {

    private static final String USER_AGENT = "AXPORTAL-Backend/1.0";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    private static final String OAUTH_LOGIN_ENDPOINT = "/auth/login";
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";

    // MultiPart 엔드포인트 패턴들
    // 주의: 더 긴 패턴을 먼저 배치해야 함 (contains 체크 시 짧은 패턴이 먼저 매칭되는 것을 방지)
    private static final String[] MULTIPART_ENDPOINTS = {
            "/agent/agents/apps/deployments/custom",  // 커스텀 Agent App 배포 (더 긴 패턴을 먼저)
            "/agent/agents/apps/custom",  // 커스텀 Agent App 생성
            "/knowledge/custom_scripts",
            "/knowledge/repos/external",  // External Knowledge 등록
            "/test/loader",
            "/test/splitter",
            "/task-files",
            "/gateway/audio/translations",
            "/upload"
    };

    // agentgateway 엔드포인트 패턴들
    private static final String[] AGENTGATEWAY_ENDPOINTS = {
            "/agentgateway/audio/translations",
            "/agentgateway/upload",
            "/agentgateway/task-files",
            "/agentgateway/test/loader",
            "/agentgateway/test/splitter",
            "/agentgateway/stream",
            "/api/v1/agent_gateway",  // 추가: API 경로 패턴
    };

    private final TokenCacheService tokenCacheService;
    private final String sktaiBaseUrl;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final String sktaiClientId;
    RestTemplate restTemplate;

    /**
     * SKTAI RequestInterceptor 생성자
     *
     * @param tokenCacheService        토큰 캐시 서비스
     * @param sktaiBaseUrl             SKTAI API Base URL
     * @param gpoUsersMasRepository    사용자 정보 Repository
     * @param gpoPrjuserroleRepository 프로젝트 사용자 역할 Repository
     * @param sktaiClientId            SKTAI 클라이언트 ID
     */
    public SktaiRequestInterceptor(TokenCacheService tokenCacheService,
                                   String sktaiBaseUrl,
                                   GpoUsersMasRepository gpoUsersMasRepository,
                                   GpoPrjuserroleRepository gpoPrjuserroleRepository,
                                   String sktaiClientId) {
        this.tokenCacheService = tokenCacheService;
        this.sktaiBaseUrl = sktaiBaseUrl;
        this.gpoUsersMasRepository = gpoUsersMasRepository;
        this.gpoPrjuserroleRepository = gpoPrjuserroleRepository;
        this.sktaiClientId = sktaiClientId;

        // 모든 인증서를 신뢰하는 TrustManager 생성
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

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
            RestTemplate restTemplate = new RestTemplate();
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            restTemplate.setRequestFactory(requestFactory);

            this.restTemplate = restTemplate;
        } catch (RuntimeException re) {
            throw new RuntimeException("Failed to create RestTemplate with SSL bypass", re);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with SSL bypass", e);
        }
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            log.debug("🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() - Method: {}, URL: {}", template.method(), template.url());

            // 공통 헤더 설정
            setCommonHeaders(template);

            // MultiPart 요청인지 확인
            boolean isMultipart = isMultipartRequest(template.url());
            boolean isOAuth2LoginRequest = false;

            if (!isMultipart) {
                // MultiPart가 아닌 경우에만 Content-Type 설정 및 OAuth2 요청 확인
                isOAuth2LoginRequest = setContentTypeHeader(template);
            } else {
                // MultiPart 요청의 경우 Content-Type을 설정하지 않음
                log.debug("🔴 MultiPart 요청 - Content-Type 설정 건너뛰기");
            }

            // OAuth2 로그인 요청이 아닌 경우에만 토큰 적용
            if (!isOAuth2LoginRequest && !"sktai-model-gateway-client".equals(template.feignTarget().name())) {
                setAuthorizationHeader(template);
            }

            log.debug("🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() 완료 - 최종 헤더들: {}", template.headers());

        } catch (IllegalArgumentException e) {
            log.error("SKTAI API 요청 인터셉터 적용 실패 (IllegalArgumentException) - 잘못된 인자: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("SKTAI API 요청 인터셉터 적용 실패 (NullPointerException) - 필수 값 누락: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("SKTAI API 요청 인터셉터 적용 실패 (예상치 못한 오류): {}", e.getMessage(), e);
        }
    }

    /**
     * 공통 헤더 설정
     *
     * @param template 요청 템플릿
     */
    private void setCommonHeaders(RequestTemplate template) {
        // 재시도 시 헤더 중복 방지를 위해 기존 헤더 제거
        template.header("User-Agent", Collections.emptyList());
        template.header("Accept", Collections.emptyList());

        template.header("User-Agent", USER_AGENT);
        template.header("Accept", CONTENT_TYPE_JSON);
    }

    /**
     * Content-Type 헤더 설정
     *
     * @param template 요청 템플릿
     * @return OAuth2 로그인 요청 여부
     */
    private boolean setContentTypeHeader(RequestTemplate template) {
        boolean isPostWithBody = "POST".equals(template.method());
        boolean isOAuth2LoginRequest = false;

        // 디버깅을 위한 로그
        log.debug("🔍 setContentTypeHeader - Method: {}, URL: {}, isPostWithBody: {}",
                template.method(), template.url(), isPostWithBody);

        if (isPostWithBody) {
            String url = template.url();

            if (url != null && url.contains(OAUTH_LOGIN_ENDPOINT)) {
                // OAuth2 로그인 요청의 경우 form-urlencoded 사용
                template.header("Content-Type", Collections.emptyList());
                template.header("Content-Type", CONTENT_TYPE_FORM);
                isOAuth2LoginRequest = true;
                log.debug("✅ OAuth2 로그인 요청 - Content-Type: form-urlencoded");
            } else if (isMultipartRequest(url)) {
                // ⚠️ MultiPart 요청의 경우 Content-Type을 설정하지 않음
                // SpringFormEncoder가 boundary와 함께 자동 설정하도록 함
                log.debug("✅ 🔴 MultiPart 요청 감지 - Content-Type 설정 건너뛰기 (SpringFormEncoder가 자동 설정)");
                // Content-Type을 설정하지 않음!
            } else {
                // 일반 요청의 경우 JSON 사용
                template.header("Content-Type", Collections.emptyList());
                template.header("Content-Type", CONTENT_TYPE_JSON);
                log.debug("✅ 일반 API 요청 - Content-Type: JSON");
            }
        } else if (!template.headers().containsKey("Content-Type")) {
            // Content-Type이 설정되지 않은 경우 기본값으로 JSON 설정
            template.header("Content-Type", CONTENT_TYPE_JSON);
            log.debug("✅ 기본 Content-Type: JSON");
        }
        return isOAuth2LoginRequest;
    }

    /**
     * Authorization 헤더 설정
     *
     * @param template 요청 템플릿
     */
    private void setAuthorizationHeader(RequestTemplate template) {
        // AgentGateway 요청인 경우 @RequestHeader로 전달된 Authorization을 그대로 사용
        if (isAgentGatewayRequest(template.url())) {
            log.debug("🌐 AgentGateway 요청 - @RequestHeader로 전달된 Authorization 헤더 사용 (건너뛰기)");
            return;
        }

        // 기존 헤더 제거 (중복 방지)
        template.header("Authorization", Collections.emptyList());

        // Admin 모드, 로그인 프로세스, 일반 인증 순으로 체크
        String username;
        if (AdminContext.isAdminMode()) {
            username = AdminContext.getAdminUsername();
            log.debug("🔑 Admin 모드 감지 - Admin 계정 토큰 사용: {}", username);
        } else {
            username = getCurrentUsername();
        }

        if (!StringUtils.hasText(username)) {
            log.warn("🚨 인증된 사용자가 없어 Authorization 헤더를 설정하지 않습니다 - URL: {}", template.url());
            return;
        }

        log.debug("🔍 현재 사용자 정보 조회: {} - URL: {}", username, template.url());

        TokenCacheData token = tokenCacheService.getTokenByUsername(username);

        if (token == null) {
            log.warn("🚨 사용자 {}의 토큰 정보를 찾을 수 없습니다. 새로 로그인 시도 - URL: {}", username, template.url());
            token = loginAndCacheToken(username);
            if (token == null) {
                log.error("🚨 사용자 {}의 새 로그인 실패. Authorization 헤더를 설정하지 않습니다 - URL: {}", username, template.url());
                return;
            }
        }

        log.debug("🎫 토큰 정보 확인 - 사용자: {}, 토큰타입: {}, 액세스토큰: {}..., 만료시간: {}",
                username,
                token.getTokenType(),
                token.getAccessToken() != null ? token.getAccessToken().substring(0, Math.min(10, token.getAccessToken().length())) : "null",
                token.getExpAt());

        // 1) 유효한 경우 바로 사용
        if (token.isValid()) {
            String tokenType = StringUtils.hasText(token.getTokenType()) ? token.getTokenType() : DEFAULT_TOKEN_TYPE;
            String authHeader = tokenType + " " + token.getAccessToken();
            template.header("Authorization", authHeader);
            log.debug("✅ 사용자 {}의 Authorization 헤더 설정 완료 (기존 토큰): {}", username, authHeader);
            return;
        }

        // 2) 액세스 토큰은 만료되었지만 리프레시 토큰은 유효한 경우: 갱신
        if (token.isExpired() && !token.isRefreshExpired()) {
            synchronized (("SKTAI_REFRESH_" + username).intern()) {
                TokenCacheData latest = tokenCacheService.getTokenByUsername(username);
                if (latest != null && latest.isValid()) {
                    String tokenType = StringUtils.hasText(latest.getTokenType()) ? latest.getTokenType() : DEFAULT_TOKEN_TYPE;
                    template.header("Authorization", tokenType + " " + latest.getAccessToken());
                    log.debug("사용자 {}의 Authorization 헤더 설정 완료 (동시 갱신 후 최신 토큰)", username);
                    return;
                }

                try {
                    log.debug("사용자 {}의 액세스 토큰 만료. 리프레시 토큰으로 갱신 시도", username);
                    TokenCacheData toRefresh = latest != null ? latest : token;
                    AccessTokenResponseWithProject refreshed = refreshTokenDirectly(toRefresh.getRefreshToken());

                    // 응답 기반으로 토큰 데이터 갱신
                    toRefresh.setAccessToken(refreshed.getAccessToken());
                    if (StringUtils.hasText(refreshed.getRefreshToken())) {
                        toRefresh.setRefreshToken(refreshed.getRefreshToken());
                    }
                    toRefresh.setTokenType(StringUtils.hasText(refreshed.getTokenType()) ? refreshed.getTokenType() : DEFAULT_TOKEN_TYPE);
                    toRefresh.setTokenExpTimes(refreshed.getExpiresIn());
                    toRefresh.setRefreshTokenExpTimes(refreshed.getRefreshExpiresIn());
                    toRefresh.setExpAt(extractExpAtFromAccessToken(refreshed.getAccessToken(), refreshed.getExpiresIn()));
                    toRefresh.setIssueAt(LocalDateTime.now());
                    toRefresh.setRefreshTokenExpAt(LocalDateTime.now().plusSeconds(refreshed.getRefreshExpiresIn()));
                    toRefresh.setTokenExpYn("N");

                    tokenCacheService.cacheToken(toRefresh);

                    String tokenType = StringUtils.hasText(toRefresh.getTokenType()) ? toRefresh.getTokenType() : DEFAULT_TOKEN_TYPE;
                    template.header("Authorization", tokenType + " " + toRefresh.getAccessToken());
                    log.debug("사용자 {}의 토큰 갱신 및 캐시 업데이트 완료", username);
                    return;
                } catch (IllegalStateException e) {
                    log.error("사용자 {} 토큰 갱신 실패 (IllegalStateException) - 상태 오류: {}", username, e.getMessage(), e);
                    // 갱신 실패 시 Authorization 헤더를 설정하지 않음
                    return;
                } catch (NullPointerException e) {
                    log.error("사용자 {} 토큰 갱신 실패 (NullPointerException) - 필수 값 누락: {}", username, e.getMessage(), e);
                    // 갱신 실패 시 Authorization 헤더를 설정하지 않음
                    return;
                } catch (RuntimeException e) {
                    log.error("사용자 {} 토큰 갱신 실패 (RuntimeException) - 런타임 오류: {}", username, e.getMessage(), e);
                    // 갱신 실패 시 Authorization 헤더를 설정하지 않음
                    return;
                } catch (Exception e) {
                    log.error("사용자 {} 토큰 갱신 실패 (예상치 못한 오류): {}", username, e.getMessage(), e);
                    // 갱신 실패 시 Authorization 헤더를 설정하지 않음
                    return;
                }
            }
        }

        // 3) 리프레시 토큰도 만료된 경우 - 새로 로그인 시도
        log.warn("🚨 사용자 {}의 액세스/리프레시 토큰이 모두 만료되었습니다. 새로 로그인 시도 - URL: {}", username, template.url());
        TokenCacheData newToken = loginAndCacheToken(username);
        if (newToken != null && newToken.isValid()) {
            String tokenType = StringUtils.hasText(newToken.getTokenType()) ? newToken.getTokenType() : DEFAULT_TOKEN_TYPE;
            template.header("Authorization", tokenType + " " + newToken.getAccessToken());
            log.info("✅ 사용자 {}의 새 로그인 성공 및 Authorization 헤더 설정 완료", username);
        } else {
            log.error("🚨 사용자 {}의 새 로그인 실패. Authorization 헤더를 설정하지 않습니다 - URL: {}", username, template.url());
        }
    }

    /**
     * 현재 인증된 사용자명 조회
     *
     * @return 사용자명 (인증되지 않은 경우 null)
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }

            return null;

        } catch (IllegalStateException e) {
            log.warn("현재 사용자 정보 조회 실패 (IllegalStateException) - SecurityContext 상태 오류: {}", e.getMessage());
            return null;
        } catch (NullPointerException e) {
            log.warn("현재 사용자 정보 조회 실패 (NullPointerException) - Authentication 객체 누락: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("현재 사용자 정보 조회 실패 (예상치 못한 오류): {}", e.getMessage());
            return null;
        }
    }

    /**
     * MultiPart 요청인지 확인
     *
     * @param url 요청 URL
     * @return MultiPart 요청 여부
     */
    private boolean isMultipartRequest(String url) {
        if (url == null) {
            return false;
        }

        for (String endpoint : MULTIPART_ENDPOINTS) {
            if (url.contains(endpoint)) {
                log.debug("🔴 MultiPart 엔드포인트 매칭: {} -> {}", endpoint, url);
                return true;
            }
        }

        return false;
    }

    /**
     * AgentGateway 요청인지 확인
     *
     * @param url 요청 URL
     * @return AgentGateway 요청 여부
     */
    private boolean isAgentGatewayRequest(String url) {
        if (url == null) {
            return false;
        }

        // API v1 agent_gateway 경로 체크
        if (url.contains("/api/v1/agent_gateway")) {
            log.debug("🔴 AgentGateway API v1 엔드포인트 매칭: {}", url);
            return true;
        }

        // 기존 패턴들도 체크
        for (String endpoint : AGENTGATEWAY_ENDPOINTS) {
            if (url.contains(endpoint)) {
                log.debug("🔴 AgentGateway 엔드포인트 매칭: {} -> {}", endpoint, url);
                return true;
            }
        }

        return false;
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 직접 HTTP 요청으로 갱신
     * (Feign Client 의존성 순환 문제를 피하기 위해 RestTemplate 사용)
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰 응답
     */
    private AccessTokenResponseWithProject refreshTokenDirectly(String refreshToken) {
        try {
            String url = sktaiBaseUrl + "/api/v1/auth/token/refresh?refresh_token=" + refreshToken;

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            headers.set("Accept", CONTENT_TYPE_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AccessTokenResponseWithProject.class
            );

            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("토큰 갱신 HTTP 요청 실패 (HttpClientErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("토큰 갱신 실패 (HTTP 클라이언트 오류)", e);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("토큰 갱신 HTTP 요청 실패 (HttpServerErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("토큰 갱신 실패 (HTTP 서버 오류)", e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("토큰 갱신 HTTP 요청 실패 (ResourceAccessException) - 네트워크 연결 오류: {}", e.getMessage(), e);
            throw new RuntimeException("토큰 갱신 실패 (네트워크 오류)", e);
        } catch (Exception e) {
            log.error("토큰 갱신 HTTP 요청 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new RuntimeException("토큰 갱신 실패", e);
        }
    }

    /**
     * SKTAI login API 직접 호출
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 로그인 응답
     */
    private AccessTokenResponseWithProject performLogin(String username, String password) {
        try {
            String loginUrl = sktaiBaseUrl + "/api/v1/auth/login";

            // application/x-www-form-urlencoded 형식의 요청 바디 생성
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("username", username.toLowerCase());
            formData.add("password", password);
            formData.add("scope", "");
            formData.add("client_id", "default");
            formData.add("client_secret", "");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("accept", CONTENT_TYPE_JSON);

            // HTTP 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

            // POST 요청 수행
            ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.postForEntity(
                    loginUrl,
                    requestEntity,
                    AccessTokenResponseWithProject.class
            );

            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("SKTAI 로그인 HTTP 요청 실패 (HttpClientErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("SKTAI 로그인 실패 (HTTP 클라이언트 오류)", e);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("SKTAI 로그인 HTTP 요청 실패 (HttpServerErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("SKTAI 로그인 실패 (HTTP 서버 오류)", e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("SKTAI 로그인 HTTP 요청 실패 (ResourceAccessException) - 네트워크 연결 오류: {}", e.getMessage(), e);
            throw new RuntimeException("SKTAI 로그인 실패 (네트워크 오류)", e);
        } catch (Exception e) {
            log.error("SKTAI 로그인 HTTP 요청 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new RuntimeException("SKTAI 로그인 실패", e);
        }
    }

    /**
     * SKTAI token exchange API 직접 호출
     *
     * @param username    사용자명
     * @param accessToken 액세스 토큰
     * @return exchange 응답
     */
    private AccessTokenResponseWithProject performExchange(String username, String accessToken) {
        try {
            // gpoPrjuserroleRepository를 사용하여 활성화된 프로젝트 조회
            ProjectUserRole projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndStatusNm(username, ProjectUserRoleStatus.ACTIVE);

            // 프로젝트 정보가 없으면 예외 발생
            if (projectUserRole == null) {
                throw new RuntimeException("활성화된 프로젝트를 찾을 수 없습니다 - username: " + username);
            }

            Long prjSeq = projectUserRole.getProject().getPrjSeq();
            Long roleSeq = projectUserRole.getRole().getRoleSeq();
            String groupName = "P" + prjSeq + "_R" + roleSeq;
            log.debug("활성화된 프로젝트 조회 성공 - username={}, prjSeq={}, roleSeq={}, groupName={}", username, prjSeq, roleSeq, groupName);

            String exchangeUrl = sktaiBaseUrl + "/api/v1/auth/token/exchange"
                    + "?to_exchange_client_name=" + sktaiClientId
                    + "&current_group=/" + groupName;

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", CONTENT_TYPE_JSON);
            headers.set("authorization", "Bearer " + accessToken);

            // HTTP 요청 엔티티 생성
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // GET 요청 수행
            ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.exchange(
                    exchangeUrl,
                    HttpMethod.GET,
                    requestEntity,
                    AccessTokenResponseWithProject.class
            );

            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("SKTAI token exchange HTTP 요청 실패 (HttpClientErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("SKTAI token exchange 실패 (HTTP 클라이언트 오류)", e);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("SKTAI token exchange HTTP 요청 실패 (HttpServerErrorException) - HTTP {}: {}", e.getStatusCode(), e.getMessage(), e);
            throw new RuntimeException("SKTAI token exchange 실패 (HTTP 서버 오류)", e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("SKTAI token exchange HTTP 요청 실패 (ResourceAccessException) - 네트워크 연결 오류: {}", e.getMessage(), e);
            throw new RuntimeException("SKTAI token exchange 실패 (네트워크 오류)", e);
        } catch (Exception e) {
            log.error("SKTAI token exchange HTTP 요청 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new RuntimeException("SKTAI token exchange 실패", e);
        }
    }

    /**
     * 새로 로그인하여 토큰을 캐시하는 메서드
     *
     * @param username 사용자명
     * @return 새로 발급받은 TokenCacheData (null이면 실패)
     */
    private TokenCacheData loginAndCacheToken(String username) {
        synchronized (("SKTAI_LOGIN_" + username).intern()) {
            // 동시 로그인 방지를 위해 다시 한번 캐시 확인
            TokenCacheData existing = tokenCacheService.getTokenByUsername(username);
            if (existing != null && existing.isValid()) {
                log.debug("사용자 {}의 토큰이 이미 갱신되었습니다 (동시 로그인 처리)", username);
                return existing;
            }

            try {
                log.info("사용자 {}의 토큰이 없거나 만료되어 새로 로그인 시도", username);

                // gpo_users_mas에서 사용자 정보 조회
                GpoUsersMas user = gpoUsersMasRepository.findByMemberId(username)
                        .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다: " + username));

                // SKTAI 로그인 수행
                AccessTokenResponseWithProject loginResponse = performLogin(user.getMemberId(), user.getUserPassword());
                log.debug("SKTAI 로그인 성공 - username={}", username);

                // SKTAI token exchange 수행
                AccessTokenResponseWithProject exchangeResponse = "admin".equals(username) ? loginResponse : performExchange(username, loginResponse.getAccessToken());
                log.debug("SKTAI token exchange 성공 - username={}", username);

                // TokenCacheData 생성 및 캐싱
                TokenCacheData newToken = new TokenCacheData();
                newToken.setMemberId(username);
                newToken.setAccessToken(exchangeResponse.getAccessToken());
                newToken.setRefreshToken(exchangeResponse.getRefreshToken());
                newToken.setTokenType(StringUtils.hasText(exchangeResponse.getTokenType()) ? exchangeResponse.getTokenType() : DEFAULT_TOKEN_TYPE);
                newToken.setTokenExpTimes(exchangeResponse.getExpiresIn());
                newToken.setRefreshTokenExpTimes(exchangeResponse.getRefreshExpiresIn());
                newToken.setExpAt(extractExpAtFromAccessToken(exchangeResponse.getAccessToken(), exchangeResponse.getExpiresIn()));
                newToken.setIssueAt(LocalDateTime.now());
                newToken.setRefreshTokenExpAt(LocalDateTime.now().plusSeconds(exchangeResponse.getRefreshExpiresIn()));
                newToken.setTokenExpYn("N");

                tokenCacheService.cacheToken(newToken);
                log.info("사용자 {}의 새 토큰 발급 및 캐싱 완료", username);

                return newToken;
            } catch (RuntimeException re) {
                log.error("사용자 {} 새 로그인 실패: {}", username, re.getMessage(), re);
                return null;
            } catch (Exception e) {
                log.error("사용자 {} 새 로그인 실패: {}", username, e.getMessage(), e);
                return null;
            }
        }
    }

    private LocalDateTime extractExpAtFromAccessToken(String accessToken, Long fallbackExpiresInSec) {
        try {
            if (!StringUtils.hasText(accessToken)) {
                throw new IllegalArgumentException("accessToken is blank");
            }
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT format");
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode node = new ObjectMapper().readTree(payloadJson);

            if (node.has("exp") && !node.get("exp").isNull()) {
                long expSeconds = node.get("exp").asLong();
                return Instant.ofEpochSecond(expSeconds)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (RuntimeException re) {
            // 보안: 토큰 파싱 실패 상세 정보를 로그에만 기록 (디버그 레벨)
            log.debug("Failed to extract exp from access token, fallback to expiresIn. error: {}", re.getMessage());
        } catch (Exception e) {
            // 보안: 토큰 파싱 실패 상세 정보를 로그에만 기록 (디버그 레벨)
            log.debug("Failed to extract exp from access token, fallback to expiresIn. error: {}", e.getMessage());
        }

        long fallback = fallbackExpiresInSec != null ? fallbackExpiresInSec : 0L;
        return LocalDateTime.now().plusSeconds(fallback);
    }
}