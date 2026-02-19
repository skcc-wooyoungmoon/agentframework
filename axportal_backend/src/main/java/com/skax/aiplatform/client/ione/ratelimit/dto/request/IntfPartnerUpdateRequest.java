package com.skax.aiplatform.client.ione.ratelimit.dto.request;

import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfPartnerCustomCountVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 파트너 업데이트 요청
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfPartnerUpdateRequest {
    
    /**
     * 정책 식별자(ID)
     */
    private String policyId;
    
    /**
     * 삭제할 회원 별 예외 건수 설정된 회원(파트너)ID 목록
     */
    private List<String> deleted;
    
    /**
     * 추가/업데이트 할 회원 별 예외 건수 설정
     */
    private List<IntfPartnerCustomCountVo> customCounts;
}
