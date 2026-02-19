package com.skax.aiplatform.client.ione.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 페이징된 API 목록 결과 DTO
 * 
 * <p>iONE 시스템에서 API 목록을 페이징과 함께 조회할 때 사용되는 응답 DTO입니다.
 * API 목록과 페이징 정보를 함께 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiListResultWithPagination {
    
    /**
     * 요청 성공 여부
     */
    private Boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * API 목록
     */
    private List<IntfApiListVo> apis;
    
    /**
     * 총 레코드 수
     */
    private Long totalCount;
    
    /**
     * 현재 페이지 번호
     */
    private Integer currentPage;
    
    /**
     * 페이지 크기
     */
    private Integer pageSize;
    
    /**
     * 총 페이지 수
     */
    private Integer totalPages;
    
    /**
     * 다음 페이지 존재 여부
     */
    private Boolean hasNext;
    
    /**
     * 이전 페이지 존재 여부
     */
    private Boolean hasPrevious;
    
    /**
     * 에러 코드 (실패 시)
     */
    private String errorCode;
    
    /**
     * 에러 메시지 (실패 시)
     */
    private String errorMessage;
}