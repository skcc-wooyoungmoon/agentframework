package com.skax.aiplatform.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 정의 보안 헤더 필터
 *
 * <p>Spring Security의 기본 헤더 외에 추가적인 보안 헤더만 설정합니다.</p>
 * <p>JWT 토큰 검증은 JwtAuthenticationFilter에서 담당합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-05
 * @version 2.0.0
 */
@Slf4j
@Component
public class CustomSecurityHeadersFilter extends OncePerRequestFilter {

    /**
     * 보안 헤더를 추가하지 않을 경로 목록 (SecurityConfig와 동일하게 유지)
     */
    private final List<String> excludedPaths = Arrays.asList(
            // 인증 관련
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/auth/login-molimate",
            "/api/auth/login-swing",
            "/api/auth/swing-sms",
            "/api/auth/swing-sms-check",

            // 시스템
            "/health",
            "/actuator/health",
            
            // API 문서화
            "/swagger-ui/",
            "/v3/api-docs/",
            "/swagger-resources/",
            "/webjars/",
            
            // 개발 도구
            "/h2-console/",
            
            // 기타
            "/favicon.ico",
            "/error",
            
            // 개발/테스트 전용
            "/api/cors/",
            "/api/sample/"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // OPTIONS 요청은 CORS preflight이므로 필터링하지 않음
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("OPTIONS 요청은 보안 헤더 필터를 건너뜁니다: {}", path);
            return true;
        }
        
        // 제외 경로 확인
        boolean shouldExclude = excludedPaths.stream().anyMatch(path::startsWith);
        
        if (shouldExclude) {
            log.debug("제외된 경로입니다. 보안 헤더를 추가하지 않습니다: {} {}", method, path);
        } else {
            log.debug("보안 헤더를 추가할 경로입니다: {} {}", method, path);
        }
        
        return shouldExclude;    
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("보안 헤더 필터 시작: {} {}", method, path);

        try {
            // 보안 헤더 추가
            addSecurityHeaders(response);
            
            // 캐시 제어 헤더 설정
            addCacheHeaders(request, response);
            
            log.debug("보안 헤더가 성공적으로 추가되었습니다: {} {}", method, path);
            
        } catch (RuntimeException re) {
            log.error("보안 헤더 추가 중 오류 발생: {} {} - {}", method, path, re.getMessage());
        } catch (Exception e) {
            log.error("보안 헤더 추가 중 오류 발생: {} {} - {}", method, path, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 보안 헤더 추가
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        // X-Frame-Options: 클릭재킹 방지
        response.setHeader("X-Frame-Options", "DENY");
        
        // X-XSS-Protection: XSS 공격 방지
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Referrer-Policy: 참조 정보 제어
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions-Policy: 브라우저 기능 제어
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        
        // X-Content-Type-Options: MIME 타입 스니핑 방지
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // CORS 관련 헤더는 WebConfig에서 처리하므로 제외
        log.debug("보안 헤더 추가 완료");
    }

    /**
     * 캐시 제어 헤더 설정
     */
    private void addCacheHeaders(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        
        if (uri.startsWith("/api/v1/public")) {
            // 공개 API는 5분 캐시
            response.setHeader("Cache-Control", "public, max-age=300");
            log.debug("공개 API 캐시 헤더 설정: {}", uri);
        } else if (uri.startsWith("/static/") || uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/")) {
            // 정적 리소스는 1시간 캐시
            response.setHeader("Cache-Control", "public, max-age=3600");
            log.debug("정적 리소스 캐시 헤더 설정: {}", uri);
        } else {
            // 기타 리소스는 캐시 안함 (API 응답 등)
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            log.debug("노캐시 헤더 설정: {}", uri);
        }
    }
}