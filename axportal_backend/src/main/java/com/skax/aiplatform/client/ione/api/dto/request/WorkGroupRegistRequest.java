package com.skax.aiplatform.client.ione.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 업무 코드 등록 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkGroupRegistRequest {
    

    /**
     * 업무 코드 ID
     */
    private String businessCode;
    
    /**
     * 업무 코드명
     */
    private String businessName;

    // /**
    //  * 업무 코드 ID
    //  */
    // private String taskId;
    
    // /**
    //  * 업무 코드명
    //  */
    // private String taskName;
    
    // /**
    //  * 업무 설명
    //  */
    // private String taskDesc;
    
    // /**
    //  * 사용 여부 (Y/N)
    //  */
    // private String useYn;
    
    // /**
    //  * 정렬 순서
    //  */
    // private Integer sortOrder;
}