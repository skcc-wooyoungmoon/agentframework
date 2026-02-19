package com.skax.aiplatform.client.lablup.config;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import feign.Client;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;

/**
 * Lablup Feign Client 설정 클래스
 * 
 * <p>
 * Lablup API와의 통신을 위한 Feign Client 설정을 정의합니다.
 * 타임아웃, 재시도 정책, 요청 인터셉터 등을 설정합니다.
 * </p>
 * 
 * <p>
 * 이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 Lablup FeignClient에만 적용되도록 합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
public class LablupClientConfig {

    /**
     * Lablup API 요청 인터셉터 설정
     * 
     * <p>
     * Lablup FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor lablupRequestInterceptor() {
        return new LablupRequestInterceptor();
    }

    /**
     * MultiPart 요청을 지원하는 SpringFormEncoder 설정
     * 
     * <p>
     * Lablup FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * <p>
     * Lablup API의 파일 업로드 요청을 위한 MultiPart Form 인코더를 설정합니다.
     * 기본 SpringEncoder 위에 SpringFormEncoder를 래핑하여 사용합니다.
     * </p>
     * 
     * @param messageConverters HTTP 메시지 컨버터 팩토리
     * @return Encoder SpringFormEncoder (MultiPart 지원)
     */
    @Bean
    public Encoder lablupFeignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * Lablup API Feign 재시도 정책 설정
     * 
     * <p>
     * Lablup FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return Retryer 재시도 정책 (최대 3회, 1초 간격)
     */
    @Bean
    public Retryer lablupRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }

    /**
     * Lablup API Feign 요청 타임아웃 설정
     * 
     * <p>
     * Lablup FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return Request.Options 연결 타임아웃 10초, 읽기 타임아웃 60초
     */
    @Bean
    public Request.Options lablupRequestOptions() {
        return new Request.Options(
                Duration.ofSeconds(10),
                Duration.ofSeconds(60),
                true);
    }

    /**
     * Lablup API 에러 디코더 설정
     * 
     * <p>
     * Lablup FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return ErrorDecoder
     */
    @Bean
    public LablupErrorDecoder lablupErrorDecoder() {
        return new LablupErrorDecoder();
    }

    /**
     * Lablup API용 OkHttp Client 설정 (PATCH 메서드 지원 + SSL 우회)
     * 
     * <p>
     * OkHttp를 사용하여 PATCH 메서드를 지원하고, 개발계에서는 SSL 검증을 우회합니다.
     * </p>
     * 
     * <strong>⚠️ 보안 경고:</strong> SSL 우회 설정은 개발 환경에서만 사용해야 하며,
     * 운영 환경에서는 절대 사용하지 마십시오.
     * 
     * @return OkHttp 기반 Feign Client
     * @throws Exception SSL 설정 실패 시
     */
    @Bean
    public Client lablupFeignClientWithSSLBypass() throws Exception {
        // 현재 활성 프로필 확인
        String activeProfile = System.getProperty("spring.profiles.active", "");

        Builder okHttpBuilder = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if (isDevelopmentProfile(activeProfile)) {
            // 개발계: SSL 검증 완전 우회

            // 모든 인증서를 신뢰하는 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            // 모든 클라이언트 인증서를 신뢰
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            // 모든 서버 인증서를 신뢰 (자체 서명, 만료된 인증서 포함)
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // SSL 컨텍스트 생성
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 호스트명 검증을 완전히 우회
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            okHttpBuilder
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(allHostsValid);
        }

        // 모든 환경에서 HTTP/HTTPS 통신 허용
        okHttpBuilder.connectionSpecs(Arrays.asList(
                ConnectionSpec.MODERN_TLS, // HTTPS 지원
                ConnectionSpec.CLEARTEXT // HTTP 지원
        ));

        return new OkHttpClient(okHttpBuilder.build());
    }

    /**
     * 개발 프로필 여부 확인
     * 
     * @param activeProfile 활성 프로필
     * @return 개발 프로필 여부
     */
    private boolean isDevelopmentProfile(String activeProfile) {
        // return activeProfile != null &&
        // (activeProfile.contains("elocal") ||
        // activeProfile.contains("edev") ||
        // activeProfile.contains("local") ||
        // activeProfile.contains("dev"));
        return true; // SSL 우회 설정을 항상 활성화하도록 변경
    }
}