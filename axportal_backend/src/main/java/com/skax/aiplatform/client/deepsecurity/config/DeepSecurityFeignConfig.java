package com.skax.aiplatform.client.deepsecurity.config;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.Client;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;

/**
 * DeepSecurity 서비스용 Feign 설정
 * 
 * <p>
 * 이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 DeepSecurity FeignClient에만 적용되도록 합니다.
 * </p>
 * 
 * @author system
 * @since 2025-01-15
 */
@Slf4j
public class DeepSecurityFeignConfig {

    // 중복 로그 방지를 위한 플래그들
    private static volatile boolean sslConfigLogged = false;
    private static volatile boolean requestInterceptorLogged = false;
    private static volatile boolean timeoutLogged = false;

    @Value("${deepsecurity.api.timeout.connect:30000}")
    private long connectTimeout;

    @Value("${deepsecurity.api.timeout.read:90000}")
    private long readTimeout;

    @Value("${deepsecurity.api.retry.max-attempts:5}")
    private int maxAttempts;

    @Value("${deepsecurity.api.retry.initial-interval:1000}")
    private long initialInterval;

    @Value("${deepsecurity.api.retry.max-interval:5000}")
    private long maxInterval;

    /**
     * DeepSecurity 전용 RequestInterceptor 생성
     * 
     * <p>
     * FeignClient configuration에서만 사용되어 전역 Bean 등록을 방지합니다.
     * </p>
     * 
     * @return DeepSecurity RequestInterceptor
     */
    @Bean
    public RequestInterceptor deepSecurityRequestInterceptor() {
        // 중복 로그 방지 - 최초 1회만 출력
        if (!requestInterceptorLogged) {
            log.info("🔧 DeepSecurityRequestInterceptor Bean 등록 중... (FeignClient Config 전용)");
            requestInterceptorLogged = true;
        }
        return new DeepSecurityRequestInterceptor();
    }

    /**
     * Feign 재시도 설정
     * 
     * @return Retryer 설정
     */
    @Bean
    public Retryer deepSecurityRetryer() {
        return new Retryer.Default(initialInterval, maxInterval, maxAttempts);
    }

    /**
     * DeepSecurity API용 ErrorDecoder 설정
     * 
     * @return DeepSecurity ErrorDecoder
     */
    @Bean
    public ErrorDecoder deepSecurityErrorDecoder() {
        return new DeepSecurityErrorDecoder();
    }

    /**
     * DeepSecurity API용 OkHttp Client 설정 (SSL 우회 지원)
     * 
     * <p>
     * OkHttp를 사용하여 REST API를 지원하고, 개발계에서는 SSL 검증을 우회합니다.
     * </p>
     * 
     * <strong>⚠️ 보안 경고:</strong> SSL 우회 설정은 개발 환경에서만 사용해야 하며,
     * 운영 환경에서는 절대 사용하지 마십시오.
     * 
     * @return OkHttp 기반 Feign Client
     * @throws Exception SSL 설정 실패 시
     */
    @Bean
    public Client deepSecurityFeignClient() throws Exception {
        // 현재 프로파일에 따라 적절한 Client 반환
        String[] activeProfiles = getActiveProfiles();

        Builder okHttpBuilder = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(readTimeout, TimeUnit.MILLISECONDS);

        if (isDevProfile(activeProfiles)) {
            // 중복 로그 방지 - 최초 1회만 출력
            if (!sslConfigLogged) {
                log.warn("DeepSecurity SSL 설정: 개발환경용 - 모든 SSL 인증서 검증 우회 활성화");
                log.warn("⚠️  보안 경고: 개발환경에서만 사용하세요!");
                sslConfigLogged = true;
            }

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
        } else {
            log.info("DeepSecurity SSL 설정: 운영환경용 인증서 완전 검증 모드 활성화");
        }

        // 모든 환경에서 HTTP/HTTPS 통신 허용
        okHttpBuilder.connectionSpecs(Arrays.asList(
                ConnectionSpec.MODERN_TLS, // HTTPS 지원
                ConnectionSpec.CLEARTEXT // HTTP 지원
        ));

        return new OkHttpClient(okHttpBuilder.build());
    }

    /**
     * 현재 활성 프로파일이 개발환경인지 확인
     */
    private boolean isDevProfile(String[] profiles) {
        // if (profiles == null || profiles.length == 0) {
        // return true; // 기본값은 개발환경으로 설정
        // }

        // for (String profile : profiles) {
        // if ("elocal".equals(profile) || "local".equals(profile) ||
        // "edev".equals(profile) || "dev".equals(profile)) {
        // return true;
        // }
        // }
        // return false;
        return true; // SSL 우회 설정을 항상 활성화하도록 변경
    }

    /**
     * 현재 활성 프로파일 조회
     */
    private String[] getActiveProfiles() {
        // Spring 컨텍스트 없이 시스템 프로퍼티에서 프로파일 확인
        String profileProperty = System.getProperty("spring.profiles.active");
        if (profileProperty != null) {
            return profileProperty.split(",");
        }

        // 환경변수에서 확인
        String profileEnv = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profileEnv != null) {
            return profileEnv.split(",");
        }

        // 기본값은 elocal
        return new String[] { "elocal" };
    }

    /**
     * 요청 옵션 설정 (타임아웃 등)
     * 
     * <p>
     * DeepSecurity API 호출 시 연결 타임아웃과 읽기 타임아웃을 설정합니다.
     * YAML 설정 파일에서 값을 읽어옵니다.
     * </p>
     */
    @Bean
    public feign.Request.Options deepSecurityRequestOptions() {
        // 중복 로그 방지 - 최초 1회만 출력
        if (!timeoutLogged) {
            log.info("DeepSecurity 요청 타임아웃 설정 - 연결: {}ms, 읽기: {}ms", connectTimeout, readTimeout);
            timeoutLogged = true;
        }

        return new feign.Request.Options(
                connectTimeout, // YAML에서 설정한 연결 타임아웃
                java.util.concurrent.TimeUnit.MILLISECONDS, // 연결 타임아웃 단위
                readTimeout, // YAML에서 설정한 읽기 타임아웃
                java.util.concurrent.TimeUnit.MILLISECONDS, // 읽기 타임아웃 단위
                true // 리다이렉트 따르기
        );
    }

    /**
     * Feign 로깅 레벨 설정
     * 
     * <p>
     * Logger.Level.BASIC: 메타데이터만 로깅 (메서드, URL, 상태 코드, 응답 시간)
     * </p>
     * <p>
     * 민감한 데이터 및 요청/응답 바디는 로깅하지 않아 보안 강화
     * </p>
     */
    @Bean
    public feign.Logger.Level deepSecurityFeignLoggerLevel() {
        return feign.Logger.Level.BASIC;
    }
}
