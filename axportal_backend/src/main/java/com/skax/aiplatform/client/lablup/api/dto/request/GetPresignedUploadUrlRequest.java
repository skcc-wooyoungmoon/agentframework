package com.skax.aiplatform.client.lablup.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사전 서명된 업로드 URL 요청 DTO
 * 
 * <p>아티팩트 업로드를 위한 사전 서명된 URL을 요청하기 위한 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPresignedUploadUrlRequest {
    
    /**
     * 업로드할 파일명
     */
    private String fileName;
    
    /**
     * 파일 크기 (bytes)
     */
    private Long fileSize;
    
    /**
     * 파일 타입 (MIME type)
     */
    private String contentType;
    
    /**
     * 대상 저장소 경로
     */
    private String targetPath;
    
    /**
     * URL 만료 시간 (초)
     */
    private Integer expiresIn;
    
    /**
     * 멀티파트 업로드 여부
     */
    private boolean multipart;
    
    /**
     * 청크 크기 (멀티파트 업로드 시)
     */
    private Integer chunkSize;
}