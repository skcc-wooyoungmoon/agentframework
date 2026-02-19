package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 목록 VO
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfApiListVo {
    
    /**
     * API 식별자(System ID)
     */
    private String apiSysId;
    
    /**
     * API ID
     */
    private String apiId;
    
    /**
     * API 명
     */
    private String apiName;
    
    /**
     * API 설명
     */
    private String apiDesc;
    
    /**
     * 업무 코드
     */
    private String taskId;
    
    /**
     * 업무 코드 명
     */
    private String taskName;
    
    /**
     * API 서비스 그룹 식별자(System ID)
     */
    private String apiSvcGrpSysId;
    
    /**
     * API 서비스 그룹 ID
     */
    private String apiSvcGrpId;
    
    /**
     * API 서비스 그룹 명
     */
    private String apiSvcGrpName;
    
    /**
     * API 서비스 그룹 버전 식별자(System ID)
     */
    private String apiVerSysId;
    
    /**
     * API 서비스 그룹 버전 ID
     */
    private String apiVerId;
    
    /**
     * API 서버 그룹 식별자(System ID)
     */
    private String apiSvrGrpSysId;
    
    /**
     * API 서버 그룹 ID
     */
    private String apiSvrGrpId;
    
    /**
     * API 생성 시간 Timestamp
     */
    private String createDate;
    
    /**
     * API 생성자
     */
    private String creator;
    
    /**
     * API 수정 시간 Timestamp
     */
    private String modifyDate;
    
    /**
     * API 수정자
     */
    private String modifier;
}
