package com.skax.aiplatform.repository.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.model.GpoModelEmbeddingMas;

/**
 * GPO 모델 임베딩 원장 Repository
 * 
 * <p>GPO 모델 임베딩 마스터 테이블의 데이터 접근을 담당하는 Repository입니다.</p>
 * 
 * @author system
 * @since 2025-01-XX
 * @version 1.0
 */
@Repository
public interface GpoModelEmbeddingMasRepository extends JpaRepository<GpoModelEmbeddingMas, String> {

    /**
     * 모델명으로 모델 임베딩 정보 조회
     * 
     * @param modelNm 모델명
     * @return 모델 임베딩 정보 (Optional)
     */
    Optional<GpoModelEmbeddingMas> findByModelNm(String modelNm);
}

