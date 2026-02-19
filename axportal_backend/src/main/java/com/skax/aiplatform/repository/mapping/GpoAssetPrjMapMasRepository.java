package com.skax.aiplatform.repository.mapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;

@Repository
public interface GpoAssetPrjMapMasRepository extends JpaRepository<GpoAssetPrjMapMas, String> {

    Optional<GpoAssetPrjMapMas> findByAsstUrl(String asstUrl);

    void deleteByAsstUrl(String asstUrl);

    List<GpoAssetPrjMapMas> findByFstPrjSeq(Integer prjSeq);

    /**
     * asst_url이 특정 UUID로 끝나는 레코드 조회
     * 예: asst_url LIKE '%{uuid}' (JPQL CONCAT 사용)
     *
     * @param uuid 자산 UUID
     * @return 매칭되는 레코드 목록
     */
    @Query(value = """
            SELECT a FROM GpoAssetPrjMapMas a
            WHERE a.asstUrl LIKE CONCAT('%', :uuid)
            """)
    List<GpoAssetPrjMapMas> findByAsstUrlContaining(@Param("uuid") String uuid);

    /**
     * 여러 asset URL로 배치 조회
     *
     * @param asstUrls 조회할 asset URL 컬렉션
     * @return 매칭되는 레코드 목록
     */
    List<GpoAssetPrjMapMas> findByAsstUrlIn(Collection<String> asstUrls);

    /**
     * 최초 생성 프로젝트에서 공개 프로젝트로 이동하지 않은 자산 목록 조회
     */
    @Query("""
            SELECT a
            FROM GpoAssetPrjMapMas a
            WHERE a.fstPrjSeq = :prjSeq
              AND a.lstPrjSeq <> -999
            ORDER BY a.fstCreatedAt DESC
            """)
    List<GpoAssetPrjMapMas> findNonPublicAssetsByFstPrjSeq(Integer prjSeq);

}
