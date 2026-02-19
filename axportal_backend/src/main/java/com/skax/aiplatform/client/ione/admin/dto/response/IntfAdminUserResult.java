package com.skax.aiplatform.client.ione.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 어드민 사용자 작업 결과 DTO
 * 
 * <p>어드민 사용자 생성/수정/삭제 작업의 결과를 담는 응답 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfAdminUserResult {
    
    /**
     * 성공 여부
     */
    private Boolean success;
    
    /**
     * 결과 코드
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMessage;
    
    /**
     * 처리된 사용자 ID
     */
    private String userId;
    
    /**
     * 처리 시간
     */
    private String processTime;
    
    /**
     * 에러 메시지 (실패 시)
     */
    private String errorMessage;
    
    /**
     * 에러 상세 정보 (실패 시)
     */
    private String errorDetails;
}