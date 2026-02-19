package com.skax.aiplatform.client.ione.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 수정 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiUpdateRequest {
    
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
     * API URL
     */
    private String apiUrl;
    
    /**
     * HTTP 메서드
     */
    private String httpMethod;
    
    /**
     * API 서버 그룹 ID
     */
    private String apiSvrGrpId;
    
    /**
     * 업무 코드
     */
    private String taskId;
    
    /**
     * API 타입
     */
    private String apiType;
    
    /**
     * 사용 여부 (Y/N)
     */
    private String useYn;
}