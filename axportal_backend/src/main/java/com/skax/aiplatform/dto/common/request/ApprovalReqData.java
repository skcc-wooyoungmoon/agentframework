package com.skax.aiplatform.dto.common.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalReqData {

    private String subject;
    private String draftDateTime;
    private String draftEmployeeNo;
    private String currentApprovalEmployeeNo;
    private String currentApprovalCount;
    private String totalApprovalCount;
    private String approvalSummary;
    private String approvalOpinionYn;
    private String callBackUrl;
    private String detailPageUrl;
    private String agentUseYn;
    private List<ApprovalEmployees> approvalEmployees;
    private List<Object> attachments;

    private String approvalDocumentId;

}
