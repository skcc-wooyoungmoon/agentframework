package com.skax.aiplatform.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 사용량 관리 헤더 정보 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUsageMgmtHeaderInfoReq {
    
    /**
     * 헤더명
     */
    private String headerName;
    
    /**
     * 필드명
     */
    private String field;
    
    /**
     * 컬럼 너비
     */
    private Integer width;
}
