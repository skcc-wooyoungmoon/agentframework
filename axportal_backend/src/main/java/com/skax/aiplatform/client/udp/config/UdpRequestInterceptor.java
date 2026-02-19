package com.skax.aiplatform.client.udp.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * UDP API 요청 인터셉터
 * 
 * <p>
 * UDP API 호출 시 공통 헤더를 자동으로 추가합니다.
 * </p>
 * 
 * <h3>추가되는 헤더:</h3>
 * <ul>
 * <li><strong>Content-Type</strong>: application/json</li>
 * <li><strong>User-Agent</strong>: AXPORTAL-Backend/1.0</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Slf4j
public class UdpRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.debug("UDP API 요청 인터셉터 실행 - URL: {}, Method: {}",
                template.url(), template.method());

        // Content-Type을 URL 경로에 따라 조건적으로 설정
        if (template.url().contains("/portal/udp/api/datasetcard/search/v2")) {
            log.debug("데이터셋 검색 API v2 - Content-Type은 @PostMapping consumes 속성에서 처리");
        } else if (template.url().contains("/portal/udp/api/doc/lists/v2")) {
            log.debug("문서 검색 API v2 - Content-Type은 @PostMapping consumes 속성에서 처리");
        } else {
            // 다른 API는 JSON을 사용
            template.header("Content-Type", "application/json");
        }

        template.header("User-Agent", "AXPORTAL-Backend/1.0");

        // Bearer 토큰이 필요한 경우 (Dataiku API 등)
        if (template.url().contains("/dataiku/") && !template.headers().containsKey("Authorization")) {
            log.debug("Dataiku API 호출 - Authorization 헤더가 필요합니다");
            // 실제 토큰은 각 서비스에서 직접 설정
        }

        log.debug("UDP API 요청 인터셉터 완료 - 공통 헤더 추가됨");
    }
}