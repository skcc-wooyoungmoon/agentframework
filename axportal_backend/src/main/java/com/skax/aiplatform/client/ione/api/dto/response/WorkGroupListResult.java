package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 업무 코드 목록 조회 결과 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkGroupListResult {
    
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
     * 업무 코드 목록
     */
    private List<WorkGroupInfo> workGroups;
    
    /**
     * 업무 코드 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkGroupInfo {
        
        /**
         * 업무 코드 ID
         */
        private String taskId;
        
        /**
         * 업무 코드명
         */
        private String taskName;
        
        /**
         * 업무 설명
         */
        private String taskDesc;
        
        /**
         * 사용 여부
         */
        private String useYn;
        
        /**
         * 정렬 순서
         */
        private Integer sortOrder;
        
        /**
         * 등록일시
         */
        private String regDt;
        
        /**
         * 수정일시
         */
        private String modDt;
    }
}