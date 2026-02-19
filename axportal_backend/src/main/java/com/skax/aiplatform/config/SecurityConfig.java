package com.skax.aiplatform.config;

import com.skax.aiplatform.common.security.JwtAccessDeniedHandler;
import com.skax.aiplatform.common.security.JwtAuthenticationEntryPoint;
import com.skax.aiplatform.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 *
 * <p>애플리케이션의 보안 설정을 담당합니다.
 * JWT 기반 인증, CORS 설정, 엔드포인트 접근 권한 등을 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * 보안 필터 체인 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 오류
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용으로 인해 불필요)
            .csrf(AbstractHttpConfigurer::disable)

            // 완전 개방형 CORS 설정 - 모든 접근 허용
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();

                // 모든 Origin 허용 (가장 개방적)
                corsConfig.setAllowedOriginPatterns(java.util.List.of("*"));
                corsConfig.addAllowedOrigin("*");


                // 모든 HTTP 메서드 허용
                corsConfig.setAllowedMethods(java.util.List.of("*"));

                // 모든 헤더 허용
                corsConfig.setAllowedHeaders(java.util.List.of("*"));

                // 자격 증명 허용 비활성화 (allowedOrigin("*")와 함께 사용 시 필요)
                corsConfig.setAllowCredentials(false);

                // 모든 응답 헤더 노출
                corsConfig.setExposedHeaders(java.util.List.of(
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
                ));

                // Preflight 요청 캐시 시간 최대값 (24시간)
                corsConfig.setMaxAge(86400L);

                return corsConfig;
            }))

            // 세션 사용하지 않음 (JWT 기반 인증)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 예외 처리 설정
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler))

            // 요청별 인증 설정
            .authorizeHttpRequests(authz -> authz
                // 공개 엔드포인트 (인증 불필요)
                .requestMatchers(
                    "/auth/login",
                    "/auth/validate",
                    "/auth/register",
                    "/auth/refresh",
                    "/auth/login-molimate",
                    "/auth/login-swing",
                    "/auth/swing-sms",
                    "/auth/swing-sms-check",
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
                ).permitAll()
                
                // elocal 프로필일 때만 H2 콘솔 접근 허용
                .requestMatchers("/h2-console/**").access((authentication, context) -> {
                    return "elocal".equals(activeProfile) ? 
                        new org.springframework.security.authorization.AuthorizationDecision(true) :
                        new org.springframework.security.authorization.AuthorizationDecision(false);
                })
                
                .requestMatchers(
                    // Elasticsearch 프록시 (에이전트 시스템 로그 검색) - 인증 우회 허용
                    "/api/agentDeploy/elastic/**",
                    "/agentDeploy/elastic/**",
                    // 모델 가든 - 인증 우회 허용
                    "/modelGarden/file-import-complete",
                    "/modelGarden/vaccine-complete",
                    "/modelGarden/vulnerability-complete",
                    "/modelGarden/import-complete"
                ).permitAll()

                // 관리자 전용 엔드포인트
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated())

            // 보안 헤더 설정 (elocal에서만 H2 콘솔을 위한 Frame Options 완화)
            .headers(headers -> headers
                // elocal 프로필일 때만 Frame options 완화 (H2 콘솔용)
                .frameOptions(frameOptions -> {
                    if ("elocal".equals(activeProfile)) {
                        frameOptions.sameOrigin();
                    } else {
                        frameOptions.deny();
                    }
                })
                // 기타 보안 헤더는 활성화 유지
                .contentTypeOptions(contentType -> {
                })  // X-Content-Type-Options: nosniff
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)  // 1년
                    .includeSubDomains(true))  // HTTPS에서만 적용됨
                // 캐시 제어 헤더 사용자 정의
                .cacheControl(cache -> cache.disable()))

            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 인코더 빈
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
