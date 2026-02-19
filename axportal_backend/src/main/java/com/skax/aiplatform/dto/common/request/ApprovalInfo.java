package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalInfo {
    Integer id;
    String approvalUniqueKey;
    String subject;
    String employeeNo;
    String approvalSummary;
    String targetEmployeeNo;
    String approvalDataId;
    String approvalDataNm;
    String uuid;
    String reaultCode;
    Integer maxApprovalCount;
    Integer currentApprovalCount;
    String payApprovalReqString;
}
