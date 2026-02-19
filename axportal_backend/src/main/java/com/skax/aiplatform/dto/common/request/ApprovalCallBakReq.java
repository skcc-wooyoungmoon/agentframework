package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalCallBakReq {
    private String approvalDocumentId;
    private String resultCode;
    private String approvalOpinion;
    private String approvalCount;
    private String approvalEmployeeNo;
    private String agentYn;
    private String gyljLineNm;
}
