package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalEmployees {
    private String employeeNo;
    private String approvalCount;
}