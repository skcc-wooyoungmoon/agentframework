package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 서버 그룹 정보 조회 결과 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiServerGroupInfoResult {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * API 서버 그룹 정보
     */
    private ApiServerGroupDetail serverGroup;
    
    /**
     * API 서버 그룹 상세 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiServerGroupDetail {
        
        /**
         * API 서버 그룹 ID
         */
        private String apiSvrGrpId;
        
        /**
         * API 서버 그룹명
         */
        private String apiSvrGrpName;
        
        /**
         * 서버 그룹 설명
         */
        private String svrGrpDesc;
        
        /**
         * 기본 URL
         */
        private String baseUrl;
        
        /**
         * 사용 여부
         */
        private String useYn;
        
        /**
         * 등록일시
         */
        private String regDt;
        
        /**
         * 등록자
         */
        private String regUser;
        
        /**
         * 수정일시
         */
        private String modDt;
        
        /**
         * 수정자
         */
        private String modUser;
    }
}