package com.skax.aiplatform.client.udp.config;

import feign.Client;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * UDP 서비스용 Feign 설정
 * 
 * <p>
 * 이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 UDP FeignClient에만 적용되도록 합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 */
public class UdpDataikuFeignConfig {

    /**
     * UDP 전용 RequestInterceptor 생성
     * 
     * <p>
     * FeignClient configuration에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return UDP RequestInterceptor
     */
    @Bean
    public RequestInterceptor udpRequestInterceptor() {
        return new UdpRequestInterceptor();
    }

    /**
     * Feign 재시도 설정
     * 
     * @return Retryer 설정
     */
    @Bean
    public Retryer udpRetryer() {
        return new Retryer.Default(2000, 10000, 5);
    }

    /**
     * UDP API용 ErrorDecoder 설정
     * 
     * @return UDP ErrorDecoder
     */
    @Bean
    public ErrorDecoder udpErrorDecoder() {
        return new UdpErrorDecoder();
    }

}