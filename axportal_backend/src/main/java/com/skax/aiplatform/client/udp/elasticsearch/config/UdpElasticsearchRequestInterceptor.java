package com.skax.aiplatform.client.udp.elasticsearch.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * UDP Elasticsearch API 요청 인터셉터
 * 
 * <p>
 * 모든 Elasticsearch API 요청에 Basic Auth 인증 헤더를 자동으로 추가합니다.
 * </p>
 * 
 * <h3>추가되는 헤더:</h3>
 * <ul>
 *   <li><strong>Authorization</strong>: Basic {base64(username:password)}</li>
 *   <li><strong>Content-Type</strong>: application/json</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Slf4j
public class UdpElasticsearchRequestInterceptor implements RequestInterceptor {

    private final String authHeader;

    /**
     * UDP Elasticsearch RequestInterceptor 생성자
     * 
     * @param username Elasticsearch 사용자명
     * @param password Elasticsearch 비밀번호
     */
    public UdpElasticsearchRequestInterceptor(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        this.authHeader = "Basic " + encodedCredentials;
        
        log.info("🔐 [UDP Elasticsearch Interceptor] Basic Auth 헤더 생성 완료");
    }

    @Override
    public void apply(RequestTemplate template) {
        // Basic Auth 헤더 추가
        template.header("Authorization", authHeader);
        
        // Content-Type 헤더 추가
        if (!template.headers().containsKey("Content-Type")) {
            template.header("Content-Type", "application/json");
        }
        
        log.debug("🔑 [UDP Elasticsearch] API 요청 - Method: {}, URL: {}", 
                template.method(), template.url());
    }
}

