package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 작업 요청 결과 목록 조회 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishWorkListResult {
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 전체 건수
     */
    private Long totalCount;
    
    /**
     * 작업 요청 목록
     */
    private List<PublishWorkInfo> publishWorks;
    
    /**
     * 작업 요청 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PublishWorkInfo {
        
        /**
         * 작업 순번
         */
        private String infWorkSeq;
        
        /**
         * API ID
         */
        private String apiId;
        
        /**
         * API 명
         */
        private String apiName;
        
        /**
         * 작업 타입 (CREATE, UPDATE, DELETE)
         */
        private String workType;
        
        /**
         * 작업 상태 (REQUEST, PROCESSING, COMPLETE, FAILED, CANCELLED)
         */
        private String workStatus;
        
        /**
         * 작업 요청일시
         */
        private String requestDt;
        
        /**
         * 작업 완료일시
         */
        private String completeDt;
        
        /**
         * 요청자
         */
        private String requestUser;
        
        /**
         * 에러 메시지
         */
        private String errorMessage;
    }
}