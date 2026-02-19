package com.skax.aiplatform.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모델 도입 내용 타입 ENUM
 */
@Getter
@RequiredArgsConstructor
public enum ModelDoipCtntType {
    
    RESORVOIR("RESORVOIR", "저장소 체크"),
    VACCINE_SCAN("VACCINE_SCAN", "백신 검사"),
    VULNERABILITY_CHECK("VULNERABILITY_CHECK", "취약점 점검"),
    VULNERABILITY_CHECK_SUMMARY("VULNERABILITY_CHECK_SUMMARY", "취약점 점검 요약");
    
    private final String code;
    private final String description;
    
    /**
     * 코드로 ENUM 찾기
     */
    public static ModelDoipCtntType fromCode(String code) {
        for (ModelDoipCtntType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type code: " + code);
    }
}

