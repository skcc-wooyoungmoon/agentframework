package com.skax.aiplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gpo_tokens_mas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
public class GpoTokensMas {

    /**
     * 고유 식별자 (PK)
     */
    @Id
    @Column(name = "uuid", nullable = false, length = 100)
    private String uuid;

    /**
     * 멤버 아이디
     */
    @Column(name = "member_id", length = 50)
    private String memberId;

    /**
     * 접근 토큰
     */
    @Column(name = "access_token", length = 4000)
    private String accessToken;

    /**
     * 리프레시 토큰
     */
    @Column(name = "refresh_token", length = 4000)
    private String refreshToken;

    /**
     * 토큰 타입
     */
    @Column(name = "token_type", length = 10)
    private String tokenType;

    /**
     * 만료 일시
     */
    @Column(name = "exp_at")
    private LocalDateTime expAt;

    /**
     * 리프레시 토큰 만료 일시
     */
    @Column(name = "refresh_token_exp_at")
    private LocalDateTime refreshTokenExpAt;

    /**
     * 토큰 만료 여부 (Y/N)
     */
    @Builder.Default
    @Column(name = "token_exp_yn", length = 1)
    private String tokenExpYn = "N";

    /**
     * 토큰 만료 시간 (초 등 단위는 스키마 정의에 따름)
     */
    @Column(name = "token_exp_times")
    private Long tokenExpTimes;

    /**
     * 리프레시 토큰 만료 시간
     */
    @Column(name = "refresh_token_exp_times")
    private Long refreshTokenExpTimes;

    // 토큰 만료 여부 확인 메소드
    public boolean isExpired() {
        return expAt == null || LocalDateTime.now().isAfter(expAt);
    }

    // 토큰 유효성 검사 메소드
    public boolean isValid() {
        return "N".equals(tokenExpYn) && !isExpired();
    }

    // 리프레시 토큰 만료 여부 확인 메소드
    public boolean isRefreshExpired() {
        return refreshTokenExpAt == null || LocalDateTime.now().isAfter(refreshTokenExpAt);
    }
}
