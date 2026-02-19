package com.skax.aiplatform.controller.auth;

import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.auth.response.ProjectInfoRes;
import com.skax.aiplatform.dto.auth.response.SktaiAccessTokenRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.service.auth.UsersService;
import com.skax.aiplatform.service.home.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 외부 서비스(셀렉트스타 등)가 전달한 JWT를 검증하고,
 * gpo_users_mas 테이블에서 사용자 credentials를 조회하여 SKTAI login/exchange를 직접 수행한 후
 * access_token을 반환하는 컨트롤러.
 * TokenCacheService를 사용하지 않고 RestTemplate로 직접 SKTAI API를 호출함.
 */
@Slf4j
@RestController
@RequestMapping()
@Tag(name = "SktaiAccessToken", description = "SKTAI 접근 토큰 조회 API")
public class SktaiTokenController {

    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final UsersService usersService;
    private final ProjectService projectService;
    private final RestTemplate restTemplate;

    @Value("${sktai.api.base-url}")
    private String sktaiBaseUrl;

    @Value("${sktai.api.client-id}")
    private String sktaiClientId;

    public SktaiTokenController(GpoUsersMasRepository gpoUsersMasRepository,
                                GpoPrjuserroleRepository gpoPrjuserroleRepository, UsersService usersService, ProjectService projectService) {
        this.gpoUsersMasRepository = gpoUsersMasRepository;
        this.gpoPrjuserroleRepository = gpoPrjuserroleRepository;
        this.usersService = usersService;
        this.projectService = projectService;

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

    /**
     * 현재 인증된 사용자의 SKTAI access_token 반환
     * - Authorization: Bearer {jwt} 헤더 필요
     * - JwtAuthenticationFilter를 통해 사전 검증됨
     * - gpo_users_mas에서 사용자 credentials 조회 후 SKTAI login/exchange 직접 호출
     */
    @GetMapping("/auth/sktai/access-token")
    @Operation(summary = "SKTAI Access Token 조회", description = "gpo_users_mas에서 사용자 정보를 조회하여 SKTAI login 및 exchange를 수행 후 access_token을 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음")
    })
    public AxResponseEntity<SktaiAccessTokenRes> getSktaiAccessToken() throws Exception {
        // SecurityContext에 설정된 인증 정보 확인 (JwtAuthenticationFilter에서 처리됨)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효한 JWT 토큰이 필요합니다");
        }

        String username = authentication.getName();
        log.info("SKTAI Access Token 조회 요청 - username={}", username);

        // gpo_users_mas 테이블에서 사용자 정보 조회
        GpoUsersMas user = gpoUsersMasRepository.findByMemberId(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "사용자 정보를 찾을 수 없습니다: " + username));

        AccessTokenResponseWithProject loginResponse;
        AccessTokenResponseWithProject exchangeResponse;

        synchronized (("SKTAI_GET_TOKEN_" + username).intern()) {
            // SKTAI login 호출
            loginResponse = performLogin(user.getMemberId(), user.getUserPassword());
            log.info("SKTAI login 성공 - username={}, {}", username, loginResponse);

            // SKTAI token exchange 호출
            exchangeResponse = performExchange(username, loginResponse.getAccessToken(), false);
            log.info("SKTAI token exchange 성공 - username={}", username);
        }

        // 프로젝트 정보 추가
        ProjectInfoRes activeProject = usersService.getUserInfo().getActiveProject();

        SktaiAccessTokenRes res = new SktaiAccessTokenRes(exchangeResponse.getAccessToken(), exchangeResponse.getRefreshToken(), activeProject);
        return AxResponseEntity.ok(res, "SKTAI Access Token 조회 성공");
    }

    /**
     * 현재 인증된 사용자의 SKTAI access_token 반환
     * - Authorization: Bearer {jwt} 헤더 필요
     * - JwtAuthenticationFilter를 통해 사전 검증됨
     * - gpo_users_mas에서 사용자 credentials 조회 후 SKTAI login/exchange 직접 호출
     */
    @GetMapping("/auth/sktai/access-public-token")
    @Operation(summary = "SKTAI Access Token 조회", description = "gpo_users_mas에서 사용자 정보를 조회하여 SKTAI login 및 exchange를 수행 후 access_token을 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음")
    })
    public AxResponseEntity<SktaiAccessTokenRes> getSktaiAccessPublicToken() throws Exception {
        // SecurityContext에 설정된 인증 정보 확인 (JwtAuthenticationFilter에서 처리됨)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효한 JWT 토큰이 필요합니다");
        }

        String username = authentication.getName();
        log.info("SKTAI Access Token 조회 요청 - username={}", username);

        // gpo_users_mas 테이블에서 사용자 정보 조회
        GpoUsersMas user = gpoUsersMasRepository.findByMemberId(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "사용자 정보를 찾을 수 없습니다: " + username));

        AccessTokenResponseWithProject loginResponse;
        AccessTokenResponseWithProject exchangeResponse;

        synchronized (("SKTAI_GET_TOKEN_" + username).intern()) {
            // SKTAI login 호출
            loginResponse = performLogin(user.getMemberId(), user.getUserPassword());
            log.info("SKTAI login 성공 - username={}, {}", username, loginResponse);

            // SKTAI token exchange 호출
            exchangeResponse = performExchange(username, loginResponse.getAccessToken(), true);
            log.info("SKTAI token exchange 성공 - username={}", username);
        }

        // 프로젝트 정보 추가
        ProjectInfoRes activeProject = getPublicProjectInfo(username);

        SktaiAccessTokenRes res = new SktaiAccessTokenRes(exchangeResponse.getAccessToken(), exchangeResponse.getRefreshToken(), activeProject);
        return AxResponseEntity.ok(res, "SKTAI Access Token 조회 성공");
    }

    /**
     * SKTAI login API 직접 호출
     */
    private AccessTokenResponseWithProject performLogin(String username, String password) {
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
        headers.set("accept", "application/json");

        // HTTP 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // POST 요청 수행
        ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.postForEntity(
                loginUrl,
                requestEntity,
                AccessTokenResponseWithProject.class);

        return response.getBody();
    }

    /**
     * SKTAI token exchange API 직접 호출
     */
    private AccessTokenResponseWithProject performExchange(String username, String accessToken, boolean isPublic) {
        ProjectUserRole projectUserRole;
        String groupName;

        if (isPublic) {
            // public 프로젝트로 강제 exchange
            projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(username, -999L)
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
            Long prjSeq = projectUserRole.getProject().getPrjSeq();
            Long roleSeq = projectUserRole.getRole().getRoleSeq();
            groupName = "P" + prjSeq + "_R" + roleSeq;
            log.info("Public 프로젝트 조회 성공 - username={}, prjSeq={}, roleSeq={}, groupName={}", username, prjSeq, roleSeq,
                    groupName);
        } else {
            // gpoPrjuserroleRepository를 사용하여 활성화된 프로젝트 조회
            projectUserRole = gpoPrjuserroleRepository.findByMemberIdAndStatusNm(username,
                    ProjectUserRoleStatus.ACTIVE);

            if (projectUserRole == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            } else {
                Long prjSeq = projectUserRole.getProject().getPrjSeq();
                Long roleSeq = projectUserRole.getRole().getRoleSeq();
                groupName = "P" + prjSeq + "_R" + roleSeq;
                log.info("활성화된 프로젝트 조회 성공 - username={}, prjSeq={}, roleSeq={}, groupName={}", username, prjSeq, roleSeq,
                        groupName);
            }
        }

        String exchangeUrl = sktaiBaseUrl + "/api/v1/auth/token/exchange"
                + "?to_exchange_client_name=" + sktaiClientId
                + "&current_group=/" + groupName;

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("authorization", "Bearer " + accessToken);

        // HTTP 요청 엔티티 생성
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // GET 요청 수행
        ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.exchange(
                exchangeUrl,
                org.springframework.http.HttpMethod.GET,
                requestEntity,
                AccessTokenResponseWithProject.class);

        return response.getBody();
    }


    private ProjectInfoRes getPublicProjectInfo(String memberId) {
        // 1. 사용자 프로젝트 목록 조회
        List<ProjectRes> projectList = projectService.getJoinProjectList(memberId);

        // 2. 사용자의 프로젝트 목록 및 역할 포함
        if (projectList != null && !projectList.isEmpty()) {
            for (ProjectRes proj : projectList) {
                ProjectInfoRes item = ProjectInfoRes.builder()
                        .prjNm(proj.getPrjNm())
                        .prjSeq(proj.getPrjSeq())
                        .prjUuid(proj.getUuid())
                        .prjRoleSeq(proj.getRoleSeq())
                        .active(true)
                        .adxpGroupNm("P%s_R%s".formatted(proj.getPrjSeq(), proj.getRoleSeq()))
                        .adxpGroupPath("/P%s_R%s".formatted(proj.getPrjSeq(), proj.getRoleSeq()))
                        .build();

                // 활성 프로젝트 별도 키 값으로 전달
                if (item.getPrjSeq().equals("-999")) {
                    return item;
                }
            }
        }

        return null;
    }
}
