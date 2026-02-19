package com.skax.aiplatform.common.exception;

import com.skax.aiplatform.common.response.AxResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 * 
 * <p>애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리합니다.
 * 표준화된 에러 응답을 제공하고 적절한 로깅을 수행합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리 - 커스텀 메시지 detail 지원
     * 
     * @param ex CustomException
     * @return 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public AxResponseEntity<Void> handleCustomException(CustomException ex) {
        log.warn("Custom exception occurred: {}", ex.getMessage(), ex);
        
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        
        // 커스텀 메시지가 ErrorCode의 기본 메시지와 다른 경우, 커스텀 메시지를 detail로 설정
        if (message != null && !message.equals(errorCode.getMessage())) {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), message, ex.getActionType());
        } else {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), null, ex.getActionType());
        }
    }

    /**
     * 비즈니스 예외 처리 - 커스텀 메시지 detail 지원
     * 
     * @param ex BusinessException
     * @return 에러 응답
     */
    @ExceptionHandler(BusinessException.class)
    public AxResponseEntity<Void> handleBusinessException(BusinessException ex) {
        log.warn("Business exception occurred: {}", ex.getMessage(), ex);
        
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        
        // 커스텀 메시지가 ErrorCode의 기본 메시지와 다른 경우, 커스텀 메시지를 detail로 설정
        if (message != null && !message.equals(errorCode.getMessage())) {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), message, ex.getActionType());
        } else {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), null, ex.getActionType());
        }
    }

    /**
     * 검증 예외 처리 - 커스텀 메시지 detail 지원
     * 
     * @param ex ValidationException
     * @return 에러 응답
     */
    @ExceptionHandler(ValidationException.class)
    public AxResponseEntity<Void> handleValidationException(ValidationException ex) {
        log.warn("Validation exception occurred: {}", ex.getMessage(), ex);
        
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        
        // 커스텀 메시지가 ErrorCode의 기본 메시지와 다른 경우, 커스텀 메시지를 detail로 설정
        if (message != null && !message.equals(errorCode.getMessage())) {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), message, ex.getActionType());
        } else {
            return AxResponseEntity.error(errorCode, errorCode.getMessage(), null, ex.getActionType());
        }
    }

    /**
     * Bean Validation 예외 처리 (@Valid 검증 실패)
     * 
     * @param ex MethodArgumentNotValidException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AxResponseEntity<Void> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Method argument validation failed: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return AxResponseEntity.badRequest(errorMessage.isEmpty() ? "입력값 검증에 실패했습니다" : errorMessage);
    }

    /**
     * Bean Validation 예외 처리 (Bind 오류)
     * 
     * @param ex BindException
     * @return 에러 응답
     */
    @ExceptionHandler(BindException.class)
    public AxResponseEntity<Void> handleBindException(BindException ex) {
        log.warn("Bind exception occurred: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return AxResponseEntity.badRequest(errorMessage.isEmpty() ? "입력값 바인딩에 실패했습니다" : errorMessage);
    }

    /**
     * Constraint Validation 예외 처리
     * 
     * @param ex ConstraintViolationException
     * @return 에러 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public AxResponseEntity<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation occurred: {}", ex.getMessage());
        
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        return AxResponseEntity.badRequest(errorMessage);
    }

    /**
     * Handler Method Validation 예외 처리 (Spring Boot 3.1+)
     * 
     * <p>@Valid 어노테이션이 메서드 파라미터에 적용된 경우 발생하는 검증 예외를 처리합니다.</p>
     * 
     * @param ex HandlerMethodValidationException
     * @return 에러 응답
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public AxResponseEntity<Void> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        log.warn("Handler method validation failed: {}", ex.getMessage());
        
        // Spring 6.2+ 호환: getAllErrors() 사용
        String errorMessage = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : error.toString())
                .collect(Collectors.joining(", "));
        
        // 에러 메시지가 없는 경우 파라미터 이름 기반 메시지 생성
        if (errorMessage.isEmpty()) {
            errorMessage = "입력값 검증에 실패했습니다";
        }
        
        return AxResponseEntity.badRequest(errorMessage);
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리
     * 
     * @param ex MethodArgumentTypeMismatchException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AxResponseEntity<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch exception occurred: {}", ex.getMessage());
        
        String errorMessage = String.format("잘못된 파라미터 타입입니다: %s", ex.getName());
        return AxResponseEntity.badRequest(errorMessage);
    }

    /**
     * 필수 요청 파라미터 누락 예외 처리
     * 
     * @param ex MissingServletRequestParameterException
     * @return 에러 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AxResponseEntity<Void> handleMissingParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        
        String errorMessage = String.format("필수 파라미터가 누락되었습니다: %s", ex.getParameterName());
        return AxResponseEntity.badRequest(errorMessage);
    }

    /**
     * HTTP 메서드 지원하지 않음 예외 처리
     * 
     * @param ex HttpRequestMethodNotSupportedException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AxResponseEntity<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        
        String supportedMethods = ex.getSupportedMethods() != null 
                ? String.join(", ", ex.getSupportedMethods()) 
                : "없음";
        String errorMessage = String.format("지원하지 않는 HTTP 메서드입니다. 지원 메서드: %s", supportedMethods);
        
        return AxResponseEntity.statusBuilder(405).body(errorMessage);
    }

    /**
     * 지원하지 않는 미디어 타입 예외 처리 (OAuth2 form-data vs JSON)
     * 
     * @param ex HttpMediaTypeNotSupportedException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public AxResponseEntity<Void> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("Media type not supported: {}", ex.getMessage());
        
        String supportedTypes = ex.getSupportedMediaTypes().stream()
                .map(mediaType -> mediaType.toString())
                .collect(Collectors.joining(", "));
        
        String errorMessage = String.format("지원하지 않는 Content-Type입니다. OAuth2 로그인은 form-data, 일반 로그인은 JSON을 사용하세요. 지원 형식: %s", supportedTypes);
        
        return AxResponseEntity.badRequest(errorMessage);
    }

    /**
     * JSON 파싱 오류 예외 처리
     * 
     * @param ex HttpMessageNotReadableException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AxResponseEntity<Void> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("JSON parsing error: {}", ex.getMessage());
        
        return AxResponseEntity.badRequest("잘못된 JSON 형식입니다");
    }

    /**
     * 기타 모든 예외 처리
     * 
     * @param ex Exception
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public AxResponseEntity<Void> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        return AxResponseEntity.internalServerError("서버 내부 오류가 발생했습니다");
    }
}
