package com.skax.aiplatform.client.udp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.RequestInterceptor;
import feign.Response;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient.Builder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class UdpFeignDocumentConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        return new Retryer.Default(1000, 3000, 3);
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

    /**
     * UDP API용 OkHttp Client 설정 (PATCH 메서드 지원 + SSL 우회)
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
    public Client udpFeignClient() throws Exception {
        // 현재 활성 프로필 확인
        String activeProfile = System.getProperty("spring.profiles.active", "");

        Builder okHttpBuilder = new Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        // elocal, local 프로필에서만 SSL 우회 적용
        // if (activeProfile.contains("elocal") || activeProfile.contains("local")) {
        // SSL 검증 우회 설정
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        // 모든 클라이언트 인증서를 신뢰
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        // 모든 서버 인증서를 신뢰
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        okHttpBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true);
        // }

        // HTTPS 및 HTTP 연결 허용
        okHttpBuilder.connectionSpecs(Arrays.asList(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.CLEARTEXT));

        return new OkHttpClient(okHttpBuilder.build());
    }

    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> converters) {
        Decoder springDecoder = new SpringDecoder(converters);
        return ((response, type) -> {
            if (response.body() == null) return springDecoder.decode(response, type);

            try {
                byte[] bodyData = response.body().asInputStream().readAllBytes();
                Map<String, Object> data = objectMapper.readValue(new String(bodyData, StandardCharsets.UTF_8), Map.class);

                if (!data.containsKey("result_lists")) return springDecoder.decode(response, type);

                List<Map<String, Object>> result_lists = (List<Map<String, Object>>) data.get("result_lists");
                result_lists.forEach(map -> map.put("origin_metadata", new HashMap<>(map)));
                Response newResp = response.toBuilder().body(objectMapper.writeValueAsString(data), StandardCharsets.UTF_8).build();

                return springDecoder.decode(newResp, type);
            } catch (Exception e) {
                return springDecoder.decode(response, type);
            }
        });
    }

}