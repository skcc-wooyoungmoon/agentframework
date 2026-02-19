package com.skax.aiplatform.dto.deploy.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 앱 수정 요청 DTO
 * 
 * @author gyuheeHwang
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUpdateReq {
    
    /**
     * 이름
     */
    private String name;
    
    /**
     * 설명
     */
    private String description;
}
