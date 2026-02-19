package com.skax.aiplatform.client.deepsecurity.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * DeepSecurity API 에러 디코더
 * 
 * <p>DeepSecurity API 호출 시 발생하는 HTTP 에러를 적절한 예외로 변환합니다.</p>
 * 
 * @author system
 * @since 2025-01-15
 */
@Slf4j
public class DeepSecurityErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("DeepSecurity API 에러 발생 - Method: {}, Status: {}, Reason: {}", 
                 methodKey, response.status(), response.reason());
        
        switch (response.status()) {
            case 400:
                return new RuntimeException("DeepSecurity API 요청이 잘못되었습니다: " + response.reason());
            case 401:
                return new RuntimeException("DeepSecurity API 인증이 실패했습니다: " + response.reason());
            case 403:
                return new RuntimeException("DeepSecurity API 접근이 거부되었습니다: " + response.reason());
            case 404:
                return new RuntimeException("DeepSecurity API 리소스를 찾을 수 없습니다: " + response.reason());
            case 500:
                return new RuntimeException("DeepSecurity API 서버 내부 오류가 발생했습니다: " + response.reason());
            case 502:
                return new RuntimeException("DeepSecurity API 게이트웨이 오류가 발생했습니다: " + response.reason());
            case 503:
                return new RuntimeException("DeepSecurity API 서비스를 사용할 수 없습니다: " + response.reason());
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
