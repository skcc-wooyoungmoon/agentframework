package com.skax.aiplatform.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 사용자 사용량 관리 내보내기 요청 DTO (데이터 직접 전송)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUsageMgmtExportDataReq {
    
    /**
     * 선택된 ID 목록
     */
    private List<String> selectedIds;
    
    /**
     * 헤더 정보 목록
     */
    private List<UserUsageMgmtHeaderInfoReq> headers;
    
    /**
     * 내보낼 데이터 목록
     */
    private List<Map<String, Object>> data;
}
