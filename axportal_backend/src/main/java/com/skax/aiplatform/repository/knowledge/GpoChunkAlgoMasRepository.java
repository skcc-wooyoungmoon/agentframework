package com.skax.aiplatform.repository.knowledge;

import com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GPO 청킹 알고리즘 마스터 리포지토리
 * 
 * <p>
 * 청킹 알고리즘 정보에 대한 데이터 액세스를 담당합니다.
 * 기존 GpoChunkMasRepository를 대체합니다.
 * </p>
 * 
 * @author system
 * @since 2025-10-17
 * @version 2.0.0
 */
@Repository
public interface GpoChunkAlgoMasRepository extends JpaRepository<GpoChunkAlgoMas, String> {

    /**
     * 알고리즘명으로 조회
     * 
     * @param algoNm 알고리즘명
     * @return 청킹 알고리즘 정보
     */
    GpoChunkAlgoMas findByAlgoNm(String algoNm);

    /**
     * 삭제되지 않은 청킹 알고리즘 정보를 알고리즘명으로 정렬하여 조회
     * 
     * @param delYn 삭제 여부 (0: 정상, 1: 삭제)
     * @return 청킹 알고리즘 목록
     */
    List<GpoChunkAlgoMas> findAllByDelYnOrderByAlgoNmAsc(Integer delYn);
}

