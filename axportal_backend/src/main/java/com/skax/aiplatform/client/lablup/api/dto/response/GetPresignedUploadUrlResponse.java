package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 사전 서명된 업로드 URL 응답 DTO
 * 
 * <p>아티팩트 업로드를 위한 사전 서명된 URL 정보를 담는 응답입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPresignedUploadUrlResponse {
    
    /**
     * 사전 서명된 업로드 URL
     */
    private String uploadUrl;
    
    /**
     * URL 만료 시간
     */
    private LocalDateTime expiresAt;
    
    /**
     * 업로드 토큰 (필요한 경우)
     */
    private String uploadToken;
    
    /**
     * 업로드 ID (멀티파트 업로드 시)
     */
    private String uploadId;
    
    /**
     * 멀티파트 업로드 URL 목록 (멀티파트인 경우)
     */
    private List<MultipartUploadUrl> multipartUrls;
    
    /**
     * 업로드 메타데이터
     */
    private Map<String, Object> metadata;
    
    /**
     * 필수 헤더 정보
     */
    private Map<String, String> requiredHeaders;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MultipartUploadUrl {
        /**
         * 파트 번호
         */
        private Integer partNumber;
        
        /**
         * 파트 업로드 URL
         */
        private String uploadUrl;
        
        /**
         * 파트 크기 (bytes)
         */
        private Long size;
    }
}