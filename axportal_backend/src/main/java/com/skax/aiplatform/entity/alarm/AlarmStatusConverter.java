package com.skax.aiplatform.entity.alarm;


import com.skax.aiplatform.common.constant.CommCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AlarmStatusConverter implements AttributeConverter<CommCode.AlarmStatus, String> {

    @Override
    public String convertToDatabaseColumn(CommCode.AlarmStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public CommCode.AlarmStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return CommCode.AlarmStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            return null; // 유효하지 않은 값은 null로 처리
        }
    }
}