package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 아티팩트 업데이트 응답 DTO
 * 
 * <p>아티팩트 업데이트 작업의 결과를 담는 응답 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateArtifactResponse {
    
    /**
     * 아티팩트 ID
     */
    private String artifactId;
    
    /**
     * 업데이트 상태
     */
    private String status;
    
    /**
     * 업데이트 시간
     */
    private LocalDateTime updatedAt;
    
    /**
     * 업데이트된 필드 목록
     */
    private String[] updatedFields;
    
    /**
     * 새로운 버전 정보
     */
    private String newVersion;
    
    /**
     * 업데이트 메타데이터
     */
    private Map<String, Object> updateMetadata;
}