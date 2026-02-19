package com.skax.aiplatform.common.exception;

/**
 * 데이터 검증 관련 예외 클래스
 * 
 * <p>입력값 검증 실패나 데이터 유효성 검사 오류 시 발생하는 예외입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
public class ValidationException extends CustomException {

    /**
     * ErrorCode를 받는 생성자
     * 
     * @param errorCode 에러 코드
     */
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * ErrorCode와 추가 메시지를 받는 생성자
     * 
     * @param errorCode 에러 코드
     * @param message 추가 메시지
     */
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * ErrorCode, 커스텀 메시지, ActionType을 사용하는 생성자
     * 
     * <p>폼 유효성 검증 실패시 ActionType.PREVIOUS 사용을 권장합니다.</p>
     * 
     * @param errorCode 유효성 검증 에러 코드
     * @param message 상세 검증 오류 메시지
     * @param actionType 클라이언트 액션 타입 (PREVIOUS 권장)
     */
    public ValidationException(ErrorCode errorCode, String message, ActionType actionType) {
        super(errorCode, message, actionType);
    }

    /**
     * ErrorCode와 ActionType을 사용하는 생성자
     * 
     * @param errorCode 유효성 검증 에러 코드
     * @param actionType 클라이언트 액션 타입
     */
    public ValidationException(ErrorCode errorCode, ActionType actionType) {
        super(errorCode, actionType);
    }

    /**
     * ErrorCode와 원인 예외를 받는 생성자
     * 
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public ValidationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * 원인 예외를 포함하는 완전한 생성자
     * 
     * @param errorCode 유효성 검증 에러 코드
     * @param message 상세 검증 오류 메시지
     * @param actionType 클라이언트 액션 타입
     * @param cause 원인 예외 (주로 ConstraintViolationException 등)
     */
    public ValidationException(ErrorCode errorCode, String message, ActionType actionType, Throwable cause) {
        super(errorCode, message, actionType, cause);
    }

    /**
     * ErrorCode, 추가 메시지, 원인 예외를 받는 생성자 (하위 호환성)
     * 
     * @param errorCode 에러 코드
     * @param message 추가 메시지
     * @param cause 원인 예외
     */
    public ValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
