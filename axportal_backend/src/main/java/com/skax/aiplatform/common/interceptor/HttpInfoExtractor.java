package com.skax.aiplatform.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 정보 추출 담당 클래스
 * 
 * <p>HTTP 상태 코드, 클라이언트 IP, User-Agent 등의 정보를 추출합니다.</p>
 * 
 * @author sonmunwoo
 * @since 2025-10-19
 * @version 1.0.0
 */
@Slf4j
@Component
public class HttpInfoExtractor {

    /**
     * HTTP 상태 코드 추출
     * 
     * @param result 메서드 실행 결과
     * @param throwable 발생한 예외 (있는 경우)
     * @return HTTP 상태 코드 문자열
     */
    public String getHttpStatusCode(Object result, Throwable throwable) {
        try {
            // 예외가 발생한 경우
            if (throwable != null) {
                return getHttpStatusCodeFromException(throwable);
            }
            
            // 성공한 경우 - ResponseEntity나 AxResponseEntity에서 상태 코드 추출
            if (result != null) {
                return getHttpStatusCodeFromResult(result);
            }
            
            // 기본값: 성공 (200)
            return "200";
            
        } catch (RuntimeException e) {
            log.debug("HTTP 상태 코드 추출 실패 (RuntimeException): {}", e.getMessage());
            return "200"; // 기본값
        }
    }

    /**
     * 예외에서 HTTP 상태 코드 추출
     */
    private String getHttpStatusCodeFromException(Throwable throwable) {
        String exceptionType = throwable.getClass().getSimpleName();
        
        // Spring의 HTTP 관련 예외들
        if (exceptionType.contains("HttpStatusException") || 
            exceptionType.contains("ResponseStatusException")) {
            String message = throwable.getMessage();
            if (message != null) {
                String[] parts = message.split("\\s+");
                for (String part : parts) {
                    if (part.matches("\\d{3}")) {
                        return part;
                    }
                }
            }
        }
        
        // 일반적인 예외 타입별 기본 상태 코드
        return switch (exceptionType) {
            case "AuthenticationException", "BadCredentialsException" -> "401";
            case "AccessDeniedException" -> "403";
            case "ValidationException", "MethodArgumentNotValidException", "ConstraintViolationException" -> "400";
            case "BusinessException" -> "400";
            case "EntityNotFoundException", "NoSuchElementException" -> "404";
            case "IllegalArgumentException", "IllegalStateException" -> "400";
            case "UnsupportedOperationException" -> "501";
            default -> "500";
        };
    }

    /**
     * 결과 객체에서 HTTP 상태 코드 추출
     */
    private String getHttpStatusCodeFromResult(Object result) {
        try {
            // ResponseEntity인 경우
            if (result.getClass().getName().contains("ResponseEntity")) {
                java.lang.reflect.Method getStatusCodeMethod = result.getClass().getMethod("getStatusCode");
                Object statusCode = getStatusCodeMethod.invoke(result);
                
                if (statusCode != null) {
                    java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
                    Object statusValue = valueMethod.invoke(statusCode);
                    return String.valueOf(statusValue);
                }
            }
            
            // AxResponseEntity인 경우
            if (result.getClass().getName().contains("AxResponseEntity")) {
                try {
                    java.lang.reflect.Method getHttpStatusMethod = result.getClass().getMethod("getHttpStatus");
                    Object httpStatus = getHttpStatusMethod.invoke(result);
                    if (httpStatus != null) {
                        return String.valueOf(httpStatus);
                    }
                } catch (NoSuchMethodException e) {
                    log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 - getHttpStatus 없음: {}", e.getMessage());
                }
            }
            
            // 기본값: 성공 (200)
            return "200";
            
        } catch (NoSuchMethodException e) {
            log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 (NoSuchMethodException): {}", e.getMessage());
            return "200"; // 기본값
        } catch (IllegalAccessException e) {
            log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 (IllegalAccessException): {}", e.getMessage());
            return "200"; // 기본값
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 (InvocationTargetException): {}", e.getMessage());
            return "200"; // 기본값
        } catch (ClassCastException e) {
            log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 (ClassCastException): {}", e.getMessage());
            return "200"; // 기본값
        } catch (RuntimeException e) {
            log.debug("결과 객체에서 HTTP 상태 코드 추출 실패 (RuntimeException): {}", e.getMessage());
            return "200"; // 기본값
        }
    }

