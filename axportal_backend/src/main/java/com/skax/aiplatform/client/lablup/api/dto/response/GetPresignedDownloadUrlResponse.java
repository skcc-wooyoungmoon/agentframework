package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 사전 서명된 다운로드 URL 응답 DTO
 * 
 * <p>아티팩트 다운로드를 위한 사전 서명된 URL 정보를 담는 응답입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPresignedDownloadUrlResponse {
    
    /**
     * 사전 서명된 다운로드 URL
     */
    private String downloadUrl;
    
    /**
     * URL 만료 시간
     */
    private LocalDateTime expiresAt;
    
    /**
     * 다운로드 토큰 (필요한 경우)
     */
    private String downloadToken;
    
    /**
     * 파일 크기 (bytes)
     */
    private Long fileSize;
    
    /**
     * 파일 타입 (MIME type)
     */
    private String contentType;
    
    /**
     * 다운로드 메타데이터
     */
    private Map<String, Object> metadata;
    
    /**
     * 압축 여부
     */
    private boolean compressed;
}