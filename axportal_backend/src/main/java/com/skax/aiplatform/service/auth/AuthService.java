package com.skax.aiplatform.service.auth;

import org.springframework.http.ResponseEntity;

import com.skax.aiplatform.client.sktai.auth.dto.response.AccessTokenResponseWithProject;
import com.skax.aiplatform.dto.auth.request.LoginReq;
import com.skax.aiplatform.dto.auth.request.RefreshTokenReq;
import com.skax.aiplatform.dto.auth.request.RegisterReq;
import com.skax.aiplatform.dto.auth.response.JwtTokenRes;

/**
 * 인증 서비스 인터페이스
 * 
 * <p>사용자 로그인, 토큰 발급, 토큰 갱신 등의 인증 관련 비즈니스 로직을 정의합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-02
 * @version 1.0.0
 */
public interface AuthService {

    /**
     * 사용자 로그인
     * 
     * @param loginReq 로그인 요청
     * @return JWT 토큰 응답
     */
    JwtTokenRes login(LoginReq loginReq) throws Exception;

    /**
     * SKTAI 토큰 교환 및 DB 저장 (기본 그룹)
     * 
     * <p>초기 인증 토큰을 사용하여 exchanged token을 발급받고 DB에 저장합니다.
     * 다른 클래스에서도 호출 가능하도록 별도 메소드로 분리되었습니다.</p>
     *
     * @return 교환된 토큰 응답
     */
    AccessTokenResponseWithProject exchangeAndSave();

    /**
     * SKTAI 토큰 교환 및 DB 저장 (프로젝트별 그룹)
     *
     * <p>prjSeq에 해당하는 사용자의 roleSeq를 조회하여 P{prjSeq}_R{roleSeq} 그룹으로 교환합니다.</p>
     *
     * @param prjSeq 프로젝트 시퀀스
     * @return 교환된 토큰 응답
     */
    AccessTokenResponseWithProject exchangeAndSave(Long prjSeq);

    ResponseEntity<?> registerUser(RegisterReq registerReq);

    boolean existsUserById(String username);

    /**
     * Access Token 갱신
     * 
     * @param refreshTokenReq 토큰 갱신 요청
     * @return 새로운 JWT 토큰 응답
     */
    JwtTokenRes refreshToken(RefreshTokenReq refreshTokenReq);

    /**
     * 로그아웃 (토큰 무효화)
     * 
     * @param username username
     */
    void logout(String username);
}
