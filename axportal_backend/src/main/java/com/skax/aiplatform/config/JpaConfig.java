package com.skax.aiplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * JPA 설정
 *
 * <p>JPA Repository 스캔 및 Auditing 설정을 담당합니다.
 * 엔티티의 생성자/수정자 자동 설정 기능을 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-01
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(basePackages = "com.skax.aiplatform.repository")
public class JpaConfig {


    /**
     * JPA Auditing용 사용자 정보 제공자
     *
     * <p>현재 인증된 사용자의 정보를 엔티티의 생성자/수정자 필드에 자동으로 설정합니다.
     * Spring Security Context에서 현재 사용자 정보를 가져옵니다.</p>
     *
     * @return AuditorAware 구현체
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증되지 않은 경우 또는 익명 사용자인 경우
            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("system");
            }

            // 인증된 사용자의 경우
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return Optional.of(userDetails.getUsername());
            }

            // 기본값
            return Optional.of("system");
        };
    }

}
