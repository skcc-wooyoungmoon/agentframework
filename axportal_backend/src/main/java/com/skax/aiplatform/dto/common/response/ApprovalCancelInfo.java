package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalCancelInfo {
    private String gyljId;
    private String gyljRespId;
    private String gyljjaMemberId;
    private String memberId;
    private String apiSpclV;
    private String dtlCtnt;
}
