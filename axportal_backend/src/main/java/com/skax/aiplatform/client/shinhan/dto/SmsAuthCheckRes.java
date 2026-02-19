package com.skax.aiplatform.client.shinhan.dto;

import com.skax.aiplatform.dto.common.response.SmsAuthResCheckData;
import com.skax.aiplatform.dto.common.response.SwingResCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthCheckRes {
    private SwingResCommon common;
    private SmsAuthResCheckData data;
}
