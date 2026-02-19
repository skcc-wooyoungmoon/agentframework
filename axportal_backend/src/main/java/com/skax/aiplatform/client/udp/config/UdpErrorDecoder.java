package com.skax.aiplatform.client.udp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * UDP API 에러 디코더
 * 
 * <p>UDP API 응답 오류를 적절한 비즈니스 예외로 변환합니다.
 * HTTP 상태 코드에 따라 적절한 비즈니스 예외를 생성하며, 응답 메시지를 파싱하여 상세한 오류 정보를 제공합니다.</p>
 * 
 * <h3>처리하는 HTTP 상태 코드:</h3>
 * <ul>
 *   <li><strong>400</strong>: 잘못된 요청 - INVALID_INPUT_VALUE</li>
 *   <li><strong>401</strong>: 인증 실패 - EXTERNAL_API_UNAUTHORIZED</li>
 *   <li><strong>403</strong>: 권한 없음 - EXTERNAL_API_FORBIDDEN</li>
 *   <li><strong>404</strong>: 리소스 없음 - EXTERNAL_API_NOT_FOUND</li>
 *   <li><strong>422</strong>: 검증 실패 - EXTERNAL_API_VALIDATION_ERROR (Database error 특별 처리)</li>
 *   <li><strong>500</strong>: 서버 오류 - EXTERNAL_API_SERVER_ERROR</li>
 *   <li><strong>기타</strong>: 외부 API 오류 - EXTERNAL_SERVICE_ERROR</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 2.0 - 상세한 에러 메시지 파싱 추가, 422 오류 특별 처리
 */
@Slf4j
public class UdpErrorDecoder implements ErrorDecoder {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        String errorDetail = extractErrorDetail(response);
        
        log.error("🟠 [UDP] API 호출 실패: {} - Status: {}, URL: {}, Detail: {}", 
                methodKey, status, requestUrl, errorDetail);

        return switch (status) {
            case 400 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_BAD_REQUEST, 
                        "UDP API 잘못된 요청" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 401 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_UNAUTHORIZED, 
                        "UDP API 인증 실패" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 403 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_FORBIDDEN, 
                        "UDP API 접근 권한 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 404 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_NOT_FOUND, 
                        "UDP API 리소스를 찾을 수 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 422 -> {
                // 422 오류의 경우 서버측 데이터베이스 오류일 가능성이 높으므로 더 구체적인 메시지 제공
                String message = errorDetail != null && errorDetail.contains("Database error") 
                    ? "UDP API 서버 데이터베이스 오류: " + errorDetail
                    : "UDP API 유효성 검증 실패" + (errorDetail != null ? ": " + errorDetail : "");
                yield new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, message);
            }
            case 500 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR, 
                        "UDP API 서버 내부 오류" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            default -> {
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        String.format("UDP API 호출 실패 (Status: %d)", status) + 
                        (errorDetail != null ? ": " + errorDetail : ""));
            }
        };
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
            
            if (responseBody.trim().isEmpty()) {
                return null;
            }
            
            // JSON 파싱 시도
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            // "detail" 필드 추출
            if (jsonNode.has("detail")) {
                return jsonNode.get("detail").asText();
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
                }
            }
            
            // JSON이지만 알려진 필드가 없는 경우 전체 응답 반환 (간략화)
            return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
            
        } catch (IOException e) {
            log.warn("UDP API 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("UDP API 예상치 못한 오류로 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}