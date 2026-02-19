package com.skax.aiplatform.client.sktai.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.service.auth.TokenCacheService;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * SKTAI API 에러 디코더
 *
 * <p>
 * SKTAI API 응답 에러를 내부 예외로 변환합니다.
 * HTTP 상태 코드에 따라 적절한 예외를 발생시키며, 응답 메시지를 파싱하여 상세한 오류 정보를 제공합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 1.1 - 상세한 에러 메시지 파싱 추가
 * @since 2025-08-15
 */
@Slf4j
public class SktaiErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenCacheService tokenCacheService;

    public SktaiErrorDecoder(TokenCacheService tokenCacheService) {
        this.tokenCacheService = tokenCacheService;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        String errorDetail = extractErrorDetail(response);

        log.error("🔵 [SKTAI] API 호출 실패: {} - Status: {}, URL: {}, Detail: {}",
                methodKey, status, requestUrl, errorDetail);

        switch (status) {
            case 400:
                return new BusinessException(ErrorCode.EXTERNAL_API_BAD_REQUEST,
                        "SKTAI API 잘못된 요청" + (errorDetail != null ? ": " + errorDetail : ""));
            case 401:
                // return new BusinessException(ErrorCode.EXTERNAL_API_UNAUTHORIZED,
                //         "SKTAI API 인증 실패" + (errorDetail != null ? ": " + errorDetail : ""));
                // 401 발생 시 토큰 만료로 간주하고 캐시에서 제거하여 재시도 유도
                // 이미 Interceptor에서 토큰을 갱신해서 보냈는데도 401이라면, 서버측에서 토큰이 폐기되었거나 유효하지 않은 상태
                log.warn("🚨 [SKTAI] 401 Unauthorized 발생 - 토큰 캐시 제거 및 재시도 요청: {}", requestUrl);
                removeTokenFromCache();

                // RetryableException을 던지면 Feign Retryer 정책에 따라 재시도
                // (기본 설정: 1초 대기, 최대 3회)
                return new RetryableException(
                        status,
                        "SKTAI API 인증 실패 (401) - 토큰 갱신 후 재시도 필요",
                        response.request().httpMethod(),
                        (Long) null, // retryAfter (null = default backoff)
                        response.request()
                );
            case 403:
                return new BusinessException(ErrorCode.EXTERNAL_API_FORBIDDEN);
            // "SKTAI API 접근 권한 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            case 404:
                return new BusinessException(ErrorCode.EXTERNAL_API_NOT_FOUND,
                        "SKTAI API 리소스를 찾을 수 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            case 409:
                return new BusinessException(ErrorCode.EXTERNAL_API_CONFLICT,
                        "SKTAI API 리소스 충돌" + (errorDetail != null ? ": " + errorDetail : ""));
            case 422:
                // 422 오류의 경우 서버측 데이터베이스 오류일 가능성이 높으므로 더 구체적인 메시지 제공
                String message = errorDetail != null && errorDetail.contains("Database error")
                        ? "SKTAI API 서버 데이터베이스 오류: " + errorDetail
                        : "SKTAI API 유효성 검증 실패" + (errorDetail != null ? ": " + errorDetail : "");
                return new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, message);
            case 500:
                return new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR,
                        "SKTAI API 서버 내부 오류" + (errorDetail != null ? ": " + errorDetail : ""));
            default:
                return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                        String.format("SKTAI API 호출 실패 (Status: %d)", status) +
                                (errorDetail != null ? ": " + errorDetail : ""));
        }
    }

    /**
     * 응답 바디에서 오류 상세 정보를 추출합니다.
     *
     * @param response Feign 응답 객체
     * @return 오류 상세 메시지 또는 null
     */
    private String extractErrorDetail(Response response) {
        if (response.body() == null) {
            return null;
        }

        try {
            // 응답 바디를 문자열로 읽기
            String responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // 디버깅을 위해 전체 응답 본문 로깅
            log.debug("SKTAI API 에러 응답 본문: {}", responseBody);

            if (responseBody.trim().isEmpty()) {
                return null;
            }

            // JSON 파싱 시도
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // "detail" 필드 추출
            if (jsonNode.has("detail")) {
                JsonNode detailNode = jsonNode.get("detail");
                if (detailNode.isTextual()) {
                    return detailNode.asText();
                } else {
                    // detail이 객체인 경우 전체를 문자열로 변환
                    return objectMapper.writeValueAsString(detailNode);
                }
            }

            // "message" 필드 추출 (대안)
            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }

            // "error" 필드 추출 (대안)
            if (jsonNode.has("error")) {
                JsonNode errorNode = jsonNode.get("error");
                if (errorNode.isTextual()) {
                    return errorNode.asText();
                } else if (errorNode.has("message")) {
                    return errorNode.get("message").asText();
                } else {
                    // error가 객체인 경우 전체를 문자열로 변환
                    return objectMapper.writeValueAsString(errorNode);
                }
            }

            // JSON이지만 알려진 필드가 없는 경우 전체 응답 반환
            return responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody;

        } catch (IOException e) {
            log.warn("응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("예상치 못한 오류로 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 토큰 캐시에서 현재 사용자의 토큰을 제거합니다.
     */
    private void removeTokenFromCache() {
        String username;
        // Admin 모드 체크
        if (AdminContext.isAdminMode()) {
            username = AdminContext.getAdminUsername();
        } else {
            username = getCurrentUsername();
        }

        if (StringUtils.hasText(username)) {
            tokenCacheService.removeTokenFromCache(username);
            log.debug("User {} token evicted from cache due to 401 error", username);
        } else {
            log.warn("Could not identify user to evict token for 401 error");
        }
    }

    /**
     * SecurityContext에서 현재 사용자명을 조회합니다.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
