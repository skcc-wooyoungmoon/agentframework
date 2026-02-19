package com.skax.aiplatform.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.auth.TokenCacheData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenCacheService {

    private Cache<String, TokenCacheData> tokenCache;

    @Value("${sktai.api.base-url}")
    private String sktaiBaseUrl;

    // 스레드 안전한 ObjectMapper 인스턴스 (재사용)
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 스레드 안전한 RestTemplate 인스턴스 (재사용)
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TOKEN_KEY_PREFIX = "token:";

    @PostConstruct
    public void init() {
        // 기본 캐시 설정
        // PostConstruct 생성자에서 최초 1회 캐시 객체를 초기화 하는 코드
        // 시큐어코딩 관점에서 조치 대상이 아님으로 판단
        tokenCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    // 사용자명으로 토큰 조회 (캐시 전용)
    public TokenCacheData getTokenByUsername(String username) {
        String key = TOKEN_KEY_PREFIX + username;

        // 캐시에서 조회
        TokenCacheData cachedToken = tokenCache.getIfPresent(key);

        if (cachedToken != null && cachedToken.isValid()) {
            return cachedToken;
        }

        // 캐시에 없거나 유효하지 않은 경우 null 반환
        return null;
    }

    // 토큰 캐시에 저장
    public void cacheToken(TokenCacheData token) {
        String key = TOKEN_KEY_PREFIX + token.getMemberId();

        // 토큰이 유효한 경우에만 캐시
        if (token.isValid()) {
            tokenCache.put(key, token);

            // 만료 시간이 있는 경우 별도 캐시 설정 (선택적)
            if (token.getExpAt() != null) {
                Duration timeToLive = Duration.between(LocalDateTime.now(), token.getExpAt());
                if (!timeToLive.isNegative() && !timeToLive.isZero()) {
                    // 개별 항목별 만료 시간은 Caffeine에서 직접 지원하지 않음
                    // 대신 주기적으로 정리하거나 캐시 전체 만료 정책 사용
                }
            }
        }
    }

    // 토큰 캐시에서 삭제
    public void removeTokenFromCache(String username) {
        String key = TOKEN_KEY_PREFIX + username;
        tokenCache.invalidate(key);
    }

    /**
     * 사용자명의 리프레시 토큰을 사용하여 SKTAI 액세스 토큰을 갱신하고 캐시 갱신
     *
     * @param username 현재 인증된 사용자명
     * @return 갱신된 TokenCacheData
     */
    public TokenCacheData refreshSktaiAccessToken(String username) {
        // 캐시에서 기존 토큰 조회 (만료 여부와 무관하게)
        String key = TOKEN_KEY_PREFIX + username;
        TokenCacheData existing = tokenCache.getIfPresent(key);

        if (existing == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자 토큰 정보가 없습니다");
        }

        // 리프레시 토큰 만료 체크
        if (existing.isRefreshExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "리프레시 토큰이 만료되었습니다");
        }

        // SKTAI 토큰 갱신 호출
        AccessTokenResponseWithProject refreshed = refreshTokenDirectly(existing.getRefreshToken());

        // 토큰 데이터 업데이트
        existing.setAccessToken(refreshed.getAccessToken());
        existing.setRefreshToken(refreshed.getRefreshToken());
        existing.setTokenType(refreshed.getTokenType());
        existing.setTokenExpTimes(refreshed.getExpiresIn());
        existing.setRefreshTokenExpTimes(refreshed.getRefreshExpiresIn());
        existing.setExpAt(extractExpAtFromAccessToken(refreshed.getAccessToken(), refreshed.getExpiresIn()));
        if (refreshed.getRefreshExpiresIn() != null) {
            existing.setRefreshTokenExpAt(LocalDateTime.now().plusSeconds(refreshed.getRefreshExpiresIn()));
        }
        existing.setTokenExpYn("N");

        // 캐시 갱신
        removeTokenFromCache(username);
        cacheToken(existing);

        return existing;
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 직접 HTTP 요청으로 갱신
     * (Feign Client 의존성 순환 문제를 피하기 위해 RestTemplate 사용)
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰 응답
     */
    private AccessTokenResponseWithProject refreshTokenDirectly(String refreshToken) {
        try {
            String url = sktaiBaseUrl + "/api/v1/auth/token/refresh?refresh_token=" + refreshToken;

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "AXPORTAL-Backend/1.0");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<AccessTokenResponseWithProject> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AccessTokenResponseWithProject.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류 (인증 실패, 잘못된 요청 등)
            log.error("토큰 갱신 HTTP 요청 실패 (클라이언트 오류, 4xx): status={}, error={}", 
                    e.getStatusCode(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "토큰 갱신 실패: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            log.error("토큰 갱신 HTTP 요청 실패 (서버 오류, 5xx): status={}, error={}", 
                    e.getStatusCode(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "토큰 갱신 실패: 서버 오류가 발생했습니다 - " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            // 네트워크 연결 오류 (타임아웃, 연결 불가 등)
            log.error("토큰 갱신 HTTP 요청 실패 (네트워크 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE, 
                    "토큰 갱신 실패: 네트워크 연결 오류가 발생했습니다");
        } catch (HttpMessageConversionException e) {
            // HTTP 메시지 변환 오류 (JSON 파싱 실패 등)
            log.error("토큰 갱신 HTTP 요청 실패 (메시지 변환 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "토큰 갱신 실패: 응답 데이터 변환 오류가 발생했습니다");
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("토큰 갱신 HTTP 요청 실패 (잘못된 인자): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "토큰 갱신 실패: 잘못된 인자로 인해 오류가 발생했습니다");
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 예외
            log.error("토큰 갱신 HTTP 요청 실패 (RestClientException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "토큰 갱신 실패: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("토큰 갱신 HTTP 요청 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new RuntimeException("토큰 갱신 실패", e);
        }
    }

    private LocalDateTime extractExpAtFromAccessToken(String accessToken, Long fallbackExpiresInSec) {
        try {
            if (!StringUtils.hasText(accessToken)) {
                throw new IllegalArgumentException("accessToken is blank");
            }
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT format");
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(payloadJson);

            if (node.has("exp") && !node.get("exp").isNull()) {
                long expSeconds = node.get("exp").asLong();
                return Instant.ofEpochSecond(expSeconds)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (IllegalArgumentException e) {
            // 잘못된 JWT 형식 또는 Base64 디코딩 실패
            log.debug("Failed to extract exp from access token (IllegalArgumentException), fallback to expiresIn: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패
            log.debug("Failed to extract exp from access token (JsonProcessingException), fallback to expiresIn: {}", e.getMessage());
        } catch (NullPointerException e) {
            // null 참조 예외
            log.debug("Failed to extract exp from access token (NullPointerException), fallback to expiresIn: {}", e.getMessage());
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.debug("Failed to extract exp from access token, fallback to expiresIn: {}", e.getMessage());
        }

        long fallback = fallbackExpiresInSec != null ? fallbackExpiresInSec : 0L;
        return LocalDateTime.now().plusSeconds(fallback);
    }
}
