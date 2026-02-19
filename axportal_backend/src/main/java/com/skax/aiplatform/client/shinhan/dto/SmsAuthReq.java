package com.skax.aiplatform.client.shinhan.dto;

import com.skax.aiplatform.dto.common.request.SmsAuthReqData;
import com.skax.aiplatform.dto.common.request.SwingReqCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthReq {
    private SwingReqCommon common;
    private SmsAuthReqData data;
}
