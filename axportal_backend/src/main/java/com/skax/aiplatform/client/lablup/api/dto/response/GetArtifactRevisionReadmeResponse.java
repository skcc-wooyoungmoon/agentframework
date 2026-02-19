package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 수정본 README 응답 DTO
 * 
 * <p>아티팩트 수정본의 README 정보를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetArtifactRevisionReadmeResponse {
    
    /**
     * README 내용
     */
    private String content;
    
    /**
     * 내용 타입 (markdown, text, html 등)
     */
    private String contentType;
    
    /**
     * 인코딩
     */
    private String encoding;
    
    /**
     * 파일 크기 (bytes)
     */
    private Long size;
    
    /**
     * README 파일이 존재하는지 여부
     */
    private boolean exists;
}