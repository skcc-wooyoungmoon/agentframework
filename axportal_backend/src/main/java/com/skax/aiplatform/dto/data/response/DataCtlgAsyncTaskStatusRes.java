package com.skax.aiplatform.dto.data.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비동기 작업 상태 응답 DTO
 * 
 * @author System
 * @since 2025-01-13
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCtlgAsyncTaskStatusRes {
    
    /**
     * 작업 ID
     */
    private String taskId;
    
    /**
     * 작업 상태 (PENDING, PROCESSING, COMPLETED, FAILED)
     */
    private String status;
    
    /**
     * 작업 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 작업 완료 시간
     */
    private LocalDateTime completedTime;
    
    /**
     * 전체 성공 여부
     */
    private Boolean success;
    
    /**
     * 메시지
     */
    private String message;
    
    /**
     * 에러 정보
     */
    private Map<String, Object> error;
    
    /**
     * 작업 결과
     */
    private Map<String, Object> result;
    
    /**
     * 처리 시간 (밀리초)
     */
    private Long processingTimeMs;
    
    /**
     * 데이터소스 파일 ID
     */
    private String datasourceFileId;
}
