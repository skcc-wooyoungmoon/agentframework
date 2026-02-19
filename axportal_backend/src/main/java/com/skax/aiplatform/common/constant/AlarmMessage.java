package com.skax.aiplatform.common.constant;

/**
 * 알람 메시지 값 객체
 * <p>
 * 알람의 제목과 내용을 담는 불변 객체입니다.
 */
public record AlarmMessage(String title, String content) {
    
    /**
     * 알람 메시지 생성
     *
     * @param title 알람 제목
     * @param content 알람 내용
     */
    public AlarmMessage {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Alarm title cannot be null or blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Alarm content cannot be null or blank");
        }
    }
}
