package com.skax.aiplatform.client.sktai.auth;

import com.skax.aiplatform.client.sktai.auth.dto.request.*;
import com.skax.aiplatform.client.sktai.auth.dto.response.*;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKTAI Auth API Feign Client
 * 
 * <p>SKTAI AI Platform의 인증 API를 호출하기 위한 Feign Client입니다.
 * OAuth2 기반 로그인, 시스템 로그인, 토큰 갱신, 정책 관리, 역할 관리, 권한 관리 등의 기능을 제공합니다.</p>
 * 
 * <p><strong>Base URL:</strong> ${sktai.api.base-url}</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 2.0
 */
@FeignClient(
    name = "sktai-auth-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Auth API", description = "SKTAI AI Platform 인증 API")
public interface SktaiAuthClient {
    
    // ================================
    // 인증 및 로그인 API
    // ================================
    
    /**
     * OAuth2 패스워드 기반 로그인
     * 
     * @param loginRequest OAuth2 로그인 요청 데이터
     * @return 액세스 토큰 응답 (프로젝트 정보 포함)
     */
    @Operation(summary = "OAuth2 패스워드 로그인", description = "사용자명, 비밀번호, 클라이언트 ID를 사용하여 액세스 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PostMapping(value = "/api/v1/auth/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponseWithProject loginAccessToken(@RequestBody LoginAccessTokenRequest loginRequest);
    
    /**
     * SSO 로그인
     * 
     * @return SSO 로그인 응답
     */
    @Operation(summary = "SSO 로그인", description = "Single Sign-On 로그인 엔드포인트입니다.")
    @GetMapping("/api/v1/auth/login/sso")
    SsoLoginResponse ssoLogin();
    
    /**
     * SSO 콜백
     * 
     * @return SSO 콜백 응답
     */
    @Operation(summary = "SSO 콜백", description = "SSO 로그인 후 콜백 처리 엔드포인트입니다.")
    @GetMapping("/api/v1/auth/login/sso/callback")
    SsoCallbackResponse ssoCallback();
    
    /**
     * SAML IDP 메타데이터 조회
     * 
     * @return SAML 메타데이터
     */
    @Operation(summary = "SAML IDP 메타데이터", description = "SAML Identity Provider 메타데이터를 조회합니다.")
    @GetMapping("/api/v1/auth/saml_idp/metadata")
    String getSamlIdpMetadata();
    
    /**
     * SAML 로그인 폼
     * 
     * @return 로그인 폼 응답
     */
    @Operation(summary = "SAML 로그인 폼", description = "SAML 기반 로그인 폼을 제공합니다.")
    @GetMapping("/api/v1/auth/saml_idp/sso")
    String getSamlLoginForm();
    
    /**
     * 시스템 간 로그인
     * 
     * @param clientSecret 클라이언트 시크릿
     * @param clientName 클라이언트 이름
     * @param systemLoginPayload 시스템 로그인 요청 데이터
     * @return 액세스 토큰 응답
     */
    @Operation(summary = "시스템 간 로그인", description = "시스템에서 관리하는 사용자 정보를 업데이트하고 액세스 토큰을 발급받습니다.")
    @PostMapping(value = "/api/v1/auth/login/system", consumes = MediaType.APPLICATION_JSON_VALUE)
    AccessTokenResponse loginSystem(
        @RequestParam("client_secret") String clientSecret,
        @RequestParam(value = "client_name", defaultValue = "default") String clientName,
        @RequestBody SystemLoginPayload systemLoginPayload
    );
    
    /**
     * 로그아웃
     * 
     * @param username 로그아웃할 사용자명
     */
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃하고 액세스 토큰을 무효화합니다.")
    @PostMapping("/api/v1/auth/logout")
    void logoutFromSystem(@RequestParam("username") String username);
    
    /**
     * 리프레시 토큰을 사용한 액세스 토큰 갱신
     * 
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰 응답
     */
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/api/v1/auth/token/refresh")
    AccessTokenResponseWithProject refreshToken(@RequestParam("refresh_token") String refreshToken);
    
    /**
     * 토큰 교환
     * 
     * @param toExchangeClientName 교환할 클라이언트 이름
     * @param currentGroup 선택된 그룹 경로
     * @return 새로운 액세스 토큰 응답
     */
    @Operation(summary = "토큰 교환", description = "선택한 프로젝트에 대한 인증 토큰을 재발급받습니다.")
    @GetMapping("/api/v1/auth/token/exchange")
    AccessTokenResponseWithProject exchangeToken(
        @RequestParam(value = "to_exchange_client_name", defaultValue = "default") String toExchangeClientName,
        @RequestParam(value = "current_group", required = false) String currentGroup
    );
    
    // ================================
    // 정책 관리 API
    // ================================
    
    /**
     * 정책 생성
     * 
     * @param policyRequest 정책 생성 요청
     * @return 생성된 정책 정보
     */
    @Operation(summary = "정책 추가", description = "새로운 정책을 추가합니다.")
    @PostMapping("/api/v1/auth/policy")
    List<PolicyRequest> createPolicy(
            @RequestParam("resource_url") String resourceUrl,
            @RequestBody List<PolicyRequest> policyRequests
    );
    
    /**
     * 정책 조회
     * 
     * @return 정책 목록
     */
    @Operation(summary = "정책 조회", description = "모든 정책을 조회합니다.")
    @GetMapping("/api/v1/auth/policy")
    List<PolicyRequest> getPolicy(@RequestParam("resource_url") String resourceUrl);
    
    /**
     * 정책 수정
     * 
     * @param resourceUrl 리소스 URL
     * @param policyRequests 정책 수정 요청 목록
     * @return 수정된 정책 정보 목록
     */
    @Operation(summary = "정책 수정", description = "기존 정책을 수정합니다.")
    @PutMapping("/api/v1/auth/policy")
    List<PolicyRequest> updatePolicy(
        @RequestParam("resource_url") String resourceUrl,
        @RequestBody List<PolicyRequest> policyRequests
    );
    
    /**
     * 기본 정책 생성
     * 
     * @param defaultPolicyRequest 기본 정책 생성 요청
     * @return 생성된 기본 정책 정보
     */
    @Operation(summary = "기본 정책 생성", description = "기본 정책을 생성합니다.")
    @PutMapping("/api/v1/auth/policy/default")
    DefaultPolicyResponse createDefaultPolicies(@RequestBody DefaultPolicyRequest defaultPolicyRequest);
    
    /**
     * 기본 정책 삭제
     */
    @Operation(summary = "기본 정책 삭제", description = "기본 정책을 삭제합니다.")
    @DeleteMapping("/api/v1/auth/policy/default")
    void deleteDefaultPolicies();
    
    /**
     * 동적 정책 수정
     * 
     * @param dynamicPolicyRequest 동적 정책 수정 요청
     * @return 수정된 동적 정책 정보
     */
    @Operation(summary = "동적 정책 수정", description = "동적 정책을 수정합니다.")
    @PutMapping("/api/v1/auth/policy/dynamic")
    DynamicPolicyResponse updateDynamicPolicies(@RequestBody DynamicPolicyRequest dynamicPolicyRequest);
    
    /**
     * 동적 정책 삭제
     */
    @Operation(summary = "동적 정책 삭제", description = "동적 정책을 삭제합니다.")
    @DeleteMapping("/api/v1/auth/policy/dynamic")
    void deleteDynamicPolicies();
    
    // ================================
    // 역할 관리 API
    // ================================
    
    /**
     * 기본 역할 생성
     * 
     * @param defaultRoleRequest 기본 역할 생성 요청
     * @return 생성된 기본 역할 정보
     */
    @Operation(summary = "기본 역할 생성", description = "기본 역할을 생성합니다.")
    @PostMapping("/api/v1/auth/role/default")
    DefaultRoleResponse createDefaultRoles(@RequestBody DefaultRoleRequest defaultRoleRequest);
    
    /**
     * 사이드바 메뉴 생성
     * 
     * @param roleName 역할 이름
     * @param sidebarMenuRequest 사이드바 메뉴 생성 요청
     * @return 생성된 사이드바 메뉴 정보
     */
    @Operation(summary = "사이드바 메뉴 생성", description = "특정 역할에 대한 사이드바 메뉴를 생성합니다.")
    @PostMapping("/api/v1/auth/role/sbn/{role_name}")
    SidebarMenuResponse createSidebarMenu(
        @PathVariable("role_name") String roleName,
        @RequestBody SidebarMenuRequest sidebarMenuRequest
    );
    
    /**
     * 사이드바 메뉴 조회
     * 
     * @param roleName 역할 이름
     * @return 사이드바 메뉴 정보
     */
    @Operation(summary = "사이드바 메뉴 조회", description = "특정 역할의 사이드바 메뉴를 조회합니다.")
    @GetMapping("/api/v1/auth/role/sbn/{role_name}")
    SidebarMenuResponse getSidebarMenu(@PathVariable("role_name") String roleName);
    
    /**
     * 사이드바 메뉴 수정
     * 
     * @param roleName 역할 이름
     * @param sidebarMenuRequest 사이드바 메뉴 수정 요청
     * @return 수정된 사이드바 메뉴 정보
     */
    @Operation(summary = "사이드바 메뉴 수정", description = "특정 역할의 사이드바 메뉴를 수정합니다.")
    @PutMapping("/api/v1/auth/role/sbn/{role_name}")
    SidebarMenuResponse updateSidebarMenu(
        @PathVariable("role_name") String roleName,
        @RequestBody SidebarMenuRequest sidebarMenuRequest
    );
    
    /**
     * 사이드바 메뉴 삭제
     * 
     * @param roleName 역할 이름
     */
    @Operation(summary = "사이드바 메뉴 삭제", description = "특정 역할의 사이드바 메뉴를 삭제합니다.")
    @DeleteMapping("/api/v1/auth/role/sbn/{role_name}")
    void deleteSidebarMenu(@PathVariable("role_name") String roleName);
    
    /**
     * 프로젝트별 사이드바 메뉴 조회
     * 
     * @return 프로젝트별 사이드바 메뉴 목록
     */
    @Operation(summary = "프로젝트별 사이드바 메뉴 조회", description = "프로젝트별 사이드바 메뉴를 조회합니다.")
    @GetMapping("/api/v1/auth/role/sbn")
    SidebarMenuListResponse getSidebarMenuByProject();
    
    // ================================
    // 권한 관리 API
    // ================================
    
    /**
     * 권한 정책 리소스 조회
     * 
     * @return 권한 정책 리소스 목록
     */
    @Operation(summary = "권한 정책 리소스 조회", description = "권한 정책 리소스를 조회합니다.")
    @GetMapping("/api/v1/auth/permission/resource")
    PermissionResourceResponse getPermissionPoliciesApi();
    
    /**
     * 권한 리소스 추가
     * 
     * @param permissionResourceRequest 권한 리소스 추가 요청
     * @return 추가된 권한 리소스 정보
     */
    @Operation(summary = "권한 리소스 추가", description = "권한 리소스를 추가합니다.")
    @PostMapping("/api/v1/auth/permission/resource")
    PermissionResourceResponse appendPermissionResources(@RequestBody PermissionResourceRequest permissionResourceRequest);
    
    /**
     * 권한 리소스 덮어쓰기
     * 
     * @param permissionResourceRequest 권한 리소스 덮어쓰기 요청
     * @return 덮어쓴 권한 리소스 정보
     */
    @Operation(summary = "권한 리소스 덮어쓰기", description = "권한 리소스를 덮어씁니다.")
    @PutMapping("/api/v1/auth/permission/resource")
    PermissionResourceResponse overwritePermissionResources(@RequestBody PermissionResourceRequest permissionResourceRequest);
}
