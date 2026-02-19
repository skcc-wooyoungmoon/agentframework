package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwingReqCommon {
    private String clientId;
    private String clientSecret;
    private String companyCode;
    private String employeeNo;
    private String requestUniqueKey;
}
