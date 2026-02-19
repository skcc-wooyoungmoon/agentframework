package com.skax.aiplatform.repository.common;

import com.skax.aiplatform.entity.GpoGroupcoJkwMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 직원 정보 마스터 테이블 Repository
 */
@Repository
public interface GpoGroupcoJkwMasRepository extends JpaRepository<GpoGroupcoJkwMas, String> {
    
    /**
     * grpco_c 값으로 모든 레코드 삭제
     */
    void deleteByGrpcoC(String grpcoC);
    
    /**
     * member_id로 직원 정보 조회
     */
    Optional<GpoGroupcoJkwMas> findByMemberId(String memberId);
}
