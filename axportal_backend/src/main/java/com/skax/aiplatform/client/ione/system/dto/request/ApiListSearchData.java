package com.skax.aiplatform.client.ione.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 목록 검색 조건
 * 
 * <p>iONE 시스템에서 API 목록을 검색할 때 사용되는 조건 DTO입니다.
 * 페이징 정보와 검색 조건을 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiListSearchData {
    
    /**
     * 페이지 번호 (1부터 시작)
     */
    private Integer currentPage;
    
    /**
     * 페이지 크기
     */
    private Integer pageSize;
    
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
    
    /**
     * API 설명
     */
    private String apiDesc;
    
    /**
     * API 서비스 그룹 ID
     */
    private String apiSvcGrpId;
    
    /**
     * API 서버 그룹 ID
     */
    private String apiSvrGrpId;
}