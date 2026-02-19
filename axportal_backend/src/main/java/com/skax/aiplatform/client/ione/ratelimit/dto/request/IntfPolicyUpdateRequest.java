package com.skax.aiplatform.client.ione.ratelimit.dto.request;

import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfRatelimitPolicyVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 정책 업데이트 요청
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfPolicyUpdateRequest {
    
    /**
     * 삭제할 정책 ID의 목록
     */
    private List<String> deletedPolicies;
    
    /**
     * 추가/수정할 Ratelimit 정책 정보
     */
    private List<IntfRatelimitPolicyVo> policies;
}
