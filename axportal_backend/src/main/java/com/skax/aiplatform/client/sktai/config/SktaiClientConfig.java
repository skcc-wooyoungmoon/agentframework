package com.skax.aiplatform.client.sktai.config;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;

import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.service.auth.TokenCacheService;

import feign.Client;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient.Builder;

/**
 * SKTAI Feign Client 전용 설정 클래스
 *
 * <p>
 * SKTAI API 호출을 위한 Feign Client 설정을 제공합니다.
 * 이 설정은 특정 Feign Client에서만 사용되어 Bean 충돌을 방지합니다.
 * </p>
 *
 * <h3>주요 설정:</h3>
 * <ul>
 * <li><strong>SSL/TLS 보안</strong>: HTTPS 통신을 위한 SSL 설정</li>
 * <li><strong>요청 인터셉터</strong>: 공통 헤더 및 인증 정보 자동 추가</li>
 * <li><strong>재시도 정책</strong>: 네트워크 오류 시 자동 재시도</li>
 * <li><strong>에러 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 * </ul>
 *
 * <p>
 * <strong>주의:</strong> @Configuration을 제거하여 전역 Bean 등록을 방지합니다.
 * 이 설정은 SKTAI FeignClient에서만 사용되어 다른 Client와의 간섭을 방지합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 3.3
 * @updated 2025-10-14 - @Configuration 제거로 Bean 간섭 방지
 * @since 2025-08-15
 */
@Slf4j
@RequiredArgsConstructor
public class SktaiClientConfig {

    private final TokenCacheService tokenCacheService;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;

    // 중복 로그 방지를 위한 플래그들
    private static volatile boolean sslConfigLogged = false;
    private static volatile boolean requestInterceptorLogged = false;
    private static volatile boolean timeoutLogged = false;
    private static volatile boolean encoderLogged = false;

    // ================================
    // SSL/TLS HTTPS 설정
    // ================================

