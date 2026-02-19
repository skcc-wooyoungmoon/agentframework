package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 업무 코드 삭제 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkGroupDeleteResponse {
    
    /**
     * 업무 코드 ID
     */
    private String businessCode;
    
    /**
     * 업무 코드명
     */
    private String businessName;
}