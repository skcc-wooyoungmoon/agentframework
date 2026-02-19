package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiDeleteResponse {
    
    
    /**
     * 작업 상태
     */
    private String infWorkStatus;
    
    /**
     * 작업 메시지
     */
    private String infWorkMsg;
    
    /**
     * 작업 순번
     */
    private String infWorkSeq;
}
