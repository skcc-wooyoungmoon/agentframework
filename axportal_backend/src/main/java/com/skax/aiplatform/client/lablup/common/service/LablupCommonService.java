package com.skax.aiplatform.client.lablup.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Lablup 공통 서비스
 *
 * <p>
 * Lablup API와의 통신에 필요한 공통 기능을 제공하는 서비스입니다.
 * 인증 헤더 생성, 공통 설정 관리 등의 기능을 담당합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>인증 헤더 생성</strong>: Lablup API 요청을 위한 Authorization 헤더 생성</li>
 * <li><strong>공통 헤더 관리</strong>: 모든 Lablup API 요청에 필요한 공통 헤더 제공</li>
 * <li><strong>설정 관리</strong>: Lablup API 관련 설정값 관리</li>
 * </ul>
 *
 * @author 김예리
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Service
public class LablupCommonService {

    /**
     * HMAC-SHA256 알고리즘 상수
     *
     * <p>
     * 보안: 암호화 알고리즘 이름을 상수로 관리하여 코드 전반에 걸친 일관성 확보
     * </p>
     */
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    @Value("${lablup.api.base-url}")
    private String baseUrl;

    @Value("${lablup.api.access-key}")
    private String accessKey;

    @Value("${lablup.api.secret-key}")
    private String secretKey;

    @Value("${lablup.api.version}")
    private String apiVersion;

    @Value("${lablup.api.content-type}")
    private String contentType;

    @Value("${lablup.api.hash-type}")
    private String hashType;

    @Value("${lablup.api.backendai-base-url}")
    private String backendAiBaseUrl;

    @Value("${lablup.api.backendai-access-key}")
    private String backendAiAccessKey;

    @Value("${lablup.api.backendai-secret-key}")
    private String backendAiSecretKey;

    @Value("${lablup.api.backendai-version}")
    private String backendAiApiVersion;

