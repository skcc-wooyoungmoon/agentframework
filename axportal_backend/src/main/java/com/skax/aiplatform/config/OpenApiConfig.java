package com.skax.aiplatform.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI 3 문서화 설정
 * 
 * <p>
 * Swagger UI를 통한 API 문서화 설정을 담당합니다.
 * JWT 인증 스키마와 프로젝트 정보를 포함하며, 도메인별 그룹 기능을 제공합니다.
 * </p>
 * 
 * <h3>지원 그룹:</h3>
 * <ul>
 * <li><strong>Home</strong>: 홈 화면 관련 API (프로젝트, IDE, 알람)</li>
 * <li><strong>Auth</strong>: 인증 및 사용자 관리 API</li>
 * <li><strong>Agent</strong>: AI 에이전트 관리 API</li>
 * <li><strong>Model</strong>: AI 모델 관리 및 배포 API</li>
 * <li><strong>Prompt</strong>: 프롬프트 및 워크플로우 관리 API</li>
 * <li><strong>Data</strong>: 데이터 카탈로그 및 도구 API</li>
 * <li><strong>Deploy</strong>: 배포 및 API 게이트웨이 API</li>
 * <li><strong>Admin</strong>: 관리자 기능 API</li>
 * <li><strong>Knowledge</strong>: 지식 관리 API</li>
 * <li><strong>Notice</strong>: 공지사항 API</li>
 * <li><strong>Resource</strong>: 시스템 리소스 관리 API</li>
 * <li><strong>Sample/Log</strong>: 샘플 데이터 및 로그 API</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 2.0.0
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {
    @Value("${app.name:AxportalBackend}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:Spring Boot 기반의 AI Portal RESTful API}")
    private String appDescription;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.profiles.active:elocal}")
    private String activeProfile;

    /**
     * OpenAPI 설정 빈
     * OAuth2PasswordBearer와 HTTPBearer 두 가지 인증 방식을 지원
     * 
     * <p>
     * 글로벌 보안 요구사항을 설정하여 OAuth2PasswordBearer로 로그인한 후
     * 모든 보안이 필요한 API에 자동으로 토큰이 적용됩니다.
     * 개별 API에서 {@code @SecurityRequirement} 어노테이션으로 추가 제어 가능합니다.
     * </p>
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                // 글로벌 보안 요구사항 설정 - OAuth2PasswordBearer 우선적용
                .security(getSecurityRequirements())
                .components(getComponents());
    }

    /**
     * API 정보 설정
     * 
     * @return API 정보 객체
     */
    private Info getApiInfo() {
        return new Info()
                .title(appName + " API")
                .description(appDescription)
                .version(appVersion);
    }

    /**
     * 서버 정보 설정 (환경별 자동 구성)
     * 
     * <p>
     * 현재 활성 프로파일에 따라 서버 목록을 동적으로 구성합니다.
     * </p>
     * 
     * <h3>환경별 서버 구성:</h3>
     * <ul>
     * <li><strong>elocal</strong>: 외부 로컬 개발 환경</li>
     * <li><strong>local</strong>: 내부 로컬 개발 환경</li>
     * <li><strong>edev</strong>: 외부 개발 환경</li>
     * <li><strong>dev</strong>: 내부 개발 환경</li>
     * <li><strong>prod</strong>: 운영 환경</li>
     * </ul>
     * 
     * @return 환경에 맞는 서버 정보 리스트
     */
    private List<Server> getServers() {
        List<Server> servers = new java.util.ArrayList<>();

        // 현재 실행 중인 서버 (항상 최우선)
        // String currentHost = getCurrentServerHost();
        // servers.add(new Server()
        // .url(currentHost)
        // .description("🟢 현재 실행 서버 (" + activeProfile + ")"));

        // 환경별 추가 서버 목록
        switch (activeProfile.toLowerCase()) {
            case "elocal":
                addElocalServers(servers);
                addEdevServers(servers);
                addDevServers(servers);
                addProdServers(servers);
                break;
            case "local":
                addLocalServers(servers);
                addDevServers(servers);
                addProdServers(servers);
                break;
            case "edev":
                addEdevServers(servers);
                break;
            case "dev":
                addDevServers(servers);
                break;
            case "prod":
                addProdServers(servers);
                break;
            default:
                addDefaultServers(servers);
                break;
        }

        return servers;
    }

    /**
     * 외부 로컬 환경 (elocal) 서버 목록 추가
     */
    private void addElocalServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://localhost:8080" + getContextPathOrEmpty())
                .description("🔵 로컬 개발 서버 (8080)"));
    }

    /**
     * 내부 로컬 환경 (local) 서버 목록 추가
     */
    private void addLocalServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://localhost:8080" + getContextPathOrEmpty())
                .description("🔵 내부 로컬 서버"));

    }

    /**
     * 외부 개발 환경 (edev) 서버 목록 추가
     */
    private void addEdevServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://k8s-axportal-backend-aae7c1a56e-1948588605.ap-northeast-2.elb.amazonaws.com"
                        + getContextPathOrEmpty())
                .description("🟡 외부 개발 서버 (AWS ELB)"));

    }

    /**
     * 내부 개발 환경 (dev) 서버 목록 추가
     */
    private void addDevServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://portal-backend.gapdev.shinhan.com" + getContextPathOrEmpty())
                .description("🟡 내부 개발 서버 (OpenShift)"));

    }

    /**
     * 운영 환경 (prod) 서버 목록 추가
     */
    private void addProdServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://portal-backend.gap.shinhan.com" + getContextPathOrEmpty())
                .description("🔴 내부 운영 서버"));
    }

    /**
     * 기본 서버 목록 추가 (알 수 없는 환경)
     */
    private void addDefaultServers(List<Server> servers) {
        servers.add(new Server()
                .url("http://localhost:8080" + getContextPathOrEmpty())
                .description("🔵 기본 로컬 서버"));
    }

    /**
     * Context Path가 있는 경우 반환, 없으면 빈 문자열 반환
     * 
     * @return Context Path 또는 빈 문자열
     */
    private String getContextPathOrEmpty() {
        return contextPath != null && !contextPath.isEmpty() ? contextPath : "";
    }

    /**
     * 보안 요구사항 설정
     * OAuth2PasswordBearer를 우선 적용하고, HTTPBearer를 대안으로 제공
     * 
     * @return 보안 요구사항 객체
     */
    private List<SecurityRequirement> getSecurityRequirements() {
        return List.of(
                new SecurityRequirement().addList("OAuth2PasswordBearer"),
                new SecurityRequirement().addList("HTTPBearer"));
    }

    /**
     * 컴포넌트 설정 (보안 스키마 포함)
     * OAuth2PasswordBearer와 HTTPBearer 두 가지 인증 방식 지원
     * 
     * @return 컴포넌트 객체
     */
    private Components getComponents() {
        return new Components()
                // OAuth2 Password Bearer 인증 스키마
                .addSecuritySchemes("OAuth2PasswordBearer",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .password(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .tokenUrl("/api/auth/login"))))
                // HTTP Bearer 인증 스키마 (JWT)
                .addSecuritySchemes("HTTPBearer",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)
                                .description("🔑 HTTP Bearer 토큰 인증 (JWT)\n\n" +
                                        "💡 사용법:\n" +
                                        "1. 로그인 API를 통해 JWT 토큰을 수동 획득하세요\n" +
                                        "2. 아래 입력란에 토큰을 입력하세요 (Bearer 접두사 제외)\n" +
                                        "3. Authorize 버튼을 클릭하여 인증을 완료하세요\n\n" +
                                        "✅ 올바른 형식: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n" +
                                        "❌ 잘못된 형식: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n\n" +
                                        "🔍 테스트용 API: /auth/test/** 엔드포인트를 활용하세요"));
    }

    // ==================== 그룹별 API 문서화 설정 ====================

    /**
     * 전체 API 그룹
     * 
     * @return 전체 API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("00-전체")
                .displayName("⚙️ 전체 API")
                .pathsToMatch("/**")
                .build();
    }

    /**
     * Home 관련 API 그룹
     * 홈 화면, 프로젝트, IDE, 알람 관련 API
     * 
     * @return Home API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi homeApi() {
        return GroupedOpenApi.builder()
                .group("01-home")
                .displayName("🏠 Home - 홈 화면")
                .pathsToMatch("/home/**", "/health", "/info", "/logging-test", "/secure")
                .build();
    }

    /**
     * Auth 관련 API 그룹
     * 인증, 로그인, 사용자 관리 관련 API
     * 
     * @return Auth API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("02-auth")
                .displayName("🔐 Auth - 인증/사용자")
                .pathsToMatch("/auth/**", "/login", "/logout", "/user/**")
                .build();
    }

    /**
     * Agent 관련 API 그룹
     * AI 에이전트, 도구, MCP 관련 API
     * 
     * @return Agent API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi agentApi() {
        return GroupedOpenApi.builder()
                .group("03-agent")
                .displayName("🤖 Agent - AI 에이전트")
                .pathsToMatch("/agent/**", "/agentTool/**", "/agentMcp/**", "/agentDeploy/**", "/agentLog/**")
                .build();
    }

    /**
     * Model 관련 API 그룹
     * AI 모델, 배포, 파인튜닝, 플레이그라운드 관련 API
     * 
     * @return Model API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi modelApi() {
        return GroupedOpenApi.builder()
                .group("04-model")
                .displayName("🧠 Model - AI 모델")
                .pathsToMatch("/v1/models/**", "/modelGarden/**", "/modelCtlg/**",
                        "/modelDeploy/**", "/modelDeployLog/**", "/v1/finetuning/**",
                        "/model-playground/**")
                .build();
    }

    /**
     * Prompt 관련 API 그룹
     * 프롬프트, Few-shot, 워크플로우 관련 API
     * 
     * @return Prompt API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi promptApi() {
        return GroupedOpenApi.builder()
                .group("05-prompt")
                .displayName("📝 Prompt - 프롬프트")
                .pathsToMatch("/fewShot/**", "/inference-prompts/**", "/workflow/**")
                .build();
    }

    /**
     * Data 관련 API 그룹
     * 데이터 카탈로그, 도구, 벡터DB 관련 API
     * 
     * @return Data API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi dataApi() {
        return GroupedOpenApi.builder()
                .group("06-data")
                .displayName("📊 Data - 데이터 관리")
                .pathsToMatch("/dataCtlg/**", "/dataTool/**")
                .build();
    }

    /**
     * Deploy 관련 API 그룹
     * 배포, API 게이트웨이 관련 API
     * 
     * @return Deploy API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi deployApi() {
        return GroupedOpenApi.builder()
                .group("07-deploy")
                .displayName("🚀 Deploy - 배포 관리")
                .pathsToMatch("/apigw/**", "/agentDeploy/**")
                .build();
    }

    /**
     * Admin 관련 API 그룹
     * 관리자 기능, 사용자 관리, 리소스 관리 관련 API
     * 
     * @return Admin API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("08-admin")
                .displayName("🏢 Admin - 관리자")
                .pathsToMatch("/admin/**", "/v2/admin/**")
                .build();
    }

    /**
     * Knowledge 관련 API 그룹
     * 지식 관리, 문서 관리 관련 API
     * 
     * @return Knowledge API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi knowledgeApi() {
        return GroupedOpenApi.builder()
                .group("09-knowledge")
                .displayName("📚 Knowledge - 지식 관리")
                .pathsToMatch("/knowledge/**")
                .build();
    }

    /**
     * Notice 관련 API 그룹
     * 공지사항 조회 및 관리 관련 API
     * 
     * @return Notice API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi noticeApi() {
        return GroupedOpenApi.builder()
                .group("10-notice")
                .displayName("📰 Notice - 공지사항")
                .pathsToMatch("/notices/**")
                .build();
    }

    /**
     * Resource 관련 API 그룹
     * 시스템 리소스, 클러스터 관리 관련 API
     * 
     * @return Resource API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi resourceApi() {
        return GroupedOpenApi.builder()
                .group("11-resource")
                .displayName("🛠️ Resource - 리소스")
                .pathsToMatch("/v1/resources/**")
                .build();
    }

    /**
     * Sample/Log 관련 API 그룹
     * 샘플 데이터, 로그, 테스트 관련 API
     * 
     * @return Sample/Log API GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi sampleLogApi() {
        return GroupedOpenApi.builder()
                .group("12-sample-log")
                .displayName("🔍 Sample/Log - 샘플/로그")
                .pathsToMatch("/samples/**", "/cors/**")
                .build();
    }

}
