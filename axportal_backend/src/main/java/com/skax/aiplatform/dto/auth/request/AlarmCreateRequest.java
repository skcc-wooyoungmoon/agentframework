package com.skax.aiplatform.dto.auth.request;

import com.skax.aiplatform.common.constant.CommCode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AlarmCreateRequest {
    private String alarmId;
    private String targetUser;
    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private CommCode.AlarmStatus statusNm;
    private String approvalDataId;
}
