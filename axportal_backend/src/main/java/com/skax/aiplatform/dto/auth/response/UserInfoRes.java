package com.skax.aiplatform.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRes {
    private String memberId;
    private String grpcoC;
    private String grpcoNm;
    private String jkwNm;
    private String jkpgNm;
    private String jkwiC;
    private String jkwiNm;
    private String retrJkwYn;
    private String deptNm;
    private String deptNo;
    private String adxpUserId;
}
