package com.skax.aiplatform.client.lablup.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사전 서명된 다운로드 URL 요청 DTO
 * 
 * <p>아티팩트 다운로드를 위한 사전 서명된 URL을 요청하기 위한 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPresignedDownloadUrlRequest {
    
    /**
     * 아티팩트 ID
     */
    private String artifactId;
    
    /**
     * 다운로드할 파일 경로 (전체 아티팩트가 아닌 특정 파일)
     */
    private String filePath;
    
    /**
     * URL 만료 시간 (초)
     */
    private Integer expiresIn;
    
    /**
     * 다운로드 타입 (full, partial, stream 등)
     */
    private String downloadType;
    
    /**
     * 압축 여부
     */
    private boolean compressed;
}