package com.skax.aiplatform.common.response;

import com.skax.aiplatform.common.exception.ActionType;
import com.skax.aiplatform.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * ResponseEntity를 상속한 통합 응답 클래스
 * 
 * <p>HTTP 상태 코드와 표준화된 응답 형식을 함께 제공하는 통합 응답 클래스입니다.
 * ResponseEntity를 상속하여 더 자연스럽게 사용할 수 있습니다.</p>
 * 
 * @param <T> 응답 데이터 타입
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 1.0.0
 */
@Schema(description = "통합 API 응답")
public class AxResponseEntity<T> extends ResponseEntity<AxResponse<T>> {

    /**
     * 생성자
     * 
     * @param body 응답 본문
     * @param status HTTP 상태
     */
    public AxResponseEntity(AxResponse<T> body, HttpStatus status) {
        super(body, status);
    }

    /**
     * 생성자 (헤더 포함)
     * 
     * @param body 응답 본문
     * @param headers HTTP 헤더
     * @param status HTTP 상태
     */
    public AxResponseEntity(AxResponse<T> body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }

    // ==================== 성공 응답 팩토리 메서드 ====================

    /**
     * 성공 응답 생성 (데이터 포함) - 200 OK
     * 
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> AxResponseEntity<T> ok(T data, String message) {
        return new AxResponseEntity<T>(createSuccess(data, message, HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * 성공 응답 생성 (데이터 포함, 기본 메시지) - 200 OK
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> AxResponseEntity<T> success(T data) {
        return ok(data, "조회 성공");
    }

    /**
     * 성공 응답 생성 (데이터 없음) - 200 OK
     * 
     * @param message 성공 메시지
     * @return 성공 응답
     */
    public static AxResponseEntity<Void> success(String message) {
        return new AxResponseEntity<Void>(createSuccess(null, message, HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * 성공 응답 생성 (기본) - 200 OK
     * 
     * @return 성공 응답
     */
    public static AxResponseEntity<Void> success() {
        return success("성공");
    }

    /**
     * 생성 성공 응답 - 201 CREATED
     * 
     * @param data 생성된 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 생성 성공 응답
     */
    public static <T> AxResponseEntity<T> created(T data, String message) {
        return new AxResponseEntity<T>(createSuccess(data, message, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    /**
     * 생성 성공 응답 (기본 메시지) - 201 CREATED
     * 
     * @param data 생성된 데이터
     * @param <T> 데이터 타입
     * @return 생성 성공 응답
     */
    public static <T> AxResponseEntity<T> created(T data) {
        return created(data, "생성 성공");
    }

    /**
     * 수정 성공 응답 - 200 OK
     * 
     * @param data 수정된 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 수정 성공 응답
     */
    public static <T> AxResponseEntity<T> updated(T data, String message) {
        return ok(data, message);
    }

    /**
     * 수정 성공 응답 (기본 메시지) - 200 OK
     * 
     * @param data 수정된 데이터
     * @param <T> 데이터 타입
     * @return 수정 성공 응답
     */
    public static <T> AxResponseEntity<T> updated(T data) {
        return updated(data, "수정 성공");
    }

    /**
     * 삭제 성공 응답 - 204 NO CONTENT
     *
     * @param message 성공 메시지
     * @return 삭제 성공 응답
     */
    public static <T> AxResponseEntity<T> deleted(T data) {
        return deleted(data, "삭제 성공");
    }
    public static <T> AxResponseEntity<T> deleted(T data, String message) {
        return new AxResponseEntity<T>(createSuccess(data, message, HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
    }

    /**
     * 삭제 성공 응답 - 204 NO CONTENT
     * 
     * @param message 성공 메시지
     * @return 삭제 성공 응답
     */
    public static AxResponseEntity<Void> deleted(String message) {
        return new AxResponseEntity<Void>(createSuccess(null, message, HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
    }

    /**
     * 삭제 성공 응답 (기본 메시지) - 204 NO CONTENT
     * 
     * @return 삭제 성공 응답
     */
    public static AxResponseEntity<Void> deleted() {
        return deleted("삭제 성공");
    }

    // ==================== 실패 응답 팩토리 메서드 ====================
    
    /**
     * ErrorCode를 사용한 실패 응답 생성 (권장)
     * 
     * @param errorCode ErrorCode 객체
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> AxResponseEntity<T> error(ErrorCode errorCode) {
        AxResponse<T> response = createFailureWithErrorCode(errorCode.getMessage(), errorCode);
        return new AxResponseEntity<T>(response, errorCode.getStatus());
    }

    /**
     * ErrorCode를 사용한 실패 응답 생성 (커스텀 메시지)
     * 
     * @param errorCode ErrorCode 객체
     * @param customMessage 커스텀 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> AxResponseEntity<T> error(ErrorCode errorCode, String customMessage) {
        AxResponse<T> response = createFailureWithErrorCode(customMessage, errorCode);
        return new AxResponseEntity<T>(response, errorCode.getStatus());
    }

    /**
     * ErrorCode를 사용한 실패 응답 생성 (커스텀 메시지와 상세 정보)
     * 
     * @param errorCode ErrorCode 객체
     * @param customMessage 커스텀 메시지
     * @param details 상세 정보
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> AxResponseEntity<T> error(ErrorCode errorCode, String customMessage, String details) {
        AxResponse<T> response = createFailureWithErrorCodeAndDetails(customMessage, errorCode, details);
        return new AxResponseEntity<T>(response, errorCode.getStatus());
    }

    /**
     * ErrorCode를 사용한 실패 응답 생성 (커스텀 메시지, 상세 정보, ActionType 포함)
     * 
     * @param errorCode ErrorCode 객체
     * @param customMessage 커스텀 메시지
     * @param details 상세 정보
     * @param actionType 에러 발생 처리 액션 타입
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> AxResponseEntity<T> error(ErrorCode errorCode, String customMessage, String details, ActionType actionType) {
        AxResponse<T> response = createFailureWithErrorCodeDetailsAndAction(customMessage, errorCode, details, actionType);
        return new AxResponseEntity<T>(response, errorCode.getStatus());
    }

    /**
     * 확장된 실패 응답 생성 (모든 파라미터 포함)
     * 
     * @param message 실패 메시지
     * @param hscode HTTP 상태 기반 코드
     * @param code ErrorCode 기반 구체적인 코드
     * @param statusCode HTTP 상태 코드
     * @param statusText HTTP 상태 텍스트
     * @param details 에러 상세 정보
     * @param actionType 에러 발생 처리 액션 타입
     * @param status HTTP 상태
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    @SuppressWarnings("unchecked")
    public static <T> AxResponseEntity<T> failure(String message, String hscode, String code, 
                                                 Integer statusCode, String statusText, 
                                                 String details, ActionType actionType, 
                                                 HttpStatus status) {
        AxResponse<Void> voidResponse = AxResponse.failure(message, hscode, code, statusCode, statusText, details, actionType);
        AxResponse<T> response = (AxResponse<T>) voidResponse;
        return new AxResponseEntity<T>(response, status);
    }

    /**
     * HTTP 상태 지정 응답
     * 
     * @param status HTTP 상태
     * @return StatusBuilder
     */
    public static StatusBuilder statusBuilder(HttpStatus status) {
        return new StatusBuilder(status);
    }

    /**
     * HTTP 상태 지정 응답
     * 
     * @param status HTTP 상태 코드
     * @return StatusBuilder
     */
    public static StatusBuilder statusBuilder(int status) {
        return new StatusBuilder(HttpStatus.valueOf(status));
    }

    /**
     * StatusBuilder 클래스
     */
    public static class StatusBuilder {
        private final HttpStatus status;

        public StatusBuilder(HttpStatus status) {
            this.status = status;
        }

        public AxResponseEntity<Void> body(String message) {
            return new AxResponseEntity<Void>(createFailure(message, status), status);
        }
    }

    /**
     * 잘못된 요청 - 400 BAD REQUEST
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @return 잘못된 요청 응답
     */
    public static AxResponseEntity<Void> badRequest(String message, String errorCode) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    /**
     * 잘못된 요청 (ErrorCode 사용) - 400 BAD REQUEST
     * 
     * @param errorCode ErrorCode 객체
     * @return 잘못된 요청 응답
     */
    public static AxResponseEntity<Void> badRequest(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 잘못된 요청 (메시지만) - 400 BAD REQUEST
     * 
     * @param message 오류 메시지
     * @return 잘못된 요청 응답
     */
    public static AxResponseEntity<Void> badRequest(String message) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    /**
     * 인증 실패 - 401 UNAUTHORIZED
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @return 인증 실패 응답
     */
    public static AxResponseEntity<Void> unauthorized(String message, String errorCode) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    /**
     * 인증 실패 (ErrorCode 사용) - 401 UNAUTHORIZED
     * 
     * @param errorCode ErrorCode 객체
     * @return 인증 실패 응답
     */
    public static AxResponseEntity<Void> unauthorized(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 인증 실패 (기본 메시지) - 401 UNAUTHORIZED
     * 
     * @param errorCode 오류 코드
     * @return 인증 실패 응답
     */
    public static AxResponseEntity<Void> unauthorized(String errorCode) {
        return unauthorized("인증이 필요합니다", errorCode);
    }

    /**
     * 권한 없음 - 403 FORBIDDEN
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @return 권한 없음 응답
     */
    public static AxResponseEntity<Void> forbidden(String message, String errorCode) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    /**
     * 권한 없음 (ErrorCode 사용) - 403 FORBIDDEN
     * 
     * @param errorCode ErrorCode 객체
     * @return 권한 없음 응답
     */
    public static AxResponseEntity<Void> forbidden(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 권한 없음 (기본 메시지) - 403 FORBIDDEN
     * 
     * @param errorCode 오류 코드
     * @return 권한 없음 응답
     */
    public static AxResponseEntity<Void> forbidden(String errorCode) {
        return forbidden("접근 권한이 없습니다", errorCode);
    }

    /**
     * 리소스 없음 - 404 NOT FOUND
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @param <T> 데이터 타입
     * @return 리소스 없음 응답
     */
    public static <T> AxResponseEntity<T> notFound(String message, String errorCode) {
        return new AxResponseEntity<T>(createFailure(message, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    /**
     * 리소스 없음 (ErrorCode 사용) - 404 NOT FOUND
     * 
     * @param errorCode ErrorCode 객체
     * @param <T> 데이터 타입
     * @return 리소스 없음 응답
     */
    public static <T> AxResponseEntity<T> notFound(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 리소스 없음 (기본 메시지) - 404 NOT FOUND
     * 
     * @param errorCode 오류 코드
     * @param <T> 데이터 타입
     * @return 리소스 없음 응답
     */
    public static <T> AxResponseEntity<T> notFound(String errorCode) {
        return notFound("요청한 리소스를 찾을 수 없습니다", errorCode);
    }

    /**
     * 충돌 - 409 CONFLICT
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @param <T> 데이터 타입
     * @return 충돌 응답
     */
    public static <T> AxResponseEntity<T> conflict(String message, String errorCode) {
        return new AxResponseEntity<T>(createFailure(message, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    /**
     * 충돌 (ErrorCode 사용) - 409 CONFLICT
     * 
     * @param errorCode ErrorCode 객체
     * @param <T> 데이터 타입
     * @return 충돌 응답
     */
    public static <T> AxResponseEntity<T> conflict(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 충돌 (기본 메시지) - 409 CONFLICT
     * 
     * @param errorCode 오류 코드
     * @param <T> 데이터 타입
     * @return 충돌 응답
     */
    public static <T> AxResponseEntity<T> conflict(String errorCode) {
        return conflict("리소스 충돌이 발생했습니다", errorCode);
    }

    /**
     * 서버 오류 - 500 INTERNAL SERVER ERROR
     * 
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @return 서버 오류 응답
     */
    public static AxResponseEntity<Void> internalServerError(String message, String errorCode) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 서버 오류 (ErrorCode 사용) - 500 INTERNAL SERVER ERROR
     * 
     * @param errorCode ErrorCode 객체
     * @return 서버 오류 응답
     */
    public static AxResponseEntity<Void> internalServerError(ErrorCode errorCode) {
        return error(errorCode);
    }

    /**
     * 서버 오류 (메시지만) - 500 INTERNAL SERVER ERROR
     * 
     * @param message 오류 메시지
     * @return 서버 오류 응답
     */
    public static AxResponseEntity<Void> internalServerError(String message) {
        return new AxResponseEntity<Void>(createFailure(message, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 유효성 검사 실패 (ErrorCode 사용) - 400 BAD REQUEST
     * 
     * @param errorCode ErrorCode 객체
     * @param fieldErrors 필드별 에러 목록
     * @return 유효성 검사 실패 응답
     */
    public static AxResponseEntity<Void> validationFailure(ErrorCode errorCode, List<AxResponse.FieldError> fieldErrors) {
        AxResponse<Void> response = AxResponse.validationFailure(
            errorCode.getMessage(),
            errorCode.getStatus().name(),
            errorCode.getCode(),
            fieldErrors
        );
        return new AxResponseEntity<Void>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 유효성 검사 실패 (기본) - 400 BAD REQUEST
     * 
     * @param fieldErrors 필드별 에러 목록
     * @return 유효성 검사 실패 응답
     */
    public static AxResponseEntity<Void> validationFailure(List<AxResponse.FieldError> fieldErrors) {
        AxResponse<Void> response = AxResponse.validationFailure(
            "입력값 검증에 실패했습니다",
            "BAD_REQUEST",
            "V001",
            fieldErrors
        );
        return new AxResponseEntity<Void>(response, HttpStatus.BAD_REQUEST);
    }

    // ==================== 페이징 응답 팩토리 메서드 ====================

    /**
     * 페이징 조회 성공 응답 (PageResponse 사용) - 200 OK
     * 
     * @param pageResponse 페이징 응답 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 페이징 성공 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> okPage(PageResponse<T> pageResponse, String message) {
        return new AxResponseEntity<PageResponse<T>>(createSuccess(pageResponse, message, HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * 페이징 조회 성공 응답 (PageResponse 사용, 기본 메시지) - 200 OK
     * 
     * @param pageResponse 페이징 응답 데이터
     * @param <T> 데이터 타입
     * @return 페이징 성공 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> okPage(PageResponse<T> pageResponse) {
        return okPage(pageResponse, "목록 조회 성공");
    }

    /**
     * 페이징 조회 성공 응답 (Page를 PageResponse로 변환) - 200 OK
     * 
     * @param page Spring Data Page 객체
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 페이징 성공 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> okPage(Page<T> page, String message) {
        PageResponse<T> pageResponse = PageResponse.from(page);
        return okPage(pageResponse, message);
    }

    /**
     * 페이징 조회 성공 응답 (Page를 PageResponse로 변환, 기본 메시지) - 200 OK
     * 
     * @param page Spring Data Page 객체
     * @param <T> 데이터 타입
     * @return 페이징 성공 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> okPage(Page<T> page) {
        return okPage(page, "목록 조회 성공");
    }

    // ==================== 경고 응답 팩토리 메서드 ====================

    /**
     * 경고 응답 생성 (데이터 포함) - 200 OK
     * 성공 응답과 동일하지만 success를 false로 설정하여 경고 상황을 표현
     * 
     * @param data 응답 데이터
     * @param message 경고 메시지
     * @param <T> 데이터 타입
     * @return 경고 응답
     */
    public static <T> AxResponseEntity<T> warning(T data, String message) {
        AxResponse<T> response = AxResponse.warning(data, message);
        return new AxResponseEntity<T>(response, HttpStatus.OK);
    }

    /**
     * 경고 응답 생성 (데이터 포함, 기본 메시지) - 200 OK
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 경고 응답
     */
    public static <T> AxResponseEntity<T> warning(T data) {
        return warning(data, "경고");
    }

    /**
     * 경고 응답 생성 (데이터 없음) - 200 OK
     * 
     * @param message 경고 메시지
     * @return 경고 응답
     */
    public static AxResponseEntity<Void> warning(String message) {
        AxResponse<Void> response = AxResponse.warning(null, message);
        return new AxResponseEntity<Void>(response, HttpStatus.OK);
    }

    /**
     * 페이징 경고 응답 (PageResponse 사용) - 200 OK
     * 성공 응답과 동일하지만 success를 false로 설정하여 경고 상황을 표현
     * 
     * @param pageResponse 페이징 응답 데이터
     * @param message 경고 메시지
     * @param <T> 데이터 타입
     * @return 페이징 경고 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> warningPage(PageResponse<T> pageResponse, String message) {
        AxResponse<PageResponse<T>> response = AxResponse.warning(pageResponse, message);
        return new AxResponseEntity<PageResponse<T>>(response, HttpStatus.OK);
    }

    /**
     * 페이징 경고 응답 (PageResponse 사용, 기본 메시지) - 200 OK
     * 
     * @param pageResponse 페이징 응답 데이터
     * @param <T> 데이터 타입
     * @return 페이징 경고 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> warningPage(PageResponse<T> pageResponse) {
        return warningPage(pageResponse, "경고");
    }

    /**
     * 페이징 경고 응답 (Page를 PageResponse로 변환) - 200 OK
     * 
     * @param page Spring Data Page 객체
     * @param message 경고 메시지
     * @param <T> 데이터 타입
     * @return 페이징 경고 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> warningPage(Page<T> page, String message) {
        PageResponse<T> pageResponse = PageResponse.from(page);
        return warningPage(pageResponse, message);
    }

    /**
     * 페이징 경고 응답 (Page를 PageResponse로 변환, 기본 메시지) - 200 OK
     * 
     * @param page Spring Data Page 객체
     * @param <T> 데이터 타입
     * @return 페이징 경고 응답
     */
    public static <T> AxResponseEntity<PageResponse<T>> warningPage(Page<T> page) {
        return warningPage(page, "경고");
    }

    // ==================== 헬퍼 메서드 ====================

    /**
     * 성공 응답 본문 생성
     * 
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @param status HTTP 상태 (참고용)
     * @param <T> 데이터 타입
     * @return 성공 응답 본문
     */
    private static <T> AxResponse<T> createSuccess(T data, String message, HttpStatus status) {
        return AxResponse.success(data, message);
    }

    /**
     * 실패 응답 본문 생성
     * 
     * @param message 오류 메시지
     * @param status HTTP 상태
     * @param <T> 데이터 타입
     * @return 실패 응답 본문
     */
    @SuppressWarnings("unchecked")
    private static <T> AxResponse<T> createFailure(String message, HttpStatus status) {
        String hscode = status.name().replace(' ', '_').toUpperCase();
        String defaultCode = generateDefaultCodeFromStatus(status);
        return (AxResponse<T>) AxResponse.failureCompat(message, hscode, defaultCode, status.getReasonPhrase());
    }

    /**
     * HTTP 상태에서 기본 코드 생성
     * 
     * @param status HTTP 상태
     * @return 기본 코드
     */
    private static String generateDefaultCodeFromStatus(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST: return "C001";
            case UNAUTHORIZED: return "A001";
            case FORBIDDEN: return "A004";
            case NOT_FOUND: return "R001";
            case CONFLICT: return "D001";
            case INTERNAL_SERVER_ERROR: return "S001";
            default: return "G001"; // Generic error code
        }
    }

    /**
     * ErrorCode를 사용한 실패 응답 본문 생성
     * 
     * @param message 오류 메시지
     * @param errorCode ErrorCode 객체
     * @param <T> 데이터 타입
     * @return 실패 응답 본문
     */
    @SuppressWarnings("unchecked")
    private static <T> AxResponse<T> createFailureWithErrorCode(String message, ErrorCode errorCode) {
        return (AxResponse<T>) AxResponse.failureCompat(
            message, 
            errorCode.getStatus().name(),
            errorCode.getCode(),
            message
        );
    }

    /**
     * ErrorCode를 사용한 실패 응답 본문 생성 - 상세 정보 포함
     * 
     * @param message 오류 메시지
     * @param errorCode ErrorCode 객체
     * @param details 상세 정보
     * @param <T> 데이터 타입
     * @return 실패 응답 본문
     */
    @SuppressWarnings("unchecked")
    private static <T> AxResponse<T> createFailureWithErrorCodeAndDetails(String message, ErrorCode errorCode, String details) {
        return (AxResponse<T>) AxResponse.failureCompat(
            message, 
            errorCode.getStatus().name(),
            errorCode.getCode(),
            details
        );
    }

    /**
     * ErrorCode를 사용한 실패 응답 본문 생성 - 상세 정보와 ActionType 포함
     * 
     * @param message 오류 메시지
     * @param errorCode ErrorCode 객체
     * @param details 상세 정보
     * @param actionType 에러 발생 처리 액션 타입
     * @param <T> 데이터 타입
     * @return 실패 응답 본문
     */
    @SuppressWarnings("unchecked")
    private static <T> AxResponse<T> createFailureWithErrorCodeDetailsAndAction(String message, ErrorCode errorCode, String details, ActionType actionType) {
        return (AxResponse<T>) AxResponse.failure(
            message, 
            errorCode.getStatus().name(),
            errorCode.getCode(),
            errorCode.getStatus().value(),
            errorCode.getStatus().getReasonPhrase(),
            details,
            actionType
        );
    }
}
