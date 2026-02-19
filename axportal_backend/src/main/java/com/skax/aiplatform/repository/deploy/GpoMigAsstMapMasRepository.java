package com.skax.aiplatform.repository.deploy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.deploy.GpoMigAsstMapMas;
import com.skax.aiplatform.entity.deploy.GpoMigAsstMapMasId;

/**
 * GPO 마이그레이션 매핑 마스터 리포지토리
 */
@Repository
public interface GpoMigAsstMapMasRepository extends JpaRepository<GpoMigAsstMapMas, GpoMigAsstMapMasId> {
    
    /**
     * 마이그레이션 UUID로 조회
     */
    List<GpoMigAsstMapMas> findByMigUuid(String migUuid);
    
    /**
     * 어시스트 UUID로 조회
     */
    List<GpoMigAsstMapMas> findByAsstUuid(String asstUuid);
    
    /**
     * 마이그레이션 시퀀스 번호로 조회
     */
    List<GpoMigAsstMapMas> findByMigSeqNo(Long migSeqNo);
}

