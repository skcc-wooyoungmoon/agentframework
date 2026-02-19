package com.skax.aiplatform.dto.knowledge.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 청킹 모듈 응답 DTO
 * 
 * <p>
 * 청킹 모듈 정보를 응답하기 위한 DTO입니다.
 * </p>
 * 
 * @author system
 * @since 2025-01-13
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkingModuleResponse {

    /**
     * 청킹 모듈 ID (UUID 문자열)
     */
    private String chunkId;

    /**
     * 청킹 모듈명
     */
    private String chunkNm;

    /**
     * 설명 내용
     */
    private String descCtnt;

    /**
     * 삭제 여부
     */
    private String delYn;

    /**
     * 생성자
     */
    private String createdBy;

    /**
     * 수정자
     */
    private String updatedBy;

    /**
     * 최초 생성일시
     */
    private LocalDateTime fstCreatedAt;

    /**
     * 최종 수정일시
     */
    private LocalDateTime lstUpdatedAt;
}

