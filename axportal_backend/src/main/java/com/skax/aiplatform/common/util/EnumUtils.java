package com.skax.aiplatform.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Enum 변환 유틸리티 클래스
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-08-27
 */
@Slf4j
public final class EnumUtils {

    private EnumUtils() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이므로 인스턴스화할 수 없습니다.");
    }

    /**
     * 문자열을 Enum으로 변환 (null 허용)
     *
     * @param value     변환할 문자열 값
     * @param enumClass Enum 클래스 타입
     * @param <T>       Enum 타입
     * @return 변환된 Enum 값 (null 가능)
     */
    public static <T extends Enum<T>> T valueOf(String value, Class<T> enumClass) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 enum 값 [{}] 이(가) enum 클래스 [{}]에 전달되었습니다.", value, enumClass.getSimpleName());
            return null;
        }
    }

}
