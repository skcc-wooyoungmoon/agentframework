package com.skax.aiplatform.client.shinhan.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Shinhan 승인 API 요청 인터셉터
 * 
 * <p>Shinhan 승인 API 호출 시 공통으로 적용되는 헤더를 자동으로 추가합니다.
 * Content-Type 등의 필수 헤더를 설정합니다.</p>
 * 
 * <p>이 클래스는 {@link ShinhanClientConfig}에서 빈으로 등록되어 사용됩니다.</p>
 * 
 * <h3>자동 추가 헤더:</h3>
 * <ul>
 *   <li><strong>Content-Type</strong>: application/json</li>
 *   <li><strong>Accept</strong>: application/json</li>
 *   <li><strong>User-Agent</strong>: AXPORTAL-Backend/1.0</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-11-17
 * @version 1.0
 */
@Slf4j
public class ShinhanRequestInterceptor implements RequestInterceptor {
    
    @Value("${app.version:1.0}")
    private String appVersion;
    
    /**
     * 요청에 공통 헤더를 추가
     * 
     * <p>모든 Shinhan API 요청에 공통 헤더를 추가합니다.</p>
     * 
     * @param template 요청 템플릿
     */
    @Override
    public void apply(RequestTemplate template) {
        log.debug("🔍 [SHINHAN REQUEST] API 호출 시작 - URL: {}, Method: {}", 
                  template.url(), template.method());
        
        // 기본 헤더 설정
        template.header("Content-Type", "application/json");
        template.header("Accept", "application/json");
        template.header("User-Agent", "AXPORTAL-Backend/" + appVersion);
        
        log.debug("✅ [SHINHAN REQUEST] 요청 헤더 설정 완료 - URL: {}, 메서드: {}", 
                  template.url(), template.method());
    }
}
