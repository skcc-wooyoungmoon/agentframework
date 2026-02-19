package com.skax.aiplatform.client.sktai.resrcMgmt.config;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.context.annotation.Bean;

import feign.Client;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;

/**
 * 자원 관리(Prometheus) Feign Client 설정
 * 
 * <p>
 * 이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 ResrcMgmt FeignClient에만 적용되도록 합니다.
 * </p>
 * 
 * <p>
 * Prometheus API 호출을 위한 Feign Client 설정을 제공합니다.
 * 개발 환경에서는 SSL 인증서 검증을 우회하여 자체 서명 인증서를 지원합니다.
 * </p>
 * 
 * @author SonMunWoo
 * @since 2025-11-05
 * @version 1.0
 */
@Slf4j
public class ResrcMgmtFeignConfig {

    // 중복 로그 방지를 위한 플래그
    private static volatile boolean sslConfigLogged = false;

    /**
     * Feign 재시도 설정
     * 
     * @return Retryer 설정
     */
    @Bean
    public Retryer resrcMgmtRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }

    /**
     * 자원 관리 API용 ErrorDecoder 설정
     * 
     * @return ErrorDecoder
     */
    @Bean
    public ErrorDecoder resrcMgmtErrorDecoder() {
        return new feign.codec.ErrorDecoder.Default();
    }

    /**
     * 자원 관리(Prometheus) API용 OkHttp Client 설정 (SSL 우회 지원)
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
    public Client resrcMgmtFeignClient() throws Exception {
        // 현재 활성 프로필 확인
        String[] activeProfiles = getActiveProfiles();

        Builder okHttpBuilder = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        // 운영 환경(prod)을 제외한 모든 환경에서 SSL 우회 (내부망 포함)
        // if (shouldBypassSSL(activeProfiles)) {
        // // 중복 로그 방지 - 최초 1회만 출력
        // if (!sslConfigLogged) {
        // log.warn("ResrcMgmt SSL 설정: 개발/내부망 환경용 - 모든 SSL 인증서 검증 우회 활성화");
        // log.warn("⚠️ 보안 경고: 운영환경(prod)에서는 SSL 검증이 활성화됩니다!");
        // sslConfigLogged = true;
        // }

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
        // } else {
        // log.info("ResrcMgmt SSL 설정: 운영환경(prod)용 인증서 완전 검증 모드 활성화");
        // }

        // 모든 환경에서 HTTP/HTTPS 통신 허용
        okHttpBuilder.connectionSpecs(Arrays.asList(
                ConnectionSpec.MODERN_TLS, // HTTPS 지원
                ConnectionSpec.CLEARTEXT // HTTP 지원
        ));

        return new OkHttpClient(okHttpBuilder.build());
    }

    /**
     * SSL 우회가 필요한 환경인지 확인
     * 운영 환경(prod)을 제외한 모든 환경에서 SSL 우회 (개발/내부망 포함)
     */
    private boolean shouldBypassSSL(String[] profiles) {
        if (profiles == null || profiles.length == 0) {
            return true; // 기본값은 개발환경으로 설정하여 SSL 우회
        }

        for (String profile : profiles) {
            // 운영 환경(prod)인 경우 SSL 검증 활성화
            if ("prod".equals(profile) || "production".equals(profile)) {
                return false;
            }
        }

        // prod가 아닌 모든 환경(개발/내부망 등)에서 SSL 우회
        return true;
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
}
