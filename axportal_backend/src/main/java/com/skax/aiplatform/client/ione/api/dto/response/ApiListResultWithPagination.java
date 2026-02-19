package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API 목록 결과 (페이징 포함)
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiListResultWithPagination {
    
    /**
     * 페이지 번호
     */
    private Integer pageNum;
    
    /**
     * 페이지 크기
     */
    private Integer pageSize;
    
    /**
     * 검색 조건에 해당하는 API 갯수
     */
    private Integer totalCount;
    
    /**
     * 검색 조건에 해당하는 API 목록
     */
    private List<IntfApiListVo> data;
}
