package com.skax.aiplatform.client.ione.ratelimit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 페이지네이션된 정책 결과
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedPolicyResult {
    
    /**
     * 전체 Ratelimit 정책 갯수
     */
    private Integer listCount;
    
    /**
     * Ratelimit 정책 목록
     */
    private List<IntfRatelimitPolicyVo> list;
}
