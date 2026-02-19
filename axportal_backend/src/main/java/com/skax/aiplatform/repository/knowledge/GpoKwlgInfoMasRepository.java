package com.skax.aiplatform.repository.knowledge;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.knowledge.GpoKwlgInfoMas;

/**
 * GPO 지식 정보 원장 Repository
 * 
 * <p>GPO 지식 정보 마스터 테이블의 데이터 접근을 담당하는 Repository입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Repository
public interface GpoKwlgInfoMasRepository extends JpaRepository<GpoKwlgInfoMas, String> {

    /**
     * 외부지식ID로 지식 정보 조회
     * 
     * @param exKwlgId 외부지식ID
     * @return 지식 정보 (Optional)
     */
    Optional<GpoKwlgInfoMas> findByExKwlgId(String exKwlgId);

    /**
     * 인덱스명으로 지식 정보 조회
     * 
     * @param idxNm 인덱스명
     * @return 지식 정보 (Optional)
     */
    Optional<GpoKwlgInfoMas> findByIdxNm(String idxNm);

    /**
     * 지식ID로 지식 정보 조회
     * 
     * @param kwlgId 지식ID
     * @return 지식 정보 (Optional)
     */
    Optional<GpoKwlgInfoMas> findByKwlgId(String kwlgId);

    /**
     * DATA PIPELINE LOAD STATUS로 지식 정보 목록 조회
     * 
     * @param dataPipelineLoadStatus DATA PIPELINE LOAD STATUS
     * @return 지식 정보 목록
     */
    List<GpoKwlgInfoMas> findByDataPipelineLoadStatus(String dataPipelineLoadStatus);

    /**
     * 진행 중인 파이프라인 조회 (data_pipeline_load_status = 'running')
     * 
     * @return 진행 중인 파이프라인 목록
     */
    @Query("SELECT g FROM GpoKwlgInfoMas g WHERE g.dataPipelineLoadStatus = 'running'")
    List<GpoKwlgInfoMas> findRunningPipelines();

    /**
     * 개발 환경 동기화 대상 조회 (dvlp_synch_yn > 0 AND data_pipeline_load_status = 'complete')
     * 
     * @return 개발 환경 동기화 대상 목록
     */
    @Query("SELECT g FROM GpoKwlgInfoMas g WHERE g.dvlpSynchYn > 0 AND g.dataPipelineLoadStatus = 'complete'")
    List<GpoKwlgInfoMas> findDevSyncTargets();

    /**
     * 운영 환경 동기화 대상 조회 (unyung_synch_yn > 0 AND data_pipeline_load_status = 'complete')
     * 
     * @return 운영 환경 동기화 대상 목록
     */
    @Query("SELECT g FROM GpoKwlgInfoMas g WHERE g.unyungSynchYn > 0 AND g.dataPipelineLoadStatus = 'complete'")
    List<GpoKwlgInfoMas> findProdSyncTargets();
}
