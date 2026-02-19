package com.skax.aiplatform.common.filter;

import com.skax.aiplatform.common.util.TraceUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 요청 추적 필터
 * 
 * <p>
 * 모든 HTTP 요청에 대해 고유한 추적 ID를 생성하고 MDC에 설정합니다.
 * 요청 시작과 완료 시점의 로그를 자동으로 기록합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String REAL_IP_HEADER = "X-Real-IP";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        try {
            // 추적 ID 생성 또는 기존 값 사용
            String traceId = getOrGenerateTraceId(httpRequest);
            String spanId = getOrGenerateSpanId(httpRequest);

            // MDC에 추적 정보 설정
            TraceUtils.setTraceId(traceId);
            TraceUtils.setSpanId(spanId);
            TraceUtils.setRequestUri(httpRequest.getRequestURI());
            TraceUtils.setRequestMethod(httpRequest.getMethod());
            TraceUtils.setClientIp(getClientIpAddress(httpRequest));
            TraceUtils.setUserAgent(httpRequest.getHeader("User-Agent"));

            // 응답 헤더에 추적 ID 추가
            httpResponse.setHeader(TRACE_ID_HEADER, sanitizeHeaderValue(traceId));
            httpResponse.setHeader(SPAN_ID_HEADER, sanitizeHeaderValue(spanId));

            // 요청 시작 로그
            TraceUtils.logRequestStart(
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    getClientIpAddress(httpRequest));

            // 다음 필터 체인 실행
            chain.doFilter(request, response);

        } finally {
            // 요청 완료 로그
            long duration = System.currentTimeMillis() - startTime;
            TraceUtils.logRequestEnd(
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration);

            // MDC 정리
            TraceUtils.clear();
        }
    }

    /**
     * 헤더 값에서 CR, LF 문자를 제거하여 보안 강화
     * 
     * @param input 헤더 값
     * @return 정제된 헤더 값
     */
    private String sanitizeHeaderValue(String input) {
        if (input == null)
            return "";
        return input.replaceAll("[\\r\\n]", ""); // CR, LF 제거
    }

    /**
     * 추적 ID 생성 또는 기존 값 반환
     * 
     * @param request HTTP 요청
     * @return 추적 ID
     */
    private String getOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceUtils.generateTraceId();
        }
        return traceId;
    }

    /**
     * 스팬 ID 생성 또는 기존 값 반환
     * 
     * @param request HTTP 요청
     * @return 스팬 ID
     */
    private String getOrGenerateSpanId(HttpServletRequest request) {
        String spanId = request.getHeader(SPAN_ID_HEADER);
        if (!StringUtils.hasText(spanId)) {
            spanId = TraceUtils.generateSpanId();
        }
        return spanId;
    }

    /**
     * 클라이언트 IP 주소 추출
     * 
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = null;

        // X-Forwarded-For 헤더 확인 (프록시 환경)
        String xForwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (StringUtils.hasText(xForwardedFor)) {
            clientIp = xForwardedFor.split(",")[0].trim();
        }

        // X-Real-IP 헤더 확인
        if (!StringUtils.hasText(clientIp)) {
            clientIp = request.getHeader(REAL_IP_HEADER);
        }

        // 기본 Remote Address 사용
        if (!StringUtils.hasText(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // IPv6 loopback을 IPv4로 변환
        if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
            clientIp = "127.0.0.1";
        }

        return clientIp != null ? clientIp : "unknown";
    }
}
