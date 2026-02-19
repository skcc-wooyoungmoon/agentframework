package com.skax.aiplatform.client.ione.apikey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 포탈용 API 목록 결과 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfPubApiResult {
    
    /** 결과 코드 */
    private String resultCode;
    
    /** 결과 메시지 */
    private String resultMessage;
    
    /** API 목록 */
    private List<ApiInfo> apiList;
    
    /**
     * API 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiInfo {
        
        /** API ID */
        private String apiId;
        
        /** API 이름 */
        private String apiName;
        
        /** API 설명 */
        private String description;
        
        /** API URL */
        private String apiUrl;
        
        /** HTTP Method */
        private String method;
        
        /** 사용 여부 */
        private String useYn;
    }
}