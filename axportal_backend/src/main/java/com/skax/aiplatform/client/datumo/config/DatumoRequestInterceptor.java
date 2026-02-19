package com.skax.aiplatform.client.datumo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Datumo API 요청 인터셉터
 * 
 * <p>Datumo API 호출 시 공통으로 적용되는 헤더를 자동으로 추가합니다.
 * 인증 토큰, Content-Type 등의 필수 헤더를 설정합니다.</p>
 * 
 * <p>이 클래스는 {@link DatumoClientConfig}에서 빈으로 등록되어 사용됩니다.</p>
 * 
 * <h3>자동 추가 헤더:</h3>
 * <ul>
 *   <li><strong>Content-Type</strong>: application/json</li>
 *   <li><strong>Accept</strong>: application/json</li>
 *   <li><strong>User-Agent</strong>: AXPORTAL-Backend/1.0</li>
 *   <li><strong>Authorization</strong>: Bearer {token} (토큰이 설정된 경우)</li>
 * </ul>
 * 
 * <h3>인증 토큰 설정:</h3>
 * <p>ThreadLocal을 사용하여 요청별로 다른 토큰을 설정할 수 있습니다.</p>
 * <pre>
 * DatumoRequestInterceptor.setAccessToken("your-token");
 * // API 호출
 * DatumoRequestInterceptor.clearAccessToken();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Slf4j
public class DatumoRequestInterceptor implements RequestInterceptor {
    
    /**
     * 요청별 액세스 토큰을 저장하는 ThreadLocal
     */
    private static final ThreadLocal<String> ACCESS_TOKEN = new ThreadLocal<>();
    
    @Value("${app.version:1.0}")
    private String appVersion;
    
    /**
     * 요청에 공통 헤더를 추가
     * 
     * <p>모든 Datumo API 요청에 공통 헤더를 추가하며,
     * 개발계에서는 SSL 우회 설정 정보도 함께 로깅합니다.</p>
     * 
     * @param template 요청 템플릿
     */
    @Override
    public void apply(RequestTemplate template) {
        log.debug("🔍 [DATUMO REQUEST] API 호출 시작 - URL: {}, Method: {}", 
                  template.url(), template.method());
        
        // HTTPS 요청인지 확인하여 SSL 우회 설정 로깅
        if (template.url().startsWith("https://")) {
            log.debug("🔒 [DATUMO SSL] HTTPS 요청 감지 - SSL 우회 설정이 적용됩니다");
        }
        
        // 기본 헤더 설정
        template.header("Content-Type", "application/json");
        template.header("Accept", "application/json");
        template.header("User-Agent", "AXPORTAL-Backend/" + appVersion);
        
        // 인증 토큰이 설정된 경우 Authorization 헤더 추가
        String accessToken = ACCESS_TOKEN.get();
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            template.header("Authorization", "Bearer " + accessToken);
            log.debug("🔑 [DATUMO AUTH] Authorization 헤더 추가: Bearer {}", maskToken(accessToken));
        }
        
        log.debug("✅ [DATUMO REQUEST] 요청 헤더 설정 완료 - URL: {}, 메서드: {}", 
                  template.url(), template.method());
    }
    
    /**
     * 현재 스레드에 액세스 토큰 설정
     * 
     * @param accessToken 설정할 액세스 토큰
     */
    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN.set(accessToken);
    }
    
    /**
     * 현재 스레드의 액세스 토큰 제거
     */
    public static void clearAccessToken() {
        ACCESS_TOKEN.remove();
    }
    
    /**
     * 현재 스레드의 액세스 토큰 조회
     * 
     * @return 설정된 액세스 토큰
     */
    public static String getAccessToken() {
        return ACCESS_TOKEN.get();
    }
    
    /**
     * 토큰 마스킹 (로깅용)
     * 
     * @param token 원본 토큰
     * @return 마스킹된 토큰
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "***" + token.substring(token.length() - 4);
    }
}