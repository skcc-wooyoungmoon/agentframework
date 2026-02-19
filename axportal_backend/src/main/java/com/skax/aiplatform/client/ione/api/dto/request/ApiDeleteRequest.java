package com.skax.aiplatform.client.ione.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 삭제 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiDeleteRequest {
    
    /**
     * API ID
     */
    private String apiId;
    
    // /**
    //  * 삭제 사유
    //  */
    // private String deleteReason;
}