package com.skax.aiplatform.common.util;

import com.skax.aiplatform.common.constant.Constants;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.exception.ValidationException;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 날짜 관련 유틸리티 클래스
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-08-26
 */
public abstract class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이므로 인스턴스화할 수 없습니다.");
    }

    /**
     * 일 포맷터
     */
    private static final DateTimeFormatter DAY_FORMATTER =
        DateTimeFormatter.ofPattern(Constants.DateTime.DATE_FORMAT);
    /**
     * 일시 포맷터
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern(Constants.DateTime.DATETIME_FORMAT);

    /**
     * "yyyy.MM.dd" 형식의 문자열을 해당 날짜의 시작 시간(00:00:00)으로 변환
     *
     * @param dateString 날짜 문자열 ("yyyy.MM.dd" 형식)
     * @return 해당 날짜의 시작 시간 LocalDateTime 객체, null인 경우 null 반환
     * @throws ValidationException 날짜 형식이 올바르지 않은 경우
     */
    public static LocalDateTime parseToStartOfDay(String dateString) {
        if (!StringUtils.hasText(dateString)) {
            return null;
        }

        try {
            LocalDate localDate = LocalDate.parse(dateString, DAY_FORMATTER);
            return localDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_FORMAT,
                "날짜 형식이 올바르지 않습니다. (%s)".formatted(dateString));
        }
    }

    /**
     * "yyyy.MM.dd" 형식의 문자열을 해당 날짜의 끝 시간(23:59:59)으로 변환
     *
     * @param dateString 날짜 문자열 ("yyyy.MM.dd" 형식)
     * @return 해당 날짜의 끝 시간 LocalDateTime 객체, null인 경우 null 반환
     * @throws ValidationException 날짜 형식이 올바르지 않은 경우
     */
    public static LocalDateTime parseToEndOfDay(String dateString) {
        if (!StringUtils.hasText(dateString)) {
            return null;
        }

        try {
            LocalDate localDate = LocalDate.parse(dateString, DAY_FORMATTER);
            return localDate.atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_FORMAT,
                "날짜 형식이 올바르지 않습니다. (%s)".formatted(dateString));
        }
    }

    /**
     * "yyyy.MM.dd" 형식의 문자열을 "yyyy.MM.dd 00:00:00" 문자열로 변환
     *
     * @param dateString 날짜 문자열 ("yyyy.MM.dd" 형식)
     * @return 시작 시간이 포함된 날짜 문자열 ("yyyy.MM.dd HH:mm:ss" 형식), null인 경우 null 반환
     * @throws ValidationException 날짜 형식이 올바르지 않은 경우
     */
    public static String toStartOfDayString(String dateString) {
        LocalDateTime start = parseToStartOfDay(dateString);
        return start != null ? start.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * "yyyy.MM.dd" 형식의 문자열을 "yyyy.MM.dd 23:59:59" 문자열로 변환
     *
     * @param dateString 날짜 문자열 ("yyyy.MM.dd" 형식)
     * @return 종료 시간이 포함된 날짜 문자열 ("yyyy.MM.dd HH:mm:ss" 형식), null인 경우 null 반환
     * @throws ValidationException 날짜 형식이 올바르지 않은 경우
     */
    public static String toEndOfDayString(String dateString) {
        LocalDateTime end = parseToEndOfDay(dateString);
        return end != null ? end.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * LocalDateTime 타입을 "yyyy.MM.dd HH:mm:ss" 형식의 문자열로 변환
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @return 포맷된 날짜 문자열 ("yyyy.MM.dd HH:mm:ss" 형식), null인 경우 null 반환
     */
    public static String toDateTimeString(String localDateTimeStr) {
        return toDateTimeString(OffsetDateTime.parse(localDateTimeStr).toLocalDateTime());
    }
    public static String toDateTimeString(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * UTC LocalDateTime을 KST(Asia/Seoul)로 변환하여 "yyyy.MM.dd HH:mm:ss" 형식의 문자열로 반환
     *
     * @param utcDateTime UTC 시간대의 LocalDateTime 객체
     * @return KST로 변환된 날짜 문자열 ("yyyy.MM.dd HH:mm:ss" 형식), null인 경우 null 반환
     */
    public static String utcToKstDateTimeString(String utcDateTimeStr) {
        return utcToKstDateTimeString(OffsetDateTime.parse(utcDateTimeStr).toLocalDateTime());
    }
    public static String utcToKstDateTimeString(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }

        // UTC LocalDateTime을 ZonedDateTime으로 변환 (UTC 시간대 지정)
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));

        // KST(Asia/Seoul) 시간대로 변환
        ZonedDateTime kstZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        // LocalDateTime으로 변환 후 포맷팅
        return kstZoned.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

}
