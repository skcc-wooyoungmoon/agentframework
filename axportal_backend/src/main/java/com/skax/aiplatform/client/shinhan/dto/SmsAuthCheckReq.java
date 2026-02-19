package com.skax.aiplatform.client.shinhan.dto;

import com.skax.aiplatform.dto.common.request.SmsAuthCheckReqData;
import com.skax.aiplatform.dto.common.request.SwingReqCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthCheckReq {
    private SwingReqCommon common;
    private SmsAuthCheckReqData data;
}
