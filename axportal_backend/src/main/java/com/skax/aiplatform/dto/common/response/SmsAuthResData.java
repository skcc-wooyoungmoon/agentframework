package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthResData {
    private String authEventId;
    private String authRdnVdTm;
    private String cellPhoneNumber;
    private String mailAddress;
    private String employeeName;
}
