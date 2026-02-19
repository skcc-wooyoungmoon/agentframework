package com.skax.aiplatform.client.ione.ratelimit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ratelimit 업데이트 결과
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfRatelimitUpdateResult {
    
    /**
     * 에러 시, 에러 메시지 반환
     */
    private String errorMsg;
    
    /**
     * 성공 여부
     */
    private Boolean success;
}
