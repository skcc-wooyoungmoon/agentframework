package com.skax.aiplatform.controller.auth;

import com.skax.aiplatform.common.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.auth.request.RefreshTokenReq;
import com.skax.aiplatform.dto.auth.response.JwtTokenRes;
import com.skax.aiplatform.service.auth.AuthService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 컨트롤러 (AxResponseEntity 적용)
 * 
 * <p>사용자 인증 및 토큰 관리 API 엔드포인트를 제공합니다.
 * 로그인, 로그아웃, 토큰 갱신, 사용자 정보 조회 기능을 포함합니다.
 * AxResponseEntity를 통해 통합된 응답 형식을 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;

    /**
     * Access Token 갱신
     * 
     * @param refreshTokenReq 토큰 갱신 요청
     * @return 새로운 JWT 토큰 응답
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "토큰 갱신",
        description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token")
    })
    public AxResponseEntity<JwtTokenRes> refreshToken(
            @Valid @RequestBody RefreshTokenReq refreshTokenReq) {
        
        log.info("토큰 갱신 요청");
        
        try {
            JwtTokenRes tokenRes = authService.refreshToken(refreshTokenReq);
            log.info("토큰 갱신 성공");
            
            return AxResponseEntity.ok(tokenRes, "토큰이 성공적으로 갱신되었습니다.");
        } catch (BusinessException | BadCredentialsException | IllegalArgumentException | NullPointerException | DataAccessException e) {
            log.error("토큰 갱신 실패 (BusinessException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN); // 토큰 만료 안내
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("토큰 갱신 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 토큰 검증 - OAuth2 또는 JWT 인증 지원
     * 
     * @param request HTTP 요청 객체 (토큰 추출용)
     * @return 토큰 검증 결과
     */
    @GetMapping("/validate")
    @Operation(
        summary = "토큰 검증",
        description = "현재 OAuth2 또는 JWT Access Token의 유효성을 검증합니다. 두 인증 방식 모두 지원합니다.",
        security = {
            @SecurityRequirement(name = "OAuth2PasswordBearer"),
            @SecurityRequirement(name = "HTTPBearer")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "유효한 토큰"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    public AxResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        
        log.info("토큰 검증 요청");
        
        try {
            // 이 엔드포인트에 도달했다는 것은 토큰이 유효하다는 의미
            // (Spring Security 필터에서 이미 검증됨)
            return AxResponseEntity.ok(true, "토큰이 유효합니다.");
        } catch (BusinessException e) {
            // 비즈니스 예외 발생 시 토큰이 유효하지 않음
            log.warn("토큰 검증 실패 (BusinessException): {}", e.getMessage());
            return AxResponseEntity.ok(false, "토큰이 유효하지 않습니다.");
        } catch (BadCredentialsException e) {
            // 인증 실패 예외 발생 시 토큰이 유효하지 않음
            log.warn("토큰 검증 실패 (BadCredentialsException): {}", e.getMessage());
            return AxResponseEntity.ok(false, "토큰이 유효하지 않습니다.");
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외 발생 시 토큰이 유효하지 않음
            log.warn("토큰 검증 실패 (잘못된 인자): {}", e.getMessage());
            return AxResponseEntity.ok(false, "토큰이 유효하지 않습니다.");
        } catch (Exception e) {
            // 기타 예상치 못한 예외 발생 시도 토큰이 유효하지 않음
            log.error("토큰 검증 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            return AxResponseEntity.ok(false, "토큰이 유효하지 않습니다.");
        }
    }

 }