package com.skax.aiplatform.client.ione.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 어드민 사용자 생성 요청 DTO
 * 
 * <p>새로운 어드민 사용자를 생성하기 위한 요청 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfAdminUserCreateRequest {
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 사용자명
     */
    private String userName;
    
    /**
     * 비밀번호
     */
    private String password;
    
    /**
     * 이메일
     */
    private String email;
    
    /**
     * 전화번호
     */
    private String phoneNumber;
    
    /**
     * 부서명
     */
    private String department;
    
    /**
     * 직급
     */
    private String position;
    
    /**
     * 권한 코드 (ADMIN, MANAGER, USER)
     */
    private String roleCode;
    
    /**
     * 사용 여부 (Y/N)
     */
    private String useYn;
    
    /**
     * 등록자 ID
     */
    private String regId;
    
    /**
     * 비고
     */
    private String remarks;
}