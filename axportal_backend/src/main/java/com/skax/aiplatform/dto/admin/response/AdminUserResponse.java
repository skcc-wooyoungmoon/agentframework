package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.entity.GpoUsersMas;
import lombok.Builder;
import lombok.Data;

/**
 * Admin 사용자 응답 DTO
 * 
 * @author Jongtae Park
 * @since 2025-10-08
 * @version 1.0.0
 */
@Data
@Builder
public class AdminUserResponse {
    
    private String userId;
    
    private String name;
    
    private String department;
    
    private String position;
    
    private String phoneNumber;
    
    /**
     * GpoUsersMas 엔티티를 AdminUserResponse로 변환
     * 
     * @param user GpoUsersMas 엔티티
     * @return AdminUserResponse
     */
    public static AdminUserResponse from(GpoUsersMas user) {
        return AdminUserResponse.builder()
            .userId(user.getMemberId())
            .name(user.getJkwNm())
            .department(user.getDeptNm())
            .position(user.getJkgpNm())
            .phoneNumber(user.getHpNo())
            .build();
    }
}
