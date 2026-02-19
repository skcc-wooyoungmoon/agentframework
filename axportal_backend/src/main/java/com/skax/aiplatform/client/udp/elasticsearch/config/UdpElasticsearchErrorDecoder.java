package com.skax.aiplatform.client.udp.elasticsearch.config;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.exception.ValidationException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * UDP Elasticsearch API 에러 디코더
 * 
 * <p>UDP Elasticsearch API 호출 시 발생하는 HTTP 에러를 적절한 비즈니스 예외로 변환합니다.</p>
 * 
 * <h3>HTTP 상태 코드별 매핑:</h3>
 * <ul>
 *   <li><strong>400</strong>: ValidationException - 잘못된 쿼리 문법</li>
 *   <li><strong>401</strong>: BusinessException(UNAUTHORIZED) - 인증 실패</li>
 *   <li><strong>403</strong>: BusinessException(FORBIDDEN) - 권한 부족</li>
 *   <li><strong>404</strong>: BusinessException(RESOURCE_NOT_FOUND) - 인덱스/문서 없음</li>
 *   <li><strong>429</strong>: BusinessException(EXTERNAL_SERVICE_ERROR) - 요청 한도 초과</li>
 *   <li><strong>500</strong>: BusinessException(EXTERNAL_SERVICE_ERROR) - 서버 오류</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Slf4j
public class UdpElasticsearchErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        // 응답 본문 읽기
        String responseBody = "";
        try {
            if (response.body() != null) {
                responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
                log.error("🔴 [UDP Elasticsearch] API 에러 발생\n  메서드: {}\n  상태코드: {}\n  응답 본문:\n{}", 
                          methodKey, response.status(), responseBody);
            } else {
                log.error("UDP Elasticsearch API 에러 발생 - 메서드: {}, 상태코드: {} (응답 본문 없음)", 
                          methodKey, response.status());
            }
        } catch (IOException e) {
            log.error("UDP Elasticsearch API 에러 응답 본문 읽기 실패 - 메서드: {}, 상태코드: {}", 
                      methodKey, response.status(), e);
        }

        return switch (response.status()) {
            case 400 -> {
                log.error("UDP Elasticsearch API 잘못된 요청 - 메서드: {}", methodKey);
                yield new ValidationException(ErrorCode.INVALID_INPUT_VALUE, 
                        "Elasticsearch 쿼리 문법이 올바르지 않습니다");
            }
            case 401 -> {
                log.error("UDP Elasticsearch API 인증 실패 - 메서드: {}", methodKey);
                yield new BusinessException(ErrorCode.UNAUTHORIZED, 
                        "Elasticsearch 인증에 실패했습니다");
            }
            case 403 -> {
                log.error("UDP Elasticsearch API 권한 없음 - 메서드: {}", methodKey);
                yield new BusinessException(ErrorCode.FORBIDDEN, 
                        "Elasticsearch 접근 권한이 없습니다");
            }
            case 404 -> {
                log.error("UDP Elasticsearch API 리소스 없음 - 메서드: {}", methodKey);
                yield new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "Elasticsearch 인덱스 또는 문서를 찾을 수 없습니다");
            }
            case 429 -> {
                log.error("UDP Elasticsearch API 요청 한도 초과 - 메서드: {}", methodKey);
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "Elasticsearch 요청 한도를 초과했습니다");
            }
            case 500 -> {
                log.error("UDP Elasticsearch API 서버 오류 - 메서드: {}", methodKey);
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "Elasticsearch 서버에서 오류가 발생했습니다");
            }
            default -> {
                log.error("UDP Elasticsearch API 알 수 없는 오류 - 메서드: {}, 상태코드: {}", 
                          methodKey, response.status());
                yield new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                        "Elasticsearch API 호출 중 알 수 없는 오류가 발생했습니다");
            }
        };
    }
}

