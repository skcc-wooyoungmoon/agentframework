package com.skax.aiplatform.client.deepsecurity.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * DeepSecurity API 요청 인터셉터
 * 
 * <p>DeepSecurity API 호출 시 공통 헤더를 자동으로 추가합니다.</p>
 * 
 * <p>이 클래스는 @Component 어노테이션을 사용하지 않습니다.
 * DeepSecurityFeignConfig에서만 빈으로 등록되어 전역 Bean 등록을 방지합니다.</p>
 * 
 * @author system
 * @since 2025-01-15
 */
@Slf4j
public class DeepSecurityRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Content-Type 헤더 설정
        template.header("Content-Type", "application/json");
        
        // User-Agent 헤더 설정
        template.header("User-Agent", "AxportalBackend/1.0");
        
        // Accept 헤더 설정
        template.header("Accept", "application/json");
        
        // 요청 로깅 (디버그용)
        if (log.isDebugEnabled()) {
            log.debug("DeepSecurity API 요청: {} {}", template.method(), template.url());
        }
    }
}
