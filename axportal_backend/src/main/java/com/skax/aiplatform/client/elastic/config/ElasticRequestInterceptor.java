package com.skax.aiplatform.client.elastic.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;

/**
 * Elasticsearch API 요청 인터셉터
 *
 * <p>Elasticsearch API 호출 시 공통적으로 적용되는 헤더 및 파라미터를 설정합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Content-Type 설정</strong>: application/json</li>
 *   <li><strong>User-Agent 설정</strong>: 클라이언트 식별</li>
 *   <li><strong>인증 헤더 설정</strong>: Basic Auth 또는 API Key</li>
 *   <li><strong>요청 로깅</strong>: API 호출 추적을 위한 로깅</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-15
 */
@Slf4j
public class ElasticRequestInterceptor implements RequestInterceptor {

    @Value("${elastic.search.auth.username:admin}")
    private String username;

    @Value("${elastic.search.auth.password:Axp2025k8s!}")
    private String password;

    @Override
    public void apply(RequestTemplate template) {
        // Content-Type 설정
        template.header("Content-Type", "application/json");

        // User-Agent 설정
        template.header("User-Agent", "AXPORTAL-Backend/1.0 (Elasticsearch Client)");

        // Accept 헤더 설정
        template.header("Accept", "application/json");

        // Connection Keep-Alive 설정 (성능 향상)
        template.header("Connection", "keep-alive");

        // Basic Authentication 헤더 설정
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        template.header("Authorization", "Basic " + encodedCredentials);

        // 요청 로깅 (디버깅용)
        if (log.isDebugEnabled()) {
            log.debug("Elasticsearch API 요청 - Method: {}, URL: {}, Username: {}",
                    template.method(), template.url(), username);
        }
    }
}