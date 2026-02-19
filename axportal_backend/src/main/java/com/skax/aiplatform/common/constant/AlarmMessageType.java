package com.skax.aiplatform.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알람 메시지 타입 열거형
 * <p>
 * 결재 처리 과정에서 발생하는 다양한 알람 메시지 유형을 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum AlarmMessageType {
    /**
     * 요청 완료 (발신자용)
     */
    SEND_DONE("sendDone"),
    
    /**
     * 요청 수신 (수신자용)
     */
    RECV_DONE("recvDone"),
    
    /**
     * 승인 결과 (발신자용)
     */
    RES_APPROVAL("resApproval"),
    
    /**
     * 반려 결과 (발신자용)
     */
    RES_REJECT("resReject"),
    
    /**
     * 처리 실패 (발신자용)
     */
    SEND_FAIL("sendFail"),
    
    /**
     * 처리 실패 (수신자용)
     */
    RECV_FAIL("recvFail"),
    
    /**
     * 자동 종료
     */
    AUTO_CLOSE("autoClose");
    
    private final String code;
    
    /**
     * 코드로부터 AlarmMessageType을 찾습니다.
     *
     * @param code 메시지 타입 코드
     * @return AlarmMessageType
     * @throws IllegalArgumentException 해당하는 타입이 없는 경우
     */
    public static AlarmMessageType fromCode(String code) {
        for (AlarmMessageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown alarm message type code: " + code);
    }
}
