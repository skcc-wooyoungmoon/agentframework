package com.skax.aiplatform.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 토큰 유틸리티 클래스
 *
 * <p>JWT 토큰의 생성, 검증, 파싱 등의 기능을 제공합니다.
 * Access Token과 Refresh Token을 관리합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-01
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    /**
     * JWT 토큰 프로바이더 생성자
     *
     * @param secret                        JWT 서명용 비밀키
     * @param accessTokenValidityInSeconds  Access Token 유효 시간 (초)
     * @param refreshTokenValidityInSeconds Refresh Token 유효 시간 (초)
     */
    public JwtTokenProvider(
            @Value("${jwt.secret:myDefaultSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong}") String secret,
            @Value("${jwt.access-token-validity-in-seconds:900}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds:4500}") long refreshTokenValidityInSeconds) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    /**
     * Access Token 생성
     *
     * @param username    사용자명
     * @param authorities 권한 정보
     * @return JWT Access Token
     */
    public String createAccessToken(String username, String authorities) {
        return createAccessToken(username, authorities, null);
    }

    /**
     * Access Token 생성 (추가 클레임 포함)
     *
     * @param username    사용자명
     * @param authorities 권한 정보
     * @param extraClaims 추가 클레임 (예: 프로젝트 목록 등)
     * @return JWT Access Token
     */
    public String createAccessToken(String username, String authorities, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenValidityInMilliseconds, ChronoUnit.MILLIS);

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry));

        if (extraClaims != null && !extraClaims.isEmpty()) {
            for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
                builder.claim(entry.getKey(), entry.getValue());
            }
        }

        return builder.signWith(key).compact();
    }

    /**
     * Refresh Token 생성
     *
     * @param username 사용자명
     * @return JWT Refresh Token
     */
    public String createRefreshToken(String username, String authorities) {
        return createRefreshToken(username, authorities, null);
    }

    /**
     * Refresh Token 생성 (추가 클레임 포함)
     *
     * @param username 사용자명
     * @return JWT Refresh Token
     */
    public String createRefreshToken(String username, String authorities, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenValidityInMilliseconds, ChronoUnit.MILLIS);

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry));

        if (extraClaims != null && !extraClaims.isEmpty()) {
            for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
                builder.claim(entry.getKey(), entry.getValue());
            }
        }

        return builder.signWith(key).compact();
    }

    /**
     * 토큰에서 사용자명 추출
     *
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 토큰에서 권한 정보 추출
     *
     * @param token JWT 토큰
     * @return 권한 정보
     */
    public String getAuthoritiesFromToken(String token) {
        return getClaimFromToken(token, claims -> (String) claims.get("authorities"));
    }

    /**
     * 토큰에서 만료 시간 추출
     *
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 발급 시간 추출
     *
     * @param token JWT 토큰
     * @return 발급 시간
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * 토큰에서 특정 클레임 추출
     *
     * @param token          JWT 토큰
     * @param claimsResolver 클레임 추출 함수
     * @param <T>            반환 타입
     * @return 클레임 값
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     *
     * @param token JWT 토큰
     * @return 클레임 객체
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 만료 여부 확인
     *
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token       JWT 토큰
     * @param userDetails 사용자 상세 정보
     * @return 유효 여부
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 토큰 유효성 검증 (사용자명만으로)
     *
     * @param token    JWT 토큰
     * @param username 사용자명
     * @return 유효 여부
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 토큰 파싱 및 검증
     *
     * @param token JWT 토큰
     * @return 파싱 성공 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 토큰입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류가 발생했습니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Refresh Token으로 새로운 Access Token 생성
     *
     * @param refreshToken Refresh Token
     * @return 새로운 Access Token
     */
    public String refreshAccessToken(String refreshToken, String authorities, Map<String, Object> extraClaims) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }

        String username = getUsernameFromToken(refreshToken);
        String tokenType = getClaimFromToken(refreshToken, claims -> (String) claims.get("type"));

        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다");
        }

        // 여기서는 기본 권한을 설정합니다. 실제로는 사용자 정보를 조회하여 권한을 가져와야 합니다.
        return createAccessToken(username, authorities, extraClaims);
    }

    /**
     * Access Token 만료 시간까지 남은 시간 (밀리초)
     *
     * @return 남은 시간 (밀리초)
     */
    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInMilliseconds;
    }

    /**
     * Refresh Token 만료 시간까지 남은 시간 (밀리초)
     *
     * @return 남은 시간 (밀리초)
     */
    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }
}
