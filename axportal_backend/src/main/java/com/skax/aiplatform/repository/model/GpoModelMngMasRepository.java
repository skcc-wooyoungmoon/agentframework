package com.skax.aiplatform.repository.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.model.GpoModelMngMas;

/**
 * GPO 모델 매핑 원장 리포지토리
 * 
 * <p>모델 관리 정보에 대한 데이터 액세스 레이어입니다.</p>
 * 
 * @author system
 * @since 2025-01-XX
 * @version 1.0.0
 */
@Repository
public interface GpoModelMngMasRepository extends JpaRepository<GpoModelMngMas, Long> {


    /**
     * 모델 관리 ID로 모델 관리 정보 조회
     * 
     * @param modelMngId 모델 관리 ID
     * @return 모델 관리 정보 (Optional)
     */
    Optional<GpoModelMngMas> findByModelMngId(String modelMngId);

    /**
     * 모델 가든 id로 모델 관리 정보 조회
     * 
     * @param useGnynModelSeqNo 모델 가든 id
     * @return 모델 관리 정보 리스트
     */
    List<GpoModelMngMas> findByUseGnynModelSeqNo(Long useGnynModelSeqNo);
}

