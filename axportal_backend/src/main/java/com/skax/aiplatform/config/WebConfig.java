package com.skax.aiplatform.config;

import com.skax.aiplatform.common.filter.CustomSecurityHeadersFilter;
import com.skax.aiplatform.common.filter.RequestTraceFilter;
import com.skax.aiplatform.common.interceptor.ServiceControllerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 *
 * <p>Spring MVC 관련 설정을 담당합니다.
 * 인터셉터, CORS, 정적 리소스 등의 설정을 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-01
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestTraceFilter requestTraceFilter;
    private final CustomSecurityHeadersFilter customSecurityHeadersFilter;
    private final ServiceControllerInterceptor serviceControllerInterceptor;

    /**
     * 요청 추적 필터 등록
     *
     * @return 필터 등록 빈
     */
    @Bean
    public FilterRegistrationBean<RequestTraceFilter> requestTraceFilterRegistration() {
        FilterRegistrationBean<RequestTraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestTraceFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("requestTraceFilter");
        return registration;
    }

    /**
     * 사용자 정의 보안 헤더 필터 등록
     *
     * @return 필터 등록 빈
     */
    @Bean
    public FilterRegistrationBean<CustomSecurityHeadersFilter> customSecurityHeadersFilterRegistration() {
        FilterRegistrationBean<CustomSecurityHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(customSecurityHeadersFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("customSecurityHeadersFilter");
        return registration;
    }

    /**
     * RestTemplate 빈 등록
     *
     * <p>SktaiTokenController에서 SKTAI API 직접 호출 시 사용</p>
     *
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 인터셉터 등록
     *
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // ServiceControllerInterceptor 등록 - 모든 컨트롤러 요청 전처리
        registry.addInterceptor(serviceControllerInterceptor)
                .addPathPatterns("/**")
                // 공개 엔드포인트 (인증 불필요)
                .excludePathPatterns(
                        "/auth/**",
                        "/common/**",
                        "/cors/**",
                        "/sample/**",
                        "/health",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    /**
     * CORS 설정 - 모든 접근을 허용하는 완전 개방형 설정
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 허용
                .allowedOriginPatterns("*")  // 모든 Origin 패턴 허용
                .allowedOrigins("*")  // 모든 Origin 허용 (가장 개방적)
                .allowedMethods("*")  // 모든 HTTP 메서드 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(false)  // 자격 증명 비활성화 (allowedOrigins("*")와 호환)
                .exposedHeaders(
                        "*",  // 모든 헤더 노출
                        "Authorization",
                        "X-Trace-Id",
                        "X-Span-Id",
                        "Content-Length",
                        "Content-Type",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Max-Age"
                )
                .maxAge(86400);  // Preflight 요청 캐시 시간 최대값 (24시간)
    }
}
