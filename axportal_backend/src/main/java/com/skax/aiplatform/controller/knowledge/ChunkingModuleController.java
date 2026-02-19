package com.skax.aiplatform.controller.knowledge;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.knowledge.response.ChunkingModuleResponse;
import com.skax.aiplatform.service.knowledge.ChunkingModuleListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 청킹 모듈 컨트롤러
 * 
 * <p>
 * 청킹 모듈 목록 조회 API를 제공합니다.
 * </p>
 * 
 * @author system
 * @since 2025-01-13
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/knowledge/chunking-modules")
@RequiredArgsConstructor
@Tag(name = "Chunking Module", description = "청킹 모듈 관리 API")
public class ChunkingModuleController {

    private final ChunkingModuleListService chunkingModuleListService;

    /**
     * 청킹 모듈 목록 조회
     * 
     * @return 청킹 모듈 목록
     */
    @Operation(summary = "청킹 모듈 목록 조회", description = "사용 가능한 청킹 모듈 목록을 조회합니다.")
    @GetMapping
    public AxResponseEntity<List<ChunkingModuleResponse>> getChunkingModuleList() {
        log.info("청킹 모듈 목록 조회 요청");
        
        List<ChunkingModuleResponse> responses = chunkingModuleListService.getChunkingModuleList();
        
        log.info("청킹 모듈 목록 조회 응답: {} 건", responses.size());
        
        return AxResponseEntity.success(responses);
    }
}

