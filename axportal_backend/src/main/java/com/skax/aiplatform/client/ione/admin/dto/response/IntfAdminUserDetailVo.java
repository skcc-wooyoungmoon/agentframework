package com.skax.aiplatform.client.ione.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 어드민 사용자 상세 정보 VO
 * 
 * <p>특정 어드민 사용자의 상세 정보를 담는 DTO입니다.
 * 기본 정보 외에 로그인 이력, 권한 상세 정보 등을 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfAdminUserDetailVo {
    
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
     * 로그인 시도 횟수
     */
    private Integer loginAttemptCount;
    
    /**
     * 계정 잠금 여부 (Y/N)
     */
    private String lockYn;
    
    /**
     * 계정 잠금 일시
     */
    private String lockDt;
    
    /**
     * 비밀번호 변경 일시
     */
    private String passwordChangeDt;
    
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
    
    /**
     * 비고
     */
    private String remarks;
    
    /**
     * 권한 상세 목록
     */
    private List<String> permissions;
    
    /**
     * 최근 로그인 이력
     */
    private List<LoginHistory> recentLoginHistory;
    
    /**
     * 로그인 이력 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginHistory {
        /**
         * 로그인 일시
         */
        private String loginDt;
        
        /**
         * 로그인 IP
         */
        private String loginIp;
        
        /**
         * 성공 여부 (Y/N)
         */
        private String successYn;
        
        /**
         * 실패 사유
         */
        private String failReason;
    }
}