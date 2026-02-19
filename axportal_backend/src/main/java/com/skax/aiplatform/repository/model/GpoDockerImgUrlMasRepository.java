package com.skax.aiplatform.repository.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.model.GpoDockerImgUrlMas;

/**
 * GPO 도커 이미지 URL 원장 Repository
 */
@Repository
public interface GpoDockerImgUrlMasRepository extends JpaRepository<GpoDockerImgUrlMas, Long> {

    /**
     * SYS_U_V 값으로 조회 (DEL_YN = 0인 경우만)
     * 
     * @param sysUV 시스템 유형값
     * @return 도커 이미지 URL 목록
     */
    @Query("SELECT d FROM GpoDockerImgUrlMas d WHERE d.sysUV = :sysUV AND (d.delYn = 0 OR d.delYn IS NULL)")
    List<GpoDockerImgUrlMas> findBySysUVAndDelYnIsZeroOrNull(@Param("sysUV") String sysUV);
}
