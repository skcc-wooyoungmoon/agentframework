package com.skax.aiplatform.client.shinhan.config;

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
 * Shinhan 승인 API 에러 디코더
 * 
 * <p>Shinhan 승인 API 호출 시 발생하는 HTTP 에러를 적절한 비즈니스 예외로 변환합니다.
 * HTTP 상태 코드에 따라 구체적인 예외 타입을 결정하며, 응답 메시지를 파싱하여 상세한 오류 정보를 제공합니다.</p>
 * 
 * <h3>에러 코드 매핑:</h3>
 * <ul>
 *   <li>400: 잘못된 요청 - EXTERNAL_API_BAD_REQUEST</li>
 *   <li>401: 인증 실패 - EXTERNAL_API_UNAUTHORIZED</li>
 *   <li>403: 권한 부족 - EXTERNAL_API_FORBIDDEN</li>
 *   <li>404: 리소스 없음 - EXTERNAL_API_NOT_FOUND</li>
 *   <li>422: 유효성 검증 실패 - EXTERNAL_API_VALIDATION_ERROR</li>
 *   <li>500: 서버 오류 - EXTERNAL_API_SERVER_ERROR</li>
 *   <li>기타: 일반적인 외부 API 오류 - EXTERNAL_SERVICE_ERROR</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-11-17
 * @version 1.0
 */
@Slf4j
public class ShinhanErrorDecoder implements ErrorDecoder {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * HTTP 에러 응답을 비즈니스 예외로 변환
     * 
     * @param methodKey 호출된 메서드 키
     * @param response HTTP 응답
     * @return 변환된 예외
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        String errorDetail = extractErrorDetail(response);
        
        log.error("🟠 [SHINHAN] API 호출 실패: {} - Status: {}, URL: {}, Detail: {}", 
                methodKey, status, requestUrl, errorDetail);
        
        switch (status) {
            case 400:
                return new BusinessException(ErrorCode.EXTERNAL_API_BAD_REQUEST, 
                    "Shinhan 승인 API 잘못된 요청" + (errorDetail != null ? ": " + errorDetail : ""));
            case 401:
                return new BusinessException(ErrorCode.EXTERNAL_API_UNAUTHORIZED, 
                    "Shinhan 승인 API 인증 실패" + (errorDetail != null ? ": " + errorDetail : ""));
            case 403:
                return new BusinessException(ErrorCode.EXTERNAL_API_FORBIDDEN, 
                    "Shinhan 승인 API 접근 권한 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            case 404:
                return new BusinessException(ErrorCode.EXTERNAL_API_NOT_FOUND, 
                    "Shinhan 승인 API 리소스를 찾을 수 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            case 422:
                return new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, 
                    "Shinhan 승인 API 유효성 검증 실패" + (errorDetail != null ? ": " + errorDetail : ""));
            case 500:
            case 502:
            case 503:
                return new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR, 
                    "Shinhan 승인 API 서버 내부 오류" + (errorDetail != null ? ": " + errorDetail : ""));
            default:
                return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    String.format("Shinhan 승인 API 호출 실패 (Status: %d)", status) + 
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
            log.warn("Shinhan 승인 API 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Shinhan 승인 API 예상치 못한 오류로 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
