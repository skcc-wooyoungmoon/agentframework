package com.skax.aiplatform.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 에러 응답시 클라이언트 UI에서 처리할 액션 타입을 정의하는 열거형
 * 
 * <p>API 에러 응답시 클라이언트가 사용자에게 어떤 액션 버튼을 표시할지 결정하기 위한 타입입니다.
 * 에러의 성격과 상황에 따라 적절한 액션 타입을 선택하여 사용자 경험을 향상시킵니다.</p>
 * 
 * <h3>사용 시나리오별 가이드:</h3>
 * <ul>
 *   <li><strong>CONFIRM</strong>: 일반적인 에러 상황 (기본값)</li>
 *   <li><strong>PREVIOUS</strong>: 폼 유효성 검증 실패시 이전 단계로 돌아가도록 유도</li>
 *   <li><strong>NEXT</strong>: 선택적 단계에서 에러 발생시 다음 단계로 진행 허용</li>
 *   <li><strong>RETRY</strong>: 일시적 장애로 재시도 가능한 상황</li>
 *   <li><strong>CANCEL</strong>: 위험한 작업이나 복구 불가능한 상황</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 일시적 네트워크 오류
 * throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "서버 연결에 실패했습니다", ActionType.RETRY);
 * 
 * // 폼 유효성 검증 실패
 * throw new ValidationException(ErrorCode.VALIDATION_FAILED, "입력값을 확인하세요", ActionType.PREVIOUS);
 * 
 * // 위험한 삭제 작업 실패
 * throw new BusinessException(ErrorCode.DELETE_FAILED, "삭제할 수 없습니다", ActionType.CANCEL);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-09
 * @version 1.0
 * @see CustomException
 * @see BusinessException
 * @see ValidationException
 */
@Schema(description = "에러 응답시 클라이언트 액션 타입")
public enum ActionType {
    
    /**
     * 확인 버튼 표시 (기본값)
     * 
     * <p>가장 일반적인 에러 상황에서 사용됩니다.
     * 사용자가 에러 메시지를 확인한 후 현재 화면에 머무르거나 이전 화면으로 돌아갑니다.</p>
     * 
     * @implNote 기본 ActionType으로 설정되어 있어 별도 지정하지 않으면 자동으로 적용됩니다.
     */
    @Schema(description = "확인 버튼 표시 (기본값)")
    CONFIRM,
    
    /**
     * 이전 버튼 표시
     * 
     * <p>폼 유효성 검증 실패나 입력값 오류시 사용됩니다.
     * 사용자가 이전 단계로 돌아가서 입력값을 수정할 수 있도록 유도합니다.</p>
     * 
     * @apiNote 주로 다단계 폼이나 위저드 형태의 UI에서 유효성 검증 실패시 사용됩니다.
     */
    @Schema(description = "이전 버튼 표시 (폼 유효성 검증 실패 등)")
    PREVIOUS,
    
    /**
     * 다음 버튼 표시
     * 
     * <p>선택적 단계에서 에러가 발생했지만 다음 단계로 진행 가능한 경우 사용됩니다.
     * 현재 단계를 건너뛰고 다음 단계로 진행할 수 있는 옵션을 제공합니다.</p>
     * 
     * @apiNote 선택적 기능이나 부가 서비스 설정에서 오류 발생시 사용됩니다.
     */
    @Schema(description = "다음 버튼 표시 (선택적 단계 건너뛰기)")
    NEXT,
    
    /**
     * 재시도 버튼 표시
     * 
     * <p>일시적인 네트워크 오류나 서버 장애로 인한 실패시 사용됩니다.
     * 사용자가 동일한 작업을 다시 시도할 수 있도록 재시도 옵션을 제공합니다.</p>
     * 
     * @apiNote 외부 API 호출 실패, 파일 업로드 실패, 네트워크 타임아웃 등에 사용됩니다.
     */
    @Schema(description = "재시도 버튼 표시 (일시적 장애)")
    RETRY,
    
    /**
     * 취소 버튼 표시
     * 
     * <p>위험한 작업이나 복구 불가능한 상황에서 사용됩니다.
     * 현재 작업을 중단하고 이전 상태로 되돌아가도록 유도합니다.</p>
     * 
     * @apiNote 데이터 삭제 실패, 보안 위반, 권한 부족 등 심각한 상황에서 사용됩니다.
     */
    @Schema(description = "취소 버튼 표시 (위험한 작업이나 복구 불가능한 상황)")
    CANCEL
}