package com.skax.aiplatform.common.exception;

import lombok.Getter;

/**
 * 애플리케이션 전체에서 사용하는 기본 커스텀 예외 클래스
 * 
 * <p>모든 비즈니스 예외의 기본 클래스로, ErrorCode와 ActionType을 포함하여
 * 일관된 예외 처리와 클라이언트 응답을 지원합니다.</p>
 * 
 * <h3>주요 특징:</h3>
 * <ul>
 *   <li><strong>ErrorCode 지원</strong>: 표준화된 에러 코드와 HTTP 상태 관리</li>
 *   <li><strong>ActionType 지원</strong>: 클라이언트 UI 액션 가이드 제공</li>
 *   <li><strong>메시지 커스터마이징</strong>: ErrorCode 기본 메시지 외 추가 상세 메시지 지원</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-09
 * @version 1.0
 * @see ErrorCode
 * @see ActionType
 * @see BusinessException
 * @see ValidationException
 */
@Getter
public class CustomException extends RuntimeException {

    /**
     * 에러 코드 (HTTP 상태, 코드, 기본 메시지 포함)
     */
    private final ErrorCode errorCode;
    
    /**
     * 클라이언트 UI 액션 타입 (기본값: CONFIRM)
     */
    private final ActionType actionType;

    /**
     * ErrorCode만 사용하는 기본 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.actionType = ActionType.CONFIRM; // 기본값
    }

    /**
     * ErrorCode와 커스텀 메시지를 사용하는 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     * @param message 커스텀 메시지 (ErrorCode 기본 메시지 외 추가 정보)
     */
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.actionType = ActionType.CONFIRM; // 기본값
    }

    /**
     * ErrorCode, 커스텀 메시지, ActionType을 사용하는 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     * @param message 커스텀 메시지
     * @param actionType 클라이언트 액션 타입
     */
    public CustomException(ErrorCode errorCode, String message, ActionType actionType) {
        super(message);
        this.errorCode = errorCode;
        this.actionType = actionType != null ? actionType : ActionType.CONFIRM;
    }

    /**
     * ErrorCode, ActionType만 사용하는 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     * @param actionType 클라이언트 액션 타입
     */
    public CustomException(ErrorCode errorCode, ActionType actionType) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.actionType = actionType != null ? actionType : ActionType.CONFIRM;
    }

    /**
     * ErrorCode와 원인 예외를 포함하는 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     * @param cause 원인 예외
     */
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.actionType = ActionType.CONFIRM; // 기본값
    }

    /**
     * 원인 예외를 포함하는 완전한 생성자
     * 
     * @param errorCode 에러 코드 (필수)
     * @param message 커스텀 메시지
     * @param actionType 클라이언트 액션 타입
     * @param cause 원인 예외
     */
    public CustomException(ErrorCode errorCode, String message, ActionType actionType, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.actionType = actionType != null ? actionType : ActionType.CONFIRM;
    }

    /**
     * ErrorCode, 추가 메시지, 원인 예외를 받는 생성자 (하위 호환성)
     * 
     * @param errorCode 에러 코드
     * @param message 추가 메시지
     * @param cause 원인 예외
     */
    public CustomException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.actionType = ActionType.CONFIRM; // 기본값
    }

    /**
     * 디버깅을 위한 예외 정보 문자열 반환
     * 
     * @return 예외 정보 문자열
     */
    @Override
    public String toString() {
        return String.format("%s(errorCode=%s, actionType=%s, message='%s')",
                getClass().getSimpleName(),
                errorCode.getCode(),
                actionType,
                getMessage());
    }
}
