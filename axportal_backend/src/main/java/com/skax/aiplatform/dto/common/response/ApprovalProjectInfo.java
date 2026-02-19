package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalProjectInfo {
    private String memberId;
    private String jkwNm;
    private String deptNm;
    private String prjNm;
    private String roleNm;

}
