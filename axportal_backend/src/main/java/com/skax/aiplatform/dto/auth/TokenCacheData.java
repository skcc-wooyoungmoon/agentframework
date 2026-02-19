package com.skax.aiplatform.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 토큰 캐시 데이터 모델
 * GpoTokensMas 엔티티를 대체하여 Caffeine 캐시에서 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenCacheData {

    /**
     * 고유 식별자 (PK)
     */
    private String uuid;

    /**
     * 멤버 아이디
     */
    private String memberId;

    /**
     * 접근 토큰
     */
    private String accessToken;

    /**
     * 리프레시 토큰
     */
    private String refreshToken;

    /**
     * 토큰 타입
     */
    private String tokenType;

    /**
     * 발급 일시
     */
    private LocalDateTime issueAt;

    /**
     * 만료 일시
     */
    private LocalDateTime expAt;

    /**
     * 리프레시 토큰 만료 일시
     */
    private LocalDateTime refreshTokenExpAt;

    /**
     * 토큰 만료 여부 (Y/N)
     */
    @Builder.Default
    private String tokenExpYn = "N";

    /**
     * 토큰 만료 시간 (초 등 단위는 스키마 정의에 따름)
     */
    private Long tokenExpTimes;

    /**
     * 리프레시 토큰 만료 시간
     */
    private Long refreshTokenExpTimes;

    /**
     * 토큰 만료 여부 확인 메소드
     */
    public boolean isExpired() {
        return expAt == null || LocalDateTime.now().isAfter(expAt);
    }

    /**
     * 토큰 유효성 검사 메소드
     */
    public boolean isValid() {
        return "N".equals(tokenExpYn) && !isExpired();
    }

    /**
     * 리프레시 토큰 만료 여부 확인 메소드
     */
    public boolean isRefreshExpired() {
        return refreshTokenExpAt == null || LocalDateTime.now().isAfter(refreshTokenExpAt);
    }
}
