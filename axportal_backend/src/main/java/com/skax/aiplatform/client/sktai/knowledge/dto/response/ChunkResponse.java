package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Chunk 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkResponse {
    private String chunkId;
    private String content;
    private Map<String, Object> metadata;
    private Integer pageNumber;
    private Integer chunkSize;
    private Integer chunkOverlap;
    private String splitter;
    private LocalDateTime createdDate;
    private String fileId;
    private String fileName;
    private Boolean isActive;
}
