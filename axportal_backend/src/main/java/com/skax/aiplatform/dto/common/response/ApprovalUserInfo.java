package com.skax.aiplatform.dto.common.response;

import com.skax.aiplatform.dto.common.request.PayApprovalReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalUserInfo {
    private String apiSpclV;
    private String memberId;
    private String gyljjaMemberId;
    private String deptNm;
    private String jkwNm;
    private String fstCreatedAt;
    private PayApprovalReq payApprovalInfo;
}
