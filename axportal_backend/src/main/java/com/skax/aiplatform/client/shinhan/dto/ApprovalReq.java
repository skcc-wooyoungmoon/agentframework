package com.skax.aiplatform.client.shinhan.dto;

import com.skax.aiplatform.dto.common.request.SwingReqCommon;
import com.skax.aiplatform.dto.common.request.ApprovalReqData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalReq {
    private ApprovalReqData data;
    private SwingReqCommon common;
}
