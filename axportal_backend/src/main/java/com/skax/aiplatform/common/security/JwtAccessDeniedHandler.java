package com.skax.aiplatform.common.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.response.AxResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 접근 거부 핸들러
 * 
 * <p>인증된 사용자가 권한이 없는 리소스에 접근할 때 호출됩니다.
 * 표준화된 에러 응답을 반환합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {

        log.warn("권한이 없는 사용자의 접근 시도: {} {}, IP: {}, User-Agent: {}", 
                request.getMethod(), request.getRequestURI(), 
                getClientIpAddress(request), request.getHeader("User-Agent"));

        // 응답 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 에러 응답 생성
        AxResponse<Void> errorResponse = AxResponse.failure(
                "접근 권한이 없습니다. 해당 리소스에 대한 권한을 확인해주세요.",
                "ACCESS_DENIED",
                "Forbidden"
        );

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 클라이언트 IP 주소 추출
     * 
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
