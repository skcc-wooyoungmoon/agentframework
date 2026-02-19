package com.skax.aiplatform.dto.vertica.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "vertica DW Account 목록 쿼리 조회 응답")

public class DwAccountListRes {
    private String userName;
    private String empNo;
    private String groupName;
    private String deptCd;
    private String validStartDate;
    private String validEndDate;
    private String dbAccountId;
    private String dbName;
    private String dbType;
    private String ipAddr;
    private String dwDataGjdt;
    private String dwLstJukjaDt;
    private String accountStatus; /* 계정상태 */
}
