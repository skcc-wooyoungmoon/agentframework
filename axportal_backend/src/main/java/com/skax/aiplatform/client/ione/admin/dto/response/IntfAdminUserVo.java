package com.skax.aiplatform.client.ione.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 어드민 사용자 정보 VO
 * 
 * <p>어드민 사용자 목록 조회 시 반환되는 기본 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfAdminUserVo {
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 사용자명
     */
    private String userName;
    
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
     * 권한명
     */
    private String roleName;
    
    /**
     * 사용 여부 (Y/N)
     */
    private String useYn;
    
    /**
     * 마지막 로그인 일시
     */
    private String lastLoginDt;
    
    /**
     * 등록일시
     */
    private String regDt;
    
    /**
     * 등록자 ID
     */
    private String regId;
    
    /**
     * 수정일시
     */
    private String modDt;
    
    /**
     * 수정자 ID
     */
    private String modId;
}