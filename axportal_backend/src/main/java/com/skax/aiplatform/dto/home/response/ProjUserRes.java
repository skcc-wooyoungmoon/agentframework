package com.skax.aiplatform.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjUserRes {
    private String memberId;
    private String jkwNm;
    private String deptNm;
    private String dmcStatus;
    private String retrJkwYn;
    private String lstLoginAt;
    private String enabled;
}
