package com.skax.aiplatform.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.skax.aiplatform.common.exception.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 표준화된 API 응답 래퍼 클래스
 * 
 * <p>모든 API 응답을 일관된 형식으로 제공하기 위한 래퍼 클래스입니다.
 * 성공/실패 여부, 메시지, 데이터, 에러 정보, 타임스탬프, 요청 경로를 포함합니다.</p>
 * 
 * @param <T> 응답 데이터 타입
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 2.0.0
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 응답 래퍼")
public class AxResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 메시지", example = "조회 성공")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    @Schema(description = "에러 정보 (실패 시)")
    private final ErrorInfo error;

    @Schema(description = "응답 시간", example = "2025-08-09T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/users")
    private final String path;

    /**
     * 에러 정보 클래스
     */
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "에러 정보")
    public static class ErrorInfo {
        @Schema(description = "HTTP 상태 기반 에러 코드", example = "NOT_FOUND")
        private final String hscode;

        @Schema(description = "ErrorCode 기반 구체적인 에러 코드", example = "U001")
        private final String code;

        @Schema(description = "에러 메시지", example = "사용자를 찾을 수 없습니다")
        private final String message;

        @Schema(description = "에러 상세 정보", example = "ID 999에 해당하는 사용자가 존재하지 않습니다")
        private final String details;

        @Schema(description = "에러 발생 시간", example = "2025-08-15T08:07:48")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime timestamp;

        @Schema(description = "에러 발생 경로", example = "/api/users/999")
        private final String path;

        @Schema(description = "에러 발생 처리 액션 타입")
        private final ActionType actionType;

        @Schema(description = "필드 검증 에러 목록")
        private final List<FieldError> fieldErrors;
    }

    /**
     * 필드 검증 에러 클래스
     */
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "필드 검증 에러")
    public static class FieldError {
        @Schema(description = "필드명", example = "email")
        private final String field;

        @Schema(description = "거부된 값", example = "invalid-email")
        private final Object rejectedValue;

        @Schema(description = "에러 메시지", example = "올바른 이메일 형식이 아닙니다")
        private final String message;
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     * 
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> AxResponse<T> success(T data, String message) {
        return AxResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .path(getCurrentRequestPath())
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 포함, 기본 메시지)
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> AxResponse<T> success(T data) {
        return success(data, "성공");
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     * 
     * @param message 성공 메시지
     * @return 성공 응답
     */
    public static AxResponse<Void> success(String message) {
        return AxResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(getCurrentRequestPath())
                .build();
    }

    /**
     * 성공 응답 생성 (기본 메시지)
     * 
     * @return 성공 응답
     */
    public static AxResponse<Void> success() {
        return success("성공");
    }

    /**
     * 경고 응답 생성 (성공 응답과 동일하지만 success = false)
     * 
     * @param data 응답 데이터
     * @param message 경고 메시지
     * @param <T> 데이터 타입
     * @return 경고 응답
     */
    public static <T> AxResponse<T> warning(T data, String message) {
        return AxResponse.<T>builder()
                .success(false)  // 경고는 success를 false로 설정
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .path(getCurrentRequestPath())
                .build();
    }

    /**
     * 경고 응답 생성 (데이터 포함, 기본 메시지)
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 경고 응답
     */
    public static <T> AxResponse<T> warning(T data) {
        return warning(data, "경고");
    }

    /**
     * 경고 응답 생성 (데이터 없음)
     * 
     * @param message 경고 메시지
     * @return 경고 응답
     */
    public static AxResponse<Void> warning(String message) {
        return AxResponse.<Void>builder()
                .success(false)  // 경고는 success를 false로 설정
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(getCurrentRequestPath())
                .build();
    }

    /**
     * 경고 응답 생성 (기본 메시지)
     * 
     * @return 경고 응답
     */
    public static AxResponse<Void> warning() {
        return warning("경고");
    }

    /**
     * 실패 응답 생성 (완전한 확장 형태 - statusCode, statusText, details, actionType 포함)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드 (예: "NOT_FOUND")
     * @param code ErrorCode 기반 구체적인 코드 (예: "U001")
     * @param statusCode HTTP 상태 코드 (예: 404)
     * @param statusText HTTP 상태 텍스트 (예: "Not Found")
     * @param details 에러 상세 정보
     * @param actionType 에러 발생 처리 액션 타입
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message, String hscode, String code, 
                                         Integer statusCode, String statusText, 
                                         String details, ActionType actionType) {
        String currentPath = getCurrentRequestPath();
        LocalDateTime now = LocalDateTime.now();
        
        ErrorInfo errorInfo = ErrorInfo.builder()
                .hscode(hscode)
                .code(code)
                .message(message)
                .details(details)
                .timestamp(now)
                .path(currentPath)
                .actionType(actionType)
                .build();
        
        return AxResponse.<Void>builder()
                .success(false)
                .message(message)
                .error(errorInfo)
                .timestamp(now)
                .path(currentPath)
                .build();
    }

    /**
     * 실패 응답 생성 (actionType 없이)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @param statusCode HTTP 상태 코드
     * @param statusText HTTP 상태 텍스트
     * @param details 에러 상세 정보
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message, String hscode, String code, 
                                         Integer statusCode, String statusText, String details) {
        return failure(message, hscode, code, statusCode, statusText, details, ActionType.CONFIRM);
    }

    /**
     * 실패 응답 생성 (details, actionType 없이)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @param statusCode HTTP 상태 코드
     * @param statusText HTTP 상태 텍스트
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message, String hscode, String code, 
                                         Integer statusCode, String statusText) {
        return failure(message, hscode, code, statusCode, statusText, null, ActionType.CONFIRM);
    }

    /**
     * 실패 응답 생성 (기본 구조 - hscode + code만 포함, 향상된 버전)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @return 실패 응답
     */
    public static AxResponse<Void> failureWithCode(String message, String hscode, String code) {
        return failure(message, hscode, code, null, null, null, ActionType.CONFIRM);
    }

    /**
     * 실패 응답 생성 (hscode + code 모두 포함) - 기존 호환성 유지
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드 (예: "NOT_FOUND")
     * @param code ErrorCode 기반 구체적인 코드 (예: "U001")
     * @param errorDetails 에러 상세 정보
     * @return 실패 응답
     */
    public static AxResponse<Void> failureCompat(String message, String hscode, String code, String errorDetails) {
        String currentPath = getCurrentRequestPath();
        LocalDateTime now = LocalDateTime.now();
        
        ErrorInfo errorInfo = ErrorInfo.builder()
                .hscode(hscode)
                .code(code)
                .message(message)
                .details(errorDetails)
                .timestamp(now)
                .path(currentPath)
                .actionType(ActionType.CONFIRM)
                .build();
        
        return AxResponse.<Void>builder()
                .success(false)
                .message(message)
                .error(errorInfo)
                .timestamp(now)
                .path(currentPath)
                .build();
    }

    /**
     * 실패 응답 생성 (에러 정보 포함) - 기존 방식 호환
     * 
     * @param message 실패 메시지
     * @param errorCode 에러 코드 (hscode로 사용)
     * @param errorDetails 에러 상세 정보
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message, String errorCode, String errorDetails) {
        return failureCompat(message, errorCode, null, errorDetails);
    }

    /**
     * 실패 응답 생성 (기본 에러 코드)
     * 
     * @param message 실패 메시지
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message) {
        return failure(message, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다");
    }

    /**
     * 실패 응답 생성 (필드 검증 에러 포함, ActionType 지원)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @param fieldErrors 필드 검증 에러 목록
     * @param actionType 에러 발생 처리 액션 타입
     * @return 실패 응답
     */
    public static AxResponse<Void> validationFailure(String message, String hscode, String code, 
                                                   List<FieldError> fieldErrors, ActionType actionType) {
        String currentPath = getCurrentRequestPath();
        LocalDateTime now = LocalDateTime.now();
        
        ErrorInfo errorInfo = ErrorInfo.builder()
                .hscode(hscode)
                .code(code)
                .message(message)
                .details("입력값 검증에 실패했습니다")
                .timestamp(now)
                .path(currentPath)
                .fieldErrors(fieldErrors)
                .actionType(actionType)
                .build();
        
        return AxResponse.<Void>builder()
                .success(false)
                .message(message)
                .error(errorInfo)
                .timestamp(now)
                .path(currentPath)
                .build();
    }

    /**
     * 실패 응답 생성 (필드 검증 에러 포함)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @param fieldErrors 필드 검증 에러 목록
     * @return 실패 응답
     */
    public static AxResponse<Void> validationFailure(String message, String hscode, String code, List<FieldError> fieldErrors) {
        return validationFailure(message, hscode, code, fieldErrors, ActionType.CONFIRM);
    }

    /**
     * 실패 응답 생성 (필드 검증 에러 포함) - 기존 방식 호환
     * 
     * @param message 실패 메시지
     * @param errorCode 에러 코드
     * @param fieldErrors 필드 검증 에러 목록
     * @return 실패 응답
     */
    public static AxResponse<Void> failure(String message, String errorCode, List<FieldError> fieldErrors) {
        return validationFailure(message, errorCode, null, fieldErrors);
    }

    /**
     * 현재 요청 경로 조회
     * 
     * @return 현재 요청 경로
     */
    private static String getCurrentRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (IllegalStateException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