    /**
     * SKTAI API용 OkHttp Client 설정 (PATCH 메서드 지원 + SSL 설정)
     *
     * <p>
     * OkHttp를 사용하여 PATCH 메서드를 지원하고, 환경에 따라 SSL 설정을 적용합니다.
     * </p>
     *
     * <h3>지원 기능:</h3>
     * <ul>
     * <li><strong>PATCH 메서드</strong>: 모든 HTTP 메서드 지원</li>
     * <li><strong>SSL 검증 우회</strong>: 개발환경에서 자체서명 인증서 지원</li>
     * <li><strong>HTTPS/HTTP 통신</strong>: 다양한 엔드포인트 지원</li>
     * <li><strong>연결 풀링</strong>: 성능 최적화</li>
     * </ul>
     *
     * @return OkHttp 기반 Feign Client
     * @throws Exception SSL 설정 실패 시
     */
    @Bean
    public Client sktaiFeignClient() throws Exception {
        // 현재 프로파일에 따라 적절한 Client 반환
        String[] activeProfiles = getActiveProfiles();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(100);
        dispatcher.setMaxRequestsPerHost(25);

        Builder okHttpBuilder = new okhttp3.OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        // if (isDevProfile(activeProfiles)) {
        // // 중복 로그 방지 - 최초 1회만 출력
        // if (!sslConfigLogged) {
        // log.warn("SKTAI SSL 설정: 개발환경용 - 모든 SSL 인증서 검증 우회 활성화");
        // log.warn("⚠️ 보안 경고: 개발환경에서만 사용하세요!");
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
        // log.info("SKTAI SSL 설정: 운영환경용 인증서 완전 검증 모드 활성화");
        // }

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
        if (profiles == null || profiles.length == 0) {
            return true; // 기본값은 개발환경으로 설정
        }

        for (String profile : profiles) {
            if ("elocal".equals(profile) || "local".equals(profile) ||
                    "edev".equals(profile) || "dev".equals(profile)) {
                return true;
            }
        }
        return false;
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

    // ================================
    // 기본 Feign 설정
    // ================================

    /**
     * SKTAI API 요청 인터셉터
     *
     * <p>
     * SKTAI 전용 RequestInterceptor를 Bean으로 등록합니다.
     *
     * @return RequestInterceptor SKTAI 전용 인터셉터
     * @Configuration 없이 FeignClient configuration에서만 사용되므로 다른 Client에 영향 없습니다.
     *                </p>
     */
    @Bean
    public RequestInterceptor sktaiRequestInterceptor() {
        // 중복 로그 방지 - 최초 1회만 출력
        if (!requestInterceptorLogged) {
            log.info("🔧 SktaiRequestInterceptor Bean 등록 중... (FeignClient Config 전용)");
            requestInterceptorLogged = true;
        }
        return new SktaiRequestInterceptor(
                tokenCacheService,
                sktaiBaseUrl,
                gpoUsersMasRepository,
                gpoPrjuserroleRepository,
                sktaiClientId);
    }

    @Value("${sktai.api.base-url}")
    private String sktaiBaseUrl;

    @Value("${sktai.api.client-id}")
    private String sktaiClientId;

    @Value("${sktai.api.timeout.connect:60000}")
    private long connectTimeout;

    @Value("${sktai.api.timeout.read:900000}")
    private long readTimeout;

    /**
     * 요청 옵션 설정 (타임아웃 등)
     *
     * <p>
     * SKTAI API 호출 시 연결 타임아웃과 읽기 타임아웃을 설정합니다.
     * YAML 설정 파일에서 값을 읽어옵니다.
     * </p>
     */
    @Bean
    public Request.Options sktaiRequestOptions() {
        // 중복 로그 방지 - 최초 1회만 출력
        if (!timeoutLogged) {
            log.info("SKTAI 요청 타임아웃 설정 - 연결: {}ms, 읽기: {}ms", connectTimeout, readTimeout);
            timeoutLogged = true;
        }

        return new Request.Options(
                connectTimeout, // YAML에서 설정한 연결 타임아웃
                java.util.concurrent.TimeUnit.MILLISECONDS, // 연결 타임아웃 단위
                readTimeout, // YAML에서 설정한 읽기 타임아웃
                java.util.concurrent.TimeUnit.MILLISECONDS, // 읽기 타임아웃 단위
                true // 리다이렉트 따르기
        );
    }

    /**
     * 재시도 정책 설정
     */
    @Bean
    public Retryer sktaiRetryer() {
        return new Retryer.Default(
                1000, // 초기 지연 시간 (1초)
                3000, // 최대 지연 시간 (3초)
                3 // 최대 재시도 횟수
        );
    }

    /**
     * 에러 디코더 설정
     */
    @Bean
    public ErrorDecoder sktaiErrorDecoder(TokenCacheService tokenCacheService) {
        return new SktaiErrorDecoder(tokenCacheService);
    }

    /**
     * Feign 로깅 레벨 설정
     *
     * <p>
     * Logger.Level.BASIC: 메타데이터만 로깅 (메서드, URL, 상태 코드, 응답 시간)
     * </p>
     * <p>
     * 민감한 파일 데이터 및 요청/응답 바디는 로깅하지 않아 보안 강화
     * </p>
     */
    @Bean
    public feign.Logger.Level sktaiFeignLoggerLevel() {
        return feign.Logger.Level.BASIC;
    }

    /**
     * SKTAI 전용 MultiPart Encoder Bean
     *
     * <p>
     * SpringFormEncoder와 SpringEncoder를 조합하여 다음을 지원합니다:
     * </p>
     * <ul>
     * <li><strong>multipart/form-data</strong>: 파일 업로드용</li>
     * <li><strong>application/json</strong>: JSON 요청용</li>
     * <li><strong>application/x-www-form-urlencoded</strong>: 폼 데이터용</li>
     * </ul>
     *
     * <p>
     * <strong>주의:</strong> @Configuration 없이 FeignClient configuration에서만 사용되므로 다른
     * Client에 영향 없습니다.
     * </p>
     *
     * @param messageConverters Spring의 HttpMessageConverters
     * @return 통합 Encoder
     */
    @Bean
    public Encoder sktaiFeignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        // 중복 로그 방지 - 최초 1회만 출력
        if (!encoderLogged) {
            log.info("통합 Encoder 설정 - MultiPart와 JSON 요청 모두 지원 활성화");
            encoderLogged = true;
        }

        try {
            // SpringEncoder를 기본으로 하고 SpringFormEncoder로 래핑
            Encoder springEncoder = new org.springframework.cloud.openfeign.support.SpringEncoder(messageConverters);
            feign.form.spring.SpringFormEncoder formEncoder = new feign.form.spring.SpringFormEncoder(springEncoder);

            // 성공 로그도 최초 1회만 출력 (같은 플래그 사용)
            if (encoderLogged) {
                log.info("통합 Encoder 생성 성공 - MultiPart + JSON 지원");
                encoderLogged = false; // 다음엔 출력하지 않도록 플래그 재설정
            }
            return formEncoder;

        } catch (IllegalStateException e) {
            log.error("통합 Encoder 생성 실패 (IllegalStateException) - 초기화 상태 오류: {}", e.getMessage(), e);
            throw new RuntimeException("통합 Encoder 초기화 실패 (상태 오류)", e);
        } catch (NullPointerException e) {
            log.error("통합 Encoder 생성 실패 (NullPointerException) - 필수 의존성 누락: {}", e.getMessage(), e);
            throw new RuntimeException("통합 Encoder 초기화 실패 (의존성 누락)", e);
        } catch (Exception e) {
            log.error("통합 Encoder 생성 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new RuntimeException("통합 Encoder 초기화 실패", e);
        }
    }
}
