package com.skax.aiplatform.client.ione.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 어드민 사용자 삭제 요청 DTO
 * 
 * <p>어드민 사용자를 삭제하기 위한 요청 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfAdminUserDeleteRequest {
    
    /**
     * 삭제할 사용자 ID
     */
    private String userId;
    
    /**
     * 삭제 유형 (SOFT, HARD)
     * SOFT: 비활성화
     * HARD: 완전 삭제
     */
    private String deleteType;
    
    /**
     * 삭제 사유
     */
    private String deleteReason;
    
    /**
     * 삭제자 ID
     */
    private String delId;
}