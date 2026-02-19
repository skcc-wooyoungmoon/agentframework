package com.skax.aiplatform.common.constant;

import java.util.Map;

import static com.skax.aiplatform.common.constant.AlarmMessageType.*;

/**
 * 결재 알람 메시지 상수 클래스
 * <p>
 * 결재 유형별(프로젝트 생성, 참여, 모델 반입 등) 알람 메시지를 관리합니다.
 * <p>
 * 구조:
 * - 첫 번째 키: 결재 유형 코드 ("01", "02", "03", ...)
 * - 두 번째 키: AlarmMessageType enum (SEND_DONE, RECV_DONE, RES_APPROVAL, RES_REJECT, SEND_FAIL, RECV_FAIL, AUTO_CLOSE)
 * - 값: AlarmMessage (title, content)
 */
public class ApprovalAlarmMessages {

    /**
     * 결재 알람 메시지 맵
     * <p>
     * 결재 유형별로 다양한 상황에 따른 알람 메시지를 정의합니다.
     * - "01": 프로젝트 생성
     * - "02": 프로젝트 참여
     * - "03": 모델 반입
     */
    public static final Map<String, Map<AlarmMessageType, AlarmMessage>> ALARM_MESSAGE_MAP = Map.of(
            "01", Map.of(
                    SEND_DONE, new AlarmMessage("프로젝트 생성 요청 완료", "생성 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("프로젝트 생성 요청", "생성 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("프로젝트 생성 완료", "요청 승인으로 프로젝트 생성이 완료되었습니다. \n" +
                            "생성된 프로젝트를 이용할 수 있어요."),
                    RES_REJECT, new AlarmMessage("프로젝트 생성 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("프로젝트 생성 실패", "요청 승인이 완료되었으나, 프로젝트 생성 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("프로젝트 생성 실패", "요청 승인하신 결과에 따라 프로젝트 생성 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("프로젝트 생성 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "02", Map.of(
                    SEND_DONE, new AlarmMessage("프로젝트 참여 요청 완료", "참여 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("프로젝트 참여 요청", "참여 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("프로젝트 참여 완료", "요청 승인으로 프로젝트 참여가 완료되었습니다. \n" +
                            "이제부터 프로젝트를 이용할 수 있어요."),
                    RES_REJECT, new AlarmMessage("프로젝트 참여 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("프로젝트 참여 실패", "요청 승인이 완료되었으나, 프로젝트 참여 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("프로젝트 참여 실패", "요청 승인하신 결과에 따라 프로젝트 참여 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("프로젝트 참여 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "03", Map.of(
                    SEND_DONE, new AlarmMessage("모델 점검 결과 승인 요청 완료", "모델 점검 결과 승인 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("모델 점검 결과 승인 요청", "점검 결과 승인 요청을 확인해주세요."),
                    RES_APPROVAL, new AlarmMessage("모델 점검 결과 승인 완료", "요청 승인으로 모델 반입이 완료되었습니다. \n" +
                            "이제부터 해당 모델을 이용할 수 있으며, 모델 카탈로그에 자동 등록됩니다."),
                    RES_REJECT, new AlarmMessage("모델 점검 결과 승인 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("모델 점검 결과 승인 실패", "요청 승인이 완료되었으나, 모델 반입 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("모델 점검 결과 승인 실패", "요청 승인하신 결과에 따라 모델 반입 처리 중 오류가 발생하였습니다. "),
                    AUTO_CLOSE, new AlarmMessage("모델 점검 결과 승인 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "04", Map.of(
                    SEND_DONE, new AlarmMessage("모델 배포 요청 완료", "배포 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("모델 배포 요청", "배포 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("모델 배포 완료", "요청 승인으로 모델 배포가 완료되었습니다."),
                    RES_REJECT, new AlarmMessage("모델 배포 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("모델 배포 실패", "요청 승인이 완료되었으나, 모델 배포 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("모델 배포 실패", "요청 승인하신 결과에 따라 모델 배포 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("모델 배포 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "05", Map.of(
                    SEND_DONE, new AlarmMessage("에이전트 배포 요청 완료", "배포 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("에이전트 배포 요청", "배포 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("에이전트 배포 완료", "요청 승인으로 에이전트 배포가 완료되었습니다."),
                    RES_REJECT, new AlarmMessage("에이전트 배포 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("에이전트 배포 실패", "요청 승인이 완료되었으나, 에이전트 배포 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("에이전트 배포 실패", "요청 승인하신 결과에 따라 에이전트 배포 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("에이전트 배포 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "06", Map.of(
                    SEND_DONE, new AlarmMessage("API Key 발급 요청 완료", "발급 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("API Key 발급 요청", "발급 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("API Key 발급 완료", "요청 승인으로 API Key 발급이 완료되었습니다."),
                    RES_REJECT, new AlarmMessage("API Key 발급 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("API Key 발급 실패", "요청 승인이 완료되었으나, API Key 발급 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("API Key 발급 실패", "요청 승인하신 결과에 따라 API Key 발급 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("API Key 발급 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "07", Map.of(
                    SEND_DONE, new AlarmMessage("NAS Storage 상향 요청 완료", "상향 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("NAS Storage 상향 요청", "상향 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("NAS Storage 상향 완료", "요청 승인으로 NAS Storage 상향이 완료되었습니다."),
                    RES_REJECT, new AlarmMessage("NAS Storage 상향 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("NAS Storage 상향 실패", "요청 승인이 완료되었으나, NAS Storage 상향 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("NAS Storage 상향 실패", "요청 승인하신 결과에 따라 NAS Storage 상향 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("NAS Storage 상향 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            "08", Map.of(
                    SEND_DONE, new AlarmMessage("IDE 생성 요청 완료", "생성 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("IDE 생성 요청", "생성 요청을 승인해주세요."),
                    RES_APPROVAL, new AlarmMessage("IDE 생성 완료", "요청 승인으로 IDE 생성이 완료되었습니다."),
                    RES_REJECT, new AlarmMessage("IDE 생성 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("IDE 생성 실패", "요청 승인이 완료되었으나, IDE 생성 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("IDE 생성 실패", "요청 승인하신 결과에 따라 IDE 생성 처리 중 오류가 발생하였습니다."),
                    AUTO_CLOSE, new AlarmMessage("IDE 생성 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            ),
            // 모델점검 1차결재
            "09", Map.of(
                    SEND_DONE, new AlarmMessage("모델 점검 결과 승인 요청 완료", "모델 점검 결과 승인 요청이 완료되었습니다. \n" +
                            "요청건 취소를 원하는 경우, 결재 취소 버튼을 클릭해주세요."),
                    RECV_DONE, new AlarmMessage("모델 점검 결과 승인 요청", "점검 결과 승인 요청을 확인해주세요."),
                    RES_APPROVAL, new AlarmMessage("모델 점검 결과 승인 완료", "요청 승인으로 모델 반입이 완료되었습니다. \n" +
                            "이제부터 해당 모델을 이용할 수 있으며, 모델 카탈로그에 자동 등록됩니다."),
                    RES_REJECT, new AlarmMessage("모델 점검 결과 승인 요청 반려", "요청 반려되었습니다. 자세한 내용은 포탈 관리자에게 문의해주세요."),
                    SEND_FAIL, new AlarmMessage("모델 점검 결과 승인 실패", "요청 승인이 완료되었으나, 모델 반입 처리 중 오류가 발생하였습니다. \n" +
                            "자세한 내용은 포탈 관리자에게 문의해주세요."),
                    RECV_FAIL, new AlarmMessage("모델 점검 결과 승인 실패", "요청 승인하신 결과에 따라 모델 반입 처리 중 오류가 발생하였습니다. "),
                    AUTO_CLOSE, new AlarmMessage("모델 점검 결과 승인 요청 종료 안내", "다른 결재자에 의해 결재가 처리되어, 해당 결재 요청은 자동으로 취소되었습니다.")
            )
    );

    /**
     * Private constructor to prevent instantiation
     */
    private ApprovalAlarmMessages() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
