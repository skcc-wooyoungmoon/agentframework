package com.skax.aiplatform.dto.deploy.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent App 수정 응답 DTO
 * 
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUpdateRes {
    
    /**
     * 수정된 애플리케이션 UUID
     */
    private String appUuid;
    
    /**
     * 작업 성공 여부
     */
    private Boolean success;
    
    /**
     * 작업 결과 메시지
     */
    private String message;
    
    /**
     * 수정 시간
     */
    private String updatedAt;
}
