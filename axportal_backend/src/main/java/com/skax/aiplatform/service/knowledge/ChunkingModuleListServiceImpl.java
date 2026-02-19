package com.skax.aiplatform.service.knowledge;

import com.skax.aiplatform.dto.knowledge.response.ChunkingModuleResponse;
import com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas;
import com.skax.aiplatform.repository.knowledge.GpoChunkAlgoMasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 청킹 모듈 목록 서비스 구현체
 * 
 * <p>
 * 청킹 모듈 목록에 대한 비즈니스 로직을 구현합니다.
 * 기존 gpo_chunk_mas 테이블을 gpo_chunk_algo_mas 테이블로 변경.
 * </p>
 * 
 * @author system
 * @since 2025-01-13
 * @version 3.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChunkingModuleListServiceImpl implements ChunkingModuleListService {

    private final GpoChunkAlgoMasRepository gpoChunkAlgoMasRepository;

    /**
     * 청킹 모듈 목록 조회
     * 삭제되지 않은 청킹 알고리즘을 알고리즘명으로 정렬하여 조회합니다.
     * 
     * @return 청킹 모듈 목록
     */
    @Override
    public List<ChunkingModuleResponse> getChunkingModuleList() {
        log.info("청킹 모듈 목록 조회 시작 (gpo_chunk_algo_mas)");
        
        List<GpoChunkAlgoMas> chunkingModules = gpoChunkAlgoMasRepository.findAllByDelYnOrderByAlgoNmAsc(0); // 0: 정상
        
        List<ChunkingModuleResponse> responses = chunkingModules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("청킹 모듈 목록 조회 완료: {} 건", responses.size());
        
        return responses;
    }

    /**
     * Entity를 Response DTO로 변환
     * 
     * @param entity GpoChunkAlgoMas 엔티티
     * @return ChunkingModuleResponse DTO
     */
    private ChunkingModuleResponse convertToResponse(GpoChunkAlgoMas entity) {
        return ChunkingModuleResponse.builder()
                .chunkId(entity.getAlgoId()) // algoId를 chunkId로 매핑 (API 호환성 유지)
                .chunkNm(entity.getAlgoNm()) // algoNm을 chunkNm으로 매핑 (API 호환성 유지)
                .descCtnt(entity.getDtlCtnt()) // dtlCtnt를 descCtnt로 매핑
                .delYn(entity.getDelYn() != null && entity.getDelYn() == 0 ? "N" : "Y") // Integer를 String으로 변환
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .fstCreatedAt(entity.getFstCreatedAt())
                .lstUpdatedAt(entity.getLstUpdatedAt())
                .build();
    }
}

