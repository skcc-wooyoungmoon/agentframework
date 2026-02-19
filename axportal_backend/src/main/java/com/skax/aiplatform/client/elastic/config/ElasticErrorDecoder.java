package com.skax.aiplatform.client.elastic.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.exception.ValidationException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch API 에러 디코더
 * 
 * <p>Elasticsearch API 호출 시 발생하는 HTTP 에러를 적절한 비즈니스 예외로 변환합니다.
 * HTTP 상태 코드에 따라 적절한 비즈니스 예외를 생성하며, 응답 메시지를 파싱하여 상세한 오류 정보를 제공합니다.</p>
 * 
 * <h3>HTTP 상태 코드별 매핑:</h3>
 * <ul>
 *   <li><strong>400</strong>: ValidationException - 잘못된 쿼리 문법</li>
 *   <li><strong>401</strong>: BusinessException(UNAUTHORIZED) - 인증 실패</li>
 *   <li><strong>403</strong>: BusinessException(FORBIDDEN) - 권한 부족</li>
 *   <li><strong>404</strong>: BusinessException(RESOURCE_NOT_FOUND) - 인덱스/문서 없음</li>
 *   <li><strong>422</strong>: BusinessException(VALIDATION_ERROR) - 데이터베이스 오류 특별 처리</li>
 *   <li><strong>429</strong>: BusinessException(EXTERNAL_SERVICE_ERROR) - 요청 한도 초과</li>
 *   <li><strong>500</strong>: BusinessException(EXTERNAL_SERVICE_ERROR) - 서버 오류</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 2.0 - 상세한 에러 메시지 파싱 추가, 422 오류 특별 처리
 */
@Slf4j
public class ElasticErrorDecoder implements ErrorDecoder {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        String errorDetail = extractErrorDetail(response);
        
        log.error("🟡 [ELASTIC] API 호출 실패: {} - Status: {}, URL: {}, Detail: {}", 
                methodKey, status, requestUrl, errorDetail);

        return switch (status) {
            case 400 -> {
                yield new ValidationException(ErrorCode.INVALID_INPUT_VALUE, 
                        "Elasticsearch 쿼리 문법 오류" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 401 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_UNAUTHORIZED, 
                        "Elasticsearch 인증 실패" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 403 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_FORBIDDEN, 
                        "Elasticsearch 접근 권한 없음" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 404 -> {
                
                // 404 에러는 기본 FeignException을 반환 (호출 측에서 상태 코드 확인하여 처리)
                log.debug("Elasticsearch 인덱스/문서를 찾을 수 없음 (404) - URL: {}, Detail: {} - FeignException 반환", 
                        requestUrl, errorDetail);
                yield feign.FeignException.errorStatus(methodKey, response);
            }
            case 422 -> {
                // 422 오류의 경우 서버측 데이터베이스 오류일 가능성이 높으므로 더 구체적인 메시지 제공
                String message = errorDetail != null && errorDetail.contains("Database error") 
                    ? "Elasticsearch 서버 데이터베이스 오류: " + errorDetail
                    : "Elasticsearch 유효성 검증 실패" + (errorDetail != null ? ": " + errorDetail : "");
                yield new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, message);
            }
            case 429 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "Elasticsearch 요청 한도 초과" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            case 500 -> {
                yield new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR, 
                        "Elasticsearch 서버 내부 오류" + (errorDetail != null ? ": " + errorDetail : ""));
            }
            default -> {
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        String.format("Elasticsearch API 호출 실패 (Status: %d)", status) + 
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
            
            // Elasticsearch 전용 "error" 필드 구조 처리
            if (jsonNode.has("error")) {
                JsonNode errorNode = jsonNode.get("error");
                
                // Elasticsearch error 객체의 reason 필드
                if (errorNode.has("reason")) {
                    return errorNode.get("reason").asText();
                }
                
                // 일반적인 error 메시지
                if (errorNode.isTextual()) {
                    return errorNode.asText();
                } else if (errorNode.has("message")) {
                    return errorNode.get("message").asText();
                }
            }
            
            // "detail" 필드 추출
            if (jsonNode.has("detail")) {
                return jsonNode.get("detail").asText();
            }
            
            // "message" 필드 추출 (대안)
            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }
            
            // JSON이지만 알려진 필드가 없는 경우 전체 응답 반환 (간략화)
            return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
            
        } catch (IOException e) {
            log.warn("Elasticsearch API 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Elasticsearch API 예상치 못한 오류로 응답 바디 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}