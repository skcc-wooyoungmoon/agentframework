package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalReqInfo {
    private String jkw_nm;
    private String dept_nm;
    private String prj_nm;
    private String role_nm;
}