    /**
     * API 엔드포인트 생성 (실제 API 경로 형태)
     */
    public String getApiEndpoint(String className, String methodName) {
        // 현재 요청 URI 가져오기
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String uri = request.getRequestURI();
                
                // 실제 요청된 API 경로 반환
                if (uri != null && !uri.isEmpty()) {
                    return uri;
                }
            }
        } catch (ClassCastException e) {
            log.debug("API 엔드포인트 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("API 엔드포인트 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("API 엔드포인트 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("API 엔드포인트 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        
        // 폴백: 클래스명과 메서드명으로 API 경로 추론
        return inferApiEndpoint(className, methodName);
    }

    /**
     * Controller 엔드포인트 생성
     */
    public String getControllerEndpoint(String className, String methodName) {
        return String.format("Controller.%s.%s", className, methodName);
    }

    /**
     * 현재 HTTP 요청의 메서드 추출 (GET, POST, PUT, DELETE 등)
     * 
     * @return HTTP 메서드
     */
    public String getHttpMethod() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request != null ? request.getMethod() : null;
            }
        } catch (ClassCastException e) {
            log.debug("HTTP 메서드 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("HTTP 메서드 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("HTTP 메서드 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("HTTP 메서드 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }

    /**
     * 클래스명과 메서드명으로 API 경로 추론
     */
    private String inferApiEndpoint(String className, String methodName) {
        StringBuilder endpoint = new StringBuilder("/api/v1");
        
        // 클래스명에서 API 경로 추론
        if (className.contains("User")) {
            endpoint.append("/auth/users");
        } else if (className.contains("Model")) {
            endpoint.append("/model");
        } else if (className.contains("Agent")) {
            endpoint.append("/agent");
        } else if (className.contains("Knowledge") || className.contains("Queries")) {
            endpoint.append("/knowledge");
        } else if (className.contains("Evaluation")) {
            endpoint.append("/evaluation");
        } else if (className.contains("Dataset") || className.contains("Data")) {
            endpoint.append("/data");
        } else if (className.contains("Project")) {
            endpoint.append("/project");
        } else if (className.contains("Admin")) {
            endpoint.append("/admin");
        } else {
            endpoint.append("/external");
        }
        
        // 메서드명에서 세부 경로 추론
        if (methodName.contains("getCurrentUser") || methodName.equals("me")) {
            endpoint.append("/me");
        } else if (methodName.contains("create") || methodName.contains("add")) {
            // POST 요청은 기본 경로 사용
        } else if (methodName.contains("update") || methodName.contains("modify")) {
            endpoint.append("/{id}");
        } else if (methodName.contains("delete") || methodName.contains("remove")) {
            endpoint.append("/{id}");
        } else if (methodName.contains("getAll") || methodName.contains("list")) {
            // GET 목록 요청은 기본 경로 사용
        } else if (methodName.contains("getById") || methodName.contains("get")) {
            endpoint.append("/{id}");
        } else if (methodName.contains("invoke") || methodName.contains("execute")) {
            endpoint.append("/invoke");
        } else if (methodName.contains("gateway")) {
            endpoint.append("/gateway");
        } else if (methodName.contains("queries") || methodName.contains("search")) {
            endpoint.append("/queries");
        }
        
        return endpoint.toString();
    }

    /**
     * 클라이언트 IP 주소 추출 (IPv6 localhost 변환 포함)
     */
    public String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 프록시를 통한 접속인 경우 실제 IP 추출 (우선순위 순서대로)
                String clientIp = request.getHeader("X-Forwarded-For");
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("Proxy-Client-IP");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("WL-Proxy-Client-IP");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("HTTP_CLIENT_IP");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("X-Real-IP");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("X-RealIP");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getHeader("REMOTE_ADDR");
                }
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getRemoteAddr();
                }
                
                // 여러 IP가 있는 경우 첫 번째 IP 사용 (프록시 체인)
                if (clientIp != null && clientIp.contains(",")) {
                    clientIp = clientIp.split(",")[0].trim();
                }
                
                // IPv6 localhost를 IPv4 localhost로 변환
                if (clientIp != null) {
                    clientIp = normalizeIpAddress(clientIp);
                }
                
                return clientIp;
            }
        } catch (ClassCastException e) {
            log.debug("클라이언트 IP 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("클라이언트 IP 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("클라이언트 IP 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("클라이언트 IP 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return "UNKNOWN";
    }
    
    /**
     * IP 주소 정규화 (IPv6 localhost를 IPv4로 변환)
     */
    private String normalizeIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "UNKNOWN";
        }
        
        // IPv6 localhost 주소들을 IPv4 localhost로 변환
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        
        // IPv6 형식인 경우 간단한 처리
        if (ip.contains(":") && ip.length() > 15) {
            // IPv6를 IPv4로 매핑된 주소 추출 (예: ::ffff:192.168.1.1)
            if (ip.startsWith("::ffff:")) {
                return ip.substring(7);
            }
            // 그 외 IPv6는 그대로 반환 (실제 IPv6 주소)
            return ip;
        }
        
        return ip;
    }

    /**
     * 정제된 사용자 접속 환경 정보 추출
     */
    public String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userAgent = request.getHeader("User-Agent");
                
                if (userAgent != null && !userAgent.isEmpty()) {
                    return parseUserAgent(userAgent);
                }
            }
        } catch (ClassCastException e) {
            log.debug("사용자 에이전트 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("사용자 에이전트 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("사용자 에이전트 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("사용자 에이전트 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return "UNKNOWN";
    }
    
    /**
     * User-Agent 문자열을 파싱하여 정제된 접속 환경 정보로 변환
     */
    private String parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "UNKNOWN";
        }
        
        String browser = detectBrowser(userAgent);
        String os = detectOS(userAgent);
        
        return String.format("%s / %s", browser, os);
    }
    
    /**
     * 브라우저 정보 감지 (버전 제외)
     */
    private String detectBrowser(String userAgent) {
        String ua = userAgent.toLowerCase();
        
        // Edge (Chromium 기반) - Chrome보다 먼저 체크
        if (ua.contains("edg/")) return "Edge";
        
        // Chrome
        if (ua.contains("chrome/") && !ua.contains("edg")) return "Chrome";
        
        // Safari (Chrome이 아닌 경우에만)
        if (ua.contains("safari/") && !ua.contains("chrome")) return "Safari";
        
        // Firefox
        if (ua.contains("firefox/")) return "Firefox";
        
        // Opera
        if (ua.contains("opr/") || ua.contains("opera/")) return "Opera";
        
        // Samsung Internet
        if (ua.contains("samsungbrowser/")) return "Samsung Internet";
        
        // Internet Explorer
        if (ua.contains("msie") || ua.contains("trident/")) return "IE";
        
        return "Other";
    }
    
    /**
     * 운영체제 정보 감지 (버전 제외)
     */
    private String detectOS(String userAgent) {
        String ua = userAgent.toLowerCase();
        
        // Windows
        if (ua.contains("windows")) return "Windows";
        
        // macOS
        if (ua.contains("mac os x") || ua.contains("macintosh")) return "macOS";
        
        // iOS
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        
        // Android
        if (ua.contains("android")) return "Android";
        
        // Linux
        if (ua.contains("linux")) return "Linux";
        
        // Unix
        if (ua.contains("unix")) return "Unix";
        
        return "Other";
    }

    /**
     * 프론트엔드 메뉴 경로 추출
     * 
     * <p>HTTP 요청 헤더에서 프론트엔드 URL 정보를 추출합니다.</p>
     * 
     * <ul>
     *   <li>우선순위 1: X-Frontend-Path 커스텀 헤더 (프론트엔드가 명시적으로 전송)</li>
     *   <li>우선순위 2: Referer 헤더 (브라우저가 자동으로 전송)</li>
     *   <li>우선순위 3: Origin 헤더 (CORS 요청 시 자동 전송)</li>
     * </ul>
     * 
     * @return 프론트엔드 메뉴 경로 (예: "/home/dashboard" 또는 전체 URL)
     */
    public String getFrontendPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 1. 커스텀 헤더 확인 (프론트엔드에서 명시적으로 전송)
                String frontendPath = request.getHeader("X-Frontend-Path");
                if (frontendPath != null && !frontendPath.isEmpty()) {
                    log.debug("프론트엔드 경로 추출 (커스텀 헤더): {}", frontendPath);
                    return frontendPath;
                }
                
                // 2. Referer 헤더에서 추출 (브라우저가 자동으로 전송하는 이전 페이지)
                String referer = request.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                    String extractedPath = extractPathFromUrl(referer);
                    log.debug("프론트엔드 경로 추출 (Referer): {} -> {}", referer, extractedPath);
                    return extractedPath;
                }
                
                // 3. Origin 헤더 확인 (CORS 요청 시)
                String origin = request.getHeader("Origin");
                if (origin != null && !origin.isEmpty()) {
                    log.debug("프론트엔드 Origin 추출: {}", origin);
                    return origin;
                }
            }
        } catch (ClassCastException e) {
            log.debug("프론트엔드 경로 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("프론트엔드 경로 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("프론트엔드 경로 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("프론트엔드 경로 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }

    /**
     * 프론트엔드 전체 URL 추출 (도메인 포함)
     * 
     * @return 프론트엔드 전체 URL (예: "http://localhost:5173/home/dashboard")
     */
    public String getFrontendFullUrl() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 1. 커스텀 헤더로 전체 URL 확인
                String frontendUrl = request.getHeader("X-Frontend-Url");
                if (frontendUrl != null && !frontendUrl.isEmpty()) {
                    log.debug("프론트엔드 전체 URL 추출 (커스텀 헤더): {}", frontendUrl);
                    return frontendUrl;
                }
                
                // 2. Referer 헤더 (일반적으로 전체 URL 포함)
                String referer = request.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                    log.debug("프론트엔드 전체 URL 추출 (Referer): {}", referer);
                    return referer;
                }
            }
        } catch (ClassCastException e) {
            log.debug("프론트엔드 전체 URL 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("프론트엔드 전체 URL 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("프론트엔드 전체 URL 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("프론트엔드 전체 URL 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }

    /**
     * 프론트엔드 메뉴명 추출
     * 
     * <p>URL 경로에서 메뉴명을 추출합니다. (예: "/home/dashboard" -> "dashboard")</p>
     * 
     * @return 프론트엔드 메뉴명
     */
    public String getFrontendMenuName() {
        String frontendPath = getFrontendPath();
        if (frontendPath == null || frontendPath.isEmpty()) {
            return null;
        }
        
        try {
            // URL에서 경로 부분만 추출
            String path = extractPathFromUrl(frontendPath);
            
            // 경로의 마지막 세그먼트를 메뉴명으로 사용
            String[] segments = path.split("/");
            for (int i = segments.length - 1; i >= 0; i--) {
                if (!segments[i].isEmpty()) {
                    return segments[i];
                }
            }
        } catch (NullPointerException e) {
            log.debug("프론트엔드 메뉴명 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("프론트엔드 메뉴명 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * URL에서 경로 부분만 추출
     * 
     * @param url 전체 URL (예: "http://localhost:5173/home/dashboard")
     * @return 경로 부분 (예: "/home/dashboard")
     */
    private String extractPathFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        try {
            // 프로토콜이 있는 경우 (http://, https://)
            if (url.startsWith("http://") || url.startsWith("https://")) {
                java.net.URL urlObj = new java.net.URL(url);
                String path = urlObj.getPath();
                
                // 쿼리 파라미터가 있으면 포함
                String query = urlObj.getQuery();
                if (query != null && !query.isEmpty()) {
                    path += "?" + query;
                }
                
                return path;
            }
            
            // 이미 경로만 있는 경우
            return url;
            
        } catch (java.net.MalformedURLException e) {
            log.debug("URL 경로 추출 실패 (MalformedURLException): {}", e.getMessage());
            return url; // 실패 시 원본 반환
        } catch (RuntimeException e) {
            log.debug("URL 경로 추출 실패 (RuntimeException): {}", e.getMessage());
            return url; // 실패 시 원본 반환
        }
    }

    /**
     * 프론트엔드 호스트 정보 추출
     * 
     * @return 프론트엔드 호스트 (예: "localhost:5173")
     */
    public String getFrontendHost() {
        try {
            String fullUrl = getFrontendFullUrl();
            if (fullUrl != null && !fullUrl.isEmpty()) {
                java.net.URL urlObj = new java.net.URL(fullUrl);
                String host = urlObj.getHost();
                int port = urlObj.getPort();
                
                if (port != -1 && port != 80 && port != 443) {
                    return host + ":" + port;
                }
                return host;
            }
        } catch (java.net.MalformedURLException e) {
            log.debug("프론트엔드 호스트 추출 실패 (MalformedURLException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("프론트엔드 호스트 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("프론트엔드 호스트 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }
}

