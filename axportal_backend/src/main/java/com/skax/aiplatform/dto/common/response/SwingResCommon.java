package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwingResCommon {
    private int resultCode;
    private String responseDatetime;
    private String requestUniqueKey;
    private String transactionId;
    private String errorCode;
    private String errorMessage;
}
