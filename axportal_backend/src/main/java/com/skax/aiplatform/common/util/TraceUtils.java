package com.skax.aiplatform.common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

/**
 * 요청 추적 유틸리티 클래스
 * 
 * <p>분산 트레이싱과 구조화된 로깅을 위한 유틸리티를 제공합니다.
 * MDC(Mapped Diagnostic Context)를 활용하여 요청 전반에 걸쳐 
 * 추적 정보를 관리합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
public class TraceUtils {

    // MDC 키 상수
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String PARENT_SPAN_ID_KEY = "parentSpanId";
    private static final String USER_ID_KEY = "userId";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String REQUEST_METHOD_KEY = "requestMethod";
    private static final String CLIENT_IP_KEY = "clientIp";
    private static final String USER_AGENT_KEY = "userAgent";

    /**
     * 새로운 추적 ID 생성
     * 
     * @return UUID 기반 추적 ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 새로운 스팬 ID 생성
     * 
     * @return UUID 기반 스팬 ID
     */
    public static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 추적 ID 설정
     * 
     * @param traceId 추적 ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 추적 ID 조회
     * 
     * @return 현재 추적 ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 스팬 ID 설정
     * 
     * @param spanId 스팬 ID
     */
    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID_KEY, spanId);
    }

    /**
     * 스팬 ID 조회
     * 
     * @return 현재 스팬 ID
     */
    public static String getSpanId() {
        return MDC.get(SPAN_ID_KEY);
    }

    /**
     * 부모 스팬 ID 설정
     * 
     * @param parentSpanId 부모 스팬 ID
     */
    public static void setParentSpanId(String parentSpanId) {
        if (parentSpanId != null) {
            MDC.put(PARENT_SPAN_ID_KEY, parentSpanId);
        }
    }

    /**
     * 부모 스팬 ID 조회
     * 
     * @return 현재 부모 스팬 ID
     */
    public static String getParentSpanId() {
        return MDC.get(PARENT_SPAN_ID_KEY);
    }

    /**
     * 부모 스팬 ID 제거
     */
    public static void removeParentSpanId() {
        MDC.remove(PARENT_SPAN_ID_KEY);
    }

    /**
     * 사용자 ID 설정
     * 
     * @param userId 사용자 ID
     */
    public static void setUserId(String userId) {
        MDC.put(USER_ID_KEY, userId);
    }

    /**
     * 사용자 ID 조회
     * 
     * @return 현재 사용자 ID
     */
    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    /**
     * 요청 URI 설정
     * 
     * @param requestUri 요청 URI
     */
    public static void setRequestUri(String requestUri) {
        MDC.put(REQUEST_URI_KEY, requestUri);
    }

    /**
     * 요청 URI 조회
     * 
     * @return 현재 요청 URI
     */
    public static String getRequestUri() {
        return MDC.get(REQUEST_URI_KEY);
    }

    /**
     * 요청 메서드 설정
     * 
     * @param requestMethod 요청 메서드
     */
    public static void setRequestMethod(String requestMethod) {
        MDC.put(REQUEST_METHOD_KEY, requestMethod);
    }

    /**
     * 요청 메서드 조회
     * 
     * @return 현재 요청 메서드
     */
    public static String getRequestMethod() {
        return MDC.get(REQUEST_METHOD_KEY);
    }

    /**
     * 클라이언트 IP 설정
     * 
     * @param clientIp 클라이언트 IP
     */
    public static void setClientIp(String clientIp) {
        MDC.put(CLIENT_IP_KEY, clientIp);
    }

    /**
     * 클라이언트 IP 조회
     * 
     * @return 현재 클라이언트 IP
     */
    public static String getClientIp() {
        return MDC.get(CLIENT_IP_KEY);
    }

    /**
     * User Agent 설정
     * 
     * @param userAgent User Agent
     */
    public static void setUserAgent(String userAgent) {
        MDC.put(USER_AGENT_KEY, userAgent);
    }

    /**
     * User Agent 조회
     * 
     * @return 현재 User Agent
     */
    public static String getUserAgent() {
        return MDC.get(USER_AGENT_KEY);
    }

    /**
     * 모든 MDC 컨텍스트 조회
     * 
     * @return MDC 컨텍스트 맵
     */
    public static Map<String, String> getContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * MDC 컨텍스트 정리
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 현재 MDC 컨텍스트 로깅 (디버그용)
     */
    public static void logContext() {
        log.debug("Current MDC Context: traceId={}, spanId={}, userId={}, requestUri={}, requestMethod={}, clientIp={}",
                getTraceId(), getSpanId(), getUserId(), getRequestUri(), getRequestMethod(), getClientIp());
    }

    /**
     * 요청 시작 로그
     * 
     * @param method HTTP 메서드
     * @param uri 요청 URI
     * @param clientIp 클라이언트 IP
     */
    public static void logRequestStart(String method, String uri, String clientIp) {
        log.info("REQUEST_START: {} {} from {}", method, uri, clientIp);
    }

    /**
     * 요청 완료 로그
     * 
     * @param method HTTP 메서드
     * @param uri 요청 URI
     * @param statusCode 응답 상태 코드
     * @param duration 실행 시간 (밀리초)
     */
    public static void logRequestEnd(String method, String uri, int statusCode, long duration) {
        log.info("REQUEST_END: {} {} - {} ({}ms)", method, uri, statusCode, duration);
    }

    /**
     * API 호출 시작 로그
     * 
     * @param className 클래스명
     * @param methodName 메서드명
     * @param params 파라미터
     */
    public static void logApiCallStart(String className, String methodName, String params) {
        log.debug("API_CALL_START: {}.{}() with params: {}", className, methodName, params);
    }

    /**
     * API 호출 완료 로그
     * 
     * @param className 클래스명
     * @param methodName 메서드명
     * @param duration 실행 시간 (밀리초)
     * @param result 호출 결과
     */
    public static void logApiCallEnd(String className, String methodName, long duration, String result) {
        log.debug("API_CALL_END: {}.{}() completed in {}ms, result: {}", className, methodName, duration, result);
    }

    /**
     * 오류 로그 (컨텍스트 정보 포함)
     * 
     * @param message 오류 메시지
     * @param throwable 예외 객체
     * @param contextPairs 추가 컨텍스트 정보 (키-값 쌍)
     */
    public static void logError(String message, Throwable throwable, String... contextPairs) {
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < contextPairs.length; i += 2) {
            if (i + 1 < contextPairs.length) {
                if (contextBuilder.length() > 0) {
                    contextBuilder.append(", ");
                }
                contextBuilder.append(contextPairs[i]).append("=").append(contextPairs[i + 1]);
            }
        }
        
        String context = contextBuilder.length() > 0 ? " [" + contextBuilder.toString() + "]" : "";
        log.error("ERROR: {}{}", message + context, throwable);
    }

    /**
     * 오버로드된 오류 로그 메서드 (기존 호환성 유지)
     * 
     * @param className 클래스명
     * @param methodName 메서드명
     * @param throwable 예외 객체
     */
    public static void logError(String className, String methodName, Throwable throwable) {
        log.error("ERROR: {}.{}() - {}: {}", className, methodName, 
                throwable.getClass().getSimpleName(), throwable.getMessage(), throwable);
    }
}
