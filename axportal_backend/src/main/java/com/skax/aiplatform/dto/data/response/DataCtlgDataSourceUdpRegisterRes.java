package com.skax.aiplatform.dto.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UDP 등록 파일 처리 응답 DTO
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCtlgDataSourceUdpRegisterRes {
    
    /**
     * 처리 성공 여부
     */
    private Boolean success;
    
    /**
     * 처리 결과 메시지
     */
    private String message;
    
    /**
     * 데이터소스 파일 ID
     */
    private String datasourceFileId;
    
    /**
     * UDP 등록 ID (성공 시)
     */
    private String udpRegistrationId;
    
    /**
     * 처리 시간 (밀리초)
     */
    private Long processingTimeMs;
    
    /**
     * 에러 코드 (실패 시)
     */
    private String errorCode;
    
    /**
     * 에러 상세 정보 (실패 시)
     */
    private String errorDetails;
    
    /**
     * 1단계: 임시 디렉토리 생성 및 파일 다운로드 결과
     */
    private StepResult downloadStep;
    
    /**
     * 2단계: S3 업로드 결과
     */
    private StepResult s3UploadStep;
    
    /**
     * 3단계: ES 메타 저장 결과
     */
    private StepResult esMetaStep;
}
