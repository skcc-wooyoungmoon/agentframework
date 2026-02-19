package com.skax.aiplatform.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모델 상태 ENUM
 */
@Getter
@RequiredArgsConstructor
public enum ModelGardenStatus {
     
    // 반입 진행현황에 따른 상태값들
    PENDING("PENDING", "반입전", "BEFORE"),
    IMPORT_REQUEST("IMPORT_REQUEST", "반입 요청", "PROGRESS"),
    // IMPORT_REQUEST_APPROVAL_IN_PROGRESS("IMPORT_REQUEST_APPROVAL_IN_PROGRESS", "반입요청 결재중", "IMPORT_REQUEST_APPROVAL_IN_PROGRESS"),
    // IMPORT_REQUEST_APPROVAL_COMPLETED("IMPORT_REQUEST_APPROVAL_COMPLETED", "반입요청 결재완료", "PROGRESS"),
    // IMPORT_REQUEST_APPROVAL_REJECTED("IMPORT_REQUEST_APPROVAL_REJECTED", "반입요청 결재반려", "BEFORE"),
    FILE_IMPORT_COMPLETED("FILE_IMPORT_COMPLETED", "파일 반입 완료", "PROGRESS"), // 추가됨 10.27
    VACCINE_SCAN_COMPLETED("VACCINE_SCAN_COMPLETED", "백신검사 완료", "PROGRESS"),
    INTERNAL_NETWORK_IMPORT_COMPLETED("INTERNAL_NETWORK_IMPORT_COMPLETED", "내부망반입 완료", "PROGRESS"),
    VULNERABILITY_CHECK_COMPLETED("VULNERABILITY_CHECK_COMPLETED", "취약점점검 완료", "PROGRESS"),
    IMPORT_FAILED("IMPORT_FAILED", "반입실패", "ERROR"),
    VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS("VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS", "취약점점검 결재중", "PROGRESS"),
    VULNERABILITY_CHECK_APPROVAL_REJECTED("VULNERABILITY_CHECK_APPROVAL_REJECTED", "취약점점검 결재반려", "ERROR"),
    IMPORT_COMPLETED("IMPORT_COMPLETED", "반입완료", "COMPLETE"),
    IMPORT_COMPLETED_REGISTERED("IMPORT_COMPLETED_REGISTERED", "반입완료 등록완료", "COMPLETE"),
    IMPORT_COMPLETED_UNREGISTERED("IMPORT_COMPLETED_UNREGISTERED", "반입완료 등록해제", "COMPLETE");
    
    private final String code;
    private final String description;
    private final String group; // 묶음
    
    /**
     * 코드로 ENUM 찾기
     */
    public static ModelGardenStatus fromCode(String code) {
        for (ModelGardenStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
    
    /**
     * value로 해당하는 모든 ENUM 찾기
     */
    public static ModelGardenStatus[] fromGroup(String value) {
        return Arrays.stream(values())
                .filter(status -> status.getGroup().equals(value))
                .toArray(ModelGardenStatus[]::new);
    }
}
