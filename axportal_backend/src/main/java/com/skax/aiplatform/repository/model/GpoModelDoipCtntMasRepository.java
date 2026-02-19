package com.skax.aiplatform.repository.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.model.GpoModelDoipCtntMas;

/**
 * GPO 모델 도입 내용 원장 리포지토리
 * 
 * <p>모델 도입 내용 정보에 대한 데이터 액세스 레이어입니다.</p>
 * 
 * @author system
 * @since 2025-01-XX
 * @version 1.0.0
 */
@Repository
public interface GpoModelDoipCtntMasRepository extends JpaRepository<GpoModelDoipCtntMas, Long> {

    /**
     * 아티팩트 ID와 리비전 ID로 모델 도입 내용 정보 조회
     * 
     * @param artifactId 아티팩트 ID
     * @param revisionId 리비전 ID
     * @return 모델 도입 내용 정보 (Optional)
     */
    Optional<GpoModelDoipCtntMas> findByArtifactIdAndRevisionId(String artifactId, String revisionId);
}

