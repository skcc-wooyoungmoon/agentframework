package com.skax.aiplatform.client.ione.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * iONE 서비스용 요청 인터셉터
 * 
 * @author system
 * @since 2025-09-16
 */
@Slf4j
public class IoneRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        System.out.println("🔍 [IONE REQUEST INTERCEPTOR] 호출됨! - URL: " + template.url() + ", Method: " + template.method());
        log.info("🔍 [IONE REQUEST] API 호출 시작 - URL: {}, Method: {}", 
                  template.url(), template.method());
        
        // HTTPS 요청인지 확인하여 SSL 우회 설정 로깅
        if (template.url().startsWith("https://")) {
            log.info("🔒 [IONE SSL] HTTPS 요청 감지 - SSL 우회 설정이 적용됩니다");
        }
        
        // Content-Type 설정
        template.header("Content-Type", "application/json");
        
        // User-Agent 설정
        template.header("User-Agent", "AXPORTAL-Backend/1.0");

        // iONE 서비스 관리 토큰 설정
        template.header("iONESvcMng-Token", "YXBpR3R3U3ZjTW5nOkJKWjRXODdJODBFNFpWVTBSMzEx");
        
        System.out.println("✅ [IONE REQUEST INTERCEPTOR] 헤더 설정 완료!");
        log.info("✅ [IONE REQUEST] 요청 헤더 설정 완료 - URL: {}, Method: {}", 
                  template.url(), template.method());
    }
}