    /**
     * Lablup API 인증 헤더 생성
     *
     * <p>
     * 현재 설정된 Lablup API 정보를 사용하여 인증 헤더를 생성합니다.
     * </p>
     *
     * @param method HTTP 메서드 (GET, POST, PUT, DELETE 등)
     * @param relUrl 상대 URL 경로 (예: "/v1/artifact-registries/scan")
     * @return Authorization 헤더 값
     * @throws Exception 인증 헤더 생성 실패 시
     */
    public Map<String, String> generateAuthHeader(String method, String relUrl, ZonedDateTime date, String feignClientName) throws Exception {

        try {
            // 파라미터 검증
            if (method == null || method.trim().isEmpty()) {
                log.warn("generateAuthHeader: method is null or empty");
                throw new IllegalArgumentException("HTTP 메서드는 필수입니다");
            }
            if (relUrl == null || relUrl.trim().isEmpty()) {
                log.warn("generateAuthHeader: relUrl is null or empty");
                throw new IllegalArgumentException("상대 URL은 필수입니다");
            }
            if (date == null) {
                log.warn("generateAuthHeader: date is null");
                throw new IllegalArgumentException("날짜는 필수입니다");
            }
            if (feignClientName == null || feignClientName.trim().isEmpty()) {
                log.warn("generateAuthHeader: feignClientName is null or empty");
                throw new IllegalArgumentException("Feign 클라이언트 이름은 필수입니다");
            }

            log.debug("Lablup 인증 헤더 생성 - method: {}, relUrl: {}, client: {}", method, relUrl, feignClientName);

            if ("lablup-session-client".equals(feignClientName) || "lablup-resource-client".equals(feignClientName)) {
                log.debug("Backend.AI 인증 사용 - client: {}, version: {}", feignClientName, backendAiApiVersion);
                return generateSignature(method, backendAiApiVersion, backendAiBaseUrl, date, relUrl, backendAiAccessKey, backendAiSecretKey, contentType, hashType);
            } else {
                log.debug("Lablup 인증 사용 - version: {}", apiVersion);
                return generateSignature(method, apiVersion, baseUrl, date, relUrl, accessKey, secretKey, contentType, hashType);
            }

        } catch (IllegalArgumentException e) {
            log.error("generateAuthHeader: Invalid argument - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage());
            throw e;
        } catch (URISyntaxException e) {
            log.error("generateAuthHeader: Invalid URI syntax - relUrl: {}, error: {}", relUrl, e.getMessage(), e);
            throw new RuntimeException("잘못된 URL 형식입니다", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("generateAuthHeader: Cryptographic algorithm not found - error: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 알고리즘을 찾을 수 없습니다", e);
        } catch (InvalidKeyException e) {
            log.error("generateAuthHeader: Invalid cryptographic key - error: {}", e.getMessage(), e);
            throw new RuntimeException("잘못된 암호화 키입니다", e);
        } catch (UnsupportedEncodingException e) {
            log.error("generateAuthHeader: Unsupported encoding - error: {}", e.getMessage(), e);
            throw new RuntimeException("지원하지 않는 문자 인코딩입니다", e);
        } catch (NullPointerException e) {
            log.error("generateAuthHeader: Null pointer - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage(), e);
            throw new RuntimeException("필수 데이터가 누락되었습니다", e);
        } catch (Exception e) {
            log.error("generateAuthHeader: Unexpected error - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage(), e);
            throw new RuntimeException("인증 헤더 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Lablup API 공통 헤더 생성
     *
     * <p>
     * Lablup API 요청에 필요한 모든 공통 헤더를 생성합니다.
     * </p>
     *
     * @param method          HTTP 메서드
     * @param relUrl          상대 URL 경로
     * @param feignClientName Feign Client 이름 (호출자 식별용)
     * @return 공통 헤더 맵
     * @throws Exception 헤더 생성 실패 시
     */
    public Map<String, String> generateCommonHeaders(String method, String relUrl, String feignClientName) throws Exception {

        try {
            // 파라미터 검증
            if (method == null || method.trim().isEmpty()) {
                log.warn("generateCommonHeaders: method is null or empty");
                throw new IllegalArgumentException("HTTP 메서드는 필수입니다");
            }
            if (relUrl == null || relUrl.trim().isEmpty()) {
                log.warn("generateCommonHeaders: relUrl is null or empty");
                throw new IllegalArgumentException("상대 URL은 필수입니다");
            }
            if (feignClientName == null || feignClientName.trim().isEmpty()) {
                log.warn("generateCommonHeaders: feignClientName is null or empty");
                throw new IllegalArgumentException("Feign 클라이언트 이름은 필수입니다");
            }

            log.debug("Lablup 공통 헤더 생성 - method: {}, relUrl: {}, client: {}", method, relUrl, feignClientName);

            Map<String, String> headers = new HashMap<>();
            ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

            // Authorization 헤더 생성
            Map<String, String> authHeader = generateAuthHeader(method, relUrl, date, feignClientName);

            if (authHeader == null || !authHeader.containsKey("Authorization")) {
                log.error("generateCommonHeaders: authHeader is null or missing Authorization");
                throw new RuntimeException("인증 헤더 생성 결과가 올바르지 않습니다");
            }

            // Feign Client에 따른 설정값 결정
            if ("lablup-session-client".equals(feignClientName) || "lablup-resource-client".equals(feignClientName)) {
                headers.put("X-BackendAI-Version", backendAiApiVersion);
            } else {
                headers.put("X-BackendAI-Version", apiVersion);
            }

            headers.put("Content-Type", contentType);
            headers.put("Date", date.format(DateTimeFormatter.ISO_INSTANT));
            headers.put("Authorization", authHeader.get("Authorization"));

            log.debug("Lablup 공통 헤더 생성 완료 - 헤더 수: {}", headers.size());
            return headers;

        } catch (IllegalArgumentException e) {
            log.error("generateCommonHeaders: Invalid argument - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage());
            throw e;
        } catch (java.time.DateTimeException e) {
            log.error("generateCommonHeaders: DateTime error - error: {}", e.getMessage(), e);
            throw new RuntimeException("날짜/시간 처리 중 오류가 발생했습니다", e);
        } catch (NullPointerException e) {
            log.error("generateCommonHeaders: Null pointer - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage(), e);
            throw new RuntimeException("필수 데이터가 누락되었습니다", e);
        } catch (Exception e) {
            log.error("generateCommonHeaders: Unexpected error - method: {}, relUrl: {}, error: {}", method, relUrl, e.getMessage(), e);
            throw new RuntimeException("공통 헤더 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Lablup API 기본 URL 반환
     *
     * @return Lablup API 기본 URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Lablup API 버전 반환
     *
     * @return Lablup API 버전
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Lablup API 콘텐츠 타입 반환
     *
     * @return 콘텐츠 타입
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Lablup API 설정 정보 로깅
     *
     * <p>
     * 디버깅 목적으로 현재 설정된 Lablup API 정보를 로깅합니다.
     * 민감한 정보(secret-key)는 마스킹 처리됩니다.
     * </p>
     */
    public void logConfiguration() {
        if (log.isDebugEnabled()) {
            String maskedSecretKey = maskSecretKey(secretKey);
            log.debug("Lablup API 설정 정보:");
            log.debug("  - Base URL: {}", baseUrl);
            log.debug("  - Access Key: {}", accessKey);
            log.debug("  - Secret Key: {}", maskedSecretKey);
            log.debug("  - API Version: {}", apiVersion);
            log.debug("  - Content Type: {}", contentType);
            log.debug("  - Hash Type: {}", hashType);
        }
    }

    /**
     * 시크릿 키 마스킹 처리
     *
     * @param secretKey 원본 시크릿 키
     * @return 마스킹된 시크릿 키
     */
    private String maskSecretKey(String secretKey) {
        if (secretKey == null || secretKey.length() <= 8) {
            return "****";
        }
        return secretKey.substring(0, 4) + "****" + secretKey.substring(secretKey.length() - 4);
    }

    /**
     * Backend.AI API 서명 생성
     *
     * @param method      HTTP 메소드 (GET, POST 등)
     * @param version     API 버전
     * @param endpoint    API 엔드포인트 URL
     * @param date        현재 날짜/시간
     * @param relUrl      상대 URL 경로
     * @param accessKey   액세스 키
     * @param secretKey   시크릿 키
     * @param contentType 컨텐츠 타입
     * @param hashType    해시 타입
     * @return Authorization 헤더 값과 서명이 포함된 Map
     */
    public static Map<String, String> generateSignature(String method, String version, String endpoint, ZonedDateTime date, String relUrl, String accessKey, String secretKey, String contentType, String hashType) throws Exception {

        try {
            // 파라미터 검증
            if (method == null || method.trim().isEmpty()) {
                throw new IllegalArgumentException("HTTP 메서드는 필수입니다");
            }
            if (version == null || version.trim().isEmpty()) {
                throw new IllegalArgumentException("API 버전은 필수입니다");
            }
            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalArgumentException("엔드포인트 URL은 필수입니다");
            }
            if (date == null) {
                throw new IllegalArgumentException("날짜는 필수입니다");
            }
            if (relUrl == null || relUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("상대 URL은 필수입니다");
            }
            if (accessKey == null || accessKey.trim().isEmpty()) {
                throw new IllegalArgumentException("액세스 키는 필수입니다");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalArgumentException("시크릿 키는 필수입니다");
            }
            if (contentType == null || contentType.trim().isEmpty()) {
                throw new IllegalArgumentException("컨텐츠 타입은 필수입니다");
            }
            if (hashType == null || hashType.trim().isEmpty()) {
                throw new IllegalArgumentException("해시 타입은 필수입니다");
            }

            // 호스트명 추출
            String hostname;
            try {
                hostname = getHostname(endpoint);
            } catch (URISyntaxException e) {
                throw new RuntimeException("잘못된 엔드포인트 URL 형식입니다: " + endpoint, e);
            }

            // 빈 바디의 해시 생성
            String bodyHash;
            try {
                bodyHash = sha256Hex("");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다", e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("지원하지 않는 문자 인코딩입니다", e);
            }

            // 서명 문자열 생성
            String signStr = String.format("%s\n%s\n%s\nhost:%s\ncontent-type:%s\nx-backendai-version:%s\n%s", method.toUpperCase(), relUrl, formatDateISO(date), hostname, contentType.toLowerCase(), version, bodyHash);

            // HMAC 키 생성 (단계별 파생)
            byte[] baseKey;
            try {
                baseKey = secretKey.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 인코딩을 지원하지 않습니다", e);
            }

            byte[] dateKey;
            byte[] signingKey;
            byte[] signatureBytes;
            try {
                dateKey = hmacSha256(baseKey, formatDateYYYYMMDD(date));
                signingKey = hmacSha256(dateKey, hostname);
                signatureBytes = hmacSha256(signingKey, signStr);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("HMAC-SHA256 알고리즘을 찾을 수 없습니다", e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException("잘못된 HMAC 키입니다", e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("지원하지 않는 문자 인코딩입니다", e);
            }

            String signature = bytesToHex(signatureBytes);

            // Authorization 헤더 생성
            String auth = String.format("BackendAI signMethod=HMAC-%s, credential=%s:%s", hashType.toUpperCase(), accessKey, signature);

            Map<String, String> result = new HashMap<>();
            result.put("Authorization", auth);
            result.put("signature", signature);

            return result;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new RuntimeException("필수 데이터가 누락되었습니다", e);
        } catch (Exception e) {
            throw new RuntimeException("서명 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * URI에서 호스트명 추출
     */
    private static String getHostname(String endpoint) throws URISyntaxException {
        URI uri = new URI(endpoint);
        String hostname = uri.getHost();
        if (hostname == null) {
            throw new URISyntaxException(endpoint, "호스트명을 추출할 수 없습니다. 유효한 절대 URI가 필요합니다.");
        }
        int port = uri.getPort();
        if (port != -1 && port != 80 && port != 443) {
            hostname += ":" + port;
        }
        return hostname;
    }

    /**
     * SHA-256 해시 생성 (16진수 문자열 반환)
     *
     * <p>
     * <strong>보안 참고:</strong> 이 메서드는 BackendAI API 서명 생성을 위한 메시지 무결성 검증용 해시입니다.
     * 비밀번호 해싱이 아니므로 솔트(salt)가 필요하지 않습니다.
     * API 요청 바디의 체크섬을 생성하여 전송 중 데이터 변조를 방지하는 용도로 사용됩니다.
     * </p>
     *
     * <p>
     * 이 해시는 HMAC-SHA256 서명 프로세스의 일부로, 최종 서명은 시크릿 키를 포함한
     * HMAC 알고리즘으로 생성되므로 충분한 보안성을 제공합니다.
     * </p>
     *
     * @param data 해시할 데이터 (일반적으로 요청 바디 또는 빈 문자열)
     * @return SHA-256 해시의 16진수 문자열 표현
     * @throws NoSuchAlgorithmException     SHA-256 알고리즘을 찾을 수 없는 경우
     * @throws UnsupportedEncodingException UTF-8 인코딩을 지원하지 않는 경우
     * @SuppressWarnings("lgtm[java/weak-cryptographic-algorithm]") // API 서명용 메시지 다이제스트, 비밀번호 해싱 아님
     */
    @SuppressWarnings("java:S4790") // SonarQube: 이 해시는 API 서명용이며 비밀번호 저장용이 아님
    private static String sha256Hex(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // BackendAI API 프로토콜 요구사항:
        // 요청 바디의 SHA-256 체크섬을 서명 문자열에 포함해야 함
        // 이는 전송 중 데이터 무결성을 보장하기 위한 것이며,
        // 최종 인증은 HMAC-SHA256으로 시크릿 키와 함께 수행됨
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes("UTF-8"));
        return bytesToHex(hash);
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 날짜를 YYYYMMDD 형식으로 포맷
     */
    private static String formatDateYYYYMMDD(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 날짜를 ISO 형식으로 포맷
     */
    private static String formatDateISO(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * HMAC-SHA256 해시 생성
     *
     * <p>
     * <strong>보안 강화:</strong> 알고리즘 이름을 상수로 관리하여 하드코딩 제거
     * </p>
     *
     * @param key  HMAC 키 (바이트 배열)
     * @param data 해시할 데이터 (문자열)
     * @return HMAC-SHA256 해시 결과 (바이트 배열)
     * @throws NoSuchAlgorithmException     HMAC-SHA256 알고리즘을 찾을 수 없는 경우
     * @throws InvalidKeyException          잘못된 키인 경우
     * @throws UnsupportedEncodingException UTF-8 인코딩을 지원하지 않는 경우
     */
    private static byte[] hmacSha256(byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        // 보안: 암호화 알고리즘 이름을 상수로 사용하여 하드코딩 제거
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes("UTF-8"));
    }
}
