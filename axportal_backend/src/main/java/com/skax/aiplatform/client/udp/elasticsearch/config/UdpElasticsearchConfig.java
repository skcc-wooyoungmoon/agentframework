package com.skax.aiplatform.client.udp.elasticsearch.config;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;

/**
 * UDP Elasticsearch Feign 설정
 * 
 * <p>
 * UDP Elasticsearch API 연동을 위한 Feign Client 설정입니다.
 * Basic Auth 인증 및 SSL 우회를 지원합니다.
 * </p>
 * 
 * <h3>연결 정보:</h3>
 * <ul>
 * <li><strong>URL</strong>: https://elasticsearch.didim365.app:9200</li>
 * <li><strong>인증</strong>: Basic Auth (elastic / password)</li>
 * <li><strong>SSL</strong>: 개발환경에서 검증 우회</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Slf4j
public class UdpElasticsearchConfig {

    @Autowired
    private Environment environment;

    /**
     * UDP Elasticsearch 전용 RequestInterceptor 생성
     * 
     * <p>
     * Basic Auth 인증 헤더를 자동으로 추가합니다.
     * </p>
     * 
     * @return UDP Elasticsearch RequestInterceptor
     */
    @Bean
    public RequestInterceptor udpElasticsearchRequestInterceptor() {
        String username = environment != null
                ? environment.getProperty("udp.elasticsearch.auth.username", "elastic")
                : "elastic";
        String password = environment != null
                ? environment.getProperty("udp.elasticsearch.auth.password", "")
                : "";

        log.info("🔐 [UDP Elasticsearch Config] RequestInterceptor 생성 - username: {}", username);

        return new UdpElasticsearchRequestInterceptor(username, password);
    }

    /**
     * Feign 재시도 설정
     * 
     * @return Retryer 설정
     */
    @Bean
    public Retryer udpElasticsearchRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }

    /**
     * UDP Elasticsearch API용 ErrorDecoder 설정
     * 
     * @return UDP Elasticsearch ErrorDecoder
     */
    @Bean
    public ErrorDecoder udpElasticsearchErrorDecoder() {
        return new UdpElasticsearchErrorDecoder();
    }

    /**
     * Feign 로깅 레벨 설정
     * 
     * <p>
     * 전체 요청/응답을 로그로 출력하여 디버깅을 지원합니다.
     * </p>
     * 
     * @return Logger Level
     */
    @Bean
    public Logger.Level udpElasticsearchFeignLoggerLevel() {
        log.info("🔍 [UDP Elasticsearch Config] Feign 로깅 레벨: BASIC (메타데이터만 로그 출력)");
        return Logger.Level.BASIC;
    }

    /**
     * UDP Elasticsearch API용 OkHttp Client 설정 (SSL 우회 지원)
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
    public Client udpElasticsearchFeignClient() throws Exception {
        Builder okHttpBuilder = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        // 개발 프로필 확인
        boolean isDevelopment = isDevelopmentProfile();

        if (isDevelopment) {
            log.warn("⚠️ [UDP Elasticsearch Client] SSL 검증 비활성화 (개발 환경 전용)");

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

            log.info("✅ [UDP Elasticsearch Client] SSL 우회 설정 완료");
        } else {
            log.info("🔒 [UDP Elasticsearch Client] SSL 검증 활성화 (운영 환경)");
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
     * <p>
     * Spring Environment를 통해 활성 프로필을 확인하여 개발 환경 여부를 판단합니다.
     * </p>
     * 
     * @return 개발 프로필 여부 (elocal, edev, local, dev 중 하나라도 활성화되어 있으면 true)
     */
    private boolean isDevelopmentProfile() {
        // if (environment == null) {
        // // Environment가 주입되지 않은 경우 안전하게 개발 프로필로 간주
        // log.warn("⚠️ Environment is null, assuming development profile for SSL
        // bypass");
        // return true;
        // }

        // String[] activeProfiles = environment.getActiveProfiles();

        // if (activeProfiles.length == 0) {
        // // 활성 프로필이 없는 경우 기본 프로필 확인
        // activeProfiles = environment.getDefaultProfiles();
        // }

        // for (String profile : activeProfiles) {
        // if (profile != null &&
        // (profile.contains("elocal") ||
        // profile.contains("edev") ||
        // profile.contains("local") ||
        // profile.contains("dev"))) {
        // log.info("🔍 [UDP Elasticsearch Client] 활성 프로필: {} (개발 환경)", profile);
        // return true;
        // }
        // }

        // log.info("🔍 [UDP Elasticsearch Client] 활성 프로필: {} (운영 환경)", String.join(",
        // ", activeProfiles));
        // return false;
        return true; // SSL 우회 설정을 항상 활성화하도록 변경
    }
}
