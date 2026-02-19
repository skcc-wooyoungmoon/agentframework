package com.skax.aiplatform.client.ione.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 목록 조회 요청 (페이징 포함)
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiListRequestWithPagination {
    
    /**
     * 페이지 번호
     */
    private Integer pageNum;
    
    /**
     * 페이지 크기
     */
    private Integer pageSize;
    
    /**
     * API 목록 검색 조건
     */
    private ApiListSearchData data;
}