package com.skax.aiplatform.service.knowledge;

import com.skax.aiplatform.dto.knowledge.response.ChunkingModuleResponse;

import java.util.List;

/**
 * 청킹 모듈 목록 서비스 인터페이스
 * 
 * <p>
 * 청킹 모듈 목록에 대한 비즈니스 로직을 정의합니다.
 * </p>
 * 
 * @author system
 * @since 2025-01-13
 * @version 1.0.0
 */
public interface ChunkingModuleListService {

    /**
     * 청킹 모듈 목록 조회
     * 
     * @return 청킹 모듈 목록
     */
    List<ChunkingModuleResponse> getChunkingModuleList();
}

