package com.skax.aiplatform.client.ione.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 목록 검색 조건
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiListSearchData {
    
    /**
     * API ID
     */
    private String apiId;
    
    /**
     * API 명
     */
    private String apiName;
    
    /**
     * 업무 코드
     */
    private String taskId;
}
