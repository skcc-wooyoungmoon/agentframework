package com.skax.aiplatform.dto.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 각 단계별 처리 결과를 담는 DTO
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepResult {
    
    /**
     * 단계 성공 여부
     */
    private Boolean success;
    
    /**
     * 단계 결과 메시지
     */
    private String message;
    
    /**
     * 단계 처리 결과 데이터 (성공 시)
     */
    private Map<String, Object> result;
    
    /**
     * 에러 정보 (실패 시)
     */
    private Map<String, Object> error;
    
    /**
     * 단계 처리 시간 (밀리초)
     */
    private Long processingTimeMs;
}
